#include <stdlib.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <inttypes.h>

#include "jni_helpers.h"

#define _IDS_EXPORT
#include <ueye.h>
#include "org_mueller_physics_device_connect_CameraConnect_IDS.h"

// Allow to throw an exception if IDS reports anything but success
void errcheck( JNIEnv * env, int hCam, int res ){
    if (res != IS_SUCCESS) {
	
	char * errMsg;
	is_GetError( hCam, &res, &errMsg);

	// TODO: throw proper custom exception
	 //(*env)->ThrowNew( env, (*env)->FindClass(env, "org/mueller_physics/device_connect/ApiError"), errMsg);
	/*(*env)->CallStaticVoidMethod( env, 
	    (*env)->FindClass(env,"org/mueller_physics/device_connect/ApiError"), 
	 jclass  cls = (*env)->FindClass(env, "org/mueller_physics/device_connect/ApiError");
	 jmethod mtd = (*env)->GetStaticMethodID(env, cls, "throwApiError", "(I)V"); */

	 //(*env)->ThrowNew( env, (*env)->FindClass(env, "org/r_physics/device_connect/CameraConnect_IDS/ApiError"), errMsg);
	 (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), errMsg);
    }

}

// query number of cameras
JNIEXPORT jint JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1GetNumberOfCameras
  (JNIEnv *env, jclass c) {

    int res, nrCams;
    res = is_GetNumberOfCameras(&nrCams);
    errcheck( env, 0, res);
    return nrCams;
}


// initialize camera
JNIEXPORT jint JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1InitCamera
  (JNIEnv *env, jclass c, jint camhd ) {
    
    errcheck( env, camhd, is_InitCamera( &camhd, NULL));
    return camhd;
}


// release the camera
JNIEXPORT void JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1ExitCamera
  (JNIEnv *env, jclass c, jint  camhd) {

    errcheck( env, camhd, is_ExitCamera( camhd ));

}


// ---- Pixel clocks, exposure times, ROI, etc. ----

// return list of avail. pixel clocks
JNIEXPORT jintArray JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1PixelClockGetList
  (JNIEnv *env, jclass c, jint camhd ) {

    uint32_t nrPxlClk;
    errcheck( env, camhd, is_PixelClock(camhd, IS_PIXELCLOCK_CMD_GET_NUMBER, &nrPxlClk, sizeof(nrPxlClk)));

    uint32_t * pxlClocks = malloc(nrPxlClk*sizeof(uint32_t));
    errcheck( env, camhd, is_PixelClock(camhd, IS_PIXELCLOCK_CMD_GET_LIST, pxlClocks, sizeof(int)*nrPxlClk));
 
    jintArray res = intp_to_jintArray(env, nrPxlClk, pxlClocks);

    free( pxlClocks );
    return res;

}

// set color mode / bit depth
JNIEXPORT void JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1SetColorMode
  (JNIEnv *env, jclass c, jint  camhd) {

    errcheck( env, camhd, is_SetColorMode( camhd, IS_CM_MONO16));

}


// set framerate
JNIEXPORT jdouble JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1FrameRateSet
  (JNIEnv *env, jclass c, jint camhd, jdouble fps) {
    double newfps;
    errcheck(env, camhd, is_SetFrameRate(camhd, fps, &newfps));
    return newfps;
}

// query framerate
JNIEXPORT jdouble JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1FrameRateQuery
  (JNIEnv *env, jclass c, jint camhd) {
    double newfps;
    errcheck(env, camhd, is_SetFrameRate(camhd, IS_GET_FRAMERATE, &newfps));
    return newfps;
}


// set exposure time
JNIEXPORT jdouble JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1ExposureTimeSet
  (JNIEnv *env, jclass c, jint camhd, jdouble exp) {

    errcheck( env, camhd, is_Exposure(camhd, IS_EXPOSURE_CMD_SET_EXPOSURE, &exp, 8));
    return exp;

}


// query exposure time
JNIEXPORT jdouble JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1ExposureTimeQuery
  (JNIEnv *env, jclass c, jint camhd) {

    double exp;
    errcheck( env, camhd, is_Exposure(camhd, IS_EXPOSURE_CMD_GET_EXPOSURE, &exp, 8));
    return exp;

}


// query ROI
JNIEXPORT jintArray JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1ROIQuery
  (JNIEnv *env, jclass c,  jint camhd ) {

    IS_RECT roi;
    errcheck( env, camhd, is_AOI(camhd, IS_AOI_IMAGE_GET_AOI, &roi, sizeof(roi)));
    return ints_to_jintArray(env, 4, roi.s32Width, roi.s32Height, roi.s32X, roi.s32Y); 

}

