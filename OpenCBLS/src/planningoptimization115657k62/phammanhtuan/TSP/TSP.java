package TSP;

import java.util.HashSet;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPSolverResponseStatus;
import com.google.ortools.linearsolver.MPVariable;

public class TSP {
	static {
		System.loadLibrary("jniortools");
	}
	private int N = 10;
	private int c[][] = {
			{0,4,2,5,6,3,4,6,3,6},
			{2,0,5,2,7,2,3,5,4,2},
			{1,2,0,6,3,4,3,4,9,3},
			{7,5,8,0,3,1,2,6,4,5},
			{1,2,4,3,0,5,2,6,5,4},
			{2,6,3,4,2,0,5,6,6,3},
			{2,6,4,3,2,4,0,4,6,3},
			{1,3,5,6,8,4,2,0,6,4},
			{2,4,2,3,4,5,2,4,0,5},
			{1,6,5,3,7,6,2,3,4,0}};
	final double INF = java.lang.Double.POSITIVE_INFINITY;
	MPSolver solver;
	MPVariable[][] X;
	public void solve () {
		solver = new MPSolver("TSP",MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING) ;
		X = new MPVariable[N][N];
		for (int i=0;i<N;i++) 
			for (int j=0;j<N;j++) {
			    if (i!=j) X[i][j] = solver.makeIntVar(0, 1, "X["+i+"]["+j+"]");
			}
		MPObjective obj = solver.objective();
		for (int i=0;i<N;i++)
			for (int j=0;j<N;j++) {
				if (i!=j) obj.setCoefficient(X[i][j], c[i][j]);
			}
		obj.setMinimization();
		for (int i=0;i<N;i++) {
			MPConstraint c1 = solver.makeConstraint(1,1);
			for (int j=0;j<N;j++) {
				if (i!=j) c1.setCoefficient(X[i][j], 1);
			}
			MPConstraint c2 = solver.makeConstraint(1,1);
			for (int j=0;j<N;j++) {
				if (i!=j) c2.setCoefficient(X[j][i], 1);
			}
		}
//		SubSetGenerator gene = new SubSetGenerator(N);
//		HashSet<Integer> S = gene.first();
//		while (S!=null) {
//			if (S.size()>0 && S.size()<N) {
//				MPConstraint c3 = solver.makeConstraint(0,S.size()-1);
//				for (int i:S)
//					for (int j : S) {
//						if (i!=j) c3.setCoefficient(X[i][j], 1);
//					}
//			}
//			S = gene.next();
//		}
		int[]  a = new int[N];
		int k;
		int temp,dem;
		HashSet<Integer> S = new HashSet<Integer>();
		do {
			k=0;dem=0;;
		MPSolver.ResultStatus rs = solver.solve();
		if (rs != MPSolver.ResultStatus.OPTIMAL) {
			System.err.println ("The problem does not have an optimal");
			return;
		} else {
			for (int i = 0;i<N;i++) 
				for (int j = 0;j<N;j++) {
					if (i!=j) {
						if (X[i][j].solutionValue()==1)
						a[k++] = j;
					}
				}
			    temp = a[0];
			    dem = 0;
			    while (a[temp]!=0) {
				    temp = a[temp];
				    dem++;
				    S.add(temp);
			    }
		if (S.size()>0 && S.size()<N) {
			MPConstraint c3 = solver.makeConstraint(0,S.size()-1);
			for (int i:S)
				for (int j : S) {
					if (i!=j) c3.setCoefficient(X[i][j], 1);
				}
		}
		}
		System.out.println("1");
		} while (dem <N-1);
		System.out.println("Optimal objective value = "+solver.objective().value());
	}
	public static void main(String[] args) {
		TSP a = new TSP();
		a.solve();
	}
}
