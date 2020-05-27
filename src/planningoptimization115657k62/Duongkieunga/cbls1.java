package planningoptimization115657k62.Duongkieunga;

import org.chocosolver.solver.variables.IntVar;

import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.constraints.multiknapsack.MultiKnapsack;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class cbls1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int N=5,M=20;
		int[] Len= {60,70,80,90,100};
		int V[]= {20,15,10,20,20,25,30,15,10,10,20,25,20,10,30,40,25,35,10,10};
		
		int P=6;
		int conflict[][]= {{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//0
				{0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0},//1
				{0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0},//2
				{0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0},//3
				{0,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},//4
				{1,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0},//5
				};
		int nConflic[]= {2,2,2,2,3,3};
		
		LocalSearchManager mgr = new LocalSearchManager(); 
		VarIntLS[][] x = new VarIntLS[5][20];
		for(int i=0;i<N;i++) {
			for(int j=0;j<M;j++) {
				x[i][j]= new VarIntLS(mgr,0,1); 
			}
		}
		ConstraintSystem S = new ConstraintSystem(mgr);
		
		for(int i=0;i<N;i++) {
			IFunction[] y = new IFunction[M]; 
			for(int j=0;j<M;j++) {
				y[j] =new FuncMult(x[i][j], V[j]);
			}
			S.post(new LessOrEqual(new Sum(y), Len[i]));
		}
		
		for(int i=0;i<M;i++) {
			IFunction[] y =new IFunction[N];
			for(int j=0;j<N;j++) {
				y[j]=new FuncMult(x[i][j], 1);
			}
			S.post(new IsEqual(new Sum(y), 1));
		}
		
		for(int i=0;i<N;i++) {
			
			for(int j=0;j<P;j++) {
				IFunction[] y =new IFunction[M];
				for(int k=0;k<M;k++) {
					y[j]=new FuncMult(x[i][k], conflict[j][k]);
				}
				FuncMult f1 = new FuncMult(nConflic[j]), 1); 
				S.post(new LessThan(new Sum(y),nConflic[j]));

			}

			
		}
		
	}

}
