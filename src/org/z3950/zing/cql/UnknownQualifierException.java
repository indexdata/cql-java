// $Id: UnknownQualifierException.java,v 1.3 2007-06-27 22:39:55 mike Exp $

package org.z3950.zing.cql;
import java.lang.Exception;


/**
 * Exception indicating that an index was not recognised.
 * At compilation time, we accept any syntactically valid index;
 * but when rendering a tree out as PQF, we need to translate the
 * indexes into sets of Type-1 query attributes.  If we can't do
 * that, because the PQF configuration doesn't know about a relation,
 * we throw one of these babies.
 *
 * @version $Id: UnknownQualifierException.java,v 1.3 2007-06-27 22:39:55 mike Exp $
 */
public class UnknownQualifierException extends PQFTranslationException {
    /**
     * Creates a new <TT>UnknownQualifierException</TT>.
     * @param s
     *	The index for which there was no PQF configuration.
     */
    public UnknownQualifierException(String s) {
	super(s);
    }
}
