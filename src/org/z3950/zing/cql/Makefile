# $Id: Makefile,v 1.5 2002-11-03 17:02:48 mike Exp $

OBJ = Utils.class \
	CQLNode.class CQLTermNode.class CQLBooleanNode.class \
	CQLAndNode.class CQLOrNode.class CQLNotNode.class \
	CQLRelation.class CQLProxNode.class ModifierSet.class \
	CQLParser.class CQLLexer.class CQLParseException.class \
	CQLGenerator.class ParameterMissingException.class

../../../../../lib/cql-java.jar: $(OBJ)
	cd ../../../..; jar cf ../lib/cql-java.jar org/z3950/zing/cql/*.class

docs: ../../../../../docs/overview-tree.html

../../../../../docs/overview-tree.html: *.java
	nice javadoc -d ../../../../../docs -author -version \
		-windowtitle cql-java org.z3950.zing.cql

%.class: %.java
	javac $<

clean:
	rm -f $(OBJ) 'CQLLexer$$Keyword.class'

cleandocs:
	rm -rf \
		../../../../../docs/allclasses-frame.html \
		../../../../../docs/deprecated-list.html \
		../../../../../docs/help-doc.html \
		../../../../../docs/index-all.html \
		../../../../../docs/index.html \
		../../../../../docs/org \
		../../../../../docs/overview-tree.html \
		../../../../../docs/package-list \
		../../../../../docs/packages.html \
		../../../../../docs/serialized-form.html \
		../../../../../docs/stylesheet.css
