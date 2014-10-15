
package org.z3950.zing.cql;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Represents a sort node in a CQL parse-tree.
 *
 */
public class CQLSortNode extends CQLNode {
    /**
     * The root of a subtree representing the query whose result is to
     * be sorted.
     */ 
    private CQLNode subtree;

    /**
     * The set of sort keys by which results are to be sorted,
     * each expressed as an index together with zero or more
     * modifiers.
     */ 
    List<ModifierSet> keys;

    public CQLNode getSubtree() {
        return subtree;
    }

    public CQLSortNode(CQLNode subtree) {
	this.subtree = subtree;
	keys = new ArrayList<ModifierSet>();
    }

    public void addSortIndex(ModifierSet key) {
	keys.add(key);
    }

    public List<ModifierSet> getSortIndexes() {
    	return keys;
    }

    @Override
    public void traverse(CQLNodeVisitor visitor) {
      visitor.onSortNode(this);
      subtree.traverse(visitor);
    }

    @Override
    void toXCQLInternal(XCQLBuilder b, int level, List<CQLPrefix> prefixes,
			 List<ModifierSet> sortkeys) {
	if (sortkeys != null)
	    throw new Error("CQLSortNode.toXCQL() called with sortkeys");
	subtree.toXCQLInternal(b, level, prefixes, keys);
    }

    @Override
    public String toCQL() {
	StringBuilder buf = new StringBuilder(subtree.toCQL());

	if (keys != null) {
	    buf.append(" sortby");
	    for (int i = 0; i < keys.size(); i++) {
		ModifierSet key = keys.get(i);
		buf.append(" ").append(key.toCQL());
	    }
	}

	return buf.toString();
    }

    @Override
    public String toPQF(Properties config) throws PQFTranslationException {
	return "@attr 1=oops \"###\"";
    }

    @Override
    public byte[] toType1BER(Properties config)
	throws PQFTranslationException {
	// There is no way to represent sorting in a standard Z39.50
	// Type-1 query, so the best we can do is return the
	// underlying query and ignore the sort-specification.
        return subtree.toType1BER(config);
    }
}
