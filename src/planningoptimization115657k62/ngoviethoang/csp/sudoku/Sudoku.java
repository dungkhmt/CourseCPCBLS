package planningoptimization115657k62.ngoviethoang.csp.sudoku;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Sudoku {
	final int n = 9;
	
	public void sudoku() {
		Model model = new Model("Sudoku");
		IntVar[][] x = new IntVar[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				x[i][j] = model.intVar("X["+i+"]["+j+"]", 1, 9);
			}
		}
		IntVar[][] row = new IntVar[n][n];
		for (int i = 0; i < n; i++) {
			row[i] = new IntVar[n];
			for (int j = 0; j < n; j++) {
				row[i][j] = x[i][j];
			}
			model.allDifferent(row[i]).post();
		}
		IntVar[][] col = new IntVar[n][n];
		for (int j = 0; j < n; j++) {
			col[j] = new IntVar[n];
			for (int i = 0; i < n; i++) {
				col[j][i] = x[i][j];
			}
			model.allDifferent(col[j]).post();
		}
		IntVar[][] con = new IntVar[n][n];
		for (int I = 0; I < 3; I++) {
			for (int J = 0; J < 3; J++) {
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						con[I*3+J][i*3+j] = x[I*3+i][J*3+j];
					}
				}
				model.allDifferent(con[I*3+J]).post();
			}
		}
		boolean ans = model.getSolver().solve();
		if (ans == false) { 
			System.out.println("No solution");
		} else {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					System.out.print(x[i][j].getValue() + " ");
				}
				System.out.println();
			}
		}
	}
	
	public static void main(String[] args) {
		Sudoku app = new Sudoku();
		app.sudoku();
	}
}

