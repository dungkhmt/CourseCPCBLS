package planningoptimization115657k62.NguyenVanLong;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class PhanBoMonHoc {
	public static void PhanMon() {
		int N = 9;// so mon hoc
		int P = 4;// so hoc ky
		int[] credits = {3,2,2,1,3,3,1,2,2};
		int alpha = 2, beta = 4, lamda = 3, gamma = 7;
		
		int[] I = {0,0,1,2,3,4,3};
		int[] J = {1,2,3,5,6,7,8};

		int[] oneN = {1,1,1,1,1,1,1,1,1};
		int[] oneP = {1,1,1,1};
		
		Model model = new Model("PhanBoMonHoc");
		IntVar[][] x = new IntVar[P][N];
		for(int i = 0; i<P; i++)
			for(int j = 0; j<N; j++) {
				x[i][j] = model.intVar("x[" + i + "][" + j + "]", 0, 1);
			}
		
		for(int j = 0; j<P; j++) {
			model.scalar(x[j], credits, "<=", gamma).post();
			model.scalar(x[j], credits, ">=", lamda).post();
			
			model.scalar(x[j], oneN, ">=", alpha).post();
			model.scalar(x[j], oneN, "<=", beta).post();
		}
		
		for(int i = 0; i<N; i++) {
			IntVar[] y = new IntVar[P];
			for(int k = 0; k<P; k++)y[k] = x[k][i];
			model.scalar(y, oneP, "=", 1).post();
			for(int k = 0; k< I.length; k++) {
				int e = I[k], f = J[k];
				for(int q = 0; q<P; q++)
					for(int p = 0; p<=q; p++)model.ifThen(model.arithm(x[q][e], "=", 1), model.arithm(x[p][f], "=", 0));
			}
			
		}
		
		model.getSolver().solve();

		for(int j = 0; j < P; j++){
		  System.out.print("semester " + j + ": ");
		  for(int i = 0; i < N; i++)if(x[j][i].getValue() == 1){
		    System.out.print("[course " + i + ", credit " + credits[i] + "] ");
		  System.out.println();
		}

		}
	}
	public static void main(String[] args) {
		PhanBoMonHoc.PhanMon();
	}
}
