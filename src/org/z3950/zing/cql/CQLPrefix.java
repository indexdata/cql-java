// $Id: CQLPrefix.java,v 1.2 2002-11-15 12:02:29 mike Exp $

package org.z3950.zing.cql;
import java.lang.String;

/**
 * Represents a CQL prefix mapping from short name to long identifier.
 *
 * @version	$Id: CQLPrefix.java,v 1.2 2002-11-15 12:02:29 mike Exp $
 */
public class CQLPrefix {
    /**
     * The short name of the prefix mapping - that is, the prefix
     * itself, such as <TT>dc</TT>, as it might be used in a qualifier
     * like <TT>dc.title</TT>.
     */
    public String name;

    /**
     * The full identifier name of the prefix mapping - that is, the prefix
     * itself, such as <TT>dc</TT>, as it might be used in a qualifier
     * like <TT>dc.title</TT>.
     */
    public String identifier;

    /**
     * Creates a new CQLPrefix mapping, which maps the specified name
     * to the specified identifier.
     */
    CQLPrefix(String name, String identifier) {
	this.name = name;
	this.identifier = identifier;
    }
}
