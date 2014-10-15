/*
 * Copyright (c) 1995-2014, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */

package org.z3950.zing.cql.sparql;

import java.io.IOException;
import static java.lang.System.out;
import static org.junit.Assert.*;

import org.junit.Test;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.utils.PrettyPrinter;

/**
 *
 * @author jakub
 */
public class QueryTest {
  
  @Test
  public void testSimpleQuery() {
    Query query = new Query().prefix("bf", "http://bibframe.org/vocab/")
      .form(new Select().var("?title")).where(
        new Where(new TriplePattern("?work", "bf:title", "?title")));
    String expected = 
        "PREFIX bf: <http://bibframe.org/vocab/>\n"
      + "SELECT ?title\n" 
      + "WHERE {\n" 
      + "  ?work bf:title ?title .\n" 
      + "}\n";
    PrettyPrinter writer = new PrettyPrinter();
    query.print(writer);
    String sparql = writer.toString();
    out.println(sparql);
    assertEquals(expected, sparql);
  }
  
  @Test
  public void testComplexQuery() {
    Query query = new Query().prefix("bf", "http://bibframe.org/vocab/")
      .form(new Select().var("?wtitle").var("?ititle"))
      .where(
        new Where(
          new TriplePattern("?work", "bf:title", "?title"), 
          new Optional(new TriplePattern("?ins", "?bf:instanceOf", "?work")))
      );
    String expected = 
        "PREFIX bf: <http://bibframe.org/vocab/>\n"
      + "SELECT ?wtitle ?ititle\n" 
      + "WHERE {\n" 
      + "  ?work bf:title ?title .\n"
      + "  OPTIONAL {\n"
      + "    ?ins ?bf:instanceOf ?work .\n"
      + "  }\n"
      + "}\n";
    PrettyPrinter pp = new PrettyPrinter();
    query.print(pp);
    String sparql = pp.toString();
    out.println(sparql);
    assertEquals(expected, sparql);
  }
  
  @Test
  public void testCQLConversionTitle() throws CQLParseException, IOException {
    CQLParser p = new CQLParser();
    CQLNode query = p.parse("bf:title = \"al gore\"");
    SPARQLNodeVisitor visitor = new SPARQLNodeVisitor();
    query.traverse(visitor);
    Query sparql = visitor.getQuery();
    PrettyPrinter pp = new PrettyPrinter();
    sparql.print(pp);
    String expected = 
      "PREFIX bf: <http://bibframe.org/vocab/>\n" +
      "SELECT *\n" +
      "WHERE {\n" +
      "  ?work a bf:Work .\n" +
      "  ?work bf:title ?o1 .\n" +
      "  FILTER contains(lcase(?o1), \"al gore\").\n" +
      "}\n";
    out.println(pp);
    assertEquals(expected, pp.toString());
  }
  
  @Test
  public void testCQLConversionTitleAuthor() throws CQLParseException, IOException {
    CQLParser p = new CQLParser();
    CQLNode query = p.parse("bf:title = \"al gore\" and \"bf:creator/bf:label\" = \"Stefoff, Rebecca\"");
    SPARQLNodeVisitor visitor = new SPARQLNodeVisitor();
    query.traverse(visitor);
    Query sparql = visitor.getQuery();
    PrettyPrinter pp = new PrettyPrinter();
    sparql.print(pp);
    String expected = 
      "PREFIX bf: <http://bibframe.org/vocab/>\n" +
      "SELECT *\n" +
      "WHERE {\n" +
      "  ?work a bf:Work .\n" +
      "  ?work bf:title ?o1 .\n" +
      "  FILTER contains(lcase(?o1), \"al gore\").\n" +
      "  ?work bf:creator/bf:label ?o2 .\n" +
      "  FILTER contains(lcase(?o2), \"stefoff, rebecca\").\n" +
      "}\n";
    out.println(pp);
    assertEquals(expected, pp.toString());
  }
  
}
