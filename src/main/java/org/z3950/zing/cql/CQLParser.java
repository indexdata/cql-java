// $Id: CQLParser.java,v 1.39 2007-08-06 15:54:48 mike Exp $

package org.z3950.zing.cql;
import java.io.IOException;
import java.util.Vector;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * Compiles CQL strings into parse trees of CQLNode subtypes.
 *
 * @version	$Id: CQLParser.java,v 1.39 2007-08-06 15:54:48 mike Exp $
 * @see		<A href="http://zing.z3950.org/cql/index.html"
 *		        >http://zing.z3950.org/cql/index.html</A>
 */
public class CQLParser {
    private CQLLexer lexer;
    private int compat;	// When false, implement CQL 1.2
    public static int V1POINT1 = 12368;
    public static int V1POINT2 = 12369;
    public static int V1POINT1SORT = 12370;

    static private boolean DEBUG = false;
    static private boolean LEXDEBUG = false;

    /**
     * The new parser implements a dialect of CQL specified by the
     * <tt>compat</tt> argument:
     * <ul>
     *  <li>V1POINT1 - CQL version 1.1
     *  </li>
     *  <li>V1POINT2 - CQL version 1.2
     *  </li>
     *  <li>V1POINT1SORT - CQL version 1.1 but including
     *		<tt>sortby</tt> as specified for CQL 1.2.
     *  </li>
     * </ul>
     */
    public CQLParser(int compat) {
	this.compat = compat;
    }

    /**
     * The new parser implements CQL 1.2
     */
    public CQLParser() {
	this.compat = V1POINT2;
    }

    private static void debug(String str) {
	if (DEBUG)
	    System.err.println("PARSEDEBUG: " + str);
    }

    /**
     * Compiles a CQL query.
     * <P>
     * The resulting parse tree may be further processed by hand (see
     * the individual node-types' documentation for details on the
     * data structure) or, more often, simply rendered out in the
     * desired form using one of the back-ends.  <TT>toCQL()</TT>
     * returns a decompiled CQL query equivalent to the one that was
     * compiled in the first place; <TT>toXCQL()</TT> returns an
     * XML snippet representing the query; and <TT>toPQF()</TT>
     * returns the query rendered in Index Data's Prefix Query
     * Format.
     *
     * @param cql	The query
     * @return		A CQLNode object which is the root of a parse
     * tree representing the query.  */
    public CQLNode parse(String cql)
	throws CQLParseException, IOException {
	lexer = new CQLLexer(cql, LEXDEBUG);

	lexer.nextToken();
	debug("about to parseQuery()");
	CQLNode root = parseTopLevelPrefixes("cql.serverChoice",
		new CQLRelation(compat == V1POINT2 ? "=" : "scr"));
	if (lexer.ttype != lexer.TT_EOF)
	    throw new CQLParseException("junk after end: " + lexer.render());

	return root;
    }

    private CQLNode parseTopLevelPrefixes(String index, CQLRelation relation)
	throws CQLParseException, IOException {
	debug("top-level prefix mapping");

	if (lexer.ttype == '>') {
	    return parsePrefix(index, relation, true);
	}

	CQLNode node = parseQuery(index, relation);
	if ((compat == V1POINT2 || compat == V1POINT1SORT) &&
	    lexer.ttype == lexer.TT_SORTBY) {
	    match(lexer.ttype);
	    debug("sortspec");

	    CQLSortNode sortnode = new CQLSortNode(node);
	    while (lexer.ttype != lexer.TT_EOF) {
		String sortindex = matchSymbol("sort index");
		ModifierSet ms = gatherModifiers(sortindex);
		sortnode.addSortIndex(ms);
	    }

	    if (sortnode.keys.size() == 0) {
		throw new CQLParseException("no sort keys");
	    }

	    node = sortnode;
	}

	return node;
    }

