// $Id: CQLParser.java,v 1.13 2002-11-02 01:24:14 mike Exp $

package org.z3950.zing.cql;
import java.io.IOException;
import java.util.Vector;


/**
 * Compiles a CQL string into a parse tree.
 * ##
 *
 * @version	$Id: CQLParser.java,v 1.13 2002-11-02 01:24:14 mike Exp $
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

    public CQLNode parse(String cql)
	throws CQLParseException, IOException {
	lexer = new CQLLexer(cql, LEXDEBUG);

	lexer.nextToken();
	debug("about to parse_query()");
	CQLNode root = parse_query("srw.serverChoice", new CQLRelation("="));
	if (lexer.ttype != lexer.TT_EOF)
	    throw new CQLParseException("junk after end: " + lexer.render());

	return root;
    }

    private CQLNode parse_query(String qualifier, CQLRelation relation)
	throws CQLParseException, IOException {
	debug("in parse_query()");

	CQLNode term = parse_term(qualifier, relation);
	while (lexer.ttype != lexer.TT_EOF &&
	       lexer.ttype != ')') {
	    if (lexer.ttype == lexer.TT_AND) {
		match(lexer.TT_AND);
		CQLNode term2 = parse_term(qualifier, relation);
		term = new CQLAndNode(term, term2);
	    } else if (lexer.ttype == lexer.TT_OR) {
		match(lexer.TT_OR);
		CQLNode term2 = parse_term(qualifier, relation);
		term = new CQLOrNode(term, term2);
	    } else if (lexer.ttype == lexer.TT_NOT) {
		match(lexer.TT_NOT);
		CQLNode term2 = parse_term(qualifier, relation);
		term = new CQLNotNode(term, term2);
	    } else if (lexer.ttype == lexer.TT_PROX) {
		match(lexer.TT_PROX);
		CQLProxNode proxnode = new CQLProxNode(term);
		gatherProxParameters(proxnode);
		CQLNode term2 = parse_term(qualifier, relation);
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

    private CQLNode parse_term(String qualifier, CQLRelation relation)
	throws CQLParseException, IOException {
	debug("in parse_term()");

	String word;
	while (true) {
	    if (lexer.ttype == '(') {
		debug("parenthesised term");
		match('(');
		CQLNode expr = parse_query(qualifier, relation);
		match(')');
		return expr;
	    } else if (lexer.ttype != lexer.TT_WORD &&
		       lexer.ttype != lexer.TT_NUMBER &&
		       lexer.ttype != '"') {
		throw new CQLParseException("expected qualifier or term, " +
					    "got " + lexer.render());
	    }

	    debug("non-parenthesised term");
	    if (lexer.ttype == lexer.TT_NUMBER) {
		word = lexer.render();
	    } else {
		word = lexer.sval;
	    }
	    match(lexer.ttype);
	    if (!isBaseRelation())
		break;

	    qualifier = word;
	    relation = new CQLRelation(lexer.render(lexer.ttype, false));
	    match(lexer.ttype);

	    while (lexer.ttype == '/') {
		match('/');
		if (lexer.ttype != lexer.TT_RELEVANT &&
		    lexer.ttype != lexer.TT_FUZZY &&
		    lexer.ttype != lexer.TT_STEM)
		    throw new CQLParseException("expected relation modifier, "
						+ "got " + lexer.render());
		relation.addModifier(lexer.sval);
		match(lexer.ttype);
	    }

	    debug("qualifier='" + qualifier + ", " +
		  "relation='" + relation.toCQL() + "'");
	}

	CQLTermNode node = new CQLTermNode(qualifier, relation, word);
	debug("made term node " + node.toCQL());
	return node;
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
		case 1: gatherProxRelation(node); break;
		case 2: gatherProxDistance(node); break;
		case 0: gatherProxUnit(node); break;
		case 3: gatherProxOrdering(node); break;
		}
	    }
	}
    }

    private void gatherProxRelation(CQLProxNode node)
	throws CQLParseException, IOException {
	if (!isProxRelation())
	    throw new CQLParseException("expected proximity relation, got " +
					lexer.render());
	node.addModifier("relation", lexer.render(lexer.ttype, false));
	match(lexer.ttype);
	debug("gPR matched " + lexer.render(lexer.ttype, false));
    }

    private void gatherProxDistance(CQLProxNode node)
	throws CQLParseException, IOException {
	if (lexer.ttype != lexer.TT_NUMBER)
	    throw new CQLParseException("expected proximity distance, got " +
					lexer.render());
	node.addModifier("distance", lexer.render(lexer.ttype, false));
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
	node.addModifier("unit", lexer.render());
	match(lexer.ttype);
    }

    private void gatherProxOrdering(CQLProxNode node)
	throws CQLParseException, IOException {
	if (lexer.ttype != lexer.TT_ORDERED &&
	    lexer.ttype != lexer.TT_UNORDERED)
	    throw new CQLParseException("expected proximity ordering, got " +
					lexer.render());
	node.addModifier("ordering", lexer.render());
	match(lexer.ttype);
    }

    boolean isBaseRelation() {
	debug("isBaseRelation: checking ttype=" + lexer.ttype +
	      " (" + lexer.render() + ")");
	return (isProxRelation() ||
		lexer.ttype == lexer.TT_ANY ||
		lexer.ttype == lexer.TT_ALL ||
		lexer.ttype == lexer.TT_EXACT);
    }

    boolean isProxRelation() {
	debug("isProxRelation: checking ttype=" + lexer.ttype +
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


    // Test harness.
    //
    // e.g. echo '(au=Kerninghan or au=Ritchie) and ti=Unix' |
    //				java org.z3950.zing.cql.CQLParser
    // yields:
    //	<triple>
    //	  <boolean>and</boolean>
    //	  <triple>
    //	    <boolean>or</boolean>
    //	    <searchClause>
    //	      <index>au<index>
    //	      <relation>=<relation>
    //	      <term>Kerninghan<term>
    //	    </searchClause>
    //	    <searchClause>
    //	      <index>au<index>
    //	      <relation>=<relation>
    //	      <term>Ritchie<term>
    //	    </searchClause>
    //	  </triple>
    //	  <searchClause>
    //	    <index>ti<index>
    //	    <relation>=<relation>
    //	    <term>Unix<term>
    //	  </searchClause>
    //	</triple>
    //
    public static void main (String[] args) {
	boolean canonicalise = false;
	Vector argv = new Vector();
	for (int i = 0; i < args.length; i++) {
	    argv.add(args[i]);
	}

	if (argv.size() > 0 && argv.get(0).equals("-c")) {
	    canonicalise = true;
	    argv.remove(0);
	}

	if (argv.size() > 1) {
	    System.err.println("Usage: CQLParser [-c] [<CQL-query>]");
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
	    } catch (java.io.IOException ex) {
		System.err.println("Can't read query: " + ex.getMessage());
		System.exit(2);
	    }
	    cql = new String(bytes);
	}

	CQLParser parser = new CQLParser();
	CQLNode root;
	try {
	    root = parser.parse(cql);
	    debug("root='" + root + "'");
	    if (canonicalise) {
		System.out.println(root.toCQL());
	    } else {
		System.out.print(root.toXCQL(0));
	    }
	} catch (CQLParseException ex) {
	    System.err.println("Syntax error: " + ex.getMessage());
	    System.exit(3);
	} catch (java.io.IOException ex) {
	    System.err.println("Can't compile query: " + ex.getMessage());
	    System.exit(4);
	}
    }
}