// set ROI
JNIEXPORT jintArray JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1ROISet
  (JNIEnv *env, jclass c, jint camhd, jint input_width, jint input_height, jint input_x, jint input_y, jboolean prnt_dbg) {


    // set the size first (as this determines possible positions)
    IS_SIZE_2D  incSize, minSize, maxSize, roiSize ;
    errcheck( env, camhd, is_AOI(camhd, IS_AOI_IMAGE_GET_SIZE_INC, &incSize, sizeof(incSize)));
    errcheck( env, camhd, is_AOI(camhd, IS_AOI_IMAGE_GET_SIZE_MIN, &minSize, sizeof(minSize)));
    errcheck( env, camhd, is_AOI(camhd, IS_AOI_IMAGE_GET_SIZE_MAX, &maxSize, sizeof(maxSize)));

    roiSize.s32Width    = (input_width/incSize.s32Width) *incSize.s32Width; 
    roiSize.s32Height   = (input_height/incSize.s32Height)*incSize.s32Height; 
    if ( roiSize.s32Width  < minSize.s32Width )  roiSize.s32Width  = minSize.s32Width ; 
    if ( roiSize.s32Height < minSize.s32Height ) roiSize.s32Height = minSize.s32Height ; 
    if ( roiSize.s32Width  > maxSize.s32Width )  roiSize.s32Width  = maxSize.s32Width ; 
    if ( roiSize.s32Height > maxSize.s32Height ) roiSize.s32Height = maxSize.s32Height ; 
   
    errcheck( env, camhd, is_AOI(camhd, IS_AOI_IMAGE_SET_SIZE, &roiSize, sizeof(roiSize)));
   
    if (prnt_dbg) {
	printf(" size: min %2d %2d max %4d %4d inc %2d %2d ROI %d %d \n",
	    minSize.s32Width, minSize.s32Height,
	    maxSize.s32Width, maxSize.s32Height,
	    incSize.s32Width, incSize.s32Height,
	    roiSize.s32Width, roiSize.s32Height);
	fflush(stdout);
    }
  
    // set the position
    IS_POINT_2D  incPos, minPos, maxPos, roiPos;
    errcheck( env, camhd, is_AOI(camhd, IS_AOI_IMAGE_GET_POS_MIN,  &minPos,  sizeof(minPos)));
    errcheck( env, camhd, is_AOI(camhd, IS_AOI_IMAGE_GET_POS_INC,  &incPos,  sizeof(incPos)));
    errcheck( env, camhd, is_AOI(camhd, IS_AOI_IMAGE_GET_POS_MAX,  &maxPos,  sizeof(maxPos)));

    roiPos.s32X   = (input_x/incPos.s32X)*incPos.s32X; 
    roiPos.s32Y   = (input_y/incPos.s32Y)*incPos.s32Y; 
    if ( roiPos.s32X < minPos.s32X ) roiPos.s32X = minPos.s32X ; 
    if ( roiPos.s32Y < minPos.s32Y ) roiPos.s32Y = minPos.s32Y ; 
    if ( roiPos.s32X > maxPos.s32X ) roiPos.s32X = maxPos.s32X ; 
    if ( roiPos.s32Y > maxPos.s32Y ) roiPos.s32Y = maxPos.s32Y ; 
   
    errcheck( env, camhd, is_AOI(camhd, IS_AOI_IMAGE_SET_POS, &roiPos, sizeof(roiPos)));

    if (prnt_dbg) {
	printf("  pos: min %2d %2d max %4d %4d inc %2d %2d ROI %d %d \n",
	    minPos.s32X, minPos.s32Y,
	    maxPos.s32X, maxPos.s32Y,
	    incPos.s32X, incPos.s32Y,
	    roiPos.s32X, roiPos.s32Y);
    
	fflush(stdout);
    }

    return ints_to_jintArray(env, 4, roiSize.s32Width, roiSize.s32Height, roiPos.s32X, roiPos.s32Y); 

}

// ---- Image memeory ----

// allocate image memory. 
// returns pointers and id as a java array
JNIEXPORT jlongArray JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1AllocImageMem
  (JNIEnv *env, jclass c, jint camhd, jint width, jint height, jint bpp) {
   
    jlongArray res = (*env)->NewLongArray(env, 2);
    nullcheck(env, res);
    
    char * loc; int pid;

    errcheck( env, camhd, is_AllocImageMem( camhd, width, height, bpp, &loc, &pid)); 

    jlong ret[2] = { (jlong)loc, pid };

    //printf("--> imgMem %zd, %d\n",loc,pid);

    (*env)->SetLongArrayRegion(env, res, 0, 2, ret);
    return res;

}

JNIEXPORT void JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1SetImageMem
  (JNIEnv *env, jclass c, jint camhd, jlong loc, jlong pod) {

    errcheck(env, camhd, is_SetImageMem( camhd, (char*)loc, (int)pod));

  }

JNIEXPORT void JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1FreeImageMem
  (JNIEnv *env, jclass c, jint camhd, jlong loc, jlong pod) {

    errcheck(env, camhd, is_FreeImageMem( camhd, (char*)loc, (int)pod));

  }


// ---- Image acquisition ----

JNIEXPORT void JNICALL Java_org_mueller_1physics_device_1connect_CameraConnect_1IDS_idsj_1FreezeVideoBlocking
  (JNIEnv *env, jclass c, jint camhd, jshortArray data, jlong cmem, jint size ) {

    errcheck(env, camhd, is_FreezeVideo(camhd, IS_WAIT));

    short * pxl = (*env)->GetPrimitiveArrayCritical(env, data, 0);
    memcpy( pxl, (void*)cmem, size);
    (*env)->ReleasePrimitiveArrayCritical(env,data,pxl,0);
}


