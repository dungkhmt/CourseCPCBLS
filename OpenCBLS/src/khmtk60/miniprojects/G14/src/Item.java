package khmtk60.miniprojects.G14.src;

import java.util.ArrayList;

public class Item {
	public final double w;
	public final double p;
	public final int t;
	public final int r;
	public final int index;
	public Bin bin = null;
	public ArrayList<Bin> bins = new ArrayList<>();
	public boolean in(Bin bin) {
		if (this.bin == null) {
			return false;
		}
		return this.bin.index == bin.index;
	}
	public Item (int index, double w, double p, int t, int r) {
		this.index = index;
		this.w = w;
		this.p = p;
		this.t = t;
		this.r = r;
	}
	@Override 
	public String toString() {
		return this.index + "";
	}
}
