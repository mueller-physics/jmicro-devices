

.PHONY: clean java os-info


ifeq ($(OS), Windows_NT)
# --- We're on Windows... ---

# Java JNI includes
JNIFLAGS=-I /c/Program\ Files/Java/jdk-11.0.2/include/ -I /c/Program\ Files/Java/jdk-11.0.2/include/win32/ 

ifeq ($(MSYSTEM), MINGW64)  
# --- We're running in MINGW64, so we can statically link w. gcc

DLL=dll
OBJ=obj
CFLAGS= -O2 -fPIC -shared $(JNIFLAGS)
CCOMP=gcc
LINKER=gcc

OBJFLAG=-o 
EXEFLAG=-o 
LINKFLAG=--shared 

UEYEINC=-D _MSC_VER  # this is needed to trick the uEye code on Windows with gcc 
UEYEINC+=-I /c/Program\ Files/IDS/uEye/Develop/include/
UEYEDLL+=/c/Program\ Files/IDS/uEye/Develop/Lib/uEye_api_64.lib


else
# --- We're running something, so lets try MSVC ---

DLL=dll
OBJ=obj
CCOMP=cl
LINKER=link

# TODO: there has to be a proper way of doing this on windows....

# C libs
CINC_BASE=/c/Program\ Files\ \(x86\)/Windows\ Kits/10/Include/10.0.18362.0/
CFLAGS=-I $(CINC_BASE)/ucrt/ -I $(CINC_BASE)/um/ -I $(CINC_BASE)/shared/

# Visual studio libs(?)
CFLAGS+=-I /c/Program\ Files\ \(x86\)/Microsoft\ Visual\ Studio/2019/Community/VC/Tools/MSVC/14.21.27702/include/  

# JNI
CFLAGS+=$(JNIFLAGS)

LINKFLAG=-DLL -LIBPATH /c/Program\ Files\ \(x86\)/Windows\ Kits/10/Lib/10.0.18362.0/um/x64/uuid.lib
LINKFLAG+=-LIBPATH /c/Program\ Files\ \(x86\)/Microsoft\ Visual\ Studio/2019/Community/VC/Tools/MSVC/14.21.27702/lib/x64/libcmt.lib
LINKFLAG+=-LIBPATH /c/Program\ Files\ \(x86\)/Microsoft\ Visual\ Studio/2019/Community/VC/Tools/MSVC/14.21.27702/lib/x64/oldnames.lib
LINKFLAG+=-LIBPATH /c/Program\ Files\ \(x86\)/Microsoft\ Visual\ Studio/2019/Community/VC/Tools/MSVC/14.21.27702/lib/x64/vcruntime.lib
LINKFLAG+=-LIBPATH /c/Program\ Files\ \(x86\)/Microsoft\ Visual\ Studio/2019/Community/VC/Tools/MSVC/14.21.27702/lib/x64/libvcruntime.lib
LINKFLAG+=-LIBPATH /c/Program\ Files\ \(x86\)/Windows\ Kits/10/Lib/10.0.18362.0/um/x64/kernel32.Lib
LINKFLAG+=-LIBPATH /c/Program\ Files\ \(x86\)/Windows\ Kits/10/Lib/10.0.18362.0/ucrt/x64/libucrt.lib

# Output flag
OBJFLAG=-Fo
EXEFLAG=-OUT:

UEYEINC=-I /c/Program\ Files/IDS/uEye/Develop/include/
UEYEDLL=-LIBPATH /c/Program\ Files/IDS/uEye/Develop/Lib/uEye_api_64.lib
#UEYEDLL=-LIBPATH /c/Program\ Files/IDS/uEye/Develop/Lib/uEye_api.lib
endif

else
# --- ok, LINUX SETTINGS ---

DLL=so
OBJ=o
JNIFLAGS= -I /usr/lib/jvm/java-8-openjdk-amd64/include/ -I /usr/lib/jvm/java-8-openjdk-amd64/include/linux/
CFLAGS= -O2 -fPIC -shared $(JNIFLAGS)
CCOMP=gcc
LINKER=gcc

OBJFLAG=-o 
EXEFLAG=-o 
LINKFLAG=--shared 

UEYEDLL=-lueye_api

endif


all: cam_connect_ids.$(DLL)

headers: org_mueller_physics_device_connect_CameraConnect_IDS.h

os-info:
	@echo $(OS)

# general .c --> .so rule
%.$(OBJ):%.c
	$(CCOMP) -c $(OBJFLAG)./$@ $< $(CFLAGS) 

# clean
clean:
	$(RM) *.so *.obj *.o *.dll *.exp *.lib org_mueller_physics_*.h


# IDS header files
org_mueller_physics_device_connect_CameraConnect_IDS.h: ../org/mueller_physics/device_connect/CameraConnect_IDS.java
	javac -Xlint:deprecation -cp ../ -h ./ $<
	touch $@

# IDS object
cam_connect_ids.c: org_mueller_physics_device_connect_CameraConnect_IDS.h

cam_connect_ids.$(OBJ): cam_connect_ids.c
	$(CCOMP) -c $(OBJFLAG)./$@ $< $(CFLAGS) $(UEYEINC) 

# IDS connector library
cam_connect_ids.$(DLL): cam_connect_ids.$(OBJ) jni_helpers.$(OBJ)
	$(LINKER)  $(EXEFLAG)$@ $^ $(LINKFLAG) $(UEYEDLL) 


