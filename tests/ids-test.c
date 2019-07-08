#include <stdlib.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ueye.h>

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
	is_InitCamera( &camhd, NULL);
	printf("camera active, handle: %d\n", camhd);

	// exposure, framerate
	is_SetFrameRate( camhd, 10., &fps);
	is_Exposure(camhd, IS_EXPOSURE_CMD_SET_EXPOSURE, &exposure, sizeof(double));
	printf("set exposure + fps: %7.5f %7.5f\n", exposure, fps);

	// AOI size + pos
	IS_SIZE_2D roiSize;
	IS_POINT_2D roiPos;
	roiSize.s32Width = 640; roiSize.s32Height = 480;
	roiPos.s32X = 16; roiPos.s32Y = 32;

	is_AOI(camhd, IS_AOI_IMAGE_SET_SIZE, &roiSize, sizeof(roiSize));
	is_AOI(camhd, IS_AOI_IMAGE_SET_POS,  &roiPos,  sizeof(roiPos));
	printf("set AOI %dx%d+%d+%d\n", roiSize.s32Width, roiSize.s32Height, roiPos.s32X, roiPos.s32Y);


	// alloc + set image mem
	char * loc; int pid;
	is_AllocImageMem(camhd, roiSize.s32Width, roiSize.s32Height, 16, &loc, &pid);
	is_SetImageMem(camhd, loc, pid);
	printf("alloc image mem: %d %ld\n", pid, (long)loc);
	

	for (int i=0; i<3; i++) {
	    printf("snap image: %d\n", i);
	    is_FreezeVideo(camhd, IS_WAIT);
	}   
	
	printf("free memory + exit\n");
	//is_FreeImageMem(camhd, loc, pid);
	is_ExitCamera(camhd);
    }
}


