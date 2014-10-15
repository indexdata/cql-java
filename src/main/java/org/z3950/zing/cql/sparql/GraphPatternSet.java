/*
 * Copyright (c) 1995-2014, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */

package org.z3950.zing.cql.sparql;

import org.z3950.zing.cql.utils.PrettyPrinter;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jakub
 */
public class GraphPatternSet implements GraphPattern {
  private final List<GraphPattern> patterns = new LinkedList<GraphPattern>();

  public GraphPatternSet(GraphPattern... patterns) {
    for (GraphPattern pattern : patterns) 
      this.patterns.add(pattern);
  }
  
  public GraphPatternSet pattern(GraphPattern pattern) {
    patterns.add(pattern);
    return this;
  }

  @Override
  public void print(PrettyPrinter pp) {
    pp.put("{").endl();
    for (GraphPattern tp : patterns) {
      tp.print(pp.levelUp());
    }
    pp.putl("}");
  }
}
