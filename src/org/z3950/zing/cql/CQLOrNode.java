// $Id: CQLOrNode.java,v 1.1 2002-10-25 07:38:16 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents an OR node in a CQL parse-tree ...
 * ###
 *
 * @version	$Id: CQLOrNode.java,v 1.1 2002-10-25 07:38:16 mike Exp $
 */
class CQLOrNode extends CQLBooleanNode {
    public CQLOrNode(CQLNode left, CQLNode right) {
	this.left = left;
	this.right = right;
    }

    String op() {
	return "or";
    }
}
