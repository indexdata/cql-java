// $Id: CQLNotNode.java,v 1.5 2002-11-06 00:05:58 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a NOT node in a CQL parse-tree.
 *
 * @version	$Id: CQLNotNode.java,v 1.5 2002-11-06 00:05:58 mike Exp $
 */
public class CQLNotNode extends CQLBooleanNode {
    /**
     * Creates a new NOT node with the specified left- and right-hand sides.
     */
    public CQLNotNode(CQLNode left, CQLNode right) {
	this.left = left;
	this.right = right;
    }

    String op() {
	return "not";
    }
}
