# $Id: Makefile,v 1.4 2002-11-20 01:15:15 mike Exp $

XMLCANONICALISER = ./xmlpp.pl
# Change this to "cat" if you want to check byte-for-byte identicality

test:
	./runtests CQLParser $(XMLCANONICALISER)

test-adam: sections/01/01.xcql
	./runtests ../../../srw/cql/cqlparse3 $(XMLCANONICALISER)

test-rob: sections/01/01.xcql
	./runtests ../../../rob/CQLParser.py $(XMLCANONICALISER)

reference: sections/01/01.xcql

sections/01/01.xcql: sections
	./mkanswers CQLParser
# OR	./mkanswers ../../srw/cql/cqlparse3
# OR	./mkanswers ../../rob/CQLParser.py
# Depending on which parser you want to use as your reference

sections: mktests queries.raw
	rm -rf sections
	./mktests queries.raw

clean:
	@echo "Nothing to do to 'make clean'"

totally-clean:
	rm -rf sections

