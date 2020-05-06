package cbls115676khmt61.levanhoang_20161669;

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
	
	public Random R = new Random();
	
	private void explorNeighborhood(IConstraint c, VarIntLS[] x, ArrayList<AssignMove> candidate) {
		/*
		 * c : constraints
		 * x : 
		 * candidate: 
		 */
		int minDelta = Integer.MAX_VALUE;
		for (int i = 0; i < x.length; i++) {
			for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
				if (v == x[i].getValue()) continue;   //ignore current solution 
				
				int delta = c.getAssignDelta(x[i], v);   // muc do vi pham khi gan x[i] = v 
				
				if (delta < minDelta) {
					candidate.clear();
					candidate.add(new AssignMove(i, v));
					minDelta = delta;
				} else if (delta == minDelta) {
					candidate.add(new AssignMove(i, v));
				}
			}
		}
	}
	
	public void generateInitialSolution(VarIntLS[] x) {
		for (int i = 0 ; i < x.length; i++) {
			int v = R.nextInt(x[i].getMaxValue() - x[i].getMinValue()+1 ) + x[i].getMinValue();
			x[i].setValuePropagate(v);
		}
	}
	
	public void search(IConstraint c, int maxIter) {
		VarIntLS[] x = c.getVariables();  // Danh sach bien: X1, X2, ...
		
		int it = 0;
		
		ArrayList<AssignMove> candidate = new ArrayList<AssignMove>();
		
		generateInitialSolution(x);
		
		while(it < maxIter && c.violations() > 0) {
			explorNeighborhood(c, x, candidate);
			if (candidate.size() == 0) {
				System.out.println("Reach local optimal!");
				break;
			}
			AssignMove m = candidate.get(R.nextInt(candidate.size()));   // select randomly a move in candidate 
			x[m.i].setValuePropagate(m.v);   // local move 
			it++;
			System.out.println("Step " + it + ", violation = " + c.violations());
		}
	}
}
