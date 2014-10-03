package edu.utk.imaging.mapviewer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import edu.utk.imaging.mapviewer.data.TrialData;

public class TrialPanel extends JPanel {
	
	Integer index;
	TrialPanelList trialPanelList;
	TrialData trialData;
	
	JLabel nameLabel;
	JLabel stepsLabel;
	
	TrialPanel(TrialPanelList _trialPanelList, Integer _index, TrialData _trialData) {
		System.out.println("Add trial panel");
		index = _index - 1;
		trialPanelList = _trialPanelList;
		trialData = _trialData;
		setLayout(new BorderLayout());
        //add(new JLabel(trialData.getName()), BorderLayout.CENTER);
        nameLabel = new JLabel(trialData.getName());
        stepsLabel = new JLabel("Steps: " + String.valueOf(trialData.getSteps()));
        add(nameLabel, BorderLayout.CENTER);
		add(stepsLabel, BorderLayout.SOUTH);
        setBackground(Color.WHITE);
        setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
		
		addMouseListener(new MouseListener() {
	    	@Override
	    	public void mouseClicked(MouseEvent e) {
	    		System.out.println("Clicked");
	    		trialPanelList.clicked(trialData.getName(), index);
	    		//changeColorBlue();
	    	}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
	    });
	}
	
	public void setClickedState() {
		this.setBackground(Color.BLUE);
		nameLabel.setForeground(Color.WHITE);
		stepsLabel.setForeground(Color.WHITE);
	}
	
	public void setNotClickedState() {
		this.setBackground(Color.WHITE);
		nameLabel.setForeground(Color.BLACK);
		stepsLabel.setForeground(Color.BLACK);
	}
	
	public TrialData getTrialData() {
		return trialData;
	}
	
	public String getName() {
		return trialData.getName();
	}
}
