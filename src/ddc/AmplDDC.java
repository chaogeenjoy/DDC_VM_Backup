package ddc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import general.Parameter;
import request.VMGenerator;
import request.VirtualMachine;

public class AmplDDC {
	public static void main(String[] args) throws IOException {
		DDC ddc = new DDC();
		ddc.createDDC();

		VMGenerator g = new VMGenerator();

		int vmNum = 10;

		ArrayList<VirtualMachine> vms = g.generatingVMs(vmNum);
		String folderName = "D:\\softspace\\ampl\\";
		String varName = "" + vmNum;
		generateDataFile(ddc, folderName, varName, vms);
		System.out.println("Finish");
	}

	public static void generateDataFile(DDC ddc, String folderName, String varName, ArrayList<VirtualMachine> vms)
			throws IOException {
		File f = new File(folderName, "ddc" + varName + ".dat");
		BufferedWriter file = new BufferedWriter(new FileWriter(f));

		/*
		 * // Resource types file.write("set R := Computing, Memory, Storage;\r\n");
		 * file.flush();
		 */

		// Module
		file.write("set C :=");
		for (Computing c : ddc.getCPUs().values())
			file.write(c.getName() + " ");
		file.write(" ;\r\n");

		file.write("set M :=");
		for (Memory m : ddc.getMemorys().values())
			file.write(m.getName() + " ");
		file.write(" ;\r\n");

		file.write("set S :=");
		for (Storage s : ddc.getStorages().values())
			file.write(s.getName() + " ");
		file.write(" ;\r\n");
		file.flush();

		// VMs
		file.write("set V :=");
		for (VirtualMachine v : vms) {
			file.write(v.getName() + " ");
		}
		file.write(" ;\r\n");
		file.flush();

		// available capacity
		file.write("param AC :=\r\n");
		for (Computing m : ddc.getCPUs().values())
			file.write(m.getName() + " " + m.getCapacity() + "\r\n");
		file.write(";\r\n");

		file.write("param AM :=\r\n");
		for (Memory m : ddc.getMemorys().values())
			file.write(m.getName() + " " + m.getCapacity() + "\r\n");
		file.write(";\r\n");

		file.write("param AS :=\r\n");
		for (Storage m : ddc.getStorages().values())
			file.write(m.getName() + " " + m.getCapacity() + "\r\n");
		file.write(";\r\n");
		file.flush();

		// Reliability
		file.write("param R :=\r\n");
		for (Computing m : ddc.getCPUs().values())
			file.write(m.getName() + " " + m.getReliability() + "\r\n");
		for (Memory m : ddc.getMemorys().values())
			file.write(m.getName() + " " + m.getReliability() + "\r\n");
		for (Storage m : ddc.getStorages().values())
			file.write(m.getName() + " " + m.getReliability() + "\r\n");
		file.write(";\r\n");
		file.flush();

		// resource demand
		file.write("param DC :=\r\n");
		for (VirtualMachine vm : vms)
			file.write(vm.getName()+" " + vm.getCpuDemand() + "\r\n");
		file.write(";\r\n");
		file.write("param DM :=\r\n");
		for (VirtualMachine vm : vms)
			file.write(vm.getName() + " " +vm.getMemDemand() + "\r\n");
		file.write(";\r\n");
		file.write("param DS :=\r\n");
		for (VirtualMachine vm : vms)
			file.write(vm.getName() + " " +vm.getStoDemand() + "\r\n");
		file.write(";\r\n");
		file.flush();

		// reliability threshold
		file.write("param Theta :=\r\n");
		for (VirtualMachine vm : vms) {
			file.write(vm.getName() + " " + vm.getReliabilityReq() + "\r\n");
		}
		file.write(";\r\n");
		file.flush();

		file.close();
		File fa = new File(folderName, "ddc" + varName + ".ampl");
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(fa));
		bw1.write("reset;\r\nmodel ddc.mod;\r\ndata ddc" + varName + ".dat;\r\n");
		bw1.write("option auxfiles c;\r\noption solver gurobi;\r\n");
		bw1.write("option gurobi_options 'mipgap = " + Parameter.GROUBI_MIPGAP
				+ " outlev = 1 nodefilestart = 0.1';\r\nsolve;\r\n");
		bw1.close();

	}
}
