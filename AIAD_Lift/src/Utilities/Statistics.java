package Utilities;

import java.util.concurrent.TimeUnit;

import uchicago.src.sim.engine.TickCounter;

public class Statistics {
	private static double callStartTime = 0; 
	private static double callEndTime = 0;
	private static double callResponseTime = 0;
	private static double callResponseTimeMil = 0;
	
	
	public static double calculateResponseTime(){
		callResponseTimeMil = callEndTime - callStartTime;
	
		
		System.out.println("Start: " + callStartTime);
		System.out.println("End: " + callEndTime);
		System.out.println("Time1: " + callResponseTimeMil);
		System.out.println("Time: " + callResponseTime);
		
		return callResponseTime;
	}
	
	public double getCallStartTime() {
		return callStartTime;
	}
	public static void setCallStartTime(double StartTime) {
		callStartTime = StartTime;
	}
	public double getCallEndTime() {
		return callEndTime;
	}
	public static void setCallEndTime(double EndTime) {
		callEndTime = EndTime;
	}
	public double getCallResponseTime() {
		return callResponseTime;
	}
	public void setCallResponseTime(double callResponseTime) {
		this.callResponseTime = callResponseTime;
	}
	
	
}
