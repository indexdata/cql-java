/*
 * Copyright (c) 1995-2014, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */

package org.z3950.zing.cql.sparql;

import org.z3950.zing.cql.CQLDefaultNodeVisitor;
import org.z3950.zing.cql.CQLRelation;
import org.z3950.zing.cql.CQLTermNode;

/**
 *
 * @author jakub
 */
public class SPARQLNodeVisitor extends CQLDefaultNodeVisitor {
  private SelectQuery query = new SelectQuery(new Select(), new Where());
  private int objectCounter = 1; 
  
  //some initial configuration
  {
    query.prefix("bf", "http://bibframe.org/vocab/");
    query.select().var("*");
    query.where().pattern(new TriplePattern("?work", "a", "bf:Work"));
  }

  @Override
  public void onRelation(CQLRelation relation) {
    if (!relation.getBase().equals("=")) 
      throw new IllegalArgumentException("Can only handle '=' relations");
  }

  @Override
  public void onTermNode(CQLTermNode node) {
    //map index to predicate
    String obj = getNextObject();
    query.where().pattern(new TriplePattern("?work", node.getIndex(), obj));
    query.where().pattern(new Filter("contains(lcase("+obj+"), \""+node.getTerm().toLowerCase()+"\")"));
  }
  
  private String getNextObject() {
    return "?o" + objectCounter++;
  }
  
  public Query getQuery() {
    return query;
  }
  
}
