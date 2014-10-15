/*
 * Copyright (c) 1995-2014, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */

package org.z3950.zing.cql;

/**
 * Allows to visit different types of nodes in the query tree.
 * @author jakub
 */
public interface CQLNodeVisitor {
  
  public void onSortNode(CQLSortNode node);
  
  public void onPrfixNode(CQLPrefixNode node);
  
  public void onBooleanNode(CQLBooleanNode node);
  
  public void onProxNode(CQLProxNode node);
  
  public void onAndNode(CQLAndNode node);
  
  public void onOrNode(CQLOrNode node);
  
  public void onNotNode(CQLNotNode node);
  
  public void onTermNode(CQLTermNode node);
  
  public void onRelation(CQLRelation relation);
  
}
