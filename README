
CQL-Java - a free CQL compiler, and other CQL tools, for Java


INTRODUCTION
------------

CQL-Java is a Free Software project that provides:

* A set of classes for representing a CQL parse tree (a base CQLNode
  class, CQLBooleanNode and its subclasses, CQLTermNode, etc.)
* A CQLCompiler class (and its lexer) which builds a parse tree given
  a CQL query as input.
* A selection of compiler back-ends to render out the parse tree as:
	* XCQL (the standard XML representation)
	* CQL (i.e. decompiling the parse-tree)
	* PQF (Yaz-style Prefix Query Format)
	* BER code for the Z39.50 Type-1 query
* A random query generator, useful for testing.

CQL is "Common Query Language", a query language designed under
the umbrella of the ZING initiative (Z39.59-International Next
Generation).  The official specification is at
	http://www.loc.gov/standards/sru/cql/
and there's more (and friendlier) information at
	http://zing.z3950.org/cql/index.html

XCQL is "XML CQL", a representation of CQL-equivalent queries in XML
which is supposed to be easier to parse.  The specification is at
	http://docs.oasis-open.org/search-ws/searchRetrieve/v1.0/os/schemas/xcql.xsd
in the form of an XML Schema.

But if you didn't know that, why are you even reading this?  :-)


WHAT'S WHAT IN THIS DISTRIBUTION?
---------------------------------

	README		This file
	Changes		History of releases
	LGPL-2.1	The GNU lesser GPL (see below)
	pom.xml Maven project file to control compilation.
	src     Source-code for the CQL-Java library and tests
	target  The compiled library file, "cql-java.jar" and javadoc
	bin     Simple shell-scripts to invoke CQL programs (parser/lexer/generator)
	util    Various testing and sanity-checking Perl scripts
	etc		Other files: PQF indexes, generator properties, etc.


COMPILATION AND INSTALLATION
----------------------------

The build process is controlled by Maven so compilation is the standard:

	mvn clean install

which generates build artifacts under target/.

"Installation" of this package would consist of putting the bin
directory on your PATH and target/cql-java.jar on your CLASSPATH.


SYNOPSIS
--------

Using the test-harnesses:

	$ CQLParser 'title=foo and author=(bar or baz)'
	$ CQLParser -c 'title=foo and author=(bar or baz)'
	$ CQLParser -p /etc/pqf.properties 'dc.title=foo and dc.author=bar'
	$ CQLLexer 'title=foo and author=(bar or baz)'
		(not very interesting unless you're debugging)
	$ CQLGenerator etc/generate.properties seed 18

Using the library in your own applications:

	import org.z3950.zing.cql.*

	// Building a parse-tree by hand
	CQLNode n1 = new CQLTermNode("dc.author", new CQLRelation("="),
				     "kernighan");
	CQLNode n2 = new CQLTermNode("dc.title", new CQLRelation("all"),
				     "elements style");
	CQLNode root = new CQLAndNode(n1, n2);
	System.out.println(root.toXCQL(0));

	// Parsing a CQL query
	CQLParser parser = new CQLParser();
	CQLNode root = parser.parse("title=dinosaur");
	System.out.print(root.toXCQL(0));
	System.out.println(root.toCQL());
	System.out.println(root.toPQF(config));
	// ... where `config' specifies CQL-qualfier => Z-attr mapping


DESCRIPTION
-----------

See the automatically generated class documentation in the "target"
subdirectory.


AUTHOR
------

Original code and documentation by Mike Taylor, Index Data <mike@indexdata.com>
At present maintained by Jakub Skoczen, Index Data <jakub@indexdata.dk>

	http://www.indexdata.com/cql-java
	http://zing.z3950.org/cql

Please email me with bug-reports, wishlist items, patches, deployment
stories and, of course, large cash donations.


LICENCE
-------

The CQL-Java suite is Free Software, which is pretty much legally
equivalent -- though not morally equivalent -- to Open Source.  See
	http://www.gnu.org/philosophy/free-software-for-freedom.html
for a detailed if somewhat one-sided discussion of the differences,
and particularly of why Free Software is an important idea.

CQL-Java is distributed under version 2.1 of the LGPL (GNU LESSER
GENERAL PUBLIC LICENSE).  A copy of the licence is included in this
distribution, as the file LGPL-2.1.  This licence does not allow you
to restrict the freedom of others to use derived versions of CQL-Java
(i.e. you must share your enhancements), but does let you do pretty
much anything else with it.  In particular, you may deploy CQL-Java as
a part of a non-free larger work.


SEE ALSO
--------

Adam Dickmeiss's CQL compiler, written in C.
Rob Sanderson's CQL compiler, written in Python.
Jakub Skoczen's CQL-js compiler, written in JavaScript https://github.com/indexdata/cql-js
All the other free CQL compilers everyone's going to write  :-)
The "Changes" file, including the "Still to do" section.
