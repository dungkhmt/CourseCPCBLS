package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

public class CBUS_mini_project_2_Choco {
	int N,K;
	int[][]d;
	int[]q ;
	int INF=Integer.MAX_VALUE;
	int[] oneK;
	
	
	Model model = new Model("CBUS");
	IntVar[] X;
	IntVar[] IR;
	IntVar[] L;
	IntVar[] W;
	IntVar F;
	IntVar[] Y;
	
	public CBUS_mini_project_2_Choco(String file) {
		readData("D:\\Program Files\\Eclipse\\TUH\\src\\data\\data-miniproject\\"+file);
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
			oneK = new int[K];
			for(int i=0; i<K; i++) {
				oneK[i] = 1;
			}
			
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
	
	public void solve(String tenfile) {
		X = new IntVar[2*N+K+1];
		IR = new IntVar[2*N+2*K+1];
		L = new IntVar[2*N+2*K+1];
		W = new IntVar[2*N+2*K+1];
		
		for(int i=1; i<=2*N+K; i++) {
			X[i] = model.intVar("X" + i,1,2*N+2*K);
		}
		
		for(int i=1; i<=2*N+2*K; i++) {
			IR[i] = model.intVar("IR" + i,1,K);
			if(i <= 2*N+K) {
				L[i] = model.intVar("L" + i,0,100);
			}else {
				L[i] = model.intVar("L" + i,1,100);
			}
			
			W[i] = model.intVar("L" + i,0,N+1);
		}
		
		F = model.intVar("F",0,100);
		
		for(int i=1; i<=K; i++) {
			model.arithm(IR[i+2*N], "=", i).post();
			model.arithm(IR[i+2*N+K], "=", i).post();
		}
		
		for(int i=1; i<=N; i++) {
			model.arithm(IR[i], "=", IR[i+N]).post();
			model.arithm(L[i], "<", L[i+N]).post();
		}
		
		for(int i = 2*N+1; i<=2*N+K; i++) {
			model.arithm(L[i], "=", 0).post();
			model.arithm(W[i], "=", 0).post();
		}
		
		for(int i=1; i<=2*N+K; i++) {
			for(int j=1; j<=2*N+K; j++) {
				if(i!=j) {
					model.arithm(X[i], "!=", X[j]).post();
				}
			}
			model.arithm(X[i], "!=", i).post();
		}
		
		//model.allDifferent(X).post();
		
		for(int i=1; i<=2*N+K; i++) {
			int I,J;
			if(i>2*N) I=0;
			else I=i;
			for(int j=1; j<=2*N+2*K; j++) {
				if(j>2*N) J=0;
				else J=j;
				model.ifThen(
						model.arithm(X[i], "=", j),
						model.arithm(IR[i], "=", IR[j]));
				model.ifThen(
						model.arithm(X[i], "=", j),
						model.arithm(L[j], "=", model.intOffsetView(L[i], d[I][J])));
				
				if(j<=N) {
					model.ifThen(
							model.arithm(X[i], "=", j),
							model.arithm(W[j], "=", model.intOffsetView(W[i], 1)));
				}
				if(N<j && j<=2*N) {
					model.ifThen(
							model.arithm(X[i], "=", j),
							model.arithm(W[j], "=", model.intOffsetView(W[i], -1)));
				}

			}
		}
		
		for(int i=1; i<=N; i++) {
			for(int j=1; j<=K; j++) {
				model.ifThen(
						model.arithm(IR[i], "=", j), 
						model.arithm(W[i], "<=", q[j]));
			}
		}
		
		for(int i=1; i<=K; i++) {
			for(int j=1; j<=K; j++) {
				if(i!=j) {
					model.arithm(IR[i+2*N], "!=", IR[j+2*N]).post();
					model.arithm(IR[i+2*N+K], "!=", IR[j+2*N+K]).post();
				}
			}
		}
		
		
		Y = new IntVar[K];
		for(int i= 0; i<K; i++) {
			Y[i] = L[2*N+K+i+1];
		}
		model.scalar(Y, oneK, "=", F).post();
		
		model.setObjective(false,F);
		//model.getSolver().solve();
		
		int best = 1000000;
		String res = "";
		while(model.getSolver().solve()) {
			if(F.getValue() < best) {
				best = F.getValue();
				res = "";
				for(int i=1;i<=K;i++){
					res += "route "+String.valueOf(i)+": 0"; 
					//System.out.print("route "+i+": "+0);
					res = duyet(i+2*N,res);
					res += "-> 0 "+"\n";
					res += "travel distance: "+ String.valueOf(L[2*N+K+i].getValue())+"\n";
				}
			}
		};
		
		System.out.println("solution Choco: " + best);
		System.out.println(res);
		writeresult("solution Choco: " + best + "\n" + res, tenfile);
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
	
	public String duyet(int i, String res) {
		if(X[i].getValue() < 2*N+1){
			res += "->"+ String.valueOf(X[i].getValue());
			res = duyet(X[i].getValue(),res);
		}
		return res;
	}
	
	
	
	public static void main(String[] args) {
		CBUS_mini_project_2_Choco app = new CBUS_mini_project_2_Choco("1.txt");
		app.solve("1.txt");
	}
}
