// $Id: CQLNode.java,v 1.6 2002-10-29 10:15:58 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a node in a CQL parse-tree ...
 * ###
 *
 * @version	$Id: CQLNode.java,v 1.6 2002-10-29 10:15:58 mike Exp $
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

    // XML Quote --
    //	s/&/&amp;/g;
    //	s/</&lt;/g;
    //	s/>/&gt;/g;
    // This is hideously inefficient, but I just don't see a better
    // way using the standard JAVA library.
    //
    protected String xq(String str) {
	str = replace(str, "&", "&amp;");
	str = replace(str, "<", "&lt;");
	str = replace(str, ">", "&gt;");
	return str;
    }

    String replace(String str, String from, String to) {
	StringBuffer sb = new StringBuffer();
	int ix;			// index of next `from'
	int offset = 0;		// index of previous `from' + length(from)

	while ((ix = str.indexOf(from, offset)) != -1) {
	    sb.append(str.substring(offset, ix));
	    sb.append(to);
	    offset = ix + from.length();
	}

	// End of string: append last bit and we're done
	sb.append(str.substring(offset));
	return sb.toString();
    }

    // Test harness
    public static void main (String[] args) {
	CQLNode n1 = new CQLTermNode("dc.author", "=", "kernighan");
	CQLNode n2 = new CQLTermNode("dc.title", "all", "elements style");
	CQLNode root = new CQLAndNode(n1, n2);
	System.out.println(root.toXCQL(0));
    }
}
