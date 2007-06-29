// $Id: CQLNotNode.java,v 1.8 2007-06-29 11:54:56 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a NOT node in a CQL parse-tree.
 *
 * @version	$Id: CQLNotNode.java,v 1.8 2007-06-29 11:54:56 mike Exp $
 */
public class CQLNotNode extends CQLBooleanNode {
    /**
     * Creates a new NOT node with the specified left- and right-hand
     * sides and modifiers.
     */
    public CQLNotNode(CQLNode left, CQLNode right, ModifierSet ms) {
	super(left, right, ms);
    }

    String op() {
	return "not";
    }

    byte[] opType1() {
	byte[] op = new byte[5];
	putTag(CONTEXT, 46, CONSTRUCTED, op, 0); // Operator
	putLen(2, op, 2);
	putTag(CONTEXT, 2, PRIMITIVE, op, 3); // and-not
	putLen(0, op, 4);
	return op;
    }
}
