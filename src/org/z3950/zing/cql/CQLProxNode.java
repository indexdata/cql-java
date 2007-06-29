// $Id: CQLProxNode.java,v 1.11 2007-06-29 11:56:06 mike Exp $

package org.z3950.zing.cql;
import java.util.Vector;


/**
 * Represents a proximity node in a CQL parse-tree.
 * The left- and right-hand-sides must be satisfied by parts of the
 * candidate records which are sufficiently close to each other, as
 * specified by a set of proximity parameters.
 *
 * @version	$Id: CQLProxNode.java,v 1.11 2007-06-29 11:56:06 mike Exp $
 */
public class CQLProxNode extends CQLBooleanNode {
    /**
     * Creates a new PROX node with the specified left- and right-hand
     * sides and modifiers.
     */
    public CQLProxNode(CQLNode left, CQLNode right, ModifierSet ms) {
	super(left, right, ms);
    }
    /**
     * Creates a new, <I>incomplete</I>, proximity node with the
     * specified left-hand side.  No right-hand side is specified at
     * this stage: that must be specified later, using the
     * <TT>addSecondSubterm()</TT> method.  (That may seem odd, but
     * it's just easier to write the parser that way.)
     * <P>
     * Proximity paramaters may be added at any time, before or after
     * the right-hand-side sub-tree is added.
     */
    public CQLProxNode(CQLNode left) {
	super(left, null, new ModifierSet("prox"));
	// this.right left unresolved for now ...
    }

    /**
     * Sets the right-hand side of the proximity node whose
     * left-hand-side was specified at creation time.
     */
    public void addSecondSubterm(CQLNode right) {
	this.right = right;
    }

    /**
     * Adds a modifier of the specified <TT>type</TT>,
     * <TT>comparison</TT> and <TT>value</TT> to a proximity node.
     */
    public void addModifier(String type, String comparison, String value) {
	ms.addModifier(type, comparison, value);
    }

    /**
     * Returns an array of the modifiers associated with a proximity
     * node.
     * @return
     *	An array of Modifier objects.
     */
    public Vector<Modifier> getModifiers() {
	return ms.getModifiers();
    }

    String op() {
	return ms.toCQL();
    }

    /*
     * proximity ::= exclusion distance ordered relation which-code unit-code.
     * exclusion ::= '1' | '0' | 'void'.
     * distance ::= integer.
     * ordered ::= '1' | '0'.
     * relation ::= integer.
     * which-code ::= 'known' | 'private' | integer.
     * unit-code ::= integer.
     */
    String opPQF() {
	int relCode = getRelCode();
	int unitCode = getProxUnitCode();

	String res = "prox " +
	    "0 " +
	    ms.modifier("distance") + " " +
	    (ms.modifier("ordering").equals("ordered") ? 1 : 0) + " " +
	    relCode + " " +
	    "1 " +
	    unitCode;

	return res;
    }

    private int getRelCode() {
	String rel = ms.modifier("relation");
	if (rel.equals("<")) {
	    return 1;
	} else if (rel.equals("<=")) {
	    return 2;
	} else if (rel.equals("=")) {
	    return 3;
	} else if (rel.equals(">=")) {
	    return 4;
	} else if (rel.equals(">")) {
	    return 5;
	} else if (rel.equals("<>")) {
	    return 6;
	}
	return 0;
    }

    private int getProxUnitCode() {
	String unit = ms.modifier("unit");
	if (unit.equals("word")) {
	    return 2;
	} else if (unit.equals("sentence")) {
	    return 3;
	} else if (unit.equals("paragraph")) {
	    return 4;
	} else if (unit.equals("element")) {
	    return 8;
	}
	return 0;
    }


    byte[] opType1() {
	byte[] op = new byte[100];
	int offset, value;
	offset = putTag(CONTEXT, 46, CONSTRUCTED, op, 0); // Operator
	op[offset++] = (byte)(0x80&0xff); // indefinite length

	offset = putTag(CONTEXT, 3, CONSTRUCTED, op, offset); // prox
	op[offset++] = (byte)(0x80&0xff); // indefinite length

	offset = putTag(CONTEXT, 1, PRIMITIVE, op, offset); // exclusion
	value = 0; // false
	offset = putLen(numLen(value), op, offset);
	offset = putNum(value, op, offset);

	offset = putTag(CONTEXT, 2, PRIMITIVE, op, offset); // distance
	value = Integer.parseInt(ms.modifier("distance"));
	offset = putLen(numLen(value), op, offset);
	offset = putNum(value, op, offset);

	offset = putTag(CONTEXT, 3, PRIMITIVE, op, offset); // ordered
	value = ms.modifier("ordering").equals("ordered") ? 1 : 0;
	offset = putLen(numLen(value), op, offset);
	offset = putNum(value, op, offset);

	offset = putTag(CONTEXT, 4, PRIMITIVE, op, offset); // relationType
	value = getRelCode();
	offset = putLen(numLen(value), op, offset);
	offset = putNum(value, op, offset);

	offset = putTag(CONTEXT, 5, CONSTRUCTED, op, offset); // proximityUnitCode
	op[offset++] = (byte)(0x80&0xff); // indefinite length
	offset = putTag(CONTEXT, 1, PRIMITIVE, op, offset); // known
	value = getProxUnitCode();
	offset = putLen(numLen(value), op, offset);
	offset = putNum(value, op, offset);
	op[offset++] = 0x00; // end of proximityUnitCode
	op[offset++] = 0x00;

	op[offset++] = 0x00; // end of prox
	op[offset++] = 0x00;
	op[offset++] = 0x00; // end of Operator
	op[offset++] = 0x00;

	byte[] o = new byte[offset];
	System.arraycopy(op, 0, o, 0, offset);
	return o;
    }
}
