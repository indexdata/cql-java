
package org.z3950.zing.cql;


/**
 * Base class for exceptions occurring when translating parse trees to PQF.
 *
 */
public class PQFTranslationException extends Exception {
    PQFTranslationException(String s) {
	super(s);
    }
}
