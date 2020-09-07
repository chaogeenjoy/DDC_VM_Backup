package ddc;

import java.util.HashMap;
import java.util.Random;

import general.Parameter;

public class DDC {
	private HashMap<String, Computing> CPUs = new HashMap<String, Computing>();
	private HashMap<String, Memory> memorys = new HashMap<String, Memory>();
	private HashMap<String, Disk> disks = new HashMap<String, Disk>();

	public HashMap<String, Computing> getCPUs() {
		return CPUs;
	}

	public void setCPUs(HashMap<String, Computing> cPUs) {
		CPUs = cPUs;
	}

	public HashMap<String, Memory> getMemorys() {
		return memorys;
	}

	public void setMemorys(HashMap<String, Memory> memorys) {
		this.memorys = memorys;
	}

	public HashMap<String, Disk> getDisks() {
		return disks;
	}

	public void setDisks(HashMap<String, Disk> disks) {
		this.disks = disks;
	}

	public void createDDC() {
		Random r = new Random(1000);
		for (int i = 0; i < Parameter.SERVER_NUM; i++) {
			Computing c = new Computing("CPU" + i, i, null, Parameter.CPU_PER_SERVER, Parameter.moduleReliability(r));
			this.getCPUs().put(c.getName(), c);

			Memory m = new Memory("Memory" + i, i, null, Parameter.MEMORY_PER_SERVER, Parameter.moduleReliability(r));
			this.getMemorys().put(m.getName(), m);

			Disk s = new Disk("Disk" + i, i, null, Parameter.DISK_PER_SERVER,
					Parameter.moduleReliability(r));
			this.getDisks().put(s.getName(), s);
		}
	}
	
	public void createServers() {
		
	}
}
