package org.z3950.zing.cql;

/**
 * Exception indicating that a position was not recognised.
 * When rendering a tree out as PQF, each term is classified either as
 * <code>any</code>, <code>first</code>, <code>last</code> or
 * <code>firstAndLast</code>, depending on whether it begins and/or ends
 * with the word-anchoring meta-character <code>^</code>. Its
 * classification is looked up as a <code>position</code> in the PQF
 * configuration. If the position is not configured, we throw one of
 * these babies.
 *
 */
public class UnknownPositionException extends PQFTranslationException {
    /**
     * Creates a new <code>UnknownPositionException</code>.
     *
     * @param s
     *          The position for which there was no PQF configuration.
     */
    public UnknownPositionException(String s) {
        super(s);
    }
}
