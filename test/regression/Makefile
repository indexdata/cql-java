# $Id: Makefile,v 1.6 2002-11-20 22:50:45 mike Exp $

XMLCANONICALISER = cat
# Change this to "./xmlpp.pl" if you want to check for equivalence

test: sections/01/01.cql sections/01/01.xcql
	./runtests CQLParser $(XMLCANONICALISER)

test-adam: sections/01/01.cql sections/01/01.xcql
	./runtests ../../../srw/cql/cqlparse3 $(XMLCANONICALISER)

test-rob: sections/01/01.cql sections/01/01.xcql
	./runtests ../../../rob/CQLParser.py $(XMLCANONICALISER)

sections/01/01.cql: mktests queries.raw
	./mktests queries.raw

sections/01/01.xcql: mkanswers
	./mkanswers CQLParser
# OR	./mkanswers ../../srw/cql/cqlparse3
# OR	./mkanswers ../../rob/CQLParser.py
# Depending on which parser you want to use as your reference

clean:
	@echo "Nothing to do to 'make clean'"

distclean:
	find sections . -name '*.cql' -exec rm \{\} \;

refclean:
	find sections . -name '*.xcql' -exec rm \{\} \;
