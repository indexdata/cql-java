// $Id: CQLOrNode.java,v 1.9 2007-06-29 12:48:21 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents an OR node in a CQL parse-tree.
 *
 * @version	$Id: CQLOrNode.java,v 1.9 2007-06-29 12:48:21 mike Exp $
 */
public class CQLOrNode extends CQLBooleanNode {
    /**
     * Creates a new OR node with the specified left- and right-hand
     * sides and modifiers.
     */
    public CQLOrNode(CQLNode left, CQLNode right, ModifierSet ms) {
	super(left, right, ms);
    }

    byte[] opType1() {
	byte[] op = new byte[5];
	putTag(CONTEXT, 46, CONSTRUCTED, op, 0); // Operator
	putLen(2, op, 2);
	putTag(CONTEXT, 1, PRIMITIVE, op, 3); // or
	putLen(0, op, 4);
	return op;
    }
}
