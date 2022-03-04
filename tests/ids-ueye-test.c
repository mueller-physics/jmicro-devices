#include <stdlib.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ueye.h>

void errcheck(int ret) {
    if (ret != IS_SUCCESS) {
	fprintf(stderr,"!! Error code %d\n",ret);
    }
}

int main() {

    HIDS camhd=1;
    double fps, exposure=25;

    int nrCams;
    is_GetNumberOfCameras(&nrCams);
    if (nrCams==0) {
	printf("no camera connected\n");
	return -1;
    }

    for (int count=0; count<2; count++) {
    
	// init camera
	errcheck( is_InitCamera( &camhd, NULL) );
	printf("camera active, handle: %d\n", camhd);

	// exposure, framerate
	errcheck( is_SetFrameRate( camhd, 10., &fps) );
	errcheck( is_Exposure(camhd, IS_EXPOSURE_CMD_SET_EXPOSURE, &exposure, sizeof(double)) );
	printf("set exposure + fps: %7.5f %7.5f\n", exposure, fps);

		
	// AOI size + pos
	IS_SIZE_2D roiSize;
	IS_POINT_2D roiPos;
	roiSize.s32Width = 640; roiSize.s32Height = 480;
	roiPos.s32X = 16; roiPos.s32Y = 32;

	errcheck( is_AOI(camhd, IS_AOI_IMAGE_SET_SIZE, &roiSize, sizeof(roiSize)) );
	errcheck( is_AOI(camhd, IS_AOI_IMAGE_SET_POS,  &roiPos,  sizeof(roiPos)) );
	printf("set AOI %dx%d+%d+%d\n", roiSize.s32Width, roiSize.s32Height, roiPos.s32X, roiPos.s32Y);
	
	errcheck( is_AOI(camhd, IS_AOI_IMAGE_GET_SIZE,  &roiSize,  sizeof(roiPos)) );
	errcheck( is_AOI(camhd, IS_AOI_IMAGE_GET_POS,  &roiPos,  sizeof(roiPos)) );
	printf("get AOI %dx%d+%d+%d\n", roiSize.s32Width, roiSize.s32Height, roiPos.s32X, roiPos.s32Y);


	// alloc + set image mem
	char * loc; int pid;
	errcheck( is_AllocImageMem(camhd, roiSize.s32Width+8, roiSize.s32Height+8, 16, &loc, &pid) );
	errcheck( is_SetImageMem(camhd, loc, pid) );
	printf("alloc image mem: %d %lld\n", pid, (long long)loc);
	
	for (int i=0; i<3; i++) {
	    printf("snap image: %d\n", i);
	    errcheck( is_FreezeVideo(camhd, IS_WAIT) );
	}   
	
	printf("free memory + exit\n");
	errcheck( is_FreeImageMem(camhd, loc, pid) );
	is_ExitCamera(camhd);
    }
}


