package org.z3950.zing.cql;

import org.junit.Test;
import static org.junit.Assert.*;

public class CQLSourcePositionsTest {
    private static void span(String query, String expected, CQLNode node) {
        assertEquals(expected, query.substring(node.getStart(), node.getEnd()));
        assertEquals(query.indexOf(expected), node.getStart());
    }

    @Test
    public void preservesOriginalText() throws Exception {
        CQLParser parser = new CQLParser();
        for (String text : new String[] {
                "cat   dog", "\"cat\" \"dog\"", "\"\"", "\"a\\\"b\"",
                "cat\t\r\ndog", "\uD83D\uDE00   fish", "((cat))",
                "title = (cat or dog)", "cat sortBy title",
                ">dc=uri cat", "cat or >dc=uri dog", "title = >dc=uri dog"}) {
            String query = " \t" + text + " \r\n";
            span(query, text, parser.parse(query));
        }
    }

    @Test
    public void nestedOperandsAndInheritedRelation() throws Exception {
        String query = " title = (cat or (dog and fish)) ";
        CQLBooleanNode root = (CQLBooleanNode) new CQLParser().parse(query);
        span(query, query.trim(), root);
        CQLTermNode cat = (CQLTermNode) root.getLeftOperand();
        span(query, "cat", cat);
        span(query, "=", cat.getRelation());
        CQLBooleanNode right = (CQLBooleanNode) root.getRightOperand();
        span(query, "(dog and fish)", right);
        span(query, "dog", right.getLeftOperand());
        span(query, "fish", right.getRightOperand());
    }

    @Test
    public void relationsAndModifiers() throws Exception {
        for (String relation : new String[] {"=", "==", "<", ">", "<=", ">=", "<>", "any", "cql.any"}) {
            String query = "title " + relation + "/relevant /distance <= \"3\" fish ";
            CQLTermNode term = (CQLTermNode) new CQLParser().parse(query);
            span(query, relation + "/relevant /distance <= \"3\"", term.getRelation());
            Modifier simple = term.getRelation().getModifiers().get(0);
            assertEquals("/relevant", query.substring(simple.getStart(), simple.getEnd()));
            Modifier complex = term.getRelation().getModifiers().get(1);
            assertEquals("/distance <= \"3\"", query.substring(complex.getStart(), complex.getEnd()));
        }
    }

    @Test
    public void prefixSortAndBooleanModifiers() throws Exception {
        String query = " >dc=uri cat prox/distance<=3 dog sortBy title/sort.ascending date ";
        CQLPrefixNode prefix = (CQLPrefixNode) new CQLParser().parse(query);
        span(query, query.trim(), prefix);
        CQLSortNode sort = (CQLSortNode) prefix.getSubtree();
        span(query, "cat prox/distance<=3 dog sortBy title/sort.ascending date", sort);
        CQLBooleanNode bool = (CQLBooleanNode) sort.getSubtree();
        span(query, "cat prox/distance<=3 dog", bool);
        Modifier modifier = bool.getModifiers().get(0);
        assertEquals("/distance<=3", query.substring(modifier.getStart(), modifier.getEnd()));
        ModifierSet key = sort.getSortIndexes().get(0);
        assertEquals("title/sort.ascending", query.substring(key.getStart(), key.getEnd()));
        key = sort.getSortIndexes().get(1);
        assertEquals("date", query.substring(key.getStart(), key.getEnd()));
    }

    @Test
    public void unknownLocations() throws Exception {
        CQLTermNode parsed = (CQLTermNode) new CQLParser().parse("fish");
        CQLNode[] nodes = {new CQLTermNode("title", new CQLRelation("="), "fish"),
                parsed.getRelation()};
        for (CQLNode node : nodes) {
            assertEquals(-1, node.getStart());
            assertEquals(-1, node.getEnd());
        }
        ModifierSet set = new ModifierSet("any");
        set.addModifier("relevant");
        set.addModifier("distance", "<", "3");
        assertEquals(-1, set.getStart());
        assertEquals(-1, set.getEnd());
        for (Modifier modifier : set.getModifiers()) {
            assertEquals(-1, modifier.getStart());
            assertEquals(-1, modifier.getEnd());
        }
    }

    @Test
    public void lexerOffsetsAndLegacyPosition() {
        String query = "  \"\uD83D\uDE00\" <= cat  ";
        CQLLexer lexer = new CQLLexer(query, false);
        assertEquals(-1, lexer.getStart());
        assertEquals(-1, lexer.getEnd());
        assertEquals(0, lexer.pos());
        for (String token : new String[] {"\"\uD83D\uDE00\"", "<=", "cat"}) {
            lexer.move();
            assertEquals(token, query.substring(lexer.getStart(), lexer.getEnd()));
            assertEquals(query.indexOf(token), lexer.getStart());
            assertEquals(lexer.pos(), lexer.getEnd());
        }
        lexer.move();
        assertEquals(CQLTokenizer.TT_EOF, lexer.what());
        assertEquals(query.length(), lexer.getStart());
        assertEquals(query.length(), lexer.getEnd());
        lexer.move();
        assertEquals(query.length(), lexer.getEnd());
    }
}
