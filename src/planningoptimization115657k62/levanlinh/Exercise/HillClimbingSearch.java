package planningoptimization115657k62.levanlinh.Exercise;

import java.util.ArrayList;
import java.util.Random;

import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;

public class HillClimbingSearch {
	IConstraint S;
	VarIntLS[] x;
	Random R = new Random();
	
	public HillClimbingSearch(IConstraint S) {
		this.S = S;
		x = S.getVariables();
	}
	
	class Move {
		int i;
		int v;
		
		public Move(int i, int v) {
			this.i = i;
			this.v = v;
		}
	}
	
	void search(int MaxIter) {
		int it = 0;
		ArrayList<Move> cand = new ArrayList<>();
		while (it < MaxIter && S.violations() > 0) {
			cand = exploreNeighbors(cand);
			
			if (cand.size() == 0) {
				System.out.println("Reach local minimum!");
				break;
			}	
			int i = R.nextInt(cand.size());
			Move m = cand.get(i);
			x[m.i].setValuePropagate(m.v);
			System.out.println ("HCS: Step " + it + ", violation: " + S.violations());
			it++;
		}
	}
	
	ArrayList<Move> exploreNeighbors (ArrayList<Move> cand){
		cand.clear();
		int minDelta = Integer.MAX_VALUE;
		
		for (int i = 0; i < x.length; i++)
			for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++)
				if (v != x[i].getValue()) {
					int delta = S.getAssignDelta(x[i], v);
					if (delta <= 0) {
						if (delta < minDelta) {
							cand.clear();
							cand.add(new Move(i, v));
							minDelta = delta;
						}
						if (delta == minDelta) {
							cand.add(new Move(i, v));
						}
					}
				}
		return cand;
	}
}
