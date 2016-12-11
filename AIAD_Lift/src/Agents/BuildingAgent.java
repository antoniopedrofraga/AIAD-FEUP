package Agents;

import sajas.core.Agent;
import sajas.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import Launcher.BuildingSpace;
import jade.core.AID;
import Utilities.Direction;
import Utilities.Statistics;


public class BuildingAgent extends Agent{

	private BuildingSpace building;	
	private ArrayList<AID> AIDLifts;
	private int buildingMaxWeight;
	private int algorithm;
	int i = 0;

	public BuildingAgent(){
		this.AIDLifts = new ArrayList<AID>();
		this.buildingMaxWeight = 0;
		this.algorithm = 1;
	}


	public int getAlgorithm(){
		return algorithm;
	}

	public void setAlgorithm(int a){
		this.algorithm = a;
	}

	public void addAID(AID aid) {
		AIDLifts.add(aid);
	}

	@Override
	protected void setup(){
		//System.out.println("Hello! Building Agent " + getAID().getName() +" is ready.");
		addBehaviour(new ListenTo());		

	}


	public BuildingSpace getBuilding() {
		return building;
	}


	public void setBuilding(BuildingSpace building) {
		this.building = building;
	}


	public void generateCall(int nrFloors){
		Statistics.addCall();
		int nrPeople = ThreadLocalRandom.current().nextInt(1, buildingMaxWeight + 1);
		String direction = null;
		Random generator = new Random();		
		int originrandFloor;

		int rand = generator.nextInt(nrFloors+10);
		if(rand >= nrFloors)
			originrandFloor = nrFloors-1;
		else
			originrandFloor = rand;

		int destrandFloor = generator.nextInt(nrFloors);

		while(originrandFloor == destrandFloor)
			destrandFloor=generator.nextInt(nrFloors);

		if(originrandFloor < destrandFloor){
			addBehaviour(new RequestPerformer(originrandFloor, Direction.UP, destrandFloor, nrPeople));
			direction = "UP";

		}
		else{
			addBehaviour(new RequestPerformer(originrandFloor, Direction.DOWN, destrandFloor, nrPeople));	
			direction = "DOWN";
		}

		int o = (nrFloors-1) - originrandFloor;
		int d = (nrFloors-1) - destrandFloor;
		building.callLiftSpace(originrandFloor, destrandFloor);

		if(algorithm == 3)
			System.out.println("NEW CALL: Origin: "+ o + " Destination: " + d + "    -    " + nrPeople + " people.\n");
		else
			System.out.println("NEW CALL: Origin: "+ o + " Direction: " + direction + "    -    " + nrPeople + " people.\n");

	}



	public int getBuildingMaxWeight() {
		return buildingMaxWeight;
	}

	public void setBuildingMaxWeight(int buildingMaxWeight) {
		this.buildingMaxWeight = buildingMaxWeight;
	}



	private class RequestPerformer extends Behaviour {

		private static final long serialVersionUID = 1L;

		private int repliesCnt; // The counter of replies 
		private int step;
		AID bestLift;
		private int bestScore;

		private int originFloor;
		private Direction direction;
		private int destFloor;
		private int nrPeople;


		public RequestPerformer(int originFloor, Direction d, int destFloor, int nrPeople) {
			this.originFloor = originFloor;
			this.direction = d;	
			this.destFloor = destFloor;
			this.nrPeople = nrPeople;
			this.repliesCnt = 0;
			this.step = 0;
		}

		@Override
		public void action() {
			switch (step) {
			case 0:

				// Send the cfp to all sellers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (AID aid : AIDLifts)
					cfp.addReceiver(aid);

				if(algorithm == 3){
					cfp.setContent(originFloor + "-" + destFloor + "/" + nrPeople);
				}
				else{
					cfp.setContent(originFloor + "-" + direction + "/" + nrPeople);					
				}

				send(cfp);
				step = 1;
				break;
			case 1:
				ACLMessage reply = receive();				

				if (reply != null && reply.getPerformative() == ACLMessage.PROPOSE ) {					
					// Reply received
					int score = Integer.parseInt(reply.getContent());

					if ((bestLift == null || score > bestScore)) {
						// This is the best offer at present
						bestScore = score;
						bestLift = reply.getSender();

					}
					repliesCnt++;
					if (repliesCnt == AIDLifts.size()) {
						if(bestScore == 0)
							step=0;
						else
							step = 2;
					}
				}

				else{
					block();
				}
				break;
			case 2:
				// Send accept to best lift
				ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				accept.addReceiver(bestLift);
				accept.setContent(destFloor + "");
				send(accept);

				// Send reject to others
				ACLMessage reject = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
				for (AID aidr : AIDLifts){
					if(aidr.getName() != bestLift.getName()){
						reject.addReceiver(aidr);
					}
				}
				reject.setContent("don't go");
				send(reject);

				step=3;
				break;
			}
		}
		public boolean done(){
			return (step == 3);
		}
	}

	private class ListenTo extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;
		private MessageTemplate mt;


		@Override
		public void action() {
			mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage reply = myAgent.receive(mt);

			if(reply!=null){
				System.out.println(reply.getContent());
			}

		}

	}


}


