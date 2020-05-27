package planningoptimization115657k62.hoangthihao;

import java.util.Arrays;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class Liquid {
	public void solve() {
		int N = 5; // So thung 
		int M = 20; // So chat long
		int O = 6; // So rang buoc;
		int[] the_tich_thung = {60, 70, 90, 80, 100};
		int[] the_tich_chat_long = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10,
									20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
		
		int[][] conflicts = { {2, 0, 1, -1}, {2, 7, 8, -1},
							  {2, 12, 17, -1}, {2, 8, 9, -1},
							  {3, 1, 2, 9}, {3, 0, 9, 12}};
		int[] oneM = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		int[] oneN = {1,1,1,1,1};
		
		Model model = new Model("Liquid");
		// x[i][j] = 1: chat long j duoc chua trong thung i 
		IntVar[][] x = new IntVar[N][M];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < M; j++)
				x[i][j] = model.intVar("x[" + i + "," + j + "]", 0, 1);
		
		// Luong chat long chua trong thung khong vuot qua the tich thung
		for (int i = 0; i < N; i++) {
			model.scalar(x[i], the_tich_chat_long, "<=", the_tich_thung[i]).post();
		}
		
		// Moi chat long chi duoc chua trong mot thung
		for (int i = 0; i < M; i++) {
			IntVar[] y  = new IntVar[N];
			for (int j = 0; j < N; j++) {
				y[j] = x[j][i];
			}
			model.scalar(y, oneN, "=", 1).post();
		}
		
		// Cac chat long xuc tac nhau khong the chua trong cung mot thung
		for (int i = 0; i < O; i++) {
			int do_dai = conflicts[i][0];
			int[] rang_buoc= new int[do_dai];
			for (int j = 0; j < do_dai; j++)
				rang_buoc[j] = conflicts[i][j+1];
			int[] one = new int[do_dai];
			Arrays.fill(one, 1);
			for (int j = 0; j < N; j++) {
				IntVar[] z = new IntVar[do_dai];
				for (int k = 0; k < do_dai; k++)
					z[k] = x[j][rang_buoc[k]];
				model.scalar(z, one, "<=", 1).post();
			}
		}
			
			
		model.getSolver().solve();
		
		// In Ket qua   
		for (int i = 0; i < N; i++) {
			int load = 0;
			System.out.print("Thung " + i + ": " );
			for (int j = 0; j < M; j++)
				if (x[i][j].getValue() == 1) {
					System.out.print(j + " ");
					load += the_tich_chat_long[j];
				}
			System.out.println(", load = " + load);
		}
		
	} 
	public static void main(String[] args) {
		Liquid app = new Liquid();
		app.solve();
	}
}
