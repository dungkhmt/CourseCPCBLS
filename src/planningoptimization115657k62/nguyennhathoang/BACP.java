package buoi3;

import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class BACP {
	
	public int N;
	public int P;
	int [] credits=new int[N];
	int alpha,beta,lamda,gamma;
	int L;
	int[] I; 
	int[] J;
	int[] oneN= {1,1,1,1,1,1,1};
	int[] oneP= {1,1,1,1};
	public IntVar[][] X;
	public Model model;
	public void input() {
		Scanner sc=new Scanner(System.in);
		System.out.println("Nhap du lieu");
		N= sc.nextInt();
		P= sc.nextInt();
		
		alpha=sc.nextInt();
		beta=sc.nextInt();
		lamda=sc.nextInt();
		gamma=sc.nextInt();
		
		
		for(int i=0;i<N;i++) {
			credits[i]=sc.nextInt();
		}
		
		L= sc.nextInt();
		I = new int[L];
		J=new int[L];
		for(int i=0;i<L;i++) {
			I[i]=sc.nextInt();
			J[i]=sc.nextInt();
		}
		
		sc.close();
		
	}
	
	public void init() {
		model =new Model("BACP");
		X=new IntVar[P][N];
		for(int i=0;i<P;i++) {
			for(int j=0;j<N;j++) {
				X[i][j]=model.intVar("",0,1);
			}
		}
		
	}
	
	public void creatConstraint() {
		int[] oneN=new int[N];
		for(int i=0;i<N;i++)
			oneN[i]=1;
		for(int j = 0; j < P; j++){
			model.scalar(X[j], credits, ">=", lamda).post();
			model.scalar(X[j], credits, "<=", gamma).post();

			model.scalar(X[j], oneN, ">=", alpha).post();
			model.scalar(X[j], oneN, "<=", beta).post();
		}
		
		int[] oneP=new int[P];
		for(int i=0;i<N;i++)
			oneP[i]=1;
		
		for(int i = 0; i < N; i++){
			  IntVar[] y = new IntVar[P];
			  for(int j = 0; j < P ; j++) 
				  y[j] = X[j][i];
			  model.scalar(y, oneP, "=", 1).post();
		}
		
		for(int k = 0; k < I.length; k++){
			  int i = I[k];  int j = J[k];
			  for(int q = 0; q < P; q++)
			    for(int p = 0; p <= q; p++)
			      model.ifThen(model.arithm(X[q][i], "=", 1), 
			                                          model.arithm(X[p][j], "=", 0));
		}
		
		
	}
	
	public void solve() {
		input();
		init();
		creatConstraint();
		model.getSolver().solve();
		
		for(int j = 0; j < P; j++){
			  System.out.print("semester " + j + ": ");
			  for(int i = 0; i < N; i++)
				  if(X[j][i].getValue() == 1){
			    System.out.print("[course " + i + ", credit " + credits[i] + "] ");
			  System.out.println();
			}
		}
	}
	

	public static void main(String[] args) {
		
		BACP app=new BACP();
		app.solve();
		
		
		
	}

}