    private CQLNode parseQuery(String index, CQLRelation relation)
	throws CQLParseException, IOException {
	debug("in parseQuery()");

	CQLNode term = parseTerm(index, relation);
	while (lexer.ttype != lexer.TT_EOF &&
	       lexer.ttype != ')' &&
	       lexer.ttype != lexer.TT_SORTBY) {
	    if (lexer.ttype == lexer.TT_AND ||
		lexer.ttype == lexer.TT_OR ||
		lexer.ttype == lexer.TT_NOT ||
		lexer.ttype == lexer.TT_PROX) {
		int type = lexer.ttype;
		String val = lexer.sval;
		match(type);
		ModifierSet ms = gatherModifiers(val);
		CQLNode term2 = parseTerm(index, relation);
		term = ((type == lexer.TT_AND) ? new CQLAndNode(term, term2, ms) :
			(type == lexer.TT_OR)  ? new CQLOrNode (term, term2, ms) :
			(type == lexer.TT_NOT) ? new CQLNotNode(term, term2, ms) :
			                         new CQLProxNode(term, term2, ms));
	    } else {
		throw new CQLParseException("expected boolean, got " +
					    lexer.render());
	    }
	}

	debug("no more ops");
	return term;
    }

    private ModifierSet gatherModifiers(String base)
	throws CQLParseException, IOException {
	debug("in gatherModifiers()");

	ModifierSet ms = new ModifierSet(base);
	while (lexer.ttype == '/') {
	    match('/');
	    if (lexer.ttype != lexer.TT_WORD)
		throw new CQLParseException("expected modifier, "
					    + "got " + lexer.render());
	    String type = lexer.sval.toLowerCase();
	    match(lexer.ttype);
	    if (!isSymbolicRelation()) {
		// It's a simple modifier consisting of type only
		ms.addModifier(type);
	    } else {
		// It's a complex modifier of the form type=value
		String comparision = lexer.render(lexer.ttype, false);
		match(lexer.ttype);
		String value = matchSymbol("modifier value");
		ms.addModifier(type, comparision, value);
	    }
	}

	return ms;
    }

    private CQLNode parseTerm(String index, CQLRelation relation)
	throws CQLParseException, IOException {
	debug("in parseTerm()");

	String word;
	while (true) {
	    if (lexer.ttype == '(') {
		debug("parenthesised term");
		match('(');
		CQLNode expr = parseQuery(index, relation);
		match(')');
		return expr;
	    } else if (lexer.ttype == '>') {
		return parsePrefix(index, relation, false);
	    }

	    debug("non-parenthesised term");
	    word = matchSymbol("index or term");
            while (lexer.ttype == lexer.TT_WORD && !isRelation()) {
              word = word + " " + lexer.sval;
              match(lexer.TT_WORD);
            }

	    if (!isRelation())
		break;

	    index = word;
	    String relstr = (lexer.ttype == lexer.TT_WORD ?
			     lexer.sval : lexer.render(lexer.ttype, false));
	    relation = new CQLRelation(relstr);
	    match(lexer.ttype);
	    ModifierSet ms = gatherModifiers(relstr);
	    relation.setModifiers(ms);
	    debug("index='" + index + ", " +
		  "relation='" + relation.toCQL() + "'");
	}

	CQLTermNode node = new CQLTermNode(index, relation, word);
	debug("made term node " + node.toCQL());
	return node;
    }

    private CQLNode parsePrefix(String index, CQLRelation relation,
				boolean topLevel)
	throws CQLParseException, IOException {
	debug("prefix mapping");

	match('>');
	String name = null;
	String identifier = matchSymbol("prefix-name");
	if (lexer.ttype == '=') {
	    match('=');
	    name = identifier;
	    identifier = matchSymbol("prefix-identifer");
	}
	CQLNode node = topLevel ?
	    parseTopLevelPrefixes(index, relation) :
	    parseQuery(index, relation);

	return new CQLPrefixNode(name, identifier, node);
    }

    private boolean isRelation() {
	debug("isRelation: checking ttype=" + lexer.ttype +
	      " (" + lexer.render() + ")");
        if (lexer.ttype == lexer.TT_WORD &&
            (lexer.sval.indexOf('.') >= 0 ||
             lexer.sval.equals("any") ||
             lexer.sval.equals("all") ||
             lexer.sval.equals("within") ||
             lexer.sval.equals("encloses") ||
             lexer.sval.equals("exact") ||
             (lexer.sval.equals("scr") && compat != V1POINT2) ||
             (lexer.sval.equals("adj") && compat == V1POINT2)))
          return true;

        return isSymbolicRelation();
    }

