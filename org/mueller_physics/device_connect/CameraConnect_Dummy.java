/*
This file is part of the jmicro-devices. 

jmicro-devices is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or
(at your option) any later version.

jmicro-devices is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with jmicro-devices.  If not, see <http://www.gnu.org/licenses/>
*/

package org.mueller_physics.device_connect;

import org.mueller_physics.devices.JMicroCamera;
import org.mueller_physics.devices.JMicroLinearMover;

import java.util.Random;

import ij.process.ImageProcessor;
import ij.ImageStack;


public class CameraConnect_Dummy 
    implements JMicroCamera {


    ImageStack [] demoStacks = null;

    double curExp=5;

    int demoStackImg = 0;
    int demoStackPos = 0;
    boolean demoStackAutoInc;

    int zOffset = 0;
    double zTwiddle = 0;
    double zSpacing = 0;
    JMicroLinearMover zStage = null;


    double incX, incY;

    final int sensor_w, sensor_h;

    int roiW, roiH, roiX, roiY;

    Random prng = new Random(2342);

    /** Create a dummy camera returning noise.
     *  Exposure time on this camera will set photon count.
     *  Shot noise and some structure is added to the image.
     *  @param w Width of the dummy sensor
     *  @param h Height of the dummy sensor */
    public CameraConnect_Dummy(int w, int h) {
	sensor_w=w; sensor_h=h;
	roiX=roiY=0;
	roiW=w; roiH=h;
    }
    

    /** Create a dummy camera playing back image stack(s).
     *  On 'snapImage', an image from the stack will be returned.
     *  Auto increment is possible (simulating time lapse) as well
     *  as linking to a stage position (simulating z-stepping).
     *  Multiple (same size!) stacks can be passed and switched through
     *  to simulate switch in xy-position.
     *  @param is Image Stack(s) of the same size (width, height) */
    public CameraConnect_Dummy(ImageStack ... is ) {
	demoStacks=is;
    
	if (is==null || is.length==0) {
	    throw new NullPointerException("stack array null or empty");
	}
	sensor_w = is[0].getWidth();
	sensor_h = is[0].getHeight();
	
	for (int i=0;i<is.length; i++) {
	    if ( is[i].getWidth() != sensor_w || is[i].getHeight() != sensor_h ) {
		throw new RuntimeException("stack "+i+" not the same dimensions");
	    }
	}
	roiX=roiY=0;
	roiW=sensor_w; roiH=sensor_h;
	demoStackAutoInc = true;
    }
   
    @Override
    public void disconnect() {};


    /** Set the exposure time of the camera */
    @Override
    public double setExposureTime(double e) {
	curExp = e*0.995;
	return curExp;
    }


    @Override
    public int [] setROI(int ... r) {

	if (r.length==2) {
	    roiW = r[0];
	    roiH = r[1];
	}
	if (r.length==4) {
	    roiW = r[0];
	    roiH = r[1];
	    roiX = r[2];
	    roiY = r[3];
	}
	
	return new int [] { roiW, roiH, roiX, roiY };
    }


    @Override
    public short [] snapImage() {
	    
	short [] ret = new short[roiW*roiH];
	
	if ( demoStacks == null ) {
	    for (int y=0; y<roiH; y++) {
		for (int x=0; x<roiW; x++) {
		     
		    double val = (curExp<1000)?(curExp*10):(curExp/10.);
		    if ( ((x+incX)%sensor_w) < 5) val*=1.5;
		    if ( ((y+incY)%sensor_h) < 5) val*=1.5;
		    
		    val += Math.sqrt(val)*prng.nextGaussian();
		    
		    ret[x+roiW*y] = (short)val;

		}
	    }
	    incX++;
	    incY++;
	} else {

	    // if we have a stage, calc the image pos
	    if ( zStage != null ) {
		double pos = zStage.getPosition() + zTwiddle;
		demoStackImg = (int)(pos / zSpacing) + zOffset;
	    }

	    int pos = (demoStackImg%demoStacks[demoStackPos].getSize());
	    while (pos<0) { pos += demoStacks[demoStackPos].getSize(); };

	    ImageProcessor ip = demoStacks[demoStackPos].getProcessor( pos+1 );

	    for (int y=0; y<roiH; y++) {
		for (int x=0; x<roiW; x++) {
		    ret[x+roiW*y] = (short)ip.get(x+roiX, y+roiY);
		}
	    }
	    if (demoStackAutoInc) {
		demoStackImg++;
	    }
	}
	
	return ret;
    }

    /** Determine which image is returned by stage position.
     * This allows to simulate z-stacking. Every time an image is taken,
     * 'getPosition' is issued to the stage, and converted into the image returned.
     * @param zOffset  Which slice in the stack is at stage pos 0
     * @param zSpacing Microns between slices in the stack 
     * @param zStage The stage to query for position*/
    public void linkStage( int zOffset, double zSpacing, JMicroLinearMover zStage ) {

	this.zOffset  = zOffset;
	this.zSpacing = zSpacing;
	this.zStage   = zStage;
	demoStackAutoInc = false;

    }

    public void setStageTwiddle( double m ) {
	this.zTwiddle = m;
    }

}

