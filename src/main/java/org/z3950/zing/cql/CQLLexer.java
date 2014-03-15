/*
 * Copyright (c) 1995-2014, Index Datassss
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.z3950.zing.cql;

/**
 * Implementation of the CQL lexical syntax analyzer
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
    //eat whitespace
    while (qi < ql && strchr(" \t\r\n", qs.charAt(qi)))
      qi++;
    //eof
    if (qi == ql) {
      what = TT_EOF;
      return;
    }
    //current char
    char c = qs.charAt(qi);
    //separators
    if (strchr("()/", c)) {
      what = c;
      qi++;
    //comparitor
    } else if (strchr("<>=", c)) {
      what = c;
      qi++;
      //two-char comparitor
      if (qi < ql) {
        char d = qs.charAt(qi);
        String comp = String.valueOf((char) c) + String.valueOf((char) d);
        if (comp.equals("==")) {
          what = TT_EQEQ;
          qi++;
        }
        else if (comp.equals("<=")) {
          what = TT_LE;
          qi++;
        }
        else if (comp.equals(">=")) {
          what = TT_GE;
          qi++;
        }
        else if (comp.equals("<>")) {
          what = TT_NE;
          qi++;
        }
      }
    //quoted string
    } else if (strchr("\"", c)) { //no single-quotes
      what = '"';
      //remember quote char
      char mark = c;
      qi++;
      boolean escaped = false;
      buf.setLength(0); //reset buffer
      while (qi < ql) {
        if (!escaped && qs.charAt(qi) == mark) //terminator
          break;
        if (escaped && strchr("*?^\\", qs.charAt(qi))) //no escaping for d-quote
          buf.append("\\");
        if (!escaped && qs.charAt(qi) == '\\') { //escape-char
          escaped = true;
          qi++;
          continue;
        }
        escaped = false; //reset escape
        buf.append(qs.charAt(qi));
        qi++;
      }
      val = buf.toString();
      lval = val.toLowerCase();
      if (qi < ql)
        qi++;
      else //unterminated
        what = TT_EOF; //notify error
      //unquoted string
    } else {
      what = TT_WORD;
      buf.setLength(0); //reset buffer
      while (qi < ql
        && !strchr("()/<>= \t\r\n", qs.charAt(qi))) {
        buf.append(qs.charAt(qi));
        qi++;
      }
      val = buf.toString();
      lval = val.toLowerCase();
      if (lval.equals("or")) what = TT_OR;
      else if (lval.equals("and")) what = TT_AND;
      else if (lval.equals("not")) what = TT_NOT;
      else if (lval.equals("prox")) what = TT_PROX;
      else if (lval.equals("sortby")) what = TT_SORTBY;
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
      case '"':
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
        //a single character, such as '(' or '/' or relation
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

    CQLTokenizer lexer = new CQLLexer(cql, true);
    while ((lexer.what()) != TT_EOF) {
      lexer.move();
      System.out.println(lexer.render());
    }
  }
}
