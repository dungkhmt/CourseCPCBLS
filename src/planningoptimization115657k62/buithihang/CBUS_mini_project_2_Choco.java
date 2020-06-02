package test;

import java.util.Random;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

public class CBUS_mini_project_2_Choco {
	int N,K;
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
	int INF=Integer.MAX_VALUE;
	int[] oneK;
	Random r=new Random();
	
	Model model = new Model("CBUS");
	IntVar[] X;
	IntVar[] IR;
	IntVar[] L;
	IntVar[] W;
	IntVar F;
	IntVar[] Y;
	
	public CBUS_mini_project_2_Choco(int N,int K) {
		this.N=N;
		this.K=K;
//		d=new int[2*N+1][2*N+1];
//		q=new int[K+1];
//		data_generator();
		oneK = new int[K];
		for(int i=0; i<K; i++) {
			oneK[i] = 1;
		}
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
	
	public void solve() {
		X = new IntVar[2*N+K+1];
		IR = new IntVar[2*N+2*K+1];
		L = new IntVar[2*N+2*K+1];
		W = new IntVar[2*N+2*K+1];
		
		for(int i=1; i<=2*N+K; i++) {
			X[i] = model.intVar("X" + i,1,2*N+2*K);
		}
		
		for(int i=1; i<=2*N+2*K; i++) {
			IR[i] = model.intVar("IR" + i,1,K);
			L[i] = model.intVar("L" + i,0,100);
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
		
		System.out.println(best);
		System.out.println(res);
		
//		for(int i=1;i<=K;i++){
//			System.out.print("route "+i+": "+0);
//			duyet(i+2*N);
//			System.out.print("->"+0+"\n");
//			System.out.println("travel distance: "+L[2*N+K+i].getValue());
//		}
	}
	
	public String duyet(int i, String res) {
		if(X[i].getValue() < 2*N+1){
			res += "->"+ String.valueOf(X[i].getValue());
			res = duyet(X[i].getValue(),res);
		}
		return res;
	}
	
	public static void main(String[] args) {
		CBUS_mini_project_2_Choco app = new CBUS_mini_project_2_Choco(4,2);
		app.solve();
	}
}
