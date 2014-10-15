/*
 * Copyright (c) 1995-2014, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */

package org.z3950.zing.cql.sparql;

import org.z3950.zing.cql.utils.PrettyPrinter;
import java.util.Map;
import java.util.TreeMap;
import org.z3950.zing.cql.utils.Printable;

/**
 *
 * @author jakub
 */
public class Query implements Printable {
  private final Map<String, Prefix> prefixes = new TreeMap<String, Prefix>();
  private Form form;
  private Where where;

  public Query() {
  }

  public Query(Form form, Where where) {
    this.form = form;
    this.where = where;
  }
  
  
  public Prefix prefix(String name) {
    return prefixes.get(name);
  }
  
  public Query prefix(String name, String url) {
    prefixes.put(name, new Prefix(name, url));
    return this;
  }
  
  public Query prefix(Prefix prefix) {
    prefixes.put(prefix.getName(), prefix);
    return this;
  }
  
  public Where where() {
    return where;
  }
  
  public Query where(Where where) {
    this.where = where;
    return this;
  }
  
  public Form form() {
    return form;
  }
  
  public Query form(Form form) {
    this.form = form;
    return this;
  }
  
  @Override
  public void print(PrettyPrinter sw) {
    for (Prefix prefix : prefixes.values()) {
      sw.startl("PREFIX ")
        .put(prefix.getName()).put(": ")
        .put("<").put(prefix.getUrl()).put(">").endl();
    }
    form.print(sw);
    where.print(sw);
  }
}
