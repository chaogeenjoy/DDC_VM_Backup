package algorithm_design;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ddc.Computing;
import ddc.DDC;
import ddc.Disk;
import ddc.Memory;
import ddc.Module;
import general.Equations;
import request.VirtualMachine;

public class DDC_Algorithm_Reliability_First {

	public int mapVMBatches(DDC ddc, ArrayList<VirtualMachine> vms) {
		// sort VMs in descending order of their reliability requests

		/*
		 * Collections.sort(vms, new Comparator<VirtualMachine>() {
		 * 
		 * @Override public int compare(VirtualMachine o1, VirtualMachine o2) { double
		 * diff = o2.getReliabilityReq() - o1.getReliabilityReq(); return diff > 0 ? 1 :
		 * (diff < 0 ? -1 : 0); } });
		 */
//		Collections.shuffle(vms);

		int accept = 0;// how many VMs are accepted
		int backup = 0;// how many VMs are accepted with two copies
		for (int i = 0; i < vms.size(); i++) {
			VirtualMachine vm = vms.get(i);
			// try to map it with single copy
			if (this.mapSingleCopy(ddc, vm, false)) {
				accept++;
				continue;// single mapping succeed, no need to try two copies
			}
			if (this.mapTwoCopies(ddc, vm)) {
				accept++;
				backup++;
				continue;
			}
		}

		System.out.println("total accept VMs\t" + accept + "\r\ntotal accepted with two copies\t" + backup
				+ "\r\ntotal requests num\t" + vms.size());
		return accept;
	}

	public boolean mapTwoCopies(DDC ddc, VirtualMachine vm) {
		ArrayList<Computing> cpus = new ArrayList<>();
		ArrayList<Memory> memories = new ArrayList<>();
		ArrayList<Disk> disks = new ArrayList<>();

		// exclude modules that remains insufficient resources
		for (Computing c : ddc.getCPUs().values()) {
			if (c.getLoad() + vm.getCpuDemand() > c.getCapacity())
				continue;
			cpus.add(c);
		}
		if (cpus.size() < 2)
			return false;//
		for (Memory m : ddc.getMemorys().values()) {
			if (m.getLoad() + vm.getMemDemand() > m.getCapacity())
				continue;
			memories.add(m);
		}
		if (memories.size() < 2)
			return false;
		for (Disk d : ddc.getDisks().values()) {
			if (d.getLoad() + vm.getDiskDemand() > d.getCapacity())
				continue;
			disks.add(d);
		}
		if (disks.size() < 2)
			return false;

		// Sort each module list in descending order or their reliabilities
		Collections.sort(cpus, new Comparator<Computing>() {
			@Override
			public int compare(Computing o1, Computing o2) {
				// TODO Auto-generated method stub
				double dif = o2.getReliability() - o1.getReliability();
				return dif > 0 ? 1 : (dif == 0 ? 0 : -1);
			}
		});
		Collections.sort(memories, new Comparator<Memory>() {
			@Override
			public int compare(Memory o1, Memory o2) {
				// TODO Auto-generated method stub
				double dif = o2.getReliability() - o1.getReliability();
				return dif > 0 ? 1 : (dif == 0 ? 0 : -1);
			}
		});
		Collections.sort(disks, new Comparator<Disk>() {
			@Override
			public int compare(Disk o1, Disk o2) {
				// TODO Auto-generated method stub
				double dif = o2.getReliability() - o1.getReliability();
				return dif > 0 ? 1 : (dif == 0 ? 0 : -1);
			}
		});

		Module[] modules = this.twoCopies_BalanceSelect(cpus, memories, disks, vm);
		if (modules[0] == null)
			return false;

		Computing c1 = (Computing) modules[0];
		c1.setLoad(c1.getLoad() + vm.getCpuDemand());
		c1.getOccupiedVMs().add(vm);

		Computing c2 = (Computing) modules[1];
		c2.setLoad(c2.getLoad() + vm.getCpuDemand());
		c2.getOccupiedVMs().add(vm);

		Memory m1 = (Memory) modules[2];
		m1.setLoad(m1.getLoad() + vm.getMemDemand());
		m1.getOccupiedVMs().add(vm);

		Memory m2 = (Memory) modules[3];
		m2.setLoad(m2.getLoad() + vm.getMemDemand());
		m2.getOccupiedVMs().add(vm);

		Disk d1 = (Disk) modules[4];
		d1.setLoad(d1.getLoad() + vm.getDiskDemand());
		d1.getOccupiedVMs().add(vm);

		Disk d2 = (Disk) modules[5];
		d2.setLoad(d2.getLoad() + vm.getDiskDemand());
		d2.getOccupiedVMs().add(vm);
		return true;
	}

