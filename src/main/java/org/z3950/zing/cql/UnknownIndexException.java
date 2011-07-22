// $Id: UnknownIndexException.java,v 1.2 2007-06-27 22:44:40 mike Exp $

package org.z3950.zing.cql;


/**
 * Exception indicating that an index was not recognised.
 * At compilation time, we accept any syntactically valid index;
 * but when rendering a tree out as PQF, we need to translate the
 * indexes into sets of Type-1 query attributes.  If we can't do
 * that, because the PQF configuration doesn't know about a relation,
 * we throw one of these babies.
 *
 * @version $Id: UnknownIndexException.java,v 1.2 2007-06-27 22:44:40 mike Exp $
 */
public class UnknownIndexException extends PQFTranslationException {
    /**
     * Creates a new <TT>UnknownIndexException</TT>.
     * @param s
     *	The index for which there was no PQF configuration.
     */
    public UnknownIndexException(String s) {
	super(s);
    }
}
