package OPenCBLS;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.constraints.multiknapsack.MultiKnapsack;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Bai1 {

	int N = 20;
	int P = 5;
	int[] capa = { 20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30,
			40, 25, 35, 10, 10 };
	int[] C = { 60, 70, 90, 80, 100 };

	int[][] conflic = { { 0, 1 }, { 7, 8 }, { 12, 17 }, { 8, 9 }, { 1, 2 },
			{ 1, 9 }, { 2, 9 }, { 0, 9 }, { 9, 12 }, { 0, 12 } };

	LocalSearchManager lsm;
	VarIntLS[] X;
	ConstraintSystem CS;
	IFunction[] V;

	public void Model() {
		lsm = new LocalSearchManager();
		X = new VarIntLS[N];
		for (int i = 0; i < N; i++)
			X[i] = new VarIntLS(lsm, 0, P - 1);

		CS = new ConstraintSystem(lsm);
		for (int k = 0; k < conflic.length; k++) {
			IConstraint c = new NotEqual(X[conflic[k][0]], X[conflic[k][1]]);
			CS.post(c);
		}

//		V = new IFunction[P];

//		for (int j = 0; j < P; j++) {
//			V[j] = new ConditionalSum(X, capa, j);
//
//			CS.post(new LessOrEqual(V[j], C[j]));
//		}
		CS.post(new MultiKnapsack(X, capa, C));
		lsm.close();

	}

	public void search() {
		MyTabuSearch searcher = new MyTabuSearch(CS);
		searcher.search(10000, 5, 50);
	}

	public void InKetQua() {
		for (int j = 0; j < P; j++) {
			System.out.println("Thung: " + j + ": ");
			int sum = 0;
			for (int i = 0; i < N; i++)
				if (X[i].getValue() == j) {
					sum += capa[i];
					System.out.print(i + ", ");
				}
			System.out.println("The tich  = " + sum);
//			System.out.println("The tich  = " + V[j].getValue());
		}
	}

	public static void main(String[] args) {
		Bai1 App = new Bai1();
		App.Model();
		App.search();
		App.InKetQua();

	}

}
