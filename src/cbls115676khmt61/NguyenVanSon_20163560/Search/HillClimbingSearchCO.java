package cbls115676khmt61.NguyenVanSon_20163560.Search;


import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.ConstraintViolations;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class HillClimbingSearchCO {
	class Move {
		int i, v;
		public Move(int i, int v) {
			this.i = i;
			this.v = v;
		}
	}
	
	private IConstraint c;
	private IFunction f;
	private VarIntLS[] X;
	private IFunction F;
	private Random R = new Random();
	
	public HillClimbingSearchCO(IConstraint c, IFunction f, VarIntLS[] X, IFunction F) {
		this.c = c;
		this.f = f;
		this.X = X;
		this.f = F;
	}
	
//	public void exploreNeighborhood(ArrayList<Move> cand) {
//		int minDelta = 0;
//		cand.clear();
//		for (int i=0; i<X.length; i++) {
//			for (int v=X[i].getMinValue(); v<=X[i].getMaxValue(); v++) {
//				if (v == X[i].getValue())
//					continue;
//				int delta = F.getAssignDelta(X[i], v);
//				if (delta < minDelta) {
//					cand.clear();
//					cand.add(new Move(i, v));
//					minDelta = delta;
//				}
//				else if (delta == minDelta)
//					cand.add(new Move(i, v));
//			}
//		}
//	}
	
	public void exploreNeighborhoodCF(ArrayList<Move> cand) {
		int minDeltaC = Integer.MAX_VALUE;
		int minDeltaF = Integer.MAX_VALUE;
		cand.clear();
		for (int i=0; i<X.length; i++) {
			for (int v=X[i].getMinValue(); v<=X[i].getMaxValue(); v++) {
				if (v == X[i].getValue())
					continue;
				int deltaC = c.getAssignDelta(X[i], v);
				int deltaF = f.getAssignDelta(X[i], v);
				if (deltaC < 0 || (deltaC == 0 && deltaF < 0))
					if (deltaC < minDeltaC || (deltaC == minDeltaC && deltaF < minDeltaF)) {
						cand.clear();
						cand.add(new Move(i, v));
						minDeltaC = deltaC;
						minDeltaF = deltaF;
					}
					else if (deltaC == minDeltaC && deltaF == minDeltaF)
						cand.add(new Move(i, v));
			}
		}
	}
	
	public void search(int maxIter) {
		int it = 0;
		ArrayList<Move> cand = new ArrayList<>();
		while (it < maxIter) {
			exploreNeighborhoodCF(cand);
			if (cand.size() == 0) {
				System.out.println("Reach local optimum");
				break;
			}
			Move m = cand.get(R.nextInt(cand.size()));
			X[m.i].setValuePropagate(m.v);
			it++;
			System.out.println("Step " + it + ", violations = " + c.violations()
			+ ", objective = " + f.getValue());
		}
	}
	
	public static void main(String[] args) {
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] X = new VarIntLS[6];
		for (int i=0; i<6; i++)
			X[i] = new VarIntLS(mgr,1,10);
		ConstraintSystem S = new ConstraintSystem(mgr);
		S.post(new AllDifferent(X));
		
		IFunction f = new FuncPlus(new FuncMult(X[0], 3), new FuncMult(X[4], 5));
		IFunction cv = new ConstraintViolations(S);
		IFunction F = new FuncPlus(new FuncMult(cv, 1000), new FuncMult(f, 1));
		
		mgr.close();
		
		HillClimbingSearchCO searcher = new HillClimbingSearchCO(S, f, X, F);
		searcher.search(1000);
	}
}
