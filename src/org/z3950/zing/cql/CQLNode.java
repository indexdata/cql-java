// $Id: CQLNode.java,v 1.21 2002-12-12 10:24:25 mike Exp $

package org.z3950.zing.cql;
import java.util.Properties;
import java.util.Vector;


/**
 * Represents a node in a CQL parse-tree.
 *
 * @version	$Id: CQLNode.java,v 1.21 2002-12-12 10:24:25 mike Exp $
 */
public abstract class CQLNode {
    CQLNode() {}		// prevent javadoc from documenting this

    /**
     * Returns the name of the result-set to which this query is a
     * reference, if and only if the entire query consists only of a
     * result-set reference.  If it's anything else, including a
     * boolean combination of a result-set reference with something
     * else, then null is returned instead.
     * @return the name of the referenced result-set
     */
    public String getResultSetName() { return null; }

    /**
     * Translates a parse-tree into an XCQL document.
     * <P>
     * @param level
     *	The number of levels to indent the top element of the XCQL
     *	document.  This will typically be 0 when invoked by an
     *	application; it takes higher values when this method is
     *	invoked recursively for nodes further down the tree.
     * @return
     *	A String containing an XCQL document equivalent to the
     *	parse-tree whose root is this node.
     */
    public String toXCQL(int level) {
	return toXCQL(level, new Vector());
    }

    abstract public String toXCQL(int level, Vector prefixes);

    protected static String renderPrefixes(int level, Vector prefixes) {
	if (prefixes.size() == 0)
	    return "";
	String res = indent(level) + "<prefixes>\n";
	for (int i = 0; i < prefixes.size(); i++) {
	    CQLPrefix p = (CQLPrefix) prefixes.get(i);
	    res += indent(level+1) + "<prefix>\n";
	    if (p.name != null)
		res += indent(level+2) + "<name>" + p.name + "</name>\n";
	    res += indent(level+2) +
		"<identifier>" + p.identifier + "</identifier>\n";
	    res += indent(level+1) + "</prefix>\n";
	}
	return res + indent(level) + "</prefixes>\n";
    }

    /**
     * Decompiles a parse-tree into a CQL query.
     * <P>
     * @return
     *	A String containing a CQL query equivalent to the parse-tree
     *	whose root is this node, so that compiling that query will
     *	yield an identical tree.
     */
    abstract public String toCQL();

    /**
     * Renders a parse-tree into a Yaz-style PQF string.
     * PQF, or Prefix Query Format, is a cryptic but powerful notation
     * that can be trivially mapped, one-to-one, int Z39.50 Type-1 and
     * Type-101 queries.  A specification for the format can be found
     * in
     * <A href="http://indexdata.dk/yaz/doc/tools.php#PQF"
     *	>Chapter 7 (Supporting Tools)</A> of the
     * <A href="http://indexdata.dk/yaz/">YAZ</A> manual.
     * <P>
     * @param config
     *	A <TT>Properties</TT> object containing configuration
     *	information that specifies the mapping from CQL qualifiers,
     *	relations, etc. to Type-1 attributes.  The mapping
     *	specification is described in the CQL-Java distribution's
     *	sample PQF-mapping configuration file,
     *	<TT>etc/pqf.properties</TT>, which see.
     * @return
     *	A String containing a PQF query equivalent to the parse-tree
     *	whose root is this node.
     */
    abstract public String toPQF(Properties config)
	throws PQFTranslationException;

    /**
     * Returns a String of spaces for indenting to the specified level.
     */
    protected static String indent(int level) { return Utils.indent(level); }

    /**
     * Returns the argument String quoted for XML.
     * For example, each occurrence of <TT>&lt;</TT> is translated to
     * <TT>&amp;lt;</TT>.
     */
    protected static String xq(String str) { return Utils.xq(str); }

    /**
     * Renders a parser-tree into a BER-endoded packet representing an
     * equivalent Z39.50 Type-1 query.  If you don't know what that
     * means, then you don't need this method :-)  This is useful
     * primarily for SRW-to-Z39.50 gateways.
     *
     * @param config
     *	A <TT>Properties</TT> object containing configuration
     *	information that specifies the mapping from CQL qualifiers,
     *	relations, etc. to Type-1 attributes.  The mapping
     *	specification is described in the CQL-Java distribution's
     *	sample PQF-mapping configuration file,
     *	<TT>etc/pqf.properties</TT>, which see.
     * @return
     *	A byte array containing the BER packet.
     * @see
     *	<A href="ftp://ftp.rsasecurity.com/pub/pkcs/ascii/layman.asc"
     *	        >ftp://ftp.rsasecurity.com/pub/pkcs/ascii/layman.asc</A>
     */
    abstract public byte[] toType1BER(Properties config)
	throws PQFTranslationException;

    // ANS.1 classes
    protected static final int UNIVERSAL   = 0;
    protected static final int APPLICATION = 1;
    protected static final int CONTEXT     = 2;
    protected static final int PRIVATE     = 3;

    // ASN.1 tag forms
    protected static final int PRIMITIVE   = 0;
    protected static final int CONSTRUCTED = 1;

    // ASN.1 UNIVERSAL data types
    public static final byte BOOLEAN          =  1;
    public static final byte INTEGER          =  2;
    public static final byte BITSTRING        =  3;
    public static final byte OCTETSTRING      =  4;
    public static final byte NULL             =  5;
    public static final byte OBJECTIDENTIFIER =  6;
    public static final byte OBJECTDESCRIPTOR =  7;
    public static final byte EXTERNAL         =  8;
    public static final byte ENUMERATED       = 10;
    public static final byte SEQUENCE         = 16;
    public static final byte SET              = 17;
    public static final byte VISIBLESTRING    = 26;
    public static final byte GENERALSTRING    = 27;

