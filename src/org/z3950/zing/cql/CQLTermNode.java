// $Id: CQLTermNode.java,v 1.6 2002-10-31 22:22:01 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a terminal node in a CQL parse-tree.
 * ##
 *
 * @version	$Id: CQLTermNode.java,v 1.6 2002-10-31 22:22:01 mike Exp $
 */
public class CQLTermNode extends CQLNode {
    private String qualifier;
    private CQLRelation relation;
    private String term;

    public CQLTermNode(String qualifier, CQLRelation relation, String term) {
	this.qualifier = qualifier;
	this.relation = relation;
	this.term = term;
    }

    String toXCQL(int level) {
	return (indent(level) + "<searchClause>\n" +
		indent(level+1) + "<index>" + xq(qualifier) + "</index>\n" +
		relation.toXCQL(level+1) +
		indent(level+1) + "<term>" + xq(term) + "</term>\n" +
		indent(level) + "</searchClause>\n");
    }

    String toCQL() {
	String quotedQualifier = maybeQuote(qualifier);
	String quotedTerm = maybeQuote(term);
	String res = quotedTerm;

	if (!qualifier.equalsIgnoreCase("srw.serverChoice")) {
	    // ### We don't always need spaces around `relation'.
	    res = quotedQualifier + " " + relation.toCQL() + " " + quotedTerm;
	}

	return res;
    }

    static String maybeQuote(String str) {
	// There _must_ be a better way to make this test ...
	if (str.length() == 0 ||
	    str.indexOf('"') != -1 ||
	    str.indexOf(' ') != -1 ||
	    str.indexOf('\t') != -1 ||
	    str.indexOf('=') != -1 ||
	    str.indexOf('<') != -1 ||
	    str.indexOf('>') != -1 ||
	    str.indexOf('/') != -1 ||
	    str.indexOf('(') != -1 ||
	    str.indexOf(')') != -1) {
	    str = '"' + Utils.replaceString(str, "\"", "\\\"") + '"';
	}

	return str;
    }
}
