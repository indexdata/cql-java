# $Id: Makefile,v 1.2 2007-06-07 16:16:15 mike Exp $

all: ../../target/cql-java-1.8.jar
	PATH=$(PATH):../../bin CLASSPATH=../../target/cql-java-1.8.jar ./mkrandom 100

../../target/cql-java-1.8.jar:
	cd ../../ && mvn package

clean:
	@echo "Nothing to do to 'make clean'"

