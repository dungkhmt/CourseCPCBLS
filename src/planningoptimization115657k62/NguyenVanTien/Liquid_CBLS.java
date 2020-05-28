package planningoptimization115657k62.NguyenVanTien;

import java.util.ArrayList;

import planningoptimization115657k62.NguyenVanTien.HillClimbingSearch;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.SumFun;

public class Liquid_CBLS {

	LocalSearchManager lsm;
	VarIntLS[][] x;
	ConstraintSystem CS;
	int N = 5;
	int liquid = 20;
	int[] limit = { 60, 70, 80, 90, 100 };
	int[] V = { 20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10 };
//	int[][] constraint;
	ArrayList<ArrayList<Integer>> a = new ArrayList<ArrayList<Integer>>();

	public void init() {
		lsm = new LocalSearchManager();
		x = new VarIntLS[N][liquid];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < liquid; j++) {
				x[i][j] = new VarIntLS(lsm, 0, 1);
			}
		}
		
		CS = new ConstraintSystem(lsm);
		
		for (int i = 0; i < N; i++) {
			IFunction[] temp = new IFunction[liquid];
			for (int j = 0; j < liquid; j++) {
				temp[j] = new FuncMult(x[i][j], V[j]);				
			}
			SumFun c1 = new SumFun(temp);
			LessOrEqual c2 = new LessOrEqual(c1, limit[i]);
			CS.post(c2);
		}
		
		int dem =0;
		IFunction[] tong = new IFunction[liquid * N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < liquid; j++) {
				tong[dem++] = new FuncMult(x[i][j], 1);
			}
		}
		SumFun c3 = new SumFun(tong);
		IsEqual c4 = new IsEqual(c3, 20);
		CS.post(c4);

		//cac thung bi rang buoc
		for (int y = 0; y < a.size(); y++) {
			int[] one1 = new int[a.get(y).size()];
			for (int i = 0; i < a.get(y).size(); i++) {
				one1[i] = 1;
			}
			for (int k = 0; k < N; k++) {
				IFunction[] temp = new IFunction[a.get(y).size()];
				for (int i = 0; i < a.get(y).size(); i++) {
					temp[i] = new FuncMult(x[k][a.get(y).get(i)], 1);				
				}
				SumFun c1 = new SumFun(temp);
				LessOrEqual c2 = new LessOrEqual(c1, a.get(y).size() - 1);
				CS.post(c2);
			}
		}


		for (int i = 0; i < liquid; i++) {
			IFunction[] temp = new IFunction[N];
			for (int j = 0; j < N; j++) {
				temp[j] = new FuncMult(x[j][i], 1);				
			}
			SumFun c1 = new SumFun(temp);
			IsEqual c2 = new IsEqual(c1, 1);
			CS.post(c2);
		}


		lsm.close();
	}
	private void search() {
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(CS, 100000);
		for (int i = 0; i < N; i++) {
			System.out.println();
			System.out.print("thung " + (i+1) + " :");
			for (int j = 0; j < liquid; j++) {
				if(x[i][j].getValue()== 1) {
					System.out.print(j +" ");
				}
			}
		}

	}

	public void solve() {
		init();
		search();
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Liquid_CBLS app = new Liquid_CBLS();
		int m, i = 0;
		ArrayList<Integer> thung1 = new ArrayList<Integer>();

		thung1.add(0);
		thung1.add(1);

		app.a.add(thung1);
		
		ArrayList<Integer> thung2 = new ArrayList<Integer>();

		thung2.add(7);
		thung2.add(8);

		app.a.add(thung2);
		
		ArrayList<Integer> thung3 = new ArrayList<Integer>();

		thung3.add(12);
		thung3.add(17);

		app.a.add(thung3);
		
		ArrayList<Integer> thung4 = new ArrayList<Integer>();

		thung4.add(8);
		thung4.add(9);

		app.a.add(thung4);
		ArrayList<Integer> thung5 = new ArrayList<Integer>();

		thung5.add(1);
		thung5.add(2);
		thung5.add(9);
		
		app.a.add(thung5);
		
		ArrayList<Integer> thung6 = new ArrayList<Integer>();

		thung6.add(0);
		thung6.add(9);
		thung6.add(12);

		app.a.add(thung6);
		
		app.solve();
	}

}
