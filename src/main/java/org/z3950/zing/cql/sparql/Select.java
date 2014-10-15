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
public class Select implements Form {
  private final List<String> variables = new LinkedList<String>();
  
  public Select var(String name) {
    variables.add(name);
    return this;
  }

  @Override
  public void print(PrettyPrinter sw) {
    sw.startl("SELECT");
    for (String var : variables) {
      sw.put(" ");
      sw.put(var);
    }
    sw.endl();
  }
  
}
