// $Id: UnknownPositionException.java,v 1.1 2002-11-06 20:13:45 mike Exp $

package org.z3950.zing.cql;
import java.lang.Exception;


/**
 * Exception indicating that a position was not recognised.
 * When rendering a tree out as PQF, each term is classified either as
 * <TT>anchored</TT> or <TT>unanchored</TT>, depending on whether it
 * begins with the word-anchoring meta-character <TT>^</TT>, and its
 * classification is looked up as a <TT>position</TT> in the PQF
 * configuration.  If the position is not configured, we throw one of
 * these babies.
 *
 * @version $Id: UnknownPositionException.java,v 1.1 2002-11-06 20:13:45 mike Exp $
 */
public class UnknownPositionException extends PQFTranslationException {
    /**
     * Creates a new <TT>UnknownPositionException</TT>.
     * @param s
     *	The position for which there was no PQF configuration.
     */
    public UnknownPositionException(String s) {
	super(s);
    }
}
