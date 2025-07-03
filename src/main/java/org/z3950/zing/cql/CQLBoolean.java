package org.z3950.zing.cql;

/**
 * Represents a boolean operator in CQL.
 * @author jakub
 */
public enum CQLBoolean {
    /** AND is the same as CQL's "and" */
    AND,
    /** OR is the same as CQL's "or" */
    OR,
    /** NOT is the same as CQL's "not" */
    NOT,
    /** PROX is the same as CQL's "prox" */
    PROX;
}
