package request;

import general.CommonObject;
import tdc.Server;

public class Request extends CommonObject {
	private int cpuDemand;
	private int memDemand;
	private int diskDemand;
	private double reliabilityReq;
	private double practicalReli;
	private Server working;
	private Server backup;
	public Request(String name, int index, String comments, int cpuDemand, int memDemand, int diskDemand,
			double reliabilityReq) {
		super(name, index, comments);
		this.cpuDemand = cpuDemand;
		this.memDemand = memDemand;
		this.diskDemand = diskDemand;
		this.reliabilityReq = reliabilityReq;
	}
	public int getCpuDemand() {
		return cpuDemand;
	}
	public void setCpuDemand(int cpuDemand) {
		this.cpuDemand = cpuDemand;
	}
	public int getMemDemand() {
		return memDemand;
	}
	public void setMemDemand(int memDemand) {
		this.memDemand = memDemand;
	}
	public int getDiskDemand() {
		return diskDemand;
	}
	public void setDiskDemand(int diskDemand) {
		this.diskDemand = diskDemand;
	}
	public double getReliabilityReq() {
		return reliabilityReq;
	}
	public void setReliabilityReq(double reliabilityReq) {
		this.reliabilityReq = reliabilityReq;
	}
	public double getPracticalReli() {
		return practicalReli;
	}
	public void setPracticalReli(double practicalReli) {
		this.practicalReli = practicalReli;
	}
	public Server getWorking() {
		return working;
	}
	public void setWorking(Server working) {
		this.working = working;
	}
	public Server getBackup() {
		return backup;
	}
	public void setBackup(Server backup) {
		this.backup = backup;
	}

}
