// $Id: CQLAndNode.java,v 1.1 2002-10-25 07:38:16 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents an AND node in a CQL parse-tree ...
 * ###
 *
 * @version	$Id: CQLAndNode.java,v 1.1 2002-10-25 07:38:16 mike Exp $
 */
class CQLAndNode extends CQLBooleanNode {
    public CQLAndNode(CQLNode left, CQLNode right) {
	this.left = left;
	this.right = right;
    }

    String op() {
	return "and";
    }
}
