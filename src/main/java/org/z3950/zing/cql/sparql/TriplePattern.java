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
public class TriplePattern implements GraphPattern {
  private final String subject;
  private final String predicate;
  private final String object;

  public TriplePattern(String subjet, String predicate, String object) {
    this.subject = subjet;
    this.predicate = predicate;
    this.object = object;
  }
  
  @Override
  public void print(PrettyPrinter pp) {
    pp.startl(subject).put(" ")
      .put(predicate).put(" ")
      .put(object).put(" .").endl();
  }
  
}
