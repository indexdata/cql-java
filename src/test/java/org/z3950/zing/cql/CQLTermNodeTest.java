package org.z3950.zing.cql;

import org.junit.Test;
import static org.junit.Assert.*;

public class CQLTermNodeTest {
    @Test
    public void TestTCQLTermQuoteNull() {
        assertNull(CQLTermNode.toCQLTerm(null));
    }

    @Test
    public void TestTCQLTermQuoteEmpty() {
        assertEquals("\"\"", CQLTermNode.toCQLTerm(""));
    }

    @Test
    public void TestTCQLTermQuoteRelation() {
        assertEquals("\"<\"", CQLTermNode.toCQLTerm("<"));
    }

    @Test
    public void TestTCQLTermQuoteSimple() {
        assertEquals("simple", CQLTermNode.toCQLTerm("simple"));
    }

    @Test
    public void TestTCQLTermQuoteBlank() {
        assertEquals("\"a b\"", CQLTermNode.toCQLTerm("a b"));
    }

    @Test
    public void TestTCQLTermQuoteQuote1() {
        assertEquals("a\\\"", CQLTermNode.toCQLTerm("a\""));
    }

    @Test
    public void TestTCQLTermQuoteQuote2() {
        assertEquals("a\\\"", CQLTermNode.toCQLTerm("a\\\""));
    }

    @Test
    public void TestTCQLTermQuoteQuote3() {
        assertEquals("a" + "\\\\" + "\\\"", CQLTermNode.toCQLTerm("a" + "\\\\" + "\""));
    }

    @Test
    public void TestTCQLTermQuoteQuote4() {
        assertEquals("a" + "\\\\" + "\\\"", CQLTermNode.toCQLTerm("a" + "\\\\" + "\\\""));
    }

    @Test
    public void TestTCQLTermQuoteBackSlashTrail() {
        assertEquals("a\\", CQLTermNode.toCQLTerm("a\\"));
    }

}
