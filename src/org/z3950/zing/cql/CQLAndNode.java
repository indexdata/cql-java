// $Id: CQLAndNode.java,v 1.5 2002-11-06 00:05:58 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents an AND node in a CQL parse-tree.
 *
 * @version	$Id: CQLAndNode.java,v 1.5 2002-11-06 00:05:58 mike Exp $
 */
public class CQLAndNode extends CQLBooleanNode {
    /**
     * Creates a new AND node with the specified left- and right-hand sides.
     */
    public CQLAndNode(CQLNode left, CQLNode right) {
	this.left = left;
	this.right = right;
    }

    String op() {
	return "and";
    }
}
