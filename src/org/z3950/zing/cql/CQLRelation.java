// $Id: CQLRelation.java,v 1.5 2002-11-06 20:13:45 mike Exp $

package org.z3950.zing.cql;
import java.util.Vector;
import java.util.Properties;
import java.lang.StringBuffer;

/**
 * Represents a relation between a CQL qualifier and term.
 *
 * @version	$Id: CQLRelation.java,v 1.5 2002-11-06 20:13:45 mike Exp $
 */
public class CQLRelation extends CQLNode {
    ModifierSet ms;

    public CQLRelation(String base) {
	ms = new ModifierSet(base);
    }

    public String getBase() {
	return ms.getBase();
    }

    public void addModifier(String modifier) {
	ms.addModifier(null, modifier);
    }

    public String[] getModifiers() {
	Vector[] v = ms.getModifiers();
	int n = v.length;
	String[] s = new String[n];
	for (int i = 0; i < n; i++) {
	    s[i] = (String) v[i].get(1);
	}
	return s;
    }

    public String toXCQL(int level) {
	return ms.toXCQL(level, "relation");
    }

    public String toCQL() {
	return ms.toCQL();
    }

    public String toPQF(Properties config) throws PQFTranslationException {
	throw new Error("CQLRelation.toPQF() can never be called");
    }
}
