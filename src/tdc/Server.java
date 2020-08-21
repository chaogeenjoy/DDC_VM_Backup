package tdc;

import ddc.Computing;
import ddc.Memory;
import ddc.Storage;
import general.CommonObject;

public class Server extends CommonObject {
	private Computing cpu;
	private Memory memory;
	private Storage storage;
	private double reliaiblity;
	
	public Server(String name, int index, String comments) {
		super(name, index, comments);
		// TODO Auto-generated constructor stub
	}

	public Computing getCpu() {
		return cpu;
	}

	public void setCpu(Computing cpu) {
		this.cpu = cpu;
	}

	public Memory getMemory() {
		return memory;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public double getReliaiblity() {
		return reliaiblity;
	}

	public void setReliaiblity(double reliaiblity) {
		this.reliaiblity = reliaiblity;
	}
}
