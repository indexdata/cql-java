// $Id: CQLNotNode.java,v 1.2 2002-10-25 16:01:26 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a NOT node in a CQL parse-tree ...
 * ###
 *
 * @version	$Id: CQLNotNode.java,v 1.2 2002-10-25 16:01:26 mike Exp $
 */
public class CQLNotNode extends CQLBooleanNode {
    public CQLNotNode(CQLNode left, CQLNode right) {
	this.left = left;
	this.right = right;
    }

    String op() {
	return "not";
    }
}
