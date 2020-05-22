package cbls115676khmt61.TranHuyHung_20164777;

import java.util.Random;
import java.util.HashSet;
import java.util.HashMap;
import localsearch.model.VarIntLS;
import localsearch.model.LocalSearchManager;
import localsearch.model.IFunction;
import localsearch.model.AbstractInvariant;

public class GraphPartitioningCost extends AbstractInvariant implements IFunction
{
	class Edge
	{
	    int node;
	    int w;
	    
	    public Edge(final int node, final int w) {
	        this.node = node;
	        this.w = w;
	    }
	}
	
    private LocalSearchManager mgr;
    private VarIntLS[] x;
    private int[][] c;
    private int N;
    private int value;
    private int minValue;
    private int maxValue;
    private HashMap<VarIntLS, Integer> map;
    private HashSet<Edge>[] A;
    
    public GraphPartitioningCost(final int[][] c, final VarIntLS[] x) {
        this.mgr = x[0].getLocalSearchManager();
        this.x = x;
        this.N = x.length;
        this.c = c;
        this.map = new HashMap<VarIntLS, Integer>();
        for (int i = 0; i < this.N; ++i) {
            this.map.put(x[i], i);
        }
        this.minValue = 0;
        this.maxValue = 0;
        for (int i = 0; i < this.N; ++i) {
            for (int j = 0; j < this.N; ++j) {
                this.maxValue += c[i][j];
            }
        }
        this.A = new HashSet[this.N];
        for (int v = 0; v < this.N; ++v) {
            this.A[v] = new HashSet<GraphPartitioningCost.Edge>();
            for (int u = 0; u < this.N; ++u) {
                if (c[u][v] > 0) {
                    this.A[v].add(new Edge(u, c[u][v]));
                }
            }
        }
        this.mgr.post((AbstractInvariant)this);
    }
    
    public void propagateInt(final VarIntLS z, final int val) {
        final int old = z.getOldValue();
        if (this.map.get(z) == null) {
            return;
        }
        if (z.getValue() == old) {
            return;
        }
        final int u = this.map.get(z);
        for (final GraphPartitioningCost.Edge e : this.A[u]) {
            final int v = e.node;
            if (this.x[u].getValue() == this.x[v].getValue()) {
                this.value -= e.w;
            }
            else {
                this.value += e.w;
            }
        }
    }
    
    public void initPropagate() {
        this.value = 0;
        for (int i = 0; i < this.N; ++i) {
            for (int j = i + 1; j < this.N; ++j) {
                if (this.x[i].getValue() != this.x[j].getValue() && this.c[i][j] > 0) {
                    this.value += this.c[i][j];
                }
            }
        }
    }
    
    public LocalSearchManager getLocalSearchManager() {
        return this.mgr;
    }
    
    public boolean verify() {
        return false;
    }
    
    public int getMinValue() {
        return this.minValue;
    }
    
    public int getMaxValue() {
        return this.maxValue;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public int getAssignDelta(final VarIntLS z, final int val) {
        if (this.map.get(z) == null) {
            return 0;
        }
        final int u = this.map.get(z);
        if (this.x[u].getValue() == val) {
            return 0;
        }
        int delta = 0;
        for (final GraphPartitioningCost.Edge e : this.A[u]) {
            final int v = e.node;
            if (this.x[u].getValue() == this.x[v].getValue()) {
                delta += e.w;
            }
            else {
                delta -= e.w;
            }
        }
        return delta;
    }
    
    public int getSwapDelta(final VarIntLS z, final VarIntLS y) {
        if (this.map.get(z) == null) {
            return this.getAssignDelta(y, z.getValue());
        }
        if (this.map.get(y) == null) {
            return this.getAssignDelta(z, y.getValue());
        }
        final int nz = this.map.get(z);
        final int ny = this.map.get(y);
        if (z.getValue() == y.getValue()) {
            return 0;
        }
        int delta = 0;
        for (final GraphPartitioningCost.Edge e : this.A[nz]) {
            final int v = e.node;
            if (this.x[nz].getValue() == this.x[v].getValue()) {
                delta += e.w;
            }
            else {
                delta -= e.w;
            }
        }
        for (final GraphPartitioningCost.Edge e : this.A[ny]) {
            final int v = e.node;
            if (this.x[ny].getValue() == this.x[v].getValue()) {
                delta += e.w;
            }
            else {
                delta -= e.w;
            }
        }
        return delta;
    }
    
    public static void main(final String[] args) {
        final int N = 10;
        final int[][] c = new int[N][N];
        final Random R = new Random();
        for (int i = 0; i < N; ++i) {
            for (int j = i; j < N; ++j) {
                if (i == j) {
                    c[i][j] = 0;
                }
                else {
                    final int x = R.nextInt(100) + 1;
                    c[i][j] = x;
                    c[j][i] = x;
                }
            }
        }
        final LocalSearchManager mgr = new LocalSearchManager();
        final VarIntLS[] x2 = new VarIntLS[N];
        for (int k = 0; k < N; ++k) {
            x2[k] = new VarIntLS(mgr, 0, 1);
        }
        final GraphPartitioningCost f = new GraphPartitioningCost(c, x2);
        mgr.close();
        System.out.println("init f = " + f.getValue());
        int cur = f.getValue();
        for (int it = 0; it < 100; ++it) {
            final int idx = R.nextInt(N);
            final int v = 1 - x2[idx].getValue();
            final int d = f.getAssignDelta(x2[idx], v);
            if (cur + d != f.getValue()) {
                System.out.println("BUG???, cur = " + cur + ", delta = " + d + ", new f = " + f.getValue());
                break;
            }
            cur = f.getValue();
            System.out.println("Step " + it + ", cur = " + cur + ", delta = " + d + ", new f = " + f.getValue());
        }
    }
}
