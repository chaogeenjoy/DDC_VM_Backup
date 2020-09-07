package algorithm_design;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ddc.Computing;
import ddc.DDC;
import ddc.Disk;
import ddc.Memory;
import general.Equations;
import request.VirtualMachine;

public class DDC_Algorithm {

	public boolean mapTwoCopies(DDC ddc, VirtualMachine vm, boolean greedy) {
		ArrayList<Computing> cpus = new ArrayList<>();
		ArrayList<Memory> memories = new ArrayList<>();
		ArrayList<Disk> disks = new ArrayList<>();

		// exclude modules that remains insufficient resources
		for (Computing c : ddc.getCPUs().values()) {
			if (c.getLoad() + vm.getCpuDemand() > c.getCapacity())
				continue;
			cpus.add(c);
		}
		if (cpus.isEmpty())
			return false;// every module has reliability lower than theta
		for (Memory m : ddc.getMemorys().values()) {
			if (m.getLoad() + vm.getMemDemand() > m.getCapacity())
				continue;
			memories.add(m);
		}
		if (memories.isEmpty())
			return false;
		for (Disk d : ddc.getDisks().values()) {
			if (d.getLoad() + vm.getDiskDemand() > d.getCapacity())
				continue;
			disks.add(d);
		}
		if (disks.isEmpty())
			return false;

		return true;
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
		Object[] obj = greedy ? this.greedySelectSingleCopy(cpus, memories, disks, vm)
				: this.bucketSelectSingleCopy(cpus, memories, disks, vm);

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
	public Object[] greedySelectSingleCopy(ArrayList<Computing> cpus, ArrayList<Memory> memories,
			ArrayList<Disk> disks, VirtualMachine vm) {
		Object[] obj = new Object[3];// three objects: chosen computing, memory, disk
		obj[0] = null;
		obj[1] = null;
		obj[2] = null;

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
						obj[0] = c;
						obj[1] = m;
						obj[2] = d;
					}
				}
			}
		}

		return obj;
	}

	public Object[] bucketSelectSingleCopy(ArrayList<Computing> cpus, ArrayList<Memory> memories,
			ArrayList<Disk> disks, VirtualMachine vm) {
		Object[] obj = new Object[3];// three objects: chosen computing, memory, disk
		obj[0] = null;
		obj[1] = null;
		obj[2] = null;
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
			obj[0] = c;
			obj[1] = m;
			obj[2] = d;

			// move the module that with highest reliability
			if (c.getReliability() >= m.getReliability() && c.getReliability() >= d.getReliability())
				i++;
			else if (m.getReliability() >= d.getReliability())
				j++;
			else
				k++;
		}

		return obj;
	}
}
