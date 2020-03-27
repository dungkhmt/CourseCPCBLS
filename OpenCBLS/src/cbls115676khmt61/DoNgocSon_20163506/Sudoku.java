package cbls115676khmt61.TranHuyHung_20164777;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Sudoku {
	class Move {
		int i, j1, j2;
		public Move(int r, int x1, int x2) {
			this.i = r;
			this.j1 = x1;
			this.j2 = x2;
		}
	}
	
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[][] X;
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[9][9];
		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++) {
				X[i][j] = new VarIntLS(mgr, 1, 9);
				X[i][j].setValue(j+1);
			}
		
		S = new ConstraintSystem(mgr);
//		for (int i=0; i<9; i++) {
//			VarIntLS[] y = new VarIntLS[9];
//			for (int j=0; j<9; j++)
//				y[j] = X[i][j];
//			S.post(new AllDifferent(y));
//		}

		for (int j=0; j<9; j++) {
			VarIntLS[] y = new VarIntLS[9];
			for (int i=0; i<9; i++)
				y[i] = X[i][j];
			S.post(new AllDifferent(y));
		}
		
		for (int I=0; I<3; I++)
			for (int J=0; J<3; J++) {
				VarIntLS[] y = new VarIntLS[9];
				int idx = -1;
				for (int i=0; i<3; i++)
					for (int j=0; j<3; j++) {
						idx++;
						y[idx] = X[3*I+i][3*J+j];
					}
				S.post(new AllDifferent(y));
			}
		
		mgr.close();
	}
	
	private void exploreNeighborhood(ArrayList<Move> candidate) {
		int minDelta = Integer.MAX_VALUE;
		candidate.clear();
		for (int i=0; i<9; i++)
			for (int j1=0; j1<8; j1++)
				for (int j2=j1+1; j2<9; j2++) {
					int delta = S.getSwapDelta(X[i][j1], X[i][j2]);
					if (delta < minDelta) {
						candidate.clear();
						candidate.add(new Move(i, j1, j2));
						minDelta = delta;
					}
					else if (delta == minDelta)
						candidate.add(new Move(i, j1, j2));
				}
	}
	
	private void generateInitialSolution() {
		ArrayList<Integer> permutation = new ArrayList<>(); 
		for (int i=0; i<9; i++)
			permutation.add(i);
		for (int i=0; i<9; i++) {
			Collections.shuffle(permutation);
			for (int j=0; j<9; j++)
				X[i][j].setValuePropagate(permutation.get(j));
		}
	}
	
	public void search(int maxIter) {
		Random R = new Random();
//		generateInitialSolution();
		
		int it = 0;
		ArrayList<Move> candidate = new ArrayList<Move>();
		while (it < maxIter && S.violations() > 0) {
			exploreNeighborhood(candidate);
			if (candidate.size() == 0) {
				System.out.println("Reach local optimum");
				break;
			}
			Move m = candidate.get(R.nextInt(candidate.size()));
			X[m.i][m.j1].swapValuePropagate(X[m.i][m.j2]);
			it++;
			System.out.println("Step " + it + ", violations = " + S.violations());
		}
		
		printSolution();
	}
	
	private void printSolution() {
		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++)
				System.out.print(X[i][j].getValue() + " ");
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		Sudoku app = new Sudoku();
		app.stateModel();
		app.search(100000);
	}
}
