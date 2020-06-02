package test;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Liquid {
	int n = 5; // so thung
	int k = 20; // so loai chat long
	int[] p = {60, 70, 80, 90, 100};
	
	Model model = new Model("Liquid");
	IntVar[][] x;
	IntVar[] y;
	int[][] c = {{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0},
				{0,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
				{1,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0}};
	
	int[] t = {20,15,10,20,20,25,30,15,10,10,
			20,25,20,10,30,40,25,35,10,10};// the tich moi chat long
	int[] oneN = {1,1,1,1,1};
	
	public void liquid() {
		x = new IntVar[n][k];
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < k; j++) {
				x[i][j] = model.intVar("x["+i+"]["+j+"]",0,1);
			}
		}
		
		for(int i=0; i<k; i++) {
			y = new IntVar[n];
			for(int j=0; j<n; j++) {
				y[j] = x[j][i];
			}
			model.scalar(y, oneN, "=",1).post();
		}
		
		for(int i=0; i < n; i++) {
			model.scalar(x[i], t ,"<=", p[i]).post();
			for(int m=0; m<6; m++) {
				model.scalar(x[i], c[m], "<=", 1).post();
			}
		}
		
		model.getSolver().solve();
		
		for(int i=0; i<n; i++) {
			System.out.print("Thung "+i+":");
			for(int j=0; j<k; j++) {
				if(x[i][j].getValue() ==1) {
					System.out.print("chat long " + j +" the tich " + t[j]);
					System.out.println();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Liquid app = new Liquid();
		app.liquid();
	}
}
