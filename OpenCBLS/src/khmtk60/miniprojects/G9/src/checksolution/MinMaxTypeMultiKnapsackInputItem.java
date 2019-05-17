package checksolution;

import java.util.Arrays;
import java.util.HashSet;

public class MinMaxTypeMultiKnapsackInputItem {
	private double w;
	private double p;
	private int t;
	private int r;

	private HashSet<Integer> binIndices;

	public MinMaxTypeMultiKnapsackInputItem(double w, double p, int t, int r, Integer[] binIndices) {
		super();
		this.w = w;
		this.p = p;
		this.t = t;
		this.r = r;
		this.binIndices = new HashSet<Integer>(Arrays.asList(binIndices));
	}

	public double getW() {
		return w;
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

	public HashSet<Integer> getBinIndices() {
		return binIndices;
	}

	public void setBinIndices(Integer[] binIndices) {
		this.binIndices = new HashSet<Integer>(Arrays.asList(binIndices));
	}

	public MinMaxTypeMultiKnapsackInputItem() {
		super();
		// TODO Auto-generated constructor stub
	}

}
