// $Id: MissingParameterException.java,v 1.2 2002-11-06 20:13:45 mike Exp $

package org.z3950.zing.cql;


/**
 * Exception indicating that a required property was not specified.
 *
 * @version	$Id: MissingParameterException.java,v 1.2 2002-11-06 20:13:45 mike Exp $
 */
public class MissingParameterException extends Exception {
    /**
     * Creates a new <TT>MissingParameterException</TT>.
     * @param s
     *	The name of the property whose value was required but not supplied.
     */
    public MissingParameterException(String s) {
	super(s);
    }
}
