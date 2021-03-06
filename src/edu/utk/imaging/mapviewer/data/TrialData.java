package edu.utk.imaging.mapviewer.data;

import java.util.ArrayList;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class TrialData {
	
	XYDataset xyDataset;
	ArrayList<Coordinate> anchoredPoints;
	ArrayList<Coordinate> anchorlessPoints;
	ArrayList<Coordinate> anchorPoints;
	
	
	public TrialData() {
		xyDataset = null;
		anchoredPoints = new ArrayList<Coordinate>(200);
		anchorlessPoints = new ArrayList<Coordinate>(200);
		anchorPoints = new ArrayList<Coordinate>(200);
	}
	
	public ArrayList<Coordinate> getAnchoredPoints() {
		return anchoredPoints;
	}
	
	public ArrayList<Coordinate> getAnchorlessPoints() {
		return anchorlessPoints;
	}
	
	public ArrayList<Coordinate> getAnchorPoints() {
		return anchorPoints;
	}
	
	public void addAnchoredPoint(Coordinate _coordinate) {
		anchoredPoints.add(_coordinate);
	}
	
	public void addAnchorlessPoint(Coordinate _coordinate) {
		anchorlessPoints.add(_coordinate);
	}
	
	public void addAnchorPoint(Coordinate _coordinate) {
		anchoredPoints.add(_coordinate);
	}
	
}
