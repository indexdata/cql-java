/*
 * Copyright (c) 1995-2014, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */

package org.z3950.zing.cql.sparql;

import org.z3950.zing.cql.utils.PrettyPrinter;

/**
 *
 * @author jakub
 */
public class Filter implements GraphPattern {
  private final String filter;

  public Filter(String filter) {
    this.filter = filter;
  }

  @Override
  public void print(PrettyPrinter pr) {
    pr.startl("FILTER ").put(filter).put(".").endl();
  }
  
}
