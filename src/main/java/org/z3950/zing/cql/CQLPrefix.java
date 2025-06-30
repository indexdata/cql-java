package org.z3950.zing.cql;

/**
 * Represents a CQL prefix mapping from short name to long identifier.
 *
 */
public class CQLPrefix {

    String name;

    /**
     * The short name of the prefix mapping.  That is, the prefix
     * itself, such as <code>dc</code>, as it might be used in an index
     * like <code>dc.title</code>.
     */
    public String getName() {
        return name;
    }

 
    String identifier;

    /**
     * The full identifier name of the prefix mapping.  That is,
     * typically, a URI permanently allocated to a specific index
     * set, such as <code>http://zthes.z3950.org/cql/1.0<code>.
     */
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
