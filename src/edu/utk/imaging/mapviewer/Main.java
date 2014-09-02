package edu.utk.imaging.mapviewer;

import javax.swing.JFrame;

import edu.utk.imaging.mapviewer.view.PlotWindow;

public class Main {

	Main() {
		System.out.println("Main");
		PlotWindow window = new PlotWindow();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main main = new Main();
	}

}
