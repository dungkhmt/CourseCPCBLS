package cbls115676khmt61.NguyenVanSon_20163560.CP;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.*;
public class sportscheduling_schoco {
	
	static {
		System.loadLibrary("jniortools");
	}
	int N = 4;// number of teams 0,1,...N-1
	int[][] d = { { 0, 3, 2, 4 }, { 9, 0, 2, 3 }, { 1, 2, 0, 7 },
			{ 1, 1, 4, 0 } };
	int T = 6;// 2N-2
	
	Model model ;
	IntVar[][][] X = null;
	IntVar[][][] Y = null;
	IntVar[][][][] F = null;
	IntVar[] D = null ;
	
	Solver solver;
	
	int INF = 10000000;
	
	public void buildModel() {
		model = new Model("Sport Schelduling");
		X  = new IntVar[N][N][T+1];
		Y = new IntVar[]
		for (int i = 0 ; i< N; i++)
			for(int j = 0; j< N; j++)
				for(int t = 0; t<T+1;t++)
					X[i][j][t] = model.intVar("Xijt", 0,1);
					
		
					
	}
	private void printSol() {
		for (int t = 1; t <= T; t++) {1
			System.out.println("#" + t);
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++)
					if (i != j) {
						if (X[i][j][t] > 0)
							System.out.println("X(" + i + "," + j + "," + t
									+ ") -> " + i + " vs " + j + " san " + i);
						if(Y[i][j][t] > 0){
							System.out.println("Y(" + i + "," + j + "," + t
									+ ") -> " + i + " vs " + j + " san " + j);
						}
					}
			}
			System.out.println("-------------");
		}

		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
