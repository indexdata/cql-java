// $Id: CQLPrefix.java,v 1.5 2007-06-27 22:39:55 mike Exp $

package org.z3950.zing.cql;
import java.lang.String;

/**
 * Represents a CQL prefix mapping from short name to long identifier.
 *
 * @version	$Id: CQLPrefix.java,v 1.5 2007-06-27 22:39:55 mike Exp $
 */
public class CQLPrefix {
    /**
     * The short name of the prefix mapping.  That is, the prefix
     * itself, such as <TT>dc</TT>, as it might be used in an index
     * like <TT>dc.title</TT>.
     */
    String name;

    public String getName() {
        return name;
    }

    /**
     * The full identifier name of the prefix mapping.  That is,
     * typically, a URI permanently allocated to a specific index
     * set, such as <TT>http://zthes.z3950.org/cql/1.0<TT>.
     */
    String identifier;

    public String getIdentifier() {
        return identifier;
    }

    /**
     * Creates a new CQLPrefix mapping, which maps the specified name
     * to the specified identifier.
     */
    CQLPrefix(String name, String identifier) {
	this.name = name;
	this.identifier = identifier;
    }
}