    private boolean isSymbolicRelation() {
	debug("isSymbolicRelation: checking ttype=" + lexer.ttype +
	      " (" + lexer.render() + ")");
	return (lexer.ttype == '<' ||
		lexer.ttype == '>' ||
		lexer.ttype == '=' ||
		lexer.ttype == lexer.TT_LE ||
		lexer.ttype == lexer.TT_GE ||
		lexer.ttype == lexer.TT_NE ||
		lexer.ttype == lexer.TT_EQEQ);
    }

    private void match(int token)
	throws CQLParseException, IOException {
	debug("in match(" + lexer.render(token, true) + ")");
	if (lexer.ttype != token)
	    throw new CQLParseException("expected " +
					lexer.render(token, true) +
					", " + "got " + lexer.render());
	int tmp = lexer.nextToken();
	debug("match() got token=" + lexer.ttype + ", " +
	      "nval=" + lexer.nval + ", sval='" + lexer.sval + "'" +
	      " (tmp=" + tmp + ")");
    }

    private String matchSymbol(String expected)
	throws CQLParseException, IOException {

	debug("in matchSymbol()");
	if (lexer.ttype == lexer.TT_WORD ||
	    lexer.ttype == lexer.TT_NUMBER ||
	    lexer.ttype == '"' ||
	    // The following is a complete list of keywords.  Because
	    // they're listed here, they can be used unquoted as
	    // indexes, terms, prefix names and prefix identifiers.
	    // ### Instead, we should ask the lexer whether what we
	    // have is a keyword, and let the knowledge reside there.
	    lexer.ttype == lexer.TT_AND ||
	    lexer.ttype == lexer.TT_OR ||
	    lexer.ttype == lexer.TT_NOT ||
	    lexer.ttype == lexer.TT_PROX ||
	    lexer.ttype == lexer.TT_SORTBY) {
	    String symbol = (lexer.ttype == lexer.TT_NUMBER) ?
		lexer.render() : lexer.sval;
	    match(lexer.ttype);
	    return symbol;
	}

	throw new CQLParseException("expected " + expected + ", " +
				    "got " + lexer.render());
    }


