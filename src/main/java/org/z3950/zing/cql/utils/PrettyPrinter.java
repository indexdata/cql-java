/*
 * Copyright (c) 1995-2014, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */

package org.z3950.zing.cql.utils;

/**
 *
 * @author jakub
 */
public class PrettyPrinter {
  private final static String INDENT = "  ";
  private final StringBuilder sb;
  private final int level;
  
  public PrettyPrinter() {
    sb = new StringBuilder();
    level = 0;
  }
  
  private PrettyPrinter(StringBuilder sb, int level) {
    this.sb = sb;
    this.level = level;
  }
  
  public PrettyPrinter put(String append) {
    sb.append(append);
    return this;
  }
  
  public PrettyPrinter endl() {
    sb.append('\n');
    return this;
  }
  
  public PrettyPrinter startl(String append) {
    indent(level);
    return put(append);
  }
  
  public PrettyPrinter putl(String line) {
    return startl(line).endl();
  }
  
  public PrettyPrinter levelUp() {
    //create new instance with a higher levelUp, backed by the same builder
    return new PrettyPrinter(sb, level+1);
  }
  
  public PrettyPrinter indent(int level) {
    while (level-- > 0) {
      sb.append(INDENT);
    }
    return this;
  }

  @Override
  public String toString() {
    return sb.toString();
  }
  
}
