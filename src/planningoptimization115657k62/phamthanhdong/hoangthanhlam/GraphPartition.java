package planningoptimization115657k62.hoangthanhlam;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class GraphPartition extends AbstractInvariant implements IFunction {
	LocalSearchManager mgr;
	VarIntLS[] X;
	int[][] c;
	private int N;
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
	
	public GraphPartition(int[][] c, VarIntLS[] x) {
		// TODO Auto-generated constructor stub
		mgr = x[0].getLocalSearchManager();
		this.X = x;
		this.N = x.length;
		this.c = c;
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
		if (map.get(x) == null) return 0;
		int u = map.get(x);
		if (X[u].getValue() == val) return 0;
		int delta = 0;
		for (Edge e: A[u]){
			int v = e.node;
			if (X[u].getValue() == X[v].getValue()) {
				delta += e.w;
			} else {
				delta -= e.w;
			}
		}
		return delta;
	}
	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if (map.get(x) == null) return getAssignDelta(y, x.getValue());
		if (map.get(y) == null) return getAssignDelta(x, y.getValue());
		int nx = map.get(x);
		int ny = map.get(y);
		if (x.getValue() == y.getValue()) return 0;
		int delta = 0;
		for (Edge e: A[nx]) {
			int v = e.node;
			if (v == ny) continue;
			if (X[nx].getValue() == X[v].getValue()) {
				delta += e.w;
			} else {
				delta -= e.w;
			}
		}
		for (Edge e: A[ny]) {
			int v = e.node;
			if (v == nx) continue;
			if (X[ny].getValue() == X[v].getValue()) {
				delta += e.w;
			} else {
				delta -= e.w;
			}
		}		
		return delta;
	}
	
	public static void main(String[] args){
		int N = 10;
		int[][] c = new int[N][N];
		Random R = new Random();
		for(int i = 0; i < N; i++){
			for(int j = i; j < N; j++){
				if(i == j) c[i][j] = 0;
				else{
					int x = R.nextInt(100) + 1;
					c[i][j] = x; c[j][i] = x;
				}
			}
		}
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[N];
		for(int i = 0; i < N; i++) x[i] = new VarIntLS(mgr,0,1);
		GraphPartition f = new GraphPartition(c, x);
		mgr.close();
		System.out.println("init f = " + f.getValue());
		
		int cur = f.getValue();
		int it = 0;
		while(it < 100000){
			int idx = R.nextInt(N);
			int v = 1 - x[idx].getValue();
			int d = f.getAssignDelta(x[idx], v);
			x[idx].setValuePropagate(v);// local move
			if(cur + d != f.getValue()){
				System.out.println("BUG??????, cur = " + cur + ", delta = " + d + ", new f = " + f.getValue());
				break;
			}
			
			System.out.println("Step " + it + ", cur = " + cur + ", delta = " + d + ", new f = " + f.getValue());
			cur = f.getValue();
			it++;
		}		
	}
}
