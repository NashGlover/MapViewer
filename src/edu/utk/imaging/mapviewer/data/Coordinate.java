package edu.utk.imaging.mapviewer.data;

public class Coordinate {
    private double x;
    private double y;
    private double z;
    private double timestamp;
    
    public Coordinate (double _x, double _y, double _z) {
    	x = _x;
    	y = _y;
    	z = _z;
    }
    
    public Coordinate (double _x, double _y, double _z, double _timestamp) {
        x = _x;
        y = _y;
        z = _z;
        timestamp = _timestamp;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getZ() {
        return z;
    }
    
    public double getTimestamp() {
        return timestamp;
    }
}