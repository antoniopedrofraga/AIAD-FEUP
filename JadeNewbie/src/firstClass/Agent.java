package firstClass;

public class Agent extends jade.core.Agent {
	
	private static final long serialVersionUID = 1L;
	
	/* Start behaviour  */
	
	public void setup() {
		System.out.println("Hello Agent!");
	}
	
	/* Kill behaviour */
	
	public void takeDown() {
		System.out.println("Bye Agent!");
	}
	
	/*
	 * addBehaviour();
	 * send();
	 * receive();
	 * getArguments();
	 * 
	 */
}
