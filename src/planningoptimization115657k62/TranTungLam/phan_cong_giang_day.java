package buoi3;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
	

public class phan_cong_giang_day {
	
	int M = 3;        // teachers
	int N = 13;          // classes
	
	// teacher i teaches class j
	int[][] teachClass = {
		{1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0},
		{1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0},
		{0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1}
	};

	// #credits of each class
	int[] credits = {3, 3, 4, 3, 4, 3, 3, 3, 4, 3, 3, 4, 4};
	
	// Conflict classes
	int[][] conflict = {
		{0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0},
		{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0},
		{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
		{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
		{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0}
	};
	
	public void solver() {
		Model model = new Model("PCGD");
		IntVar[][] x = new IntVar[M][N];    // x[i][j] = 1 => teacher i teaches class j
		for (int i = 0; i < M; i++)
			for (int j = 0; j < N; j++)
				x[i][j] = model.intVar("x[" + i + "," + j + "]", 0, 1);
		
		// one class can only be taught by 1 teacher: sum of values in one column is 1
		int[] oneC = new int[M];
		for (int i = 0; i < M; i++) oneC[i] = 1;
		for (int c = 0; c < N; c++) {
			IntVar[] tem = new IntVar[M];
			for (int i = 0; i < M; i++)
				tem[i] = x[i][c];
			model.scalar(tem, oneC, "=", 1).post();
		}
		
		//  
		for(int i = 0; i < M; i++)
			for (int j = 0; j < N; j++)
				if (teachClass[i][j] == 0)
					model.arithm(x[i][j], "=", 0).post();
		
		//
		int[] oneT = {1,1};
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				if (conflict[i][j] == 1) {
					for (int t = 0; t < M; t++) {
						IntVar[] tem = new IntVar[2];
						tem[0] = x[t][i];
						tem[1] = x[t][j];
						model.scalar(tem, oneT, "<=", 1).post();
					}
				}
		
		model.getSolver().solve();
		
		for (int i = 0; i < M; i++) {
			System.out.println("Teacher "+ i + ": ");
			for (int j = 0; j < N; j++) {
				if (x[i][j].getValue() == 1) {
					System.out.println(j + "-" + credits[j]);
				}
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		phan_cong_giang_day app = new phan_cong_giang_day();
		app.solver();
	}

}
