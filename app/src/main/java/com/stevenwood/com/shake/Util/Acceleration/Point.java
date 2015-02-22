package com.stevenwood.com.shake.Util.Acceleration;

public class Point {

	private double mag;
	private long time;
	
	public Point(double mag, long timestamp){
		this.mag = mag;
		time = timestamp;
	}
	public Point(float x, float y, float z, long timestamp){
		mag = Math.sqrt(x*x + y*y + z*z);
		time = timestamp;
	}
	
	public double getMagnitude(){
		return mag;
	}
	public long getTimestamp(){
		return time;
	}
	
	public String toString(){
		return "{mag="+mag+", time="+time+"}";
	}
	
}
