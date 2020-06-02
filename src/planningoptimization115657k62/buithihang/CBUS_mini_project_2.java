package test;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import java.util.Random;

public class CBUS_mini_project_2 {

	int N,K;
	int INF=Integer.MAX_VALUE;
	int[][]d = {{0, 5, 9, 8, 2, 3, 8, 3, 1 },
			{6, 0 ,2, 6, 9, 9, 7, 8, 4 },
			{9, 9, 0, 2 ,4, 3, 9, 5, 9 },
			{6, 2 ,4, 0, 9, 9, 7, 9, 2}, 
			{9, 3 ,5, 5 ,0, 3 ,7, 8, 4 },
			{4, 6 ,8 ,4, 6 ,0 ,5, 9, 9 },
			{9, 6 ,3, 5, 4, 6,0 ,9 ,9}, 
			{7, 8 ,1 ,4 ,1, 8, 5, 0, 6 },
			{8, 6 ,2 ,9, 3 ,2 ,6, 9, 0 }
				};
	int[]q = {4,2,2};
	MPVariable X[][];
	MPVariable Y[];
	MPVariable Z[][];
	Random r=new Random();
	public CBUS_mini_project_2(int N,int K) {
		this.N=N;
		this.K=K;
//		d=new int[2*N+1][2*N+1];
//		q=new int[K+1];
//		data_generator();
	}
	
	public void data_generator() {
		while(true) {
			int check_sum=0;
			for(int i=1;i<=K;i++) {
				q[i]=r.nextInt(N+1);
				check_sum+=q[i];
			}
			if(check_sum>=N)break;
		}
		for(int i=1;i<=K;i++) {
			System.out.print(q[i]+" ");
		}System.out.println();
		for(int i=0;i<=2*N;i++) {
			for(int j=0;j<=2*N;j++) {
				d[i][j]=r.nextInt(9)+1;
				if(i==j)d[i][j]=0;
				System.out.print(d[i][j]+" ");
			}System.out.println();
		}System.out.println();
	}
	
	static {
		System.loadLibrary("jniortools");
	}
	
