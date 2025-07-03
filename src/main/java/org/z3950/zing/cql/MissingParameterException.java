package org.z3950.zing.cql;

/**
 * Exception indicating that a required property was not specified.
 *
 */
public class MissingParameterException extends Exception {
    /**
     * Creates a new <code>MissingParameterException</code>.
     * @param s
     *	The name of the property whose value was required but not supplied.
     */
    public MissingParameterException(String s) {
        super(s);
    }
}
