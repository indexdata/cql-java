# $Id: Makefile,v 1.11 2002-11-14 22:04:16 mike Exp $
#
# Your Java compiler, and javadoc, will require that this source
# directory is on the classpath.  The best way to do that is just to
# add the cql-java distribution's "src" subdirectory to your CLASSPATH
# environment variable, like this:
#	CLASSPATH=$CLASSPATH:/where/ever/you/unpacked/it/cql-java-VERSION/src

DOCDIR = ../../../../../docs

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

docs: $(DOCDIR)/overview-tree.html

$(DOCDIR)/overview-tree.html: *.java
	nice javadoc -d $(DOCDIR) -author -version \
		-windowtitle cql-java org.z3950.zing.cql

clean:
	rm -f $(OBJ) 'CQLLexer$$Keyword.class'

cleandocs:
	rm -rf	$(DOCDIR)/allclasses-frame.html \
		$(DOCDIR)/deprecated-list.html \
		$(DOCDIR)/help-doc.html \
		$(DOCDIR)/index-all.html \
		$(DOCDIR)/index.html \
		$(DOCDIR)/org \
		$(DOCDIR)/overview-tree.html \
		$(DOCDIR)/package-list \
		$(DOCDIR)/packages.html \
		$(DOCDIR)/serialized-form.html \
		$(DOCDIR)/stylesheet.css
