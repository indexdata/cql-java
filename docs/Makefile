# $Id: Makefile,v 1.2 2007-06-06 13:20:44 mike Exp $

all: overview-tree.html

overview-tree.html: ../src/org/z3950/zing/cql/*.java
	nice javadoc -classpath ../src \
		-author -version -windowtitle cql-java org.z3950.zing.cql

clean:
	rm -rf	allclasses-frame.html \
		allclasses-noframe.html \
		deprecated-list.html \
		help-doc.html \
		index-all.html \
		index.html \
		org \
		overview-tree.html \
		package-list \
		packages.html \
		serialized-form.html \
		constant-values.html \
		stylesheet.css \
		resources
