// $Id: Modifier.java,v 1.1 2007-06-27 17:01:23 mike Exp $

package org.z3950.zing.cql;
import java.util.Vector;
import java.lang.StringBuffer;

/**
 * Represents a single modifier, consisting of three elements: a type,
 * a comparision and a value.  For example, "distance", "<", "3".  The
 * type is mandatory; either the comparison and value must both occur,
 * or neither must.
 * <P>
 * This class is used only by ModifierSet.
 *
 * @version $Id: Modifier.java,v 1.1 2007-06-27 17:01:23 mike Exp $
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
    }

    /**
     * Creates a new Modifier with the specified value but no
     * comparison or value.
     */
    public Modifier(String value) {
	this.value = value;
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

    public String toXCQL(int level, String relationElement) {
	StringBuffer buf = new StringBuffer();

	buf.append(Utils.indent(level) + "<modifier>\n");
	buf.append(Utils.indent(level+1) +
		   "<type>" + Utils.xq(type) + "</type>\n");
	if (value != null) {
	    buf.append(Utils.indent(level+1) + "<" + relationElement + ">" +
		       Utils.xq(comparison) + "</" + relationElement + ">\n");
	    buf.append(Utils.indent(level+1) +
		       "<value>" + Utils.xq(value) + "</value>\n");
	}
	
	buf.append(Utils.indent(level) + "</modifier>\n");
	return buf.toString();
    }

    public String toCQL() {
	StringBuffer buf = new StringBuffer(type);

	if (value != null)
	    buf.append(" " + comparison + " " + value);

	return buf.toString();
    }
}
