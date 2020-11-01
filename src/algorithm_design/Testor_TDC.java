package algorithm_design;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import ddc.DDC;
import request.VMGenerator;
import request.VirtualMachine;
import tdc.TDC;

public class Testor_TDC {
	public static final int SHUFFLE = 1000000;

	public static void main(String[] args) {

//		int[] nums = { 25 };
//		int[][] res = new int[nums.length][];
//		for (int i = 0; i < nums.length; i++) {
//			int vmNum = nums[i];
//			double lower = 0.95;
//			double upper = 0.99;
//			System.out.println("VM Number = " + vmNum + "\t=======");

		double[] nums = {0.8, 0.85, 0.9, 0.95, 0.96, 0.97, 0.98, 0.985, 0.99, 0.991, 0.992, 0.993, 0.994, 0.995 };
		int[][] res = new int[nums.length][];
		int vmNum = 30;
		for (int i = 0; i < nums.length; i++) {
			double th = nums[i];
			double lower = th;
			double upper = th;
			System.out.println("VM Number = " + th + "\t=======");

			res[i] = shuffleTrial(vmNum, lower, upper, SHUFFLE);
		}
		System.out.println("\r\nFinal Results for" + Arrays.toString(nums));
		System.out.println("Accept\tBackup");
		for (int i = 0; i < nums.length; i++) {
			System.out.println(res[i][0] + "\t" + res[i][1]);
		}
	}

	public static int[] shuffleTrial(int vmNum, double lower, double upper, int shuffle) {
		int res[] = { 0, 0 };
		double obj = 0.0;
		long begin = System.currentTimeMillis();
		for (int i = 1; i < shuffle + 1; i++) {
			DDC ddc = new DDC();
			ddc.createDDC();
			TDC tdc = new TDC();
			tdc.convertingDDCToTDC(ddc);

			VMGenerator g = new VMGenerator();
			ArrayList<VirtualMachine> vms = g.generatingVMs(vmNum, lower, upper);
			TDC_Algorithm da = new TDC_Algorithm();

			Collections.shuffle(vms);

			da.mapVMBatches(tdc, vms);
			double temp = da.getAccept() - 0.001 * da.getBackup();
			if (temp > obj) {
				res[0] = da.getAccept();
				res[1] = da.getBackup();
				obj = temp;
			}
			if (shuffle > 10 && i % (shuffle / 10) == 0)
				System.out.println(
						"\t" + i + ":\t" + obj + "\t" + ((double) (System.currentTimeMillis() - begin) / 1000.0) + "s");
		}

		System.out.println("Running time:\t" + ((double) (System.currentTimeMillis() - begin) / 1000.0) + "s");
		System.out.println("total acceptance\t\t" + res[0] + "\r\ntotal accepted with backups\t" + res[1]);
		System.out.println("_________________________________");

		return res;
	}
}
