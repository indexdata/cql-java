package org.z3950.zing.cql;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Compiles CQL strings into parse trees of CQLNode subtypes.
 *
 * @see		<A href="http://zing.z3950.org/cql/index.html"
 *		        >http://zing.z3950.org/cql/index.html</A>
 */
public class CQLParser {
    private CQLTokenizer lexer;
    private final int compat;	// When false, implement CQL 1.2
    private final Set<String> customRelations = new HashSet<String>();
    
    public static final int V1POINT1 = 12368;
    public static final int V1POINT2 = 12369;
    public static final int V1POINT1SORT = 12370;
    public final boolean allowKeywordTerms;

    static private boolean DEBUG = false;
    static private boolean LEXDEBUG = false;

    /**
     * The new parser implements a dialect of CQL specified by the
     * <code>compat</code> argument:
     * <UL>
     *  <LI>V1POINT1 - CQL version 1.1
     *  </LI>
     *  <LI>V1POINT2 - CQL version 1.2
     *  </LI>
     *  <LI>V1POINT1SORT - CQL version 1.1 but including
     *		<tt>sortby</tt> as specified for CQL 1.2.
     *  </LI>
     * </UL>
     */
    public CQLParser(int compat) {
	this.compat = compat;
        this.allowKeywordTerms = true;
    }
    
    /**
     * Official CQL grammar allows registered keywords like 'and/or/not/sortby/prox' 
     * to be used unquoted in terms. This constructor allows to create an instance 
     * of a parser that prohibits this behavior while sacrificing compatibility.
     * @param compat CQL version compatibility
     * @param allowKeywordTerms when false registered keywords are disallowed in unquoted terms
     */
    public CQLParser(int compat, boolean allowKeywordTerms) {
	this.compat = compat;
        this.allowKeywordTerms = allowKeywordTerms;
    }
    
    /**
     * The new parser implements CQL 1.2
     */
    public CQLParser() {
	this.compat = V1POINT2;
        this.allowKeywordTerms = true;
    }

    private static void debug(String str) {
	if (DEBUG)
	    System.err.println("PARSEDEBUG: " + str);
    }
    
    /**
     * Registers custom relation in this parser. Note that when a custom relation
     * is registered the parser is no longer strictly compliant with the chosen spec.
     * @param relation
     * @return true if custom relation has not been registered already
     */
    public boolean registerCustomRelation(String relation) {
      return customRelations.add(relation);
    }
    
    /**
     * Unregisters previously registered custom relation in this instance of the parser.
     * @param relation
     * @return true is relation has been previously registered
     */
    public boolean unregisterCustomRelation(String relation) {
      return customRelations.remove(relation);
    }

    /**
     * Compiles a CQL query.
     * <P>
     * The resulting parse tree may be further processed by hand (see
     * the individual node-types' documentation for details on the
     * data structure) or, more often, simply rendered out in the
     * desired form using one of the back-ends.  <code>toCQL()</code>
     * returns a decompiled CQL query equivalent to the one that was
     * compiled in the first place; <code>toXCQL()</code> returns an
     * XML snippet representing the query; and <code>toPQF()</code>
     * returns the query rendered in Index Data's Prefix Query
     * Format.
     *
     * @param cql	The query
     * @return		A CQLNode object which is the root of a parse
     * tree representing the query.  */
    public CQLNode parse(String cql)
	throws CQLParseException, IOException {
	lexer = new CQLLexer(cql, LEXDEBUG);

	lexer.move();
	debug("about to parseQuery()");
	CQLNode root = parseTopLevelPrefixes("cql.serverChoice",
		new CQLRelation(compat == V1POINT2 ? "=" : "scr"));
	if (lexer.what() != CQLTokenizer.TT_EOF)
	    throw new CQLParseException("junk after end: " + lexer.render(), 
              lexer.pos());

	return root;
    }

    private CQLNode parseTopLevelPrefixes(String index, CQLRelation relation)
	throws CQLParseException, IOException {
	debug("top-level prefix mapping");

	if (lexer.what() == '>') {
	    return parsePrefix(index, relation, true);
	}

	CQLNode node = parseQuery(index, relation);
	if ((compat == V1POINT2 || compat == V1POINT1SORT) &&
	    lexer.what() == CQLTokenizer.TT_SORTBY) {
	    match(lexer.what());
	    debug("sortspec");

	    CQLSortNode sortnode = new CQLSortNode(node);
	    while (lexer.what() != CQLTokenizer.TT_EOF) {
		String sortindex = matchSymbol("sort index");
		ModifierSet ms = gatherModifiers(sortindex);
		sortnode.addSortIndex(ms);
	    }

	    if (sortnode.keys.size() == 0) {
		throw new CQLParseException("no sort keys", lexer.pos());
	    }

	    node = sortnode;
	}

	return node;
    }

