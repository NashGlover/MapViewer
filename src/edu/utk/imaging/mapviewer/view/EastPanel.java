package edu.utk.imaging.mapviewer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.utk.imaging.mapviewer.data.TrialData;

public class EastPanel extends JPanel {
	
	TrialPanelList trialPanelList;
	
	public EastPanel(PlotWindow window) {
		setLayout(new BorderLayout());
		trialPanelList = new TrialPanelList(window);
		add(new JScrollPane(trialPanelList));
		setBackground(Color.WHITE);
	}
	
	public void addTrial(TrialData trial) {
		trialPanelList.addTrial(trial);
	}
	
}
