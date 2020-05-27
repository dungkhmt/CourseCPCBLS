package Baitap;

import cbls115676khmt61.phamquangdung.Binpacking2D;
import cbls115676khmt61.phamquangdung.HillClimbingSearch;
import core.VarInt;
import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class BaitapContainer {
	int MAX_W = 4;
	int MAX_L = 6;
	int N = 6;
	
	int [] w = {1, 3, 2, 3, 1, 2};
	int [] l = {4, 1, 2, 1, 4, 3};
	VarIntLS [] x;
	VarIntLS [] y;
	VarIntLS [] o;
	
	LocalSearchManager mgr;
	ConstraintSystem S;
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
		
		x = new VarIntLS[N];
		y = new VarIntLS[N];
		o = new VarIntLS[N];

		for(int i = 0; i < N; i++){
			x[i] = new VarIntLS(mgr, 0, MAX_W);
			y[i] = new VarIntLS(mgr, 0, MAX_L);
			o[i] = new VarIntLS(mgr, 0, 1);
		}
		
		// rang buoc cac kien hang phai nam trong container
		for(int i = 0; i < N; i++){
			S.post(new Implicate(new IsEqual(o[i], 0), 
					new LessOrEqual(new FuncPlus(x[i], w[i]), MAX_W)));
			S.post(new Implicate(new IsEqual(o[i], 0), 
					new LessOrEqual(new FuncPlus(y[i], l[i]), MAX_L)));
			
			S.post(new Implicate(new IsEqual(o[i], 1), 
					new LessOrEqual(new FuncPlus(x[i], l[i]), MAX_W)));
			S.post(new Implicate(new IsEqual(o[i], 1), 
					new LessOrEqual(new FuncPlus(y[i], w[i]), MAX_L)));
		}
		
		// rang buoc thu tu lay kien hang
		for (int i = 0; i < N; i++)
			for (int j = i+1; j < N; j++) {
				IConstraint[] c1 = new IConstraint[2];
				c1[0] = new IsEqual(o[i], 0);
				c1[1] = new IsEqual(o[j], 0);
				IConstraint c2 = new AND(c1);
				IConstraint[] c3 = new IConstraint[3];
				c3[0] = new LessOrEqual(new FuncPlus(x[i], w[i]),x[j]);
				c3[1] = new LessOrEqual(new FuncPlus(x[j], l[j]),x[i]);
				c3[2] = new LessOrEqual(new FuncPlus(y[j], w[j]),y[i]);
				IConstraint c4 = new OR(c3);
				S.post(new Implicate(c2, c4));
				
				c1 = new IConstraint[2];
				c1[0] = new IsEqual(o[i], 0);
				c1[1] = new IsEqual(o[j], 1);
				c2 = new AND(c1);
				c3 = new IConstraint[3];
				c3[0] = new LessOrEqual(new FuncPlus(x[i], w[i]),x[j]);
				c3[1] = new LessOrEqual(new FuncPlus(x[j], l[j]),x[i]);
				c3[2] = new LessOrEqual(new FuncPlus(y[j], w[j]),y[i]);
				c4 = new OR(c3);
				S.post(new Implicate(c2, c4));
				
				c1 = new IConstraint[2];
				c1[0] = new IsEqual(o[i], 1);
				c1[1] = new IsEqual(o[j], 0);
				c2 = new AND(c1);
				c3 = new IConstraint[3];
				c3[0] = new LessOrEqual(new FuncPlus(x[i], l[i]),x[j]);
				c3[1] = new LessOrEqual(new FuncPlus(x[j], w[j]),x[i]);
				c3[2] = new LessOrEqual(new FuncPlus(y[j], l[j]),y[i]);
				c4 = new OR(c3);
				S.post(new Implicate(c2, c4));
				
				c1 = new IConstraint[2];
				c1[0] = new IsEqual(o[i], 1);
				c1[1] = new IsEqual(o[j], 1);
				c2 = new AND(c1);
				c3 = new IConstraint[3];
				c3[0] = new LessOrEqual(new FuncPlus(x[i], l[i]),x[j]);
				c3[1] = new LessOrEqual(new FuncPlus(x[j], l[j]),x[i]);
				c3[2] = new LessOrEqual(new FuncPlus(y[j], w[j]),y[i]);
				c4 = new OR(c3);
				S.post(new Implicate(c2, c4));
			}
		
		mgr.close();
	}

	private void search(){
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S, 100000);
		for(int i = 0; i < N; i++){
			System.out.println("item " + i + ": " + x[i].getValue() + "," + y[i].getValue() + "," + o[i].getValue());
		}
	}
	
	public void solve(){
		stateModel();
		search();
	}
	

	public void solve2() {
		System.out.println("Init S = " + S.violations());
		
		MinMaxSelector mms = new MinMaxSelector(S);
		
		int it = 0;
		while(it < 10000 && S.violations() > 0){
			
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_v = mms.selectMostPromissingValue(sel_x);
			
			sel_x.setValuePropagate(sel_v);
			System.out.println("Buoc lap thu " + it + ",  so vi pham rang buoc = " + S.violations());
//			System.out.println("day la cai deo gi the" + sel_x);
//			System.out.println(sel_v);
			it++;
		}
	}
	
//	public void printSol() {
//		int [][] C = new int[MAX_W][MAX_L];
//		
//		for (int i = 0; i < MAX_W; i++)
//			for (int j = 0; j < MAX_L; j++)
//				C[i][j] = -1;
//		
//		int _w = 0, _l = 0;
//		
//		for (int i = 0; i < N; i++) {
//			if (o[i].getValue() == 0) {
//				_w = w[i];
//				_l = l[i];
//			}
//			else {
//				_w = l[i];
//				_l = w[i];
//			}
//			
//			for (int j = x[i].getValue(); j < x[i].getValue() + _w; j++)
//				for (int k = y[i].getValue(); k < y[i].getValue() + _l; k++)
//					C[j][k] = i;
//		}
//			
//		for (int i = 0; i < MAX_W; i++) {
//			System.out.println();
//			for (int j =0; j < MAX_L; j++)
//				System.out.print(C[i][j] + " ");
//			
//		}
//	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("hello");
		BaitapContainer app = new BaitapContainer();
		app.solve();
//		app.printSol();
	}	
}
