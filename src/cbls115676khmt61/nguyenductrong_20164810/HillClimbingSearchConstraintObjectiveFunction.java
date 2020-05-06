package cbls115676khmt61.nguyenductrong_20164810;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class HillClimbingSearchConstraintObjectiveFunction {
	class Move {
		int i, v, t;

		public Move(int i, int v, int t) {
			this.i = i;
			this.v = v;
			this.t = t;
			// t = 0 -> x[i] =v;
			// t = 1 -> swap(x[i],x[j])
		}
	}

	private IConstraint c;
	private IFunction f;
	private VarIntLS[] X;
	// private IFunction F;
	Random R = new Random();

	public HillClimbingSearchConstraintObjectiveFunction(IConstraint c, IFunction f, VarIntLS[] X) {
		this.c = c;
		this.f = f;
		this.X = X;
		// IFunction cv = new ConstraintViolations(c);

		// F = new FuncPlus(new FuncMult(cv, 1000), new FuncMult(f, 1));
	}

	public void explorerNeighborhoodConstraintFunction(ArrayList<Move> cand) {
		cand.clear();
		int minDeltaC = Integer.MAX_VALUE;
		int minDeltaF = Integer.MAX_VALUE;

		for (int i = 0; i < X.length; i++) {
			for (int v = X[i].getMinValue(); v <= X[i].getMaxValue(); v++) {
				int deltaC = c.getAssignDelta(X[i], v);
				int deltaF = f.getAssignDelta(X[i], v);
				if (deltaC < 0 || (deltaC == 0 && deltaF < 0)) {
					if (deltaC < minDeltaC || (deltaC == minDeltaC && deltaF < minDeltaF)) {
						cand.clear();
						cand.add(new Move(i, v, 0));
						minDeltaC = deltaC;
						minDeltaF = deltaF;
					} else if (deltaC == minDeltaC && deltaF == minDeltaF) {
						cand.add(new Move(i, v, 0));
					}
				}
			}
		}
	}

	public void explorerNeighborhoodMaintainConstraintMinimizeFunction(ArrayList<Move> cand) {
		cand.clear();
		int minDeltaF = Integer.MAX_VALUE;
		int minDeltaC = Integer.MAX_VALUE;
		for (int i = 0; i < X.length; i++) {
			for (int v = X[i].getMinValue(); v <= X[i].getMaxValue(); v++)
				if (v != X[i].getValue()) {
					int deltaC = c.getAssignDelta(X[i], v);
					int deltaF = f.getAssignDelta(X[i], v);
					if (c.violations() > 0) {
						if (deltaC < 0) {
							if (deltaC < minDeltaC) {
								cand.clear();
								cand.add(new Move(i, v, 0));
								minDeltaC = deltaC;
							} else if (deltaC == minDeltaC) {
								cand.add(new Move(i, v, 0));
							}
						}
					} else if (deltaC == 0) {
						if (deltaF < 0) {
							if (deltaF < minDeltaF) {
								cand.clear();
								cand.add(new Move(i, v, 0));
								minDeltaF = deltaF;
							} else if (deltaF == minDeltaF) {
								cand.add(new Move(i, v, 0));
							}
						}
					}
				}
		}

		for (int i = 0; i < X.length; i++) {
			for (int j = i + 1; j < X.length; j++) {
				int deltaC = c.getSwapDelta(X[i], X[j]);
				int deltaF = f.getSwapDelta(X[i], X[j]);

				if (c.violations() > 0) {
					if (deltaC < 0) {
						if (deltaC < minDeltaC) {
							cand.clear();
							cand.add(new Move(i, j, 1));
							minDeltaC = deltaC;
						} else if (deltaC == minDeltaC) {
							cand.add(new Move(i, j, 1));
						}
					}
				} else if (deltaC == 0) {
					if (deltaF < 0) {
						if (deltaF < minDeltaF) {
							cand.clear();
							cand.add(new Move(i, j, 1));
							minDeltaF = deltaF;
						} else if (deltaF == minDeltaF) {
							cand.add(new Move(i, j, 1));
						}
					}
				}
			}
		}
	}

	public void explorerNeighborhood(ArrayList<Move> cand) {
		cand.clear();
		int minDelta = Integer.MAX_VALUE;

		for (int i = 0; i < X.length; i++) {
			for (int v = X[i].getMinValue(); v <= X[i].getMaxValue(); v++)
				if (v != X[i].getValue()) {
					int delta = c.getAssignDelta(X[i], v);
					if (delta < minDelta) {
						minDelta = delta;
						cand.clear();
						cand.add(new Move(i, v, 0));
					} else if (delta == minDelta) {
						cand.add(new Move(i, v, 0));
					}
				}
		}
	}

	public void search(int maxIter) {
		int it = 0;
		ArrayList<Move> cand = new ArrayList<>();
		while (it < maxIter) {
			//explorerNeighborhoodMaintainConstraintMinimizeFunction(cand);
			explorerNeighborhoodConstraintFunction(cand);

			if (cand.size() == 0) {
				System.out.println("Reach local optimum");
				// print result
				for (int i = 0; i < X.length; i++) {
					System.out.println("X[" + i + "]: " + X[i].getValue());
				}
				break;
			}

			Move m = cand.get(R.nextInt(cand.size()));
			if (m.t == 0)
				X[m.i].setValuePropagate(m.v);
			else
				X[m.i].swapValuePropagate(X[m.v]);

			System.out
					.println("Step " + it + ", violations = " + c.violations() + ", function value = " + f.getValue());
			it++;
		}
	}

	public static void main(String[] args) {
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] X = new VarIntLS[6];
		for (int i = 0; i <= 5; i++)
			X[i] = new VarIntLS(mgr, 1, 10);
		ConstraintSystem S = new ConstraintSystem(mgr);
		S.post(new AllDifferent(X));
		IFunction f = new FuncPlus(new FuncMult(X[0], 3), new FuncMult(X[5], 5));

		mgr.close();

		HillClimbingSearchConstraintObjectiveFunction searcher = new HillClimbingSearchConstraintObjectiveFunction(S, f,
				X);
		searcher.search(10000);
	}

}
