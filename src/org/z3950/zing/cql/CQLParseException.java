// $Id: CQLParseException.java,v 1.1 2002-10-30 09:19:26 mike Exp $

package org.z3950.zing.cql;
import java.lang.Exception;


/**
 * Exception indicating that an error ocurred parsing CQL.
 *
 * @version	$Id: CQLParseException.java,v 1.1 2002-10-30 09:19:26 mike Exp $
 */
public class CQLParseException extends Exception {
    CQLParseException(String s) {
	super(s);
    }
}

