package test;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import java.util.ArrayList;

public class Liquid {

	int N=20,K=5;
	int []d= {60,70,90,80,100};// dung tich thung
	int []v= {20,15,10,20,20,25,30,15,10,10,20,25,20,10,30,40,25,35,10,10};//the tich chat long
	int [][]c= {{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			    {0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0},
			    {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0},
			    {0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0},
			    {0,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
			    {1,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0}};// rang buoc
	int[] oneN ={1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
	int[] oneK = {1,1,1,1,1};

	public void choco_solve() {
		Model model=new Model("Liquid");
		IntVar x[][]=new IntVar[K][N];//thung i chua chat long j
		
		for(int i=0;i<K;i++) {
			for(int j=0;j<N;j++) {
				x[i][j] = model.intVar("x[" + i + "," + j + "]",0,1);
			}
		}
		for(int i=0;i<N;i++) {
			IntVar t[]=new IntVar[K];
			for(int j=0;j<K;j++) {
				t[j]=x[j][i];
			}
			model.scalar(t, oneK, "=", 1).post();
			
		}
		for(int i = 0; i < K; i++){
			model.scalar(x[i], v, "<=", d[i]).post();
			for(int j=0;j<c.length;j++) {
				model.scalar(x[i], c[j], "<=", 1).post();
			}
		}
		model.getSolver().solve();

		for(int j = 0; j < K; j++){
		  System.out.println("thung " + j + ": ");
		  for(int i = 0; i < N; i++)if(x[j][i].getValue() == 1){
		    System.out.print("[chat long " + i + ", the tich " + v[i] + "] ");
		  System.out.println();
		  }
		}

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Liquid().choco_solve();;
		
	}

}
