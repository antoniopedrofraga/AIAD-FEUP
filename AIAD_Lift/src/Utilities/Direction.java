package Utilities;

public enum Direction {
	UP ("UP"), 
	DOWN ("DOWN"), 
	STOP ("STOP");
	
	private int f;
	private final String name;       

	private Direction(String s) {
		name = s;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return this.name;
	}

	public int getF() {
		return f;
	}

	public void setF(int f) {
		this.f = f;
	}
}
