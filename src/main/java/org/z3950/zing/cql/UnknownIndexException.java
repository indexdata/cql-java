package org.z3950.zing.cql;

/**
 * Exception indicating that an index was not recognised.
 * At compilation time, we accept any syntactically valid index;
 * but when rendering a tree out as PQF, we need to translate the
 * indexes into sets of Type-1 query attributes. If we can't do
 * that, because the PQF configuration doesn't know about a relation,
 * we throw one of these babies.
 *
 */
public class UnknownIndexException extends PQFTranslationException {
    /**
     * Creates a new <code>UnknownIndexException</code>.
     *
     * @param s
     *          The index for which there was no PQF configuration.
     */
    public UnknownIndexException(String s) {
        super(s);
    }
}
