// $Id: CQLNode.java,v 1.9 2002-10-31 22:22:01 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a node in a CQL parse-tree.
 * ##
 *
 * @version	$Id: CQLNode.java,v 1.9 2002-10-31 22:22:01 mike Exp $
 */
public abstract class CQLNode {
    abstract String toXCQL(int level);
    abstract String toCQL();

    // Utility-function abbreviations for the use of subclasses
    protected static String indent(int level) { return Utils.indent(level); }
    protected static String xq(String str) { return Utils.xq(str); }

    // Test harness
    public static void main (String[] args) {
	CQLNode n1 = new CQLTermNode("dc.author",
				     new CQLRelation("="),
				     "kernighan");
	CQLNode n2 = new CQLTermNode("dc.title",
				     new CQLRelation("all"),
				     "elements style");
	CQLNode root = new CQLAndNode(n1, n2);
	System.out.println(root.toXCQL(0));
    }
}
