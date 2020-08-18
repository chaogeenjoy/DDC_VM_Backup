package request;

import general.CommonObject;

public class VirtualMachine extends CommonObject {
	private int cpuDemand;
	private int memDemand;
	private int stoDemand;
	private double reliabilityReq;
	public VirtualMachine(String name, int index, String comments, int cpuDemand, int memDemand, int stoDemand,
			double reliabilityReq) {
		super(name, index, comments);
		this.cpuDemand = cpuDemand;
		this.memDemand = memDemand;
		this.stoDemand = stoDemand;
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
	public int getStoDemand() {
		return stoDemand;
	}
	public void setStoDemand(int stoDemand) {
		this.stoDemand = stoDemand;
	}
	public double getReliabilityReq() {
		return reliabilityReq;
	}
	public void setReliabilityReq(double reliabilityReq) {
		this.reliabilityReq = reliabilityReq;
	}

}
