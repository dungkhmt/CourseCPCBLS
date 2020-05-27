package test;

import java.util.ArrayList;

import localsearch.constraints.basic.NotEqual;
import localsearch.functions.conditionalsum.ConditionalSum;

import org.chocosolver.solver.*;
import org.chocosolver.solver.variables.IntVar;

public class LiquidChoco {
	public IntVar X[][]; 
	public IntVar Y[][];
	public int V[] = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	public int maxV[] = {60, 70, 90, 80, 100};
	ArrayList<Integer> conf[] = new ArrayList [6];
	int n=5, m=20;
	Model model;
	
	public void init(){
		model = new Model("Model");
		X = new IntVar[n][m];
		Y = new IntVar[m][n];
		for(int i=0;i<n;i++)
			for(int j=0;j<m;j++)
				X[i][j] = model.intVar("", 0, 1);
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				Y[i][j] = model.intVar("", 0, 1);
		
		for(int i=0;i<6;i++)
			conf[i] = new ArrayList<Integer>();
		conf[0].add(0); conf[0].add(1);
		conf[1].add(7); conf[1].add(8);
		conf[2].add(12); conf[2].add(17);
		conf[3].add(8); conf[3].add(9);
		conf[4].add(1); conf[4].add(8); conf[4].add(9);
		conf[5].add(0); conf[5].add(9); conf[5].add(12);
	}
	
	public void createConstraint(){
		for(int i=0;i<n;i++)
			for(int j=0;j<m;j++)
				model.arithm(X[i][j], "=", Y[j][i]).post();
		
		/// moi chat trong 1 thung
		int ones_n[] = new int [n];
		for(int i=0;i<n;i++)ones_n[i]=1;
		for(int i=0;i<m;i++)
			model.scalar(Y[i], ones_n, "=", 1).post();
		
		/// tong the tich khong qua the tich cua thung
		for(int i=0;i<n;i++)
			model.scalar(X[i], V, "<=", maxV[i]).post();
		
		/// kiem tra cac chat khong duoc cung 1 thung
		for(int k=0;k<6;k++){
			for(int i=0;i<n;i++){
				int oke[] = new int [m];
				for(int j=0;j<m;j++)oke[j]=0;
				for(int j=0;j<conf[k].size();j++){
					oke[conf[k].get(j)] = 1;
				}
				model.scalar(X[i], oke, "<", conf[k].size()).post();
			}
		}
	}
	
	public void solve(){
		init();
		createConstraint();
		Solver solver = model.getSolver();
		solver.solve();
		for(int i=0;i<n;i++){
			System.out.print("Cac chat trong thung " + i + ": ");
			int total_V=0;
			for(int j=0;j<m;j++)
				if(X[i][j].getValue()==1){
					System.out.print(j + " ");
					total_V += V[j];
				}
			System.out.println();
			System.out.println("The tich hien tai: "+ total_V + "\nThe tich toi da: " + maxV[i]);
		}
	}
	
	public static void main(String[] arg){
		LiquidChoco app = new LiquidChoco();
		app.solve();
	}
}
