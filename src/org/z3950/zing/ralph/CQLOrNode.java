// $Id: CQLOrNode.java,v 1.1 2002-12-04 16:54:01 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents an OR node in a CQL parse-tree.
 *
 * @version	$Id: CQLOrNode.java,v 1.1 2002-12-04 16:54:01 mike Exp $
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
    
    byte[] opType1() {
        byte[] op=new byte[5];
        putTag(CONTEXT, 46, CONSTRUCTED, op, 0); // Operator
        putLen(2, op, 2);
        putTag(CONTEXT, 1, PRIMITIVE, op, 3); // or
        putLen(0, op, 4);
        return op;
    }
    
}
