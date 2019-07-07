

.PHONY: clean java


all: cam_connect_ids.so


# jni location
JNIFLAG= -I /usr/lib/jvm/java-8-openjdk-amd64/include/ -I /usr/lib/jvm/java-8-openjdk-amd64/include/linux/

# generic c flags
CFLAGS= -O2 $(JNIFLAG)


# general .c --> .so rule
%.so:%.c
	$(CC) -fPIC -shared -o $@ $< $(CFLAGS) -lueye_api 


# IDS header files
org_mueller_physics_camera_connect_CameraConnect_IDS.h: ../org/mueller_physics/camera_connect/CameraConnect_IDS.java
	javah -cp ../ org.mueller_physics.camera_connect.CameraConnect_IDS
	touch $@

# IDS connector library
cam_connect_ids.so: org_mueller_physics_camera_connect_CameraConnect_IDS.h


# clean
clean:
	$(RM) *.so org_mueller_physics_*.h
