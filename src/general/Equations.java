package general;

import ddc.Computing;
import ddc.Disk;
import ddc.Memory;
import tdc.Server;

public class Equations {
	public static double reliCalcu_Single(Computing c, Memory m, Disk d) {
		return c.getReliability() * m.getReliability() * d.getReliability();
	}

	public static double reliCalcu_Backup_NoFateSharing_DDC(Computing c1, Computing c2, Memory m1, Memory m2, Disk d1,
			Disk d2) {
		double reli_C = 1 - (1 - c1.getReliability()) * (1 - c2.getReliability());
		double reli_M = 1 - (1 - m1.getReliability()) * (1 - m2.getReliability());
		double reli_D = 1 - (1 - d1.getReliability()) * (1 - d2.getReliability());

		return reli_C * reli_M * reli_D;
	}

	public static double reliCalcu_Backup_CompleteFateSharing_DDC(Computing c1, Computing c2, Memory m1, Memory m2,
			Disk d1, Disk d2) {
		double reli_1 = reliCalcu_Single(c1, m1, d1);
		double reli_2 = reliCalcu_Single(c2, m2, d2);
		return 1 - (1 - reli_1) * (1 - reli_2);
	}

	public static double reli_twoServers(Server s1, Server s2) {
		return 1 - (1 - s1.getReliaiblity()) * (1 - s2.getReliaiblity());
	}

}
