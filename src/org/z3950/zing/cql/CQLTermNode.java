// $Id: CQLTermNode.java,v 1.11 2002-11-29 16:43:23 mike Exp $

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
 * @version	$Id: CQLTermNode.java,v 1.11 2002-11-29 16:43:23 mike Exp $
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

    public String toXCQL(int level, Vector prefixes) {
	return (indent(level) + "<searchClause>\n" +
		renderPrefixes(level+1, prefixes) +
		indent(level+1) + "<index>" + xq(qualifier) + "</index>\n" +
		relation.toXCQL(level+1, new Vector()) +
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

    public String toPQF(Properties config) throws PQFTranslationException {
	Vector attrs = new Vector();

	// Do this first so that if any other truncation or
	// completeness attributes are generated, they "overwrite"
	// those specified here.
	//
	//  ###	This approach relies on an unpleasant detail of Index
	//	Data's (admittedly definitive) implementation of PQF,
	//	and should not relied upon.
	//
	String attr = config.getProperty("always");
	if (attr != null)
	    attrs.add(attr);

	attr = config.getProperty("qualifier." + qualifier);
	if (attr == null)
	    throw new UnknownQualifierException(qualifier);
	attrs.add(attr);

	String rel = relation.getBase();
	if (rel.equals("=")) {
	    rel = "eq";
	} else if (rel.equals("<=")) {
	    rel = "le";
	} else if (rel.equals(">=")) {
	    rel = "ge";
	}
	// ### Handling "any" and "all" properly would involve breaking
	// the string down into a bunch of individual words and ORring
	// or ANDing them together.  Another day.
	attr = config.getProperty("relation." + rel);
	if (attr == null)
	    throw new UnknownRelationException(rel);
	attrs.add(attr);

	String[] mods = relation.getModifiers();
	for (int i = 0; i < mods.length; i++) {
	    attr = config.getProperty("relationModifier." + mods[i]);
	    if (attr == null)
		throw new UnknownRelationModifierException(mods[i]);
	    attrs.add(attr);
	}

	String pos = "any";
	String text = term;
	//System.err.println("before check: text='" + text + "'");
	if (text.length() > 0 && text.substring(0, 1).equals("^")) {
	    //System.err.println("first in field");
	    text = text.substring(1);
	    pos = "first";
	}
	//System.err.println("between checks: text='" + text + "'");
	int len = text.length();
	if (len > 0 && text.substring(len-1, len).equals("^")) {
	    //System.err.println("last in field");
	    text = text.substring(0, len-1);
	    pos = pos.equals("first") ? "firstAndLast" : "last";
	    // ### in the firstAndLast case, the standard
	    //	pqf.properties file specifies that we generate a
	    //	completeness=whole-field attributem, which means that
	    //	we don't generate a position attribute at all.  Do we
	    //	care?  Does it matter?
	}
	//System.err.println("after checks: text='" + text + "'");
	attr = config.getProperty("position." + pos);
	if (attr == null)
	    throw new UnknownPositionException(pos);
	attrs.add(attr);

	attr = config.getProperty("structure." + rel);
	if (attr == null)
	    attr = config.getProperty("structure.*");
	attrs.add(attr);

	String s = "";
	for (int i = 0; i < attrs.size(); i++) {
	    attr = (String) attrs.get(i);
	    s += "@attr " + Utils.replaceString(attr, " ", " @attr ") + " ";
	}

	return s + maybeQuote(text);
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
