// $Id: CQLBooleanNode.java,v 1.2 2002-10-25 16:11:05 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a boolean node in a CQL parse-tree ...
 * ###
 *
 * @version	$Id: CQLBooleanNode.java,v 1.2 2002-10-25 16:11:05 mike Exp $
 */
public abstract class CQLBooleanNode extends CQLNode {
    protected CQLNode left;
    protected CQLNode right;
}
