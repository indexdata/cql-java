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
  public void onPrfixNode(CQLPrefixNode node) {
  }

  @Override
  public void onBooleanNode(CQLBooleanNode node) {
  }

  @Override
  public void onProxNode(CQLProxNode node) {
  }

  @Override
  public void onAndNode(CQLAndNode node) {
  }

  @Override
  public void onOrNode(CQLOrNode node) {
  }

  @Override
  public void onNotNode(CQLNotNode node) {
  }

  @Override
  public void onTermNode(CQLTermNode node) {
  }

  @Override
  public void onRelation(CQLRelation relation) {
  }
  
}
