package model;


public class Item {
	double w,p;
	int t,r;
	int D;
	int index,hIndex;
	int bin;
	int id;
	
	public Item(MinMaxTypeMultiKnapsackInputItem item) {
		this.w = item.getW();
		this.p = item.getP();
		this.t = item.getT();
		this.r = item.getR();
		this.D = item.getBinIndices().length;
		this.bin = -1;
	}
	
	public Item(int D) {this.D = D;};
	public String toString() {
		return String.format("D: %d\tIdx:%d",D,hIndex);
	}
}
