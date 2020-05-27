package planningoptimization115657k62.NguyenQuynhLoc;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Liquid {
	int n=20; // so luong chat long
	int m=5; // so luong thung hang
	int [] s= {20,15,10,20,20,25,30,15,10,10,
			20,25,20,10,30,40,25,35,10,10}; // the tich moi chat long
	int []p= {60,70,90,80,100}; // dung tich moi thung hang
	int n_constrain=6;
	int [] oneM= {1,1,1,1,1};
	int [][]constraint = {{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0},
						  {0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0},
						  {0,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
						  {1,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0}};
	public void solve() {
		Model solver = new Model("Liquid");
		IntVar [][] X = new IntVar[m][n]; // dung dich j duoc phan vao thung hang i
		// X[i][j]=0||1
		for(int i=0;i<m;i++) {
			for(int j=0;j<n;j++) {
				X[i][j]= solver.intVar("X["+i+"]["+j+"]",0,1);
			}
		}
		// moi dung dich chi duoc phan vao 1 thung hang
		for(int j=0;j<n;j++) {
			IntVar []y= new IntVar[m];
			for(int i=0;i<m;i++) {
				y[i]=X[i][j];
			}
			solver.scalar(y, oneM, "=", 1).post();
		}
		// moi thugn hang khong chua qua so luong cho phep
		for(int i=0;i<m;i++) {
			solver.scalar(X[i], s, "<=", p[i]).post();
		}
		// thoa man cac rang buoc
		for(int i=0;i<m;i++) {
			for(int k=0;k<n_constrain;k++) {
				solver.scalar(X[i], constraint[k], "<=",1 ).post();
			}
		}
		solver.getSolver().solve();
		for(int i=0;i<m;i++) {
			System.out.print("thung "+i+" chua: ");
			for(int j=0;j<n;j++) {
				if(X[i][j].getValue()==1) {
					System.out.print(j+" ");
				}
			}
			System.out.println();
		}
	}
	public static void main(String[] args) {
		Liquid app= new Liquid();
		app.solve();
	}
}
