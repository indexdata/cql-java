// $Id: UnknownRelationModifierException.java,v 1.1 2002-11-06 20:13:45 mike Exp $

package org.z3950.zing.cql;
import java.lang.Exception;


/**
 * Exception indicating that a relation modifier was not recognised.
 * At compilation time, we accept any syntactically valid relation modifier;
 * but when rendering a tree out as PQF, we need to translate the
 * relation modifiers into sets of Type-1 query attributes.  If we can't do
 * that, because the PQF configuration doesn't know about a relation modifier,
 * we throw one of these babies.
 *
 * @version $Id: UnknownRelationModifierException.java,v 1.1 2002-11-06 20:13:45 mike Exp $
 */
public class UnknownRelationModifierException extends PQFTranslationException {
    /**
     * Creates a new <TT>UnknownRelationModifierException</TT>.
     * @param s
     *	The relation modifier for which there was no PQF configuration.
     */
    public UnknownRelationModifierException(String s) {
	super(s);
    }
}
