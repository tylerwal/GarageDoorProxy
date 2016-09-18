package lookups;

public enum DoorState {
	Close(0),
	Open(1),
	Closed(2)
	
	private int value
	
	public int getValue() {
		return value
	}
	
	DoorState(int input) {
		value = input;
	}
}