	/**
	 * 
	 * @param cpus
	 * @param memories
	 * @param disks
	 * @param vm
	 * @return !!!!each module must be sorted in descending order of their
	 *         reliability as the input
	 */
	public Module[] twoCopies_BalanceSelect(ArrayList<Computing> cpus, ArrayList<Memory> memories,
			ArrayList<Disk> disks, VirtualMachine vm) {
		Module[] modules = new Module[6];
		for (int i = 0; i < modules.length; modules[i++] = null)
			;
		int i1 = 0, i2 = 1;
		int j1 = 0, j2 = 1;
		int k1 = 0, k2 = 1;
		while (i1 < cpus.size() && i2 < cpus.size() && j1 < memories.size() && j2 < memories.size() && k1 < disks.size()
				&& k2 < disks.size()) {
			Computing c1 = cpus.get(i1);
			Computing c2 = cpus.get(i2);
			Memory m1 = memories.get(j1);
			Memory m2 = memories.get(j1);
			Disk d1 = disks.get(k1);
			Disk d2 = disks.get(k1);

			// judge whether reliability is legal
			double temp = Equations.reliCalcu_Backup(c1, c2, m1, m2, d1, d2);
			if (temp < vm.getReliabilityReq())
				break;

			modules[0] = c1;
			modules[1] = c2;
			modules[2] = m1;
			modules[3] = m2;
			modules[4] = d1;
			modules[5] = d2;

			// find out who is the most reliable
			double maxReli = 0;
			int maxInd = 0;
			for (int i = 0; i < 6; i++) {
				double reliTemp = modules[i].getReliability();
				if (maxReli < reliTemp) {
					maxReli = reliTemp;
					maxInd = i;
				}
			}

			// increment corresponding pointer
			switch (maxInd) {
			case 0:
				i1 += (i1 + 1) == i2 ? 2 : 1;
				break;
			case 1:
				i2 += (i2 + 1) == i1 ? 2 : 1;
				break;
			case 2:
				j1 += (j1 + 1) == j2 ? 2 : 1;
				break;
			case 3:
				j2 += (j2 + 1) == j1 ? 2 : 1;
				break;
			case 4:
				k1 += (k1 + 1) == k2 ? 2 : 1;
				break;
			case 5:
				k2 += (k2 + 1) == k1 ? 2 : 1;
			}
		}
		return modules;
	}

	public boolean mapSingleCopy(DDC ddc, VirtualMachine vm, boolean greedy) {
		ArrayList<Computing> cpus = new ArrayList<>();
		ArrayList<Memory> memories = new ArrayList<>();
		ArrayList<Disk> disks = new ArrayList<>();

		// exclude modules that remains insufficient resources
		for (Computing c : ddc.getCPUs().values()) {
			boolean filter1 = c.getLoad() + vm.getCpuDemand() > c.getCapacity();
			boolean filter2 = c.getReliability() < vm.getReliabilityReq();
			if (filter1)
				continue;
			if (greedy && filter2)
				continue;// in greedy alg, exclude also low reliability module
			cpus.add(c);
		}
		if (cpus.isEmpty())
			return false;// every module has reliability lower than theta
		for (Memory m : ddc.getMemorys().values()) {
			boolean filter1 = m.getLoad() + vm.getCpuDemand() > m.getCapacity();
			boolean filter2 = m.getReliability() < vm.getReliabilityReq();
			if (filter1)
				continue;
			if (greedy && filter2)
				continue;
			memories.add(m);
		}
		if (memories.isEmpty())
			return false;
		for (Disk d : ddc.getDisks().values()) {
			boolean filter1 = d.getLoad() + vm.getCpuDemand() > d.getCapacity();
			boolean filter2 = d.getReliability() < vm.getReliabilityReq();
			if (filter1)
				continue;
			if (greedy && filter2)
				continue;
			disks.add(d);
		}
		if (disks.isEmpty())
			return false;

		// try to find the most suitable modules by calling the corresponding algorithm
		Module[] obj = greedy ? this.singleCopy_GreedySelect(cpus, memories, disks, vm)
				: this.singleCopy_BalanceSelect(cpus, memories, disks, vm);

		if (obj[0] == null)
			return false;// fail to find a module

		// update resources
		Computing c = (Computing) obj[0];
		c.setLoad(c.getLoad() + vm.getCpuDemand());
		c.getOccupiedVMs().add(vm);
		Memory m = (Memory) obj[1];
		m.setLoad(m.getLoad() + vm.getMemDemand());
		m.getOccupiedVMs().add(vm);
		Disk d = (Disk) obj[2];
		d.setLoad(d.getLoad() + vm.getDiskDemand());
		d.getOccupiedVMs().add(vm);

		return true;
	}

