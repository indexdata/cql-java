// $Id: CQLRelation.java,v 1.8 2002-12-05 17:14:52 mike Exp $

package org.z3950.zing.cql;
import java.util.Vector;
import java.util.Properties;
import java.lang.StringBuffer;

/**
 * Represents a relation between a CQL qualifier and term.
 *
 * @version	$Id: CQLRelation.java,v 1.8 2002-12-05 17:14:52 mike Exp $
 */
public class CQLRelation extends CQLNode {
    ModifierSet ms;

    /**
     * Creates a new CQLRelation with the specified base relation.
     * Typical base relations include the usual six ordering relations
     * (<TT>&lt;=</TT>, <TT>&gt</TT>, <I>etc.</I>), the text
     * relations <TT>any</TT>, <TT>all</TT> and <TT>exact</TT> and the
     * server-choice relation <TT>scr</TT>.
     */
    public CQLRelation(String base) {
	ms = new ModifierSet(base);
    }

    /**
     * Returns the base relation with which the CQLRelation was
     * originally created.
     */
    public String getBase() {
	return ms.getBase();
    }

    /**
     * Adds a new relation modifier to the specified CQLRelation.
     * Typical relation modifiers include <TT>relevant</TT>,
     * <TT>fuzzy</TT>, <TT>stem</TT> and <TT>phonetic</TT>.  On the
     * whole, these modifiers have a meaningful interpretation only
     * for the text relations.
     */
    public void addModifier(String modifier) {
	ms.addModifier(null, modifier);
    }

    /**
     * Returns an array of the modifiers associated with a CQLRelation.
     * @return
     *	An array of zero or more <TT>String</TT>s, each representing a
     *	modifier associated with the specified CQLRelation.
     */
    public String[] getModifiers() {
	Vector[] v = ms.getModifiers();
	int n = v.length;
	String[] s = new String[n];
	for (int i = 0; i < n; i++) {
	    s[i] = (String) v[i].get(1);
	}
	return s;
    }

    public String toXCQL(int level, Vector prefixes) {
	return ms.toXCQL(level, "relation");
    }

    public String toCQL() {
	return ms.toCQL();
    }

    public String toPQF(Properties config) throws PQFTranslationException {
	throw new Error("CQLRelation.toPQF() can never be called");
    }

    // ### THIS IS NOT THE REAL CODE.  I'm waiting for Ralph to send it.
    public byte[] toType1(Properties config) {
	byte[] op = new byte[0];
	return op;
    }
}