    /**
     * Simple test-harness for the CQLParser class.
     * <P>
     * Reads a CQL query either from its command-line argument, if
     * there is one, or standard input otherwise.  So these two
     * invocations are equivalent:
     * <PRE>
     *  CQLParser 'au=(Kerninghan or Ritchie) and ti=Unix'
     *  echo au=(Kerninghan or Ritchie) and ti=Unix | CQLParser
     * </PRE>
     * The test-harness parses the supplied query and renders is as
     * XCQL, so that both of the invocations above produce the
     * following output:
     * <PRE>
     *	&lt;triple&gt;
     *	  &lt;boolean&gt;
     *	    &lt;value&gt;and&lt;/value&gt;
     *	  &lt;/boolean&gt;
     *	  &lt;triple&gt;
     *	    &lt;boolean&gt;
     *	      &lt;value&gt;or&lt;/value&gt;
     *	    &lt;/boolean&gt;
     *	    &lt;searchClause&gt;
     *	      &lt;index&gt;au&lt;/index&gt;
     *	      &lt;relation&gt;
     *	        &lt;value&gt;=&lt;/value&gt;
     *	      &lt;/relation&gt;
     *	      &lt;term&gt;Kerninghan&lt;/term&gt;
     *	    &lt;/searchClause&gt;
     *	    &lt;searchClause&gt;
     *	      &lt;index&gt;au&lt;/index&gt;
     *	      &lt;relation&gt;
     *	        &lt;value&gt;=&lt;/value&gt;
     *	      &lt;/relation&gt;
     *	      &lt;term&gt;Ritchie&lt;/term&gt;
     *	    &lt;/searchClause&gt;
     *	  &lt;/triple&gt;
     *	  &lt;searchClause&gt;
     *	    &lt;index&gt;ti&lt;/index&gt;
     *	    &lt;relation&gt;
     *	      &lt;value&gt;=&lt;/value&gt;
     *	    &lt;/relation&gt;
     *	    &lt;term&gt;Unix&lt;/term&gt;
     *	  &lt;/searchClause&gt;
     *	&lt;/triple&gt;
     * </PRE>
     * <P>
     * @param -1
     *	CQL version 1.1 (default version 1.2)
     * @param -d
     *	Debug mode: extra output written to stderr.
     * @param -c
     *	Causes the output to be written in CQL rather than XCQL - that
     *	is, a query equivalent to that which was input, is output.  In
     *	effect, the test harness acts as a query canonicaliser.
     * @return
     *	The input query, either as XCQL [default] or CQL [if the
     *	<TT>-c</TT> option is supplied].
     */
    public static void main (String[] args) {
	char mode = 'x';	// x=XCQL, c=CQL, p=PQF
	String pfile = null;

	Vector<String> argv = new Vector<String>();
	for (int i = 0; i < args.length; i++) {
	    argv.add(args[i]);
	}

	int compat = V1POINT2;
	if (argv.size() > 0 && argv.get(0).equals("-1")) {
	    compat = V1POINT1;
	    argv.remove(0);
	}

	if (argv.size() > 0 && argv.get(0).equals("-d")) {
	    DEBUG = true;
	    argv.remove(0);
	}

	if (argv.size() > 0 && argv.get(0).equals("-c")) {
	    mode = 'c';
	    argv.remove(0);
	} else if (argv.size() > 1 && argv.get(0).equals("-p")) {
	    mode = 'p';
	    argv.remove(0);
	    pfile = (String) argv.get(0);
	    argv.remove(0);
	}

	if (argv.size() > 1) {
	    System.err.println("Usage: CQLParser [-1] [-d] [-c] " +
			       "[-p <pqf-properties> [<CQL-query>]");
	    System.err.println("If unspecified, query is read from stdin");
	    System.exit(1);
	}

	String cql;
	if (argv.size() == 1) {
	    cql = (String) argv.get(0);
	} else {
	    byte[] bytes = new byte[10000];
	    try {
		// Read in the whole of standard input in one go
		int nbytes = System.in.read(bytes);
	    } catch (IOException ex) {
		System.err.println("Can't read query: " + ex.getMessage());
		System.exit(2);
	    }
	    cql = new String(bytes);
	}

	CQLParser parser = new CQLParser(compat);
	CQLNode root = null;
	try {
	    root = parser.parse(cql);
	} catch (CQLParseException ex) {
	    System.err.println("Syntax error: " + ex.getMessage());
	    System.exit(3);
	} catch (IOException ex) {
	    System.err.println("Can't compile query: " + ex.getMessage());
	    System.exit(4);
	}

	try {
	    if (mode == 'c') {
		System.out.println(root.toCQL());
	    } else if (mode == 'p') {
		InputStream f = new FileInputStream(pfile);
		if (f == null)
		    throw new FileNotFoundException(pfile);

		Properties config = new Properties();
		config.load(f);
		f.close();
		System.out.println(root.toPQF(config));
	    } else {
		System.out.print(root.toXCQL(0));
	    }
	} catch (IOException ex) {
	    System.err.println("Can't render query: " + ex.getMessage());
	    System.exit(5);
	} catch (UnknownIndexException ex) {
	    System.err.println("Unknown index: " + ex.getMessage());
	    System.exit(6);
	} catch (UnknownRelationException ex) {
	    System.err.println("Unknown relation: " + ex.getMessage());
	    System.exit(7);
	} catch (UnknownRelationModifierException ex) {
	    System.err.println("Unknown relation modifier: " +
			       ex.getMessage());
	    System.exit(8);
	} catch (UnknownPositionException ex) {
	    System.err.println("Unknown position: " + ex.getMessage());
	    System.exit(9);
	} catch (PQFTranslationException ex) {
	    // We catch all of this class's subclasses, so --
	    throw new Error("can't get a PQFTranslationException");
	}
    }
}
