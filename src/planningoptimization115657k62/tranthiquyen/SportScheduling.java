package my_work;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;

public class SportScheduling {
	
	static {
		System.loadLibrary("jniortools");
	}
	
	int N = 4;// number of teams 0,1,...N-1
	int[][] d = { { 0, 3, 2, 4 }, { 9, 0, 2, 3 }, { 1, 2, 0, 7 },
			{ 1, 1, 4, 0 } };
	int T = 6;// 2N-2

	MPVariable[][][] X = null;// new MPVariable[N][N][T + 1];
	MPVariable[][][] Y = null;// new MPVariable[N][N][T + 1];
	MPVariable[][][][] F = null;// new MPVariable[N][N][N][T + 1];
	MPVariable[] D = null;// new MPVariable[N];
	MPSolver solver;
	MPObjective obj;

	int INF = 10000000;

	public void buildModel() {
		solver = new MPSolver("SportScheduling",
				MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		X = new MPVariable[N][N][T + 1];
		Y = new MPVariable[N][N][T + 1];
		F = new MPVariable[N][N][N][T + 1];
		D = new MPVariable[N];
		obj = solver.objective();

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++)
				if (i != j) {
					for (int t = 1; t <= T; t++) {
						X[i][j][t] = solver.makeIntVar(0, 1, "X[" + i + "," + j
								+ "," + t + "]");
//						Y[i][j][t] = solver.makeIntVar(0, 1, "Y[" + i + "," + j
//								+ "," + t + "]");
					}
				}
		}
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++)
				//if (i != j) {
					for (int k = 0; k < N; k++) if(k != j) {
						for (int t = 0; t <= T; t++) {
							F[i][j][k][t] = solver.makeIntVar(0, 1, "F[" + i
									+ "," + j + "," + k + "," + t + "]");
						}
					}
				//}
		}
		for (int i = 0; i < N; i++) {
			D[i] = solver.makeIntVar(0, 1000, "D[" + i + "]");
		}
		/// Them cac rang buoc:
		for(int t = 1; t<T; t++) {
			for (int i = 0; i<N; i++) {
				for (int j = 0; j<N; j++) {
					for(int k = 0; k<N; k++) if(j != k) {
						MPConstraint c = solver.makeConstraint(0, 0);
						c.setCoefficient(X[i][j][t],1);
						c.setCoefficient(X[i][k][t+1], 1);
						c.setCoefficient(F[i][j][k][t], -2);
					}
				}
			}
		}
		
		MPVariable d1, d2, d3; //cac bien tam
		d1 = solver.makeIntVar(0, INF, "d1");
		d2 = solver.makeIntVar(0, INF, "d2");
		d3 = solver.makeIntVar(0, INF, "d3");
		
		for(int i = 0; i<N; i++) {
				
			MPConstraint c1 = solver.makeConstraint(0, 0);
			for(int ii = 0; ii<N; ii++)
				for(int j = 0; j<N; j++) if(ii != j){
					c1.setCoefficient(X[ii][j][1], d[j][ii]);
				}
			c1.setCoefficient(d1, -1);
			
			MPConstraint c2 = solver.makeConstraint(0, 0);
			for(int t = 1; t<=T; t++) {
				for(int j = 0; j<N; j++) {
					for(int k = 0; k<N; k++) if(j != k) {
						c2.setCoefficient(F[i][j][k][t], d[j][k]);
					}
				}
			}
			c2.setCoefficient(d2,  -1);
			
			MPConstraint c3 = solver.makeConstraint(0, 0);
			for(int j = 0; j<N; j++) {
				c3.setCoefficient(F[i][j][i][T], d[j][i]);
			}
			c3.setCoefficient(d3, -1);
			
		}
		
		for(int i = 0; i<N; i++) {
			MPConstraint cc = solver.makeConstraint(0, 0);
			cc.setCoefficient(d1, 1);
			cc.setCoefficient(d2, 1);
			cc.setCoefficient(d3, 1);
			cc.setCoefficient(D[i], -1);
		}
		
		
		for(int i = 0; i<N; i++) 
			for(int t = 1; t<=T; t++){
				MPConstraint cc = solver.makeConstraint(1, 1);
				for(int j = 0; j<N; j++) {
					for(int k = 0; k<N; k++) if(j != k){
						cc.setCoefficient(F[i][j][k][t], 1);
					}
				}
			}

		// Ham muc tieu:
		for(int i = 0; i<N; i++) {
			obj.setCoefficient(D[i], 1);
		}
		
			
		ResultStatus rs = solver.solve();
		if (rs != ResultStatus.OPTIMAL) {
			System.out.println("cannot find optimal solution");
		} else {
			System.out.println("obj= " + obj.value());
			printSol();
		}
	}

	private void printSol() {
		for (int t = 1; t <= T; t++) {
			System.out.println("#" + t);
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++)
					if (i != j) {
						if (X[i][j][t].solutionValue() > 0)
							System.out.println("X(" + i + "," + j + "," + t
									+ ") -> " + i + " vs " + j + " san " + j);
						/*
						 * if(Y[i][j][t].solutionValue() > 0){ System.out.println("Y(" + i + "," + j +
						 * "," + t + ") -> " + i + " vs " + j + " san " + j); }
						 */
					}
			}
			System.out.println("-------------");
		}

		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SportScheduling app = new SportScheduling();
		app.buildModel();
	}

}
