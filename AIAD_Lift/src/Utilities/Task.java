package utilities;

public class Task {
	private int originFloor;
	private int destFloor;


	public Task(int o, int d){
		this.originFloor = o;
		this.destFloor = d;
	}

	public Direction getDirection(){
		if(originFloor < destFloor)
			return Direction.UP;
		else
			return Direction.DOWN;
	}

	public int getOriginFloor(){
		return originFloor;
	}

	public int getDestFloor(){
		return destFloor;
	}
	

}
