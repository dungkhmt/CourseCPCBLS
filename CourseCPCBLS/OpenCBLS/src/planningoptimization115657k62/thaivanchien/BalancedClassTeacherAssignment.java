package ortools;

import java.util.Scanner;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;


public class BalancedClassTeacherAssignment1 {

	private static int N;
	private static int M;
	private static int Q;
	private static int totalCredits;
	private static int[] credits;
	private static int[] qu;
	private static int[] qv;
	private static int[][] teachClass;
	
	private static MPSolver solver;
	private static MPVariable Y;
	private static MPVariable[][] X;
	private static MPObjective obj;
	
	private static double inf = java.lang.Double.POSITIVE_INFINITY;
	
	static {
		System.loadLibrary("jniortools");
	}
	
	public static void readData() {
		Scanner input = new Scanner(System.in);
		
		//N: so luong lop, M: so luong giang vien
		N = input.nextInt();
		M = input.nextInt();
		
		teachClass = new int[M][N];
		for(int i = 0; i < M; i++)
			for(int j = 0; j < N; j++)
				teachClass[i][j] = 0;
		
		for(int i = 0; i < M; i++) {
			//k: so luong so lop ma giang vien i co the day
			int k = input.nextInt();
			for(int j = 0; j < k; j++) {
				int tmp = input.nextInt();
				teachClass[i][tmp] = 1;
			}
		}
		
		credits = new int[N];
		for(int i = 0; i < N; i++) {
			credits[i] = input.nextInt();
			totalCredits += credits[i];
		}
		
		Q = input.nextInt();
		qu = new int[Q];
		qv = new int[Q];
		for(int i = 0; i < Q; i++) {
			qu[i] = input.nextInt();
			qv[i] = input.nextInt();
		}
		
		input.close();
	}
	
	//
	public static void init() {
		solver = new MPSolver("BalancedClassTeacherAssignment", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		Y = solver.makeIntVar(0, totalCredits, "y");
		X = new MPVariable[N][M];
		
		for(int i = 0; i < N; i++)
			for(int j = 0; j < M; j++)
				X[i][j] = solver.makeIntVar(0, teachClass[j][i], "x[" + i + "][" + j + "]");
	}
	
	//Constraint X[i1][j] + X[i2][j] <= 1, voi moi (i1, i2) thuoc Q
	public static void makeConstraint1() {
		for(int j = 0; j < M; j++)
			for(int q = 0; q < Q; q++) {
				MPConstraint cstr = solver.makeConstraint(0, 1, "");
				
				cstr.setCoefficient(X[qu[q]][j], 1);
				cstr.setCoefficient(X[qv[q]][j], 1);
			}
	}
	
	//Constraint tong X[i][j] = 1, j = 0..M-1 voi moi i = 0..N-1
	public static void makeConstraint2() {
		for(int i = 0; i < N; i++) {
			MPConstraint cstr = solver.makeConstraint(1, 1, "");
			
			for(int j = 0; j < M; j++) 
				cstr.setCoefficient(X[i][j], 1);
		}
	}
		
	//Constraint X[i][j]*c[i] <= y
	public static void makeConstraint3() {
		for(int j = 0; j < M; j++) {
			MPConstraint cstr = solver.makeConstraint(-inf, 0, "");
			
			for(int i = 0; i < N; i++)
				cstr.setCoefficient(X[i][j], credits[i]);
			cstr.setCoefficient(Y, -1);
		}
	}
	
	//Objective
	public static void makeObjective() {
		obj = solver.objective();
		obj.setCoefficient(Y, 1);
		obj.setMinimization();
	}
	
	//Solve Mixed Integer Programming
	public static void solveMIP() {
		MPSolver.ResultStatus rs = solver.solve();
		
		if (rs != MPSolver.ResultStatus.OPTIMAL) {
			System.out.println("Cann't find optimal solution");
		} 
		else {
			System.out.println("Optimal solution: " + obj.value());
			
			for(int i = 0; i < M; i++) {
				System.out.println("Giang vien " + i + " day lop:");
				for(int j = 0; j < N; j++)
					if (X[j][i].solutionValue() == 1)
						System.out.print(j + " ");
					System.out.println();
			}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readData();
		init();
		makeConstraint1();
		makeConstraint2();
		makeConstraint3();
		makeObjective();
		solveMIP();
	}

}
