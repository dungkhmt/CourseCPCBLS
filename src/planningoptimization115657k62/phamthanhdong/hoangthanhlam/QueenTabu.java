package planningoptimization115657k62.hoangthanhlam;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class QueenTabu {

	public int n;
	public QueenTabu(int n) {
		this.n = n;
	}
	
	LocalSearchManager ls;
	ConstraintSystem S;
	VarIntLS[] x;
	
	public void createModel() {
		ls = new LocalSearchManager();
		S = new ConstraintSystem(ls);
		
		x = new VarIntLS[n];
		for (int i = 0; i < n; i++) {
			x[i] = new VarIntLS(ls, 0, n-1);
		}
		
		S.post(new AllDifferent(x));
		
		IFunction[] f1 = new IFunction[n];
		for (int i = 0; i < n; i++) {
			f1[i] = new FuncPlus(x[i], i);
		}
		S.post(new AllDifferent(f1));
		
		IFunction[] f2 = new IFunction[n];
		for (int i = 0; i < n; i++) {
			f2[i] = new FuncPlus(x[i], -i);
		}
		S.post(new AllDifferent(f2));
		
		ls.close();
	}
	
	class Move {
		int i; int v;
		public Move(int i, int v) {
			this.i = i; this.v = v;
		}
	}
	
	public void search(int maxIter) {
		int[][] tabu = new int[n][n];
		int tbl = 20;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				tabu[i][j] = -1;
			}
		}
		
		int nic = 0;
		int maxStable = 100;
		int best = S.violations();
		Random R = new Random();
		int it = 0;
		
		ArrayList<Move> cand = new ArrayList<Move>();
		while (it < maxIter && S.violations() > 0) {
			int minDelta = Integer.MAX_VALUE;
			for (int i = 0; i < n; i++) {
				for (int v = 0; v < n; v++) {
					if (v != x[i].getValue()) {
						int delta = S.getAssignDelta(x[i], v);
						if (tabu[i][v] <= it || delta + S.violations() < best) {
							if (delta < minDelta) {
								minDelta = delta;
								cand.clear();
								cand.add(new Move(i, v));
							} else if (delta == minDelta) {
								cand.add(new Move(i, v));
							}
						}
					}
				}
			}
			Move m = cand.get(R.nextInt(cand.size()));
			
			x[m.i].setValuePropagate(m.v);
			tabu[m.i][m.v] = it + tbl;
			
			if (S.violations() < best) {
				best = S.violations();
				nic = 0;
			} else {
				nic++;
				if (nic >= maxStable) {
					for (int i = 0; i < n; i++) {
						x[i].setValuePropagate(R.nextInt(n));
					}
				}
			}
			System.out.println("Step " + it + " S = " + S.violations());
			it++;
		}
	}
	
	public void printSolution() {
		System.out.print("Solution: ");
		for (int i = 0; i < n; i++) {
			System.out.print(x[i].getValue() + " ");
		}
	}
	public static void main(String[] args) {
		QueenTabu app = new QueenTabu(500);
		app.createModel();
		app.search(10000);
		app.printSolution();
	}

}
