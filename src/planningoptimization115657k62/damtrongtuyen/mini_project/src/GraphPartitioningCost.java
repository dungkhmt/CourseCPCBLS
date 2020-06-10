package planningoptimization115657k62.damtrongtuyen.mini_project.src;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class GraphPartitioningCost extends AbstractInvariant implements IFunction {
	private LocalSearchManager mgr;
	private VarIntLS[] x;
	private int[][] c;
	private int N; // number of nodes: 0,1,2,...,N-1
	private int value;
	private int minValue;
	private int maxValue;
	private HashMap<VarIntLS, Integer> map;
	private HashSet<Edge>[] A;// A[v] is set of adjacent edges of node v
	
	class Edge{
		int node;
		int w;
		public Edge(int node, int w){
			this.node = node; this.w = w;
		}
	}
	public GraphPartitioningCost(int[][] c, VarIntLS[] x){
		// c cost matrix, (u,v) not an edge -> c[u][v] = 0
		// x: decision variable 0-1
		// check if x is []
		mgr = x[0].getLocalSearchManager();
		this.x = x;
		N = x.length;
		this.c = c;
		map = new HashMap<VarIntLS, Integer>();
		for(int i = 0; i < N; i++) map.put(x[i],i);
		minValue = 0;
		maxValue = 0;
		for(int i = 0; i < N; i++)
			for(int j = 0; j < N; j++)
				maxValue += c[i][j];
		A = new HashSet[N];
		for(int v = 0; v < N; v++){
			A[v] = new HashSet<Edge>();
			for(int u = 0; u < N; u++) if(c[u][v] > 0){
				A[v].add(new Edge(u,c[u][v]));
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
	public void propagateInt(VarIntLS z, int val) {
		// TODO Auto-generated method stub
		int old = z.getOldValue();
		if(map.get(z) == null) return;
		if(z.getValue() == old) return;
		int u = map.get(z);// get corresponding node
		for(Edge e: A[u]){
			int v = e.node;
			if(x[u].getValue() == x[v].getValue()){
				value -= e.w;
			}else{
				value += e.w;
			}
		}
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		// initialize data structures of the component
		System.out.println("GraphPartitioningCost::initPropagate");
		value = 0;
		for(int i = 0; i < N; i++){
			for(int j = i+1; j < N; j++){
				if(x[i].getValue() != x[j].getValue() && c[i][j] > 0)
					value += c[i][j];
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
		return value;// current value of the function
	}

	@Override
	public int getAssignDelta(VarIntLS z, int val) {
		// TODO Auto-generated method stub
		if(map.get(z) == null) return 0;
		int u = map.get(z);// get corresponding node
		if(x[u].getValue() == val) return 0;
		int delta = 0;
		for(Edge e: A[u]){
			int v = e.node;
			if(x[u].getValue() == x[v].getValue()){
				delta += e.w;
			}else{
				delta -= e.w;
			}
		}
		return delta;
	}

	@Override
	public int getSwapDelta(VarIntLS z, VarIntLS y) {
		// TODO Auto-generated method stub
		if(map.get(z) == null) return getAssignDelta(y,z.getValue());
		if(map.get(y) == null) return getAssignDelta(z,y.getValue());
		int nz = map.get(z);
		int ny = map.get(y);
		if(z.getValue() == y.getValue()) return 0;
		int delta = 0;
		for(Edge e: A[nz]){
			int v = e.node;
			if(v == ny) continue;
			if(x[nz].getValue() == x[v].getValue()){
				delta += e.w;
			}else{
				delta -= e.w;
			}
		}
		for(Edge e: A[ny]){
			int v = e.node;
			if(v == nz) continue;
			if(x[ny].getValue() == x[v].getValue()){
				delta += e.w;
			}else{
				delta -= e.w;
			}
		}		
		return delta;
	}

	public static void main(String[] args){
		int N = 10;
		int[][] c = new int[N][N];
		Random R = new Random();
		// random genenerate graph
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

		for(int i = 0; i < N; i++) x[i] = new VarIntLS(mgr,0,1); // biến x do mgr quản lý
		GraphPartitioningCost f = new GraphPartitioningCost(c, x);
		mgr.close();

		System.out.println("init f = " + f.getValue());
		
		int cur = f.getValue();
		int it = 0;
		while(it < 100000){
			int idx = R.nextInt(N);
			int v = 1 - x[idx].getValue();
			int d = f.getAssignDelta(x[idx], v);// chọn random một đỉnh chuyển sang cụm còn lại
			x[idx].setValuePropagate(v);// local move, assign xong phải setValuePropagate để lan truyền, tinh lại obj function, constraint

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
