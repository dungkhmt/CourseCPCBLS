package planningoptimization115657k62.phamvietbang.project;


import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class liqid {
	int N=20;
	int M=5;
	int v[]= {20,15,10,20,20,25,30,15,10,10,
			20,25,20,10,30,40,25,35,10,10};
	int V[]= {60,70,90,80,100};
	int check[][] = {{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0},
						  {0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0},
						  {0,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
						  {1,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0}};
	
	public void solve() {
		Model model = new Model();
		IntVar x[][] = new IntVar[M][N]; 
		for(int i=0;i<M;i++) {
			for(int j=0;j<N;j++) {
				x[i][j]= model.intVar("x["+i+","+j+"]",0,1);
			}
		}
		for(int j=0;j<N;j++) {
			IntVar y[]= new IntVar[M];
			int one[]=new int[M];
			for(int i=0;i<M;i++) {
				y[i]=x[i][j];
				one[i]=1;
			}
			model.scalar(y, one, "=", 1).post();
		}
		for(int i=0;i<M;i++) {
			model.scalar(x[i], v, "<=", V[i]).post();
		}
		for(int i=0;i<M;i++) {
			for(int j=0;j<check.length;j++) {
				model.scalar(x[i], check[j], "<=",1 ).post();
			}
		}
		model.getSolver().solve();
		for(int i=0;i<M;i++) {
			System.out.print("Thung "+V[i]+" lit chua cac chat long: ");
			for(int j=0;j<N;j++) {
				if(x[i][j].getValue()==1) {
					System.out.print(j+", ");
				}
			}
			System.out.println();
		}
	}
	public static void main(String[] args) {
		liqid app= new liqid();
		app.solve();
	}
}
