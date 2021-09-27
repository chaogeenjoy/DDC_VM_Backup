package algorithm_design;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import ddc.DDC;
import request.RequestGenerator;
import request.Request;

public class DDCThread extends Thread {
	private int shuffleTime = 0;
	private boolean nonFateSharing = false;
	private int vmNum = 0;
	private double lowerReq = 0;
	private double upperReq = 0;
	private int accetance = 0;
	private int accptWithBackups = 0;
	private Random rand_shuff = null;
	private boolean relibilityFirst = false;// if true, select reliability first algorithm, otherwise, choose first fit

	public int getShuffleTime() {
		return shuffleTime;
	}

	public void setShuffleTime(int shuffleTime) {
		this.shuffleTime = shuffleTime;
	}

	public boolean isNonFateSharing() {
		return nonFateSharing;
	}

	public void setNonFateSharing(boolean nonFateSharing) {
		this.nonFateSharing = nonFateSharing;
	}

	public int getVmNum() {
		return vmNum;
	}

	public void setVmNum(int vmNum) {
		this.vmNum = vmNum;
	}

	public double getLowerReq() {
		return lowerReq;
	}

	public void setLowerReq(double lowerReq) {
		this.lowerReq = lowerReq;
	}

	public double getUpperReq() {
		return upperReq;
	}

	public void setUpperReq(double upperReq) {
		this.upperReq = upperReq;
	}

	public int getAccetance() {
		return accetance;
	}

	public void setAccetance(int accetance) {
		this.accetance = accetance;
	}

	public int getAccptWithBackups() {
		return accptWithBackups;
	}

	public void setAccptWithBackups(int accptWithBackups) {
		this.accptWithBackups = accptWithBackups;
	}
	
	public Random getRand_shuff() {
		return rand_shuff;
	}

	public void setRand_shuff(Random rand_shuff) {
		this.rand_shuff = rand_shuff;
	}
	
	public boolean isRelibilityFirst() {
		return relibilityFirst;
	}

	public void setRelibilityFirst(boolean relibilityFirst) {
		this.relibilityFirst = relibilityFirst;
	}

	public DDCThread(int shuffleTime, boolean nonFateSharing, int vmNum, double lowerReq, double upperReq, Random shuffleRandom,boolean relibilityFirst,
			String threadName) {
		super(threadName);
		this.shuffleTime = shuffleTime;
		this.nonFateSharing = nonFateSharing;
		this.vmNum = vmNum;
		this.lowerReq = lowerReq;
		this.upperReq = upperReq;
		this.rand_shuff = shuffleRandom;
		this.relibilityFirst = relibilityFirst;
	}

	@Override
	public void run() {
		int maxAcceptance = 0;
		int maxNumofAccWithBackup = 0;
		double obj = 0.0;
		long begin = System.currentTimeMillis();
		for (int i = 1; i < this.getShuffleTime() + 1; i++) {
			DDC ddc = new DDC();
			ddc.createDDC();

			RequestGenerator g = new RequestGenerator();
			ArrayList<Request> vms = g.generatingVMs(this.getVmNum(), this.getLowerReq(), this.getUpperReq());
			DDC_Algorithm da = new DDC_Algorithm();
			
			if(this.isRelibilityFirst())
				Collections.shuffle(vms,this.getRand_shuff());//only applicable to the non-first fit scenario

			if(this.isRelibilityFirst())
				da.mapVMBatches(ddc, vms, this.isNonFateSharing());
			else
				da.mapVMBatches_FirstFit(ddc, vms, this.isNonFateSharing());
			double temp = da.getAccept() - 0.001 * da.getBackup();
			if (temp > obj) {
				maxAcceptance = da.getAccept();
				maxNumofAccWithBackup = da.getBackup();
				obj = temp;
			}
			if (this.getShuffleTime() > 10 && i % (this.getShuffleTime() / 10) == 0)
				System.out.println("\t" + currentThread().getName() + ":\t" + i + ":\t" + obj + "\t"
						+ ((double) (System.currentTimeMillis() - begin) / 1000.0) + "s");
			if(!this.isRelibilityFirst())
				break;//for a first fit scenario, no shuffle process is applied
		}

		System.out.println("\n___________________________________\n" + currentThread().getName() + "\tRunning time:\t"
				+ ((double) (System.currentTimeMillis() - begin) / 1000.0) + "s\ntotal acceptance\t\t" + maxAcceptance
				+ "\r\ntotal accepted with backups\t" + maxNumofAccWithBackup
				+ "\n_________________________________\n");
		
		this.setAccetance(maxAcceptance);
		this.setAccptWithBackups(maxNumofAccWithBackup);

	}
}
