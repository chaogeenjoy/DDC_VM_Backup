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
	private double alpha = 0.1;
	private int accept = 0;
	private int backup = 0;

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/**
	 * @return - number of accepted VMs
	 */
	public int getAccept() {
		return accept;
	}

	public void setAccept(int accept) {
		this.accept = accept;
	}

	/**
	 * @return - number of accepted VMs that needs a backup
	 */
	public int getBackup() {
		return backup;
	}

	public void setBackup(int backup) {
		this.backup = backup;
	}

	public int mapVMBatches(DDC ddc, ArrayList<VirtualMachine> vms, boolean noFateSharing) {
		// sort VMs in descending order of their reliability requests

		/*
		 * Collections.sort(vms, new Comparator<VirtualMachine>() {
		 * 
		 * @Override public int compare(VirtualMachine o1, VirtualMachine o2) { double
		 * diff = o2.getReliabilityReq() - o1.getReliabilityReq(); return diff > 0 ? 1 :
		 * (diff < 0 ? -1 : 0); } });
		 */

//		Collections.shuffle(vms);

		for (int i = 0; i < vms.size(); i++) {
			VirtualMachine vm = vms.get(i);
			// try to map it with single copy
			int res = this.vmMapping(ddc, vm, this.getAlpha(),noFateSharing);
			if (res > 0)
				this.setAccept(this.getAccept() + 1);
			if (res == 2)
				this.setBackup(this.getBackup() + 1);

		}
		return this.getAccept();
	}

	/**
	 * @param ddc
	 * @param vm
	 * @return -1: failure; 1 succeed with one copy; 2 succeed with two copies
	 */
	public int vmMapping(DDC ddc, VirtualMachine vm, double ALPHA,boolean noFateSharing) {
		ArrayList<Computing> cpus = new ArrayList<>();
		ArrayList<Memory> memories = new ArrayList<>();
		ArrayList<Disk> disks = new ArrayList<>();

		{// exclude modules that remains insufficient resources
			for (Computing c : ddc.getCPUs().values()) {
				if (c.getLoad() + vm.getCpuDemand() > c.getCapacity())
					continue;
				cpus.add(c);
			}
			if (cpus.isEmpty())
				return -1;
			for (Memory m : ddc.getMemorys().values()) {
				if (m.getLoad() + vm.getMemDemand() > m.getCapacity())
					continue;
				memories.add(m);
			}
			if (memories.isEmpty())
				return -1;
			for (Disk d : ddc.getDisks().values()) {
				if (d.getLoad() + vm.getDiskDemand() > d.getCapacity())
					continue;
				disks.add(d);
			}
			if (disks.isEmpty())
				return -1;
		}

		{// sort each module list in ascending order of R_mr + alpha * F_mr
			// equivalently, descending order of loads
			Collections.sort(cpus, new Comparator<Computing>() {
				@Override
				public int compare(Computing o1, Computing o2) {
					double w1 = Equations.sortingWeight(o1, vm, ALPHA);
					double w2 = Equations.sortingWeight(o2, vm, ALPHA);
					double dif = w2 - w1;
					return dif > 0 ? 1 : (dif == 0 ? 0 : -1);
				}
			});
			Collections.sort(memories, new Comparator<Memory>() {
				@Override
				public int compare(Memory o1, Memory o2) {
					double w1 = Equations.sortingWeight(o1, vm, ALPHA);
					double w2 = Equations.sortingWeight(o2, vm, ALPHA);
					double dif = w2 - w1;
					return dif > 0 ? 1 : (dif == 0 ? 0 : -1);
				}
			});
			Collections.sort(disks, new Comparator<Disk>() {
				@Override
				public int compare(Disk o1, Disk o2) {
					double w1 = Equations.sortingWeight(o1, vm, ALPHA);
					double w2 = Equations.sortingWeight(o2, vm, ALPHA);
					double dif = w2 - w1;
					return dif > 0 ? 1 : (dif == 0 ? 0 : -1);
				}
			});
		}

		if (singleCopyTry(cpus, memories, disks, vm))
			return 1;
		if (twoCopiesTry(cpus, memories, disks, vm, noFateSharing))
			return 2;
		return -1;
	}

	public boolean singleCopyTry(ArrayList<Computing> cpus, ArrayList<Memory> memories, ArrayList<Disk> disks,
			VirtualMachine vm) {
		Computing targetC = null;
		Memory targetM = null;
		Disk targetD = null;
		{// find for last feasible modules
			int i = 0, j = 0, k = 0;
			while (i < cpus.size() && j < memories.size() && k < disks.size()) {
				Computing c = cpus.get(i);
				Memory m = memories.get(j);
				Disk d = disks.get(k);

				// judge whether reliability is legal
				double temp = Equations.reliCalcu_Single(c, m, d);
				if (temp < vm.getReliabilityReq())
					break;
				targetC = c;
				targetM = m;
				targetD = d;

				// move the module that with highest reliability
				if (c.getReliability() >= m.getReliability() && c.getReliability() >= d.getReliability())
					i++;
				else if (m.getReliability() >= d.getReliability())
					j++;
				else
					k++;
			}
		}

		if (targetC == null)
			return false;// fail to find a module

		// update resources
		targetC.setLoad(targetC.getLoad() + vm.getCpuDemand());
		targetC.getOccupiedVMs().add(vm);
		targetM.setLoad(targetM.getLoad() + vm.getMemDemand());
		targetM.getOccupiedVMs().add(vm);
		targetD.setLoad(targetD.getLoad() + vm.getDiskDemand());
		targetD.getOccupiedVMs().add(vm);

		return true;
	}

	public boolean twoCopiesTry(ArrayList<Computing> cpus, ArrayList<Memory> memories, ArrayList<Disk> disks,
			VirtualMachine vm, boolean NoFateSharing) {

		if (cpus.size() < 2 || memories.size() < 2 || disks.size() < 2)
			return false;

		Module[] tm = new Module[6];// store the target modules
		for (int i = 0; i < tm.length; tm[i++] = null)
			;

		{// search for target modules
			int i1 = 0, i2 = 1;
			int j1 = 0, j2 = 1;
			int k1 = 0, k2 = 1;
			while (i1 < cpus.size() && i2 < cpus.size() && j1 < memories.size() && j2 < memories.size()
					&& k1 < disks.size() && k2 < disks.size()) {
				Computing c1 = cpus.get(i1);
				Computing c2 = cpus.get(i2);
				Memory m1 = memories.get(j1);
				Memory m2 = memories.get(j2);
				Disk d1 = disks.get(k1);
				Disk d2 = disks.get(k2);

				// judge whether reliability is legal
				double temp = NoFateSharing ? Equations.reliCalcu_Backup_NoFateSharing_DDC(c1, c2, m1, m2, d1, d2)
						: Equations.reliCalcu_Backup_CompleteFateSharing_DDC(c1, c2, m1, m2, d1, d2);
				if (temp < vm.getReliabilityReq())
					break;

				tm[0] = c1;
				tm[1] = c2;
				tm[2] = m1;
				tm[3] = m2;
				tm[4] = d1;
				tm[5] = d2;

				// find out who is the most reliable
				double maxReli = 0;
				int maxInd = 0;
				for (int i = 0; i < 6; i++) {
					double reliTemp = tm[i].getReliability();
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
		}

		if (tm[0] == null)
			return false;// fail to find modules

		for (Module m : tm) {// update resource
			int demand = (m instanceof Computing) ? vm.getCpuDemand()
					: ((m instanceof Memory) ? vm.getMemDemand() : vm.getDiskDemand());
			m.setLoad(m.getLoad() + demand);
			m.getOccupiedVMs().add(vm);
		}
		return true;
	}
}
