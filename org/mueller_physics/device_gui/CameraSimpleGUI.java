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

import org.mueller_physics.devices.JMicroCamera;
import org.fairsim.sim_gui.PlainImageDisplay;
import org.fairsim.sim_gui.Tiles;

import ij.ImageJ;
import ij.IJ;
import ij.ImagePlus;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class CameraSimpleGUI {


    JFrame mainFrame = new JFrame("Simple camera GUI");
    JFrame displayFrame;

    final JMicroCamera cam;
    PlainImageDisplay dspl;

    public CameraSimpleGUI( JMicroCamera c ) {
	
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


	double  tmp = cam.setExposureTime(initExposure);
	final JLabel expLabel = new JLabel();
	expLabel.setText(String.format("cur exp: %07.1f", tmp));

	Tiles.LNSpinner expSpinner = new Tiles.LNSpinner("exp. time", initExposure, 0.5, 1000, 0.5);
	expSpinner.addNumberListener( new Tiles.NumberListener() {
	    @Override 
	    public void number(double i, Tiles.LNSpinner n) {
		double exp = cam.setExposureTime(i);
		expLabel.setText(String.format("cur exp: %07.1f ", exp));
	    };
	});
    
	mainPanel.add( expLabel);
	mainPanel.add( expSpinner);

	cam.setROI(0,0,512,512);
	updateDisplay(512,512);


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
	
	if (arg.length==0) {
	    System.out.println("'ids'  - connect to IDS with ID #1");
	    System.out.println("'dummy' - run the dummy camera with noise");
	    System.out.println("'tif [tif-file]' - run the dummy camera with TIFF file");
	    return;
	}
	
	if ( arg[0].equals("ids")) {	
	    CameraSimpleGUI gui = new CameraSimpleGUI( 
		org.mueller_physics.device_connect.CameraConnect_IDS.connect(1)); 
	}
	if ( arg[0].equals("dummy")) {
	    CameraSimpleGUI gui = new CameraSimpleGUI( 
		new org.mueller_physics.device_connect.CameraConnect_Dummy(512,512));
	}
	if (arg[0].equals("tiff")) {
	    new ij.ImageJ( ij.ImageJ.EMBEDDED );
	    ImagePlus ip = IJ.openImage(arg[1]);
	    CameraSimpleGUI gui = new CameraSimpleGUI( 
		new org.mueller_physics.device_connect.CameraConnect_Dummy(ip.getStack()));
	}
	    	
    }



}
