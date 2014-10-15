/*
 * Copyright (c) 1995-2014, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */

package org.z3950.zing.cql.sparql;

/**
 * API helper for select queries
 * @author jakub
 */
public class SelectQuery extends Query {

  public SelectQuery(Select select, Where where) {
    form(select);
    where(where);
  }

  
  public Select select() {
    return (Select) form();
  }
  
  public SelectQuery select(Select select) {
    form(select);
    return this;
  }
  
}
