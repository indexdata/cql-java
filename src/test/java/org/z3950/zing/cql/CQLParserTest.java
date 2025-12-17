package org.z3950.zing.cql;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static java.lang.System.out;

/**
 *
 * @author jakub
 */
public class CQLParserTest {
    public CQLParserTest() {
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

    /**
     * Test of main method, of class CQLParser.
     */
    @Test
    public void testRegressionQueries() throws IOException {
        System.out.println("Testing the parser using pre-canned regression queries...");
        // we might be running the test from within the jar
        // list all resource dirs, then traverse them
        String[] dirs = getResourceListing(this.getClass(), "regression");
        for (String dir : dirs) {
            String files[] = getResourceListing(this.getClass(), "regression/" + dir);
            for (String file : files) {
                if (!file.endsWith(".cql"))
                    continue;
                out.println("Parsing " + dir + "/" + file);
                InputStream is = this.getClass().getResourceAsStream("/regression/" + dir + "/" + file);
                BufferedReader reader = null, reader2 = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(is));
                    String input = reader.readLine();
                    out.println("Query: " + input);
                    String result;
                    try {
                        CQLParser parser = new CQLParser();
                        CQLNode parsed = parser.parse(input);
                        result = parsed.toXCQL();
                    } catch (CQLParseException pe) {
                        result = pe.getMessage() + "\n";
                    }
                    out.println("Parsed:");
                    out.println(result);
                    // read the expected xcql output
                    String expected = "<expected result file not found>";
                    String prefix = file.substring(0, file.length() - 4);
                    InputStream is2 = this.getClass()
                            .getResourceAsStream("/regression/" + dir + "/" + prefix + ".xcql");
                    if (is2 != null) {
                        reader2 = new BufferedReader(new InputStreamReader(is2));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader2.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        expected = sb.toString();
                    }
                    out.println("Expected: ");
                    out.println(expected);
                    assertEquals("Assertion failure for " + dir + "/" + file, expected, result);
                } finally {
                    if (reader != null)
                        reader.close();
                    if (reader2 != null)
                        reader2.close();
                }
            }
        }
    }

    /**
     * Test the integrity of the parser as follows:
     * - Generate a random tree with CQLGenerator
     * - Serialize it
     * - Canonicalise it by running through the parser
     * - Compare the before-and-after versions.
     * Since the CQLGenerator output is in canonical form anyway, the
     * before-and-after versions should be identical. This process exercises
     * the comprehensiveness and bullet-proofing of the parser, as well as
     * the accuracy of the rendering.
     *
     * @throws IOException
     * @throws MissingParameterException
     */
    @Test
    public void testRandomQueries() throws IOException, MissingParameterException {
        out.println("Testing the parser using 100 randomly generated queries...");
        Properties params = new Properties();
        InputStream is = getClass().getResourceAsStream("/generate.properties");
        if (is == null)
            fail("Cannot locate generate.properties");
        params.load(is);
        is.close();
        CQLGenerator generator = new CQLGenerator(params);
        for (int i = 0; i < 1000; i++) {
            CQLNode random = generator.generate();
            String expected = random.toCQL();
            out.println("Generated query: " + expected);
            CQLParser parser = new CQLParser();
            try {
                CQLNode parsed = parser.parse(expected);
                String result = parsed.toCQL();
                assertEquals(expected, result);
            } catch (CQLParseException pe) {
                fail("Generated query failed to parse: " + pe.getMessage());
            }
        }
    }

    // helper methods follow
    // TODO move to masterkey-common

    @SuppressWarnings("rawtypes")
    public static String[] getResourceListing(Class clazz, String path) throws IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            /* A file path: easy enough */
            try {
                return new File(dirURL.toURI()).list();
            } catch (URISyntaxException use) {
                throw new UnsupportedOperationException(use);
            }
        }

        if (dirURL == null) {
            /*
             * In case of a jar file, we can't actually find a directory.
             * Have to assume the same jar as clazz.
             */
            String me = clazz.getName().replace(".", "/") + ".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }

        if (dirURL.getProtocol().equals("jar")) {
            /* A JAR path */
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf(
                    "!")); // strip out only the JAR file
            try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))) {
                Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
                Set<String> result = new HashSet<String>(); // avoid duplicates in case it is a subdirectory
                while (entries.hasMoreElements()) {
                    String name = entries.nextElement().getName();
                    if (name.startsWith(path)) { // filter according to the path
                        String entry = name.substring(path.length());
                        int checkSubdir = entry.indexOf("/");
                        if (checkSubdir >= 0) {
                            // if it is a subdirectory, we just return the directory name
                            entry = entry.substring(0, checkSubdir);
                        }
                        result.add(entry);
                    }
                }
                return result.toArray(new String[result.size()]);
            }
        }
        throw new UnsupportedOperationException("Cannot list files for URL "
                + dirURL);
    }
}