    private CQLNode parseQuery(String index, CQLRelation relation)
	throws CQLParseException, IOException {
	debug("in parseQuery()");

	CQLNode term = parseTerm(index, relation);
	while (lexer.what() != CQLTokenizer.TT_EOF &&
	       lexer.what() != ')' &&
	       lexer.what() != CQLTokenizer.TT_SORTBY) {
	    if (lexer.what() == CQLTokenizer.TT_AND ||
		lexer.what() == CQLTokenizer.TT_OR ||
		lexer.what() == CQLTokenizer.TT_NOT ||
		lexer.what() == CQLTokenizer.TT_PROX) {
		int type = lexer.what();
		String val = lexer.value();
		match(type);
		ModifierSet ms = gatherModifiers(val);
		CQLNode term2 = parseTerm(index, relation);
		term = ((type == CQLTokenizer.TT_AND) ? new CQLAndNode(term, term2, ms) :
			(type == CQLTokenizer.TT_OR)  ? new CQLOrNode (term, term2, ms) :
			(type == CQLTokenizer.TT_NOT) ? new CQLNotNode(term, term2, ms) :
			                         new CQLProxNode(term, term2, ms));
	    } else {
		throw new CQLParseException("expected boolean, got " +
					    lexer.render(), lexer.pos());
	    }
	}

	debug("no more ops");
	return term;
    }

    private ModifierSet gatherModifiers(String base)
	throws CQLParseException, IOException {
	debug("in gatherModifiers()");

	ModifierSet ms = new ModifierSet(base);
	while (lexer.what() == '/') {
	    match('/');
	    if (lexer.what() != CQLTokenizer.TT_WORD)
		throw new CQLParseException("expected modifier, "
					    + "got " + lexer.render(), 
                  lexer.pos());
	    String type = lexer.value().toLowerCase();
	    match(lexer.what());
	    if (!isSymbolicRelation()) {
		// It's a simple modifier consisting of type only
		ms.addModifier(type);
	    } else {
		// It's a complex modifier of the form type=value
		String comparision = lexer.render(lexer.what(), false);
		match(lexer.what());
		String value = matchSymbol("modifier value");
		ms.addModifier(type, comparision, value);
	    }
	}

	return ms;
    }

    private CQLNode parseTerm(String index, CQLRelation relation)
	throws CQLParseException, IOException {
	debug("in parseTerm()");

	String first;
        StringBuilder all;
	while (true) {
	    if (lexer.what() == '(') {
		debug("parenthesised term");
		match('(');
		CQLNode expr = parseQuery(index, relation);
		match(')');
		return expr;
	    } else if (lexer.what() == '>') {
		return parsePrefix(index, relation, false);
	    }

	    debug("non-parenthesised term");
	    first = matchSymbol("index or term");
            all = new StringBuilder(first);
            //match relation only on second postion
            while (isWordOrString() && (all.length() > first.length() || !isRelation())) {
              all.append(" ").append(lexer.value());
              match(lexer.what());
            }

	    if (!isRelation())
              break; //we're done if no relation
	    
            //render relation
	    String relstr = (lexer.what() == CQLTokenizer.TT_WORD ?
			     lexer.value() : lexer.render(lexer.what(), false));
            //we have relation, but it only makes sense if preceded by a single term
            if (all.length() > first.length()) {
              throw new CQLParseException("unexpected relation '"+relstr+"'"
                , lexer.pos());
            }
            index = first;
	    relation = new CQLRelation(relstr);
	    match(lexer.what());
	    ModifierSet ms = gatherModifiers(relstr);
	    relation.ms = ms;
	    debug("index='" + index + ", " +
		  "relation='" + relation.toCQL() + "'");
	}
	CQLTermNode node = new CQLTermNode(index, relation, all.toString());
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
	if (lexer.what() == '=') {
	    match('=');
	    name = identifier;
	    identifier = matchSymbol("prefix-identifer");
	}
	CQLNode node = topLevel ?
	    parseTopLevelPrefixes(index, relation) :
	    parseQuery(index, relation);

	return new CQLPrefixNode(name, identifier, node);
    }

    private boolean isWordOrString() {
      return CQLTokenizer.TT_WORD == lexer.what()
        || CQLTokenizer.TT_STRING == lexer.what();
    }

