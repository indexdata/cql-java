// $Id: CQLTermNode.java,v 1.4 2002-10-27 00:46:25 mike Exp $

package org.z3950.zing.cql;


/**
 * Represents a terminal node in a CQL parse-tree ...
 * ###
 *
 * @version	$Id: CQLTermNode.java,v 1.4 2002-10-27 00:46:25 mike Exp $
 */
public class CQLTermNode extends CQLNode {
    private String qualifier;
    private String relation;
    private String term;

    public CQLTermNode(String qualifier, String relation, String term) {
	this.qualifier = qualifier;
	this.relation = relation;
	this.term = term;
    }

    String toXCQL(int level) {
	return (indent(level) + "<searchClause>\n" +
		indent(level+1) + "<index>" + xq(qualifier) + "<index>\n" +
		indent(level+1) + "<relation>" + xq(relation) + "<relation>\n"+
		indent(level+1) + "<term>" + xq(term) + "<term>\n" +
		indent(level) + "</searchClause>\n");
    }

    String toCQL() {
	String quotedTerm = term;

	if (quotedTerm.indexOf('"') != -1) {
	    // ### precede each '"' with a '/'
	}

	// ### There must be a better way ...
	if (quotedTerm.indexOf('"') != -1 ||
	    quotedTerm.indexOf(' ') != -1 ||
	    quotedTerm.indexOf('\t') != -1 ||
	    quotedTerm.indexOf('=') != -1 ||
	    quotedTerm.indexOf('<') != -1 ||
	    quotedTerm.indexOf('>') != -1 ||
	    quotedTerm.indexOf('/') != -1 ||
	    quotedTerm.indexOf('(') != -1 ||
	    quotedTerm.indexOf(')') != -1) {
	    quotedTerm = '"' + quotedTerm + '"';
	}

	// ### The qualifier may need quoting.
	// ### We don't always need spaces around `relation'.
	return qualifier + " " + relation + " " + quotedTerm;
    }
}
