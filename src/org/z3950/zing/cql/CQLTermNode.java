// $Id: CQLTermNode.java,v 1.7 2002-11-06 00:05:58 mike Exp $

package org.z3950.zing.cql;
import java.util.Properties;
import java.util.Vector;


/**
 * Represents a terminal node in a CQL parse-tree.
 * A term node consists of the term String itself, together with,
 * optionally, a qualifier string and a relation.  Neither or both of
 * these must be provided - you can't have a qualifier without a
 * relation or vice versa.
 *
 * @version	$Id: CQLTermNode.java,v 1.7 2002-11-06 00:05:58 mike Exp $
 */
public class CQLTermNode extends CQLNode {
    private String qualifier;
    private CQLRelation relation;
    private String term;

    /**
     * Creates a new term node with the specified <TT>qualifier</TT>,
     * <TT>relation</TT> and <TT>term</TT>.  The first two may be
     * <TT>null</TT>, but the <TT>term</TT> may not.
     */
    public CQLTermNode(String qualifier, CQLRelation relation, String term) {
	this.qualifier = qualifier;
	this.relation = relation;
	this.term = term;
    }

    public String getQualifier() { return qualifier; }
    public CQLRelation getRelation() { return relation; }
    public String getTerm() { return term; }

    public String toXCQL(int level) {
	return (indent(level) + "<searchClause>\n" +
		indent(level+1) + "<index>" + xq(qualifier) + "</index>\n" +
		relation.toXCQL(level+1) +
		indent(level+1) + "<term>" + xq(term) + "</term>\n" +
		indent(level) + "</searchClause>\n");
    }

    public String toCQL() {
	String quotedQualifier = maybeQuote(qualifier);
	String quotedTerm = maybeQuote(term);
	String res = quotedTerm;

	if (!qualifier.equalsIgnoreCase("srw.serverChoice")) {
	    // ### We don't always need spaces around `relation'.
	    res = quotedQualifier + " " + relation.toCQL() + " " + quotedTerm;
	}

	return res;
    }

    public String toPQF(Properties config)
	throws UnknownQualifierException, UnknownRelationException {
	Vector attrs = new Vector();

	if (qualifier != null) {
	    String s = config.getProperty(qualifier);
	    if (s == null)
		throw new UnknownQualifierException(qualifier);
	    attrs.add(s);
	} else {
	    // ### get a default access point from properties?
	}

	if (relation != null) {
	    String rel = relation.getBase();
	    // ### handle "any" and "all"
	    String s = config.getProperty("cql-java.relation." + rel);
	    if (s == null)
		throw new UnknownRelationException(rel);
	    attrs.add(s);
	} else {
	    // ### get a default relation from properties?
	}

	// ### handle position attributes
	// ### handle structure attributes
	// ### handle "always" attributes

	// ### should split Vector elements on spaces
	String s = "";
	for (int i = 0; i < attrs.size(); i++) {
	    s += "@attr " + (String) attrs.get(i) + " ";
	}

	return s + maybeQuote(term);
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
