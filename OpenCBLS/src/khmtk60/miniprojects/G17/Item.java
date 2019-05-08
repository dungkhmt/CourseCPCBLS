package khmtk60.miniprojects.G17;

public class Item {
	private double w;
	private double p;
	private int t;
	private int r;
	private int[] binIndices;
	private int assignTo;
	private int idx;

	public Item(double w, double p, int t, int r, int[] binIndices, int idx) {
		super();
		this.w = w;
		this.p = p;
		this.t = t;
		this.r = r;
		this.binIndices = binIndices;
		this.assignTo = -1;
		this.setIdx(idx);
	}

	public double violations() {
		return assignTo == -1 ? 1 : 0;
	}

	public double getW() {
		return w;
	}

	public void setAssignTo(int assignTo) {
		this.assignTo = assignTo;
	}

	public int getAssignTo() {
		return assignTo;
	}

	public void setW(double w) {
		this.w = w;
	}

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}

	public int getT() {
		return t;
	}

	public void setT(int t) {
		this.t = t;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int[] getBinIndices() {
		return binIndices;
	}

	public void setBinIndices(int[] binIndices) {
		this.binIndices = binIndices;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}
}
