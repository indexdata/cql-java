// $Id: CQLRelation.java,v 1.4 2002-11-06 00:14:32 mike Exp $

package org.z3950.zing.cql;
import java.util.Vector;
import java.util.Properties;
import java.lang.StringBuffer;

/**
 * Represents a relation between a CQL qualifier and term.
 * ##
 *
 * @version	$Id: CQLRelation.java,v 1.4 2002-11-06 00:14:32 mike Exp $
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

    // ### should have a public method to retrieve all modifiers

    public String toXCQL(int level) {
	return ms.toXCQL(level, "relation");
    }

    public String toCQL() {
	return ms.toCQL();
    }

    public String toPQF(Properties config)
	throws UnknownQualifierException, UnknownRelationException {
	throw Error("CQLRelation.toPQF() can never be called");
    }
}
