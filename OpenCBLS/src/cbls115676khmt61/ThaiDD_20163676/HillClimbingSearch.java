package cbls115676khmt61.ThaiDD_20163676;

import java.util.ArrayList;
import java.util.Random;

import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;

public class HillClimbingSearch {
	class AssignMove {
		int i;
		int v;
		public AssignMove(int i, int v) {
			this.i = i;
			this.v = v;
		}
	}
	
	public IConstraint c;
	public ArrayList<AssignMove> cand;
	public VarIntLS[] y;
	public Random R;
	
	public HillClimbingSearch(IConstraint c) {
		this.c = c;
		y = c.getVariables();
		cand = new ArrayList<AssignMove>();
		R = new Random();
	}
	
	public void exploreNeighborhood() {
		cand.clear();
		int minDelta = Integer.MAX_VALUE;
		for (int i = 0; i < y.length; i++) {
			for (int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) {
				if(v == y[i].getValue()) continue;
				int d = c.getAssignDelta(y[i], v);
				if (d < minDelta) {
					cand.clear();
					cand.add(new AssignMove(i, v));
					minDelta = d;
				} else if (d == minDelta) {
					cand.add(new AssignMove(i, v));
				}
			}
		}
	}
	
	public void search(int maxIter) {
		int it = 0;
		while(it < maxIter && c.violations() > 0) {
			exploreNeighborhood();
			AssignMove m = cand.get(R.nextInt(cand.size()));
			y[m.i].setValuePropagate(m.v);
			System.out.println("Step " + it + ", violation = " + c.violations());
			it++;
		}
	}
}
