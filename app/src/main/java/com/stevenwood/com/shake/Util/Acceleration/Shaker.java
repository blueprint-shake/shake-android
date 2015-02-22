package com.stevenwood.com.shake.Util.Acceleration;

import java.util.ArrayList;

public class Shaker {
	
	// THRESHHOLDS
	public static final double PEAKS = 0.07;
	public static final double FRAMES = 0.0075;

	public static ArrayList<Point> detect(ArrayList<Point> points){
		if(points.size() <= 2) return null;
		ArrayList<Point> filtered_peaks = FilterPeaks(FindPeaks(points));
		Frame largest_frame = LargestFrame(FrequencyFrames(filtered_peaks));
		if(largest_frame == null) return null;
		return new ArrayList<Point>(filtered_peaks.subList(largest_frame.getStart(), largest_frame.getEnd()));
	}
	
	private static ArrayList<Point> FindPeaks(ArrayList<Point> points){
		ArrayList<Point> peaks = new ArrayList<Point>();
		
		for(int i=0; i<points.size(); i++){
			if(i == 0 || i == points.size()-1) continue;
			Point point = points.get(i);
			Point last = points.get(i-1);
			Point next = points.get(i+1);
			if(last.getMagnitude() <= point.getMagnitude() && next.getMagnitude() <= point.getMagnitude())
				peaks.add(point);
			if(last.getMagnitude() >= point.getMagnitude() && next.getMagnitude() >= point.getMagnitude())
				peaks.add(point);
		}
		
		return peaks;
	}
	private static ArrayList<Point> FilterPeaks(ArrayList<Point> peaks){
		ArrayList<Point> filtered = new ArrayList<Point>();
		if(peaks.size() == 1) return filtered;
		
		for(int i=0; i<peaks.size(); i++){
			double avg_change;
			
			if(i==0) avg_change = Math.abs(FilterPeakChange(peaks.get(0), peaks.get(1)));
			else if(i==peaks.size()-1) avg_change = Math.abs(FilterPeakChange(peaks.get(peaks.size()-2), peaks.get(peaks.size()-1)));
			else{
				double a1 = Math.abs(FilterPeakChange(peaks.get(i-1), peaks.get(i)));
				double a2 = Math.abs(FilterPeakChange(peaks.get(i), peaks.get(i+1)));
				avg_change = (a1+a2)/2;
			}
			
			if(avg_change < PEAKS) continue;
			
			filtered.add(peaks.get(i));
		}
		
		return filtered;
	}
	private static double FilterPeakChange(Point p1, Point p2){
		long difftime = p2.getTimestamp()-p1.getTimestamp();
		if(difftime == 0) return 0.;
		return (p2.getMagnitude()-p1.getMagnitude()) / difftime;
	}
	private static ArrayList<Frame> FrequencyFrames(ArrayList<Point> peaks){
		ArrayList<Frame> frames = new ArrayList<Frame>();
		
		for(int start=0; start<peaks.size(); start++)
			for(int end=start+1; end<peaks.size(); end++){
				double freq = (double) (end-start) / (peaks.get(end).getTimestamp() - peaks.get(start).getTimestamp());
				if(freq > FRAMES) frames.add(new Frame(start, end));
			}
		
		return frames;
	}
	private static Frame LargestFrame(ArrayList<Frame> frames){
		if(frames.size() == 0) return null;
		
		Frame largest = frames.get(0);
		
		for(Frame frame : frames)
			if(largest.getEnd() - largest.getStart() < frame.getEnd() - frame.getStart())
				largest = frame;
		
		return largest;
	}
	
}
