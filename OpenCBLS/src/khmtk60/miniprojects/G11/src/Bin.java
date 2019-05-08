package khmtk60.miniprojects.G11.src;

import java.util.ArrayList;

public class Bin {
	public final double w;
	public final double lw;
	public final double p;
	public final int t;
	public final int mt, mr;
	public int index;
	public final int r;
	private double currentW = 0;
	private double currentP = 0;
	public double violation = 0;
	public ArrayList<Item> items = new ArrayList<>();
	public ArrayList<Item> candidateItems = new ArrayList<>();
	private int tNum[];
	private int rNum[];
	public int currentT = 0;
	public int currentR = 0;
	public Bin(int index, double w, double lw, double p, int t, int r, int mt, int mr) {
		this.index = index;
		this.w = w;
		this.lw = lw;
		this.p = p;
		this.t = t;
		this.r = r;
		this.mt = mt;
		this.mr = mr;
		this.tNum = new int[mt];
		this.rNum = new int[mr];
	}
	@Override
	public Bin clone() {
		Bin cloned = new Bin(index, w, lw, p, t, r, mt, mr);
		cloned.items = new ArrayList<>();
		for (Item item: items) {
			cloned.items.add(item);
		}
		cloned.currentP = currentP;
		cloned.currentR = currentR;
		cloned.currentT = currentT;
		cloned.currentW = currentW;
		cloned.violation = violation;
		return cloned;
	}
	@Override 
	public String toString() {
		String result = "[";
		for (Item item: items) {
			result += item.toString() + ", ";
		}
		result += "]";
		return result;
	}
	public boolean violateIfAddItem(Item item) {
		if (tNum[item.t] == 0 && currentT >= t) {
			return true;
		}
		if (rNum[item.r] == 0 && currentR >= r) {
			return true;
		}
		if (item.w + currentW > w) {
			return true;
		}
		if (item.p + currentP > p) {
			return true;
		}
		return false;
	}
	public double getAddItemDelta(Item item) {
		double delta = 0;
		if (currentW <= lw) {
			if (currentW + item.w > lw) {
				delta -= lw - currentW;
			} else {
				delta -= item.w;
			}
		}
		if (currentW <= w && currentW + item.w > w) {
			delta += currentW + item.w - w;
		}
		if (currentP <= p && currentP + item.p > p) {
			delta += currentP + item.p - p;
		}
		if (tNum[item.t] == 0) {
			delta ++;
		}
		if (rNum[item.r] == 0) {
			delta ++;
		}
		return delta;
	}
	public double getRemoveItemDelta(Item item) {
		double delta = 0;
		if (currentW >= lw) {
			if (currentW - item.w < lw) {
				delta += lw - (currentW - item.w);
			}
		}
		if (currentW < lw) {
			delta += item.w;
		}
		return delta;
	}
	
	public void addItem(Item item) {
		items.add(item);
		currentW += item.w;
		currentP += item.p;
		tNum[item.t]++;
		if (tNum[item.t] == 1)  {
			currentT++;
		}
		rNum[item.r]++;
		if (rNum[item.r] == 1)  {
			currentR++;
		}
		this.updateViolation();
	}
	public void removeItem(Item removedItem) {
		items.removeIf(item -> item.index == removedItem.index);
		currentW -= removedItem.w;
		currentP -= removedItem.p;
		tNum[removedItem.t]--;
		if (tNum[removedItem.t] == 0)  {
			currentT--;
		}
		rNum[removedItem.r]--;
		if (rNum[removedItem.r] == 0)  {
			currentR--;
		}
		this.updateViolation();
	}
	public double getW() {
		return this.currentW;
	}
	public double getP() {
		return this.currentP;
	}
	private void updateViolation() {
		violation = 0;
		if (items.isEmpty()) {
			return;
		}
		if (currentW < lw) {
//			violation ++;
			violation += Math.abs(lw - currentW);
		}
		
		if (currentW > w) {
//			violation++;
			violation += Math.abs(w - currentW);
		}
		
		if (currentP > p) {
//			violation++;
			violation += Math.abs(p - currentP) * Solution1.LAMBDA;
		}
		
		if (currentR > r) {
//			violation++;
			violation += Math.abs(r - currentR) * Solution1.LAMBDA;
		}
		
		if (currentT > t) {
			violation++;
		}
		
		if (violation > 0) {
			violation += items.size();
		}
		
		if (violation < 1e-10) {
			violation = 0;
		}

	}
	
}
