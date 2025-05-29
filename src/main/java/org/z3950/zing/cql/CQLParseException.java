package org.z3950.zing.cql;

/**
 * Exception indicating that an error ocurred parsing CQL.
 *
 */
public class CQLParseException extends Exception {
    private int pos;
    /**
     * Creates a new <code>CQLParseException</code>.
     * @param s
     *	An error message describing the problem with the query,
     *	usually a syntax error of some kind.
     */
    public CQLParseException(String s, int pos) {
	super(s);
        this.pos = pos;
    }
    
    /**
     * Character position of the parsing error.
     * @return 
     */
    public int getPosition() {
      return pos;
    }
}

