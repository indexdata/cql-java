// $Id: CQLOrNode.java,v 1.2 2002-10-25 16:01:26 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents an OR node in a CQL parse-tree ...
 * ###
 *
 * @version	$Id: CQLOrNode.java,v 1.2 2002-10-25 16:01:26 mike Exp $
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
