import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class phanconggiangday {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int N=13;// số lớp
		int M=3;// số giáo viên
		int[] credits = { 3, 3, 4, 3, 4, 3, 3, 3, 4, 3, 3, 4, 4 };
		
		int[] oneN = {1,1,1,1,1,1,1,1,1,1,1,1,1};
		int[] oneM = {1,1,1};
		
		int T[][]= {
				{1,0,1,1,1,0,0,0,1,0,1,0,0},
				{1,1,0,1,0,1,1,1,1,0,0,0,0},
				{0,1,1,1,0,0,0,1,0,1,0,1,1}
		};// danh sachs giao vien co the day lop
		int Q[][]= {{0,2},{0,4},{0,8},{1,4},{1,10},
				{3,7},{3,9},{5,11},{5,12},{6,8},{6,12}};
		
		Model model=new Model("PCGD");
		IntVar[][] X=new IntVar[M][N];
		
		for(int i=0;i<M;i++) {
			for(int j=0;j<N;j++) {
				if(T[i][j]==0) {
					X[i][j]=model.intVar("X["+i+","+j+"]", 0);
					continue;
				}
				X[i][j]=model.intVar("X["+i+","+j+"]", 0, 1);
			}
		}

		for(int i=0;i<N;i++) {
			IntVar[] y= new IntVar[M];
			for(int j=0;j<M;j++) {
				y[j]=X[j][i];
			}
			model.scalar(y, oneM, "=", 1).post();
		}
		for( int i=0;i<Q[1].length;i++) {
			for(int j=0;j<M;j++) {
				model.arithm(X[j][Q[i][0]], "+", X[j][Q[i][1]], "<=", 1).post();
			}
		}
		for(int i=0;i<M;i++) {
			model.scalar(X[i], credits, "<=", 15).post();
		}
		model.getSolver().solve();
		
		for(int i=0;i<M;i++) {
			System.out.print("Giao vien "+i+": ");
			for(int j=0;j<N;j++) {
		
		if(X[i][j].getValue()==1) {
					System.out.print(" "+j+" ");
				}
			}
			System.out.println();
		}

		
		
	}

}
