package ddc;

import general.CommonObject;

public class Computing extends CommonObject{
	private int capacity = 0;
	private double reliability = 0;
	
	public Computing(String name, int index, String comments, int capacity, double reliability) {
		super(name, index, comments);
		this.capacity = capacity;
		this.reliability = reliability;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public double getReliability() {
		return reliability;
	}

	public void setReliability(double reliability) {
		this.reliability = reliability;
	}
	
}
