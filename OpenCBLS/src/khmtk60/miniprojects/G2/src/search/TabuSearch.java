package khmtk60.miniprojects.G2.src.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import khmtk60.miniprojects.G2.src.localsearch.model.IFloatConstraint;
import khmtk60.miniprojects.G2.src.localsearch.model.VarIntLS;

public class TabuSearch {
	private java.util.Random rand = null;
	private double t_best;
	private double t0;
	private double t;

	public TabuSearch() {
		this.rand = new java.util.Random();
	}

	public double getTimeBest() {
		return t_best * 0.001;
	}

	public void search(IFloatConstraint S, int tabulen, int maxTime, int maxIter, int maxStable) {
		double t0 = System.currentTimeMillis();
		VarIntLS[] x = S.getVariables();
		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < x.length; i++)
			map.put(x[i], i);

		int n = x.length;
		int maxV = -1000000000;
		int minV = 1000000000;
		System.out.println(n);
		for (int i = 0; i < n; i++) {
			if (maxV < x[i].getMaxValue()) {
				maxV = x[i].getMaxValue();
			}
			if (minV > x[i].getMinValue()) {
				minV = x[i].getMinValue();
			}
		}
		int D = maxV - minV;
		int tabu[][] = new int[n][D + 1];
		for (int i = 0; i < n; i++)
			for (int v = 0; v <= D; v++)
				tabu[i][v] = -1;

		int it = 0;
		maxTime = maxTime * 1000;
		float best = S.violations();
		int[] x_best = new int[x.length];
		for (int i = 0; i < x.length; i++) {
			x_best[i] = x[i].getValue();
		}
		System.out.println("TabuSearch, init S = " + S.violations());
		int nic = 0;
		ArrayList<OneVariableValueMove> moves = new ArrayList<OneVariableValueMove>();
		Random R = new Random();
		while (it < maxIter && System.currentTimeMillis() - t0 < maxTime && S.violations() > 0) {
			int sel_i = -1;
			int sel_v = -1;
			float minDelta = 10000000f;
			moves.clear();
			for (int i = 0; i < n; i++) {
				for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
					float delta = S.getAssignDelta(x[i], v);
					if (tabu[i][v - minV] <= it || S.violations() + delta < best) {
						if (delta < minDelta) {
							minDelta = delta;
							sel_i = i;
							sel_v = v;
							moves.clear();
							moves.add(new OneVariableValueMove(MoveType.OneVariableValueAssignment, minDelta, x[i], v));
						}
					} else if (delta == minDelta) {
						moves.add(new OneVariableValueMove(MoveType.OneVariableValueAssignment, minDelta, x[i], v));
					}
				}
			}
			if (moves.size() <= 0) {
				System.out.println("TabuSearch::restart.....");
				restartMaintainConstraint(x, S, tabu);
				if (S.violations() == 0) {
					best = S.violations();
					for (int i = 0; i < x.length; i++)
						x_best[i] = x[i].getValue();
				}
				nic = 0;
			} else {
				OneVariableValueMove m = moves.get(R.nextInt(moves.size()));
				sel_i = map.get(m.getVariable());
				sel_v = m.getValue();
				x[sel_i].setValuePropagate(sel_v);
				tabu[sel_i][sel_v - minV] = it + tabulen;
				System.out.println("Step " + it + ", S = " + S.violations() + ", best = " + best + ", delta = "
						+ minDelta + ", nic = " + nic);
				if (S.violations() < best) {
					best = S.violations();
					for (int i = 0; i < x.length; i++)
						x_best[i] = x[i].getValue();
					updateBest();
					t_best = System.currentTimeMillis() - t0;
				}
				if (S.violations() >= 0) {
					nic++;
					if (nic > maxStable) {
						System.out.println("TabuSearch::restart.....");
						restartMaintainConstraint(x, S, tabu);
						nic = 0;
					}
				} else {
					nic = 0;
				}
			}
			it++;
		}
		for (int i = 0; i < x.length; i++)
			x[i].setValuePropagate(x_best[i]);
	}

	public void updateBest() {

	}

	private void restartMaintainConstraint(VarIntLS[] x, IFloatConstraint S, int[][] tabu) {

		for (int i = 0; i < x.length; i++) {
			java.util.ArrayList<Integer> L = new java.util.ArrayList<Integer>();
			for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
				if (S.getAssignDelta(x[i], v) <= 0)
					L.add(v);
			}
			int idx = rand.nextInt(L.size());
			int v = L.get(idx);
			x[i].setValuePropagate(v);
		}
		for (int i = 0; i < tabu.length; i++) {
			for (int j = 0; j < tabu[i].length; j++)
				tabu[i][j] = -1;
		}

	}

}
