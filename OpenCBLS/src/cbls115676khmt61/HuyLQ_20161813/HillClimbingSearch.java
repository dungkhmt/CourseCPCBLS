package Leodoi;

import java.util.ArrayList;
import java.util.Random;

import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;

public class HillClimbingSearch {
	private class AssignMove{
		int i, v; //index and value of move
		public AssignMove(int i, int v) {
			this.i = i;
			this.v = v;
		}
	}
	private IConstraint c;
	private ArrayList<AssignMove> cand;
	private VarIntLS[] y;
	private Random R;
	public HillClimbingSearch(IConstraint c) {
		this.c = c;
		y = c.getVariables();
		cand = new ArrayList<HillClimbingSearch.AssignMove>();
		R = new Random();
	}
	public void exploreNeighborhood() {
		this.cand.clear();
		int minDelta = Integer.MAX_VALUE;
		for (int i = 0; i < y.length; i++) {
			for (int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) {
				int d = c.getAssignDelta(y[i], v); //su thay doi muc do vi pham khi y[i] duoc gan gia tri v
				if (d < minDelta) {
					this.cand.clear();
					this.cand.add(new AssignMove(i, v));
					minDelta = d;
				}
				else if (d == minDelta){
					this.cand.add(new AssignMove(i, v));
				}
			}
		}
	}
	public void search(int maxIter) {
		int it = 0;
		while (it < maxIter && c.violations() > 0) {
			exploreNeighborhood();
			AssignMove m = cand.get(R.nextInt(cand.size()));
			y[m.i].setValuePropagate(m.v);
			System.out.print("iter " + it + ": ");
			for (int i = 0; i < y.length; i++) {
				System.out.print(y[i].getValue() + " ");
			}
			System.out.println(", violations: " + c.violations());
			it++;
		}
	}
}
