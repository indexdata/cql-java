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
public class Optional extends GraphPatternSet {

  public Optional(GraphPattern pattern) {
    super(pattern);
  }
  
  @Override
  public void print(PrettyPrinter sw) {
    sw.startl("OPTIONAL ");
    super.print(sw);
  }
 
}
