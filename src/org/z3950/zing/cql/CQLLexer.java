// $Id: CQLLexer.java,v 1.3 2002-11-01 23:45:28 mike Exp $

package org.z3950.zing.cql;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Hashtable;


// This is a semi-trivial subclass for java.io.StreamTokenizer that:
//	* Has a halfDecentPushBack() method that actually works
//	* Includes a render() method
//	* Knows about the multi-character tokens "<=", ">=" and "<>"
//	* Recognises a set of keywords as tokens in their own right
//	* Includes some primitive debugging-output facilities
// It's used only by CQLParser.
//
class CQLLexer extends StreamTokenizer {
    // New publicly visible token-types
    static int TT_LE        = 1000;	// The "<=" relation
    static int TT_GE        = 1001;	// The ">=" relation
    static int TT_NE        = 1002;	// The "<>" relation
    static int TT_AND       = 1003;	// The "and" boolean
    static int TT_OR        = 1004;	// The "or" boolean
    static int TT_NOT       = 1005;	// The "not" boolean
    static int TT_PROX      = 1006;	// The "prox" boolean
    static int TT_ANY       = 1007;	// The "any" relation
    static int TT_ALL       = 1008;	// The "all" relation
    static int TT_EXACT     = 1009;	// The "exact" relation
    static int TT_pWORD     = 1010;	// The "word" proximity unit
    static int TT_SENTENCE  = 1011;	// The "sentence" proximity unit
    static int TT_PARAGRAPH = 1012;	// The "paragraph" proximity unit
    static int TT_ELEMENT   = 1013;	// The "element" proximity unit
    static int TT_ORDERED   = 1014;	// The "ordered" proximity ordering
    static int TT_UNORDERED = 1015;	// The "unordered" proximity ordering
    static int TT_RELEVANT  = 1016;	// The "relevant" relation modifier
    static int TT_FUZZY     = 1017;	// The "fuzzy" relation modifier
    static int TT_STEM      = 1018;	// The "stem" relation modifier

    // Support for keywords.  It would be nice to compile this linear
    // list into a Hashtable, but it's hard to store ints as hash
    // values, and next to impossible to use them as hash keys.  So
    // we'll just scan the (very short) list every time we need to do
    // a lookup.
    private class Keyword {
	int token;
	String keyword;
	Keyword(int token, String keyword) {
	    this.token = token;
	    this.keyword = keyword;
	}
    }
    // This should logically be static, but Java won't allow it  :-P
    private Keyword[] keywords = {
	new Keyword(TT_AND, "and"),
	new Keyword(TT_OR,  "or"),
	new Keyword(TT_NOT, "not"),
	new Keyword(TT_PROX, "prox"),
	new Keyword(TT_ANY, "any"),
	new Keyword(TT_ALL, "all"),
	new Keyword(TT_EXACT, "exact"),
	new Keyword(TT_pWORD, "word"),
	new Keyword(TT_SENTENCE, "sentence"),
	new Keyword(TT_PARAGRAPH, "paragraph"),
	new Keyword(TT_ELEMENT, "element"),
	new Keyword(TT_ORDERED, "ordered"),
	new Keyword(TT_UNORDERED, "unordered"),
	new Keyword(TT_RELEVANT, "relevant"),
	new Keyword(TT_FUZZY, "fuzzy"),
	new Keyword(TT_STEM, "stem"),
    };

    // For halfDecentPushBack() and the code at the top of nextToken()
    private static int TT_UNDEFINED = -1000;
    private int saved_ttype = TT_UNDEFINED;
    private double saved_nval;
    private String saved_sval;

    // Controls debugging output
    private static boolean DEBUG;

    CQLLexer(String cql, boolean lexdebug) {
	super(new StringReader(cql));
	ordinaryChar('=');
	ordinaryChar('<');
	ordinaryChar('>');
	ordinaryChar('/');
	ordinaryChar('(');
	ordinaryChar(')');
	wordChars('\'', '\''); // prevent this from introducing strings
	parseNumbers();
	DEBUG = lexdebug;
    }

    private static void debug(String str) {
	if (DEBUG)
	    System.err.println("LEXDEBUG: " + str);
    }

