package ampl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ddc.Computing;
import ddc.DDC;
import ddc.Memory;
import ddc.Storage;
import general.Parameter;
import request.VMGenerator;
import request.VirtualMachine;
import tdc.Server;
import tdc.TDC;

public class AmplWriter {
	public static void main(String[] args) throws IOException {
		DDC ddc = new DDC();
		ddc.createDDC();

		TDC tdc = new TDC();
		tdc.convertingDDCToTDC(ddc);

		int vmNum = 60;
		VMGenerator g = new VMGenerator();
		ArrayList<VirtualMachine> vms = g.generatingVMs(vmNum);

		String folderName = "D:\\softspace\\ampl\\";
		String varName = "" + vmNum;
		generateDataFile_DDC(ddc, folderName, varName, vms);
		generateDataFile_TDC(tdc, ddc, folderName, varName, vms);
		System.out.println("Finish");
	}

	public static void generateDataFile_TDC(TDC tdc, DDC ddc, String folderName, String varName,
			ArrayList<VirtualMachine> vms) throws IOException {
		File f = new File(folderName, "tdc" + varName + ".dat");
		BufferedWriter file = new BufferedWriter(new FileWriter(f));

		// Resource types
		file.write("set R := Computing, Memory, Storage;\r\n");
		file.flush();

		// Module
		file.write("set M[Computing] :=");
		for (Computing c : ddc.getCPUs().values())
			file.write(c.getName() + " ");
		file.write(" ;\r\n");

		file.write("set M[Memory] :=");
		for (Memory m : ddc.getMemorys().values())
			file.write(m.getName() + " ");
		file.write(" ;\r\n");

		file.write("set M[Storage] :=");
		for (Storage s : ddc.getStorages().values())
			file.write(s.getName() + " ");
		file.write(" ;\r\n");
		file.flush();

		// set of servers
		file.write("set S :=");
		for (Server s : tdc.getServers().values())
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

		// Beta
		file.write("param Beta :=\r\n");
		for (Server s : tdc.getServers().values()) {
			file.write(s.getName() + ",Computing," + s.getCpu().getName() + " 1\r\n");
			file.write(s.getName() + ",Memory," + s.getMemory().getName() + " 1\r\n");
			file.write(s.getName() + ",Storage," + s.getStorage().getName() + " 1\r\n");
		}
		file.write(" ;\r\n");
		file.flush();

		// available capacity
		file.write("param C :=\r\n");
		for (Computing m : ddc.getCPUs().values())
			file.write("Computing," + m.getName() + " " + m.getCapacity() + "\r\n");
		for (Memory m : ddc.getMemorys().values())
			file.write("Memory," + m.getName() + " " + m.getCapacity() + "\r\n");
		for (Storage m : ddc.getStorages().values())
			file.write("Storage," + m.getName() + " " + m.getCapacity() + "\r\n");
		file.write(";\r\n");
		file.flush();

		// Reliability
		file.write("param Reli :=\r\n");
		for (Computing m : ddc.getCPUs().values())
			file.write("Computing," + m.getName() + " " + m.getReliability() + "\r\n");
		for (Memory m : ddc.getMemorys().values())
			file.write("Memory," + m.getName() + " " + m.getReliability() + "\r\n");
		for (Storage m : ddc.getStorages().values())
			file.write("Storage," + m.getName() + " " + m.getReliability() + "\r\n");
		file.write(";\r\n");
		file.flush();

		// resource demand
		file.write("param D :=\r\n");
		for (VirtualMachine vm : vms) {
			file.write(vm.getName() + ",Computing " + vm.getCpuDemand() + "\r\n");
			file.write(vm.getName() + ",Memory " + vm.getMemDemand() + "\r\n");
			file.write(vm.getName() + ",Storage " + vm.getStoDemand() + "\r\n");
		}
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
		File fa = new File(folderName, "tdc" + varName + ".ampl");
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(fa));
		bw1.write("reset;\r\nmodel tdc.mod;\r\ndata tdc" + varName + ".dat;\r\n");
		bw1.write("option auxfiles c;\r\noption solver gurobi;\r\n");
		bw1.write("option gurobi_options 'mipgap = " + Parameter.GROUBI_MIPGAP
				+ " outlev = 1 nodefilestart = 0.1';\r\nsolve;\r\n");
		bw1.write("display sum{v in V} chi[v];\r\n");
		bw1.write(
				"display {v in V} sum{s in S, t in S} phi[v,s,t] * (1-(1-prod{r in R} (sum{m in M[r]} Beta[s,r,m]*Reli[r,m]))*(1-prod{r in R} (sum{m in M[r]} Beta[t,r,m]*Reli[r,m])))"
						+ "+ sum{s in S} vphi[v,s] * prod{r in R}(sum{m in M[r]} Beta[s,r,m]*Reli[r,m]) >> Reli_Tdc"+varName+".txt;\r\n");
		bw1.close();
	}

	public static void generateDataFile_DDC(DDC ddc, String folderName, String varName, ArrayList<VirtualMachine> vms)
			throws IOException {
		File f = new File(folderName, "ddc" + varName + ".dat");
		BufferedWriter file = new BufferedWriter(new FileWriter(f));

		// Resource types
		file.write("set R := Computing, Memory, Storage;\r\n");
		file.flush();

		// Module
		file.write("set M[Computing] :=");
		for (Computing c : ddc.getCPUs().values())
			file.write(c.getName() + " ");
		file.write(" ;\r\n");

		file.write("set M[Memory] :=");
		for (Memory m : ddc.getMemorys().values())
			file.write(m.getName() + " ");
		file.write(" ;\r\n");

		file.write("set M[Storage] :=");
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
		file.write("param C :=\r\n");
		for (Computing m : ddc.getCPUs().values())
			file.write("Computing," + m.getName() + " " + m.getCapacity() + "\r\n");
		for (Memory m : ddc.getMemorys().values())
			file.write("Memory," + m.getName() + " " + m.getCapacity() + "\r\n");
		for (Storage m : ddc.getStorages().values())
			file.write("Storage," + m.getName() + " " + m.getCapacity() + "\r\n");
		file.write(";\r\n");
		file.flush();

		// Reliability
		file.write("param Reli :=\r\n");
		for (Computing m : ddc.getCPUs().values())
			file.write("Computing," + m.getName() + " " + m.getReliability() + "\r\n");
		for (Memory m : ddc.getMemorys().values())
			file.write("Memory," + m.getName() + " " + m.getReliability() + "\r\n");
		for (Storage m : ddc.getStorages().values())
			file.write("Storage," + m.getName() + " " + m.getReliability() + "\r\n");
		file.write(";\r\n");
		file.flush();

		// resource demand
		file.write("param D :=\r\n");
		for (VirtualMachine vm : vms) {
			file.write(vm.getName() + ",Computing " + vm.getCpuDemand() + "\r\n");
			file.write(vm.getName() + ",Memory " + vm.getMemDemand() + "\r\n");
			file.write(vm.getName() + ",Storage " + vm.getStoDemand() + "\r\n");
		}
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
		bw1.write("display sum{v in V} chi[v];\r\n");
		bw1.write("display {v in V} "
				+ "chi[v] * prod{r in R} (1- sum{m in M[r],n in M[r]} delta[v,r,m]*gamma[v,r,n]*(1-Reli[r,m])*(1-Reli[r,n])) "
				+ "+prod{r in R} (sum{m in M[r]} delta[v,r,m]*(1-chi[v])*Reli[r,m])>>Reli_ddc"+varName+".txt;\r\n");
		bw1.write(
				"display {v in V} exp(sum{r in R, m in M[r], n in M[r]} mu[v,r,m,n] * log(1-(1-Reli[r,m])*(1-Reli[r,n]))"
						+ "+ sum{r in R, m in M[r]} xi[v,r,m] * log(Reli[r,m]));\r\n");
		bw1.close();

	}
}
