# $Id: Makefile,v 1.3 2002-10-31 22:22:01 mike Exp $

all: Utils.class \
	CQLNode.class CQLTermNode.class CQLBooleanNode.class \
	CQLAndNode.class CQLOrNode.class CQLNotNode.class \
	CQLRelation.class CQLProxNode.class ModifierSet.class \
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
