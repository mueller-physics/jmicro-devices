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

import java.util.Random;

import ij.process.ImageProcessor;
import ij.ImageStack;


public class CameraConnect_Dummy 
    implements JMicroCamera {


    ImageStack demoStack = null;

    double curExp=5;
    int demoStackPos = 0;
    boolean demoStackAutoInc;

    double incX, incY;

    final int sensor_w, sensor_h;

    int roiW, roiH, roiX, roiY;

    Random prng = new Random(2342);
    
    public CameraConnect_Dummy(int w, int h) {
	sensor_w=w; sensor_h=h;
	roiX=roiY=0;
	roiW=w; roiH=h;
    }
    
    public CameraConnect_Dummy(ImageStack is ) {
	demoStack=is;
	sensor_w = is.getWidth();
	sensor_h = is.getHeight();
	demoStackAutoInc = true;
    }
    

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
	if (r.length==2) {
	    roiX = r[0];
	    roiY = r[1];
	    roiW = r[2];
	    roiH = r[3];
	}
	
	return new int [] { roiW, roiH };


    }


    @Override
    public short [] snapImage() {
	    
	short [] ret = new short[roiW*roiH];
	
	if ( demoStack == null ) {
	    for (int y=0; y<roiH; y++) {
		for (int x=0; x<roiW; x++) {
		     
		    double val = (curExp<1000)?(curExp*10):(curExp/10.);
		    if ( ((x+incX)%sensor_w) < 5) val*=1.5;
		    if ( ((y+incY)%sensor_h) < 5) val*=1.5;
		    
		    val += Math.sqrt(val)*prng.nextGaussian();
		    
		    ret[x+sensor_w*y] = (short)val;

		}
	    }
	    incX++;
	    incY++;
	} else {

	    ImageProcessor ip = demoStack.getProcessor( 
		((demoStackPos)%demoStack.getSize())+1);

	    for (int y=0; y<roiH; y++) {
		for (int x=0; x<roiW; x++) {
		    ret[x+sensor_w*y] = (short)ip.getf(x+roiX, y+roiY);
		}
	    }
	    if (demoStackAutoInc) {
		demoStackPos++;
	    }
	}
	
	return ret;
    }



}

