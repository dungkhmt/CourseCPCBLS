package planningoptimization115657k62phammanhtuan;


import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Choco_Liquid {
	int N = 5;
	int M = 20;
	int K = 6;
	int c[] = {20,15,10,20,20,25,30,15,10,10,20,25,20,10,30,40,25,35,10,10};
	int d[] = {60,70,90,80,100};
	int q[][] = {
			{0,1},
			{7, 8},
			{12, 17},
			{8, 9},
			{1, 2, 9},
			{0, 9, 12}
	};
	int oneM[]= {1,1,1,1,1};
	Model model;
	IntVar X[][];
	public void builmodel () {
		model = new Model("Liquid");
		X = new IntVar[N][M];
		for (int i=0;i<N;i++) {
			for (int j=0;j<M;j++)
			X[i][j] = model.intVar("X["+i+"]"+"["+j+"]",0,1);
		}
	}
	public void solver () {
		int [][] cs = new int[K][M];
		for (int k=0;k<K;k++) {
			if  (q[k].length==2) {
				for (int l=0;l<M;l++) {
					if (l==q[k][0] || l==q[k][1]) {
						cs[k][l]=1;
					} else {
						cs[k][l]=0;
					}
				}
			} else {
				for (int l=0;l<M;l++) {
					if (l==q[k][0] || l==q[k][1] || l==q[k][2]) {
						cs[k][l]=1;
					} else {
						cs[k][l]=0;
					}
				}
			}
		}
		for(int i=0;i<N;i++) {
			for(int k=0;k<K;k++) {
				model.scalar(X[i], cs[k], "<=",1 ).post();
			}
		}
		for(int j=0;j<M;j++) {
			IntVar Y[]= new IntVar[N];
			for(int i=0;i<N;i++) {
				Y[i]=X[i][j];
			}
			model.scalar(Y, oneM, "=", 1).post();
		}
		for(int i=0;i<N;i++) {
			model.scalar(X[i], c, "<=", d[i]).post();
		}

		model.getSolver().solve();

		for(int i=0;i<N;i++) {
			System.out.print("Thung "+i+" chua: ");
			for(int j=0;j<M;j++) {
				if(X[i][j].getValue()==1) {
					System.out.print(j+" ");
				}
			}
			System.out.println();
		}
	}
	public static void main(String[] args) {
		Choco_Liquid app = new Choco_Liquid();
		app.builmodel();
		app.solver();
	}
}
