package cbls115676khmt61.nguyenductrong_20164810;

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

public class Container {
	int L = 6;
	int W = 4;
	int[] w = { 1, 3, 2, 3, 1, 2 };
	int[] l = { 4, 1, 2, 1, 4, 3 };
	int N = 6;

	VarIntLS[] X;
	VarIntLS[] Y;
	VarIntLS[] O;

	LocalSearchManager mgr;
	ConstraintSystem S;

	private void stateModel() {
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
		X = new VarIntLS[N];
		Y = new VarIntLS[N];
		O = new VarIntLS[N];

		for (int i = 0; i < N; i++) {
			X[i] = new VarIntLS(mgr, 0, W);
			Y[i] = new VarIntLS(mgr, 0, L);
			O[i] = new VarIntLS(mgr, 0, 1);
		}

		// in bin constrain
		for (int i = 0; i < N; i++) {
			AND tmpAnd = new AND(new LessOrEqual(new FuncPlus(X[i], w[i]), W),
					new LessOrEqual(new FuncPlus(Y[i], l[i]), L));
			Implicate tmpIm = new Implicate(new IsEqual(O[i], 0), tmpAnd);
			S.post(tmpIm);

			AND tmpAnd1 = new AND(new LessOrEqual(new FuncPlus(X[i], l[i]), W),
					new LessOrEqual(new FuncPlus(Y[i], w[i]), L));
			Implicate tmpIm1 = new Implicate(new IsEqual(O[i], 1), tmpAnd1);
			S.post(tmpIm1);
		}

		// overlap constrain
		for (int i = 0; i < N; i++) {
			for (int j = i + 1; j < N; j++) {
				AND o00 = new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 0));
				IConstraint[] tmpC = new IConstraint[4];
				tmpC[0] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
				tmpC[1] = new LessOrEqual(new FuncPlus(X[j], w[j]), X[i]);
				tmpC[2] = new LessOrEqual(new FuncPlus(Y[i], l[i]), Y[j]);
				tmpC[3] = new LessOrEqual(new FuncPlus(Y[j], l[j]), Y[i]);
				OR tmpOr1 = new OR(tmpC);
				S.post(new Implicate(o00, tmpOr1));

				//
				AND o01 = new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 1));
				IConstraint[] tmpC1 = new IConstraint[4];
				tmpC1[0] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
				tmpC1[1] = new LessOrEqual(new FuncPlus(X[j], l[j]), X[i]);
				tmpC1[2] = new LessOrEqual(new FuncPlus(Y[i], l[i]), Y[j]);
				tmpC1[3] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
				OR tmpOr2 = new OR(tmpC1);
				S.post(new Implicate(o01, tmpOr2));

				//
				AND o10 = new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 0));
				IConstraint[] tmpC2 = new IConstraint[4];
				tmpC2[0] = new LessOrEqual(new FuncPlus(X[i], l[i]), X[j]);
				tmpC2[1] = new LessOrEqual(new FuncPlus(X[j], w[j]), X[i]);
				tmpC2[2] = new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]);
				tmpC2[3] = new LessOrEqual(new FuncPlus(Y[j], l[j]), Y[i]);
				OR tmpOr3 = new OR(tmpC2);
				S.post(new Implicate(o10, tmpOr3));

				//
				AND o11 = new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 1));
				IConstraint[] tmpC4 = new IConstraint[4];
				tmpC4[0] = new LessOrEqual(new FuncPlus(X[i], l[i]), X[j]);
				tmpC4[1] = new LessOrEqual(new FuncPlus(X[j], l[j]), X[i]);
				tmpC4[2] = new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]);
				tmpC4[3] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
				OR tmpOr4 = new OR(tmpC4);
				S.post(new Implicate(o11, tmpOr4));
			}
		}

		// contraint order
		for (int i = 0; i < N; i++) {
			for (int j = i + 1; j < N; j++) {
				// O[i] = 0, O[j] = 0
				AND o00 = new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 0));
				IConstraint[] tmp00 = new IConstraint[3];
				tmp00[0] = new LessOrEqual(Y[i], Y[j]);
				tmp00[1] = new LessOrEqual(new FuncPlus(X[j], w[j]), X[i]);
				tmp00[2] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
				OR tmpOr0 = new OR(tmp00);
				S.post(new Implicate(o00, tmpOr0));
				
				// 
				AND o01 = new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 1));
				IConstraint[] tmp01 = new IConstraint[3];
				tmp01[0] = new LessOrEqual(Y[i], Y[j]);
				tmp01[1] = new LessOrEqual(new FuncPlus(X[j], l[j]), X[i]);
				tmp01[2] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
				OR tmpOr1 = new OR(tmp01);
				S.post(new Implicate(o01, tmpOr1));
				
				// 
				AND o10 = new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 0));
				IConstraint[] tmp10 = new IConstraint[3];
				tmp10[0] = new LessOrEqual(Y[i], Y[j]);
				tmp10[1] = new LessOrEqual(new FuncPlus(X[j], l[j]), X[i]);
				tmp10[2] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
				OR tmpOr2 = new OR(tmp10);
				S.post(new Implicate(o10, tmpOr2));
				
				// 
				AND o11 = new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 1));
				IConstraint[] tmp11 = new IConstraint[3];
				tmp11[0] = new LessOrEqual(Y[i], Y[j]);
				tmp11[1] = new LessOrEqual(new FuncPlus(X[j], l[j]), X[i]);
				tmp11[2] = new LessOrEqual(new FuncPlus(X[i], l[i]), X[j]);
				OR tmpOr3 = new OR(tmp11);
				S.post(new Implicate(o11, tmpOr3));
			}
		}

		mgr.close();
	}

	public void search() {
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S, 10000);
	}

	public void solve() {
		stateModel();
		search();
		for (int i = 0; i < N; i++) {
			System.out.println(X[i].getValue() + " " + Y[i].getValue() + " " + O[i].getValue());
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Container cont = new Container();
		cont.solve();
	}
}
