// $Id: CQLOrNode.java,v 1.5 2002-11-06 00:05:58 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents an OR node in a CQL parse-tree.
 *
 * @version	$Id: CQLOrNode.java,v 1.5 2002-11-06 00:05:58 mike Exp $
 */
public class CQLOrNode extends CQLBooleanNode {
    /**
     * Creates a new OR node with the specified left- and right-hand sides.
     */
    public CQLOrNode(CQLNode left, CQLNode right) {
	this.left = left;
	this.right = right;
    }

    String op() {
	return "or";
    }
}
