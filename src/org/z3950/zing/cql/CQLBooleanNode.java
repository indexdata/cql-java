// $Id: CQLBooleanNode.java,v 1.8 2002-11-06 20:13:45 mike Exp $

package org.z3950.zing.cql;
import java.util.Properties;


/**
 * Represents a boolean node in a CQL parse-tree.
 *
 * @version	$Id: CQLBooleanNode.java,v 1.8 2002-11-06 20:13:45 mike Exp $
 */
public abstract class CQLBooleanNode extends CQLNode {
    CQLBooleanNode() {}		// prevent javadoc from documenting this

    /**
     * The root of a parse-tree representing the left-hand side.
     */ 
    public CQLNode left;

    /**
     * The root of a parse-tree representing the right-hand side.
     */ 
    public CQLNode right;

    public String toXCQL(int level) {
	return (indent(level) + "<triple>\n" +
		opXQL(level+1) +
		left.toXCQL(level+1) +
		right.toXCQL(level+1) +
		indent(level) + "</triple>\n");
    }

    // Represents the boolean operation itself: overridden for CQLProxNode
    String opXQL(int level) {
	return(indent(level) + "<boolean>\n" +
	       indent(level+1) + "<value>" + op() + "</value>\n" +
	       indent(level) + "</boolean>\n");
    }

    public String toCQL() {
	// ### We don't always need parens around the operands
	return "(" + left.toCQL() + ") " + op() + " (" + right.toCQL() + ")";
    }

    public String toPQF(Properties config) throws PQFTranslationException {
	return ("@" + opPQF() +
		" " + left.toPQF(config) +
		" " + right.toPQF(config));
    }

    // represents the operation for PQF: overridden for CQLProxNode
    String opPQF() { return op(); }

    abstract String op();
}
