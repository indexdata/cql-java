// $Id: CQLPrefix.java,v 1.1 2002-11-14 22:04:16 mike Exp $

package org.z3950.zing.cql;
import java.lang.String;

/**
 * Represents a CQL prefix mapping from short name to long identifier.
 *
 * @version	$Id: CQLPrefix.java,v 1.1 2002-11-14 22:04:16 mike Exp $
 */
public class CQLPrefix {
    /**
     * The short name of the prefix mapping - that is, the prefix
     * itself, such as <TT>dc</TT>, as it might be used in a qualifier
     * like <TT>dc.title</TT>.
     */
    String name;

    /**
     * The full identifier name of the prefix mapping - that is, the prefix
     * itself, such as <TT>dc</TT>, as it might be used in a qualifier
     * like <TT>dc.title</TT>.
     */
    String identifier;

    /**
     * Creates a new CQLPrefix mapping, which maps the specified name
     * to the specified identifier.
     */
    CQLPrefix(String name, String identifier) {
	this.name = name;
	this.identifier = identifier;
    }
}
