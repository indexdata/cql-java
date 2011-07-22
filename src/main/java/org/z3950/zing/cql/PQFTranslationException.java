// $Id: PQFTranslationException.java,v 1.1 2002-11-06 20:13:45 mike Exp $

package org.z3950.zing.cql;


/**
 * Base class for exceptions occurring when translating parse trees to PQF.
 *
 * @version $Id: PQFTranslationException.java,v 1.1 2002-11-06 20:13:45 mike Exp $
 */
public class PQFTranslationException extends Exception {
    PQFTranslationException(String s) {
	super(s);
    }
}
