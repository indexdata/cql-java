// $Id: ModifierSet.java,v 1.13 2007-07-03 13:30:18 mike Exp $

package org.z3950.zing.cql;
import java.util.ArrayList;
import java.util.List;

import static org.z3950.zing.cql.Utils.*;

/**
 * Represents a base String and a set of Modifiers.
 * <P>
 * This class is used as a workhorse delegate by both CQLRelation and
 * CQLProxNode - two functionally very separate classes that happen to
 * require similar data structures and functionality.
 * <P>
 * A ModifierSet consists of a ``base'' string together with a set of
 * zero or more <I>type</I> <I>comparison</I> <I>value</I> pairs,
 * where type, comparison and value are all strings.
 *
 * @version $Id: ModifierSet.java,v 1.13 2007-07-03 13:30:18 mike Exp $
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
     * Adds a modifier of the specified <TT>type</TT>,
     * <TT>comparison</TT> and <TT>value</TT> to a ModifierSet.
     */
    public void addModifier(String type, String comparison, String value) {
	Modifier modifier = new Modifier(type, comparison, value);
	modifiers.add(modifier);
    }

    /**
     * Adds a modifier of the specified <TT>type</TT>, but with no
     * <TT>comparison</TT> and <TT>value</TT>, to a ModifierSet.
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
     * @return
     *	An array of Modifiers.
     */
    public List<Modifier> getModifiers() {
	return modifiers;
    }

    public String toXCQL(int level, String topLevelElement) {
	return underlyingToXCQL(level, topLevelElement, "value");
    }

    public String sortKeyToXCQL(int level) {
	return underlyingToXCQL(level, "key", "index");
    }

    private String underlyingToXCQL(int level, String topLevelElement,
				    String valueElement) {
	StringBuilder buf = new StringBuilder();
	buf.append(indent(level)).append("<").append(topLevelElement).
            append(">\n").append(indent(level + 1)).append("<").
            append(valueElement).append(">").append(xq(base)).append("</").
            append(valueElement).append(">\n");
	if (modifiers.size() > 0) {
	    buf.append(indent(level + 1)).append("<modifiers>\n");
	    for (int i = 0; i < modifiers.size(); i++) {
		buf.append(modifiers.get(i).toXCQL(level+2, "comparison"));
	    }
	    buf.append(indent(level + 1)).append("</modifiers>\n");
	}
	buf.append(indent(level)).append("</").append(topLevelElement).
            append(">\n");
	return buf.toString();
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
	    res.addModifier(args[i], args[i+1], args[i+2]);
	}

	System.out.println(res.toCQL());
    }
}
