# $Id: Makefile,v 1.2 2002-10-30 09:19:26 mike Exp $

all: CQLNode.class CQLTermNode.class CQLBooleanNode.class \
	CQLAndNode.class CQLOrNode.class CQLNotNode.class \
	CQLRelation.class \
	CQLParser.class CQLLexer.class CQLParseException.class \
	CQLGenerator.class ParameterMissingException.class

docs:
	nice javadoc -d ../../../../../docs -author -version \
		-windowtitle cql-java org.z3950.zing.cql

%.class: %.java
	javac $<

clean:
	rm -f *.class

cleandocs:
	rm -r docs/*
