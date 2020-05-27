package planningoptimization115657k62.KieuMinhHieu;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import java.util.ArrayList;


public class Liquid_Choco {
	int N = 20;
	int M = 5;
	int limit[] = {60, 70, 80, 90, 100};
	int V[] = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 
			20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	
	int[] oneM = {1,1,1,1,1};
	
	int[][] conf = { {0,1,-1},
					{7,8,-1},
					{12,17,-1},
					{8,9,-1},
					{1,2,9},
					{0,9,12}};
	
	
	
	public void solver() {
		Model model = new Model("Liquid");
		
		IntVar[][] x =new IntVar[M][N];
				
		for (int i = 0; i < M; i++) 
			for (int j = 0; j< N; j++) 
				x[i][j] = model.intVar("x[" + i + "," + j + "]", 0, 1);
		
		
		//Mỗi chất lỏng chỉ nằm ở 1 thùng
		for (int i = 0; i < M; i++) {
			model.scalar(x[i], V, "<=", limit[i]).post();	
		}
		
		//Tổng chất lỏng không vượt quá sức chứa của thùng
		for (int i = 0; i < N; i++) {
			IntVar[] y = new IntVar[M];
			for (int j = 0; j < M; j++)
				y[j] = x[j][i];
			model.scalar(y, oneM, "=", 1).post();
		}
		
		
		//Các chất lỏng cấm không nằm chung 1 thùng
		for (int k = 0; k < 6; k++) {
			ArrayList<Integer> c = new ArrayList<Integer>();
			for (int j = 0; j < 3; j++)
					if (conf[k][j] > -1) {
						c.add(conf[k][j]);
					}
			
			for (int i = 0; i < M; i++) {
				int size = c.size();
				IntVar[] y = new IntVar[size];
				int[] Y = new int[size];
							
				for (int u = 0; u < size; u++) {
					y[u] = x[i][c.get(u)];
					Y[u] = 1;
				}
				model.scalar(y, Y, "<", size).post();;
			}
		}
	

		model.getSolver().solve();
		
		for (int i = 0; i < M; i++) {
			System.out.println("Thung " + i + ":");
			for (int j = 0; j < N; j++)
				if (x[i][j].getValue() == 1) {
					System.out.println("Chat long " + j + " co the tich: " + V[j]);
				}
		}
			
	}
	public static void main(String[] args) {

		Liquid_Choco app = new Liquid_Choco();
		app.solver();
	}
	
}
