# $Id: Makefile,v 1.3 2002-11-03 16:49:38 mike Exp $

sections/01/01.xcql: sections
	./mkanswers CQLParser
# OR	./mkanswers ../../srw/cql/cqlparse3
# OR	./mkanswers ../../rob/CQLParser.py

sections: mktests queries.raw
	rm -rf sections
	./mktests queries.raw

adam-tests: sections/01/01.xcql
	./runtests ../../srw/cql/cqlparse3

rob-tests: sections/01/01.xcql
	./runtests ../../rob/CQLParser.py

clean:
	find sections -name '*.xcql' -print | xargs rm -f

distclean:
	rm -rf sections

