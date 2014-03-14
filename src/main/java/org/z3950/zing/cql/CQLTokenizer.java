/*
 * Copyright (c) 1995-2014, Index Datassss
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.z3950.zing.cql;

/**
 *
 * @author jakub
 */
public interface CQLTokenizer {
  
  public static final int TT_EOF  = -1;
  public static final int TT_WORD = -3;
  public static final int TT_NOTHING = -4;
  
  public final static int TT_LE     = 1000;	// The "<=" relation
  public final static int TT_GE     = 1001;	// The ">=" relation
  public final static int TT_NE     = 1002;	// The "<>" relation
  public final static int TT_EQEQ   = 1003;	// The "==" relation
  public final static int TT_AND    = 1004;	// The "and" boolean
  public final static int TT_OR     = 1005;	// The "or" boolean
  public final static int TT_NOT    = 1006;	// The "not" boolean
  public final static int TT_PROX   = 1007;	// The "prox" boolean
  public final static int TT_SORTBY = 1008;	// The "sortby" operator
  
  public void move();
  
  public String value();
  
  public int what();
  
  public String render();
  
  public String render(int what, boolean quote);
  
  public int pos();
  
}
