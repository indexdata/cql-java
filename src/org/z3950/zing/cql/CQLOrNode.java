// $Id: CQLOrNode.java,v 1.4 2002-10-31 22:22:01 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents an OR node in a CQL parse-tree.
 * ##
 *
 * @version	$Id: CQLOrNode.java,v 1.4 2002-10-31 22:22:01 mike Exp $
 */
public class CQLOrNode extends CQLBooleanNode {
    public CQLOrNode(CQLNode left, CQLNode right) {
	this.left = left;
	this.right = right;
    }

    String op() {
	return "or";
    }
}
