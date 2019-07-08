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

    // check there is actually a camera connected
    int nrCams;
    errcheck( is_GetNumberOfCameras(&nrCams));
    if (nrCams==0) {
	printf("no camera connected\n");
	return -1;
    }

    // try this multiple times... !!! it fails on the second run
    for (int count=0; count<5; count++) {
    
	// init camera
	errcheck( is_InitCamera( &camhd, NULL));
	printf("camera active, handle: %d\n", camhd);


	IS_RECT sensor;
	errcheck( is_AOI( camhd, IS_AOI_IMAGE_GET_AOI, &sensor, sizeof(sensor)));
	printf("sensor %dx%d+%d+%d", sensor.s32Width, sensor.s32Height, sensor.s32X, sensor.s32Y);

	// alloc 2048x2048, def. bigger than sensor
	char * loc; int pid;
	errcheck( is_AllocImageMem(camhd, sensor.s32Width, sensor.s32Height, 16, &loc, &pid));
	errcheck( is_SetImageMem(camhd, loc, pid));
	printf("alloc image mem: %d %ld\n", pid, (long)loc);
	
	// snap some images
	for (int i=0; i<3; i++) {
	    printf("snap image: %d\n", i);
	    errcheck( is_FreezeVideo(camhd, IS_WAIT) );
	}   
	
	printf("free memory + exit\n");
	errcheck( is_FreeImageMem(camhd, loc, pid) );
	errcheck( is_ExitCamera(camhd) );
    }
}


