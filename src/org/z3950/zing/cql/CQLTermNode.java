// $Id: CQLTermNode.java,v 1.25 2007-06-27 22:39:55 mike Exp $

package org.z3950.zing.cql;
import java.util.Properties;
import java.util.Vector;


/**
 * Represents a terminal node in a CQL parse-tree.
 * A term node consists of the term String itself, together with,
 * optionally, an index string and a relation.  Neither or both of
 * these must be provided - you can't have an index without a
 * relation or vice versa.
 *
 * @version	$Id: CQLTermNode.java,v 1.25 2007-06-27 22:39:55 mike Exp $
 */
public class CQLTermNode extends CQLNode {
    private String index;
    private CQLRelation relation;
    private String term;

    /**
     * Creates a new term node with the specified <TT>index</TT>,
     * <TT>relation</TT> and <TT>term</TT>.  The first two may be
     * <TT>null</TT>, but the <TT>term</TT> may not.
     */
    public CQLTermNode(String index, CQLRelation relation, String term) {
	this.index = index;
	this.relation = relation;
	this.term = term;
    }

    public String getIndex() { return index; }
    public String getQualifier() { return getIndex(); } // for legacy applications
    public CQLRelation getRelation() { return relation; }
    public String getTerm() { return term; }

    private static boolean isResultSetIndex(String qual) {
	return (qual.equals("srw.resultSet") ||
		qual.equals("srw.resultSetId") ||
		qual.equals("srw.resultSetName"));
    }

    public String getResultSetName() {
	if (isResultSetIndex(index))
	    return term;
	else
	    return null;
    }

    public String toXCQL(int level, Vector prefixes) {
	return (indent(level) + "<searchClause>\n" +
		renderPrefixes(level+1, prefixes) +
		indent(level+1) + "<index>" + xq(index) + "</index>\n" +
		relation.toXCQL(level+1, new Vector<String>()) +
		indent(level+1) + "<term>" + xq(term) + "</term>\n" +
		indent(level) + "</searchClause>\n");
    }

    public String toCQL() {
	String quotedIndex = maybeQuote(index);
	String quotedTerm = maybeQuote(term);
	String res = quotedTerm;

	if (index != null &&
	    !index.equalsIgnoreCase("srw.serverChoice")) {
	    // ### We don't always need spaces around `relation'.
	    res = quotedIndex + " " + relation.toCQL() + " " + quotedTerm;
	}

	return res;
    }

    // ### Interaction between this and its callers is not good as
    //	regards truncation of the term and generation of truncation
    //	attributes.  Change the interface to fix this.
    private Vector getAttrs(Properties config) throws PQFTranslationException {
	Vector<String> attrs = new Vector<String>();

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

	attr = config.getProperty("index." + index);
	if (attr == null)
	    throw new UnknownQualifierException(index);
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

	Vector<Modifier> mods = relation.getModifiers();
	for (int i = 0; i < mods.size(); i++) {
	    String type = mods.get(i).type;
	    attr = config.getProperty("relationModifier." + type);
	    if (attr == null)
		throw new UnknownRelationModifierException(type);
	    attrs.add(attr);
	}

	String pos = "any";
	String text = term;
	if (text.length() > 0 && text.substring(0, 1).equals("^")) {
	    text = text.substring(1); // ### change not seen by caller
	    pos = "first";
	}
	int len = text.length();
	if (len > 0 && text.substring(len-1, len).equals("^")) {
	    text = text.substring(0, len-1); // ### change not seen by caller
	    pos = pos.equals("first") ? "firstAndLast" : "last";
	    // ### in the firstAndLast case, the standard
	    //  pqf.properties file specifies that we generate a
	    //  completeness=whole-field attributem, which means that
	    //  we don't generate a position attribute at all.  Do we
	    //  care?  Does it matter?
	}

	attr = config.getProperty("position." + pos);
	if (attr == null)
	    throw new UnknownPositionException(pos);
	attrs.add(attr);

	attr = config.getProperty("structure." + rel);
	if (attr == null)
	    attr = config.getProperty("structure.*");
	attrs.add(attr);

	return attrs;
    }

