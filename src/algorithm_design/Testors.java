package algorithm_design;

import java.util.ArrayList;

import ddc.DDC;
import request.VMGenerator;
import request.VirtualMachine;

public class Testors {
	public static void main(String[] args) {
		double[] th = { 0.9, 0.92, 0.94, 0.96, 0.98 };
		for (int i = 0; i < th.length; i++) {

			DDC ddc = new DDC();
			ddc.createDDC();

			int vmNum = 50;
			double lower = th[i] - 0.01;
			double upper = th[i] + 0.01;
			System.out.println("threhold=\t" + th[i]);
			VMGenerator g = new VMGenerator();
			ArrayList<VirtualMachine> vms = g.generatingVMs(vmNum, lower, upper);
//			DDC_Algorithm_Reliability_First da = new DDC_Algorithm_Reliability_First();
			DDC_Algorithm_Utilization_First da = new DDC_Algorithm_Utilization_First();

			int accept = da.mapVMBatches(ddc, vms);
			System.out.println("total accept VMs\t" + da.getAccept() + "\r\ntotal accepted with two copies\t"
					+ da.getBackup() + "\r\ntotal requests num\t" + vms.size());
			System.out.println();
		}
	}
}
