package agents;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import gui.BuildingSpace;
import sajas.core.Agent;
import sajas.core.behaviours.*;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import utilities.Direction;
import utilities.Statistics;
import utilities.Task;

public class LiftAgent extends Agent implements Drawable{
	private int y;
	private int door;
	private int maxWeight;
	private Direction direction;
	private static int IDNumber = 0;
	private int ID;
	private int nrfloors;
	public ArrayList<Task> LiftTasks;
	public boolean onBoard;
	private BuildingSpace building;
	private BufferedImage image;
	private int algorithm;
	private long callStartTime;
	private long callEndTime;
	AID buildingAID;


	public LiftAgent(int bmaxWeight, int nrfloors, int algorithm){
		this.algorithm = algorithm;
		this.y = nrfloors - 1;
		this.maxWeight = ThreadLocalRandom.current().nextInt(1, bmaxWeight + 1);
		IDNumber++;
		ID = IDNumber;
		door = ID-1;
		direction = Direction.STOP;
		this.nrfloors = nrfloors;
		this.LiftTasks = new ArrayList<Task>();
		this.setCallStartTime(0);
		this.setCallEndTime(0);


		//boolean onBoard = true;

		try {
			image = ImageIO.read(new File("img/elevator.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setBuilding(BuildingSpace building){
		this.building = building;
	}


	public void setup(){
		System.out.println(getAID().getName() + " was initialized with " + maxWeight + " maximum weight.");
		addBehaviour(new MsgListener(this));		
	}



	@Override
	public void draw(SimGraphics L) {
		L.drawImageToFit(image);
	}

	public void addLiftTasks(int o, int d){
		LiftTasks.add(new Task(o, d));
	}

	public Task getLastTask(){
		return LiftTasks.get(LiftTasks.size() - 1);
	}

	public Direction getLastTaskDir(){
		return getLastTask().getDirection();
	}

	@Override
	public int getX() {
		return door;
	}
	@Override
	public int getY() {
		return y;
	}

	public int getID(){
		return ID;
	}

	public int getMaxWeight(){
		return maxWeight;
	}


	public void goToOrigin(){
		if(LiftTasks.size()>0){
			int o = LiftTasks.get(0).getOriginFloor();

			if(o < getY() && !onBoard ){
				goesUp();
			}
			else if(o > getY() && !onBoard){
				goesDown();
			}
			else if(o == getY() || onBoard ){ 
				onBoard = true;
				goToDestiny();
				building.removeCallSpace(o);
			}
		}
	}

	public void goToDestiny(){

		int o = LiftTasks.get(0).getOriginFloor();
		int d = LiftTasks.get(0).getDestFloor();

		if(d < getY() && onBoard){
			//building.removeCallSpace(o);
			goesUp();
		}
		else if(d > getY() && onBoard){
			//	building.removeCallSpace(o);
			goesDown();
		}								
		else if(d == getY() && onBoard){
			onBoard = false;
			LiftTasks.remove(0);
			int o1 = (nrfloors-1) - o;
			int d1 = (nrfloors-1) - d;
			System.out.println("Lift " + ID +" went from " + o1 + " to " + d1+ "\n");
			TaskDone();
			this.setCallEndTime(System.nanoTime());
			long time = this.callEndTime - this.callStartTime;
			
			Statistics.addTimeToLift(ID, time);
			Statistics.addCallToLift(ID);
		}
		//building.removeCallSpace(o);
	}
	/*
	public void callAnswered(int o){
		building.removeCallSpace(o);
	}
	 */

	public void goesUp(){
		y--;
		direction = Direction.UP;
	}


	public void goesDown() {
		y++;
		direction = Direction.DOWN;

	}
	/*
	public void stopLift(){
		direction = Direction.STOP;
	}


	 */
	public void setLiftState(Direction dir){
		this.direction = dir;
	}

	public long getCallStartTime() {
		return callStartTime;
	}

	public void setCallStartTime(long callStartTime) {
		this.callStartTime = callStartTime;
	}

	public long getCallEndTime() {
		return callEndTime;
	}

	public void setCallEndTime(long callEndTime) {
		this.callEndTime = callEndTime;
	}

	public void TaskDone(){
		ACLMessage done = new ACLMessage(ACLMessage.INFORM);
		done.addReceiver(buildingAID);
		done.setConversationId("taskdone");
		done.setContent(getAID().getName() + " completed the task!");
		send(done);
	}


	public class MsgListener extends CyclicBehaviour{
		private static final long serialVersionUID = 1L;
		int step = 0;
		int requestOriginFloor = 0;
		int requestDestinyFloor = 0;

		public MsgListener(Agent a){
			super(a);
		}

		@Override
		public void action() {

			switch(step){
			case 0:		
				ACLMessage message = myAgent.receive();
				if(message!=null && message.getPerformative() == ACLMessage.CFP){
					String msg = message.getContent();	

					String info[] = msg.split("/");
					String floorAndDirection = info[0];
					int nrPeople = Integer.parseInt(info[info.length - 1]);

					String cost = calculateCost(floorAndDirection, nrPeople);

					ACLMessage reply = message.createReply();
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(cost);


					//System.out.println("Sou o elevador " + getAID().getName() + " e o meu custo é de " + cost);
					myAgent.send(reply);
					step = 1;
				}
				else{
					block();
				}

				break;
			case 1:
				ACLMessage rsp = myAgent.receive();

				if (rsp != null) {
					buildingAID = rsp.getSender();
					if (rsp.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
						String dFloor = rsp.getContent();
						int destY = Integer.parseInt(dFloor);
						//int destFloor = nrfloors - destY;
						addLiftTasks(requestOriginFloor, destY);
						setCallStartTime(System.nanoTime());


						//System.out.println("Elevador " + ID + " aceite!");
					}
					else{
						//	System.out.print("Elevador " + ID+ " rejeitado\n");
					}
					step = 0;
				}
				break;
			}
		}

		private String calculateCost(String msg, int nrPeople){
			String info[] = msg.split("-");
			String requestDirection = " ";

			//Grab the first
			requestOriginFloor = Integer.parseInt(info[0]);

			if(algorithm == 3)
				requestDestinyFloor = Integer.parseInt(info[info.length - 1]);
			else if(algorithm != 3)
				requestDirection = info[info.length - 1];

			int c = 0;

			if(nrPeople > maxWeight){
				c = 0;
				System.out.println("Lift " + ID + " doesn't have capacity to answer the call.");
			}
			else
			{

				switch(algorithm){
				case 1: 
					if(LiftTasks.size()>0){
						if(requestOriginFloor == getLastTask().getDestFloor())
							c = 1000 - LiftTasks.size() + maxWeight;
						else if(requestOriginFloor > getLastTask().getDestFloor() && direction.equalsName("DOWN") || requestOriginFloor < getLastTask().getDestFloor() && direction.equalsName("UP")){ // em direcao ao piso de origem
							if(direction.equalsName(requestDirection)){ // mesma direcao do pedido
								c = 1000 - Math.abs(requestOriginFloor-getLastTask().getDestFloor()) - LiftTasks.size();
							}
							else{
								c = 1000 - Math.abs(requestOriginFloor-getLastTask().getDestFloor()) - LiftTasks.size() - 2;
							}
						}
						else{
							c = 1000 - Math.abs(requestOriginFloor-getLastTask().getDestFloor()) - LiftTasks.size() - 10;
						}

					}
					else{
						if(requestOriginFloor == y)
							c = 1000;
						else if(requestOriginFloor > y && direction.equalsName("DOWN") || requestOriginFloor < y && direction.equalsName("UP")){ // em direcao ao piso de origem
							if(direction.equalsName(requestDirection)){ // mesma direcao do pedido
								c = 1000 - Math.abs(requestOriginFloor-y);
							}
							else{
								c = 1000 - Math.abs(requestOriginFloor-y)- 2;
							}
						}
						else{
							c = 1000 - Math.abs(requestOriginFloor-y);
						}
					}

					break;
				case 2:  
					if(LiftTasks.size()>0){
						if(requestOriginFloor == getLastTask().getDestFloor())
							c = 1000;
						else{
							c = 1000 - Math.abs(requestOriginFloor-getLastTask().getDestFloor()) - LiftTasks.size();
						}

					}
					else{
						if(requestOriginFloor == y)
							c = 1000;
						else{
							c = 1000 - Math.abs(requestOriginFloor-y);
						}
					}
					break;
				case 3:
					int totalLifts = building.getCurrentLiftSpace().getSizeX();
					int totalFloors = building.getCurrentDoorSpace().getSizeY();

					int middleLift = totalLifts/2 + 1;
					int middleFloor = totalFloors/2 + 1;

					if(LiftTasks.size()>0){
						if(ID < middleLift){
							if(requestOriginFloor <= middleFloor && requestDestinyFloor <= middleFloor)
								c = 1000 - Math.abs(requestOriginFloor - getLastTask().getDestFloor());
							else if(requestOriginFloor >= middleFloor && requestDestinyFloor <= middleFloor)
								c = 800 - Math.abs(requestOriginFloor - getLastTask().getDestFloor()); 
							else if(requestOriginFloor <= middleFloor && requestDestinyFloor >= middleFloor)
								c = 700 - Math.abs(requestOriginFloor - getLastTask().getDestFloor()); 
							else if(requestOriginFloor > middleFloor && requestDestinyFloor > middleFloor)
								c = 600 - Math.abs(requestOriginFloor - getLastTask().getDestFloor()); 
							else
								c = 500 - Math.abs(requestOriginFloor - getLastTask().getDestFloor()); 

						}
						else
						{

							if(requestOriginFloor >= middleFloor && requestDestinyFloor >= middleFloor)
								c = 1000 - Math.abs(requestOriginFloor - getLastTask().getDestFloor()); 
							else if(requestOriginFloor <= middleFloor && requestDestinyFloor >= middleFloor)
								c = 800 - Math.abs(requestOriginFloor - getLastTask().getDestFloor()); 
							else if(requestOriginFloor >= middleFloor && requestDestinyFloor <= middleFloor)
								c = 700 - Math.abs(requestOriginFloor - getLastTask().getDestFloor());  
							else if(requestOriginFloor < middleFloor && requestDestinyFloor < middleFloor)
								c = 600 - Math.abs(requestOriginFloor - getLastTask().getDestFloor());  
							else 
								c = 500 - Math.abs(requestOriginFloor - getLastTask().getDestFloor()); 
						}
					}
					else {
						if(ID < middleLift){
							if(requestOriginFloor <= middleFloor && requestDestinyFloor <= middleFloor)
								c = 1000 - Math.abs(requestOriginFloor - y);
							else if(requestOriginFloor >= middleFloor && requestDestinyFloor <= middleFloor)
								c = 800 - Math.abs(requestOriginFloor - y);
							else if(requestOriginFloor <= middleFloor && requestDestinyFloor >= middleFloor)
								c = 700 - Math.abs(requestOriginFloor - y); 
							else if(requestOriginFloor > middleFloor && requestDestinyFloor > middleFloor)
								c = 600 - Math.abs(requestOriginFloor - y);
							else
								c = 500 - Math.abs(requestOriginFloor - y);

						}
						else
						{
							if(requestOriginFloor >= middleFloor && requestDestinyFloor >= middleFloor)
								c = 1000 - Math.abs(requestOriginFloor - y);
							else if(requestOriginFloor <= middleFloor && requestDestinyFloor >= middleFloor)
								c = 800 - Math.abs(requestOriginFloor - y); 
							else if(requestOriginFloor >= middleFloor && requestDestinyFloor <= middleFloor)
								c = 700 - Math.abs(requestOriginFloor - y); 
							else if(requestOriginFloor < middleFloor && requestDestinyFloor < middleFloor)
								c = 600 - Math.abs(requestOriginFloor - y); 
							else 
								c = 500 - Math.abs(requestOriginFloor - y);

						}
					}
					break;

				}

			}
			String cost = c + "";
			return cost;
		}

	}

}



