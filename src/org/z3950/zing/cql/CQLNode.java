// $Id: CQLNode.java,v 1.12 2002-11-06 20:13:45 mike Exp $

package org.z3950.zing.cql;
import java.util.Properties;


/**
 * Represents a node in a CQL parse-tree.
 *
 * @version	$Id: CQLNode.java,v 1.12 2002-11-06 20:13:45 mike Exp $
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
     * <P>
     * <PRE>
	query ::= top-set query-struct.
	top-set ::= [ '@attrset' string ]
	query-struct ::= attr-spec | simple | complex | '@term' term-type
	attr-spec ::= '@attr' [ string ] string query-struct
	complex ::= operator query-struct query-struct.
	operator ::= '@and' | '@or' | '@not' | '@prox' proximity.
	simple ::= result-set | term.
	result-set ::= '@set' string.
	term ::= string.
	proximity ::= exclusion distance ordered relation which-code unit-code.
	exclusion ::= '1' | '0' | 'void'.
	distance ::= integer.
	ordered ::= '1' | '0'.
	relation ::= integer.
	which-code ::= 'known' | 'private' | integer.
	unit-code ::= integer.
	term-type ::= 'general' | 'numeric' | 'string' | 'oid' | 'datetime' | 'null'.
     * </PRE>
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
