// $Id: CQLNode.java,v 1.5 2002-10-27 00:46:25 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a node in a CQL parse-tree ...
 * ###
 *
 * @version	$Id: CQLNode.java,v 1.5 2002-10-27 00:46:25 mike Exp $
 */
public abstract class CQLNode {
    abstract String toXCQL(int level);
    abstract String toCQL();

    protected String indent(int level) {
	String x = "";
	while (level-- > 0) {
	    x += "  ";
	}
	return x;
    }

    protected String xq(String str) {
	// XML Quote
	// ###	s/&/&amp;/g;
	//	s/</&lt;/g;
	//	s/>/&gt;/g;
	return str;
    }

    // Test harness
    public static void main (String[] args) {
	CQLNode n1 = new CQLTermNode("dc.author", "=", "kernighan");
	CQLNode n2 = new CQLTermNode("dc.title", "all", "elements style");
	CQLNode root = new CQLAndNode(n1, n2);
	System.out.println(root.toXCQL(0));
    }
}
