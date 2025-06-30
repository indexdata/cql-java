package org.z3950.zing.cql;

import java.util.List;
import java.util.Properties;

/**
 * Represents a relation between a CQL index and term.
 *
 */
public class CQLRelation extends CQLNode {
    ModifierSet ms;

    /**
     * Creates a new CQLRelation with the specified base relation.
     * Typical base relations include the usual six ordering relations
     * (<code>&lt;=</code>, <code>&gt</code>, <I>etc.</I>), the text
     * relations <code>any</code>, <code>all</code> and <code>exact</code>, the
     * old server-choice relation <code>scr</code> and profiled relations of
     * the form <code><I>prefix</I>.<I>name</I></code>.
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
    public void traverse(CQLNodeVisitor visitor) {
      visitor.onRelation(this);
    } 
    
    @Override
    void toXCQLInternal(XCQLBuilder b, int level, List<CQLPrefix> prefixes,
      List<ModifierSet> sortkeys) {
	if (sortkeys != null)
	    throw new Error("CQLRelation.toXCQL() called with sortkeys");
	ms.toXCQLInternal(b, level, "relation", "value");
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
