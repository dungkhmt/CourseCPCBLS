package lol;
import java.awt.Point;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class PhanCongGiangDay {

	static {
		System.loadLibrary("jniortools");
	}
	int[][] Q;
	public void set(int i,int j) {
		Q[i][j]=Q[j][i]=1;
	}
	
	public void solveMIP() {
		double INF=Double.POSITIVE_INFINITY;
		MPSolver solver= new MPSolver("SimpleMIP",MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		int N=13,M=3;
		int c[]=  { 3, 3, 4, 3, 4, 3, 3, 3, 4, 3, 3, 4, 4 };	
		int[][] teachClass = { { 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0 }, { 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0 }, { 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1 }}; 
		Q=new int[N][N];
		set(0,2);set(0,4);set(0,8);set(1,4);set(1,10);set(3,7);set(3,9);set(5,11);set(5,12);set(6,8);set(6,12);
		MPVariable X[][]=new MPVariable[M][N];
		for(int i=0;i<M;i++)
			for(int j=0;j<N;j++) {
				X[i][j]=solver.makeIntVar(0, teachClass[i][j],  "X[" + i + "," + j + "]");
			}
		MPVariable Y=solver.makeIntVar(0, INF, "Y");
		
		for(int j=0;j<N;j++) {
			MPConstraint d = solver.makeConstraint(1,1);
			for(int i=0;i<M;i++) {
				d.setCoefficient(X[i][j], 1);
			}
		}
		for(int i=0;i<M;i++) {
			MPConstraint d = solver.makeConstraint(-INF,0);
			for(int j=0;j<N;j++) {
				d.setCoefficient(X[i][j], c[j]);
				d.setCoefficient(Y, -1);
			}
		}
		for(int i=0;i<M;i++) {
			for(int n=0;n<N;n++)
				for(int m=0;m<N;m++)
					if(Q[n][m]==1) {
						MPConstraint d= solver.makeConstraint(0,1);
						d.setCoefficient(X[i][m],1);
						d.setCoefficient(X[i][n],1);
					}
		}
		
		
		MPObjective obj=solver.objective();
		obj.setCoefficient(Y, 1);
		obj.setMinimization();
		
		MPSolver.ResultStatus rs=solver.solve();
		
		if(rs != MPSolver.ResultStatus.OPTIMAL) {
			System.out.println("no solution ");
		}
		else {
			System.out.println("so tin chi lon nhat nho nhat: "+ obj.value());
			for(int i=0;i<M;i++) {
				System.out.println("giao vien "+i+" day cac lop : ");
				int t=0;
				for(int j=0;j<N;j++) {
					if(X[i][j].solutionValue()==1) {
						t+=c[j];
						System.out.print(j+" ");
					}
				}
				System.out.println("tong so tin chi:"+t);
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new PhanCongGiangDay().solveMIP();
	}

}
