package org.z3950.zing.cql;

/**
 * Exception indicating that a relation modifier was not recognised.
 * At compilation time, we accept any syntactically valid relation modifier;
 * but when rendering a tree out as PQF, we need to translate the
 * relation modifiers into sets of Type-1 query attributes. If we can't do
 * that, because the PQF configuration doesn't know about a relation modifier,
 * we throw one of these babies.
 *
 */
public class UnknownRelationModifierException extends PQFTranslationException {
    /**
     * Creates a new <code>UnknownRelationModifierException</code>.
     *
     * @param s
     *          The relation modifier for which there was no PQF configuration.
     */
    public UnknownRelationModifierException(String s) {
        super(s);
    }
}
