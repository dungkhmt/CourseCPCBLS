package planningoptimization115657k62.ngoviethoang.modeling.sport;

import org.chocosolver.solver.Model;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;

public class Sport {
	static {
		System.loadLibrary("jniortools");
	}
	int N = 4;// number of teams 0,1,...N-1
	int[][] d = { { 0, 3, 2, 4 }, { 9, 0, 2, 3 }, { 1, 2, 0, 7 },
			{ 1, 1, 4, 0 } };
	int T = 6;// 2N-2
	MPVariable[][][] X; // new MPVariable[week][team][team];
	MPVariable[][][][] F; //  new MPVariable[team][team][team][week];
	MPVariable[] D;// new MPVariable[N];
	MPSolver solver;
	int INF = Integer.MAX_VALUE;
	private void printSol() {
		for (int k = 0; k < T; k++) {
			System.out.println("#" + (k+1));
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++)
					if (i != j) {
						if (X[k][i][j].solutionValue() > 0)
							System.out.println("X(" + (i+1) + "," + (j+1) + "," + (k+1)
									+ ") -> " + (i+1) + " vs " + (j+1) + " san " + (i+1));
					}
			}
			System.out.println("-------------");
		}
	}
	public void solve() {
		solver = new MPSolver("Sport", 
					MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		X = new MPVariable[T][N][N];
		F = new MPVariable[T][N][N][N];
		D = new MPVariable[N];
		for (int k = 0; k < T; k++) {
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					X[k][i][j] = solver.makeIntVar(0, 1, 
										"X["+k+"]["+i+"]["+j+"]");
					for (int l = 0; l < N; l++) {
						F[k][i][j][l] = solver.makeIntVar(0, 1, 
								"X["+k+"]["+i+"]["+j+"]["+l+"]");
					}
				}
			}
		}		
		for (int i = 0; i < N; i++) {
			D[i] = solver.makeIntVar(0, 1000
					, "D["+i+"]");
		}
		
		// Constraint 
		for (int k = 0; k < T; k++) {
			for (int i = 0; i < N; i++) {
				MPConstraint c = solver.makeConstraint(1, 1);
				for (int j = 0; j < N; j++) {
					if (i != j) {
						c.setCoefficient(X[k][i][j], 1);
						c.setCoefficient(X[k][j][i], 1);
					}
				}
			}
		}
		for (int k = 0; k < T; k++) {
			for (int i = 0; i < N; i++) {
				MPConstraint c = solver.makeConstraint(1,1);
				for (int j = 0; j < N; j++) {
					for (int l = 0; l < N; l++) {
						if (j != l) {
							c.setCoefficient(F[k][i][j][l], 1);
						}
					}
				}
			}
		}
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (i != j) {
					MPConstraint c1 = solver.makeConstraint(1,1);
					MPConstraint c2 = solver.makeConstraint(1,1);
					for (int k = 0; k < T; k++) {
						c1.setCoefficient(X[k][i][j], 1);
						c2.setCoefficient(X[k][j][i], 1);
					}
				}
			}
		}
		for (int i = 0; i < N; i++) {
			MPConstraint c = solver.makeConstraint(0,0);
			c.setCoefficient(D[i], 1);
			for (int j = 0; j < N; j++) {
				c.setCoefficient(X[0][j][i], -d[i][j]);
			}
			for (int k = 0; k < T; k++) {
				for (int j = 0; j < N; j++) {
					for (int l = 0; l < N; l++) {
						if (j != l) {
							c.setCoefficient(F[k][i][j][l], -d[j][l]);
						}
					}
				}
			}
			for (int j = 0; j < N; j++) {
				for (int l = 0; l < N; l++) {
					c.setCoefficient(F[T-1][i][j][l], -d[l][i]);					
				}
			}
		}
		int[] oneT = new int[T];
		for (int i = 0; i < T; i++) {
			oneT[i] = 1;
		}
		MPObjective obj = solver.objective();
		for (int i = 0; i < N; i++) {
			obj.setCoefficient(D[i], 1);
		}
		obj.setMinimization();
		ResultStatus rs = solver.solve();
		if (rs != ResultStatus.OPTIMAL) {
			System.out.println("Cannot find optimal solution");
		} else {
			System.out.println("obj= " + obj.value());
			printSol();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Sport app = new Sport();
		app.solve();
	}
}
