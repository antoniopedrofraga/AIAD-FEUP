package Launcher;
import java.awt.Color;
import java.util.ArrayList;

import Agents.BuildingAgent;
import Agents.LiftAgent;
import Utilities.Door;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;

import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;

import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.gui.Object2DDisplay;





public class LiftModel extends Repast3Launcher {

	//Default Values
	private static final int NRLIFTS = 4;
	private static final int NRFLOORS = 15;
	private static final int GENCALLSFREQ = 500;
	private static final int LIFTSPEED = 30;
	private static final int BMAXWEIGHT = 5;
	private static final int CRITERIAG = 1;

	private int nrLifts = NRLIFTS;
	private int nrFloors = NRFLOORS;
	private int liftspeed = LIFTSPEED;
	private int callFrequency = GENCALLSFREQ;
	private int buildingMaxWeight = BMAXWEIGHT;
	private int criteriaGroup = CRITERIAG;

	private ContainerController mainContainer;

	private BuildingSpace buildingSpace;
	private DisplaySurface displaySurf;

	private ArrayList<LiftAgent> liftList;
	private BuildingAgent buildingAgent;

	private ArrayList<Door> doorList;

	public String getName(){
		return "My Lift Model";
	}

	public void setup(){
		//System.out.println("Running setup");
		System.out.println("Please choose a group of criteria:" + "\n\n" 
				+ "1. -Lift proximity to the call; \n   -Direction in which the lift is going; \n   -Number of tasks per lift; \n   -Lift capacity.\n\n"
				+ "2. -Lift proximity to the call. \n\n"
				+ "3. -Building sectorisation; \n   -Lift proximity to the call. \n\n");
		super.setup();
		//buildingSpace = null;
		liftList = new ArrayList<LiftAgent>();
		buildingAgent = new BuildingAgent();
		doorList = new ArrayList<Door>(); 

		if (displaySurf != null){
			displaySurf.dispose();
		}
		displaySurf = null;

		displaySurf = new DisplaySurface(this, "Lift Model Window 1");

		registerDisplaySurface("Lift Model Window 1", displaySurf);



	}


	public void begin(){
		super.begin();
		buildModel();
		buildSchedule();
		buildDisplay();

		int maxWeightinLift = 0;
		for(int i = 0; i < liftList.size(); i++){
			if(liftList.get(i).getMaxWeight() > maxWeightinLift)
				maxWeightinLift = liftList.get(i).getMaxWeight();
		}
		buildingAgent.setBuildingMaxWeight(maxWeightinLift);
	
		displaySurf.display();	
	}


	private void buildSchedule() {
		System.out.println("Running buildSchedule");


		class callLift extends BasicAction {
			public void execute() {
				buildingAgent.generateCall(nrFloors);					
			}
		}

		class doTask extends BasicAction{
			public void execute(){
				for(int i = 0; i < liftList.size(); i++){
					LiftAgent lift = liftList.get(i);
					lift.goToOrigin();					
				}
			}
		}

		getSchedule().scheduleActionAtInterval(1, displaySurf, "updateDisplay", Schedule.LAST);
		getSchedule().scheduleActionAtInterval(callFrequency, new callLift());
		getSchedule().scheduleActionAtInterval(liftspeed, new doTask());
	}

	private void buildModel() {
		System.out.println("Running buildModel");
		buildingSpace = new BuildingSpace(nrLifts, nrFloors);
		buildingSpace.addBuildingSpace(buildingAgent);

		for(int i = 0; i < liftList.size(); i++){
			buildingSpace.addBuildingSpaceL(liftList.get(i));
		}



		for(int i = 0; i < nrFloors; i++){
			for(int j = 0; j < nrLifts; j++){				
				Door d = new Door(j,i);
				doorList.add(d);
			}
		}
	}

	private void buildDisplay(){
		System.out.println("Running buildDisplay\n");


		ColorMap map = new ColorMap();
		map.mapColor(0, Color.white);
		map.mapColor(1, Color.green);
		map.mapColor(2, Color.red);

		Value2DDisplay displayBuilding = new Value2DDisplay(buildingSpace.getCurrentDoorSpace(), map);
		Object2DDisplay displayDoors = new Object2DDisplay(buildingSpace.getDoors());
		Object2DDisplay displayAgents = new Object2DDisplay(buildingSpace.getCurrentLiftSpace());
		displayAgents.setObjectList(liftList);		
		displayDoors.setObjectList(doorList);


		displaySurf.addDisplayable(displayBuilding, "Building");
		displaySurf.addDisplayable(displayDoors, "Doors");
		displaySurf.addDisplayable(displayAgents, "Lifts");
	}


	public String[] getInitParam(){
		String[] initParams = { "NrLifts", "NrFloors", "Liftspeed", "CallFrequency", "BuildingMaxWeight", "CriteriaGroup"};
		return initParams;
	}

	public int getNrLifts(){
		return nrLifts;
	}

	public void setNrLifts(int na){
		if(na>0)
			nrLifts = na;
		else 
			System.out.println("There cannot be less than 1 lift");
	}


	public int getNrFloors() {
		return nrFloors;
	}

	public void setNrFloors(int nrFloors) {
		if(nrFloors>1)
			this.nrFloors = nrFloors;
		else 
			System.out.println("There cannot be less than 2 floors");
	}

	public static void main(String[] args) {
		SimInit init = new SimInit();
		LiftModel model = new LiftModel();
		init.loadModel(model, "", false);
	}

	public int getLiftspeed() {
		return liftspeed;
	}

	public void setLiftspeed(int liftspeed) {
		this.liftspeed = liftspeed;
	}

	private void launchAgents(){
		for(int i = 0; i < nrLifts; i++){
			LiftAgent a = new LiftAgent(buildingMaxWeight, nrFloors, criteriaGroup);
			liftList.add(a);

			try {
				mainContainer.acceptNewAgent("Lift " + a.getID(), a).start();
				buildingAgent.addAID(a.getAID());
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			mainContainer.acceptNewAgent("Building Agent", buildingAgent).start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		buildingAgent.setAlgorithm(criteriaGroup);
	}

	@Override
	protected void launchJADE() {
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);		

		launchAgents();
	}

	public int getCallFrequency() {
		return callFrequency;
	}

	public void setCallFrequency(int callFrequency) {
		this.callFrequency = callFrequency;
	}

	public int getBuildingMaxWeight() {
		return buildingMaxWeight;
	}

	public void setBuildingMaxWeight(int maxWeight) {
		this.buildingMaxWeight = maxWeight;
	}

	public int getCriteriaGroup() {
		return criteriaGroup;
	}

	public void setCriteriaGroup(int algorithm) {
		this.criteriaGroup = algorithm;
	}


}
