package algorithm_design;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import general.Equations;
import request.VirtualMachine;
import tdc.Server;
import tdc.TDC;

public class TDC_Algorithm {
	private int accept = 0;
	private int backup = 0;

	public int getAccept() {
		return accept;
	}

	public void setAccept(int accept) {
		this.accept = accept;
	}

	public int getBackup() {
		return backup;
	}

	public void setBackup(int backup) {
		this.backup = backup;
	}

	public int mapVMBatches(TDC tdc, ArrayList<VirtualMachine> vms) {

		for (int i = 0; i < vms.size(); i++) {
			VirtualMachine vm = vms.get(i);
			// try to map it with single copy
			int res = this.vmMapping(tdc, vm);
			if (res > 0)
				this.setAccept(this.getAccept() + 1);
			if (res == 2)
				this.setBackup(this.getBackup() + 1);

		}
		return this.getAccept();
	}

	private int vmMapping(TDC tdc, VirtualMachine vm) {
		ArrayList<Server> servers = new ArrayList<>();

		// exclude servers that remains insufficient resources
		for (Server s : tdc.getServers().values()) {
			if (s.getCpu().getLoad() + vm.getCpuDemand() > s.getCpu().getCapacity()
					|| s.getMemory().getLoad() + vm.getMemDemand() > s.getMemory().getCapacity()
					|| s.getDisk().getLoad() + vm.getDiskDemand() > s.getDisk().getCapacity())
				continue;
			servers.add(s);
		}

		if (servers.isEmpty())
			return -1;
		// sort servers in ascending order of reliability R_mr + alpha * F_mr
		Collections.sort(servers, new Comparator<Server>() {
			@Override
			public int compare(Server o1, Server o2) {
				double res = o2.getReliaiblity() - o1.getReliaiblity();
				return res > 0 ? 1 : (res < 0 ? -1 : 0);
			}
		});

		if (noBackupTrial(servers, vm))
			return 1;
		if (withBackupTrial(servers, vm))
			return 2;
		return -1;
	}

	private boolean noBackupTrial(ArrayList<Server> servers, VirtualMachine vm) {
		Server target = null;
		int i = 0;
		while (i < servers.size()) {
			Server s = servers.get(i);
			if (s.getReliaiblity() < vm.getReliabilityReq())
				break;
			target = s;
			i++;
		}
		if (target == null)
			return false;

		target.getCpu().setLoad(target.getCpu().getLoad() + vm.getCpuDemand());
		target.getCpu().getOccupiedVMs().add(vm);
		target.getMemory().setLoad(target.getMemory().getLoad() + vm.getMemDemand());
		target.getMemory().getOccupiedVMs().add(vm);
		target.getDisk().setLoad(target.getDisk().getLoad() + vm.getDiskDemand());
		target.getDisk().getOccupiedVMs().add(vm);

		return true;
	}

	private boolean withBackupTrial(ArrayList<Server> servers, VirtualMachine vm) {
		Server t1 = null, t2 = null;
		int i = 0, j = 0;
		while (i < servers.size() && j < servers.size()) {
			Server s1 = servers.get(i);
			Server s2 = servers.get(j);
			double reli = Equations.reli_twoServers(s1, s2);
			if (reli < vm.getReliabilityReq())
				break;
			t1 = s1;
			t2 = s2;
			if (s1.getReliaiblity() >= s2.getReliaiblity())
				i++;
			else
				j++;
		}
		
		if (t1 == null || t2 == null)
			return false;
		
		Server[] ss= {t1,t2};
		for(Server s:ss) {
			s.getCpu().setLoad(s.getCpu().getLoad()+vm.getCpuDemand());
			s.getCpu().getOccupiedVMs().add(vm);
			s.getMemory().setLoad(s.getMemory().getLoad()+vm.getMemDemand());
			s.getMemory().getOccupiedVMs().add(vm);
			s.getDisk().setLoad(s.getDisk().getLoad() + vm.getDiskDemand());
			s.getDisk().getOccupiedVMs().add(vm);
		}
		return true;
	}

}
