package planningoptimization115657k62.phungngocminh;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import java.util.ArrayList;
import java.util.Random;

public class Sudoku {
	
	public void BuildModel() {
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[][] x = new VarIntLS[9][9];
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				x[i][j] = new VarIntLS(mgr, 1, 9);
		ConstraintSystem S = new ConstraintSystem(mgr);

		// Row Constraint
		for (int i = 0; i < 9; i++) {
			VarIntLS[] y = new VarIntLS[9];
			for (int j = 0; j < 9; j++) {
				y[j] = x[i][j];
			}
			S.post(new AllDifferent(y));
		}
		
		// Column Constraint
		for (int j = 0; j < 9; j++) {
			VarIntLS[] y = new VarIntLS[9];
			for (int i = 0; i < 9; i++)
				y[i] = x[i][j];
			S.post(new AllDifferent(y));
		}
		
		// Sub-Square Constraint
		for (int I = 0; I < 3; I++) {
			for (int J = 0; J < 3; J++) {
				VarIntLS[] y = new VarIntLS[9];
				int idx = -1;
				for (int i = 0; i < 3; i++)
					for (int j = 0; j < 3; j++) {
						idx++;
						y[idx] = x[i+3*I][j+3*J];
					}
				S.post(new AllDifferent(y));

			}
		}
		
		mgr.close();
	}
	
	public void generateInitialSolution() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				x[i][j].setValuePropagate(j+1);
			}
		}
	}
	
	class Move {
		int i;
		int j1; 
		int j2;
		Move(int i, int j1, int j2) {
			this.i = i;
			this.j1 = j1;
			this.j2 = j2;
		}
	}
	
	private void exploreNeighborhood(ArrayList<Move> cand) {
		cand.clear(); 
		int minDelta = Integer.MAX_VALUE;
		for (int i = 0; i < 9; i++) {
			for (int j1 = 0; j1 < 8; j1++) {
				for (int j2 = j1 + 1; j2 < 9; j2++) {
					int delta = S.getSwapDelta(x[i][j1], x[i][j2]);
					if (delta < minDelta) {
						cand.clear();
						cand.add(new Move(i, j1, j2));
						minDelta = delta;
					} else if (delta == minDelta)
						cand.add(new Move(i, j1, j2));
				}
			}
		}
	}
	
	public void search() {
		generateInitialSolution();
		ArrayList<Move> cand = new ArrayList<Move>();
		int it = 0;
		Random R = new Random();
		while (it < 100000 && S.violations() > 0) {
			exploreNeighborhood(cand);
			int idx = R.nextInt(cand.size());
			Move m = cand.get(idx);
			int i = m.i;
			int j1 = m.j1;
			int j2 = m.j2;
			x[i][j1].swapValuePropagate(x[i][j2]);
			it++;
			System.out.println("Violation " + it + "= " + S.violations());
		}
	}
	
	public void printSolution() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++)
				System.out.print("X[" + i + "][" + j + "] = " + x[i][j].getValue() + "  ");
			System.out.println();
		}
	
	}
	
	public static void main(String[] args) {
		Sudoku solver = new Sudoku();
		solver.buildModel();
		solver.search();
		solver.printSolution();
	}
}