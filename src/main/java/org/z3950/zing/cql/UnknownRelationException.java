// $Id: UnknownRelationException.java,v 1.2 2002-11-06 20:13:45 mike Exp $

package org.z3950.zing.cql;
import java.lang.Exception;


/**
 * Exception indicating that a relation was not recognised.
 * At compilation time, we accept any syntactically valid relation;
 * but when rendering a tree out as PQF, we need to translate the
 * relations into sets of Type-1 query attributes.  If we can't do
 * that, because the PQF configuration doesn't know about a relation,
 * we throw one of these babies.
 *
 * @version $Id: UnknownRelationException.java,v 1.2 2002-11-06 20:13:45 mike Exp $
 */
public class UnknownRelationException extends PQFTranslationException {
    /**
     * Creates a new <TT>UnknownRelationException</TT>.
     * @param s
     *	The relation for which there was no PQF configuration.
     */
    public UnknownRelationException(String s) {
	super(s);
    }
}
