#include <stdlib.h>
#include <stdio.h>

#include <ueye.h>
#include "org_mueller_physics_camera_connect_CameraConnect_IDS.h"


// Allow to throw an exception if IDS reports anything but success
void errcheck( JNIEnv * env, int res, const char * const errMsg ){
    if (res != IS_SUCCESS) {
	 
	 // TODO: throw proper custom exception
	 
	 //(*env)->ThrowNew( env, (*env)->FindClass(env, "org/mueller_physics/camera_connect/ApiError"), errMsg);

	/*(*env)->CallStaticVoidMethod( env, 
	    (*env)->FindClass(env,"org/mueller_physics/camera_connect/ApiError"), 



	 jclass  cls = (*env)->FindClass(env, "org/mueller_physics/camera_connect/ApiError");
	 jmethod mtd = (*env)->GetStaticMethodID(env, cls, "throwApiError", "(I)V"); */

	 //(*env)->ThrowNew( env, (*env)->FindClass(env, "org/r_physics/camera_connect/CameraConnect_IDS/ApiError"), errMsg);
	 (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), errMsg);
    }

}



// query number of cameras
JNIEXPORT jint JNICALL Java_org_mueller_1physics_camera_1connect_CameraConnect_1IDS_idsj_1getNrCams
  (JNIEnv *env, jclass c) {

    int res, nrCams;
    res = is_GetNumberOfCameras(&nrCams);
    errcheck( env, res, "IDS internal error");
    return nrCams;
  }


// initialize camera
JNIEXPORT jint JNICALL Java_org_mueller_1physics_camera_1connect_CameraConnect_1IDS_idsj_1initCam
  (JNIEnv *env, jclass c, jint camhd ) {
    
    errcheck( env, is_InitCamera( &camhd, NULL), "Failed to connect to camera");
    return 0;
    }

