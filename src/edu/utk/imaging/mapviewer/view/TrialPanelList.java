package edu.utk.imaging.mapviewer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import edu.utk.imaging.mapviewer.data.TrialData;

public class TrialPanelList extends JPanel{

	TrialPanelList() {
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

	public void addTrial(TrialData trial) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
        panel.add(new JLabel(trial.getName()), BorderLayout.CENTER);
        panel.add(new JLabel("Hello"), BorderLayout.SOUTH);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridwidth = GridBagConstraints.REMAINDER;
        gbc2.weightx = 1;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        
        panel.addMouseListener(new MouseListener() {
        	
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		System.out.println("Clicked");
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
        
        add(panel, gbc2, 1);
	}
}
