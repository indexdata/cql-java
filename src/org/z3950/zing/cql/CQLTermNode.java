// $Id: CQLTermNode.java,v 1.2 2002-10-25 16:01:26 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a terminal node in a CQL parse-tree ...
 * ###
 *
 * @version	$Id: CQLTermNode.java,v 1.2 2002-10-25 16:01:26 mike Exp $
 */
public class CQLTermNode extends CQLNode {
    private String qualifier;
    private String relation;
    private String value;

    public CQLTermNode(String qualifier, String relation, String value) {
	this.qualifier = qualifier;
	this.relation = relation;
	this.value = value;
    }

    String toXCQL(int level) {
	return (indent(level) + "<searchClause>\n" +
		indent(level+1) + "<index>" + qualifier + "<index>\n" +
		indent(level+1) + "<relation>" + relation + "<relation>\n" +
		indent(level+1) + "<term>" + value + "<term>\n" +
		indent(level) + "</searchClause>\n");
    }

    String toCQL() {
	String res = value;

	if (res.indexOf('"') != -1) {
	    // ### precede each '"' with a '/'
	}

	if (res.indexOf('"') != -1 ||
	    res.indexOf(' ') != -1 ||
	    res.indexOf('\t') != -1 ||
	    res.indexOf('=') != -1 ||
	    res.indexOf('<') != -1 ||
	    res.indexOf('>') != -1 ||
	    res.indexOf('/') != -1 ||
	    res.indexOf('(') != -1 ||
	    res.indexOf(')') != -1) {
	    res = '"' + res + '"';
	}

	// ### The qualifier may need quoting.
	// ### We don't always need spaces around `relation'.
	return qualifier + " " + relation + " " + value;
    }
}
