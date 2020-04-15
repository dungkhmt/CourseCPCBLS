package planningoptimization115657k62.levanlinh.mixedintegerprogramming;

import java.util.HashSet;
import java.util.ArrayList;

import com.google.ortools.linearsolver.*;

public class TSPDynSEC {
	static {
		System.loadLibrary("jniortools");
	}
	
	int N = 11;
	int[][] c = { { 0, 29, 20, 21, 16, 31, 100, 12, 4, 31, 18 }, { 29, 0, 15, 29, 28, 40, 72, 21, 29, 41, 12 },
			{ 20, 15, 0, 15, 14, 25, 81, 9, 23, 27, 13 }, { 21, 29, 15, 0, 4, 12, 92, 12, 25, 13, 25 },
			{ 16, 28, 14, 4, 0, 16, 94, 9, 20, 16, 22 }, { 31, 40, 25, 12, 16, 0, 95, 24, 36, 3, 37 },
			{ 100, 72, 81, 92, 94, 95, 0, 90, 101, 99, 84 }, { 12, 21, 9, 12, 9, 24, 90, 0, 15, 25, 13 },
			{ 4, 29, 23, 25, 20, 36, 101, 15, 0, 35, 18 }, { 31, 41, 27, 13, 16, 3, 99, 25, 35, 0, 38 },
			{ 18, 12, 13, 25, 22, 37, 84, 13, 18, 38, 0 } };
	double inf = java.lang.Double.POSITIVE_INFINITY;
	MPSolver solver;
	MPVariable[][] X;
	
	private int findNext(int s) {
		for (int i = 0; i < N; ++i) {
			if (i != s && X[s][i].solutionValue() > 0) return i;
		}
		return -1;
	}
	
	public ArrayList<Integer> extractCycle(int s){
		ArrayList<Integer> L = new ArrayList<>();
		int x = s;
		while (true) {
			L.add(x);
			x = findNext(x);
			int rep = -1;
			for (int i = 0; i < L.size(); ++i)
				if (L.get(i) == x) {
					rep = i;
					break;
				}
			
			if (rep != -1) {
				ArrayList<Integer> rL = new ArrayList<>();
				for(int i = rep; i < L.size(); ++i) rL.add(L.get(i));
				return rL;
			}
		}
	}
	
	public void createSolver() {
		solver = new MPSolver("TSPDynSEC", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		
		X = new MPVariable[N][N];
		for (int i = 0; i < N; ++i)
			for (int j = 0;j < N; ++j)
				if (i != j) X[i][j] = solver.makeIntVar(0, 1, "X["+i+","+j+"]");
		
		MPObjective obj = solver.objective();
		for (int i = 0; i < N; ++i)
			for (int j = 0;j < N; ++j)
				if (i != j) obj.setCoefficient(X[i][j], c[i][j]);
		obj.setMinimization();
		
		for (int i = 0; i < N; ++i) {
			MPConstraint c1 = solver.makeConstraint(1,1);
			for (int j = 0;j < N; ++j)
				if (i != j)
					c1.setCoefficient(X[i][j], 1);
			MPConstraint c2 = solver.makeConstraint(1,1);
			for (int j = 0;j < N; ++j)
				if (i != j)
					c2.setCoefficient(X[j][i], 1);
		}
	}
	
	public void createSEC(HashSet<ArrayList<Integer>> S) {
		for (ArrayList<Integer> C: S) {
			MPConstraint sc = solver.makeConstraint(0, C.size() - 1);
			for (int i : C)
				for (int j : C) 
					if (i != j) sc.setCoefficient(X[i][j], 1);
		}
	}
	
	public void solveDynamicAddSubtourConstraint() {
		HashSet<ArrayList<Integer>> S = new HashSet<>();
		boolean[] mark = new boolean[N];
		boolean found = false;
		
		createSolver();
		while (!found) {
			createSEC(S);
			S.clear();
			
			MPSolver.ResultStatus rs = solver.solve();
			if (rs != MPSolver.ResultStatus.OPTIMAL) {
				System.err.println("The problem does not have optimal solution!");
				return;
			}
			
			System.out.println("obj = " + solver.objective().value());
			for (int i = 0; i < N; ++i) mark[i] = false;
			for (int s = 0; s < N; ++s) if (!mark[s]) {
				ArrayList<Integer> C = extractCycle(s);
				if (C.size() < N) { // subtour detected
					System.out.print("Subtour detected, C = ");
					for (int i : C) {
						System.out.print(i + " ");
						mark[i] = true;						
					}
					System.out.println();
					S.add(C);
				} else {
					System.out.println ("Global tour detected, solution found!");
					found = true; break;
				}
			}
			
		}
		
		ArrayList<Integer> tour = extractCycle(0);
		for (int i = 0; i < tour.size(); ++i)
			System.out.print(tour.get(i) + " -> ");
		System.out.println (tour.get(0));
	}
	
	public static void main(String[] args) {
		TSPDynSEC app = new TSPDynSEC();
		app.solveDynamicAddSubtourConstraint();
	}
}
