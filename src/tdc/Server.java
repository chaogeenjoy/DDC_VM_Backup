package tdc;

import ddc.Computing;
import ddc.Memory;
import ddc.Storage;
import general.CommonObject;

public class Server extends CommonObject {
	private Computing cpu;
	private Memory memory;
	private Storage disk;
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
	public Storage getDisk() {
		return disk;
	}

	public void setDisk(Storage disk) {
		this.disk = disk;
	}

	public double getReliaiblity() {
		return reliaiblity;
	}

	public void setReliaiblity(double reliaiblity) {
		this.reliaiblity = reliaiblity;
	}
}
