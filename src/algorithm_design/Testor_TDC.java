package algorithm_design;

import java.util.ArrayList;
import java.util.Random;

public class Testor_TDC {
	public static boolean reliabilityFirst = false;
	public static void main(String[] args) throws InterruptedException {
		ArrayList<TDCThread> threads = new ArrayList<TDCThread>();

		int[] variables = { 1500,2000,2500,3000,3500,4000 };
		for (int i = 0; i < variables.length; i++) {
			int vmNum = variables[i];
			double lower = 0.98;
			double upper = 0.999;
			System.out.println("VM Number = " + vmNum + "\t=======");
			TDCThread thread = new TDCThread(Testor_DDC.SHUFFLE, vmNum, lower, upper, new Random(i), reliabilityFirst, "#of requests = "+vmNum);

			

//		double[] variables = { 0.8 };		
//		int vmNum = 3000;
//		for (int i = 0; i < variables.length; i++) {
//			double th = variables[i];
//			double lower = th;
//			double upper = th;
//			System.out.println("var = " + th + "\t=======");
//			TDCThread thread = new TDCThread(Testor_DDC.SHUFFLE, vmNum, lower, upper, new Random(i), "ReliaReq"+th);
			
			threads.add(thread);
			thread.start();			
		}
		
		
		for(Thread thread : threads) {
			thread.join();//保证所有子线程执行完毕以后再执行后面的语句
		}
		System.out.println("\n\n\nFinal Results for #of Accept and AccwithBackups");
		for(TDCThread thread:threads) 	
			System.out.print(thread.getName()+"\t ");
		System.out.println();
		for(TDCThread thread:threads)
			System.out.print(thread.getAccetance() +"\t ");
		System.out.println();
		for(TDCThread thread:threads)
			System.out.print(thread.getAccptWithBackups() +"\t ");
		System.out.println();
	}

}