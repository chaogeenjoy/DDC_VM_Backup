package ddc;

import java.util.HashMap;
import java.util.Random;

import general.Parameter;

public class DDC {
	private HashMap<String, Computing> CPUs = new HashMap<String, Computing>();
	private HashMap<String, Memory> memorys = new HashMap<String, Memory>();
	private HashMap<String, Storage> storages = new HashMap<String, Storage>();

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

	public HashMap<String, Storage> getStorages() {
		return storages;
	}

	public void setStorages(HashMap<String, Storage> storages) {
		this.storages = storages;
	}

	public void createDDC() {
		Random r = new Random(1000);
		for (int i = 0; i < Parameter.SERVER_NUM; i++) {
			Computing c = new Computing("CPU" + i, i, null, Parameter.CPU_PER_SERVER, Parameter.moduleReliability(r));
			this.getCPUs().put(c.getName(), c);

			Memory m = new Memory("Memory" + i, i, null, Parameter.MEMORY_PER_SERVER, Parameter.moduleReliability(r));
			this.getMemorys().put(m.getName(), m);

			Storage s = new Storage("Storage" + i, i, null, Parameter.STORAGE_PER_SERVER,
					Parameter.moduleReliability(r));
			this.getStorages().put(s.getName(), s);
		}
	}
	
	public void createServers() {
		
	}
}
