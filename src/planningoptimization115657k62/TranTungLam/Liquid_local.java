package giua_ky;

import localsearch.constraints.basic.LessOrEqual;

import java.util.ArrayList;

import org.chocosolver.solver.variables.IntVar;

import localsearch.constraints.basic.IsEqual;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.sum.SumFun;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import project.HillClimbingSearch;

public class Liquid_local {

	int n = 5, m = 20;
	int con[] = {60, 70, 90, 80, 100};
	int liq[] = {20,15,10,20,20,25,30,15,10,10,
			     20,25,20,10,30,40,25,35,10,10
				};
	int conflict[][] = {{0,1,-1}, {7,8,-1}, {12,17,-1},
						{8,9,-1}, {1,2,9},
						{0,9,12}
						};
	
	LocalSearchManager mgr;
	VarIntLS[][] x;
	ConstraintSystem S;

	private void state() {
		mgr = new LocalSearchManager();
		x = new VarIntLS[m][n];
		
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++){
				x[i][j] = new VarIntLS(mgr, 0, 1);
			}
		
		S = new ConstraintSystem(mgr);
		
		// C1: mot chat long chi thuoc 1 thung
		for (int i = 0; i < m; i++) {
			IFunction[] tem = new IFunction[n];
			for (int idx = 0; idx < n; idx++) {
				tem[idx] = new FuncMult(x[m][idx], 1);
			}
			SumFun s = new SumFun(tem); 
			IConstraint c = new IsEqual(s, 1);
			S.post(c);
		}
		
		// C2: tong the tich cac chat long khong vuot qua nguong
		for (int j = 0; j < n; j++) {
			IFunction[] tem = new IFunction[m];
			for (int idx = 0; idx < m; idx++) {
				tem[idx] = new FuncMult(x[idx][j], liq[idx]);
			}
			SumFun s = new SumFun(tem);
			IConstraint c = new LessOrEqual(s, con[j]);
			S.post(c);
		}
		
		// C3: cac chat long khac nhau
		for (int i = 0; i < 6; i++) {
			ArrayList<Integer> c = new ArrayList<Integer>(); // c chua nhung chat long khong o duoc voi nhau cua 1 rang buoc
			
			for (int j = 0; j < 3; j++) {
				if (conflict[i][j] != -1) {
					c.add(conflict[i][j]);
				}
			}
			
	//		System.out.println("size c: " + c.size());
			
			for (int t = 0; t < n; t++) {
				int size = c.size();
				VarIntLS[] y = new VarIntLS[size];
				for (int idx = 0; idx < size; idx++) {
					y[idx] = x[c.get(idx)][t];
				}
				IFunction[] tem = new IFunction[size];
				for (int idx = 0; idx < size; idx++) 
					tem[idx] = new FuncMult(y[idx], 1);
				SumFun s = new SumFun(tem);
				
				if (size == 3) {
					IConstraint cons = new LessOrEqual(s, 2);
					S.post(cons);
				}
					
				if (size == 2) {
					IConstraint cons = new LessOrEqual(s, 1);
					S.post(cons);
				}
					
			}
		}
		mgr.close();
	}
	
	private void search() {
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S, 100000);
		for (int i = 0; i < n; i++) {
			System.out.println("Thung " + (i) + " :");
			for (int j = 0; j < m; j++) {
				if (x[j][i].getValue() == 1) {
					System.out.println(j);
					
				}
			}
		}
	}
	
	public void solve() {
		state();
		search();
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Liquid_local app= new Liquid_local();
		app.solve();
	}

}
