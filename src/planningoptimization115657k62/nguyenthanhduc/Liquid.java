package test;

import java.util.ArrayList;

import test.HillClimbingSearch;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;
import miniproject.HillClimbing;

public class Liquid {
	LocalSearchManager mgr = new LocalSearchManager();
	ConstraintSystem cs = new ConstraintSystem(mgr);
	VarIntLS[][] X;
	VarIntLS[][] Y;
	public int V[] = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	public int maxV[] = {60, 70, 90, 80, 100};
	ArrayList<Integer> conf[] = new ArrayList [6];
	int n=5, m=20;
	
	public void init(){
		X = new VarIntLS[n][m];
		Y = new VarIntLS[m][n];
		for(int i=0;i<n;i++)
			for(int j=0;j<m;j++)
				X[i][j] = new VarIntLS(mgr, 0, 1);
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				Y[i][j] = new VarIntLS(mgr, 0, 1);
		
		for(int i=0;i<6;i++)
			conf[i] = new ArrayList<Integer>();
		conf[0].add(0); conf[0].add(1);
		conf[1].add(7); conf[1].add(8);
		conf[2].add(12); conf[2].add(17);
		conf[3].add(8); conf[3].add(9);
		conf[4].add(1); conf[4].add(8); conf[4].add(9);
		conf[5].add(0); conf[5].add(9); conf[5].add(12);
		
//		for(int i=0;i<n;i++)
//			for(int j=0;j<m;j++)
//				X[i][j].setValue(1);
//		for(int i=0;i<m;i++)
//			for(int j=0;j<n;j++)
//				Y[i][j].setValue(1);
	}
	
	public void buildModel(){
		for(int i=0;i<n;i++)
			for(int j=0;j<m;j++)
				cs.post(new IsEqual(X[i][j], Y[j][i]));
			
		/// moi chat trong 1 thung
		for(int i=0;i<m;i++)
			cs.post(new IsEqual(new Sum(Y[i]), 1));
		
		/// tong V khong qua V cua thung
		for(int i=0;i<n;i++)
			cs.post(new LessOrEqual(new ConditionalSum(X[i], V, 1), maxV[i]));
		for(int k=0;k<6;k++){
			for(int i=0;i<n;i++){
				int oke[] = new int [20];
				for(int j=0;j<conf[k].size();j++){
					oke[conf[k].get(j)] = 1;
				}
				cs.post(new NotEqual(new ConditionalSum(X[i], oke,1), conf[k].size()));
			}
		}
		mgr.close();
	}
	
	public void search(){
		init();
		buildModel();
		System.out.println("Searching\n");
//		HillClimbingSearch searcher = new HillClimbingSearch();
//		searcher.search(cs,  100000);
		TabuSearch searcher = new TabuSearch();
		searcher.search(cs, 50, 20, 20000, 500);
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
		Liquid app = new Liquid();
		app.search();
	}
}
