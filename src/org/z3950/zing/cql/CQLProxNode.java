// $Id: CQLProxNode.java,v 1.2 2002-11-06 00:05:58 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a proximity node in a CQL parse-tree.
 * The left- and right-hand-sides must be satisfied by parts of the
 * candidate records which are sufficiently close to each other, as
 * specified by a set of proximity parameters.
 *
 * @version	$Id: CQLProxNode.java,v 1.2 2002-11-06 00:05:58 mike Exp $
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

    String op() {
	return ms.toCQL();
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

    // ### should have a public method to retrieve all modifiers

    String booleanXQL(int level) {
	return ms.toXCQL(level, "boolean");
    }
}
