package planningoptimization115657k62.NguyenQuynhLoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class CovidwithCSP {
	int N; // so doan khach
	int M; // so khu cach ly
	int [] s; // so luong nguoi trong moi doan khach
	int [] c; // so luong nguoi ma khu cach ly chua toi da
	int []oneM;
	int max_dis;
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
		oneM=new int[M];
		for(int i=0;i<M;i++)
			oneM[i]=1;
		max_dis=N+M-1;
	}
	
	public void solve() {
		ReadFile("/home/lnq/Desktop/20192/tulkh/dataproject/100s35c.txt");
		Model model = new Model("covid with csp");
		IntVar [][] X = new IntVar[M][N];
		// X[i][j]=1 neu doan khach j duoc dua ve khu i
		// X[i][j]=0 neu doan khach j khong duoc dua ve khu i
		for( int i=0;i<M;i++) {
			for(int j=0;j<N;j++) {
				X[i][j]=model.intVar("X["+i+"]["+j+"]",0,1);
			}
		}
		// moi doan khach chi duoc dua ve duy nhat 1 khu
		for(int j=0;j<N;j++) {
			IntVar [] y = new IntVar[M];
			for(int i=0;i<M;i++)
				y[i]=X[i][j];
			model.scalar(y, oneM, "=", 1).post();
		}
		
		// so luong khach dua ve moi khu khong duoc lon hon c[i]
		for(int i=0;i<M;i++) {
			model.scalar(X[i], s, "<=", c[i]).post();
		}
		// Ham muc tieu
		IntVar obj = model.intVar("obj",1,max_dis);
		for(int i=0;i<M;i++) {
			for(int j=0;j<N;j++) {
				model.arithm(model.intScaleView(X[i][j], i+N-j), "<=", obj).post();
			}
		}
		model.setObjective(Model.MINIMIZE, obj);
		
		while(model.getSolver().solve()) {
			System.out.println("khoang cach min= "+obj.getValue());
			for(int i=0;i<M;i++) {
				System.out.print("cac nhom dua ve khu "+(i+1)+ ": ");
				for(int j=0;j<N;j++) {
					if(X[i][j].getValue()==1) {
						System.out.print((j+1)+" ");
					}
				}
				System.out.println();
			}
		}
		
	}
	
	public static void main(String[] args) {
		CovidwithCSP app= new CovidwithCSP();
		long t= System.currentTimeMillis();
		app.solve();
		System.out.println((System.currentTimeMillis()-t)/1000.0);
	}
}
