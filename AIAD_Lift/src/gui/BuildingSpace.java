package gui;


import agents.BuildingAgent;
import agents.LiftAgent;
import uchicago.src.sim.space.Object2DGrid;
import utilities.Door;


public class BuildingSpace {

	private Object2DGrid doorSpace;
	private Object2DGrid liftSpace;
	private Object2DGrid doors;
	long startTimeCall;
	long endTimeCall;
	long timeToRespond;

	public BuildingSpace(int xSize, int ySize){
		startTimeCall = 0;
		doorSpace = new Object2DGrid(xSize, ySize);
		doors = new Object2DGrid(xSize, ySize);
		liftSpace = new Object2DGrid(xSize, 1);

		for(int i = 0; i < xSize; i++){
			for(int j = 0; j < ySize; j++){
				doorSpace.putObjectAt(i,j,new Integer(0));
			}
		}
	}

	public Object2DGrid getCurrentDoorSpace(){
		return doorSpace;
	}

	public Object2DGrid getCurrentLiftSpace(){
		return liftSpace;
	}

	public boolean addAgent(LiftAgent agent){
		liftSpace.putObjectAt(agent.getX(), agent.getY(),agent);
		return true;
	}

	public Object2DGrid getDoors(){
		return doors;
	}

	public boolean addDoor(Door door){
		doors.putObjectAt(door.getX(), door.getY(),door);
		return true;
	}

	public void addBuildingSpace(BuildingAgent agent){
		agent.setBuilding(this);
	}

	public void addBuildingSpaceL(LiftAgent agent){
		agent.setBuilding(this);
	}

	public void callLiftSpace(int originfloor, int destfloor){
		int floorWidth = doorSpace.getSizeX();

		if(originfloor > destfloor){
			for(int i = 0; i < floorWidth; i++){
				if(doorSpace.getObjectAt(i, originfloor).equals(0))
					doorSpace.putObjectAt(i, originfloor, new Integer(1));
				else if(doorSpace.getObjectAt(i, originfloor).equals(1))
					doorSpace.putObjectAt(i, originfloor, new Integer(5));
			}
		}
		else{
			for(int i = 0; i < floorWidth; i++){
				if(doorSpace.getObjectAt(i, originfloor).equals(0))
					doorSpace.putObjectAt(i, originfloor, new Integer(2));
				else if(doorSpace.getObjectAt(i, originfloor).equals(2))
					doorSpace.putObjectAt(i, originfloor, new Integer(4));
			}
		}
	}

	public void removeCallSpace(int originfloor){
		int floorWidth = doorSpace.getSizeX();

		for(int i = 0; i < floorWidth; i++){
			if(doorSpace.getObjectAt(i, originfloor).equals(4))
				doorSpace.putObjectAt(i, originfloor, new Integer(2));
			else if(doorSpace.getObjectAt(i, originfloor).equals(5))
				doorSpace.putObjectAt(i, originfloor, new Integer(1));
			else
				doorSpace.putObjectAt(i, originfloor, new Integer(0));
		}

	}
}