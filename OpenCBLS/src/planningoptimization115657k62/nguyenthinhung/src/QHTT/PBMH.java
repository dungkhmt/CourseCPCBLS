import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class PBMH {
	
	public static void main(String[] agrs) {
		int N = 9;
		int P = 4;
		int[] credits = {3, 2, 2, 1, 3, 3, 1, 2, 2};
		int anpha = 2;
		int beta = 4;
		int lamda = 3;
		int gamma = 7;
		int[] I = {0,0,1,2,3,4,3};
		int[] J = {1,2,3,5,6,7,8};
		int[] oneN = {1,1,1,1,1,1,1,1,1};
		int[] oneP = {1,1,1,1};
		Model model = new Model("BACP");
		IntVar[][] x =  new IntVar[P][N];
		
		for(int i = 0;i < P;i++) {
			for(int j = 0;j < N;j ++) {
				x[i][j] = model.intVar("x[" + i + "," + j + "]", 0,1);
			}
		}
		
		for(int i = 0; i < P;i++) {
			model.scalar(x[i], credits, ">=", lamda).post();
			model.scalar(x[i], credits, "<=", gamma).post();
			model.scalar(x[i], oneN, ">=", anpha).post();
			model.scalar(x[i], oneN, "<=", beta).post();
		}
		
		for(int i = 0;i < N;i++) {
			IntVar[] y = new IntVar[P];
			for(int j = 0; j < P ; j++) y[j] = x[j][i];
			model.scalar(y, oneP, "=", 1).post();
		}
		
		for(int i = 0;i < I.length;i++) {
			int j = I[i];
			int k = J[i];
			for(int p = 0;p < P;p++) {
				for(int q = 0;q <= p;q++) {
					model.ifThen(model.arithm(x[p][j], "=", 1), model.arithm(x[q][k], "=", 0));

				}
			}
		}
		
		model.getSolver().solve();
		
		for(int i = 0;i < P;i++) {
			System.out.print("\nsemeter " + i + ":");
			System.out.println();
			for(int j = 0;j < N;j++) {
				if(x[i][j].getValue() == 1) {
					System.out.print("[course " + i + ", credit " + credits[i] + "] ");
					System.out.println();
				}
				
			}
		}
	}
	
}
