
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

    public CameraConnect_IDS(int nr) {
	connect(nr);
    }

    /** connect to camera #n **/
    public void connect(int camId) {
	idsj_initCam(camId);
    }





    
    // --- JNI native calls ----


    /** returns the total number of connected IDS cameras */
    static native int idsj_getNrCams();

    static native int idsj_initCam(int camId);
    


    // --- test the class ---


    public static void main(String [] args) {


	int nrCams = idsj_getNrCams();
	System.out.println("IDS cameras: "+nrCams);
	
	if (nrCams <1) {
	    System.out.println("No camera connected ...");
	    return;
	}

	CameraConnect_IDS idsConn = new CameraConnect_IDS(1);

	System.out.println("connected");
	System.out.flush();

    }


}
