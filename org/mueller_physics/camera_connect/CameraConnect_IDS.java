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

import org.fairsim.sim_gui.PlainImageDisplay;
import javax.swing.JFrame;

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
    private final int camhd;
    private boolean cameraConnected = true;
    private CameraConnect_IDS(int c) {
	camhd=c;	
    };

    protected void finalize() throws Throwable {
	if (cameraConnected) {
	    idsj_ExitCamera(camhd);
	}
	super.finalize();
    }

    /** connect to camera #n **/
    public static CameraConnect_IDS connect(int id) {
	idsj_InitCamera(id);
	return new CameraConnect_IDS(id);
    }

    /** disconnect from camera */
    public void disconnect() {
	idsj_ExitCamera(camhd);
	cameraConnected=false;
    }


    public double [] setExposureTime( double expTimeMS ) {
	double [] ret = new double[2];

	ret[1] = idsj_FrameRateSet( camhd, (1000/expTimeMS) );
	ret[0] = idsj_ExposureTimeSet( camhd, expTimeMS);

	return ret;
    }
    







    
    // --- JNI native calls ----


    /** Returns the total number of connected IDS cameras.
     *  calls 'ids_GetNumberOfCameras() */
    private static native int idsj_GetNumberOfCameras();

    /** Initializes an IDS camera based on ID.
     *  calls 'ids_InitCamera(camId)'
     *	@param camera_id */
    private static native int idsj_InitCamera(int hCam);
    
    /** Exits an IDS camera based on ID.
     *  calls 'ids_ExitCamera(camId)' */
    private static native void idsj_ExitCamera(int hCam);


    /** Get a list of supported pixel clocks.
     *  calls is_PixelClock(). TODO: Handle cameras with continuous pixel clock settings. 
     *  These will currently return an empty list of size 0. */
    private static native int[] idsj_PixelClockGetList(int hCam);
    
    /** Set a new camera framerate.
     * calls 'is_SetFrameRate()'.
     * @return the new framerate */
    private static native double idsj_FrameRateSet(int hCam, double fps);

    /** Query the cameras currently set frame rate */
    private static native double idsj_FrameRateQuery(int hCam);

    /** Set the cameras exposure time */
    private static native double idsj_ExposureTimeSet( int hCam, double time);
    
    /** Query the cameras exposure time */
    private static native double idsj_ExposureTimeQuery( int hCam);

    /** Get the camera's ROI.
     *  Calls into 'is_AOI', only performs a 'GET'
     * */
    private static native int [] idsj_ROIQuery(int hCam);

    /** Set the camera's ROI.
     *	Calls into 'is_AOI', performs various size + bound checks.
     * */
    private static native int [] idsj_ROISet(int hCam, int x, int y, int w, int h, boolean dbg);


    /** Allocates image memory.
     *  calls 'is_AllocImageMemory'.
     *  @return A long[] with 'ppcImgMem' and 'pid' */
    private static native long[] idsj_AllocImageMem(int hCam, int w, int h, int bpp);

    /** Set image memory active.
     * calls 'is_SetImageMem' */
    private static native void idsj_SetImageMem(int hCam, long pcImgMem, long id);
    

    /** Blocking image acquisition.
     *  calls 'is_freezeVideo' and returns result into the provided array
     * */
    private static native void idsj_FreezeVideoBlocking(int hCam, short [] data, long cmem, int size );

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
	int rx=17,ry=41,rw=643,rh=481;	// parameters for ROI setting
	
	
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

	double exp = idsj_ExposureTimeQuery(id);
	System.out.println("Exposure time: "+exp);

	exp = idsj_ExposureTimeSet(id, 2.);
	System.out.println("Exposure time set to 2ms, not: "+exp);


	int [] roi;
	roi = idsj_ROIQuery(id);
	System.out.println("get ROI, cur x: "+roi[0]+" y:  "+roi[1]+" w: "+roi[2]+" h: "+roi[3]);
	roi = idsj_ROISet(id, rx,ry,rw,rh, true);
	System.out.println("set ROI, new x: "+roi[0]+" y:  "+roi[1]+" w: "+roi[2]+" h: "+roi[3]);


	long [] res = idsj_AllocImageMem(id, rw, rh, 16);
	System.out.println("allocated memory: pid: "+res[1]+" loc: "+res[0]);
	
	idsj_SetImageMem( id, res[0], res[1]);
	System.out.println("memory "+res[1]+" set as active");

	short [] pxl = new short[ roi[2]*roi[3] ];

        // create a frame and add the display
	PlainImageDisplay dspl = new PlainImageDisplay(1,roi[2],roi[3],"IDS_Camera");
        JFrame mainFrame = new JFrame("Plain Image Receiver");
        mainFrame.add( dspl.getPanel() ); 
        
        mainFrame.pack();
        mainFrame.setLocation( 100, 100 );
        mainFrame.setVisible(true);

	for (int i=0; i<200; i++) {
	    long t1 = System.nanoTime();
	    idsj_FreezeVideoBlocking( id, pxl, res[0], roi[2]*roi[3]*2 );
	    long t2 = System.nanoTime();
	    System.out.println("Snapped image "+i+": "+(t2-t1)/1000000+"ms");
	    dspl.newImage(0, pxl);	
	
	}


	idsj_ExitCamera(id);
	System.out.println("disconnected");
	
	try { Thread.sleep(1); } catch (Exception e) {};

	System.out.println("---- JAVA / class level functions ----");

	CameraConnect_IDS cc = CameraConnect_IDS.connect(id);
	System.out.println("-> connected");

	double [] expTime = cc.setExposureTime(5.);
	System.out.println("Exposure time set to 5ms, now "+expTime[0]+" fps "+expTime[1]);




	cc.disconnect();
	System.out.println("-> disconnected, done");

    }


}
