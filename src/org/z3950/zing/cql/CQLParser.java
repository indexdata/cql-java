// $Id: CQLParser.java,v 1.30 2007-06-28 00:24:48 mike Exp $

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
 * @version	$Id: CQLParser.java,v 1.30 2007-06-28 00:24:48 mike Exp $
 * @see		<A href="http://zing.z3950.org/cql/index.html"
 *		        >http://zing.z3950.org/cql/index.html</A>
 */
public class CQLParser {
    private CQLLexer lexer;
    static private boolean DEBUG = false;
    static private boolean LEXDEBUG = false;

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
	CQLNode root = parseQuery("srw.serverChoice", new CQLRelation("scr"));
	if (lexer.ttype != lexer.TT_EOF)
	    throw new CQLParseException("junk after end: " + lexer.render());

	return root;
    }

    private CQLNode parseQuery(String index, CQLRelation relation)
	throws CQLParseException, IOException {
	debug("in parseQuery()");

	CQLNode term = parseTerm(index, relation);
	while (lexer.ttype != lexer.TT_EOF &&
	       lexer.ttype != ')') {
	    if (lexer.ttype == lexer.TT_AND) {
		match(lexer.TT_AND);
		CQLNode term2 = parseTerm(index, relation);
		term = new CQLAndNode(term, term2);
	    } else if (lexer.ttype == lexer.TT_OR) {
		match(lexer.TT_OR);
		CQLNode term2 = parseTerm(index, relation);
		term = new CQLOrNode(term, term2);
	    } else if (lexer.ttype == lexer.TT_NOT) {
		match(lexer.TT_NOT);
		CQLNode term2 = parseTerm(index, relation);
		term = new CQLNotNode(term, term2);
	    } else if (lexer.ttype == lexer.TT_PROX) {
		match(lexer.TT_PROX);
		CQLProxNode proxnode = new CQLProxNode(term);
		gatherProxParameters(proxnode);
		CQLNode term2 = parseTerm(index, relation);
		proxnode.addSecondSubterm(term2);
		term = (CQLNode) proxnode;
	    } else {
		throw new CQLParseException("expected boolean, got " +
					    lexer.render());
	    }
	}

	debug("no more ops");
	return term;
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
		match('>');
		return parsePrefix(index, relation);
	    }

	    debug("non-parenthesised term");
	    word = matchSymbol("index or term");
	    if (!isRelation() && lexer.ttype != lexer.TT_WORD)
		break;

	    index = word;
	    relation = new CQLRelation(lexer.ttype == lexer.TT_WORD ?
				       lexer.sval :
				       lexer.render(lexer.ttype, false));
	    match(lexer.ttype);

	    while (lexer.ttype == '/') {
		match('/');
		if (lexer.ttype != lexer.TT_WORD)
		    throw new CQLParseException("expected relation modifier, "
						+ "got " + lexer.render());
		String type = lexer.sval.toLowerCase();
		match(lexer.ttype);
		if (!isRelation()) {
		    // It's a simple modifier consisting of type only
		    relation.addModifier(type);
		} else {
		    // It's a complex modifier of the form type=value
		    String comparision = lexer.render(lexer.ttype, false);
		    match(lexer.ttype);

		    // Yuck
		    String value = lexer.ttype == lexer.TT_WORD ? lexer.sval :
			(double) lexer.nval == (int) lexer.nval ?
			new Integer((int) lexer.nval).toString() :
			new Double((double) lexer.nval).toString();

		    matchSymbol("relation-modifier value");
		    relation.addModifier(type, comparision, value);
		}
	    }

	    debug("index='" + index + ", " +
		  "relation='" + relation.toCQL() + "'");
	}

	CQLTermNode node = new CQLTermNode(index, relation, word);
	debug("made term node " + node.toCQL());
	return node;
    }

    private CQLNode parsePrefix(String index, CQLRelation relation)
	throws CQLParseException, IOException {
	debug("prefix mapping");

	String name = null;
	String identifier = matchSymbol("prefix-name");
	if (lexer.ttype == '=') {
	    match('=');
	    name = identifier;
	    identifier = matchSymbol("prefix-identifer");
	}
	CQLNode term = parseQuery(index, relation);
	return new CQLPrefixNode(name, identifier, term);
    }

    private void gatherProxParameters(CQLProxNode node)
	throws CQLParseException, IOException {
	for (int i = 0; i < 4; i++) {
	    if (lexer.ttype != '/')
		return;		// end of proximity parameters

	    match('/');
	    if (lexer.ttype != '/') {
		// not an omitted default
		switch (i) {
		    // Order should be: relation/distance/unit/ordering
		    // For now, use MA's: unit/relation/distance/ordering
		case 0: gatherProxRelation(node); break;
		case 1: gatherProxDistance(node); break;
		case 2: gatherProxUnit(node); break;
		case 3: gatherProxOrdering(node); break;
		}
	    }
	}
    }

    private void gatherProxRelation(CQLProxNode node)
	throws CQLParseException, IOException {
	if (!isRelation())
	    throw new CQLParseException("expected proximity relation, got " +
					lexer.render());
	node.addModifier("relation", null, lexer.render(lexer.ttype, false));
	match(lexer.ttype);
	debug("gPR matched " + lexer.render(lexer.ttype, false));
    }

    private void gatherProxDistance(CQLProxNode node)
	throws CQLParseException, IOException {
	if (lexer.ttype != lexer.TT_NUMBER)
	    throw new CQLParseException("expected proximity distance, got " +
					lexer.render());
	node.addModifier("distance", null, lexer.render(lexer.ttype, false));
	match(lexer.ttype);
	debug("gPD matched " + lexer.render(lexer.ttype, false));
    }

    private void gatherProxUnit(CQLProxNode node)
	throws CQLParseException, IOException {
	if (lexer.ttype != lexer.TT_pWORD &&
	    lexer.ttype != lexer.TT_SENTENCE &&
	    lexer.ttype != lexer.TT_PARAGRAPH &&
	    lexer.ttype != lexer.TT_ELEMENT)
	    throw new CQLParseException("expected proximity unit, got " +
					lexer.render());
	node.addModifier("unit", null, lexer.render());
	match(lexer.ttype);
    }

    private void gatherProxOrdering(CQLProxNode node)
	throws CQLParseException, IOException {
	if (lexer.ttype != lexer.TT_ORDERED &&
	    lexer.ttype != lexer.TT_UNORDERED)
	    throw new CQLParseException("expected proximity ordering, got " +
					lexer.render());
	node.addModifier("ordering", null, lexer.render());
	match(lexer.ttype);
    }

    // Checks for a relation that may be used inside a prox operator
    private boolean isRelation() {
	debug("isRelation: checking ttype=" + lexer.ttype +
	      " (" + lexer.render() + ")");
	return (lexer.ttype == '<' ||
		lexer.ttype == '>' ||
		lexer.ttype == '=' ||
		lexer.ttype == lexer.TT_LE ||
		lexer.ttype == lexer.TT_GE ||
		lexer.ttype == lexer.TT_NE);
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
	    lexer.ttype == lexer.TT_pWORD ||
	    lexer.ttype == lexer.TT_SENTENCE ||
	    lexer.ttype == lexer.TT_PARAGRAPH ||
	    lexer.ttype == lexer.TT_ELEMENT ||
	    lexer.ttype == lexer.TT_ORDERED ||
	    lexer.ttype == lexer.TT_UNORDERED) {
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
	    System.err.println("Usage: CQLParser [-d] [-c] [-p <pqf-properties> [<CQL-query>]");
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

	CQLParser parser = new CQLParser();
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
