package planningoptimization115657k62.NguyenQuynhLoc;


import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.functions.sum.SumFun;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;


import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;

public class LiquidwithLS {
	int n=20;
	int m=5;
	int n_constraint=6;
	int [] s= {20,15,10,20,20,25,30,15,10,10,
			20,25,20,10,30,40,25,35,10,10}; // the tich moi chat long
	int []p= {60,70,90,80,100}; // dung tich moi thung hang
	int [][]constraint = {{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			  {0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0},
			  {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0},
			  {0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0},
			  {0,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
			  {1,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0}};
	class Move{
		int i;
		int v;
		public Move(int x,int y){
			this.i=x;
			this.v=y;
		}
	}
	public void solve() {
		Random R=new Random();
		LocalSearchManager mgr= new LocalSearchManager();
		VarIntLS [][] X = new VarIntLS[m][n];
		for(int i=0;i<m;i++) {
			for(int j=0;j<n;j++) {
				X[i][j]= new VarIntLS(mgr, 0, 1);
			}
		}
		ConstraintSystem S= new ConstraintSystem(mgr);
		// moi dung dich chi duoc chua trong 1 thung
		for(int j=0;j<n;j++) {
			IFunction []y= new IFunction[m];
			for(int i=0;i<m;i++) {
				y[i]=new FuncPlus(X[i][j], 0);
			}
			S.post(new IsEqual(new Sum(y), 1));
		}
		//  so luong dung dich phai nho hon the tich moi thung
		for(int i=0;i<m;i++) {
			IFunction []y = new IFunction[n];
			for(int j=0;j<n;j++) {
				y[j]= new FuncMult(X[i][j], s[j]);
			}
			S.post(new LessOrEqual(new Sum(y), p[i]));
		}
		
		// thoa man rang buoc
		for(int i=0;i<m;i++) {
			for(int k=0;k<n_constraint;k++) {
				IFunction [] y= new IFunction[n];
				for(int j=0;j<n;j++) {
					y[j]= new FuncMult(X[i][j], constraint[k][j]);
				}
				S.post(new LessOrEqual(new Sum(y), 1));
			}
		}
		mgr.close();
		int k=0;
		ArrayList<Move> cand = new ArrayList<Move>();
		VarIntLS [] y = S.getVariables();
		System.out.println(S.violations());
		while(k<1000000&S.violations()>0) {
			cand.clear();
			int dmax=Integer.MAX_VALUE;
			for(int i=0;i<y.length;i++) {
				for(int v=y[i].getMinValue();v<=y[i].getMaxValue();v++) {
					int delta= S.getAssignDelta(y[i], v);
					if(delta<dmax) {
						cand.clear();
						cand.add(new Move(i, v));
						dmax=delta;
					}else if(delta==dmax) {
						cand.add(new Move(i, v));
					}
				}
			}
			Move m= cand.get(R.nextInt(cand.size()));
			y[m.i].setValuePropagate(m.v);
			System.out.println("step "+ k+","+ "violation = "+ S.violations());
			k++;
		}
	}
	public static void main(String[] args) {
		LiquidwithLS app = new LiquidwithLS();
		app.solve();
	}
}
