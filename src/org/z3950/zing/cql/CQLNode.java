// $Id: CQLNode.java,v 1.14 2002-11-17 23:29:02 mike Exp $

package org.z3950.zing.cql;
import java.util.Properties;


/**
 * Represents a node in a CQL parse-tree.
 *
 * @version	$Id: CQLNode.java,v 1.14 2002-11-17 23:29:02 mike Exp $
 */
public abstract class CQLNode {
    CQLNode() {}		// prevent javadoc from documenting this

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
    abstract public String toXCQL(int level);

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
     *	specification is described in the cql-java distribution's
     *	sample PQF-mapping configuration file,
     *	<TT>etc/pqf.properties</TT>, which see.
     * @return
     *	A String containing a PQF query equivalent to the parse-tree
     *	whose root is this node.  This may be fed into the tool of
     *	your choice to obtain a BER-encoded packet.
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
}
