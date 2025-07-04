package org.z3950.zing.cql;

import java.util.Properties;
import java.util.Random;
import java.io.InputStream;
import java.io.FileInputStream;

/**
 * A generator that produces random CQL queries.
 * <P>
 * Why is that useful?  Mainly to produce test-cases for CQL parsers
 * (including the <code>CQLParser</code> class in this package): you can
 * generate a random search tree, render it to XCQL and remember the
 * result.  Then decompile the tree to CQL, feed the generated CQL to
 * the parser of your choice, and check that the XCQL it comes up with
 * is the same what you got from your initial rendering.
 * <P>
 * This code is based on the same grammar as the <code>CQLParser</code> class in
 * this distribution - there is a <code>generate_<I>x</I>()</code> method
 * for each grammar element <I>X</I>.
 *
 * @see		<A href="http://zing.z3950.org/cql/index.html"
 *		        >http://zing.z3950.org/cql/index.html</A>
 */
public class CQLGenerator {
    private Properties params;
    private Random rnd;
    static private boolean DEBUG = false;

    /**
     * Creates a new CQL generator with the specified parameters.
     * @param params
     *	A <code>Properties</code> table containing configuration
     *	parameters for the queries to be generated by this generator.
     *  <P>
     *  Recognised parameters are:
     *  </P>
     *  <DL>
     *	 <DT><code>seed</code></DT>
     *	 <DD>
     *	  If specified, this is a <code>long</code> used to seed the
     *	  random number generator, so that the CQL generator can be
     *	  run repeatably, giving the same results each time.  If it's
     *	  omitted, then no seed is explicitly specified, and the
     *	  results of each run will be different (so long as you don't
     *	  run it more that 2^32 times :-)
     *   </DD>
     *	 <DT><code>complexQuery</code></DT>
     *	 <DD>
     *	  [mandatory] A floating-point number between 0.0 and 1.0,
     *	  indicating the probability for each <code>cql-query</code> node
     *	  that it will be expanded into a ``complex query''
     *	  (<code>cql-query&nbsp;boolean&nbsp;search-clause</code>) rather
     *	  than a <code>search-clause</code>.
     *   </DD>
     *	 <DT><code>complexClause</code></DT>
     *	 <DD>
     *	  [mandatory] A floating-point number between 0.0 and 1.0,
     *	  indicating the probability for each <code>search-clause</code>
     *	  node that it will be expanded into a full sub-query rather
     *	  than an <code>[ index relation ] term</code> triplet.
     *   </DD>
     *	 <DT><code>proxOp</code></DT>
     *	 <DD>
     *	  [mandatory] A floating-point number between 0.0 and 1.0,
     *	  indicating the probability that each boolean operator will
     *	  be chosen to be proximity operation; otherwise, the three
     *	  simpler boolean operations (<code>and</code>, <code>or</code> and
     *	  <code>not</code>) are chosen with equal probability.
     *   </DD>
     *	 <DT><code>equalsRelation</code></DT>
     *	 <DD>
     *	  [mandatory] A floating-point number between 0.0 and 1.0,
     *	  indicating the probability that each relation will be chosen
     *	  to be <code>=</code> - this is treated as a special case, since
     *	  it's likely to be by far the most common relation in
     *	  ``real life'' searches.
     *   </DD>
     *	 <DT><code>numericRelation</code></DT>
     *	 <DD>q
     *	  [mandatory] A floating-point number between 0.0 and 1.0,
     *	  indicating the probability that a relation, having chosen
     *	  not to be <code>=</code>, is instead chosen to be one of the six
     *	  numeric relations (<code>&lt;</code>, <code>&gt;</code>,
     *	  <code>&lt;=</code>, <code>&gt;=</code>, <code>&lt;&gt;</code> and
     *	  <code>=</code>).
     *   </DD>
     *  </DL>
     */
    public CQLGenerator(Properties params) {
	this.params = params;
	String seed = params.getProperty("seed");
	if (seed != null)
	    rnd = new Random(new Long(seed).longValue());
	else
	    rnd = new Random();
    }

    private static void debug(String str) {
	if (DEBUG)
	    System.err.println("DEBUG: " + str);
    }

    /**	
     * Generates a single random CQL query.
     * <P>
     * Uses the parameters that were associated with the generator
     * when it was created.  You are free to create as many random
     * queries as you wish from a single generator; each of them will
     * use the same parameters.
     * </P>
     * @return
     *	A <code>CQLNode</code> that is the root of the generated tree.
     *	That tree may be rendered in XCQL using its <code>toXCQL()</code>
     *	method, or decompiled into CQL using its <code>toCQL</code>
     *	method.
     */
    public CQLNode generate() throws MissingParameterException {
	return generate_cql_query();
    }

    private CQLNode generate_cql_query() throws MissingParameterException {
	if (!maybe("complexQuery")) {
	    return generate_search_clause();
	}

	CQLNode node1 = generate_cql_query();
	CQLNode node2 = generate_search_clause();
	// ### should generate prefix-mapping nodes
	if (maybe("proxOp")) {
	    // ### generate proximity nodes
	} else {
	    switch (rnd.nextInt(3)) {
	    case 0: return new CQLAndNode(node1, node2, new ModifierSet("and"));
	    case 1: return new CQLOrNode (node1, node2, new ModifierSet("or"));
	    case 2: return new CQLNotNode(node1, node2, new ModifierSet("not"));
	    }
	}

	return generate_search_clause();
    }

