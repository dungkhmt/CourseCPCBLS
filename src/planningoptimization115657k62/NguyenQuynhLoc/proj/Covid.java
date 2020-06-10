package planningoptimization115657k62.NguyenQuynhLoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;


public class Covid {
	static {
		System.loadLibrary("jniortools");
	}
	int N; // so doan khach
	int M; // so khu cach ly
	int [] s; // so luong nguoi trong moi doan khach
	int [] c; // so luong nguoi ma khu cach ly chua toi da
	public void ReadFile(String path_) {
 		// doc du lieu tu file
 		// Input:
 		//Dong dau chua 2 so nguyen N, M
 		//Dong thu 2 chua N so nguyen la so luong nguoi s cua moi doan khach
 		// Dong thu 3 chua M so nguyen la gioi han c nguoi cua khu cach ly
 		// Ham doc file doc tu 1.
 		Scanner sc;
		try {
			sc = new Scanner(new File(path_));
			 N = sc.nextInt();
	
		     M = sc. nextInt();
		     s=new int[N];
		     c=new int[M];
		     //System.out.println(N);
		     //System.out.println(M);
		     
		       for(int i = 0; i < N; i++) {
		    	   s[i] = sc.nextInt();
		    	   //System.out.println(i+" : " +s[i]);
		       }
		       for(int i = 0; i < M; i++) {
		    	   c[i] = sc.nextInt();
		    	   //System.out.println(i+" : " +c[i]);
		       }	 
		       System.out.println("Reading Complete!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void solve() {
		ReadFile("/home/lnq/Desktop/20192/tulkh/dataproject/10v5.txt");
		MPSolver solver = new MPSolver("Covid",MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		MPVariable [][] X = new MPVariable[M][N];
		int dis_max=N+M;
		// X[i][j]=1 neu doan khach j duoc dua ve khu i
		// X[i][j]=0 neu doan khach j khong duoc dua ve khu i
		for( int i=0;i<M;i++) {
			for(int j=0;j<N;j++) {
				X[i][j]=solver.makeIntVar(0, 1, "X["+i+"]["+j+"]");
			}
		}
		
		// moi doan khach chi duoc dua ve 1 noi cach ly
		for( int j=0;j<N;j++) {
			MPConstraint c= solver.makeConstraint(1,1);
			for(int i=0;i<M;i++) {
				c.setCoefficient(X[i][j], 1);
			}
		}
		
		// so luong khach o khu i phai nho hon c[i]
		for(int i=0;i<M;i++) {
			int x=c[i];
			MPConstraint c= solver.makeConstraint(0,x);
			for(int j=0;j<N;j++) {
				c.setCoefficient(X[i][j], s[j]);
			}
		}
		
		// Ham muc tieu
		MPVariable y= solver.makeIntVar(0, dis_max, "y");
		for(int i=0;i<M;i++) {
			for(int j=0;j<N;j++) {
				MPConstraint c= solver.makeConstraint(0,dis_max);
				c.setCoefficient(X[i][j], j-i-N);
				c.setCoefficient(y, 1);
			}
		}
		MPObjective obj= solver.objective();
		obj.setCoefficient(y, 1);
		obj.minimization();
		ResultStatus rs= solver.solve();
		if(rs!=ResultStatus.OPTIMAL) {
			System.out.println("no solution");
		}else {
			System.out.println("khoang cach max= "+ obj.value());
			for(int i=0;i<M;i++) {
				System.out.print("cac nhom dua ve khu "+(i+1)+": ");
				for(int j=0;j<N;j++) {
					if(X[i][j].solutionValue()==1) {
						System.out.print((j+1)+" ");
					}
				}
				System.out.println();
			}
		}
	}
	public static void main(String[] args) {
		Covid app= new Covid();
		long t=System.currentTimeMillis();
		app.solve();
		System.out.println((System.currentTimeMillis()-t)/1000.0);
	}
}
