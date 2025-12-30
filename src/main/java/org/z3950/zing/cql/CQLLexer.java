package org.z3950.zing.cql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Implementation of the CQL lexical syntax analyzer
 *
 * @author jakub
 */
public class CQLLexer implements CQLTokenizer {
    private String qs;
    private int qi;
    private int ql;
    private int what = TT_NOTHING;
    private String val;
    private String lval;
    private StringBuilder buf = new StringBuilder();

    public CQLLexer(String cql, boolean debug) {
        qs = cql;
        ql = cql.length();
    }

    @Override
    public void move() {
        // eat whitespace
        while (qi < ql && strchr(" \t\r\n", qs.charAt(qi)))
            qi++;
        // eof
        if (qi == ql) {
            what = TT_EOF;
            return;
        }
        // current char
        char c = qs.charAt(qi);
        // separators
        if (strchr("()/", c)) {
            what = c;
            qi++;
            // comparitor
        } else if (strchr("<>=", c)) {
            what = c;
            qi++;
            // two-char comparitor
            if (qi < ql) {
                char d = qs.charAt(qi);
                String comp = String.valueOf((char) c) + String.valueOf((char) d);
                if (comp.equals("==")) {
                    what = TT_EQEQ;
                    qi++;
                } else if (comp.equals("<=")) {
                    what = TT_LE;
                    qi++;
                } else if (comp.equals(">=")) {
                    what = TT_GE;
                    qi++;
                } else if (comp.equals("<>")) {
                    what = TT_NE;
                    qi++;
                }
            }
            // quoted string
        } else if (strchr("\"", c)) { // no single-quotes
            what = TT_STRING;
            // remember quote char
            char mark = c;
            qi++;
            buf.setLength(0); // reset buffer
            while (qi < ql && qs.charAt(qi) != mark) {
                if (qs.charAt(qi) == '\\') { // escape-char
                    if (qi == ql - 1) {
                        break; // unterminated
                    }
                    buf.append(qs.charAt(qi));
                    qi++;
                }
                buf.append(qs.charAt(qi));
                qi++;
            }
            val = buf.toString();
            lval = val.toLowerCase();
            if (qi < ql)
                qi++;
            else // unterminated
                what = TT_EOF; // notify error
            // unquoted string
        } else {
            what = TT_WORD;
            buf.setLength(0); // reset buffer
            while (qi < ql
                    && !strchr("()/<>= \t\r\n", qs.charAt(qi))) {
                buf.append(qs.charAt(qi));
                qi++;
            }
            val = buf.toString();
            lval = val.toLowerCase();
            if (lval.equals("or"))
                what = TT_OR;
            else if (lval.equals("and"))
                what = TT_AND;
            else if (lval.equals("not"))
                what = TT_NOT;
            else if (lval.equals("prox"))
                what = TT_PROX;
            else if (lval.equals("sortby"))
                what = TT_SORTBY;
        }
    }

    private boolean strchr(String s, char ch) {
        return s.indexOf(ch) >= 0;
    }

    @Override
    public String value() {
        return val;
    }

    @Override
    public int what() {
        return what;
    }

    @Override
    public String render() {
        return render(what, true);
    }

    @Override
    public String render(int token, boolean quoteChars) {
        switch (token) {
            case TT_EOF:
                return "EOF";
            case TT_WORD:
                return "word: '" + val + "'";
            case TT_STRING:
                return "string: \"" + val + "\"";
            case TT_LE:
                return "<=";
            case TT_GE:
                return ">=";
            case TT_NE:
                return "<>";
            case TT_EQEQ:
                return "==";
            case TT_AND:
                return "and";
            case TT_NOT:
                return "not";
            case TT_OR:
                return "or";
            case TT_PROX:
                return "prox";
            case TT_SORTBY:
                return "sortby";
            default:
                // a single character, such as '(' or '/' or relation
                String res = String.valueOf((char) token);
                if (quoteChars)
                    res = "'" + res + "'";
                return res;
        }
    }

    @Override
    public int pos() {
        return qi;
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

        CQLTokenizer lexer = new CQLLexer(cql, true);
        while ((lexer.what()) != TT_EOF) {
            lexer.move();
            System.out.println(lexer.render());
        }
    }
}
