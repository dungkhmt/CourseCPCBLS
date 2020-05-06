package cbls115676khmt61.HuyLQ_20161813;

import java.util.HashMap;
import java.util.HashSet;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class GraphPartitioningCost extends AbstractInvariant implements IFunction  {
	int N;
	VarIntLS[] x;
	int[][] c;
	LocalSearchManager mgr;
	HashMap<VarIntLS, Integer> map;
	private HashSet<Edge>[] A; // A[v] la danh sach canh ke voi dinh v
	int value, maxValue, minValue;
	class Edge {
		public Edge(int u, int w) {
			this.node = u;
			this.w = w;
		}
		int node;
		int w;
	}
	
	@SuppressWarnings("unchecked")
	public GraphPartitioningCost(int[][] c, VarIntLS x[]) {
		// x[i] = 0 || 1
		// u v khong la canh => c[u][v] = 0
		// ham tra ve tong trong so cac canh ma 2 dau mut thuoc 2 tap khac nhau
		mgr = x[0].getLocalSearchManager();
		N = x.length;
		this.x = x;
		this.c = c;
		map  = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < x.length; i++) {
			map.put(x[i], i);
		}
		minValue = 0;
		maxValue = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				maxValue += c[i][j];
			}
		}
		A = new HashSet[N];
		for (int v = 0; v < N; v++) {
			A[v] = new HashSet<Edge>();
			for (int u = 0; u < N; u++) {
				if (c[u][v] > 0) {
					A[v].add(new Edge(u, c[u][v]));
				}
			}
		}
		mgr.post(this);
		
	}
    
	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return x;
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
	public int getAssignDelta(VarIntLS z, int val) {
		// TODO Auto-generated method stub
		if (map.get(z) == null) {
			return 0;
		}
		int u = map.get(z);
		if (x[u].getValue() == val) return 0;
		int delta = 0;
		for (Edge e: A[u]) {
			int v = e.node;
			if (x[u].getValue() == x[v].getValue()) {
				delta += e.w;
			}
			else {
				delta -= e.w;
			}
		}
		return delta;
	}

	@Override
	public int getSwapDelta(VarIntLS z, VarIntLS y) {
		// TODO Auto-generated method stub
		if(map.get(z) == null) return getAssignDelta(z, y.getValue());
		if(map.get(y) == null) return getAssignDelta(y, z.getValue());
		int nz = map.get(z);
		int ny = map.get(y);
		if (z.getValue() == y.getValue()) {
			return 0;
		}
		int delta = 0;
		for (Edge e : A[nz]) {
			int v = e.node;
			if (v == ny) continue;
			if (x[nz].getValue() == x[v].getValue()) {
				delta += e.w;
			}
			else {
				delta -= e.w;
			}
		}
		for (Edge e : A[ny]) {
			int v = e.node;
			if (v == nz) continue;
			if (x[ny].getValue() == x[v].getValue()) {
				delta += e.w;
			}
			else {
				delta -= e.w;
			}
		}
		return delta;
	}
	
	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		// initialize data structures of the component
		value = 0;
		for(int i = 0; i < N; i++){
			for(int j = i+1; j < N; j++){
				if(x[i].getValue() != x[j].getValue() && c[i][j] > 0)
					value += c[i][j];
			}
		}
	}
	
	@Override
	public void propagateInt(VarIntLS z, int val) {
		// TODO Auto-generated method stub
		int old = z.getOldValue();
		if(map.get(z) == null) return;
		if(z.getValue() == old) return;
		int u = map.get(z);// get corresponding node
		for(Edge e: A[u]){
			int v = e.node;
			if(x[u].getValue() == x[v].getValue()){
				value += e.w;
			}else{
				value -= e.w;
			}
		}
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		return mgr;
	}

	@Override
	public boolean verify() {
		return false;
	}

	
	
}