    private boolean isRelation() {
        debug("isRelation: checking what()=" + lexer.what() +
                " (" + lexer.render() + ")");
        if (lexer.what() == CQLTokenizer.TT_WORD) {
            return lexer.value().indexOf('.') >= 0 ||
                    lexer.value().equalsIgnoreCase("any") ||
                    lexer.value().equalsIgnoreCase("all") ||
                    lexer.value().equalsIgnoreCase("within") ||
                    lexer.value().equalsIgnoreCase("encloses") ||
                    (lexer.value().equalsIgnoreCase("exact") && compat != V1POINT2) ||
                    (lexer.value().equalsIgnoreCase("scr") && compat != V1POINT2) ||
                    (lexer.value().equalsIgnoreCase("adj") && compat == V1POINT2) ||
                    customRelations.stream().anyMatch(r -> r.equalsIgnoreCase(lexer.value()));
        }
        return isSymbolicRelation();
    }

    private boolean isSymbolicRelation() {
        debug("isSymbolicRelation: checking what()=" + lexer.what() +
                " (" + lexer.render() + ")");
        return (lexer.what() == '<' ||
                lexer.what() == '>' ||
                lexer.what() == '=' ||
                lexer.what() == CQLTokenizer.TT_LE ||
                lexer.what() == CQLTokenizer.TT_GE ||
                lexer.what() == CQLTokenizer.TT_NE ||
                lexer.what() == CQLTokenizer.TT_EQEQ);
    }

    private void match(int token)
	throws CQLParseException, IOException {
	debug("in match(" + lexer.render(token, true) + ")");
	if (lexer.what() != token)
	    throw new CQLParseException("expected " +
					lexer.render(token, true) +
					", " + "got " + lexer.render(), 
              lexer.pos());
	lexer.move();
	debug("match() got token=" + lexer.what() + ", value()='" + lexer.value() + "'");
    }

    private String matchSymbol(String expected)
	throws CQLParseException, IOException {

	debug("in matchSymbol()");
	if (lexer.what() == CQLTokenizer.TT_WORD ||
	    lexer.what() == CQLTokenizer.TT_STRING ||
	    // The following is a complete list of keywords.  Because
	    // they're listed here, they can be used unquoted as
	    // indexes, terms, prefix names and prefix identifiers.
            (allowKeywordTerms &&
	    lexer.what() == CQLTokenizer.TT_AND ||
	    lexer.what() == CQLTokenizer.TT_OR ||
	    lexer.what() == CQLTokenizer.TT_NOT ||
	    lexer.what() == CQLTokenizer.TT_PROX ||
	    lexer.what() == CQLTokenizer.TT_SORTBY)) {
	    String symbol = lexer.value();
	    match(lexer.what());
	    return symbol;
	}

	throw new CQLParseException("expected " + expected + ", " +
				    "got " + lexer.render(), lexer.pos());
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
     *	<code>-c</code> option is supplied].
     */
    public static void main (String[] args) {
	char mode = 'x';	// x=XCQL, c=CQL, p=PQF
	String pfile = null;

	List<String> argv = new ArrayList<String>();
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
	    BufferedReader buff = new BufferedReader(new InputStreamReader(System.in));
	    try {
		// read a single line of input
              cql = buff.readLine();
              if (cql == null) {
                System.err.println("Can't read query from stdin");
		System.exit(2);
                return;
              }
	    } catch (IOException ex) {
		System.err.println("Can't read query: " + ex.getMessage());
		System.exit(2);
                return;
	    }
	}

	CQLParser parser = new CQLParser(compat);
	CQLNode root;
	try {
	    root = parser.parse(cql);
	} catch (CQLParseException ex) {
	    System.err.println("Syntax error: " + ex.getMessage());
            StringBuilder space = new StringBuilder(cql.length());
            System.out.println(cql);
            for (int i=0; i<ex.getPosition(); i++) space.append(" ");
            space.append("^");
            System.err.println(space.toString());
	    System.exit(3);
            return; //compiler
	} catch (IOException ex) {
	    System.err.println("Can't compile query: " + ex.getMessage());
	    System.exit(4);
            return; //compiler
	}

	try {
	    if (mode == 'c') {
		System.out.println(root.toCQL());
	    } else if (mode == 'p') {
              try {
		InputStream f = new FileInputStream(pfile);
		Properties config = new Properties();
		config.load(f);
		f.close();
		System.out.println(root.toPQF(config));
              } catch (IOException ex) {
                System.err.println("Can't load PQF properties:" + 
                  ex.getMessage());
                System.exit(5);
              }
	    } else {
		System.out.print(root.toXCQL());
	    }
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
	    System.err.println("Cannot translate to PQF: " + ex.getMessage());
	    System.exit(10);
	}
    }
}
