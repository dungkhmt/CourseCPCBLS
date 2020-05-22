package cbls115676khmt61.NguyenVanSon_20163560.Search;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import localsearch.constraints.basic.IsEqual;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;
public class GraphPartitioning {
	private int[][] E = {
            {1,2,8},
            {1,3,2},
            {1,7,3},
            {2,3,8},
            {2,4,7},
            {2,7,4},
            {2,8,6},
            {0,2,5},
            {3,5,1},
            {3,8,5},
            {4,6,8},
            {4,7,9},
            {0,4,1},
            {5,9,5},
            {5,8,4},
            {6,9,4},
            {0,6,7},
            {0,8,2},
            {0,9,8}
    };
	private int N = 10;
	
	class Move {
		int i, j;
		public Move(int i, int j) {
			this.i = i;
			this.j = j;
		}
	}
	
	private LocalSearchManager mgr;
	private VarIntLS[] X;
	private ConstraintSystem S;
	private IFunction f;
	
	private void stateModel() {
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
		
		X = new VarIntLS[N];
		for (int i=0; i<N; i++)
			X[i] = new VarIntLS(mgr, 0, 1);
		S.post(new IsEqual(new Sum(X), N / 2));
		
		IFunction[] w = new IFunction[E.length];
		for (int i=0; i<E.length; i++) {
			int[] e = E[i];
			IFunction part = new FuncMinus(X[e[0]], X[e[1]]);
			w[i] = new FuncMult(new FuncMult(part, part), e[2]);
		}
		f = new Sum(w);
		
		mgr.close();
	}
	
	private void generateInitialSolution() {
		ArrayList<Integer> sol = new ArrayList<>();
		for (int i=0; i<N/2; i++) {
			sol.add(0);
			sol.add(1);
		}
		Collections.shuffle(sol);
		for (int i=0; i<N; i++)
			X[i].setValuePropagate(sol.get(i));
	}
	
	private void exploreNeighborhood(ArrayList<Move> candidate) {
		int minDelta = 0;
		candidate.clear();
		for (int i=0; i<N; i++)
			for (int j=i+1; j<N; j++) {
				int delta = f.getSwapDelta(X[i], X[j]);
				if (delta < minDelta) {
					candidate.clear();
					candidate.add(new Move(i, j));
					minDelta = delta;
				}
//				else if (delta == minDelta)
//					candidate.add(new Move(i, j));
			}
	}
	
	public void search(int maxIter) {
		Random R = new Random();
		generateInitialSolution();
		
		int it = 0;
		ArrayList<Move> candidate = new ArrayList<Move>();
		while (it < maxIter) {
			exploreNeighborhood(candidate);
			if (candidate.size() == 0) {
				System.out.println("Reach local optimum");
				break;
			}
			Move m = candidate.get(R.nextInt(candidate.size()));
			X[m.i].swapValuePropagate(X[m.j]);
			it++;
			System.out.println("Step " + it + ", objective = " + f.getValue());
		}
		
		printSolution();
	}
	
	private void printSolution() {
		System.out.print("Part:");
		for (int i=0; i<N; i++)
			System.out.print(" " + X[i].getValue());
	}
	
	public static void main(String[] args) {
		GraphPartitioning app = new GraphPartitioning();
		app.stateModel();
		app.search(10);
	}
}
