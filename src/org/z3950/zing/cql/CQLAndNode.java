// $Id: CQLAndNode.java,v 1.3 2002-10-30 09:19:26 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents an AND node in a CQL parse-tree.
 * ###
 *
 * @version	$Id: CQLAndNode.java,v 1.3 2002-10-30 09:19:26 mike Exp $
 */
public class CQLAndNode extends CQLBooleanNode {
    public CQLAndNode(CQLNode left, CQLNode right) {
	this.left = left;
	this.right = right;
    }

    String op() {
	return "and";
    }
}
