#!/bin/bash

fileMissing=0

# Check if we have all commands installed that this script needs

function failcmd() {
    if [ -x "$( command -v $1 )" ] ; then
	return 0
    else
	1>&2 echo "Command \"$1\" not found, please install"
	exit -1
    fi
}

failcmd wget
#failcmd ar
#failcmd xz
#failcmd tar

# Get the ImageJ base library (in version 1.48, which is the lowest we currently support)
if [ ! -e ij148v.jar ] ; then
    fileMissing=1
    wget https://imagej.nih.gov/ij/download/jars/ij148v.jar
fi

if [ ! -e fairSIM_v1.4.2-beta1.jar ] ; then
    fileMissing=1
    wget https://github.com/fairSIM/fairSIM/releases/download/v1.4.2-beta1/fairSIM_plugin.jar -O fairSIM_v1.4.2-beta1.jar 
fi



if [ $fileMissing -eq 0 ] ; then
    echo "All files found, fairSIM should compile"
fi

