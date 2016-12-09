/*
 * Copyright (c) 1995-2014, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */

package org.z3950.zing.cql;

/**
 * Query tree visitor with default (no-op) implementation
 * @author jakub
 */
public class CQLDefaultNodeVisitor implements CQLNodeVisitor {
  
  @Override
  public void onSortNode(CQLSortNode node) {
  }

  @Override
  public void onPrefixNode(CQLPrefixNode node) {
  }

  @Override
  public void onBooleanNodeStart(CQLBooleanNode node) {
  }

  @Override
  public void onBooleanNodeOp(CQLBooleanNode node) {
  }

  @Override
  public void onBooleanNodeEnd(CQLBooleanNode node) {
  }
  
  @Override
  public void onTermNode(CQLTermNode node) {
  }

  @Override
  public void onRelation(CQLRelation relation) {
  }
  
}
