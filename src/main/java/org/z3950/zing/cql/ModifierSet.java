package org.z3950.zing.cql;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a base String and a set of Modifiers.
 * <P>
 * This class is used as a workhorse delegate by both CQLRelation and
 * CQLProxNode - two functionally very separate classes that happen to
 * require similar data structures and functionality.
 * </P>
 * <P>
 * A ModifierSet consists of a ``base'' string together with a set of
 * zero or more <I>type</I> <I>comparison</I> <I>value</I> pairs,
 * where type, comparison and value are all strings.
 * </P>
 *
 */
public class ModifierSet {
    private String base;
    private List<Modifier> modifiers;

    /**
     * Creates a new ModifierSet with the specified base.
     */
    public ModifierSet(String base) {
        this.base = base;
        modifiers = new ArrayList<Modifier>();
    }

    /**
     * Returns the base string with which the ModifierSet was created.
     */
    public String getBase() {
        return base;
    }

    /**
     * Adds a modifier of the specified <code>type</code>,
     * <code>comparison</code> and <code>value</code> to a ModifierSet.
     */
    public void addModifier(String type, String comparison, String value) {
        Modifier modifier = new Modifier(type, comparison, value);
        modifiers.add(modifier);
    }

    /**
     * Adds a modifier of the specified <code>type</code>, but with no
     * <code>comparison</code> and <code>value</code>, to a ModifierSet.
     */
    public void addModifier(String type) {
        Modifier modifier = new Modifier(type);
        modifiers.add(modifier);
    }

    /**
     * Returns the value of the modifier in the specified ModifierSet
     * that corresponds to the specified type.
     */
    public String modifier(String type) {
        int n = modifiers.size();
        for (int i = 0; i < n; i++) {
            Modifier mod = modifiers.get(i);
            if (mod.type.equals(type))
                return mod.value;
        }
        return null;
    }

    /**
     * Returns an array of the modifiers in a ModifierSet.
     *
     * @return
     *         An array of Modifiers.
     */
    public List<Modifier> getModifiers() {
        return modifiers;
    }

    void toXCQLInternal(XCQLBuilder b, int level,
            String topLevelElement, String valueElement) {
        b.indent(level).append("<").append(topLevelElement).append(">\n").indent(level + 1).append("<")
                .append(valueElement).append(">").xq(base).append("</").append(valueElement).append(">\n");
        if (modifiers.size() > 0) {
            b.indent(level + 1).append("<modifiers>\n");
            for (int i = 0; i < modifiers.size(); i++) {
                modifiers.get(i).toXCQLInternal(b, level + 2, "comparison");
            }
            b.indent(level + 1).append("</modifiers>\n");
        }
        b.indent(level).append("</").append(topLevelElement).append(">\n");
    }

    public String toCQL() {
        StringBuilder buf = new StringBuilder(base);
        for (int i = 0; i < modifiers.size(); i++) {
            buf.append("/").append(modifiers.get(i).toCQL());
        }

        return buf.toString();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: ModifierSet <base> [<type> <comparison> <name>]...");
            System.exit(1);
        }

        ModifierSet res = new ModifierSet(args[0]);
        for (int i = 1; i < args.length; i += 3) {
            res.addModifier(args[i], args[i + 1], args[i + 2]);
        }

        System.out.println(res.toCQL());
    }
}
