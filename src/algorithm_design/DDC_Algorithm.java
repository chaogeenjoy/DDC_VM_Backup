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
import request.Request;

public class DDC_Algorithm {
	private int accept = 0;
	private int backup = 0;

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

	public int mapVMBatches(DDC ddc, ArrayList<Request> vms, boolean noFateSharing) {		

		for (int i = 0; i < vms.size(); i++) {
			Request vm = vms.get(i);
			// try to map it with single copy
			int res = this.vmMapping(ddc, vm, noFateSharing);
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
	public int vmMapping(DDC ddc, Request vm, boolean noFateSharing) {
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

		{// sort each module list in descending order of reliability
			Collections.sort(cpus, new Comparator<Computing>() {
				@Override
				public int compare(Computing o1, Computing o2) {				
					double dif = o2.getReliability() - o1.getReliability();
					return dif > 0 ? 1 : (dif == 0 ? 0 : -1);
				}
			});
			Collections.sort(memories, new Comparator<Memory>() {
				@Override
				public int compare(Memory o1, Memory o2) {
					double dif = o2.getReliability() - o1.getReliability();
					return dif > 0 ? 1 : (dif == 0 ? 0 : -1);
				}
			});
			Collections.sort(disks, new Comparator<Disk>() {
				@Override
				public int compare(Disk o1, Disk o2) {
					double dif = o2.getReliability() - o1.getReliability();
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
			Request vm) {
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
			Request vm, boolean NoFateSharing) {

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
	
	public int mapVMBatches_FirstFit(DDC ddc, ArrayList<Request> requests, boolean noFateSharing) {		

		for (int i = 0; i < requests.size(); i++) {			
			Request request = requests.get(i);
			if((i+1)%300==1)
				System.out.println((i+1)+"-th requests \t"+request.getReliabilityReq() +" \t"+requests.size());
			// try to map it with single copy
			int res = this.vmMapping_FirstFit(ddc, request, noFateSharing);
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
	public int vmMapping_FirstFit(DDC ddc, Request vm, boolean noFateSharing) {
		ArrayList<Computing> cpus = new ArrayList<>();
		ArrayList<Memory> memories = new ArrayList<>();
		ArrayList<Disk> disks = new ArrayList<>();
		cpus.addAll(ddc.getCPUs().values());
		memories.addAll(ddc.getMemorys().values());
		disks.addAll(ddc.getDisks().values());

		if (singleCopyTry_FirstFit(cpus, memories, disks, vm))
			return 1;
		if (twoCopiesTry_FirstFit(cpus, memories, disks, vm, noFateSharing))
			return 2;
		return -1;
	}
	
	/**
	 * scan each module, find the first feasible combination
	 * @param cpus
	 * @param memories
	 * @param disks
	 * @param vm
	 * @return
	 */
	public boolean singleCopyTry_FirstFit(ArrayList<Computing> cpus, ArrayList<Memory> memories, ArrayList<Disk> disks,
			Request vm) {
		Computing targetC = null;
		Memory targetM = null;
		Disk targetD = null;
		here:{// find for last feasible modules
			for(int i=0;i<cpus.size();i++) {
				Computing c = cpus.get(i);
				if (c.getLoad() + vm.getCpuDemand() > c.getCapacity())
					continue;
				for(int j=0;j<memories.size();j++) {
					Memory m = memories.get(j);
					if (m.getLoad() + vm.getMemDemand() > m.getCapacity())
						continue;
					for(int k=0;k<disks.size();k++) {
						Disk d = disks.get(k);
						if (d.getLoad() + vm.getDiskDemand() > d.getCapacity())
							continue;
						// judge whether reliability is feasible
						double temp = Equations.reliCalcu_Single(c, m, d);
						if (temp >= vm.getReliabilityReq()) {
							targetC = c;
							targetM = m;
							targetD = d;
							break here;
						}
						
					}
				}
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
	
	/**
	 * Scan directly, like window scanning, with size of 2, 
	 * meaning in pairs of each type of module, find the first fit combination
	 * @param cpus
	 * @param memories
	 * @param disks
	 * @param vm
	 * @param NoFateSharing
	 * @return
	 */
	public boolean twoCopiesTry_FirstFit(ArrayList<Computing> cpus, ArrayList<Memory> memories, ArrayList<Disk> disks,
			Request vm, boolean NoFateSharing) {

		if (cpus.size() < 2 || memories.size() < 2 || disks.size() < 2)
			return false;

		Module[] tm = new Module[6];// store the target modules
		for (int i = 0; i < tm.length; tm[i++] = null)
			;

		here:{// search for target modules
			for(int i=0;i<cpus.size()-1;i++) {//there are in total #CPU -1 pairs 
				Computing c1 = cpus.get(i);
				Computing c2 = cpus.get(i+1);
				if (c1.getLoad() + vm.getDiskDemand() > c1.getCapacity() 
						||c2.getLoad() + vm.getDiskDemand() > c2.getCapacity() )
					continue;
				for(int j=0;j<memories.size()-1;j++) {
					Memory m1 = memories.get(j);
					Memory m2 = memories.get(j+1);
					if (m1.getLoad() + vm.getDiskDemand() > m1.getCapacity() 
							||m2.getLoad() + vm.getDiskDemand() > m2.getCapacity() )
						continue;
					for(int k=0;k<disks.size()-1;k++) {
						Disk d1 = disks.get(k);
						Disk d2 = disks.get(k+1);
						if (d1.getLoad() + vm.getDiskDemand() > d1.getCapacity() 
								||d2.getLoad() + vm.getDiskDemand() > d2.getCapacity() )
							continue;
						
						// judge whether reliability is legal
						double temp = NoFateSharing ? Equations.reliCalcu_Backup_NoFateSharing_DDC(c1, c2, m1, m2, d1, d2)
								: Equations.reliCalcu_Backup_CompleteFateSharing_DDC(c1, c2, m1, m2, d1, d2);
						if (temp >= vm.getReliabilityReq()) {
							tm[0] = c1;
							tm[1] = c2;
							tm[2] = m1;
							tm[3] = m2;
							tm[4] = d1;
							tm[5] = d2;
							break here;
						}
					}
					
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
