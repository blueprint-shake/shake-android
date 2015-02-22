package Acceleration;

public class Frame {

	private int start;
	private int end;
	
	public Frame(int start, int end){
		this.start = start;
		this.end = end;
	}
	
	public int getStart(){
		return start;
	}
	public int getEnd(){
		return end;
	}
	
	public String toString(){
		return "{end:"+end+", start:"+start+"}";
	}
	
}
