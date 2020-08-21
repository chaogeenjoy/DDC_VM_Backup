package general;

import java.util.Random;

public class Parameter {
	public static final double GROUBI_MIPGAP = 0.0001;
	public static final int DELTA = 10000000;// a large value for ampl

	public static final int SERVER_NUM = 10;
	public static final String[] RESOURCE_TYPE = { "Computing", "Memory", "Storage" };
	public static final int CPU_PER_SERVER = 64;// CPU cores
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
	 * @param r
	 * @return 0.9 ~ 0.9999
	 */
	public static double vMReliability(Random r) {
		return r.nextDouble() * 0.0999 + 0.9;
	}

	public static int cpuDemand(Random cpu) {// the benchmark
		return cpu.nextInt(16) + 1;// 1 ~ 16 cores
	}

	public static int memoryDemand(Random memory) {
		return memory.nextInt(32) + 1;// 0~100
	}

	public static int stoDemand(Random sto) {
		return sto.nextInt(512) + 1;// 0~1000
	}

}
