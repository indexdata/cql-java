// $Id: CQLRelation.java,v 1.1 2002-10-30 09:19:26 mike Exp $

package org.z3950.zing.cql;
import java.util.Vector;
import java.lang.StringBuffer;

/**
 * Represents a relation between a CQL qualifier and term.
 * ###
 *
 * @version	$Id: CQLRelation.java,v 1.1 2002-10-30 09:19:26 mike Exp $
 */
public class CQLRelation extends CQLNode {
    String base;
    Vector modifiers;

    public CQLRelation(String base) {
	this.base = base;
	modifiers = new Vector();
    }

    public void addModifier(String modifier) {
	modifiers.add(modifier);
    }

    public String[] getModifiers() {
	int n = modifiers.size();
	String[] res = new String[n];
	for (int i = 0; i < n; i++) {
	    res[i] = (String) modifiers.get(i);
	}

	return res;
    }

    public String toXCQL(int level) {
	StringBuffer buf = new StringBuffer();
	buf.append (indent(level) + "<relation>\n" +
		    indent(level+1) + "<value>" + xq(base) + "</value>\n");
	String[] mods = getModifiers();
	if (mods.length > 0) {
	    buf.append(indent(level+1) + "<modifiers>\n");
	    for (int i = 0; i < mods.length; i++)
		buf.append(indent(level+2)).
		    append("<modifier><value>"). append(mods[i]).
		    append("</value></modifier>\n");
	    buf.append(indent(level+1) + "</modifiers>\n");
	}
	buf.append(indent(level) + "</relation>\n");
	return buf.toString();
    }

    public String toCQL() {
	StringBuffer buf = new StringBuffer(base);
	String[] mods = getModifiers();
	for (int i = 0; i < mods.length; i++) {
	    buf.append("/").append(mods[i]);
	}

	return buf.toString();
    }

    public static void main(String[] args) {
	if (args.length < 1) {
	    System.err.println("Usage: CQLRelation <base> <modifier>...");
	    System.exit(1);
	}

	CQLRelation res = new CQLRelation(args[0]);
	for (int i = 1; i < args.length; i++) {
	    res.addModifier(args[i]);
	}

	System.out.println(res.toCQL());
    }
}
