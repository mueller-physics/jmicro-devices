

.PHONY: clean java os-info


ifeq ($(OS), Windows_NT)
# --- We're on Windows... ---

$(info Running on Windows)

# include the windows-specific path settings
include Makefile-path-windows

JNIFLAGS= -I $(JNIPATH) -I $(JNIPATH_W32) 


$(info Build system: $(MSYSTEM) )

ifeq ($(MSYSTEM), MINGW64)  
# --- We're running in MINGW64, so we can statically link w. gcc

DLL=dll
OBJ=obj
CFLAGS= -O2 -fPIC -shared $(JNIFLAGS) -D__int64=int64_t
CCOMP=gcc
LINKER=gcc

OBJFLAG=-o 
EXEFLAG=-o 
LINKFLAG=--shared 

UEYEINC=-D _MSC_VER  # this is needed to trick the uEye code on Windows with gcc 
UEYEINC+=-I $(UEYEINC_PATH)
UEYEDLL+=$(UEYELIB_PATH)/uEye_api_64.lib


else
# --- We're running something, so lets try MSVC ---

$(info Build system is not 'MINGW64', so using VC tools)

DLL=dll
OBJ=obj
CCOMP=$(VCBIN_PATH)/cl
LINKER=$(VCBIN_PATH)/link

# TODO: there has to be a more proper way of doing this on windows....

# C libs
CFLAGS=-I $(WKINC_BASE)/ucrt/ -I $(WKINC_BASE)/um/ -I $(WKINC_BASE)/shared/

# Visual studio libs(?) include
CFLAGS+=-I $(VCINC_BASE) 

# JNI
CFLAGS+=$(JNIFLAGS)

LINKFLAG=-DLL 

# from Windows Kits
LINKFLAG+=-LIBPATH $(WKLIB_BASE)/um/x64/uuid.lib
LINKFLAG+=-LIBPATH $(WKLIB_BASE)/um/x64/kernel32.Lib
LINKFLAG+=-LIBPATH $(WKLIB_BASE)/ucrt/x64/libucrt.lib

# from MSVC
LINKFLAG+=-LIBPATH $(VCLIB_BASE)/libcmt.lib
LINKFLAG+=-LIBPATH $(VCLIB_BASE)/oldnames.lib
LINKFLAG+=-LIBPATH $(VCLIB_BASE)/vcruntime.lib
LINKFLAG+=-LIBPATH $(VCLIB_BASE)/libvcruntime.lib


# Output flag
OBJFLAG=-Fo
EXEFLAG=-OUT:

UEYEINC=-I $(UEYEINC_PATH)
UEYEDLL=-LIBPATH $(UEYELIB_PATH)/uEye_api_64.lib
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


