// $Id: CQLBooleanNode.java,v 1.6 2002-10-31 22:22:01 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a boolean node in a CQL parse-tree.
 * ##
 *
 * @version	$Id: CQLBooleanNode.java,v 1.6 2002-10-31 22:22:01 mike Exp $
 */
public abstract class CQLBooleanNode extends CQLNode {
    protected CQLNode left;
    protected CQLNode right;

    abstract String op();

    String toXCQL(int level) {
	return (indent(level) + "<triple>\n" +
		booleanXQL(level+1) +
		left.toXCQL(level+1) +
		right.toXCQL(level+1) +
		indent(level) + "</triple>\n");
    }

    String booleanXQL(int level) {
	return(indent(level) + "<boolean>\n" +
	       indent(level+1) + "<value>" + op() + "</value>\n" +
	       indent(level) + "</boolean>\n");
    }

    String toCQL() {
	// ### We don't always need parens around the operands
	return "(" + left.toCQL() + ") " + op() + " (" + right.toCQL() + ")";
    }
}
