package general;

import java.util.Random;

public class Parameter {
	public static final double GROUBI_MIPGAP = 0.001; //%0.1
	public static final int DELTA = 10000000;// a large value for ampl

	public static final int SERVER_NUM = 20;
	public static final int CPU_PER_SERVER = 32;// CPU cores
	public static final int MEMORY_PER_SERVER = 128;
	public static final int STORAGE_PER_SERVER = 1024;
	// public static final double[] RELI_MODULE = {0.99999,0.9999,0.9995,0.999};
	// public static final double[] RELI_VM = {0.999999,0.99999,0.9999,0.999};

	/**
	 * @param r
	 * @return 0.9 ~ 0.999
	 */
	public static double moduleReliability(Random r) {
		return r.nextDouble() * 0.099 + 0.9;
	}

	/**
	 * 
	 * @param r
	 * @param direct
	 * @param mu
	 * @param halfSpan
	 * @return
	 * direct == true return 0.99 ~ 0.9999
	 * else return (lower, upper)	
	 */
	public static double vMReliability(Random r,double lower, double upper) {
		return r.nextDouble()*(upper-lower) + lower; //(lower, upper)		
	}

	public static int cpuDemand(Random cpu,boolean cpuIntensive) {// the benchmark
		return //cpuIntensive? cpu.nextInt(17)+8:// 8 ~ 24 cores
				cpu.nextInt(16) + 1;// 1 ~ 8 cores
	}

	public static int memoryDemand(Random memory,boolean memoryIntensive) {
		return //memoryIntensive? memory.nextInt(33) + 16: //16~48
			memory.nextInt(64) + 1;// 1~16
	}

	public static int stoDemand(Random sto,boolean diskIntensive) {
		return //diskIntensive?sto.nextInt(513)+256: //256~768
			sto.nextInt(512) + 1;// 1~256
	}

}
