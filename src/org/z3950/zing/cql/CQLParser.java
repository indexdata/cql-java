// $Id: CQLParser.java,v 1.6 2002-10-25 16:11:05 mike Exp $

package org.z3950.zing.cql;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StreamTokenizer;


/**
 * Compiles a CQL string into a parse tree ...
 * ###
 *
 * @version	$Id: CQLParser.java,v 1.6 2002-10-25 16:11:05 mike Exp $
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
	throws FileNotFoundException, IOException {
	this.cql = cql;
	st = new StreamTokenizer(new StringReader(cql));
	// ### these settings are wrong
	st.wordChars('/', '/');
	st.wordChars('0', '9');	// ### but 1 is still recognised as TT_NUM
	st.wordChars('.', '.');
	st.wordChars('-', '-');
	st.ordinaryChar('=');
	st.ordinaryChar(',');
	st.ordinaryChar('(');
	st.ordinaryChar(')');

//  	int token;
//	while ((token = st.nextToken()) != st.TT_EOF) {
//	    System.out.println("token=" + token + ", " +
//			       "nval=" + st.nval + ", " +
//			       "sval=" + st.sval);
//	}

	st.nextToken();
	CQLNode root;
	try {
	    root = parse_expression();
	} catch (CQLParseException ex) {
	    System.err.println("### Oops: " + ex);
	    return null;
	}

	if (st.ttype != st.TT_EOF) {
	    System.err.println("### Extra bits: " + render(st));
	    return null;
	}

	return root;
    }

    private CQLNode parse_expression()
	throws CQLParseException, IOException {
	CQLNode term = parse_term();

	while (st.ttype == st.TT_WORD) {
	    String op = st.sval.toLowerCase();
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
	}

	return term;
    }

    private CQLNode parse_term()
	throws CQLParseException, IOException {
	if (st.ttype == '(') {
	    match('(');
	    CQLNode expr = parse_expression();
	    match(')');
	    return expr;
	}

	String word = st.sval;
	return new CQLTermNode("x", "=", word);
    }

    private void match(int token)
	throws CQLParseException, IOException {
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
	    return "number";
	} else if (token == st.TT_WORD) {
	    return "word";
	} else if (token == '"' && token == '\'') {
	    return "string";
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

	byte[] bytes = new byte[1000];
	try {
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
	    System.out.println(root.toXCQL(0));
	} catch (java.io.IOException ex) {
	    System.err.println("Can't compile query: " + ex);
	    System.exit(3);
	}
    }
}
