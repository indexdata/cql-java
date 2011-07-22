# $Id: Makefile,v 1.2 2007-06-07 16:16:15 mike Exp $

../../target/cql-java-1.7.jar:
	cd ../../ && mvn compile

all: ../../target/cql-java-1.7.jar
	PATH=$(PATH):../../bin CLASSPATH=../../target/cql-java-1.7.jar ./mkrandom 100

clean:
	@echo "Nothing to do to 'make clean'"

