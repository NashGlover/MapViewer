package edu.utk.imaging.mapviewer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import edu.utk.imaging.mapviewer.data.TrialData;

public class TrialPanelList extends JPanel{

	int count = 1;
	
	PlotWindow window;
	ArrayList<TrialPanel> trialPanelList = new ArrayList<TrialPanel>(10);
	
	TrialPanelList(PlotWindow _window) {
		window = _window;
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(new JPanel(), gbc);
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 5));
        panel.add(new JLabel("Trial List"));
        //panel.add(new JLabel("Hello"), BorderLayout.SOUTH);
        panel.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridwidth = GridBagConstraints.REMAINDER;
        gbc2.weightx = 1;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        add(panel, gbc2, 0);

        validate();
        repaint();
	}

	public void clicked(Integer index) {
		
	}
	
	public void addTrial(TrialData trial) {
		System.out.println(trial.getName());
		TrialPanel trialPanel = new TrialPanel(this, count, trial);
		trialPanelList.add(trialPanel);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridwidth = GridBagConstraints.REMAINDER;
        gbc2.weightx = 1;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        final String trialName = trial.getName();
        
        add(trialPanel, gbc2, count);
        count++;
	}
	
	public void test() {
		
	}
}
