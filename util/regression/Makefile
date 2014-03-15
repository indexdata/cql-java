
XMLCANONICALISER = cat
# Change this to "./xmlpp.pl" if you want to check for equivalence

test: sections/01/01.cql sections/01/01.xcql
	./runtests ../../bin/CQLParser $(XMLCANONICALISER)

test-adam: sections/01/01.cql sections/01/01.xcql
	./runtests ../../../srw/cql/cql2xcql $(XMLCANONICALISER)

test-rob: sections/01/01.cql sections/01/01.xcql
	./runtests ../../../rob/CQLParser.py $(XMLCANONICALISER)

sections/01/01.cql: mktests queries.raw
	./mktests queries.raw

sections/01/01.xcql: mkanswers
	./mkanswers ../../bin/CQLParser
# OR	./mkanswers ../../../srw/cql/cql2xcql
# OR	./mkanswers ../../../rob/CQLParser.py
# Depending on which parser you want to use as your reference

clean:
	@echo "Nothing to do to 'make clean'"

distclean:
	find sections . -name '*.cql' -exec rm \{\} \;

refclean:
	find sections . -name '*.xcql' -exec rm \{\} \;
