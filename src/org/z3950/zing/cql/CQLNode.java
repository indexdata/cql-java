// $Id: CQLNode.java,v 1.10 2002-11-05 17:21:30 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a node in a CQL parse-tree.
 * ##
 *
 * @version	$Id: CQLNode.java,v 1.10 2002-11-05 17:21:30 mike Exp $
 */
public abstract class CQLNode {
    abstract public String toXCQL(int level);
    abstract public String toCQL();

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
