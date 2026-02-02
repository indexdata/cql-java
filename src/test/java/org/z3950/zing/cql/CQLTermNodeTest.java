package org.z3950.zing.cql;

import org.junit.Test;
import static org.junit.Assert.*;

public class CQLTermNodeTest {
    @Test
    public void TestMaybeQuoteNull() {
        assertNull(CQLTermNode.maybeQuote(null));
    }

    @Test
    public void TestMaybeQuoteEmpty() {
        assertEquals("\"\"", CQLTermNode.maybeQuote(""));
    }

    @Test
    public void TestMaybeQuoteRelation() {
        assertEquals("\"<\"", CQLTermNode.maybeQuote("<"));
    }

    @Test
    public void TestMaybeQuoteSimple() {
        assertEquals("simple", CQLTermNode.maybeQuote("simple"));
    }

    @Test
    public void TestMaybeQuoteBlank() {
        assertEquals("\"a b\"", CQLTermNode.maybeQuote("a b"));
    }

    @Test
    public void TestMaybeQuoteQuote1() {
        assertEquals("a\\\"", CQLTermNode.maybeQuote("a\""));
    }

    @Test
    public void TestMaybeQuoteQuote2() {
        assertEquals("a\\\"", CQLTermNode.maybeQuote("a\\\""));
    }

    @Test
    public void TestMaybeQuoteQuote3() {
        assertEquals("a" + "\\\\" + "\\\"", CQLTermNode.maybeQuote("a" + "\\\\" + "\""));
    }

    @Test
    public void TestMaybeQuoteQuote4() {
        assertEquals("a" + "\\\\" + "\\\"", CQLTermNode.maybeQuote("a" + "\\\\" + "\\\""));
    }

}
