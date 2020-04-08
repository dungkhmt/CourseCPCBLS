package practice.search;

import java.util.ArrayList;
import java.util.Random;

import localsearch.model.*;


public class HillClimbingSearch {
	private IConstraint c;
	private ArrayList<AssignMove> cand;
	private VarIntLS[] y;
	private Random R;
	
	public HillClimbingSearch(IConstraint c) {
		this.c = c;
		y = c.getVariables();
		cand = new ArrayList<AssignMove>();
		R = new Random();
	}
	
	private void exploreNeighborhood() {
		cand.clear();
		int minDelta = Integer.MAX_VALUE;
		for (int i = 0; i < y.length; i++) {
			for (int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) {
				int d = c.getAssignDelta(y[i], v);
				if (d < minDelta) {
					cand.clear();
					minDelta = d;
				}
				cand.add(new AssignMove(i, v));
			}
		}
	}
	
	public void search(int maxIter) {
		int it = 0;
		while (it < maxIter && c.violations() > 0) {
			exploreNeighborhood();
			AssignMove m = cand.get(R.nextInt(cand.size()));
			y[m.i].setValuePropagate(m.v);
			it++;
		}
	}
}
