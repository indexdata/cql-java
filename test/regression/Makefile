# $Id: Makefile,v 1.2 2002-11-06 22:03:58 mike Exp $

sections/01/01.xcql: sections
	./mkanswers CQLParser
# OR	./mkanswers ../../srw/cql/cqlparse3
# OR	./mkanswers ../../rob/CQLParser.py

sections: mktests queries.raw
	rm -rf sections
	./mktests queries.raw

adam-tests: sections/01/01.xcql
	./runtests ../../../srw/cql/cqlparse3 cat

rob-tests: sections/01/01.xcql
	./runtests ../../../rob/CQLParser.py cat

clean:
	find sections -name '*.xcql' -print | xargs rm -f

distclean:
	rm -rf sections

