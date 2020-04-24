package planningoptimization115657k62.hoangthanhlam;

import com.google.ortools.linearsolver.*;

public class MiniProject {
	
	static {
		System.loadLibrary("jniortools");
	}
	
	public int N = 10;
	public int K = 3;
	
	public int[] d = {0, 2, 5, 1, 3, 4, 6, 2, 4, 2, 3};
	public int[][] t = {
			{6, 7, 8, 5, 6, 4, 8, 9, 5, 8},
			{6, 7, 8, 5, 6, 4, 8, 9, 5, 8},
			{6, 7, 8, 5, 6, 4, 8, 9, 5, 8},
			{6, 7, 8, 5, 6, 4, 8, 9, 5, 8},
			{6, 7, 8, 5, 6, 4, 8, 9, 5, 8},
			{6, 7, 8, 5, 6, 4, 8, 9, 5, 8},
			{6, 7, 8, 5, 6, 4, 8, 9, 5, 8},
			{6, 7, 8, 5, 6, 4, 8, 9, 5, 8},
			{6, 7, 8, 5, 6, 4, 8, 9, 5, 8},
			{6, 7, 8, 5, 6, 4, 8, 9, 5, 8}
	};
	
	public MPSolver solver;
	public MPVariable x[][];
	public MPVariable T[];
	public MPVariable y;
	
	public void createModel() {
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