    public String toPQF(Properties config) throws PQFTranslationException {
	if (isResultSetIndex(index)) {
	    // Special case: ignore relation, modifiers, wildcards, etc.
	    // There's parallel code in toType1BER()
	    return "@set " + maybeQuote(term);
	}

	Vector attrs = getAttrs(config);

	String attr, s = "";
	for (int i = 0; i < attrs.size(); i++) {
	    attr = (String) attrs.get(i);
	    s += "@attr " + Utils.replaceString(attr, " ", " @attr ") + " ";
	}

	String text = term;
	if (text.length() > 0 && text.substring(0, 1).equals("^"))
	    text = text.substring(1);
	int len = text.length();
	if (len > 0 && text.substring(len-1, len).equals("^"))
	    text = text.substring(0, len-1);

	return s + maybeQuote(text);
    }

    static String maybeQuote(String str) {
       if (str == null)
          return null;

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

    public byte[] toType1BER(Properties config) throws PQFTranslationException {
	if (isResultSetIndex(index)) {
	    // Special case: ignore relation, modifiers, wildcards, etc.
	    // There's parallel code in toPQF()
	    byte[] operand = new byte[term.length()+100];
	    int offset;
	    offset = putTag(CONTEXT, 0, CONSTRUCTED, operand, 0); // op
	    operand[offset++] = (byte)(0x80&0xff); // indefinite length
	    offset = putTag(CONTEXT, 31, PRIMITIVE, operand, offset); // ResultSetId
	    byte[] t = term.getBytes();
	    offset = putLen(t.length, operand, offset);
	    System.arraycopy(t, 0, operand, offset, t.length);
	    offset += t.length;
	    operand[offset++] = 0x00; // end of Operand
	    operand[offset++] = 0x00;
	    byte[] o = new byte[offset];
	    System.arraycopy(operand, 0, o, 0, offset);
	    return o;
	}

	String text = term;
	if (text.length() > 0 && text.substring(0, 1).equals("^"))
	    text = text.substring(1);
	int len = text.length();
	if (len > 0 && text.substring(len-1, len).equals("^"))
	    text = text.substring(0, len-1);

	String attr, attrList, term = text;
	byte[] operand = new byte[text.length()+100];
	int i, j, offset, type, value;
	offset = putTag(CONTEXT, 0, CONSTRUCTED, operand, 0); // op
	operand[offset++]=(byte)(0x80&0xff); // indefinite length
	offset = putTag(CONTEXT, 102, CONSTRUCTED, operand, offset); // AttributesPlusTerm
	operand[offset++] = (byte)(0x80&0xff); // indefinite length
	offset = putTag(CONTEXT, 44, CONSTRUCTED, operand, offset); // AttributeList
	operand[offset++] = (byte)(0x80&0xff); // indefinite length

	Vector attrs = getAttrs(config);
	for(i = 0; i < attrs.size(); i++) {
	    attrList = (String) attrs.get(i);
	    java.util.StringTokenizer st =
		new java.util.StringTokenizer(attrList);
	    while (st.hasMoreTokens()) {
		attr = st.nextToken();
		j = attr.indexOf('=');
		offset = putTag(UNIVERSAL, SEQUENCE, CONSTRUCTED, operand, offset);
		operand[offset++] = (byte)(0x80&0xff);
		offset = putTag(CONTEXT, 120, PRIMITIVE, operand, offset);
		type = Integer.parseInt(attr.substring(0, j));
		offset = putLen(numLen(type), operand, offset);
		offset = putNum(type, operand, offset);

		offset = putTag(CONTEXT, 121, PRIMITIVE, operand, offset);
		value = Integer.parseInt(attr.substring(j+1));
		offset = putLen(numLen(value), operand, offset);
		offset = putNum(value, operand, offset);
		operand[offset++] = 0x00; // end of SEQUENCE
		operand[offset++] = 0x00;
	    }
	}
	operand[offset++] = 0x00; // end of AttributeList
	operand[offset++] = 0x00;

	offset = putTag(CONTEXT, 45, PRIMITIVE, operand, offset); // general Term
	byte[] t = term.getBytes();
	offset = putLen(t.length, operand, offset);
	System.arraycopy(t, 0, operand, offset, t.length);
	offset += t.length;

	operand[offset++] = 0x00; // end of AttributesPlusTerm
	operand[offset++] = 0x00;
	operand[offset++] = 0x00; // end of Operand
	operand[offset++] = 0x00;
	byte[] o = new byte[offset];
	System.arraycopy(operand, 0, o, 0, offset);
	return o;
    }
}
