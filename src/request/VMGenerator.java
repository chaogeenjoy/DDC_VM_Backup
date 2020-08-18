package request;

import java.util.ArrayList;
import java.util.Random;

import general.Parameter;

public class VMGenerator {

	public ArrayList<VirtualMachine> generatingVMs(int vmNum) {
		Random r = new Random(132);
		ArrayList<VirtualMachine> vms = new ArrayList<VirtualMachine>();
		for (int i = 0; i < vmNum; i++) {
			VirtualMachine vm = new VirtualMachine("VM" + i, i, null, Parameter.cpuDemand(r), Parameter.memoryDemand(r),
					Parameter.stoDemand(r), Parameter.reliRequire(r));
			vms.add(vm);
		}

		return vms;
	}
}
