# $Id: Makefile,v 1.13 2002-12-09 16:29:44 mike Exp $
#
# Your Java compiler will require that this source directory is on the
# classpath.  The best way to do that is just to add the CQL-Java
# distribution's "src" subdirectory to your CLASSPATH environment
# variable, like this:
#	CLASSPATH=$CLASSPATH:/where/ever/you/unpacked/it/cql-java-VERSION/src

OBJ = Utils.class \
	CQLNode.class CQLTermNode.class CQLBooleanNode.class \
	CQLAndNode.class CQLOrNode.class CQLNotNode.class \
	CQLProxNode.class CQLPrefixNode.class CQLPrefix.class \
	CQLRelation.class ModifierSet.class \
	CQLParser.class CQLLexer.class CQLGenerator.class \
	CQLParseException.class MissingParameterException.class \
	PQFTranslationException.class \
	UnknownQualifierException.class UnknownRelationException.class \
	UnknownRelationModifierException.class UnknownPositionException.class

../../../../../lib/cql-java.jar: $(OBJ)
	cd ../../../..; jar cf ../lib/cql-java.jar org/z3950/zing/cql/*.class

%.class: %.java
	javac $<

clean:
	rm -f $(OBJ) 'CQLLexer$$Keyword.class'

