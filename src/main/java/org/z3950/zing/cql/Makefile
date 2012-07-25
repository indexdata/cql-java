#
# Your Java compiler will require that this source directory is on the
# classpath.  The best way to do that is just to add the CQL-Java
# distribution's "src" subdirectory to your CLASSPATH environment
# variable, like this:
#	CLASSPATH=$CLASSPATH:/where/ever/you/unpacked/it/cql-java-VERSION/src

OBJ = Utils.class \
	CQLNode.class CQLTermNode.class CQLBooleanNode.class \
	CQLAndNode.class CQLOrNode.class CQLNotNode.class \
	CQLProxNode.class CQLPrefixNode.class CQLSortNode.class \
	CQLPrefix.class \
	CQLRelation.class Modifier.class ModifierSet.class \
	CQLParser.class CQLLexer.class CQLGenerator.class \
	CQLParseException.class MissingParameterException.class \
	PQFTranslationException.class \
	UnknownIndexException.class UnknownRelationException.class \
	UnknownRelationModifierException.class UnknownPositionException.class

JARPATH = ../../../lib/cql-java.jar
JAR = ../../../../../../$(JARPATH)
$(JAR): $(OBJ)
	cd ../../../..; jar cf $(JARPATH) org/z3950/zing/cql/*.class

%.class: %.java
	javac -Xlint:unchecked *.java

test: $(JAR)
	cd ../../../../../../../test/regression && make

clean:
	rm -f $(OBJ) 'CQLLexer$$Keyword.class'

