# $Id: Makefile,v 1.7 2002-11-06 00:05:58 mike Exp $

DOCDIR = ../../../../../docs

OBJ = Utils.class \
	CQLNode.class CQLTermNode.class CQLBooleanNode.class \
	CQLAndNode.class CQLOrNode.class CQLNotNode.class \
	CQLRelation.class CQLProxNode.class ModifierSet.class \
	CQLParser.class CQLLexer.class CQLParseException.class \
	CQLGenerator.class MissingParameterException.class \
	UnknownQualifierException.class UnknownRelationException.class

../../../../../lib/cql-java.jar: $(OBJ)
	cd ../../../..; jar cf ../lib/cql-java.jar org/z3950/zing/cql/*.class

docs: $(DOCDIR)/overview-tree.html

$(DOCDIR)/overview-tree.html: *.java
	nice javadoc -d $(DOCDIR) -author -version \
		-windowtitle cql-java org.z3950.zing.cql

# Your Java compiler will require that this source directory is on the
# CLASSPATH.  You can either set your CLASSPATH environment variable
# to include /where/ever/you/unpacked/it/cql-java-VERSION/src or try
# changing the rule below to:
#
#	javac -classpath ../../../.. $<
#
# (But that will break if you need other elements in the CLASSPATH
# too, for the Java library -- as, for example, Jikes does.)
#
%.class: %.java
	javac $<

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
