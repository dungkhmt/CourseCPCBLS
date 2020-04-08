package BT;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class GraphPartitioningCBLS {
	private LocalSearchManager mgr;
	private VarIntLS[] X;
	private ConstraintSystem S;
	private GraphPartitioningCost f;
	
	class SwapMove {
		int i, j;

		public SwapMove(int i, int j) {
			this.i = i;
			this.j = j;
		}
	}
	
	Random R = new Random();
	
	
	int n = 10;
	private int[][] E = { { 1, 2, 8 }, { 1, 3, 2 }, { 1, 7, 3 }, { 2, 3, 8 }, { 2, 4, 7 }, { 2, 7, 4 }, { 2, 8, 6 },
			{ 0, 2, 5 }, { 3, 5, 1 }, { 3, 8, 5 }, { 4, 6, 8 }, { 4, 7, 9 }, { 0, 4, 1 }, { 5, 9, 5 }, { 5, 8, 4 },
			{ 6, 9, 4 }, { 0, 6, 7 }, { 0, 8, 2 }, { 0, 9, 8 } };
	int c[][] = new int[][]{
        {0, 0, 5, 0, 1, 0, 7, 0, 2, 8},
        {0, 0, 8, 2, 0, 0, 0, 3, 0, 0},
        {5, 8, 0, 8, 7, 0, 0, 4, 6, 0},
        {0, 2, 8, 0, 0, 1, 0, 0, 5, 0},
        {1, 0, 7, 0, 0, 0, 8, 9, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 4, 5},
        {7, 0, 0, 0, 8, 0, 0, 0, 0, 4},
        {0, 3, 4, 0, 9, 0, 0, 0, 0, 0},
        {2, 0, 6, 5, 0, 4, 0, 0, 0, 3},
        {8, 0, 0, 0, 0, 5, 4, 0, 3, 0}};

	public void stateModel() {
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
		X = new VarIntLS[n];

		for (int i = 0; i < n; i++) {
			X[i] = new VarIntLS(mgr, 0, 1);
		}

		S.post(new IsEqual(new Sum(X), n / 2));

		VarIntLS[][] Z = new VarIntLS[n][n];

		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				Z[i][j] = new VarIntLS(mgr, 0,1);
				S.post(new Implicate(new IsEqual(Z[i][j], 1), new NotEqual(X[i], X[j])));
				S.post(new Implicate(new NotEqual(X[i], X[j]), new IsEqual(Z[i][j], 1)));
			}
		
		f = new GraphPartitioningCost(X, c);

		mgr.close();
	}
	
	public void initSolution() {
		for(int i=0;i<n/2;i++) {
			X[i].setValuePropagate(0);
		}
		for(int i=n/2;i<n;i++) {
			X[i].setValuePropagate(1);
		}
	}
	
	public void search(int maxIter) {
		int it = 0;
		ArrayList<SwapMove> cand = new ArrayList<>();
		
		while(it<maxIter) {
			cand.clear();
			int minDeltaF = Integer.MAX_VALUE;

			for (int i = 0; i < X.length; i++) {
				for (int j = i+1; j < X.length; j++) if(X[i].getValue() != X[j].getValue()) {
					int deltaF = f.getSwapDelta(X[i], X[j]);
					if (deltaF < 0) {
						//System.out.println("delta: " + deltaF);
						if (deltaF < minDeltaF) {
							cand.clear();
							cand.add(new SwapMove(i, j));
							minDeltaF = deltaF;
						} else if (deltaF == minDeltaF) {
							cand.add(new SwapMove(i, j));
						}
					}
				}
			}
			
			if (cand.size() == 0) {
				System.out.println("Reach local optimum");
				// print result
				for (int i = 0; i < X.length; i++) {
					System.out.println("X[" + i + "]: " + X[i].getValue());
				}
				break;
			}
			
			
			SwapMove m = cand.get(R.nextInt(cand.size()));
			X[m.i].swapValuePropagate(X[m.j]);
			//f.propagateInt(X[i], 0);

			System.out.println("Step " + it + ", function value = " + f.getValue());
			it++;
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphPartitioningCBLS g = new GraphPartitioningCBLS();
		g.stateModel();
		g.initSolution();
		g.search(1000);
	}

}
