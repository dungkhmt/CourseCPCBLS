package planningoptimization115657k62.dinhbahai;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class Bai1_choco {
	public int[] D = {60, 70, 80, 90, 100};
	public int[] V = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	public int cam[][] = new int[20][20];
	public void solver() {
		cam[0][1] = 1; cam[1][0] = 1;
		cam[7][8] = 1; cam[8][7] = 1;
		cam[12][17] = 1; cam[17][12] = 1;
		cam[8][9] = 1; cam[9][8] = 1;
		cam[1][2] = 1; cam[2][1] = 1;
		cam[1][9] = 1; cam[9][1] = 1;
		cam[2][9] = 1; cam[9][2] = 1;
		cam[0][9] = 1; cam[9][0] = 1;
		cam[0][12] = 1; cam[12][0] = 1;
		cam[9][12] = 1; cam[12][9] = 1;
		
		int[] OneD = {1,1,1,1,1};
		//int[] OneV = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		Model m = new Model();
		IntVar[][] X = new IntVar[5][20];
		
		for(int i=0; i<5; i++)
			for(int j=0; j<20; j++)
				X[i][j] = m.intVar(0, 1);
		
		for(int i=0; i<5; i++) {
			m.scalar(X[i], V, "<=", D[i]).post();;
		}
		
		for(int j1 = 0; j1<19; j1++)
			for(int j2=j1+1; j2<20; j2++) {
				if(cam[j1][j2] == 1) {
					for(int i=0; i<5; i++) {
						m.scalar(new IntVar[] {X[i][j1], X[i][j2]}, new int[] {1,1}, "<=", 1).post();
					}
				}
			}
		for(int j=0; j<20; j++) {
			IntVar[] y = new IntVar[5];
			for(int k = 0; k<5; k++)
				y[k] = X[k][j];
				m.scalar(y, OneD, "=", 1).post();
		}
		
		
		Solver s = m.getSolver();
		s.solve();
		for(int i=0; i<5; i++) {
			System.out.print("Thung " + D[i] + " lit: ");
			for(int j=0; j<20; j++) {
				if(X[i][j].getValue() == 1) {
					System.out.print(j + " ");
				}
			}
			System.out.println();
		}
		
	}

	public static void main(String[] args) {
		Bai1_choco run = new Bai1_choco();
		run.solver();

	}

}
