// $Id: MissingParameterException.java,v 1.1 2002-11-06 00:05:58 mike Exp $

package org.z3950.zing.cql;
import java.lang.Exception;


/**
 * Exception indicating that a required property was not specified.
 *
 * @version	$Id: MissingParameterException.java,v 1.1 2002-11-06 00:05:58 mike Exp $
 */
public class MissingParameterException extends Exception {
    MissingParameterException(String s) {
	super(s);
    }
}
