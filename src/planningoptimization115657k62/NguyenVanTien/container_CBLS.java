package planningoptimization115657k62.NguyenVanTien;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;


import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.SumFun;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class container_CBLS {

	LocalSearchManager lsm;
	VarIntLS[][] x;
	ConstraintSystem CS;

	int N = 6;
	int L = 6;
	int W = 4;
	int[] w = { 1, 3, 2, 3, 1, 2 };
	int[] l = { 4, 1, 2, 1, 4, 3 };

	public void init() {
		lsm = new LocalSearchManager();
		x = new VarIntLS[N][3];
		VarIntLS[] position = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			position[i] = new VarIntLS(lsm, 0, L);
			x[i][0] = new VarIntLS(lsm, 0, W);
			x[i][1] = new VarIntLS(lsm, 0, L);
			x[i][2] = new VarIntLS(lsm, 0, 1);
		}
		
		CS = new ConstraintSystem(lsm);
		
		for (int i = 0; i < N; i++) {
			CS.post(new Implicate(new IsEqual(x[i][2], 0), new AND(new LessOrEqual(x[i][0], W - w[i]), new LessOrEqual(x[i][1], L - l[i]))));
			CS.post(new Implicate(new IsEqual(x[i][2], 1), new AND(new LessOrEqual(x[i][0], L - w[i]), new LessOrEqual(x[i][1], W - l[i]))));
		}
		
		for (int i = 0; i < N - 1; i++) {
			for (int j = i + 1; j < N; j++) {
				IConstraint[] c1 = new IConstraint[4];
				c1[0] = new IsEqual(x[i][2], 0);
				c1[1] = new IsEqual(x[i][2], 1);
				c1[2] = new IsEqual(x[j][2], 0);
				c1[3] = new IsEqual(x[j][2], 1);

				IConstraint a1 = new AND(c1[0],c1[2]);
				IConstraint[][] c3 = new IConstraint[4][4];
				c3[0][0] = new LessOrEqual(new FuncPlus(x[i][0], w[i]), x[j][0]);
				c3[0][1] = new LessOrEqual(new FuncPlus(x[j][0], w[j]), x[i][0]);
				c3[0][2] = new LessOrEqual(new FuncPlus(x[i][1], l[i]), x[j][1]);
				c3[0][3] = new LessOrEqual(new FuncPlus(x[j][1], l[j]), x[i][1]);
				
				IConstraint a2 = new AND(c1[0],c1[3]);
				c3[1][0] = new LessOrEqual(new FuncPlus(x[i][0], w[i]), x[j][0]);
				c3[1][1] = new LessOrEqual(new FuncPlus(x[j][0], l[j]), x[i][0]);
				c3[1][2] = new LessOrEqual(new FuncPlus(x[i][1], l[i]), x[j][1]);
				c3[1][3] = new LessOrEqual(new FuncPlus(x[j][1], w[j]), x[i][1]);
				
				IConstraint a3 = new AND(c1[1],c1[2]);
				c3[2][0] = new LessOrEqual(new FuncPlus(x[i][0], l[i]), x[j][0]);
				c3[2][1] = new LessOrEqual(new FuncPlus(x[j][0], w[j]), x[i][0]);
				c3[2][2] = new LessOrEqual(new FuncPlus(x[i][1], w[i]), x[j][1]);
				c3[2][3] = new LessOrEqual(new FuncPlus(x[j][1], l[j]), x[i][1]);
				
				IConstraint a4 = new AND(c1[1],c1[3]);
				c3[3][0] = new LessOrEqual(new FuncPlus(x[i][0], l[i]), x[j][0]);
				c3[3][1] = new LessOrEqual(new FuncPlus(x[j][0], l[j]), x[i][0]);
				c3[3][2] = new LessOrEqual(new FuncPlus(x[i][1], w[i]), x[j][1]);
				c3[3][3] = new LessOrEqual(new FuncPlus(x[j][1], w[j]), x[i][1]);

				IConstraint[] c4 = new IConstraint[4];
				c4[0] = new OR(c3[0]);
				c4[1] = new OR(c3[1]);
				c4[2] = new OR(c3[2]);
				c4[3] = new OR(c3[3]);
				
				CS.post(new Implicate(a1, c4[0]));
				CS.post(new Implicate(a2, c4[1]));
				CS.post(new Implicate(a3, c4[2]));
				CS.post(new Implicate(a4, c4[3]));	
			}
		}
		
		// thu tu truoc duoc ra truoc
		for (int i = 0; i < N; i++) {
			IConstraint c1 = new Implicate(new IsEqual(x[i][2], 0), new IsEqual(new FuncMinus(position[i], x[i][1]), l[i]));
			IConstraint c2 = new Implicate(new IsEqual(x[i][2], 1), new IsEqual(new FuncMinus(position[i], x[i][1]), w[i]));
		}

		for (int i = 0; i < N - 1; i++) {
			CS.post(new LessOrEqual(position[i], position[i + 1]));
		}
		lsm.close();
	}
	
	private void search() {
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(CS, 10000);
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < 3; j++) {
				System.out.print(x[i][j].getValue()+" ");
			}
			System.out.println();
		}
	}
	

	public void solver() {
		init();
		search();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		container_CBLS app = new container_CBLS();
		app.solver();
	}

}
