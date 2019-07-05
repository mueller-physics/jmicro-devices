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

JFLAGS = -g -Xlint:unchecked -Xlint:deprecation -extdirs ./external -d ./
JFLAGS+= -target 1.6 -source 1.6 -bootclasspath ./external/rt-1.6.jar
#JFLAGS+= -release 6


# remove command to clean up
RM = rm -vf

.PHONY:	all org/mueller_physics/ir_tracking/git-version.txt

all:	
	$(JC) $(JFLAGS) org/mueller_physics/*/*.java


# misc rules
git-version :
	git rev-parse HEAD > org/mueller_physics/ir_tracking/git-version.txt  ; \
	git tag --contains >> org/mueller_physics/ir_tracking/git-version.txt ; \
	echo "n/a" >> org/mueller_physics/ir_tracking/git-version.txt
	 	


jar: git-version
	$(JAR) -cfm irTracking_plugin_$(shell head -c 10 org/fairsim/git-version.txt).jar \
	Manifest.txt \
	org/mueller_physics/*/*.class \
	org/mueller_physics/ir_tracking/git-version.txt \
	org/mueller_physics/ir_tracking/resources/* \


clean : clean-jtransforms
	$(RM) fairSIM_*.jar fairSIM_*.tar.bz2
	$(RM) org/fairsim/*/*.class org/fairsim/git-version.txt
	$(RM) org/fairsim/extern/*/*.class
	$(RM) -r doc/*
	$(RM) -r target

