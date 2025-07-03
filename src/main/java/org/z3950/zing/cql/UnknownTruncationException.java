package org.z3950.zing.cql;

/**
 * Exception indicating that a truncation was not recognised. When rendering a
 * tree out as PQF, each term is classified either as <code>left</code>,
 * <code>right</code>, <code>left and right</code> truncated, depending on whether
 * it begins and/or ends with the character <code>*</code>. Its
 * classification is looked up as a <code>truncation</code> in the PQF
 * configuration. If the truncation is not configured, we throw one of these
 * babies.
 *
 */
public class UnknownTruncationException extends PQFTranslationException {
    private static final long serialVersionUID = 6971993723734811253L;

    /**
     * Creates a new <code>UnknownTruncationException</code>.
     *
     * @param s
     *            The truncation for which there was no PQF configuration.
     */
    public UnknownTruncationException(String s) {
        super(s);
    }
}
