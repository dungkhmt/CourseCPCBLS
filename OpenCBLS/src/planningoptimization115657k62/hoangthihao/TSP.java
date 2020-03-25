package demmo2;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.util.HashSet;

public class TSP {
	static {
		System.loadLibrary("jniortools");
	}
	
	public void solveTSP() {
		
		
	int N = 5;
	int [][] c = {
			{0,4,2,5,6},
			{2,0,5,2,7},
			{1,2,0,6,3},
			{7,5,8,0,3},
			{1,2,4,3,0}};
	//double inf = java.lang.Double.POSITIVE_INFINITY;
	
	MPSolver solver = new MPSolver("TSPSolver", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
	MPVariable[][] X = new MPVariable[N][N];
	for (int i = 0; i < N; i++)
		for (int j = 0; j < N; j++)
			if(i != j)
				X[i][j] = solver.makeIntVar(0, 1, "X[" + i + ", " + j + "]");
	
	// flow constraint
	for (int i = 0; i < N; i++) {
		MPConstraint fc1 = solver.makeConstraint(1,1);
		for (int j = 0; j < N; j++)
			if (j != i)
				fc1.setCoefficient(X[i][j], 1);
	
		MPConstraint fc2 = solver.makeConstraint(1,1);
		for (int j = 0; j < N; j++)
			if(i !=j)
				fc2.setCoefficient(X[j][i], 1);

	}
	
	// SUC constraint
	SubSetGenerator generator = new SubSetGenerator(N);
	HashSet<Integer> S = generator.first();
	while(S != null) {
		if (S.size() > 1 && S.size() < N) {
			MPConstraint sc = solver.makeConstraint(0, S.size() - 1);
			for(int i: S) {
				for(int j: S)
					if(i != j) {
						sc.setCoefficient(X[i][j], 1);
					}
			}
		}
		S = generator.next();
	}
	
	MPObjective obj = solver.objective();
	for (int i = 0; i < N; i++)
		for (int j = 0; j < N; j++)
			if(i != j)
				obj.setCoefficient(X[i][j], c[i][j]);
	
	final MPSolver.ResultStatus rs = solver.solve();
	if (rs != MPSolver.ResultStatus.OPTIMAL) {
		System.out.println("No solution");
	} else {
		System.out.println("Time " + solver.wallTime());
		System.out.println("Obj = " + obj.value());
	}
}
				
	public static void main(String[] args) {
		TSP app = new TSP();
		app.solveTSP();
	}
}

// generate subsets of {0,1,...,N-1}
class SubSetGenerator {
	int N;
	int[] X; // represents binary sequence
	public SubSetGenerator(int N) {
		this.N = N;
	}
	
	public HashSet<Integer> first() {
		X = new int[N];
		for (int i = 0; i < N; i++)
			X[i] = 0;
		HashSet<Integer> S = new HashSet<Integer>();
		for (int i = 0; i < N; i++)
			if(X[i] == 1) 
				S.add(i);
		return S;
	}
	
	public HashSet<Integer> next() {
		int j = N-1;
		while(j >= 0 && X[j] == 1) {
			X[j] = 0;
			j--;
		}
		if (j >=0) {
			X[j] = 1;
			HashSet<Integer> S = new HashSet<Integer>();
			for(int i = 0; i < N; i++)
				if(X[i] == 1)
					S.add(i);
			return S;
		} else {
			return null;
		}
	}
}
