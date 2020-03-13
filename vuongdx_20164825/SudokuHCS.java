package practice;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import practice.search.HillClimbingSearch;

public class SudokuHCS {
	private LocalSearchManager mgr;
	private VarIntLS[][] X;
	private ConstraintSystem S;
	
	private void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				X[i][j] = new VarIntLS(mgr, 1, 9);
				X[i][j].setValue(j + 1);
			}
		}
		
		S = new ConstraintSystem(mgr);
		for (int i = 0; i < 9; i++) {
			VarIntLS[] y = new VarIntLS[9];
			for (int j = 0; j < 9; j++) {
				y[j] = X[i][j];
			}
			S.post(new AllDifferent(y));
		}
		
		for (int i = 0; i < 9; i++) {
			VarIntLS[] y = new VarIntLS[9];
			for (int j = 0; j < 9; j++) {
				y[j] = X[j][i];
			}
			S.post(new AllDifferent(y));
		}
		
		for (int I = 0; I < 3; I++) {
			for (int J = 0; J < 3; J++) {
				VarIntLS[]  y = new VarIntLS[9];
				int idx = -1;
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						idx++;
						y[idx] = X[3 * I + i][3 * J + j];
					}
				}
				S.post(new AllDifferent(y));
			}
		}
		
		mgr.close();
	}
	
	private void search() {
		HillClimbingSearch s = new HillClimbingSearch(S);
		s.search(100000);
	}
	
	private void printResult() {
		System.out.println(S.violations());
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				System.out.print(X[i][j].getValue() + " ");
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		SudokuHCS sudoku = new SudokuHCS();
		sudoku.stateModel();
		sudoku.search();
		sudoku.printResult();
	}
}
