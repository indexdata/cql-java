// $Id: CQLBooleanNode.java,v 1.1 2002-10-25 16:04:44 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a boolean node in a CQL parse-tree ...
 * ###
 *
 * @version	$Id: CQLBooleanNode.java,v 1.1 2002-10-25 16:04:44 mike Exp $
 */
public abstract class CQLBooleanNode {
    protected CQLNode left;
    protected CQLNode right;
}
