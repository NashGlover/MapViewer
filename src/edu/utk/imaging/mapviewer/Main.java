package edu.utk.imaging.mapviewer;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import edu.utk.imaging.mapviewer.view.PlotWindow;

public class Main {

	Main() {
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (UnsupportedLookAndFeelException e) {
			} catch (IllegalAccessException illegalE) {
			} catch (InstantiationException instantiationE) {
			} catch (ClassNotFoundException classE) {
			}
			PlotWindow window = new PlotWindow();
			window.setVisible(true);
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
			});
		
		/*//System.out.println("Main");
		PlotWindow window = new PlotWindow();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//window.pack();
		//window.setVisible(true);*/
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main main = new Main();
	}

}
