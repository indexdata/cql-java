// $Id: CQLProxNode.java,v 1.4 2002-11-20 01:15:15 mike Exp $

package org.z3950.zing.cql;
import java.util.Vector;


/**
 * Represents a proximity node in a CQL parse-tree.
 * The left- and right-hand-sides must be satisfied by parts of the
 * candidate records which are sufficiently close to each other, as
 * specified by a set of proximity parameters.
 *
 * @version	$Id: CQLProxNode.java,v 1.4 2002-11-20 01:15:15 mike Exp $
 */
public class CQLProxNode extends CQLBooleanNode {
    ModifierSet ms;

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
	ms = new ModifierSet("prox");
	this.left = left;
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
     * Adds a modifier of the specified <TT>type</TT> and
     * <TT>value</TT> to a proximity node.  Valid types are
     * <TT>relation</TT>, <TT>distance</TT>, <TT>unit</TT> and
     * <TT>ordering</TT>.
     * <P>
     * For information on the semantics of these paramaters, see
     * <A href="http://zing.z3950.org/cql/intro.html#3.1"
     *	>section 3.1 (Proximity)</A> of
     * <I>A Gentle Introduction to CQL</I></A>.
     */
    public void addModifier(String type, String value) {
	ms.addModifier(type, value);
    }

    /**
     * Returns an array of the modifiers associated with a proximity
     * node.
     * @return
     *	An array of modifiers, each represented by a two-element
     *	<TT>Vector</TT>, in which element 0 is the modifier type
     *	(e.g. <TT>distance</TT> or <TT>ordering</TT>) and element 1 is
     *	the associated value (e.g. <TT>3</TT> or <TT>unordered</TT>).
     */
    public Vector[] getModifiers() {
	return ms.getModifiers();
    }

    String op() {
	return ms.toCQL();
    }

    String opXQL(int level, Vector prefixes) {
	return ms.toXCQL(level, "boolean");
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
	String rel = ms.modifier("relation");
	int relCode = 0;
	if (rel.equals("<")) {
	    relCode = 1;
	} else if (rel.equals("<=")) {
	    relCode = 2;
	} else if (rel.equals("=")) {
	    relCode = 3;
	} else if (rel.equals(">=")) {
	    relCode = 4;
	} else if (rel.equals(">")) {
	    relCode = 5;
	} else if (rel.equals("<>")) {
	    relCode = 6;
	}

	String unit = ms.modifier("unit");
	int unitCode = 0;
	if (unit.equals("word")) {
	    unitCode = 2;
	} else if (unit.equals("sentence")) {
	    unitCode = 3;
	} else if (unit.equals("paragraph")) {
	    unitCode = 4;
	} else if (unit.equals("element")) {
	    unitCode = 8;
	}

	String res = "prox " +
	    "0 " +
	    ms.modifier("distance") + " " +
	    (ms.modifier("ordering").equals("ordered") ? 1 : 0) + " " +
	    relCode + " " +
	    "1 " +
	    unitCode;
	
	return res;
    }
}