    // I don't honestly understand why we need this, but the
    // documentation for java.io.StreamTokenizer.pushBack() is pretty
    // vague about its semantics, and it seems to me that they could
    // be summed up as "it doesn't work".  This version has the very
    // clear semantics "pretend I didn't call nextToken() just then".
    //
    private void halfDecentPushBack() {
	saved_ttype = ttype;
	saved_nval = nval;
	saved_sval = sval;
    }

    public int nextToken() throws java.io.IOException {
	if (saved_ttype != TT_UNDEFINED) {
	    ttype = saved_ttype;
	    nval = saved_nval;
	    sval = saved_sval;
	    saved_ttype = TT_UNDEFINED;
	    debug("using saved ttype=" + ttype + ", " +
		  "nval=" + nval + ", sval='" + sval + "'");
	    return ttype;
	}

	underlyingNextToken();
	if (ttype == '<') {
	    debug("token starts with '<' ...");
	    underlyingNextToken();
	    if (ttype == '=') {
		debug("token continues with '=' - it's '<='");
		ttype = TT_LE;
	    } else if (ttype == '>') {
		debug("token continues with '>' - it's '<>'");
		ttype = TT_NE;
	    } else {
		debug("next token is " + render() + " (pushed back)");
		halfDecentPushBack();
		ttype = '<';
		debug("AFTER: ttype is now " + ttype + " - " + render());
	    }
	} else if (ttype == '>') {
	    debug("token starts with '>' ...");
	    underlyingNextToken();
	    if (ttype == '=') {
		debug("token continues with '=' - it's '>='");
		ttype = TT_GE;
	    } else {
		debug("next token is " + render() + " (pushed back)");
		halfDecentPushBack();
		ttype = '>';
		debug("AFTER: ttype is now " + ttype + " - " + render());
	    }
	}

	debug("done nextToken(): ttype=" + ttype + ", " +
	      "nval=" + nval + ", " + "sval='" + sval + "'" +
	      " (" + render() + ")");

	return ttype;
    }

    // It's important to do keyword recognition here at the lowest
    // level, otherwise when one of these words follows "<" or ">"
    // (which can be the beginning of multi-character tokens) it gets
    // pushed back as a string, and its keywordiness is not
    // recognised.
    //
    public int underlyingNextToken() throws java.io.IOException {
	super.nextToken();
	if (ttype == TT_WORD)
	    for (int i = 0; i < keywords.length; i++)
		if (sval.equalsIgnoreCase(keywords[i].keyword))
		    ttype = keywords[i].token;

	return ttype;
    }

    // Simpler interface for the usual case: current token with quoting
    String render() {
	return render(ttype, true);
    }

    String render(int token, boolean quoteChars) {
	if (token == TT_EOF) {
	    return "EOF";
	} else if (token == TT_NUMBER) {
	    return new Integer((int) nval).toString();
	} else if (token == TT_WORD) {
	    return "word: " + sval;
	} else if (token == '"') {
	    return "string: \"" + sval + "\"";
	} else if (token == TT_LE) {
	    return "<=";
	} else if (token == TT_GE) {
	    return ">=";
	} else if (token == TT_NE) {
	    return "<>";
	}

	// Check whether its associated with one of the keywords
	for (int i = 0; i < keywords.length; i++)
	    if (token == keywords[i].token)
		return keywords[i].keyword;

	// Otherwise it must be a single character, such as '(' or '/'.
	String res = String.valueOf((char) token);
	if (quoteChars) res = "'" + res + "'";
        return res;
    }

    public static void main(String[] args) throws Exception {
	if (args.length > 1) {
	    System.err.println("Usage: CQLLexer [<CQL-query>]");
	    System.err.println("If unspecified, query is read from stdin");
	    System.exit(1);
	}

	String cql;
	if (args.length == 1) {
	    cql = args[0];
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

	CQLLexer lexer = new CQLLexer(cql, true);
	int token;
	while ((token = lexer.nextToken()) != TT_EOF) {
	    // Nothing to do: debug() statements render tokens for us
	}
    }
}
