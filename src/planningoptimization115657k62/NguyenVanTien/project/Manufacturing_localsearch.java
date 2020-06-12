package project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.sum.SumFun;
import localsearch.functions.basic.FuncMult;

public class Manufacturing_localsearch {
	class Move {
		int i;
		int v;

		public Move(int i, int v) {
			this.i = i;
			this.v = v;
		}
	}
    long start;
	long end;

	int C = 2000, A = 2000, N = 5;
	int c[] = { 1, 2, 4, 5,1 };
	int a[] = { 3, 4, 10, 15, 2 };
	int f[] = { 1, 1, 1, 1 , 2};
	int m[] = { 3, 4, 2, 1 ,5};

//	int C, A, N;
//	int[] a, c, m, f;
	LocalSearchManager mgr;
	VarIntLS[] x;
	ConstraintSystem CS;
	SumFun muctieu;
	Random R = new Random();
	private List<Move> cand = new ArrayList<Move>();

	public void init() {
		mgr = new LocalSearchManager();

		x = new VarIntLS[N];

		ArrayList<Integer> min_u = new ArrayList<Integer>();
		for (int i = 0; i < N; i++) {
			min_u.add(Math.min((int) A / a[i], (int) C / c[i]));
		}

		for (int i = 0; i < N; i++) {
			x[i] = new VarIntLS(mgr, 0, min_u.get(i));
			x[i].setValue(0);
		}

		CS = new ConstraintSystem(mgr);

		// rang buoc voi dien tich A
		IFunction[] area = new IFunction[N];
		for (int i = 0; i < N; i++) {
			area[i] = new FuncMult(x[i], a[i]);
		}
		SumFun dientich = new SumFun(area);
		IConstraint dt = new LessOrEqual(dientich, A);
		CS.post(dt);

		// rang buoc voi chi phi C
		IFunction[] cost = new IFunction[N];
		for (int i = 0; i < N; i++) {
			cost[i] = new FuncMult(x[i], c[i]);
		}
		SumFun chiphi = new SumFun(cost);
		IConstraint cp = new LessOrEqual(chiphi, C);
		CS.post(cp);
		System.out.println(cp.getVariables().length);

		// ham muc tieu
		IFunction[] target = new IFunction[N];
		for (int i = 0; i < N; i++) {
			target[i] = new FuncMult(x[i], f[i]);
		}
		muctieu = new SumFun(target);

		mgr.close();
	}
	public void exploreNeighborhoodConstraintThenFunction(VarIntLS[] X, IConstraint c, IFunction f, List<Move> cand) {
		cand.clear();
		int minDeltaC = Integer.MAX_VALUE;
		int minDeltaF = -Integer.MAX_VALUE;
		for (int i = 0; i < X.length; i++) {
			
			//choose[i] = 0
			int deltaC0 = c.getAssignDelta(X[i], 0);
			int deltaF0 = f.getAssignDelta(X[i], 0);
//			System.out.println("deltaC  + deltaF"+deltaC0 + " " + deltaF0 + " " + minDeltaC + " " + minDeltaF);
			if ((deltaC0 < 0 || deltaC0 == 0 && deltaF0 > 0)) {// accept only better neighbors
//				System.out.println("tien");
				if (deltaC0 < minDeltaC || (deltaC0 == minDeltaC && deltaF0 > minDeltaF)) {
					cand.clear();
					cand.add(new Move(i, 0));
					minDeltaC = deltaC0;
					minDeltaF = deltaF0;
				} else if (deltaC0 == minDeltaC && deltaF0 == minDeltaF) {
					cand.add(new Move(i, 0));
				}
			}

			//choose[i] = 1
			for (int v = m[i]; v <= X[i].getMaxValue(); v++) {
				int deltaC = c.getAssignDelta(X[i], v);
				int deltaF = f.getAssignDelta(X[i], v);
//				System.out.println("deltaC  + deltaF "+deltaC + " " + deltaF + " " + minDeltaC + " " + minDeltaF);
				if ((deltaC < 0 || deltaC == 0 && deltaF > 0)) {// accept only better neighbors
//					System.out.println("lam");
					if (deltaC < minDeltaC || (deltaC == minDeltaC && deltaF > minDeltaF)) {
						cand.clear();
						cand.add(new Move(i, v));
						minDeltaC = deltaC;
						minDeltaF = deltaF;
					} else if (deltaC == minDeltaC && deltaF == minDeltaF) {
						cand.add(new Move(i, v));
					}
				}
			}
		}
	}

	public void move() {
		Move m = cand.get(R.nextInt(cand.size()));
		x[m.i].setValuePropagate(m.v);
	}
	public void search2(int maxIter) {
		int it = 0;
		while (it < maxIter) {
			System.out.println("---------");
//			for (int i = 0; i < CS.getVariables().length; i++) {
//				System.out.println(CS.getVariables()[i].getMaxValue());
//			}

			exploreNeighborhoodConstraintThenFunction(x, CS, muctieu, cand);
			if (cand.size() == 0) {
				System.out.println("local optimum");
				break;
			}
			move();
			System.out.println("Step " + it + ": violations = " + CS.violations() + ", obj = " + muctieu.getValue());
			it++;
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Manufacturing_localsearch app = new Manufacturing_localsearch();
		read_data.N = 500;
		read_data.a = new int[read_data.N];
		read_data.c = new int[read_data.N];
		read_data.m = new int[read_data.N];
		read_data.f = new int[read_data.N];
		read_data.readFile(read_data.N);
		app.N = read_data.N;
		app.c = read_data.c;
		app.C = read_data.C;
		app.A = read_data.A;
		app.a = read_data.a;
		app.m = read_data.m;
		app.f = read_data.f;

		app.start  = System.currentTimeMillis();
		app.init();
		
		System.out.println("---------");
		app.search2(100000);
		app.end = System.currentTimeMillis();
		System.out.println("time: "+ (app.end-app.start) );

//		for (int i = 0; i < app.N; i++) {
//			System.out.println(i + " " + app.x[i].getValue());
//		}
//		System.out.println(app.muctieu.getValue());
	}

}
