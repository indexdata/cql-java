/*
 * Copyright (c) 1995-2014, Index Datassss
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.z3950.zing.cql;

/**
 * API bridge between the parser and lexer implementation
 * @author jakub
 */
public interface CQLTokenizer {
  
  public static final int TT_EOF  = -1;
  public static final int TT_WORD = -3;
  public static final int TT_NOTHING = -4;
  
  public final static int TT_STRING = 999;      // quoted string
  public final static int TT_LE     = 1000;	// The "<=" relation
  public final static int TT_GE     = 1001;	// The ">=" relation
  public final static int TT_NE     = 1002;	// The "<>" relation
  public final static int TT_EQEQ   = 1003;	// The "==" relation
  public final static int TT_AND    = 1004;	// The "and" boolean
  public final static int TT_OR     = 1005;	// The "or" boolean
  public final static int TT_NOT    = 1006;	// The "not" boolean
  public final static int TT_PROX   = 1007;	// The "prox" boolean
  public final static int TT_SORTBY = 1008;	// The "sortby" operator
  
  /**
   * Consume next input token
   */
  public void move();
  
  /**
   * Return the value of the last consumed token
   * @return value of the token
   */
  public String value();
  
  /**
   * Return the type of the last consumed token
   * @return last consumed token
   */
  public int what();
  
  /**
   * Render the type and value of the last consumed token
   * @return human-readable string
   */
  public String render();
  
  /**
   * Render specified token type
   * @param what token type
   * @param quote true, if single characters should be quoted for readability
   * @return human-readable string
   */
  public String render(int what, boolean quote);
  
  public int pos();
  
}
