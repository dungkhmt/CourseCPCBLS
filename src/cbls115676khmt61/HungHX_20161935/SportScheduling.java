package cbls115676khmt61.DoNgocSon_20163506;

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

	int INF = 10000000;

	public void buildModel() {
		solver = new MPSolver("SportScheduling",
				MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		X = new MPVariable[N][N][T + 1];
		Y = new MPVariable[N][N][T + 1];
		F = new MPVariable[N][N][N][T + 1];
		D = new MPVariable[N];

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++)
				if (i != j) {
					for (int t = 1; t <= T; t++) {
						X[i][j][t] = solver.makeIntVar(0, 1, "X[" + i + "," + j
								+ "," + t + "]");
						Y[i][j][t] = solver.makeIntVar(0, 1, "Y[" + i + "," + j
								+ "," + t + "]");
					}x
				}
		}
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++)
				if (i != j) {
					for (int k = 0; k < N; k++) {
						for (int t = 0; t <= T; t++) {
							F[i][j][k][t] = solver.makeIntVar(0, 1, "F[" + i
									+ "," + j + "," + k + "," + t + "]");
						}
					}
				}
		}
		for (int i = 0; i < N; i++) {
			D[i] = solver.makeIntVar(0, 1000, "D[" + i + "]");
		}

		for (int t=1; t<=T; t++)
			for (int i=0; i<N; i++) {
				MPConstraint c = solver.makeConstraint(1, 1);
				for (int j=0; j<N; j++)
					if (i != j) {
						c.setCoefficient(X[i][j][t], 1);
						c.setCoefficient(Y[i][j][t], 1);
					}
			}
		
		for (int i=0; i<N; i++)
			for (int j=0; j<N; j++)
				if (i != j)
					for (int t1=1; t1<=T; t1++)
						for (int t2=1; t2<=T; t2++)
							if (t1 != t2) {
								MPConstraint c = solver.makeConstraint(-MPSolver.infinity(), 1);
								c.setCoefficient(X[i][j][t1], 1);
								c.setCoefficient(X[i][j][t2], 1);
							}
		
		for (int i=0; i<N; i++)
			for (int j=0; j<N; j++)
				if (i != j)
					for (int t1=1; t1<=T; t1++)
						for (int t2=1; t2<=T; t2++)
							if (t1 != t2) {
								MPConstraint c = solver.makeConstraint(-MPSolver.infinity(), 1);
								c.setCoefficient(Y[i][j][t1], 1);
								c.setCoefficient(Y[i][j][t2], 1);
							}
		
		for (int i=0; i<N; i++)
			for (int j=0; j<N; j++)
				if (i != j) {
					MPConstraint c = solver.makeConstraint(1, 1);
					for (int t=1; t<=T; t++)	
						c.setCoefficient(X[i][j][t], 1);
				}
		
		for (int i=0; i<N; i++)
			for (int j=0; j<N; j++)
				if (i != j) {
					MPConstraint c = solver.makeConstraint(1, 1);
					for (int t=1; t<=T; t++)		
						c.setCoefficient(Y[i][j][t], 1);
				}
		
		for (int i=0; i<N; i++)
			for (int j=0; j<N; j++)
				if (j != i)
					for (int k=0; k<N; k++)
						if (k != i && k != j) 
							for (int t=1; t<T; t++) {
								MPConstraint c = solver.makeConstraint(-1, MPSolver.infinity());
								c.setCoefficient(F[i][j][k][t], 2);
								c.setCoefficient(Y[i][j][t], -1);
								c.setCoefficient(Y[i][k][t+1], -1);
							}
		
		for (int i=0; i<N; i++) {
			MPConstraint c = solver.makeConstraint(0, 0);
			c.setCoefficient(D[i], -1);
			for (int j=0; j<N; j++)
				for (int k=0; k<N; k++)
					if (i !=j && j != k && k != i)
						for (int t=1; t<=T; t++)
							c.setCoefficient(F[i][j][k][t], d[j][k]);
		}
		
			
		MPObjective obj = solver.objective();
		for (int i=0; i<N; i++)
			obj.setCoefficient(D[i], 1);
		obj.setMinimization();
		
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
									+ ") -> " + i + " vs " + j + " san " + i);
						if(Y[i][j][t].solutionValue() > 0){
							System.out.println("Y(" + i + "," + j + "," + t
									+ ") -> " + i + " vs " + j + " san " + j);
						}
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