package toiuu2;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.*;

public class Liquid_Choco {
	int N=20;
	int M=5;
	int [] n= { 20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10}; 
	int [] m= { 60, 70, 80, 90, 100};
	int [][]conflict =   {{ 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
						  { 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
						  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0},
						  { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
						  { 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
						  { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}};
	
	Model model ;
	IntVar [][] X;
	
	public void buildModel() {
		model = new Model(" Liquid ");
		X = new IntVar[M][N];
		
		for(int i= 0;i<M;i++) {
			for(int j= 0;j<N;j++) {
				X[i][j]= model.intVar("X["+i+"]["+j+"]", 0, 1);
			}
		}
		
		for(int i= 0;i<M;i++) {
			model.scalar(X[i], n, "<=", m[i]).post();
		}
		
		for(int i= 0;i<N;i++) {
			IntVar[] y= new IntVar[M];
			for(int j= 0;j<M;j++) {
				y[j]=X[j][i];
			}
			model.scalar(y, new int[]{1,1,1,1,1} , "=", 1 ).post();
		}
		
		for(int i= 0;i<M;i++) {
			for(int j= 0; j<6 ;j++) {
				model.scalar(X[i], conflict[j], "<=", 1 ).post();
			}
		}		
	}
	
	public void solve() {
		Solver s= model.getSolver();
		
		if(s.solve()) {
			for(int i=0;i<M;i++) {
				System.out.print("Thung thu "+ i +" chua: ");
				for(int j=0;j<N;j++) {
					if(X[i][j].getValue()==1) {
						System.out.print(j+"     ");
					}
				}
				System.out.println();
			}
		}
		System.out.println("------------------------------------");
	}
	public static void main(String[] args) {
		Liquid_Choco app= new Liquid_Choco();
		app.buildModel();
		app.solve();
	}
}