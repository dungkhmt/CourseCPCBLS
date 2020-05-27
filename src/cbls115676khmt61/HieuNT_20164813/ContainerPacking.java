package cbls115676khmt61.HieuNT_20164813;

import cbls115676khmt61.HieuNT_20164813.HillClimbingSearch;
import cbls115676khmt61.HieuNT_20164813.search.AssignMove;
import cbls115676khmt61.HieuNT_20164813.search.TabuSearch;
import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ContainerPacking {
	int W;
	int L;
	int N;
	int[] w;
	int[] l;
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[] x;
	VarIntLS[] y;
	VarIntLS[] o;

	public ContainerPacking() {
		this.W = 4;
		this.L = 6;
		this.N = 6;
		this.w = new int[] { 1, 3, 2, 3, 1, 2 };
		this.l = new int[] { 4, 1, 2, 1, 4, 3 };
	}

	private void stateModel() {
		this.mgr = new LocalSearchManager();
		this.x = new VarIntLS[this.N];
		this.y = new VarIntLS[this.N];
		this.o = new VarIntLS[this.N];
		for (int i = 0; i < this.N; ++i) {
			this.x[i] = new VarIntLS(this.mgr, 0, this.W - 1);
			this.y[i] = new VarIntLS(this.mgr, 0, this.L - 1);
			this.o[i] = new VarIntLS(this.mgr, 0, 1);
		}
		this.S = new ConstraintSystem(this.mgr);
		for (int i = 0; i < this.N; ++i) {
			this.S.post((IConstraint) new Implicate((IConstraint) new IsEqual(this.o[i], 0),
					(IConstraint) new LessOrEqual((IFunction) new FuncPlus(this.x[i], this.w[i]), this.W)));
			this.S.post((IConstraint) new Implicate((IConstraint) new IsEqual(this.o[i], 0),
					(IConstraint) new LessOrEqual((IFunction) new FuncPlus(this.y[i], this.l[i]), this.L)));
			this.S.post((IConstraint) new Implicate((IConstraint) new IsEqual(this.o[i], 1),
					(IConstraint) new LessOrEqual((IFunction) new FuncPlus(this.x[i], this.l[i]), this.W)));
			this.S.post((IConstraint) new Implicate((IConstraint) new IsEqual(this.o[i], 1),
					(IConstraint) new LessOrEqual((IFunction) new FuncPlus(this.y[i], this.w[i]), this.L)));
		}

		for (int i = 0; i < this.N - 1; ++i) {
			for (int j = i + 1; j < this.N; ++j) {
				IConstraint[] c1 = { new IsEqual(this.o[i], 0), new IsEqual(this.o[j], 0) };
				IConstraint c2 = new AND(c1);
				IConstraint[] c3 = { new LessOrEqual(new FuncPlus(this.x[i], this.w[i]), this.x[j]),
						new LessOrEqual(new FuncPlus(this.x[j], this.w[j]), this.x[i]),
						new LessOrEqual(new FuncPlus(this.y[i], this.l[i]), this.y[j]),
						new LessOrEqual(new FuncPlus(this.y[j], this.l[j]), this.y[i]) };
				IConstraint c4 = new OR(c3);
				this.S.post(new Implicate(c2, c4));

				IConstraint[] c5 = { new LessOrEqual(this.x[j], this.x[i]),
						new LessOrEqual(new FuncPlus(this.x[i], 1), new FuncPlus(this.x[j], this.w[j])) };
				IConstraint c6 = new AND(c5);
				IConstraint[] c7 = { new LessOrEqual(this.x[i], this.x[j]),
						new LessOrEqual(new FuncPlus(this.x[j], 1), new FuncPlus(this.x[i], this.w[i])) };
				IConstraint c8 = new AND(c7);
				IConstraint c9 = new OR(c6, c8);
				IConstraint c10 = new LessThan(this.y[i], this.y[j]);
				IConstraint c11 = new AND(c2, c9);
				this.S.post(new Implicate(c11, c10));
			}
		}

		for (int i = 0; i < this.N - 1; ++i) {
			for (int j = i + 1; j < this.N; ++j) {
				IConstraint[] c1 = { new IsEqual(this.o[i], 0), new IsEqual(this.o[j], 1) };
				IConstraint c2 = (IConstraint) new AND(c1);
				IConstraint[] c3 = { new LessOrEqual(new FuncPlus(this.x[i], this.w[i]), this.x[j]),
						new LessOrEqual(new FuncPlus(this.x[j], this.l[j]), this.x[i]),
						new LessOrEqual(new FuncPlus(this.y[i], this.l[i]), this.y[j]),
						new LessOrEqual(new FuncPlus(this.y[j], this.w[j]), this.y[i]) };
				IConstraint c4 = new OR(c3);
				this.S.post(new Implicate(c2, c4));

				IConstraint[] c5 = { new LessOrEqual(this.x[j], this.x[i]),
						new LessOrEqual(new FuncPlus(this.x[i], 1), new FuncPlus(this.x[j], this.l[j])) };
				IConstraint c6 = (IConstraint) new AND(c5);
				IConstraint[] c7 = { (IConstraint) new LessOrEqual(this.x[i], this.x[j]),
						(IConstraint) new LessOrEqual(new FuncPlus(this.x[j], 1), new FuncPlus(this.x[i], this.w[i])) };
				IConstraint c8 = new AND(c7);
				IConstraint c9 = new OR(c6, c8);
				IConstraint c10 = new LessThan(this.y[i], this.y[j]);
				IConstraint c11 = new AND(c2, c9);
				this.S.post(new Implicate(c11, c10));
			}
		}

		for (int i = 0; i < this.N - 1; ++i) {
			for (int j = i + 1; j < this.N; ++j) {
				IConstraint[] c1 = { new IsEqual(this.o[i], 1), new IsEqual(this.o[j], 0) };
				IConstraint c2 = new AND(c1);
				IConstraint[] c3 = { new LessOrEqual(new FuncPlus(this.x[i], this.l[i]), this.x[j]),
						new LessOrEqual(new FuncPlus(this.x[j], this.w[j]), this.x[i]),
						new LessOrEqual(new FuncPlus(this.y[i], this.w[i]), this.y[j]),
						new LessOrEqual(new FuncPlus(this.y[j], this.l[j]), this.y[i]) };
				IConstraint c4 = new OR(c3);
				this.S.post(new Implicate(c2, c4));

				IConstraint[] c5 = { new LessOrEqual(this.x[j], this.x[i]),
						new LessOrEqual(new FuncPlus(this.x[i], 1), new FuncPlus(this.x[j], this.w[j])) };
				IConstraint c6 = new AND(c5);
				IConstraint[] c7 = { new LessOrEqual(this.x[i], this.x[j]),
						new LessOrEqual(new FuncPlus(this.x[j], 1), new FuncPlus(this.x[i], this.l[i])) };
				IConstraint c8 = new AND(c7);
				IConstraint c9 = new OR(c6, c8);
				IConstraint c10 = new LessThan(this.y[i], this.y[j]);
				IConstraint c11 = new AND(c2, c9);
				this.S.post(new Implicate(c11, c10));
			}
		}

		for (int i = 0; i < this.N - 1; ++i) {
			for (int j = i + 1; j < this.N; ++j) {
				IConstraint[] c1 = { new IsEqual(this.o[i], 1), new IsEqual(this.o[j], 1) };
				IConstraint c2 = (IConstraint) new AND(c1);
				IConstraint[] c3 = { new LessOrEqual(new FuncPlus(this.x[i], this.l[i]), this.x[j]),
						new LessOrEqual(new FuncPlus(this.x[j], this.l[j]), this.x[i]),
						new LessOrEqual(new FuncPlus(this.y[i], this.w[i]), this.y[j]),
						new LessOrEqual(new FuncPlus(this.y[j], this.w[j]), this.y[i]) };
				IConstraint c4 = new OR(c3);
				this.S.post(new Implicate(c2, c4));

				IConstraint[] c5 = { new LessOrEqual(this.x[j], this.x[i]),
						new LessOrEqual(new FuncPlus(this.x[i], 1), new FuncPlus(this.x[j], this.l[j])) };
				IConstraint c6 = new AND(c5);
				IConstraint[] c7 = { new LessOrEqual(this.x[i], this.x[j]),
						new LessOrEqual(new FuncPlus(this.x[j], 1), new FuncPlus(this.x[i], this.l[i])) };
				IConstraint c8 = new AND(c7);
				IConstraint c9 = new OR(c6, c8);
				IConstraint c10 = new LessThan(this.y[i], this.y[j]);
				IConstraint c11 = new AND(c2, c9);
				this.S.post((IConstraint) new Implicate(c11, c10));
			}
		}

		this.mgr.close();
	}

	private void search() {
		final HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search((IConstraint) this.S, 10000);

	}

	private void print() {
		final char[][] p = new char[this.W][this.L];
		for (int i = 0; i < this.W; ++i) {
			for (int j = 0; j < this.L; ++j) {
				p[i][j] = '.';
			}
		}
		for (int i = 0; i < this.N; ++i) {
			if (this.o[i].getValue() == 0) {
				for (int j = this.x[i].getValue(); j < this.x[i].getValue() + this.w[i]; ++j) {
					for (int k = this.y[i].getValue(); k < this.y[i].getValue() + this.l[i]; ++k) {
						p[j][k] = (char) (i + 48);
					}
				}
			} else {
				for (int j = this.x[i].getValue(); j < this.x[i].getValue() + this.l[i]; ++j) {
					for (int k = this.y[i].getValue(); k < this.y[i].getValue() + this.w[i]; ++k) {
						p[j][k] = (char) (i + 48);
					}
				}
			}
		}
		for (int i = 0; i < this.W; ++i) {
			System.out.println(p[i]);
		}
	}

	public void solve() {
		this.stateModel();
		this.search();
		this.print();
	}

	public static void main(final String[] args) {
		final ContainerPacking app = new ContainerPacking();
//		int seed = 8;
//    int max_iter = 1000;
//    int tabu_size = 100;
//    int max_stable = 10;
//    app.stateModel();
//
//    TabuSearch searcher2 = new TabuSearch(tabu_size, max_stable, max_iter, seed);
//    searcher2.satisfy_constraint(app.S, new AssignMove());
//
//		app.print();
		app.solve();

	}
}

//014444
//012255
//012255
//033355

