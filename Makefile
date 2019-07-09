#
# IR tracking make file
#
# To work, either 'java6' has to point to a java 
# compiler (vers. 1.6), or change the line below to
# 'java' instead of 'java6'

JC = javac
JAR = jar

# Options for the java compiler
EXTDIR="./external"

JFLAGS = -g -Xlint:unchecked -Xlint:deprecation -cp ./external/*:./ 
JFLAGS+= -target 1.6 -source 1.6 -bootclasspath ./external/rt-1.6.jar


# remove command to clean up
RM = rm -vf

.PHONY:	all bridge bridgeheaders org/mueller_physics/ir_tracking/git-version.txt bridge java



all:	bridgeheaders java	
native_all: bridge java 

java:		
	$(JC) $(JFLAGS) -d ./ org/mueller_physics/*/*.java

bridgeheaders:
	make -C bridgelibs headers
	
bridge:
	make -C bridgelibs all 

# misc rules
git-version :
	git rev-parse HEAD > org/mueller_physics/git-version.txt  ; \
	git tag --contains >> org/mueller_physics/git-version.txt ; \
	echo "n/a" >> org/mueller_physics/git-version.txt
	 	


jar: git-version
	$(JAR) -cfm irTracking_plugin_$(shell head -c 10 org/mueller_physics/git-version.txt).jar \
	Manifest.txt \
	org/mueller_physics/*/*.class \
	org/mueller_physics/git-version.txt \
	org/mueller_physics/resources/* \


clean :
	make -C bridgelibs clean 
	$(RM) irTracking_plugin_*.tar.bz2
	$(RM) org/mueller_physics/*/*.class org/mueller_physics/git-version.txt
	$(RM) -r doc/*
	$(RM) -r target

