package model;

public class Bin {
	double W,LW,P;
	double sumW,w,p;
	int T,R;
	int D;
	boolean wait;
	int item;
	boolean useable;
	public Bin(MinMaxTypeMultiKnapsackInputBin bin,int i) {
		this.W = bin.getCapacity();
		this.LW = bin.getMinLoad();
		this.P = bin.getP();
		this.T = bin.getT();
		this.R = bin.getR();
		this.D = 0;
		this.w = 0;
		this.p = 0;
		this.sumW = 0;
		this.wait = false;
		this.item = 0;
		this.useable = true;
	}
}
