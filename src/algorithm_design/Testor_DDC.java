package algorithm_design;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import ddc.DDC;
import request.VMGenerator;
import request.VirtualMachine;

public class Testor_DDC {
	public static final int SHUFFLE = Integer.MAX_VALUE-1;

	public static void main(String[] args) {

		int[] nums = { 45 };
		int[][] res = new int[nums.length][];
		for (int i = 0; i < nums.length; i++) {
			int vmNum = nums[i];
			double lower = 0.95;
			double upper = 0.99;
			System.out.println("VM Number = " + vmNum + "\t=======");

//		double[] ths = {0.85 };
//		int vmNum = 30;
//		for (int i = 0; i < nums.length; i++) {
//			double th = nums[i];
//			double lower = th;
//			double upper = th;

			res[i] = shuffleTrial(vmNum, lower, upper, SHUFFLE);
		}
		System.out.println("\r\nFinal Results for"+Arrays.toString(nums));
		System.out.println("Accept\tBackup");
		for (int i = 0; i < nums.length; i++) {
			System.out.println(res[i][0]+"\t"+res[i][1]);
		}
	}

	public static int[] shuffleTrial(int vmNum, double lower, double upper, int shuffle) {
		int res[] = { 0, 0 };
		double obj = 0.0;
		long begin = System.currentTimeMillis();
		for (int i = 1; i < shuffle + 1; i++) {
			DDC ddc = new DDC();
			ddc.createDDC();

			VMGenerator g = new VMGenerator();
			ArrayList<VirtualMachine> vms = g.generatingVMs(vmNum, lower, upper);
			DDC_Algorithm_Reliability_First da = new DDC_Algorithm_Reliability_First();
			da.setAlpha(0.000);
			Collections.shuffle(vms);

			da.mapVMBatches(ddc, vms);
			double temp = da.getAccept() - 0.001 * da.getBackup();
			if (temp > obj) {
				res[0] = da.getAccept();
				res[1] = da.getBackup();
				obj = temp;
			}
			if (shuffle > 10 && i % (shuffle / 1000) == 0)
				System.out.println(
						"\t" + i + ":\t" + obj + "\t" + ((double) (System.currentTimeMillis() - begin) / 1000.0) + "s");
		}

		System.out.println("Running time:\t" + ((double) (System.currentTimeMillis() - begin) / 1000.0) + "s");
		System.out.println("total acceptance\t\t" + res[0] + "\r\ntotal accepted with backups\t" + res[1]);
		System.out.println("_________________________________");

		return res;
	}
}
