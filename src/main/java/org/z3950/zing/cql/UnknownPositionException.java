// $Id: UnknownPositionException.java,v 1.2 2002-11-29 16:42:54 mike Exp $

package org.z3950.zing.cql;


/**
 * Exception indicating that a position was not recognised.
 * When rendering a tree out as PQF, each term is classified either as
 * <TT>any</TT>, <TT>first</TT>, <TT>last</TT> or
 * <TT>firstAndLast</TT>, depending on whether it begins and/or ends
 * with the word-anchoring meta-character <TT>^</TT>.  Its
 * classification is looked up as a <TT>position</TT> in the PQF
 * configuration.  If the position is not configured, we throw one of
 * these babies.
 *
 * @version $Id: UnknownPositionException.java,v 1.2 2002-11-29 16:42:54 mike Exp $
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