    protected static final int putTag(int asn1class, int fldid, int form,
				      byte[] record, int offset) {
        if (fldid < 31)
            record[offset++] = (byte)(fldid + asn1class*64 + form*32);
        else {
            record[offset++] = (byte)(31 + asn1class*64 + form*32);
            if (fldid < 128)
                record[offset++] = (byte)(fldid);
            else {
                record[offset++] = (byte)(128 + fldid/128);
                record[offset++] = (byte)(fldid % 128);
            }
        }
        return offset;
    }

    /**
     * Put a length directly into a BER record.
     *
     * @param length length to put into record
     * @return the new, incremented value of the offset parameter.
     */
    public // ### shouldn't this be protected?
	static final int putLen(int len, byte[] record, int offset) {

        if (len < 128)
            record[offset++] = (byte)len;
        else {
            int t;
            record[offset] = (byte)(lenLen(len) - 1);
            for (t = record[offset]; t > 0; t--) {
                record[offset+t] = (byte)(len & 0xff);
                len >>= 8;
            }
            t = offset;
            offset += (record[offset]&0xff) + 1;
            record[t] += 128; // turn on bit 8 in length byte.
        }
        return offset;
    }

    /**
     * Get the length needed to represent the given length.
     *
     * @param length determine length needed to encode this
     * @return length needed to encode given length
     */
    protected // ### shouldn't this be private?
	static final int lenLen(int length) {

        return ((length < 128) ? 1 :
            (length < 256) ? 2 :
                (length < 65536L) ? 3 : 4);
    }

    /**
     * Get the length needed to represent the given number.
     *
     * @param number determine length needed to encode this
     * @return length needed to encode given number
     */
    protected static final int numLen(long num) {
        num = num < 0 ? -num : num;
	// ### Wouldn't this be better done algorithmically?
	// Or at least with the constants expressed in hex?
        return ((num < 128) ? 1 :
            (num < 32768) ? 2 :
                (num < 8388608) ? 3 :
                    (num < 2147483648L) ? 4 :
                        (num < 549755813888L) ? 5 :
                            (num < 140737488355328L) ? 6 :
                                (num < 36028797018963968L) ? 7 : 8);
    }

    /**
     * Put a number into a given buffer
     *
     * @param num number to put into buffer
     * @param record buffer to use
     * @param offset offset into buffer
     * @return the new, incremented value of the offset parameter.
     */
    protected static final int putNum(long num, byte record[], int offset) {
        int cnt=numLen(num);

        for (int count = cnt - 1; count >= 0; count--) {
            record[offset+count] = (byte)(num & 0xff);
            num >>= 8;
        }
        return offset+cnt;
    }

    // Used only by the makeOID() method
    private static final java.util.Hashtable madeOIDs =
	new java.util.Hashtable(10);

    protected static final byte[] makeOID(String oid) {
        byte[] o;
        int dot, offset = 0, oidOffset = 0, value;

        if ((o = (byte[])madeOIDs.get(oid)) == null) {
            o = new byte[100];

	    // Isn't this kind of thing excruciating in Java?
            while (oidOffset < oid.length() &&
              Character.isDigit(oid.charAt(oidOffset)) == true) {
                if (offset > 90) // too large
                    return null;

                dot = oid.indexOf('.', oidOffset);
                if (dot == -1)
                    dot = oid.length();

                value = Integer.parseInt(oid.substring(oidOffset, dot));

                if (offset == 0) {  // 1st two are special
                    if (dot == -1) // ### can't happen: -1 is reassigned above
                        return null; // can't be this short
                    oidOffset = dot+1; // skip past '.'

                    dot = oid.indexOf('.', oidOffset);
                    if (dot == -1)
                        dot = oid.length();

		    // ### Eh?!
                    value = value * 40 +
                        Integer.parseInt(oid.substring(oidOffset,dot));
                }

                if (value < 0x80) {
                    o[offset++] = (byte)value;
                } else {
                    int count = 0;
                    byte bits[] = new byte[12]; // save a 84 (12*7) bit number

                    while (value != 0) {
                        bits[count++] = (byte)(value & 0x7f);
                        value >>= 7;
                    }

                    // Now place in the correct order
                    while (--count > 0)
                        o[offset++] = (byte)(bits[count] | 0x80);

                    o[offset++] = bits[count];
		}

                dot = oid.indexOf('.', oidOffset);
                if (dot == -1)
                    break;

		oidOffset = dot+1;
            }

            byte[] ptr = new byte[offset];
            System.arraycopy(o, 0, ptr, 0, offset);
            madeOIDs.put(oid, ptr);
            return ptr;
        }
        return o;
    }

    public static final byte[] makeQuery(CQLNode root, Properties properties)
	throws PQFTranslationException {
        byte[] rpnStructure = root.toType1BER(properties);
        byte[] qry = new byte[rpnStructure.length+100];
        int offset = 0;
        offset = putTag(CONTEXT, 1, CONSTRUCTED, qry, offset);
        qry[offset++] = (byte)(0x80&0xff);  // indefinite length
        offset = putTag(UNIVERSAL, OBJECTIDENTIFIER, PRIMITIVE, qry, offset);
        byte[] oid = makeOID("1.2.840.10003.3.1"); // bib-1
        offset = putLen(oid.length, qry, offset);
        System.arraycopy(oid, 0, qry, offset, oid.length);
        offset += oid.length;
        System.arraycopy(rpnStructure, 0, qry, offset, rpnStructure.length);
        offset += rpnStructure.length;
        qry[offset++] = 0x00;  // end of query
        qry[offset++] = 0x00;
        byte[] q = new byte[offset];
        System.arraycopy(qry, 0, q, 0, offset);
        return q;
    }
}
