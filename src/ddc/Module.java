package ddc;

import java.util.ArrayList;

import general.CommonObject;
import request.Request;

public class Module extends CommonObject {
	private int capacity = 0;
	private int load = 0;
	private ArrayList<Request> occupiedVMs = new ArrayList<>();
	private double reliability = 0;

	public Module(String name, int index, String comments, int capacity, double reliability) {
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

	public int getLoad() {
		return load;
	}

	public void setLoad(int load) {
		this.load = load;
	}

	public ArrayList<Request> getOccupiedVMs() {
		return occupiedVMs;
	}

	public void setOccupiedVMs(ArrayList<Request> occupiedVMs) {
		this.occupiedVMs = occupiedVMs;
	}

	public double getReliability() {
		return reliability;
	}

	public void setReliability(double reliability) {
		this.reliability = reliability;
	}

}
