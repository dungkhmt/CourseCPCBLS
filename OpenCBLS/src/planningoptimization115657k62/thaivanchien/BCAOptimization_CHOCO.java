package chocoex;

import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class BCAOptimization_CHOCO {

	public int N, M, Q, totalCredits;
	public IntVar[][] X;
	Model model;
	public int[][] teachClass;
	public int[] credits, qu, qv;
	IntVar y;
	
	public void readData() {
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
	
	public void init() {
		readData();
		model = new Model("BCA");
		X = new IntVar[N][M];
		for(int i = 0; i < N; i++)
			for(int j = 0; j < M; j++)
				X[i][j] = model.intVar("", 0, teachClass[j][i]);
		
		for(int i = 0; i < M; i++)
			for(int j = 0; j < Q; j++) {
				model.arithm(X[qu[j]][i], "+", X[qv[j]][i], ">=", 0).post();
				model.arithm(X[qu[j]][i], "+", X[qv[j]][i], "<=", 1).post();
			}
		
		int[] ones = new int[M];
		for(int i = 0; i < M; i++)
			ones[i] = 1;
		
		for(int i = 0; i < N; i++)
			model.scalar(X[i], ones, "=", 1).post();
		
		y = model.intVar("", 0, totalCredits);
		for(int i = 0; i < M; i++) {
			IntVar[] tempArr = new IntVar[N];
			for(int j = 0; j < N; j++)
				tempArr[j] = X[j][i];
			model.scalar(tempArr, credits, "<=", y).post();
		}
			
			
		model.setObjective(Model.MINIMIZE, y);

		Solver solver = model.getSolver();
		if (solver.solve()) {
			System.out.println(y.getValue());
			for(int i = 0; i < M; i++) {
				System.out.println("Giang vien " + i + ": ");
				for(int j = 0; j < N; j++)
					if (X[j][i].getValue() == 1)
						System.out.print(j + "   ");
				System.out.println();
			}
		}
				
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BCAOptimization_CHOCO app = new BCAOptimization_CHOCO();
		app.init();
	}

}
