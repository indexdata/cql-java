// $Id: ParameterMissingException.java,v 1.1 2002-10-30 09:19:26 mike Exp $

package org.z3950.zing.cql;
import java.lang.Exception;


/**
 * Exception indicating that a required property was not specified.
 *
 * @version	$Id: ParameterMissingException.java,v 1.1 2002-10-30 09:19:26 mike Exp $
 */
public class ParameterMissingException extends Exception {
    ParameterMissingException(String s) {
	super(s);
    }
}
