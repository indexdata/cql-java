// $Id: UnknownRelationException.java,v 1.1 2002-11-06 00:05:58 mike Exp $

package org.z3950.zing.cql;
import java.lang.Exception;


/**
 * Exception indicating that a relation was not recognised.
 * At compilation time, we accept any syntactically valid relation;
 * but when rendering a tree out as PQF, we need to translate the
 * relations into sets of Type-1 query attributes.  If we can't do
 * that, we throw one of these babies.
 *
 * @version $Id: UnknownRelationException.java,v 1.1 2002-11-06 00:05:58 mike Exp $
 */
public class UnknownRelationException extends Exception {
    UnknownRelationException(String s) {
	super(s);
    }
}
