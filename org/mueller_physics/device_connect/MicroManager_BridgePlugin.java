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

import org.mueller_physics.device_gui.CameraSimpleGUI;

import org.micromanager.api.ScriptInterface;
import mmcorej.CMMCore;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MicroManager_BridgePlugin
    implements org.micromanager.api.MMPlugin {
    
    static CMMCore mmc = null;
    static ScriptInterface si = null;

    public static final String menuName = "jmicro-device bridge control";
    public static final String tooltipDescription = "Export MicroManager devices to jmicro";

    private JFrame mainFrame = null;

    // All bridge devices
    StageConnect_MicroManager stageConnect ;

    CameraSimpleGUI camGUI = null;

    /**
     * called when micro manager is closing
     */
    @Override
    public void dispose() {
	//this.mmc = null;
    }

    /**
     * called by micro while starting this plugin
     * @param si ScriptInterface of micro manager
     */
    @Override
    public void setApp(ScriptInterface si) {
	this.si  = si;
	this.mmc = si.getMMCore();
	stageConnect = StageConnect_MicroManager.connect(si);
    }

    /**
     * called by micro manager after setApp
     * @see setApp(ScriptInterface si)
     */
    @Override
    public void show() {
    
	if (mainFrame != null) {
	    mainFrame.setVisible(true);
	    return;
	}
	
	mainFrame = new JFrame("jmicro-devices");
	JPanel pnl = new JPanel();

	JButton up = new JButton("^");
	JButton dn = new JButton("v");

	final JButton cam = new JButton("open IDS");
	cam.setEnabled(CameraConnect_IDS._available);	

	up.addActionListener( new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		stageConnect.moveRelative(-0.5);
		System.out.println("up");
	    }
	});
	dn.addActionListener( new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		stageConnect.moveRelative(0.5);
		System.out.println("dn");
	    }
	});

	cam.addActionListener( new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		try {
		    CameraConnect_IDS c = CameraConnect_IDS.connect(1);
		    if (c==null) {
			cam.setEnabled(false);
		    }
		    camGUI = new CameraSimpleGUI( c );
		} catch (Exception ex) {
		    si.showError(ex, "jmicro-device");	    
		}
	    }
	});

    

	pnl.add(up);
	pnl.add(dn);
	pnl.add( cam );
	
	
	mainFrame.add(pnl);
	mainFrame.pack();
	mainFrame.setVisible(true);

    
    }

    @Override
    public String getDescription() {
	return "MicroManager to jmicro-devices bridge";
    }

    @Override
    public String getInfo() {
        return getDescription();
    }

    @Override
    public String getVersion() {
        return "version 1";
    }

    @Override
    public String getCopyright() {
        return "GPL licence";
    }



}
