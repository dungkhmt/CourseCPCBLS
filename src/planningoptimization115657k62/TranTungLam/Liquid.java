package giua_ky;
	
import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Liquid {

	int n = 5, m = 20;
	int con[] = {60, 70, 90, 80, 100};
	int liq[] = {20,15,10,20,20,25,30,15,10,10,
			     20,25,20,10,30,40,25,35,10,10
				};
	int conflict[][] = {{0,1,-1}, {7,8,-1}, {12,17,-1},
						{8,9,-1}, {1,2,9},
						{0,9,12}
						};
	
	public void solve() {
		Model model = new Model("Liquid");
		
		IntVar[][] x = new IntVar[m][n];
		
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++){
				x[i][j] = model.intVar(0,1);
			}
			
		// Cac chat long khac nhau
		for (int i = 0; i < 6; i++) {
			ArrayList<Integer> c = new ArrayList<Integer>(); // c chua nhung chat long khong o duoc voi nhau cua 1 rang buoc
			
			for (int j = 0; j < 3; j++) {
				if (conflict[i][j] != -1) {
					c.add(conflict[i][j]);
				}
			}
			
	//		System.out.println("size c: " + c.size());
			
			for (int t = 0; t < n; t++) {
				int s = c.size();
				IntVar[] y = new IntVar[s];
				int [] tem = new int[s];
				for (int idx = 0; idx < s; idx++) {
					y[idx] = x[c.get(idx)][t];
					tem[idx] = 1;
				}
				
				if (s == 3)
					model.scalar(y, tem, "<=", 2).post();
				if (s == 2)
					model.scalar(y, tem, "<=", 1).post();
			}
			
		}
		// C1: tong the tich 1 thung khong vuot qua nguong
		
		for (int i1 = 0; i1 < n; i1++) {
			IntVar[] y = new IntVar[m];
			int []tem = new int[m];
			for (int a = 0; a < m; a++) tem[a] = liq[a];
			for (int j = 0; j < m; j++) {
				y[j] = x[j][i1];
			}
			model.scalar(y, tem, "<=", con[i1]).post();
		}
		
		// C2: moi chat long chi thuoc 1 thung
		for (int i = 0; i < m; i++) {
			IntVar[] y = new IntVar[n];
			int[] tem = new int[n];
			for (int j = 0; j < n; j++)  {
				y[j] = x[i][j];
				tem[j] = 1;
			}
				
			model.scalar(y, tem, "=", 1).post();
		}
		
		model.getSolver().solve();
		{
			for (int i = 0; i < n; i++) {
				System.out.println("Thung " + (i) + " :");
				for (int j = 0; j < m; j++) {
					if (x[j][i].getValue() == 1) {
						System.out.println(j);
						
					}
				}
			}
		}
		
}
		
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Liquid app = new Liquid();
		app.solve();
	}

}
