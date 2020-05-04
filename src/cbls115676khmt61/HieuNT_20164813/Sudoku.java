import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Sudoku {
	
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[][] X;
	Random R = new Random();
	
	class SwapMove {
		int i;
		int j1;
		int j2;
		public SwapMove(int i, int j1, int j2) {
			this.i = i;
			this.j1 = j1;
			this.j2 = j2;
		}
	}
	
	private void exploreNeighbor_2D(IConstraint c, VarIntLS[][] x, ArrayList<SwapMove> candidate) {
		int minDelta = Integer.MAX_VALUE;
		System.out.println("min delta " + minDelta);
		candidate.clear();
		for(int i = 0; i < 9; i++) {
			for(int j1 = 0; j1 < 8; j1++) {
				for(int j2 = j1 + 1; j2 < 9; j2++) {
					int delta = c.getSwapDelta(x[i][j1], x[i][j2]);
					if(delta < minDelta) {
						candidate.clear();
						candidate.add(new SwapMove(i, j1, j2));
						minDelta = delta;
					} else if(minDelta == delta) {
						candidate.add(new SwapMove(i, j1, j2));
					}
				}
			}
		}
		System.out.println("min delta " + minDelta);
	}
	
	public void search_2D(IConstraint c, int maxIter) {
		VarIntLS[][] x = new VarIntLS[9][9];
//		genInitialSol_Soduku(x);
//		for(int i = 0; i < 9; i++) {
//			x[i] = c.getVariables();
//		}
		
		int it = 0; 
		ArrayList<SwapMove> candidate = new ArrayList<SwapMove>();
		while(it < maxIter && c.violations() > 0) {
			exploreNeighbor_2D(c, x, candidate);
			System.out.println(candidate.size());
			if(candidate.size() == 0) {
				System.out.println("Reach local optimum");
				break;
			}
			SwapMove m = candidate.get(R.nextInt(candidate.size()));
			x[m.i][m.j1].swapValuePropagate(x[m.i][m.j2]);
			System.out.println("Step " + it + ", violation " + c.violations());
			it++;
		}
	}
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[9][9];
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				X[i][j] = new VarIntLS(mgr, 1, 9);
				X[i][j].setValue(j+1);
			}
		}
		
		S = new ConstraintSystem(mgr);
		for(int i = 0; i < 9; i++) {
			VarIntLS[] y = new VarIntLS[9];
			for(int j = 0; j < 9; j++) {
				y[j] = X[i][j];
			}
			S.post(new AllDifferent(y));
		}
		for(int i = 0; i < 9; i++) {
			VarIntLS[] y = new VarIntLS[9];
			for(int j = 0; j < 9; j++) {
				y[j] = X[j][i];
			}
			S.post(new AllDifferent(y));
		}
		for(int I = 0; I < 3; I++) {
			for(int J = 0; J < 3; J++) {
				int idx = -1;
				VarIntLS[] y = new VarIntLS[9];
				for(int i = 0; i < 3; i++) {
					for(int j = 0; j < 3; j++) {
						idx++;
						y[idx] = X[I*3 + i][J*3 + j];
					}
				}
				S.post(new AllDifferent(y));
			}
		}
		mgr.close();
	}
 	
	public static void main(String[] args) {
		Sudoku app = new Sudoku();
		app.stateModel();
		app.search_2D(app.S, 100000);
//		for(int i = 0; i < X.length; i++) {
//			System.out.println("X[" + i + "]: "  + X[i].getValue());
//		}
	}
}
