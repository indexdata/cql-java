# $Id: Makefile,v 1.1 2002-10-25 07:38:17 mike Exp $

all: CQLNode.class CQLTermNode.class CQLBooleanNode.class \
	CQLAndNode.class CQLOrNode.class CQLNotNode.class \
	CQLParser.class

javadocs:
	nice javadoc -d ../../../../../docs -author -version \
		-windowtitle cql-java org.z3950.zing.cql

%.class: %.java
	javac $<

clean:
	rm -f *.class

cleandocs:
	rm -r docs/*
