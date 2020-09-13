package algorithm_design;

import java.util.ArrayList;
import java.util.Collections;

import ddc.DDC;
import request.VMGenerator;
import request.VirtualMachine;

public class Testors {
	public static void main(String[] args) {
		int max = 0;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			if(i%100000==0) System.out.println(i+":\r\n\t"+max+"\t"+((double)(System.currentTimeMillis()-begin)/1000.0)+"s");
			DDC ddc = new DDC();
			ddc.createDDC();

			int vmNum = 70;
			double lower = 0.95;// - 0.01;
			double upper = 0.99;// + 0.01;
			//System.out.println("threhold=\t" );
			VMGenerator g = new VMGenerator();
			ArrayList<VirtualMachine> vms = g.generatingVMs(vmNum, lower, upper);
			DDC_Algorithm_Reliability_First da = new DDC_Algorithm_Reliability_First();
//			DDC_Algorithm_Utilization_First da = new DDC_Algorithm_Utilization_First();

			Collections.shuffle(vms);
			int accept = da.mapVMBatches(ddc, vms);
			if(accept>max)
				max = accept;
//			System.out.println("total accept VMs\t" + da.getAccept() + "\r\ntotal accepted with two copies\t"
//					+ da.getBackup() + "\r\ntotal requests num\t" + vms.size());
		}
		
		System.out.println(max);
	}
}
