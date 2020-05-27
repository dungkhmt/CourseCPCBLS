package planningoptimization115657k62.hoangthanhlam;

import java.util.HashMap;
import java.util.HashSet;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class MiniProjectInterface extends AbstractInvariant implements IFunction {
	
	private LocalSearchManager mgr;
	private VarIntLS[] X;
	private VarIntLS[] Route;
	private int N;
	private int K;
	private int[][] c;
	private int[] d;
	private int value;
	private int minValue;
	private int maxValue;
	private HashMap<VarIntLS, Integer> map;
	private HashSet<Edge>[] A;
	
	class Edge {
		int node;
		int w;
		public Edge (int node, int w) {
			this.node = node; this.w = w;
		}
	}
	
	public MiniProjectInterface (int[] d, int[][] c, VarIntLS[] x) {
		// TODO Auto-generated constructor stub
		mgr = x[0].getLocalSearchManager();
		this.X = x;
		this.N = x.length;
		this.c = c;
		this.d = d;
		minValue = 0;
		maxValue = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				maxValue += c[i][j];
			}
		}
		map = new HashMap<VarIntLS, Integer>();
		A = new HashSet[N];
		for (int i = 0; i < N; i++) {
			map.put(X[i], i);
			A[i] = new HashSet<Edge>();
			for (int j = 0; j < N; j++) {
				if (c[i][j] > 0) {
					A[i].add(new Edge(j, c[i][j]));
				}
			}
		}
		mgr.post(this);
		
	}
	
	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return X;
	}
	
	@Override
	public void propagateInt(VarIntLS z, int val) {
		// TODO Auto-generated method stub
		int old = z.getOldValue();
		if (map.get(z) == null) return;
		if (z.getValue() == old) return;
		int u = map.get(z);
		for (Edge e: A[u]) {
			int v = e.node;
			if (X[u].getValue() == X[v].getValue()) {
				value -= e.w;
			} else {
				value += e.w;
			}
		}
	}
	
	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		value = 0;
		for (int i = 0; i < N; i++) {
			for (int j = i+1; j < N; j++) {
				if (X[i].getValue() != X[j].getValue() && c[i][j] > 0) {
					value += c[i][j];
				}
			}
		}
	}
	
	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return mgr;
	}
	
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMinValue() {
		// TODO Auto-generated method stub
		return minValue;
	}

	@Override
	public int getMaxValue() {
		// TODO Auto-generated method stub
		return maxValue;
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		return 0;
	}

}
