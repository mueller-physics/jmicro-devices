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
failcmd ar
failcmd xz
failcmd tar
failcmd 7z

# Get the ImageJ base library (in version 1.48, which is the lowest we currently support)
if [ ! -e ij148v.jar ] ; then
    fileMissing=1
    wget https://imagej.nih.gov/ij/download/jars/ij148v.jar
fi

# Get the fairSIM plugin
if [ ! -e fairSIM_v1.4.2-beta1.jar ] ; then
    fileMissing=1
    wget https://github.com/fairSIM/fairSIM/releases/download/v1.4.2-beta1/fairSIM_plugin.jar -O fairSIM_v1.4.2-beta1.jar 
fi

# Get the serial comm library
if [ ! -e nrjavaserial-3.15.0.jar ] ; then
    fileMissing=1
    wget https://github.com/NeuronRobotics/nrjavaserial/releases/download/3.15.0/nrjavaserial-3.15.0.jar
fi

# This fetches the java 1.6 runtime, needed for backwards-compatible
# compilation to Java 6 with newer compilers
#   unfortunately, Java 6's rt.jar does not seem to be on Maven, so
#   fetch it by extracting it from Ubuntu's openjdk deb.
#   Java 9 will make it much more easier with the "-release" option
if [ ! -e rt-1.6.jar ] ; then
    fileMissing=1

    mkdir tmp-rt-jar
    cd tmp-rt-jar

    # download the 'openjdk-6-jre-headless' deb file
    wget http://security.ubuntu.com/ubuntu/pool/universe/o/openjdk-6/openjdk-6-jre-headless_6b41-1.13.13-0ubuntu0.14.04.1_amd64.deb -O openjdk.deb


    # extract the deb
    echo "Extracting rt.jar from the .deb file"
    echo "This might take a few moments... "
    ar -x openjdk.deb

    # extract the rt.jar from the data.tar
    xz -d data.tar.xz
    tar -xf data.tar ./usr/lib/jvm/java-6-openjdk-amd64/jre/lib/rt.jar
    mv ./usr/lib/jvm/java-6-openjdk-amd64/jre/lib/rt.jar ../rt-1.6.jar
    cd ..

    rm -rf tmp-rt-jar
    echo "Done."
else
    echo "found Java 1.6 runtime"
fi


# This extracts the MicroManager jars we need to compile the camera
# plugin. It requires '7z' from p7zip to be installed
if [ ! -e MMCoreJ.jar -o ! -e MMJ_.jar ] ; then
    fileMissing=1

    # downloading the micromanager install dmg (easier to handle than the exe)
    wget -c "http://valelab4.ucsf.edu/~MM/builds/1.4/Mac/Micro-Manager1.4.22.dmg"

    mkdir tmp-mm-jar
    cd tmp-mm-jar

    # extract the dmg file
    7z x ../Micro-Manager1.4.22.dmg
    
    # unpack the files from the image 
    7z x 2.hfs
    
    # 'MMCoreJ.jar' and 'MMJ_.jar' from that exe file
    cp Micro-Manager/Micro-Manager1.4/plugins/Micro-Manager/MMCoreJ.jar ../
    cp Micro-Manager/Micro-Manager1.4/plugins/Micro-Manager/MMJ_.jar ../

    # delete all the unused stuff
    cd ..
    rm -rf tmp-mm-jar
else
    echo "found MicroManager-jars"
fi



if [ $fileMissing -eq 0 ] ; then
    echo "All files found, jmicro-devices should compile"
fi

