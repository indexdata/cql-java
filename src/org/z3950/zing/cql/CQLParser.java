// $Header: /home/cvsroot/cql-java/src/org/z3950/zing/cql/CQLParser.java,v 1.2 2002-10-24 16:05:15 mike Exp $

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
 * @version	$Id: CQLParser.java,v 1.2 2002-10-24 16:05:15 mike Exp $
 * @see		<A href="http://zing.z3950.org/cql/index.html"
 *		        >http://zing.z3950.org/cql/index.html</A>
 */
class CQLCompiler {
    private String cql;
    private String qualset;
    private Properties qualsetProperties;
    private StreamTokenizer st;

    private class CQLParseException extends Exception {
	CQLParseException(String s) { super(s); }
    }

    public CQLCompiler(String cql, String qualset) {
	this.cql = cql;
	this.qualset = qualset;
    }

    public String convertToPQN()
	throws FileNotFoundException, IOException {

	if (qualsetProperties == null) {
	    //      ###	Could think about caching named qualifier sets
	    //		across compilations (i.e. shared, in a static
	    //		Hashtable, between multiple CQLCompiler
	    //		instances.)  Probably not worth it.
	    InputStream is = this.getClass().getResourceAsStream(qualset);
	    if (is == null)
		throw new FileNotFoundException("getResourceAsStream(" +
						qualset + ")");
	    qualsetProperties = new Properties();
	    qualsetProperties.load(is);
	}

	st = new StreamTokenizer(new StringReader(cql));
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
	String ret;
	try {
	    ret = parse_expression();
	} catch (CQLParseException ex) {
	    System.err.println("### Oops: " + ex);
	    return null;
	}

	if (st.ttype != st.TT_EOF) {
	    System.err.println("### Extra bits: " + render(st));
	    return null;
	}

	// Interpret attributes as BIB-1 unless otherwise specified
	return "@attrset bib-1 " + ret;
    }

    private String parse_expression()
	throws CQLParseException, IOException {
	String term = parse_term();

	while (st.ttype == st.TT_WORD) {
	    String op = st.sval.toLowerCase();
	    if (!st.sval.equals("and") &&
		!st.sval.equals("or") &&
		!st.sval.equals("not"))
		break;
	    match(st.TT_WORD);
	    String term2 = parse_term();
	    term = "@" + op + " " + term + " " + term2;
	}

	return term;
    }

    private String parse_term()
	throws CQLParseException, IOException {
	if (st.ttype == '(') {
	    match('(');
	    String expr = parse_expression();
	    match(')');
	    return expr;
	}

	String word = null;
	String attrs = "";

	// ### We treat ',' and '=' equivalently here, which isn't quite right.
	while (st.ttype == st.TT_WORD) {
	    word = st.sval;
	    match(st.TT_WORD);
	    if (st.ttype != '=' && st.ttype != ',') {
		// end of qualifer list
		break;
	    }

	    String attr = qualsetProperties.getProperty(word);
	    if (attr == null) {
		throw new CQLParseException("unrecognised qualifier: " + word);
	    }
	    attrs = attrs + attr + " ";
	    match(st.ttype);
	    word = null;	// mark as not-yet-read
	}

	if (word == null) {
	    // got to the end of a "foo,bar=" sequence
	    word = st.sval;
	    if (st.ttype != '\'' || st.ttype != '"') {
		word = "\"" + word + "\"";
		match(st.ttype);
	    } else {
		match(st.TT_WORD);
	    }
	}

	return attrs + word;
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

	switch (token) {
	case st.TT_EOF: return "EOF";
	case st.TT_EOL: return "EOL";
	case st.TT_NUMBER: return "number";
	case st.TT_WORD: ret = "word"; break;
	case '"': case '\'': ret = "string"; break;
	default: return "'" + String.valueOf((char) token) + "'";
	}

	if (str != null)
	    ret += "(\"" + str + "\")";
	return ret;
    }

    // ### Not really the right place for this test harness.
    //
    // e.g. java uk.org.miketaylor.zoom.CQLCompiler
    //		'(au=Kerninghan or au=Ritchie) and ti=Unix' qualset.properties
    // yields:
    //	@and
    //		@or
    //			@attr 1=1 @attr 4=1 Kerninghan
    //			@attr 1=1 @attr 4=1 Ritchie
    //		@attr 1=4 @attr 4=1 Unix
    //
    public static void main (String[] args) {
	if (args.length != 2) {
	    System.err.println("Usage: CQLQuery <cql> <qualset>");
	    System.exit(1);
	}

	CQLCompiler cc = new CQLCompiler(args[0], args[1]);
	try {
	    String pqn = cc.convertToPQN();
	    System.out.println(pqn);
	} catch (FileNotFoundException ex) {
	    System.err.println("Can't find qualifier set: " + ex);
	    System.exit(2);
	} catch (IOException ex) {
	    System.err.println("Can't read qualifier set: " + ex);
	    System.exit(2);
	}
    }
}
