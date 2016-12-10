package Utilities;

import java.util.ArrayList;
import java.util.Collections;

public class Statistics {

	private static long programEndTime;
	private static long programStartTime;
	private static long programTime;
	private static int callsAnswered;	
	private static int nrLifts;
	private static ArrayList<Integer> callsPerLift;
	private static ArrayList<Double> timePerLift;
	private static ArrayList<Double> waitTimePerLift;

	public Statistics(int nLifts){
		nrLifts = nLifts;
		callsPerLift = new ArrayList<Integer>(Collections.nCopies(nrLifts, 0));
		timePerLift = new ArrayList<Double>(Collections.nCopies(nrLifts, (double) 0));
		waitTimePerLift = new ArrayList<Double>(Collections.nCopies(nrLifts, (double) 0));
	}

	public static void calculateStatistics(){
		long pTime = programEndTime - programStartTime; 

		programTime  = (long) (pTime / 1000);
	}

	public static void addCall(){
		callsAnswered++;
	}

	public static void addCallToLift(int ID){
		int previousCalls = callsPerLift.get(ID-1);
		callsPerLift.set(ID-1, previousCalls+1);
	}

	public static void addTimeToLift(int ID, double time){		
		double timeSec = (double) (time / 1000000000);		
		double previousTimes = timePerLift.get(ID-1);		

		timePerLift.set(ID-1, (previousTimes + timeSec));


	}

	public static ArrayList<Double> calcTimePerLift(ArrayList<Integer> calls, ArrayList<Double> times){
		ArrayList<Double> calcArray = new ArrayList<Double>();

		for(int i = 0; i < times.size(); i++){
			calcArray.add(times.get(i)/calls.get(i));
		}

		return calcArray;
	}

	public static double calcMediumWaitTime(ArrayList<Double> times){
		double mediumWaitTime, time = 0;

		for(int i = 0; i < times.size(); i++){
			if(times.get(i) > 0)
			time +=  times.get(i);
		}
		mediumWaitTime = time/times.size();

		return mediumWaitTime;
	}


	public static void printStatistics(){
		System.out.println("-----STATISTICS-----");
		System.out.println("TOTAL TIME : " + programTime);
		System.out.println("TOTAL CALLS ANSWERED: " + callsAnswered + "\n");
		System.out.println("CALLS PER LIFT: " + callsPerLift);
		System.out.println("TIME PER LIFT: " + timePerLift);
		waitTimePerLift = calcTimePerLift(callsPerLift, timePerLift);
		double mediumWaitTime = calcMediumWaitTime(waitTimePerLift);
		System.out.println("MEDIUM WAITING TIME PER LIFT: " + waitTimePerLift);
		System.out.println("MEDIUM WAITING TIME OF BUILDING: " + mediumWaitTime + "\n");
		System.out.println("------------------------");
	}

	public static double getProgramEndTime() {
		return programEndTime;
	}

	public static void setProgramEndTime(long programEnd) {
		Statistics.programEndTime = programEnd;
	}

	public static double getProgramStartTime() {
		return programStartTime;
	}

	public static void setProgramStartTime(long programStart) {
		Statistics.programStartTime = programStart;
	}

	public static double getProgramTime() {
		return programTime;
	}

	public static void setProgramTime(long programTime) {
		Statistics.programTime = programTime;
	}

	public static int getCallsAnswered() {
		return callsAnswered;
	}

	public static void setCallsAnswered(int callsAnswered) {
		Statistics.callsAnswered = callsAnswered;
	}

	public static int getNrLifts() {
		return nrLifts;
	}

	public static void setNrLifts(int nrLifts) {
		Statistics.nrLifts = nrLifts;
	}

	public static ArrayList<Double> getTimePerLift() {
		return timePerLift;
	}

	public static void setTimePerLift(ArrayList<Double> timePerLift) {
		Statistics.timePerLift = timePerLift;
	}

}
