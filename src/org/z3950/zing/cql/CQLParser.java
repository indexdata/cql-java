// $Id: CQLParser.java,v 1.8 2002-10-27 00:46:25 mike Exp $

package org.z3950.zing.cql;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StreamTokenizer;


/**
 * Compiles a CQL string into a parse tree ...
 * ###
 *
 * @version	$Id: CQLParser.java,v 1.8 2002-10-27 00:46:25 mike Exp $
 * @see		<A href="http://zing.z3950.org/cql/index.html"
 *		        >http://zing.z3950.org/cql/index.html</A>
 */
public class CQLParser {
    private CQLLexer lexer;
    static private boolean PARSEDEBUG = false;
    static private boolean LEXDEBUG = false;

    private class CQLParseException extends Exception {
	CQLParseException(String s) { super(s); }
    }

    static void debug(String str) {
	if (PARSEDEBUG)
	    System.err.println("PARSEDEBUG: " + str);
    }

    public CQLNode parse(String cql)
	throws CQLParseException, IOException {
	lexer = new CQLLexer(cql, LEXDEBUG);

	lexer.nextToken();
	debug("about to parse_query()");
	CQLNode root = parse_query("srw.serverChoice", "=");
	if (lexer.ttype != lexer.TT_EOF)
	    throw new CQLParseException("junk after end: " + lexer.render());

	return root;
    }

    private CQLNode parse_query(String qualifier, String relation)
	throws CQLParseException, IOException {
	debug("in parse_query()");

	CQLNode term = parse_term(qualifier, relation);
	while (lexer.ttype == lexer.TT_WORD) {
	    String op = lexer.sval.toLowerCase();
	    debug("checking op '" + op + "'");
	    if (lexer.sval.equals("and")) {
		match(lexer.TT_WORD);
		CQLNode term2 = parse_term(qualifier, relation);
		term = new CQLAndNode(term, term2);
	    } else if (lexer.sval.equals("or")) {
		match(lexer.TT_WORD);
		CQLNode term2 = parse_term(qualifier, relation);
		term = new CQLOrNode(term, term2);
	    } else if (lexer.sval.equals("not")) {
		match(lexer.TT_WORD);
		CQLNode term2 = parse_term(qualifier, relation);
		term = new CQLNotNode(term, term2);
	    } else if (lexer.sval.equals("prox")) {
		// ### Handle "prox"
	    } else {
		throw new CQLParseException("unrecognised boolean: '" +
					    lexer.sval + "'");
	    }
	}

	debug("no more ops");
	return term;
    }

    private CQLNode parse_term(String qualifier, String relation)
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
	    } else if (lexer.ttype != lexer.TT_WORD && lexer.ttype != '"') {
		throw new CQLParseException("expected qualifier or term, " +
					    "got " + lexer.render());
	    }

	    debug("non-parenthesised term");
	    word = lexer.sval;
	    match(lexer.ttype);
	    if (!isRelation())
		break;

	    qualifier = word;
	    relation = lexer.render(false);
	    match(lexer.ttype);
	    debug("qualifier='" + qualifier + ", relation='" + relation + "'");
	}

	CQLTermNode node = new CQLTermNode(qualifier, relation, word);
	debug("made term node " + node);
	return node;
    }

    boolean isRelation() {
	// ### Also need to handle <=, >=, <>
	return (lexer.ttype == '<' ||
		lexer.ttype == '>' ||
		lexer.ttype == '=');
    }

    private void match(int token)
	throws CQLParseException, IOException {
	debug("in match(" + lexer.render(token, null, true) + ")");
	if (lexer.ttype != token)
	    throw new CQLParseException("expected " +
					lexer.render(token, null, true) +
					", " + "got " + lexer.render());
	lexer.nextToken();
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
	if (args.length != 0) {
	    System.err.println("Usage: " + args[0]);
	    System.exit(1);
	}

	byte[] bytes = new byte[10000];
	try {
	    // Read in the whole of standard input in one go
	    int nbytes = System.in.read(bytes);
	} catch (java.io.IOException ex) {
	    System.err.println("Can't read query: " + ex.getMessage());
	    System.exit(2);
	}
	String cql = new String(bytes);
	CQLParser parser = new CQLParser();
	CQLNode root;
	try {
	    root = parser.parse(cql);
	    debug("root='" + root + "'");
	    System.out.println(root.toXCQL(0));
	} catch (CQLParseException ex) {
	    System.err.println("Syntax error: " + ex.getMessage());
	    System.exit(3);
	} catch (java.io.IOException ex) {
	    System.err.println("Can't compile query: " + ex.getMessage());
	    System.exit(4);
	}
    }
}


// This is a trivial subclass for java.io.StreamTokenizer which knows
// about the multi-character tokens "<=", ">=" and "<>", and included
// a render() method.  Used only by CQLParser.
//
class CQLLexer extends StreamTokenizer {
    private static boolean lexdebug;

    CQLLexer(String cql, boolean lexdebug) {
	super(new StringReader(cql));
	this.ordinaryChar('=');
	this.ordinaryChar('<');
	this.ordinaryChar('>');
	this.ordinaryChar('/');
	this.ordinaryChar('(');
	this.ordinaryChar(')');
	this.wordChars('\'', '\''); // prevent this from introducing strings
	this.lexdebug = lexdebug;
    }

    public int nextToken() throws java.io.IOException {
	int token = super.nextToken();
	if (lexdebug)
	    System.out.println("LEXDEBUG: " +
			       "token=" + token + ", " +
			       "nval=" + this.nval + ", " +
			       "sval=" + this.sval);

	return token;
    }

    String render() {
	return this.render(this.ttype, null, true);
    }

    String render(boolean quoteChars) {
	return this.render(this.ttype, null, quoteChars);
    }

    String render(int token, String str, boolean quoteChars) {
	String ret;

	if (token == this.TT_EOF) {
	    return "EOF";
	} else if (token == this.TT_EOL) {
	    return "EOL";
	} else if (token == this.TT_NUMBER) {
	    return "number: " + this.nval;
	} else if (token == this.TT_WORD) {
	    return "word: \"" + this.sval + "\"";
	} else if (token == '"') {
	    return "string: \"" + this.sval + "\"";
	}

	String res = String.valueOf((char) token);
	if (quoteChars) res = "'" + res + "'";
        return res;
    }
}
