

.PHONY: clean java


all: cam_connect_ids.so


# jni location
JNIFLAG= -I /usr/lib/jvm/java-8-openjdk-amd64/include/ -I /usr/lib/jvm/java-8-openjdk-amd64/include/linux/

# generic c flags
CFLAGS= -O2 $(JNIFLAG)


# general .c --> .so rule
%.o:%.c
	$(CC) -c -fPIC -o $@ $< $(CFLAGS) 

# clean
clean:
	$(RM) *.so *.o org_mueller_physics_*.h


# IDS header files
org_mueller_physics_camera_connect_CameraConnect_IDS.h: ../org/mueller_physics/camera_connect/CameraConnect_IDS.java
	javah -cp ../ org.mueller_physics.camera_connect.CameraConnect_IDS
	touch $@

# IDS object
cam_connect_ids.o: org_mueller_physics_camera_connect_CameraConnect_IDS.h

# IDS connector library
cam_connect_ids.so: cam_connect_ids.o jni_helpers.o
	$(CC) -fPIC -shared -o $@ $^ $(CFLAGS) -lueye_api 