	/**
	 * 
	 * @param cpus
	 * @param memories
	 * @param disks
	 * @param vm
	 * @return find all possible module combinations and choose the one with least
	 *         reliability with no violating threshold
	 */
	public Module[] singleCopy_GreedySelect(ArrayList<Computing> cpus, ArrayList<Memory> memories,
			ArrayList<Disk> disks, VirtualMachine vm) {
		Module[] modules = new Module[3];// three objects: chosen computing, memory, disk
		for (int i = 0; i < modules.length; modules[i++] = null)
			;

		double minRel = 2.0;

		for (int i = 0; i < cpus.size(); i++) {
			Computing c = cpus.get(i);
			for (int j = 0; j < memories.size(); j++) {
				Memory m = memories.get(j);
				for (int k = 0; k < disks.size(); k++) {
					Disk d = disks.get(k);

					double reli = Equations.reliCalcu_Single(c, m, d);
					if (reli > vm.getReliabilityReq() && minRel < reli) {
						reli = minRel;
						modules[0] = c;
						modules[1] = m;
						modules[2] = d;
					}
				}
			}
		}

		return modules;
	}

	/**
	 * 
	 * @param cpus
	 * @param memories
	 * @param disks
	 * @param vm
	 * @return try to balance reliability, avoiding bottleneck of modules
	 */

	public Module[] singleCopy_BalanceSelect(ArrayList<Computing> cpus, ArrayList<Memory> memories,
			ArrayList<Disk> disks, VirtualMachine vm) {
		Module[] modules = new Module[3];// three objects: chosen computing, memory, disk
		modules[0] = null;
		modules[1] = null;
		modules[2] = null;
		// sort each module list in descending order of their modules
		Collections.sort(cpus, new Comparator<Computing>() {
			@Override
			public int compare(Computing o1, Computing o2) {
				// TODO Auto-generated method stub
				double dif = o2.getReliability() - o1.getReliability();
				return dif > 0 ? 1 : (dif == 0 ? 0 : -1);
			}
		});
		Collections.sort(memories, new Comparator<Memory>() {
			@Override
			public int compare(Memory o1, Memory o2) {
				// TODO Auto-generated method stub
				double dif = o2.getReliability() - o1.getReliability();
				return dif > 0 ? 1 : (dif == 0 ? 0 : -1);
			}
		});
		Collections.sort(disks, new Comparator<Disk>() {
			@Override
			public int compare(Disk o1, Disk o2) {
				// TODO Auto-generated method stub
				double dif = o2.getReliability() - o1.getReliability();
				return dif > 0 ? 1 : (dif == 0 ? 0 : -1);
			}
		});

		int i = 0, j = 0, k = 0;
		while (i < cpus.size() && j < memories.size() && k < disks.size()) {
			Computing c = cpus.get(i);
			Memory m = memories.get(j);
			Disk d = disks.get(k);

			// judge whether reliability is legal
			double temp = Equations.reliCalcu_Single(c, m, d);
			if (temp < vm.getReliabilityReq())
				break;
			modules[0] = c;
			modules[1] = m;
			modules[2] = d;

			// move the module that with highest reliability
			if (c.getReliability() >= m.getReliability() && c.getReliability() >= d.getReliability())
				i++;
			else if (m.getReliability() >= d.getReliability())
				j++;
			else
				k++;
		}

		return modules;
	}
}
