// $Id: CQLLexer.java,v 1.1 2002-10-30 09:19:26 mike Exp $

package org.z3950.zing.cql;
import java.io.StreamTokenizer;
import java.io.StringReader;


// This is a trivial subclass for java.io.StreamTokenizer which knows
// about the multi-character tokens "<=", ">=" and "<>", and includes
// a render() method.  Used only by CQLParser.
//
class CQLLexer extends StreamTokenizer {
    private static boolean DEBUG;
    static int TT_LE    = 1000;	// The "<=" relation
    static int TT_GE    = 1001;	// The ">=" relation
    static int TT_NE    = 1002;	// The "<>" relation
    static int TT_AND   = 1003;	// The "and" boolean
    static int TT_OR    = 1004;	// The "or" boolean
    static int TT_NOT   = 1005;	// The "not" boolean
    static int TT_PROX  = 1006;	// The "prox" boolean
    static int TT_ANY   = 1007;	// The "any" relation
    static int TT_ALL   = 1008;	// The "all" relation
    static int TT_EXACT = 1009;	// The "exact" relation

    // For halfDecentPushBack() and the code at the top of nextToken()
    private static int TT_UNDEFINED = -1000;
    int saved_ttype = TT_UNDEFINED;
    double saved_nval;
    String saved_sval;

    CQLLexer(String cql, boolean lexdebug) {
	super(new StringReader(cql));
	ordinaryChar('=');
	ordinaryChar('<');
	ordinaryChar('>');
	ordinaryChar('/');
	ordinaryChar('(');
	ordinaryChar(')');
	wordChars('\'', '\''); // prevent this from introducing strings
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
	if (ttype == TT_WORD) {
	    if (sval.equalsIgnoreCase("and")) {
		ttype = TT_AND;
	    } else if (sval.equalsIgnoreCase("or")) {
		ttype = TT_OR;
	    } else if (sval.equalsIgnoreCase("not")) {
		ttype = TT_NOT;
	    } else if (sval.equalsIgnoreCase("prox")) {
		ttype = TT_PROX;
	    } else if (sval.equalsIgnoreCase("any")) {
		ttype = TT_ANY;
	    } else if (sval.equalsIgnoreCase("all")) {
		ttype = TT_ALL;
	    } else if (sval.equalsIgnoreCase("exact")) {
		ttype = TT_EXACT;
	    }
	}
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
	    return "number: " + nval;
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
	} else if (token == TT_AND) {
	    return "and";
	} else if (token == TT_OR) {
	    return "or";
	} else if (token == TT_NOT) {
	    return "not";
	} else if (token == TT_PROX) {
	    return "prox";
	} else if (token == TT_ANY) {
	    return "any";
	} else if (token == TT_ALL) {
	    return "all";
	} else if (token == TT_EXACT) {
	    return "exact";
	}

	String res = String.valueOf((char) token);
	if (quoteChars) res = "'" + res + "'";
        return res;
    }

    public static void main(String[] args) throws Exception {
	CQLLexer lexer = new CQLLexer(args[0], true);
	int token;

	while ((token = lexer.nextToken()) != TT_EOF) {
	    // Nothing to do: debug() statements render tokens for us
	}
    }
}
