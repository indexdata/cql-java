# $Id: Makefile,v 1.1 2002-11-01 23:45:28 mike Exp $

sections/01/01.xcql: sections
#	./mkanswers ../../rob/CQLParser.py
	./mkanswers ../../srw/cql/cqlparse3

sections: mktests raw
	rm -rf sections
	./mktests raw

clean:
	find sections -name '*.xcql' -print | xargs rm -f

distclean:
	rm -rf sections

