package ortool;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;

public class Project14 {
	static {
		System.loadLibrary("jniortools");
	}
	int N = 3;// number of customer 1,...N
	int[][] d; // d[i][j] khoang cach tu diem i den diem j;
	int[][] t; // t[i][j] thoi gian di chuyen tu i den j;
	int[] e; // e[i] thoi gian khach i bat dau co the nhan hang;
	int[] l; // l[i] thoi gian cuoi cung co the nhan hang;
	int[] k;
	public int t0; //thoi gian bat dau giao hang
	MPVariable[][] r = null;// new MPVariable[N+1][N+1]; 
	MPVariable[] X = null; // new MPVariable[N+1];
	MPSolver solver;

	int INF = 10000000;

	public void buildModel() {
		solver = new MPSolver("Project14",
				MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		r = new MPVariable[N+1][N+1]; //r[i][j] = 1 -> di tu i den j
		X = new MPVariable[N+1]; // X[i] -> thoi gian nhan hang cua khach i

		for (int i = 0; i <= N; i++)
			for (int j = 0; j <= N; j++) 
				if (i != j)
					r[i][j] = solver.makeIntVar(0, 1, "r[" + i + "," + j + "]");
		X[0] = solver.makeIntVar(t0, t0, "X[0]");
		for (int i = 1; i <= N; i++)
			X[i] = solver.makeIntVar(e[i], l[i], "X[" + i + "]");
		
		// minimize 
		MPObjective obj = solver.objective();
		for (int i = 0; i <= N; i++)
			for (int j = 0; j <= N; j++)
				if (i != j)
					obj.setCoefficient(r[i][j],d[i][j]);
		obj.setMinimization();
		
		// make constraint
		// moi khach chi den 1 lan
		for (int i = 0; i <= N; i++) {
			MPConstraint c0 = solver.makeConstraint(1,1);
			for (int j = 0; j <= N; j++)
				if (i != j)
					c0.setCoefficient(r[i][j], 1);
		}
		
		for (int j = 0; j <= N; j++) {
			MPConstraint c1 = solver.makeConstraint(1,1);
			for (int i = 0; i <= N; i++)
				if (i != j)
					c1.setCoefficient(r[i][j], 1);
		}
		
		// rang buoc thoi gian giao hang
		for (int i = 0; i <= N; i++)
			for (int j = 1; j <= N; j++)
				if (i != j) {
					MPConstraint c2 = solver.makeConstraint(-INF,20000 - t[i][j] - k[i]);
					c2.setCoefficient(X[i], 1);
					c2.setCoefficient(X[j], -1);
					c2.setCoefficient(r[i][j], 20000);
				}
		
		// khong tao chu trinh con
		for (int k = 0; k <= N; k++) {
			MPConstraint c3 = solver.makeConstraint(0,N);
			for (int i = 0; i <= N; i++)
				if (i != k)
					for (int j = i + 1; j <= N; j++)
						if (j != k) c3.setCoefficient(r[i][j], 1);
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
		System.out.println("Thu tu giao hang cho khach:");
		int i = 0;
		System.out.print("0 --> ");
		for (int k = 0; k < N; k++) {
			for (int j = 0; j <= N; j++)
				if (i != j)
					if (r[i][j].solutionValue() > 0) {
						System.out.print(j + " --> ");
						i = j; break;
					}
		}
		System.out.println("0");
		System.out.println("-------------");
		
		System.out.println("Moc thoi gian:");
		i = 0;
		System.out.print(t0 + "--->");
		for (int k = 0; k < N; k++) {
			for (int j = 0; j <= N; j++)
				if (i != j)
					if (r[i][j].solutionValue() > 0) {
						System.out.print(X[j].solutionValue() + " --> ");
						i = j; break;
					}
		}
		System.out.println(X[0].solutionValue());
		System.out.println("-------------");		
	}
	
	public void initData(String src){
		File f = new File(src);
		Scanner input = null;
		try{
			input = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		N = input.nextInt();
		t0 = input.nextInt();
		e = new int[N+1];
		for(int i=1; i <= N; i++)
			e[i] = input.nextInt();
		
		l = new int[N+1];
		for(int i=1; i <= N; i++)
			l[i] = input.nextInt();
		
		k = new int[N+1];
		for(int i=1; i <= N; i++)
			k[i] = input.nextInt();
		l[0] = l[N] + k[N];
		
		d = new int[N+1][N+1];
		for(int i=0; i<= N; i++)
			for(int j=0; j<= N; j++)
				d[i][j] = input.nextInt();
		
		t = new int[N+1][N+1];
		for(int i=0; i<= N; i++)
			for(int j=0; j<= N; j++)
				t[i][j] = input.nextInt();
		//System.out.println(t[3][3]);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Project14 app = new Project14();
		long millis1 = System.currentTimeMillis();
		app.initData("./data/input4.txt");
		app.buildModel();
		long millis2 = System.currentTimeMillis();
		System.out.println(millis2 - millis1);

	}
}