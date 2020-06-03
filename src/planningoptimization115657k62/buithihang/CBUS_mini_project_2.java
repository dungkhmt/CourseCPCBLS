package test;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class CBUS_mini_project_2 {

	int N,K;
	int INF=Integer.MAX_VALUE;
	int[][] d;
	int[] q;
	MPVariable X[][];
	MPVariable Y[];
	MPVariable Z[][];
	
	public CBUS_mini_project_2(String file) {	
		readData("D:\\Program Files\\Eclipse\\TUH\\src\\data\\data-miniproject\\"+file);
	}
	
	static {
		System.loadLibrary("jniortools");
	}
	
	public void solve(String tenfile) {
		MPSolver solver= new MPSolver("CBUS",MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		//X[i][j] la  diem j la diem tiep theo cua i
		X=new MPVariable[2*N+K+1][2*N+2*K+1];	
		//Y[i] la khoang cach tich luy tu diem xuat phat cua lo trinh den diem i
		Y=new MPVariable[2*N+2*K+1];
		//Z[i][j]=1 neu diem i thuoc lo trinh j
		Z=new MPVariable[2*N+2*K+1][K+1];
		for(int i=1;i<=2*N;i++) {
			Y[i]=solver.makeIntVar(0, 100,  "Y[" + i + "]");
			for(int j=1;j<=K;j++) 
				Z[i][j]=solver.makeIntVar(0, 1,  "Z[" + i + "," + j + "]");
		}
		for(int i=1;i<=K;i++) {
			int I=i+2*N;
			int Ik=i+2*N+K;
			Y[I]=solver.makeIntVar(0, 0,  "Y[" + I + "]");
			Y[Ik]=solver.makeIntVar(1, INF,  "Y[" + Ik + "]");
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
//			System.out.println("solution "+obj.value());
//			System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
			String res = "solution Ortool: " + String.valueOf(obj.value())+"\n";
			res += printSolution();
			System.out.println(res);
			writeresult(res, tenfile);
		}
	}
	
	public String duyet(int i,String res) {
		for(int j=1;j<=2*N;j++) {
			if(X[i][j].solutionValue()==1) {
				res+="->"+String.valueOf(j);
				res=duyet(j,res);
			}
		}
		return res;
	}
	
	public String printSolution() {
		String res="";
		for(int i=1;i<=K;i++){
			res += "route "+String.valueOf(i)+": 0";
			res =duyet(i+2*N,res);
			res+="->0\n";
			res+="travel distance: "+String.valueOf(Y[2*N+K+i].solutionValue())+"\n";
		}
		return res;
	}
	
	public void writeresult(String res, String file) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			File f = new File("D:\\Program Files\\Eclipse\\TUH\\src\\data\\ketqua\\"+file);
            //FileWriter fw = new FileWriter("D:\\Program Files\\Eclipse\\TUH\\src\\data\\ketqua\\"+file);
            fw = new FileWriter(f.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.write(res);
            fw.append(res+"\n");
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Success...");
	}
	
	
	public void readData(String file) {
		try {
			File fi = new File(file);
			Scanner s = new Scanner(fi);
			N = s.nextInt();
			K = s.nextInt();
			System.out.println(N + " "+K);
			q = new int[K+1];
			d = new int[2*N+1][2*N+1];
			
			for (int i = 1; i <= K; i++) {
				q[i] = s.nextInt();
			}
			
			for(int i=1;i<=K;i++) {
				System.out.print(q[i]+" ");
			}System.out.println();
			
			for(int i=0;i<=2*N;i++) {
				for(int j=0;j<=2*N;j++) {
					d[i][j]=s.nextInt();
					System.out.print(d[i][j]+" ");
				}System.out.println();
			}System.out.println();
			s.close();
		} catch (IOException e){
			System.out.println("An error occured.");
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		CBUS_mini_project_2 x=new CBUS_mini_project_2("1.txt");
		x.solve("1.txt");
		//x.printSolution();	
	}
}

