// $Id: CQLProxNode.java,v 1.1 2002-10-31 22:22:01 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a proximity node in a CQL parse-tree.
 * ##
 *
 * @version	$Id: CQLProxNode.java,v 1.1 2002-10-31 22:22:01 mike Exp $
 */
public class CQLProxNode extends CQLBooleanNode {
    ModifierSet ms;

    public CQLProxNode(CQLNode left) {
	ms = new ModifierSet("prox");
	this.left = left;
	// this.right left unresolved for now ...
    }

    // ... delayed "second half" of the constructor
    public void addSecondSubterm(CQLNode right) {
	this.right = right;
    }

    String op() {
	return ms.toCQL();
    }

    public void addModifier(String type, String value) {
	ms.addModifier(type, value);
    }

    String booleanXQL(int level) {
	return ms.toXCQL(level, "boolean");
    }
}
