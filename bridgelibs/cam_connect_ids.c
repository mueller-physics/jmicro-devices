#include <stdlib.h>
#include <stdio.h>

#include <ueye.h>
#include "org_mueller_physics_camera_connect_CameraConnect_IDS.h"

struct {

    char *  ppcImgMem;
    int	    pid;

} ids_mem_info ;



// Allow to throw an exception if IDS reports anything but success
void errcheck( JNIEnv * env, int hCam, int res ){
    if (res != IS_SUCCESS) {
	
	char * errMsg;
	is_GetError( hCam, &res, &errMsg);

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
JNIEXPORT jint JNICALL Java_org_mueller_1physics_camera_1connect_CameraConnect_1IDS_idsj_1GetNumberOfCameras
  (JNIEnv *env, jclass c) {

    int res, nrCams;
    res = is_GetNumberOfCameras(&nrCams);
    errcheck( env, 0, res);
    return nrCams;
}


// initialize camera
JNIEXPORT jint JNICALL Java_org_mueller_1physics_camera_1connect_CameraConnect_1IDS_idsj_1InitCamera
  (JNIEnv *env, jclass c, jint camhd ) {
    
    errcheck( env, camhd, is_InitCamera( &camhd, NULL));
    return 0;
}


// release the camera
JNIEXPORT void JNICALL Java_org_mueller_1physics_camera_1connect_CameraConnect_1IDS_idsj_1ExitCamera
  (JNIEnv *env, jclass c, jint  camhd) {

    errcheck( env, camhd, is_ExitCamera( camhd ));

}


// allocate image memory. 
// returns pointers and id as a java array
JNIEXPORT jlongArray JNICALL Java_org_mueller_1physics_camera_1connect_CameraConnect_1IDS_idsj_1AllocImageMem
  (JNIEnv *env, jclass c, jint camhd, jint width, jint height, jint bpp) {
   
    jlongArray res = (*env)->NewLongArray(env, 2);
    if (res==NULL) {
	(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), "mem alloc failed");
    }

    char * loc; int pid;

    errcheck( env, camhd, is_AllocImageMem( camhd, width, height, bpp, &loc, &pid)); 

    jlong ret[2] = { (long)loc, pid };

    (*env)->SetLongArrayRegion(env, res, 0, 2, ret);
    return res;

}


// ---- Pixel clocks, exposure times, etc. ----

// return list of avail. pixel clocks
JNIEXPORT jintArray JNICALL Java_org_mueller_1physics_camera_1connect_CameraConnect_1IDS_idsj_1PixelClockGetList
  (JNIEnv *env, jclass c, jint camhd ) {

    int nrPxlClk;
    errcheck( env, camhd, is_PixelClock(camhd, IS_PIXELCLOCK_CMD_GET_NUMBER, &nrPxlClk, sizeof(nrPxlClk)));

    jintArray res = (*env)->NewIntArray(env, nrPxlClk);
    if (res==NULL) {
	(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), "mem alloc failed");
    }

    if (nrPxlClk==0) {
	return res;
    }

    UINT pxlClocks[nrPxlClk];
    errcheck( env, camhd, is_PixelClock(camhd, IS_PIXELCLOCK_CMD_GET_LIST, &pxlClocks, sizeof(UINT)*nrPxlClk));

    jint ret[nrPxlClk];
    for (int i=0;i<nrPxlClk;i++) {
	ret[i] = pxlClocks[i];
    }

    (*env)->SetIntArrayRegion(env, res, 0, nrPxlClk, ret);
    return res;

}

// set framerate
JNIEXPORT jdouble JNICALL Java_org_mueller_1physics_camera_1connect_CameraConnect_1IDS_idsj_1FrameRateSet
  (JNIEnv *env, jclass c, jint camhd, jdouble fps) {
    double newfps;
    errcheck(env, camhd, is_SetFrameRate(camhd, fps, &newfps));
    return newfps;
}

// query framerate
JNIEXPORT jdouble JNICALL Java_org_mueller_1physics_camera_1connect_CameraConnect_1IDS_idsj_1FrameRateQuery
  (JNIEnv *env, jclass c, jint camhd) {
    double newfps;
    errcheck(env, camhd, is_SetFrameRate(camhd, IS_GET_FRAMERATE, &newfps));
    return newfps;
}




