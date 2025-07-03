package org.z3950.zing.cql;

import java.util.List;
import java.util.Properties;

/**
 * Represents a boolean node in a CQL parse-tree.
 *
 */
public abstract class CQLBooleanNode extends CQLNode {
    private final CQLBoolean operator;

    public CQLBoolean getOperator() {
        return operator;
    }

    private CQLNode left;

    /**
     * The root of a parse-tree representing the left-hand side.
     * @return the left operand of this boolean node
     */
    public CQLNode getLeftOperand() {
        return left;
    }

    private CQLNode right;

    /**
     * The root of a parse-tree representing the right-hand side.
     * @return the right operand of this boolean node
     */
    public CQLNode getRightOperand() {
        return right;
    }

    ModifierSet ms;

    /**
     * The set of modifiers that are applied to this boolean.
     * @return a list of Modifier objects, which may be empty.
     */
    public List<Modifier> getModifiers() {
        return ms.getModifiers();
    }

    protected CQLBooleanNode(CQLNode left, CQLNode right, ModifierSet ms, CQLBoolean operator) {
        this.left = left;
        this.right = right;
        this.ms = ms;
        this.operator = operator;
    }

    @Override
    public void traverse(CQLNodeVisitor visitor) {
        visitor.onBooleanNodeStart(this);
        left.traverse(visitor);
        visitor.onBooleanNodeOp(this);
        right.traverse(visitor);
        visitor.onBooleanNodeEnd(this);
    }

    @Override
    void toXCQLInternal(XCQLBuilder b, int level,
                        List<CQLPrefix> prefixes, List<ModifierSet> sortkeys) {
        b.indent(level).append("<triple>\n");
        renderPrefixes(b, level + 1, prefixes);
        ms.toXCQLInternal(b, level + 1, "boolean", "value");
        b.indent(level + 1).append("<leftOperand>\n");
        left.toXCQLInternal(b, level + 2);
        b.indent(level + 1).append("</leftOperand>\n");
        b.indent(level + 1).append("<rightOperand>\n");
        right.toXCQLInternal(b, level + 2);
        b.indent(level + 1).append("</rightOperand>\n");
        renderSortKeys(b, level + 1, sortkeys);
        b.indent(level).append("</triple>\n");
    }

    @Override
    public String toCQL() {
        // ### We don't always need parens around the operands
        return ("(" + left.toCQL() + ")" +
                " " + ms.toCQL() + " " +
                "(" + right.toCQL() + ")");
    }

    @Override
    public String toPQF(Properties config) throws PQFTranslationException {
        return ("@" + opPQF() +
                " " + left.toPQF(config) +
                " " + right.toPQF(config));
    }

    // represents the operation for PQF: overridden for CQLProxNode
    String opPQF() { return ms.getBase(); }

    @Override
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
