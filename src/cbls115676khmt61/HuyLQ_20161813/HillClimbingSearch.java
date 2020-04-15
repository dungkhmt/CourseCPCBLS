package cbls115676khmt61.HuyLQ_20161813;

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
	private Random R = new Random();
	public void exploreNeighborhood(IConstraint c, VarIntLS[] x, ArrayList<AssignMove> candidate) {
		candidate.clear();
		int minDelta = Integer.MAX_VALUE;
		for (int i = 0; i < x.length; i++) {
			for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
				int d = c.getAssignDelta(x[i], v); //su thay doi muc do vi pham khi y[i] duoc gan gia tri v
				if (d < minDelta) {
					candidate.clear();
					candidate.add(new AssignMove(i, v));
					minDelta = d;
				}
				else if (d == minDelta){
					candidate.add(new AssignMove(i, v));
				}
			}
		}
	}
	private void generateInitialSolution(VarIntLS[] x){
		for(int i = 0; i < x.length; i++){
			int v = R.nextInt(x[i].getMaxValue() - x[i].getMinValue() + 1) + x[i].getMinValue();
			x[i].setValuePropagate(v);
		}
	}
	public void search(IConstraint c, int maxIter){
		VarIntLS[] x = c.getVariables();

		generateInitialSolution(x);

		int it = 0;// iteration
		ArrayList<AssignMove> candidate = new ArrayList<AssignMove>();
		while(it < maxIter && c.violations() > 0){
			exploreNeighborhood(c, x, candidate);
			if(candidate.size() == 0){
				System.out.println("Reach local optimum");
				break;
			}
			AssignMove m = candidate.get(R.nextInt(candidate.size()));// select randomly a move in candidate
			x[m.i].setValuePropagate(m.v);// local move (assign value, update violations, data structures
			it++;
			System.out.println("Step " + it + ", violations = " + c.violations());
		}
	}
}
