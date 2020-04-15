package planningoptimization115657k62.hoangthanhlam;

import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;
import localsearch.model.LocalSearchManager;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;

import java.util.ArrayList;
import java.util.Random;

public class HillClimbing {
	
	class AssignMove {
		int i;
		int v;
		public AssignMove(int i, int v) {
			this.i = i;
			this.v = v;
		}
	}
	
	Random R = new Random();
	
	private void explorNeighborhood(IConstraint c, VarIntLS[] x, ArrayList<AssignMove> candidate) {
		int minDelta = Integer.MAX_VALUE;
		candidate.clear();
		for (int i = 0; i < x.length; i++){
			for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
				if (v == x[i].getValue()) continue;// ignore current solution
				int delta = c.getAssignDelta(x[i], v);
				if (delta < minDelta) {
					candidate.clear();
					candidate.add(new AssignMove(i,v));
					minDelta = delta;
				} else if (delta == minDelta) {
					candidate.add(new AssignMove(i,v));
				}
			}
		}
	}
	
	// Khởi tạo giá trị ngẫu nhiên cho mảng x trong khoảng minValue đến maxValue 
	private void generateInitialSolution(VarIntLS[] x) {
		for (int i = 0; i < x.length; i++) {
			int v = R.nextInt(x[i].getMaxValue() - x[i].getMinValue() + 1) + x[i].getMinValue();
			x[i].setValuePropagate(v);
		}
	}
	
	public void search(IConstraint c, int maxIter) {
		VarIntLS[] x = c.getVariables();
		
		generateInitialSolution(x);
		
		int it = 0;
		ArrayList<AssignMove> candidate = new ArrayList<AssignMove>();
		while (it < maxIter && c.violations() > 0) {
			explorNeighborhood(c, x, candidate);
			if (candidate.size() == 0) {
				System.out.println("Reach local optimum");
				break;
			}
			
			AssignMove m = candidate.get(R.nextInt(candidate.size()));// select randomly a move in candidate
			x[m.i].setValuePropagate(m.v);// local move (assign value, update violations, data structures
			it++;
			System.out.println("Step " + it + ", violations = " + c.violations());
		}
	}
	
	public void test(int n) {
		LocalSearchManager ls = new LocalSearchManager();
		ConstraintSystem c = new ConstraintSystem(ls);
		
		VarIntLS[] x = new VarIntLS[n];
		for (int i = 0; i < n; i++) {
			x[i] = new VarIntLS(ls, 0, n-1);
		}
		c.post(new AllDifferent(x));
		
		IFunction[] f1 = new IFunction[n];
		for (int i = 0; i < n; i++) {
			f1[i] = new FuncPlus(x[i], i);
		}
		c.post(new AllDifferent(f1));
		
		IFunction[] f2 = new IFunction[n];
		for (int i = 0; i < n; i++) {
			f2[i] = new FuncPlus(x[i], -i);
		}
		c.post(new AllDifferent(f2));
		
		ls.close();
		
		search(c, 1000);
		System.out.println("Result = " + c.violations());
	}
	
	public static void main (String[] args) {
		System.out.println("_QueenHillClimbing_");
		HillClimbing lam = new HillClimbing();
		lam.test(1000);
	}
}