	public void solve() {
		MPSolver solver= new MPSolver("CBUS",MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		//X[i][j] la  diem j la diem tiep theo cua i
		X=new MPVariable[2*N+K+1][2*N+2*K+1];	
		//Y[i] la khoang cach tich luy tu diem xuat phat cua lo trinh den diem i
		Y=new MPVariable[2*N+2*K+1];
		//Z[i][j]=1 neu diem i thuoc lo trinh j
		Z=new MPVariable[2*N+2*K+1][K+1];
		for(int i=1;i<=2*N;i++) {
			Y[i]=solver.makeIntVar(0, INF,  "Y[" + i + "]");
			for(int j=1;j<=K;j++) 
				Z[i][j]=solver.makeIntVar(0, 1,  "Z[" + i + "," + j + "]");
		}
		for(int i=1;i<=K;i++) {
			int I=i+2*N;
			int Ik=i+2*N+K;
			Y[I]=solver.makeIntVar(0, 0,  "Y[" + I + "]");
			Y[Ik]=solver.makeIntVar(0, INF,  "Y[" + Ik + "]");
			for(int j=1;j<=K;j++) {
				if(i==j) Z[I][j]=solver.makeIntVar(1, 1,  "Z[" + I + "," + j + "]");
				else Z[I][j]=solver.makeIntVar(0, 1,  "Z[" + I + "," + j + "]");
				if(i==j) Z[Ik][j]=solver.makeIntVar(1, 1,  "Z[" + Ik + "," + j + "]");
				else Z[Ik][j]=solver.makeIntVar(0, 1,  "Z[" + Ik + "," + j + "]");
			}
		}
		
		//moi diem chi thuoc 1 lo trinh
		for(int i=1;i<=2*N+K;i++) {
			MPConstraint e = solver.makeConstraint(1,1);
			for(int j=1;j<=2*N;j++) {
				X[i][j]=solver.makeIntVar(0, 1,  "X[" + i + "," + j + "]");
				e.setCoefficient(X[i][j], 1);
			}
			for(int j=2*N+1+K;j<=2*N+2*K;j++ ) {
				X[i][j]=solver.makeIntVar(0, 1,  "X[" + i + "," + j + "]");
				e.setCoefficient(X[i][j], 1);
			}
		}
		for(int j=1;j<=2*N;j++) {
			MPConstraint e = solver.makeConstraint(1,1);
			for(int i=1;i<=2*N+K;i++) {
				e.setCoefficient(X[i][j], 1);
			}
		}
		for(int j=2*N+1+K;j<=2*N+2*K;j++ ) {
			MPConstraint e = solver.makeConstraint(1,1);
			for(int i=1;i<=2*N+K;i++) {
				e.setCoefficient(X[i][j], 1);
			}
		}
		
		//j la diem sau cua i thi Y[j]=Y[i]+d[i][j]
		for(int i=1;i<=2*N+K;i++) {
			int I=i;
			if(i>2*N)I=0;
			for(int j=1;j<=2*N;j++) {
				MPConstraint e = solver.makeConstraint(-INF,1e6+d[I][j]);
				MPConstraint f = solver.makeConstraint(-1e6+d[I][j],INF);
				e.setCoefficient(X[i][j], 1e6);
				e.setCoefficient(Y[j], 1);
				e.setCoefficient(Y[i], -1);
				f.setCoefficient(X[i][j], -1e6);
				f.setCoefficient(Y[j], 1);
				f.setCoefficient(Y[i], -1);
				for(int m=1;m<=K;m++) {
					MPConstraint g = solver.makeConstraint(-INF,1e6);
					MPConstraint h = solver.makeConstraint(-1e6,INF);
					g.setCoefficient(X[i][j], 1e6);
					g.setCoefficient(Z[i][m], 1);
					g.setCoefficient(Z[j][m], -1);
					h.setCoefficient(X[i][j], -1e6);
					h.setCoefficient(Z[i][m], 1);
					h.setCoefficient(Z[j][m], -1);
				}
			}
			for(int j=2*N+1+K;j<=2*N+2*K;j++ ) {
				MPConstraint e = solver.makeConstraint(-INF,1e6+d[I][0]);
				MPConstraint f = solver.makeConstraint(-1e6+d[I][0],INF);
				e.setCoefficient(X[i][j], 1e6);
				e.setCoefficient(Y[j], 1);
				e.setCoefficient(Y[i], -1);
				f.setCoefficient(X[i][j], -1e6);
				f.setCoefficient(Y[j], 1);
				f.setCoefficient(Y[i], -1);
				for(int m=1;m<=K;m++) {
					MPConstraint g = solver.makeConstraint(-INF,1e6);
					MPConstraint h = solver.makeConstraint(-1e6,INF);
					g.setCoefficient(X[i][j], 1e6);
					g.setCoefficient(Z[i][m], 1);
					g.setCoefficient(Z[j][m], -1);
					h.setCoefficient(X[i][j], -1e6);
					h.setCoefficient(Z[i][m], 1);
					h.setCoefficient(Z[j][m], -1);
				}
			}
		}
		
		
		//diem i phai tham truoc i+N
		for(int i=1;i<=N;i++) {
			MPConstraint e = solver.makeConstraint(-INF,0);
			e.setCoefficient(Y[i], 1);
			e.setCoefficient(Y[i+N], -1);
			MPConstraint f = solver.makeConstraint(0,0);
		}
		
		for(int i=1;i<=2*N+2*K;i++) {
			MPConstraint e = solver.makeConstraint(1,1);
			for(int m=1;m<=K;m++) {
				e.setCoefficient(Z[i][m], 1);
			}
		}
		
		MPObjective obj=solver.objective();// ham muc tieu la tong khoang cach tich luy tai cac diem ket thuc
		for(int m=1;m<=K;m++) {
			obj.setCoefficient(Y[2*N+K+m], 1);
			MPConstraint e = solver.makeConstraint(-INF,q[m]); //diem i va i+N phai thuoc 1 lo trinh
			for(int i=1;i<=N;i++) {
				e.setCoefficient(Z[i][m], 1);
				MPConstraint f = solver.makeConstraint(0,0);
				f.setCoefficient(Z[i][m], 1);
				f.setCoefficient(Z[i+N][m], -1);
			}
		}
		
		obj.setMinimization();
		MPSolver.ResultStatus rs=solver.solve();
		
		if(rs != MPSolver.ResultStatus.OPTIMAL) {
			System.out.println("no solution ");
		}
		else {
			System.out.println("solution "+obj.value());
			System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
		}
	}
	
	void duyet(int i) {
		for(int j=1;j<=2*N;j++) {
			if(X[i][j].solutionValue()==1) {
				System.out.print("->"+j);
				duyet(j);
			}
		}
	}
	
	public void printSolution() {
		for(int i=1;i<=K;i++){
			System.out.print("route "+i+": "+0);
			duyet(i+2*N);
			System.out.print("->"+0+"\n");
			System.out.println("travel distance: "+Y[2*N+K+i].solutionValue());
		}
		/*for(int i=1;i<=2*N+K;i++) {
			for(int j=1;j<=2*N;j++) {
				if(X[i][j].solutionValue()==1)
					System.out.println("X[" + i + "," + j + "]="+X[i][j].solutionValue());
			}
			for(int j=2*N+1+K;j<=2*N+2*K;j++ ) {
				if(X[i][j].solutionValue()==1)
					System.out.println("X[" + i + "," + j + "]="+X[i][j].solutionValue());
			}
		}
		for(int i=1;i<=2*N;i++) {
			System.out.println("Y[" + i + "]="+Y[i].solutionValue());
		}
		for(int i=2*N+1;i<=2*N+K;i++) {
			System.out.println("Y[" + i + "]="+Y[i].solutionValue());
		}
		for(int i=2*N+K+1;i<=2*N+2*K;i++) {
			System.out.println("Y[" + i + "]="+Y[i].solutionValue());
		}
		for(int i=1;i<=2*N+2*K;i++) {
			for(int j=1;j<=K;j++) {
				if(Z[i][j].solutionValue()==1)System.out.println("Z[" + i + "," + j + "]="+Z[i][j].solutionValue());
			}
		}*/
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CBUS_mini_project_2 x=new CBUS_mini_project_2(4,2);
		x.solve();
		x.printSolution();	
	}
}

