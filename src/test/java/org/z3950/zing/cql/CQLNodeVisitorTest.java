package org.z3950.zing.cql;

import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import static java.lang.System.*;

/**
 *
 * @author jakub
 */
public class CQLNodeVisitorTest {

    public CQLNodeVisitorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testPrefixVisitor() {
        String cql = "((x=a and y=b) or z=c)";
        String epxected = "@or @and @attr 1=x a @attr 1=y b @attr 1=z c";
        CQLParser p = new CQLParser(CQLParser.V1POINT2);
        out.println("Parsing "+cql);
        try {
            CQLNode n = p.parse(cql);
            CQLNodeVisitor v = new CQLDefaultNodeVisitor() {
                String actual = "";
                String termSep = "";
                @Override
                public void onBooleanNodeStart(CQLBooleanNode node) {
                    String op = "";
                    switch (node.getOperator()) {
                        case AND: op = "@and"; break;
                        case OR:  op = "@or"; break;
                    }
                    actual += op + " ";
                }

                @Override
                public void onTermNode(CQLTermNode node) {
                    actual += termSep + "@attr 1="+node.getIndex() + " " + node.getTerm();
                    termSep = " ";
                }

                @Override
                public String toString() {
                    return actual;
                }
            };
            n.traverse(v);
            out.println(v);
            assertEquals(epxected, v.toString());
        } catch (CQLParseException ex) {
            fail(ex.getMessage());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testInfixVisitor() {
        String cql = "((x=a and y=b) or z=c)";
        String epxected = "((x:a OG y:b) ELLER z:c)";
        CQLParser p = new CQLParser(CQLParser.V1POINT2);
        out.println("Parsing "+cql);
        try {
            CQLNode n = p.parse(cql);
            CQLNodeVisitor v = new CQLDefaultNodeVisitor() {
                String actual = "";

                @Override
                public void onBooleanNodeStart(CQLBooleanNode node) {
                    actual += "(";
                }

                @Override
                public void onBooleanNodeOp(CQLBooleanNode node) {
                    String op = "";
                    switch (node.getOperator()) {
                        case AND: op = "OG"; break;
                        case OR:  op = "ELLER"; break;
                    }
                    actual += " " + op + " ";
                }

                @Override
                public void onBooleanNodeEnd(CQLBooleanNode node) {
                    actual += ")";
                }

                @Override
                public void onTermNode(CQLTermNode node) {
                    actual += node.getIndex() + ":" + node.getTerm();
                }

                @Override
                public String toString() {
                    return actual;
                }
            };
            n.traverse(v);
            out.println(v);
            assertEquals(epxected, v.toString());
        } catch (CQLParseException ex) {
            fail(ex.getMessage());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

}
