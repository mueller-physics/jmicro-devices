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

package org.mueller_physics.devices;

public interface JMicroLinearMover {

    public void moveAbsolute(double pos);

    public void moveRelative(double pos);

    public Double getPosition();


}
