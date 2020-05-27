package planningoptimization115657k62.ngoviethoang.ls.queen;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;
import planningoptimization115657k62.ngoviethoang.ls.HillClimbingSearch;

public class Queen {
	LocalSearchManager mgr;
	VarIntLS[] x;
	ConstraintSystem cs;
	int N = 50;
	
	public void buildModelAssign() {
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
			z[i] = new FuncMinus(x[i], i);
		}
		cs.post(new AllDifferent(z));
		mgr.close();
	}
	
	public void buildModelSwap() {
		mgr = new LocalSearchManager();
		x = new VarIntLS[N];
		cs = new ConstraintSystem(mgr);
		
		for (int i = 0; i < N; i++) {
			x[i] = new VarIntLS(mgr, 0, N-1);
		}		
		
		IFunction[] y = new IFunction[N];
		for (int i = 0; i < N; i++) {
			y[i] = new FuncPlus(x[i], i);
		}
		cs.post(new AllDifferent(y));
		
		IFunction[] z = new IFunction[N];
		for (int i = 0; i < N; i++) {
			z[i] = new FuncMinus(x[i], i);
		}
		cs.post(new AllDifferent(z));
		mgr.close();
	}
	
	public void generateInitialSolution() {
		for (int i = 0; i < N; i++)
			x[i].setValuePropagate(i);
	}
	
	class MoveAssign{
		int i; 
		int v;
		public MoveAssign(int i, int v) {
			this.i = i;
			this.v = v;
		}
	}
	
	class MoveSwap {
		int i;
		int j;
		public MoveSwap(int i, int j) {
			this.i = i;
			this.j = j;
		}
	}
	
	public void exploreNeighborhoodAssign(ArrayList<MoveAssign> cand) {
		int minDelta = Integer.MAX_VALUE;
		cand.clear();
		for (int i = 0; i < N; i++) {
			for (int v = 0; v < N; v++) {
				if (x[i].getValue() != v) {
					int delta = cs.getAssignDelta(x[i], v);
					if (delta < minDelta) {
						minDelta = delta;
						cand.clear();
						cand.add(new MoveAssign(i, v));
					} else {
						if (delta == minDelta) {
							cand.add(new MoveAssign(i, v));
						}
					}
				}
			}
		}
	}
	
	public void exploreNeighborhoodSwap(ArrayList<MoveSwap> cand) {
		int minDelta = Integer.MAX_VALUE;
		cand.clear();
		for (int i = 0; i < N-1; i++) {
			for (int j = i+1; j < N; j++) {
				int delta = cs.getSwapDelta(x[i], x[j]);
				if (delta < minDelta) {
					minDelta = delta;
					cand.clear();
					cand.add(new MoveSwap(i, j));
				} else {
					if (delta == minDelta) {
						cand.add(new MoveSwap(i, j));
					}
				}
			}
		}
	}
	
	public void print() {
		for (int i = 0; i < N; i++) {
			System.out.print(x[i].getValue() +" ");
		}
		System.out.println();
	}
	
	public void searchAssign() {
		buildModelAssign();
		ArrayList<MoveAssign> cand = new ArrayList<MoveAssign>();
		int it = 0;
		Random r = new Random();
		while(it < 1000 & cs.violations() > 0) {
			exploreNeighborhoodAssign(cand);
			if (cand.size() == 0) {
				System.out.println("Reach local optimum");
				break;
			} else {
				MoveAssign m = cand.get(r.nextInt(cand.size()));
				x[m.i].setValuePropagate(m.v); 
				System.out.println("Step "+it+", violation = "+cs.violations());
				it++;
			}
		}
		print();
	}
	
	public void searchSwap() {
		buildModelSwap();
		generateInitialSolution();
		ArrayList<MoveSwap> cand = new ArrayList<MoveSwap>();
		int it = 0;
		Random r = new Random();
		while (it < 1000 && cs.violations() > 0) {
			exploreNeighborhoodSwap(cand);
			if (cand.size() == 0) {
				System.out.println("Reach local optimum");
				break;
			} else {
				MoveSwap m = cand.get(r.nextInt(cand.size()));
				int v1 = x[m.i].getValue();
				int v2 = x[m.j].getValue();
				x[m.i].setValuePropagate(v2); 
				x[m.j].setValuePropagate(v1);
				System.out.println("Step "+it+", violation = "+cs.violations());
				it++;
			}
		}
		print();
	}
	
	public void search() {
		buildModelAssign();
		HillClimbingSearch s = new HillClimbingSearch();
		s.search(cs, 1000);
		print();
	}
	
	public void searchTwoStepGreedy() {
		buildModelAssign();
		generateInitialSolution();
		MinMaxSelector mms = new MinMaxSelector(cs);
		int it = 0;
		while (it < 1000 && cs.violations() > 0) {
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_v = mms.selectMostPromissingValue(sel_x);
			sel_x.setValuePropagate(sel_v);
			System.out.println("Step " + it + ", violations = " + cs.violations());
			it++;
		}
		print();
	}
	
	public static void main(String[] args) {
		Queen app = new Queen();
		app.search();
		//app.searchSwap();
		//app.searchAssign();
		//app.searchTwoStepGreedy();
	}
}
