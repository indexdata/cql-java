# $Id: Makefile,v 1.2 2002-11-02 01:19:23 mike Exp $

tests: sections/01/01.xcql
	./runtests CQLParser cat

sections/01/01.xcql: sections
	./mkanswers ../../srw/cql/cqlparse3
# OR	./mkanswers ../../rob/CQLParser.py

sections: mktests raw
	rm -rf sections
	./mktests raw

clean:
	find sections -name '*.xcql' -print | xargs rm -f

distclean:
	rm -rf sections

