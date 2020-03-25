package ortools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class TravelingSalesmanProblem {

	private int N;
	private int[][] A;
	
	private MPVariable[][] X;
	private MPVariable Y;
	private MPSolver solver;
	private MPObjective obj;
	private int[] B;
	
	static {
		System.loadLibrary("jniortools");
	}
	public HashSet<Integer> firstSubtour(int N) {
		B = new int[N + 5];
		HashSet<Integer> S = new HashSet<Integer>();
		return S;
	}
	public HashSet<Integer> nextSubtour() {
		int j = N;
		while ((j >= 0) && (B[j] == 1)) {
			B[j] = 0;
			j --;
		}
		if (j == 0)
			return null;
		B[j] = 1;
		HashSet<Integer> S = new HashSet<Integer>();
		for(int i = 1; i <= N; i++)
			if (B[i] == 1)
				S.add(i);
		return S;
	}
	public void readData() {
		Scanner input = new Scanner(System.in);
		System.out.print("Nhap N: ");
		N = input.nextInt();
		
		A = new int[N + 5][N + 5];
		for(int i = 1; i <= N; i++)
			for(int j = 1; j <= N; j++)
				A[i][j] = input.nextInt();
		input.close();
	}
	
	public void init() {
		solver = new MPSolver("TravelingSalesmanProblem", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		X = new MPVariable[N + 5][N + 5];
		for(int i = 1; i <= N; i++)
			for(int j = 1; j <= N; j++)
				if (i != j)
					X[i][j] = solver.makeIntVar(0, 1, "");
				else X[i][j] = solver.makeIntVar(0, 0, "");
	}
	
	//Rang buoc moi dinh chi co mot cung di ra
	public void makeConstraint1() {
		for(int i = 1; i <= N; i++) {
			MPConstraint c = solver.makeConstraint(1, 1, "");
			for(int j = 1; j <= N; j++)
				if (i != j)
					c.setCoefficient(X[i][j], 1);
		}
	}
	
	//Rang buoc moi dinh chi co mot cung di vao
	public void makeConstraint2() {
		for(int i = 1; i <= N; i++) {
			MPConstraint c = solver.makeConstraint(1, 1, "");
			for(int j = 1; j <= N; j++)
				if (i != j)
					c.setCoefficient(X[j][i], 1);
		}
	}
	
	//Rang buoc khong co chu trinh con
	/*public static void makeConstraint3() {
		HashSet<Integer> S = firstSubtour(N);
		while (S != null) {
			//System.out.println("Subtour...");
			int len = S.size();
			if ((len < N) && (len >= 2)) {
				MPConstraint c = solver.makeConstraint(0, len - 1, "");
				for(int a : S)
					for(int b : S)
						if (a != b)
							c.setCoefficient(X[a][b], 1);
			}
			S = nextSubtour();
		}
		
	}*/
	//tim chu trinh con
	public int findNext(int s) {
		for(int i = 1; i <= N; i++)
			if ((i != s) && (X[s][i].solutionValue() == 1))
				return i;
		return -1;
	}
	public ArrayList<Integer> extractCycle(int s) {
		ArrayList<Integer> L = new ArrayList<Integer>(); L.clear();
		ArrayList<Integer> rL = new ArrayList<Integer>(); rL.clear();
		int x = s; 
		while (true) {
			L.add(x); 
			x = findNext(x);
			if (x == -1) {
				ArrayList<Integer> rL2 = new ArrayList<Integer>(); rL2.clear();
				return rL2;
			}
				
			int repeat = -1;
			for(int i = 0; i < L.size(); i++)
				if (L.get(i) == x) {
					repeat = i;
					break;
				}
			if (repeat != -1) {
				for(int i = repeat; i < L.size(); i++)
					rL.add(L.get(i));
				break;
			}
		}
		return rL;
	}
	public void makeConstraint(HashSet<ArrayList<Integer>> S) {
		init();
		makeConstraint1();
		makeConstraint2();
		for(ArrayList<Integer> C : S) {
			MPConstraint c = solver.makeConstraint(0, C.size() - 1, "");
			for(int i = 0; i < C.size(); i++)
				for(int j = 0; j < C.size(); j++) 
					if (i != j)
						c.setCoefficient(X[C.get(i)][C.get(j)], 1);
		}
	}
	public void makeObjective() {
		obj = solver.objective();
		for(int i = 1; i <= N; i++)
			for(int j = 1; j <= N; j++)
				obj.setCoefficient(X[i][j], A[i][j]);
		obj.setMinimization();
	}
	public void solveMIP() {
		makeObjective();
		HashSet<ArrayList<Integer>> S = new HashSet<ArrayList<Integer>>(); S.clear();
		int[] mark = new int[N + 5];
		int found = 0;
		while (found == 0) {
			MPSolver.ResultStatus rs = solver.solve();
			if (rs != MPSolver.ResultStatus.OPTIMAL) {
				System.out.println("Cann't find optimal solution");
				return;
			} else {
				System.out.println("Obj = " + obj.value());
				for(int i = 1; i <= N; i++)
					mark[i] = 0;
				for(int s = 1; s <= N; s++)
					if (mark[s] == 0) {
						ArrayList<Integer> C = extractCycle(s);
						if ((C.size() < N) && (C.size() > 1)) {
							System.out.print("Subtour Detected ");
							for(int i : C)
								System.out.print(i + "  ");
							System.out.println();
							MPConstraint c = solver.makeConstraint(0, C.size() - 1, "");
							for(int i : C) 
								for(int j : C)
									if (i != j)
										c.setCoefficient(X[i][j], 1);
							S.add(C);
							for(int i : C)
								mark[i] = 1;
						} else if (C.size() == N) {
							found = 1;
							break;
						}
					} 
			}
		}
		ArrayList<Integer> tour = extractCycle(1);
		System.out.println("Solution value: " + obj.value());
		for(int i = 0; i < tour.size(); i++)
			System.out.print(tour.get(i) + " -> ");
		System.out.println(1);
	}
	public void solveTSP() {
		readData();
		init();
		makeConstraint1();
		makeConstraint2();
		solveMIP();
	}

}
