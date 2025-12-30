package org.z3950.zing.cql;

/**
 * Represents a single modifier, consisting of three elements: a type,
 * a comparison and a value. For example, "distance", "&lt;", "3". The
 * type is mandatory; either the comparison and value must both occur,
 * or neither must.
 * <P>
 * This class is used only by ModifierSet.
 *
 */
public class Modifier {
    String type;
    String comparison;
    String value;

    /**
     * Creates a new Modifier with the specified type, comparison
     * and value.
     */
    public Modifier(String type, String comparison, String value) {
        this.type = type;
        this.comparison = comparison;
        this.value = value;
        // System.err.println("Made new modifier with " + "type='" + type + "', " +
        // "comparison='" + comparison + "', " + "value='" + value + "',\n");
    }

    /**
     * Creates a new Modifier with the specified type but no
     * comparison or value.
     */
    public Modifier(String type) {
        this.type = type;
        // System.err.println("Made new modifier of type '" + type + "'\n");
    }

    /**
     * Returns the type with which the Modifier was created.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the comparison with which the Modifier was created.
     */
    public String getComparison() {
        return comparison;
    }

    /**
     * Returns the value with which the Modifier was created.
     */
    public String getValue() {
        return value;
    }

    void toXCQLInternal(XCQLBuilder b, int level, String relationElement) {
        b.indent(level).append("<modifier>\n");
        b.indent(level + 1).append("<type>");
        b.xq(type).append("</type>\n");
        if (value != null) {
            b.indent(level + 1).append("<").append(relationElement).append(">");
            b.xq(comparison).append("</").append(relationElement).append(">\n");
            b.indent(level + 1).append("<value>");
            b.xq(value).append("</value>\n");
        }
        b.indent(level).append("</modifier>\n");
    }

    public String toCQL() {
        StringBuilder buf = new StringBuilder(type);
        if (value != null)
            buf.append(" ").append(comparison).append(" ").append(value);
        return buf.toString();
    }
}
