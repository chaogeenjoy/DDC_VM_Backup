package algorithm_design;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import ddc.DDC;
import request.VMGenerator;
import request.VirtualMachine;

public class Testor_DDC {
	public static final int SHUFFLE = 10000;
	public static final boolean nonFateSharing = false;// true - no fate sharing model
	// false - complete fate sharing model

	public static void main(String[] args) throws InterruptedException {
		ArrayList<DDCThread> threads = new ArrayList<DDCThread>();
		
//		int[] variables = { 15,20,25,30,35,40 };
//		int[][] res = new int[variables.length][];
//		for (int i = 0; i < variables.length; i++) {
//			int vmNum = variables[i];
//			double lower = 0.98;
//			double upper = 0.999;
//			System.out.println("VM Number = " + vmNum + "\t=======");

		double[] variables = { 0.8, 0.85, 0.9, 0.95, 0.99, 0.995, 0.999 };		
		int vmNum = 3000;
		for (int i = 0; i < variables.length; i++) {
			double th = variables[i];
			double lower = th;
			double upper = th;
			System.out.println("var = " + th + "\t=======");

			DDCThread thread = new DDCThread(SHUFFLE, nonFateSharing, vmNum, lower, upper, "ReliaReq"+th);
			threads.add(thread);
			thread.start();			
		}
		
	
		for(Thread thread : threads) {
			thread.join();//保证所有子线程执行完毕以后再执行后面的语句
		}
		System.out.println("\n\n\nFinal Results for #of Accept and AccwithBackups");
		for(DDCThread thread:threads) {	
			System.out.println(thread.getName()+"\t "+ thread.getAccetance() +"\t "+ thread.getAccptWithBackups());
		}
	}
}
