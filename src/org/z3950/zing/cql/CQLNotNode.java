// $Id: CQLNotNode.java,v 1.1 2002-10-25 07:38:16 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a NOT node in a CQL parse-tree ...
 * ###
 *
 * @version	$Id: CQLNotNode.java,v 1.1 2002-10-25 07:38:16 mike Exp $
 */
class CQLNotNode extends CQLBooleanNode {
    public CQLNotNode(CQLNode left, CQLNode right) {
	this.left = left;
	this.right = right;
    }

    String op() {
	return "not";
    }
}
