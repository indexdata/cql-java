// $Id: UnknownQualifierException.java,v 1.1 2002-11-06 00:05:58 mike Exp $

package org.z3950.zing.cql;
import java.lang.Exception;


/**
 * Exception indicating that a qualifier was not recognised.
 * At compilation time, we accept any syntactically valid qualifier;
 * but when rendering a tree out as PQF, we need to translate the
 * qualifiers into sets of Type-1 query attributes.  If we can't do
 * that, we throw one of these babies.
 *
 * @version $Id: UnknownQualifierException.java,v 1.1 2002-11-06 00:05:58 mike Exp $
 */
public class UnknownQualifierException extends Exception {
    UnknownQualifierException(String s) {
	super(s);
    }
}
