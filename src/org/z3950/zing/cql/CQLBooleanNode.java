// $Id: CQLBooleanNode.java,v 1.13 2002-12-11 17:14:20 mike Exp $

package org.z3950.zing.cql;
import java.util.Properties;
import java.util.Vector;


/**
 * Represents a boolean node in a CQL parse-tree.
 *
 * @version	$Id: CQLBooleanNode.java,v 1.13 2002-12-11 17:14:20 mike Exp $
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

    public String toXCQL(int level, Vector prefixes) {
	return (indent(level) + "<triple>\n" +
		renderPrefixes(level+1, prefixes) +
		opXCQL(level+1) +
		indent(level+1) + "<leftOperand>\n" +
		left.toXCQL(level+2, new Vector()) +
		indent(level+1) + "</leftOperand>\n" +
		indent(level+1) + "<rightOperand>\n" +
		right.toXCQL(level+2, new Vector()) +
		indent(level+1) + "</rightOperand>\n" +
		indent(level) + "</triple>\n");
    }

    // Represents the boolean operation itself: overridden for CQLProxNode
    String opXCQL(int level) {
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

    public byte[] toType1BER(Properties config) throws PQFTranslationException {
        System.out.println("in CQLBooleanNode.toType1BER(): PQF=" +
			   toPQF(config));
        byte[] rpn1 = left.toType1BER(config);
        byte[] rpn2 = right.toType1BER(config);
        byte[] op = opType1();
        byte[] rpnStructure = new byte[rpn1.length+rpn2.length+op.length+4];
        
	// rpnRpnOp
        int offset = putTag(CONTEXT, 1, CONSTRUCTED, rpnStructure, 0);

        rpnStructure[offset++] = (byte)(0x80&0xff); // indefinite length
        System.arraycopy(rpn1, 0, rpnStructure, offset, rpn1.length);
        offset += rpn1.length;
        System.arraycopy(rpn2, 0, rpnStructure, offset, rpn2.length);
        offset += rpn2.length;
        System.arraycopy(op, 0, rpnStructure, offset, op.length);
        offset += op.length;
        rpnStructure[offset++] = 0x00; // end rpnRpnOp
        rpnStructure[offset++] = 0x00;
        return rpnStructure;
    }

    abstract byte[] opType1();
}
