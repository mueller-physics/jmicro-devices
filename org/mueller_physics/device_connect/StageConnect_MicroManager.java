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

import org.micromanager.api.ScriptInterface;
import mmcorej.CMMCore;


/** Plugin connecting a stage in MicroManager to jmicro-devices.
 * To MicroManager, this class looks like a plugin (that issues stage move commands).
 * To jmicro-devices, this is a LinearMotion device */
public class StageConnect_MicroManager { 

    private ScriptInterface si = null;
    private CMMCore mmc = null;

    private StageConnect_MicroManager( ScriptInterface s ) {
	si = s;
	mmc = si.getMMCore();
    };

    /** Select a stage axis */
    public  static StageConnect_MicroManager connect( ScriptInterface s ) {
	return new StageConnect_MicroManager( s );
    }

    public void moveAbsolute(double pos) {
	try {
	    mmc.setPosition(pos);
	} catch (Exception e) {
	    si.showError(e, "jmicro-device");	    
	}
    }

    public void moveRelative(double pos) {
	try {
	    mmc.setRelativePosition(pos);
	} catch (Exception e) {
	    si.showError(e, "jmicro-device");	    
	}
    }

    public Double getPosition() {
	try {
	    return mmc.getPosition();
	} catch (Exception e) {
	    si.showError(e, "jmicro-device");
	}
	return null;
    }

}

 

