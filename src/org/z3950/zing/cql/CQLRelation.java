// $Id: CQLRelation.java,v 1.2 2002-10-31 22:22:01 mike Exp $

package org.z3950.zing.cql;
import java.util.Vector;
import java.lang.StringBuffer;

/**
 * Represents a relation between a CQL qualifier and term.
 * ##
 *
 * @version	$Id: CQLRelation.java,v 1.2 2002-10-31 22:22:01 mike Exp $
 */
public class CQLRelation extends CQLNode {
    ModifierSet ms;

    public CQLRelation(String base) {
	ms = new ModifierSet(base);
    }

    public void addModifier(String modifier) {
	ms.addModifier(null, modifier);
    }

    public String toXCQL(int level) {
	return ms.toXCQL(level, "relation");
    }

    public String toCQL() {
	return ms.toCQL();
    }
}
