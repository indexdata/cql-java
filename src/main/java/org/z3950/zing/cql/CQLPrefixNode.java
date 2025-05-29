package org.z3950.zing.cql;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Represents a prefix node in a CQL parse-tree.
 *
 */
public class CQLPrefixNode extends CQLNode {

    private CQLPrefix prefix;

    /**
     * The prefix definition that governs the subtree.
     */
    public CQLPrefix getPrefix() {
        return prefix;
    }

    private CQLNode subtree;

    /**
     * The root of a parse-tree representing the part of the query
     * that is governed by this prefix definition.
     */
    public CQLNode getSubtree() {
      return subtree;
    }

    /**
     * Creates a new CQLPrefixNode inducing a mapping from the
     * specified index-set name to the specified identifier across
     * the specified subtree.
     */
    public CQLPrefixNode(String name, String identifier, CQLNode subtree) {
	this.prefix = new CQLPrefix(name, identifier);
	this.subtree = subtree;
    }

    @Override
    public void traverse(CQLNodeVisitor visitor) {
      visitor.onPrefixNode(this);
      subtree.traverse(visitor);
    }

    @Override
    void toXCQLInternal(XCQLBuilder b, int level, List<CQLPrefix> prefixes,
			 List<ModifierSet> sortkeys) {
	List<CQLPrefix> tmp = (prefixes == null ?
				 new ArrayList<CQLPrefix>() :
				 new ArrayList<CQLPrefix>(prefixes));
	tmp.add(prefix);
	subtree.toXCQLInternal(b, level, tmp, sortkeys);
    }

    @Override
    public String toCQL() {
	// ### We don't always need parens around the subtree
	if (prefix.name == null) {
	    return ">\"" + prefix.identifier + "\" " +
		"(" + subtree.toCQL() + ")";
	} else {
	    return ">" + prefix.name + "=\"" + prefix.identifier + "\" " +
		"(" + subtree.toCQL() + ")";
	}
    }

    @Override
    public String toPQF(Properties config) throws PQFTranslationException {
	// Prefixes and their identifiers don't actually play any role
	// in PQF translation, since the meanings of the indexes,
	// including their prefixes if any, are instead wired into
	// `config'.
	return subtree.toPQF(config);
    }

    @Override
    public byte[] toType1BER(Properties config) throws PQFTranslationException {
	// See comment on toPQF()
	return subtree.toType1BER(config);
    }
}
