package Utilities;

import java.text.DecimalFormat;
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
	private static ArrayList<Double> noUseTimePerLift;
	private static ArrayList<Double> useRatePerLift;
	private static ArrayList<Double> waitTimePerLift;
	private static ArrayList<Double> maxWaitTimePerLift;
	private static ArrayList<Double> minWaitTimePerLift;

	public Statistics(int nLifts){
		nrLifts = nLifts;
		callsPerLift = new ArrayList<Integer>(Collections.nCopies(nrLifts, 0));
		timePerLift = new ArrayList<Double>(Collections.nCopies(nrLifts, (double) 0));
		waitTimePerLift = new ArrayList<Double>(Collections.nCopies(nrLifts, (double) 0));
		maxWaitTimePerLift = new ArrayList<Double>(Collections.nCopies(nrLifts, (double) 0));
		minWaitTimePerLift = new ArrayList<Double>(Collections.nCopies(nrLifts, (double) 1000));
		noUseTimePerLift = new ArrayList<Double>(Collections.nCopies(nrLifts, (double) 0));
		useRatePerLift = new ArrayList<Double>(Collections.nCopies(nrLifts, (double) 0));
	}

	public static void calculateStatistics(){
		long pTime = programEndTime - programStartTime; 
		programTime  = (long) (pTime / 1000);		
	}
	
	public static void calcNoUseTime(ArrayList<Double> useTime){
		for(int i = 0; i < useTime.size(); i++){
			noUseTimePerLift.set(i, (programTime - timePerLift.get(i)));
		}
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
		
		if(timeSec > maxWaitTimePerLift.get(ID-1))
			maxWaitTimePerLift.set(ID-1, timeSec);
		
		if(timeSec < minWaitTimePerLift.get(ID-1))
			minWaitTimePerLift.set(ID-1, timeSec);

		timePerLift.set(ID-1, (previousTimes + timeSec));


	}

	public static ArrayList<Double> calcTimePerLift(ArrayList<Integer> calls, ArrayList<Double> times){
		ArrayList<Double> calcArray = new ArrayList<Double>();

		for(int i = 0; i < times.size(); i++){
			calcArray.add(times.get(i)/calls.get(i));
		}

		return calcArray;
	}



	public static void printStatistics(){
		System.out.println("\n\n\n");
		System.out.println("---------------------------------------- STATISTICS ----------------------------------------");
		System.out.println("Total calls: " + callsAnswered);
		System.out.println("Max waiting time: " + new DecimalFormat("#.##").format(Collections.max(maxWaitTimePerLift)));
		System.out.println("Min waiting time: " + new DecimalFormat("#.##").format(Collections.min(minWaitTimePerLift)) + "\n");
		
		System.out.println("Calls per Lift:");
		for(int i = 0; i < nrLifts; i++){
			System.out.println("  Lift " + (i+1) + ": " + callsPerLift.get(i));
		}
		System.out.println("");
		calcNoUseTime(timePerLift);
		System.out.println("Use rate per Lift (use time / no use time):");	
		for(int i = 0; i < nrLifts; i++){
			double useRate = timePerLift.get(i)/noUseTimePerLift.get(i);
			System.out.println("  Lift " + (i+1) + ": " + new DecimalFormat("#.##").format(useRate));
		}
		System.out.println("");
		
		System.out.println("Use time per Lift:");
		for(int i = 0; i < nrLifts; i++){
			System.out.println("  Lift " + (i+1) + ": " + new DecimalFormat("#.##").format(timePerLift.get(i)));
		}
		System.out.println("");
		
	
		
		System.out.println("No use time per Lift:");
		for(int i = 0; i < nrLifts; i++){
			System.out.println("  Lift " + (i+1) + ": " + new DecimalFormat("#.##").format(noUseTimePerLift.get(i)));
		}
		System.out.println("");
		
		waitTimePerLift = calcTimePerLift(callsPerLift, timePerLift);
		
		
		System.out.println("Medium waiting time per Lift:");
		for(int i = 0; i < nrLifts; i++){
			System.out.println("  Lift " + (i+1) + ": " + new DecimalFormat("#.##").format(waitTimePerLift.get(i)));
		}
		System.out.println("");
	
		System.out.println("---------------------------------------------------------------------------------------------");
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

	public static ArrayList<Double> getMaxWaitTimePerLift() {
		return maxWaitTimePerLift;
	}

	public static void setMaxWaitTimePerLift(ArrayList<Double> maxWaitTimePerLift) {
		Statistics.maxWaitTimePerLift = maxWaitTimePerLift;
	}

	public static ArrayList<Double> getMinWaitTimePerLift() {
		return minWaitTimePerLift;
	}

	public static void setMinWaitTimePerLift(ArrayList<Double> minWaitTimePerLift) {
		Statistics.minWaitTimePerLift = minWaitTimePerLift;
	}

	public static ArrayList<Double> getNoUseTimePerLift() {
		return noUseTimePerLift;
	}

	public static void setNoUseTimePerLift(ArrayList<Double> noUseTimePerLift) {
		Statistics.noUseTimePerLift = noUseTimePerLift;
	}

	public static ArrayList<Double> getUseRatePerLift() {
		return useRatePerLift;
	}

	public static void setUseRatePerLift(ArrayList<Double> useRatePerLift) {
		Statistics.useRatePerLift = useRatePerLift;
	}

}
