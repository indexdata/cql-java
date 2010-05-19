// $Id: CQLRelation.java,v 1.19 2007-07-03 13:40:58 mike Exp $

package org.z3950.zing.cql;
import java.util.Vector;
import java.util.Properties;
import java.lang.StringBuffer;

/**
 * Represents a relation between a CQL index and term.
 *
 * @version	$Id: CQLRelation.java,v 1.19 2007-07-03 13:40:58 mike Exp $
 */
public class CQLRelation extends CQLNode {
    ModifierSet ms;

    /**
     * Creates a new CQLRelation with the specified base relation.
     * Typical base relations include the usual six ordering relations
     * (<TT>&lt;=</TT>, <TT>&gt</TT>, <I>etc.</I>), the text
     * relations <TT>any</TT>, <TT>all</TT> and <TT>exact</TT>, the
     * old server-choice relation <TT>scr</TT> and profiled relations of
     * the form <TT><I>prefix</I>.<I>name</I></TT>.
     */
    // ### Seems wrong: a modifier set should not have a base, a
    // relation should
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
     * Sets the modifiers of the specified CQLRelation.
     * Typical relation modifiers include <TT>relevant</TT>,
     * <TT>fuzzy</TT>, <TT>stem</TT> and <TT>phonetic</TT>.  On the
     * whole, these modifiers have a meaningful interpretation only
     * for the text relations.
     */
    public void setModifiers(ModifierSet ms) {
	this.ms = ms;
    }

    /**
     * Returns an array of the modifiers associated with a CQLRelation.
     * @return
     *	An array of Modifier objects.
     */
    public Vector<Modifier> getModifiers() {
	return ms.getModifiers();
    }

    public String toXCQL(int level, Vector prefixes, Vector sortkeys) {
	if (sortkeys != null)
	    throw new Error("CQLRelation.toXCQL() called with sortkeys");

	return ms.toXCQL(level, "relation");
    }

    public String toCQL() {
	return ms.toCQL();
    }

    public String toPQF(Properties config) throws PQFTranslationException {
	throw new Error("CQLRelation.toPQF() can never be called");
    }

    public byte[] toType1BER(Properties config) {
	throw new Error("CQLRelation.toType1BER() can never be called");
    }
}