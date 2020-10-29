package general;

import ddc.Computing;
import ddc.Disk;
import ddc.Memory;
import ddc.Module;
import request.VirtualMachine;
import tdc.Server;

public class Equations {
	public static double reliCalcu_Single(Computing c, Memory m, Disk d) {
		return c.getReliability() * m.getReliability() * d.getReliability();
	}

	public static double reliCalcu_Backup(Computing c1, Computing c2, Memory m1, Memory m2, Disk d1, Disk d2) {
		double reli_C = 1 - (1 - c1.getReliability()) * (1 - c2.getReliability());
		double reli_M = 1 - (1 - m1.getReliability()) * (1 - m2.getReliability());
		double reli_D = 1 - (1 - d1.getReliability()) * (1 - d2.getReliability());

		return reli_C * reli_M * reli_D;
	}

	public static double reli_twoServers(Server s1, Server s2) {
		return 1 - (1 - s1.getReliaiblity()) * (1 - s2.getReliaiblity());
	}

	/**
	 * @param m
	 * @param vm
	 * @return (1-alpha)*R_mr + alpha * F_mr
	 */
	public static double sortingWeight(Module m, VirtualMachine vm, double alpha) {
		int demand = (m instanceof Computing) ? vm.getCpuDemand()
				: ((m instanceof Memory) ? vm.getMemDemand() : vm.getDiskDemand());
		double fragLevel = ((double) (m.getCapacity() - m.getLoad() - demand)) / ((double) m.getCapacity());
		return (1 - alpha) * m.getReliability() + alpha * fragLevel;
	}
}
