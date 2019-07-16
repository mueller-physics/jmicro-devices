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

import org.mueller_physics.devices.JMicroLinearMover;



/** Plugin connecting a stage in MicroManager to jmicro-devices.
 * To MicroManager, this class looks like a plugin (that issues stage move commands).
 * To jmicro-devices, this is a LinearMotion device */
public class StageConnect_Dummy
    implements JMicroLinearMover {

	private double pos = 0.;
    
	int moveSpeed=5;
	final double min, max;

	public StageConnect_Dummy(double min, double max) {
	    this.min = min;
	    this.max = max;
	}

	public void moveRelative(double p) {
	    pos += p;
	    if (pos>max) pos = max;
	    if (pos<min) pos = min;
	    try {
                Thread.sleep(moveSpeed);
	    } catch (Exception e) {
		
	    }
	}

	public void moveAbsolute(double p) {
	    if (p>=min && p <=max) {
		pos=p;
	    }
	    try {
                Thread.sleep(moveSpeed);
	    } catch (Exception e) {
		
	    }
	}   

	public Double getPosition() {
	    return pos;
	}

	public void disconnect() {

	}

}
