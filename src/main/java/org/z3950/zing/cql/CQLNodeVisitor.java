package org.z3950.zing.cql;

/**
 * Allows to visit different types of nodes in the query tree. In most cases
 * you will want to extend CQLDefaultNodeVisitor and
 * choose only the methods you need to override.
 * CQLBooleanNode/Start/Op/End methods allow to generate prefix, infix or
 * postfix notations without needing to keep track of the parse tree.
 * See the CQLNodeVisitorTest for examples.
 *
 * @author jakub
 */
public interface CQLNodeVisitor {

    public void onSortNode(CQLSortNode node);

    public void onPrefixNode(CQLPrefixNode node);

    public void onBooleanNodeStart(CQLBooleanNode node);

    public void onBooleanNodeOp(CQLBooleanNode node);

    public void onBooleanNodeEnd(CQLBooleanNode node);

    public void onTermNode(CQLTermNode node);

    public void onRelation(CQLRelation relation);

}
