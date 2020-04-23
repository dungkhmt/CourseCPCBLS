package planningoptimization115657k62.ngoviethoang.ts.queen;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Queen {
	LocalSearchManager mgr;
	VarIntLS[] x;
	ConstraintSystem cs;
	int N = 50;
	
	class Move {
		int i;
		int v;
		public Move(int i, int v) {
			this.i = i;
			this.v = v;
		}
	}
	
	public void buildModel() {
		mgr = new LocalSearchManager();
		x = new VarIntLS[N];
		cs = new ConstraintSystem(mgr);
		
		for (int i = 0; i < N; i++) {
			x[i] = new VarIntLS(mgr, 0, N-1);
		}		
		cs.post(new AllDifferent(x));
		
		IFunction[] y = new IFunction[N];
		for (int i = 0; i < N; i++) {
			y[i] = new FuncPlus(x[i], i);
		}
		cs.post(new AllDifferent(y));
		
		IFunction[] z = new IFunction[N];
		for (int i = 0; i < N; i++) {
			z[i] = new FuncPlus(x[i], -i);
		}
		cs.post(new AllDifferent(z));
		mgr.close();
	}
	
	
	
	public void tabuSearch() {
		buildModel();
		int tbl = 20;
		int maxStable = 100;
		int tabu[][] = new int[N][N];
		for (int i = 0; i < N; i++) {
			for (int v = 0; v < N; v++) {
				tabu[i][v] = -1;
			}
		}
		int it = 0;
		int best = cs.violations();
		
		Random r =  new Random();
		ArrayList<Move> cand = new ArrayList<Move>();
		int nic = 0;
		while (it < 10000 && cs.violations() > 0) {
			int minDelta = Integer.MAX_VALUE;
			for (int i = 0; i < N; i++) {
				for (int v = 0; v < N; v++) {
					if (x[i].getValue() != v) {
						int delta = cs.getAssignDelta(x[i], v);
						if (tabu[i][v] <= it ||  cs.violations() + delta < best ) {
							if (delta < minDelta) {
								minDelta = delta;
								cand.clear();
								cand.add(new Move(i, v));
							} else {
								if (delta == minDelta) {
									cand.add(new Move(i, v));
								}
							}						
						}
					}
				}
			}
			Move m = cand.get(r.nextInt(cand.size()));
			tabu[m.i][m.v] = it + tbl;
			x[m.i].setValuePropagate(m.v);
			if (cs.violations() < best) {
				best = cs.violations();
				nic = 0;
			} else {
				nic += 1;
				if (nic >= maxStable) {
					for(int i = 0; i < N; i++) x[i].setValuePropagate(r.nextInt(N));
					System.out.println("nic > MaxStable. Random new state");
				}
			}
			System.out.println("Iteration: " + it + ", violations: " + cs.violations());
			it++;
		}
	}
	
	public static void main(String[] args) {
		Queen app = new Queen();
		app.tabuSearch();
	}
}
