// $Id: CQLRelation.java,v 1.19 2007-07-03 13:40:58 mike Exp $

package org.z3950.zing.cql;
import java.util.List;
import java.util.Properties;

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
     * Returns an array of the modifiers associated with a CQLRelation.
     * @return
     *	An array of Modifier objects.
     */
    public List<Modifier> getModifiers() {
	return ms.getModifiers();
    }

    @Override
    public String toXCQL(int level, List<CQLPrefix> prefixes,
      List<ModifierSet> sortkeys) {
	if (sortkeys != null)
	    throw new Error("CQLRelation.toXCQL() called with sortkeys");

	return ms.toXCQL(level, "relation");
    }

    @Override
    public String toCQL() {
	return ms.toCQL();
    }

    @Override
    public String toPQF(Properties config) throws PQFTranslationException {
	throw new Error("CQLRelation.toPQF() can never be called");
    }

    @Override
    public byte[] toType1BER(Properties config) {
	throw new Error("CQLRelation.toType1BER() can never be called");
    }
}
