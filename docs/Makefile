# $Id: Makefile,v 1.1 2002-11-17 23:29:55 mike Exp $

all: overview-tree.html

overview-tree.html: ../src/org/z3950/zing/cql/*.java
	nice javadoc -classpath ../src \
		-author -version -windowtitle cql-java org.z3950.zing.cql

clean:
	rm -rf	allclasses-frame.html \
		deprecated-list.html \
		help-doc.html \
		index-all.html \
		index.html \
		org \
		overview-tree.html \
		package-list \
		packages.html \
		serialized-form.html \
		stylesheet.css
