# $Id: Makefile,v 1.10 2002-11-12 22:38:35 mike Exp $

DOCDIR = ../../../../../docs

OBJ = Utils.class \
	CQLNode.class CQLTermNode.class CQLBooleanNode.class \
	CQLAndNode.class CQLOrNode.class CQLNotNode.class \
	CQLRelation.class CQLProxNode.class ModifierSet.class \
	CQLParser.class CQLLexer.class CQLParseException.class \
	CQLGenerator.class MissingParameterException.class \
	PQFTranslationException.class \
	UnknownQualifierException.class UnknownRelationException.class \
	UnknownRelationModifierException.class UnknownPositionException.class

../../../../../lib/cql-java.jar: $(OBJ)
	cd ../../../..; jar cf ../lib/cql-java.jar org/z3950/zing/cql/*.class

# ### FIX THIS COMMENT!
# Your Java compiler will require that this source directory is on the
# classpath.  Generally, you can use the rules below, which set the
# classpath suitably.  But that will break if you need other elements
# in the CLASSPATH too.  If that's the situation you're in, take the
# "-classpath ../../../.." flag out of the rules below, and set your
# CLASSPATH environment variable to include
#	/where/ever/you/unpacked/it/cql-java-VERSION/src
#
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
