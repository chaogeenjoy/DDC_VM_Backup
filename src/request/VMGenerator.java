package request;

import java.util.ArrayList;
import java.util.Random;

import general.Parameter;

public class VMGenerator {

	public ArrayList<VirtualMachine> generatingVMs(int vmNum) {
		Random p = new Random(123);
		Random r = new Random(132);

		ArrayList<VirtualMachine> vms = new ArrayList<VirtualMachine>();
		for (int i = 0; i < vmNum; i++) {
			boolean cpuIntensive = false, memoryIntensive = false, diskIntensive = false;
			double pr = p.nextDouble();
			if (pr < 1.0 / 3.0)
				cpuIntensive = true;
			else if (pr >= 1.0 / 3.0 && pr <= 2.0 / 3.0)
				memoryIntensive = true;
			else
				diskIntensive = true;

			VirtualMachine vm = new VirtualMachine("VM" + i, i, null, Parameter.cpuDemand(r, cpuIntensive),
					Parameter.memoryDemand(r, memoryIntensive), Parameter.stoDemand(r, diskIntensive),
					Parameter.vMReliability(r));
			vms.add(vm);
		}

		return vms;
	}
}
