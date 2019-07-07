/*
This file is part of the IR focus tracker (ir-track). 

ir-track is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or
(at your option) any later version.

ir-track is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with ir-track.  If not, see <http://www.gnu.org/licenses/>
*/

package org.mueller_physics.camera_connect;


public class CameraConnect_IDS {

    

    // initialize the static library...
    static {
	try {
	   // TODO: read this from some config
	   System.load(System.getProperty("user.dir")+"/bridgelibs/cam_connect_ids.so");
	} catch (UnsatisfiedLinkError e) {
	    System.err.println("Native code library failed to load.\n" + e);
	    System.exit(1);
	}
    }


    // the camera id this instance is connected to
    private int camhd=-1;

    public CameraConnect_IDS() {};

    /** connect to camera #n **/
    public void connect(int id) {
	idsj_InitCamera(id);
	camhd=id;
    }

    /** connect from camera */
    public void disconnect() {
	if (camhd<0) return;
	idsj_ExitCamera(camhd);
	camhd=-1;
    }




    
    // --- JNI native calls ----


    /** Returns the total number of connected IDS cameras.
     *  calls 'ids_GetNumberOfCameras() */
    private static native int idsj_GetNumberOfCameras();

    /** Initializes an IDS camera based on ID.
     *  calls 'ids_InitCamera(camId)'
     *	@param camera_id */
    private static native int idsj_InitCamera(int hCam);
    
    /** Allocates image memory.
     *  calls 'is_AllocImageMemory'.
     *  @return A long[] with 'ppcImgMem' and 'pid' */
    private static native long[] idsj_AllocImageMem(int hCam, int w, int h, int bpp);

    /** Exits an IDS camera based on ID.
     *  calls 'ids_ExitCamera(camId)' */
    private static native void idsj_ExitCamera(int hCam);


    /** Get a list of supported pixel clocks.
     *  calls is_PixelClock(). TODO: Handle cameras with continuous pixel clock settings. 
     *  These will currently return an empty list of size 0. */
    private static native int[] idsj_PixelClockGetList(int hCam);
    
    /** Set a new camera framerate-
     * calls 'is_SetFrameRate()'.
     * @return the new framerate */
    private static native double idsj_FrameRateSet(int hCam, double fps);

    /** Query the cameras currently set frame rate */
    private static native double idsj_FrameRateQuery(int hCam);


    // --- test the class ---
    public static void main(String [] args) {


	int nrCams = idsj_GetNumberOfCameras();
	System.out.println("IDS cameras: "+nrCams);
	
	if (nrCams <1) {
	    System.out.println("No camera connected ...");
	    return;
	}

	// run low level (static jni calls) functions
	int id=1;			// camera id to use
	int rx=16,ry=32,rw=640,rh=480;	// parameters for ROI setting
	
	
	System.out.println("--- API level functions ----");

	idsj_InitCamera(id);
	System.out.println("connected to camera: "+id);


	int [] pxlClocks = idsj_PixelClockGetList(id);
	for (int i=0; i<pxlClocks.length; i++) {
	    System.out.println("pixel clock option ["+i+"]: "+pxlClocks[i]);
	}

	double fps = idsj_FrameRateQuery(id);
	System.out.println("current fps: "+fps);

	fps = idsj_FrameRateSet(id,10.);
	System.out.println("Frame rate set 10 fps, now: "+fps);


	long [] res = idsj_AllocImageMem(id, rw, rh, 16);
	System.out.println("allocated memory: pid: "+res[1]+" loc: "+res[0]);

	idsj_ExitCamera(id);
	System.out.println("disconnected");
	


    }


}
