// $Id: CQLParser.java,v 1.7 2002-10-25 16:56:43 mike Exp $

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
 * @version	$Id: CQLParser.java,v 1.7 2002-10-25 16:56:43 mike Exp $
 * @see		<A href="http://zing.z3950.org/cql/index.html"
 *		        >http://zing.z3950.org/cql/index.html</A>
 */
public class CQLParser {
    private String cql;
    private StreamTokenizer st;

    private class CQLParseException extends Exception {
	CQLParseException(String s) { super(s); }
    }

    public CQLParser() {
	// Nothing to do: do we need this constructor, then?
    }

    public CQLNode parse(String cql)
	throws CQLParseException, IOException {
	this.cql = cql;
	st = new StreamTokenizer(new StringReader(cql));
	st.ordinaryChar('=');
	st.ordinaryChar('<');
	st.ordinaryChar('>');
	st.ordinaryChar('/');
	st.ordinaryChar('(');
	st.ordinaryChar(')');

	if (false) {
	    // Lexical debug
	    int token;
	    while ((token = st.nextToken()) != st.TT_EOF) {
		System.out.println("token=" + token + ", " +
				   "nval=" + st.nval + ", " +
				   "sval=" + st.sval);
	    }
	    System.exit(0);
	}

	st.nextToken();
	System.err.println("*about to parse_query()");
	CQLNode root = parse_query();
	if (st.ttype != st.TT_EOF)
	    throw new CQLParseException("junk after end: " + render(st));

	return root;
    }

    private CQLNode parse_query()
	throws CQLParseException, IOException {
	System.err.println("*in parse_query()");

	CQLNode term = parse_term();
	while (st.ttype == st.TT_WORD) {
	    String op = st.sval.toLowerCase();
	    System.err.println("*checking op '" + op + "'");
	    if (st.sval.equals("and")) {
		match(st.TT_WORD);
		CQLNode term2 = parse_term();
		term = new CQLAndNode(term, term2);
	    } else if (st.sval.equals("or")) {
		match(st.TT_WORD);
		CQLNode term2 = parse_term();
		term = new CQLOrNode(term, term2);
	    } else if (st.sval.equals("not")) {
		match(st.TT_WORD);
		CQLNode term2 = parse_term();
		term = new CQLNotNode(term, term2);
	    }
	    // ### Need to handle "prox"
	}

	System.err.println("*no more ops");
	return term;
    }

    private CQLNode parse_term()
	throws CQLParseException, IOException {
	System.err.println("*in parse_term()");
	if (st.ttype == '(') {
	    match('(');
	    CQLNode expr = parse_query();
	    match(')');
	    return expr;
	}

	System.err.println("*not a parenthesised term");
	// ### Need to parse qualifier-relation pairs
	String word = st.sval;
	match(st.ttype);
	CQLTermNode node = new CQLTermNode("x", "=", word);
	System.err.println("*made term node " + node);
	return node;
    }

    private void match(int token)
	throws CQLParseException, IOException {
	System.err.println("*in match(" + render(st, token, null) + ")");
	if (st.ttype != token)
	    throw new CQLParseException("expected " + render(st, token, null) +
					", " + "got " + render(st));
	st.nextToken();
    }

    // ### This utility should surely be a method of the StreamTokenizer class
    private static String render(StreamTokenizer st) {
	return render(st, st.ttype, null);
    }

    private static String render(StreamTokenizer st, int token, String str) {
	String ret;

	if (token == st.TT_EOF) {
	    return "EOF";
	} else if (token == st.TT_EOL) {
	    return "EOL";
	} else if (token == st.TT_NUMBER) {
	    return "number: " + st.nval;
	} else if (token == st.TT_WORD) {
	    return "word: \"" + st.sval + "\"";
	} else if (token == '"' || token == '\'') {
	    return "string: \"" + st.sval + "\"";
	}

        return "'" + String.valueOf((char) token) + "'";
    }


    // Test harness.
    //
    // e.g. echo '(au=Kerninghan or au=Ritchie) and ti=Unix' |
    //				java org.z3950.zing.cql.CQLParser
    // yields:
    //	###
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
	    System.err.println("Can't read query: " + ex);
	    System.exit(2);
	}
	String cql = new String(bytes);
	CQLParser parser = new CQLParser();
	CQLNode root;
	try {
	    root = parser.parse(cql);
	    System.err.println("root='" + root + "'");
	    System.out.println(root.toXCQL(0));
	} catch (CQLParseException ex) {
	    System.err.println("Syntax error: " + ex);
	    System.exit(3);
	} catch (java.io.IOException ex) {
	    System.err.println("Can't compile query: " + ex);
	    System.exit(4);
	}
    }
}
