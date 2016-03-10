package org.z3950.zing.cql;

/**
 * Exception indicating that a truncation was not recognised. When rendering a
 * tree out as PQF, each term is classified either as <TT>left</TT>,
 * <TT>right</TT>, <TT>left and right</TT> truncated, depending on whether
 * it begins and/or ends with the character <TT>*</TT>. Its
 * classification is looked up as a <TT>truncation</TT> in the PQF
 * configuration. If the truncation is not configured, we throw one of these
 * babies.
 *
 */
public class UnknownTruncationException extends PQFTranslationException {
	private static final long serialVersionUID = 6971993723734811253L;

	/**
	 * Creates a new <TT>UnknownTruncationException</TT>.
	 * 
	 * @param s
	 *            The truncation for which there was no PQF configuration.
	 */
	public UnknownTruncationException(String s) {
		super(s);
	}
}
