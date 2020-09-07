package tdc;

import java.util.ArrayList;
import java.util.HashMap;

import ddc.Computing;
import ddc.DDC;
import ddc.Memory;
import ddc.Disk;
import general.Parameter;

public class TDC {
	private HashMap<String, Server> servers = new HashMap<String, Server>();

	public HashMap<String, Server> getServers() {
		return servers;
	}

	public void setServers(HashMap<String, Server> servers) {
		this.servers = servers;
	}

	public void convertingDDCToTDC(DDC ddc) {
		ArrayList<Computing> cpus = new ArrayList<Computing>();
		ArrayList<Memory> memorys = new ArrayList<Memory>();
		ArrayList<Disk> disks = new ArrayList<Disk>();
		cpus.addAll(ddc.getCPUs().values());
		memorys.addAll(ddc.getMemorys().values());
		disks.addAll(ddc.getDisks().values());

		for (int i = 0; i < Parameter.SERVER_NUM; i++) {
			Server s = new Server("Server" + i, i, null);
			this.getServers().put(s.getName(),s);
			s.setCpu(cpus.get(i));
			s.setMemory(memorys.get(i));
			s.setDisk(disks.get(i));
			s.setReliaiblity(
					s.getCpu().getReliability() * s.getMemory().getReliability() * s.getDisk().getReliability());
		}

	}

}
