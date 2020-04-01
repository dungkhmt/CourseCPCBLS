package TU;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class BCA {
	int N = 9; // number of course
	int P = 4; // number of semester
	int[] credits = {3,2,2,1,3,3,1,2,2};
	int a = 2;
	int b = 4;
	int l = 3;
	int g = 7;
	int[] I = {0,0,1,2,3,4,3};
	int[] J = {1,2,3,5,6,7,8};
	
	int[] oneN = {1,1,1,1,1,1,1,1,1};
	int[] oneP = {1,1,1,1};
	
	public void solve(){
		Model model = new Model("TA");
		IntVar[][] x = new IntVar[P][N];
		
		for(int j = 0; j < P; j++){
			for(int i = 0; i < N; i++){
				x[j][i] = model.intVar("x[" + j + "],[" + i + "]", 0,1);
				
			}
		}
		for(int i = 0; i < N; i++){
			IntVar[] y = new IntVar[P];
			for(int j = 0; j < P; j++){
				y[j] = x[j][i];
			}
		}
		for(int k = 0; k < I.length; k++){
			int i = I[k]; 
			int j = J[k];
			for(int q = 0; q < P; q++){
				for(int p = 0; p <= q; p++){
					model.ifThen(model.arithm(x[q][i],"=",1),model.arithm(x[p][i], "=", 0));
					
				}
			}
		}
		model.getSolver().solve();
		for(int j = 0; j < P; j++){
			System.out.println("[cours " + j + ", credit" + credits[j] + "]");
		}
		System.out.println();
	}
	public static void main(String[] args) {
		BCA ba = new BCA();
		ba.solve();
	}
}
