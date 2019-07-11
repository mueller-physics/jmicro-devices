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


package org.mueller_physics.device_gui;

import org.mueller_physics.device_connect.CameraConnect_IDS;
import org.fairsim.sim_gui.PlainImageDisplay;
import org.fairsim.sim_gui.Tiles;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CameraSimpleGUI {


    JFrame mainFrame = new JFrame("Simple camera GUI");
    JFrame displayFrame;

    final CameraConnect_IDS cam;
    PlainImageDisplay dspl;

    public CameraSimpleGUI( CameraConnect_IDS c ) {
	
	double initExposure =5.;
	cam = c;
	
	JPanel mainPanel = new JPanel();
	
	JButton snapButton = new JButton("snap");
	snapButton.addActionListener( new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		snapAndUpdate();
	    }
	});
	mainPanel.add( snapButton);


	double [] tmp = cam.setExposureTime(initExposure);
	final JLabel expLabel = new JLabel();
	expLabel.setText(String.format("cur exp: %7.1f (%7.2f fps)", tmp[0], tmp[1]));

	Tiles.LNSpinner expSpinner = new Tiles.LNSpinner("exp. time", initExposure, 0.5, 1000, 0.5);
	expSpinner.addNumberListener( new Tiles.NumberListener() {
	    @Override 
	    public void number(double i, Tiles.LNSpinner n) {
		double [] exp = cam.setExposureTime(i);
		expLabel.setText(String.format("cur exp: %7.1f (%7.2f fps)", exp[0], exp[1]));
	    };
	});
    
	mainPanel.add( expLabel);
	mainPanel.add( expSpinner);

	cam.setROI(32,32,1024,1024);
	updateDisplay(1024,1024);


	mainFrame.add( mainPanel);
	mainFrame.pack();
	mainFrame.setVisible(true);

    }

    void updateDisplay(int w, int h) {
	displayFrame = new JFrame("camera");
	dspl = new PlainImageDisplay(1, w, h);
	displayFrame.add( dspl.getPanel());
	displayFrame.pack();
	displayFrame.setVisible(true);
    }


    public void snapAndUpdate() {
	short [] img = cam.snapImage();
	dspl.newImage(0, img);
	dspl.refresh();
    }



    public static void main(String [] arg) {
	CameraSimpleGUI gui = new CameraSimpleGUI( CameraConnect_IDS.connect(1)); 
    
    }



}
