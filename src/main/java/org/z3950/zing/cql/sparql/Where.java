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
public class Where extends GraphPatternSet {

  public Where(GraphPattern... patterns) {
    super(patterns);
  }
  
  @Override
  public void print(PrettyPrinter sw) {
    sw.startl("WHERE ");
    super.print(sw);
  }
  
}