    private CQLNode generate_search_clause() throws MissingParameterException {
	if (maybe("complexClause")) {
	    return generate_cql_query();
	}

	// ### Should sometimes generate index/relation-free terms
	String index = generate_index();
	CQLRelation relation = generate_relation();
	String term = generate_term();

	return new CQLTermNode(index, relation, term);
    }

    // ### Should probably be more configurable
    private String generate_index() {
	String index = "";	// shut up compiler warning
	if (rnd.nextInt(2) == 0) {
	    switch (rnd.nextInt(3)) {
	    case 0: index = "dc.author"; break;
	    case 1: index = "dc.title"; break;
	    case 2: index = "dc.subject"; break;
	    }
	} else {
	    switch (rnd.nextInt(4)) {
	    case 0: index = "bath.author"; break;
	    case 1: index = "bath.title"; break;
	    case 2: index = "bath.subject"; break;
	    case 3: index = "foo>bar"; break;
	    }
	}

	return index;
    }

    private CQLRelation generate_relation() throws MissingParameterException {
	String base = generate_base_relation();
	CQLRelation rel = new CQLRelation(base);
	// ### should generate modifiers too
	return rel;
    }

    private String generate_base_relation() throws MissingParameterException {
	if (maybe("equalsRelation")) {
	    return "=";
	} else if (maybe("numericRelation")) {
	    return generate_numeric_relation();
	} else {
	    switch (rnd.nextInt(3)) {
	    case 0: return "within";
	    case 1: return "all";
	    case 2: return "any";
	    }
	}

	// NOTREACHED
	return "";		// shut up compiler warning
    }

    // ### could read candidate terms from /usr/dict/words
    // ### should introduce wildcard characters
    // ### should generate multi-word terms
    private String generate_term() {
	switch (rnd.nextInt(10)) {
	case 0: return "cat";
	case 1: return "\"cat\"";
	case 2: return "comp.os.linux";
	case 3: return "xml:element";
	case 4: return "<xml.element>";
	case 5: return "prox/word/>=/5";
	case 6: return "";
	case 7: return "frog fish";
	case 8: return "the complete dinosaur";
	case 9: return "foo*bar";
	}

	// NOTREACHED
	return "";		// shut up compiler warning
    }

    private String generate_numeric_relation() {
	switch (rnd.nextInt(6)) {
	case 0: return "<";
	case 1: return ">";
	case 2: return "<=";
	case 3: return ">=";
	case 4: return "<>";
	case 5: return "=";
	}

	// NOTREACHED
	return "";		// shut up compiler warning
    }

    boolean maybe(String param) throws MissingParameterException {
	String probability = params.getProperty(param);
	if (probability == null)
	    throw new MissingParameterException(param);

	double dice = rnd.nextDouble();
	double threshhold = new Double(probability).doubleValue();
	boolean res = dice < threshhold;
	debug("dice=" + String.valueOf(dice).substring(0, 8) +
	      " vs. " + threshhold + "='" + param + "': " + res);
	return res;
    }	


    /**
     * A simple test-harness for the generator.
     * <P>
     * It generates a single random query using the parameters
     * specified in a nominated properties file, plus any additional
     * <I>name value</I> pairs provided on the command-line, and
     * decompiles it into CQL which is written to standard output.
     * <P>
     * For example,
     * <code>java org.z3950.zing.cql.CQLGenerator
     *	etc/generate.properties seed 18398</code>,
     * where the file <code>generate.properties</code> contains:<PRE>
     *	complexQuery=0.4
     *	complexClause=0.4
     *	equalsRelation=0.5
     *	numericRelation=0.7
     *	proxOp=0.0
     * </PRE>
     * yields:<PRE>
     *	((dc.author = "&lt;xml.element&gt;") or (bath.title = cat)) and
     *		(dc.subject &gt;= "the complete dinosaur")
     * </PRE>
     * <P>
     * @param configFile
     *	The name of a properties file from which to read the
     *	configuration parameters (see above).
     * @param name
     *	The name of a configuration parameter.
     * @param value
     *	The value to assign to the configuration parameter named in
     *	the immediately preceding command-line argument.
     * @return
     *	A CQL query expressed in a form that should be comprehensible
     *	to all conformant CQL compilers.
     */
    public static void main (String[] args) throws Exception {
	if (args.length % 2 != 1) {
	    System.err.println("Usage: CQLGenerator <props-file> "+
			       "[<name> <value>]...");
	    System.exit(1);
	}

	String configFile = args[0];
	InputStream f = new FileInputStream(configFile);
	Properties params = new Properties();
	params.load(f);
	f.close();
	for (int i = 1; i < args.length; i += 2)
	    params.setProperty(args[i], args[i+1]);
        
	CQLGenerator generator = new CQLGenerator(params);
	CQLNode tree = generator.generate();
	System.out.println(tree.toCQL());
    }
}
