package cbls115676khmt61.TranHuyHung_20164777.old;

import java.util.HashMap;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.model.IFunction;
import localsearch.model.AbstractInvariant;

public class MySum extends AbstractInvariant implements IFunction
{
    private VarIntLS[] x;
    private LocalSearchManager mgr;
    private int minValue;
    private int maxValue;
    private int value;
    private HashMap<VarIntLS, Integer> map;
    
    public MySum(final VarIntLS[] x) {
        this.x = x;
        if (x == null || x.length == 0) {
            System.out.println("x must be not null");
            return;
        }
        this.map = new HashMap<VarIntLS, Integer>();
        for (int i = 0; i < x.length; ++i) {
            this.map.put(x[i], i);
        }
        (this.mgr = x[0].getLocalSearchManager()).post((AbstractInvariant)this);
    }
    
    public VarIntLS[] getVariables() {
        return this.x;
    }
    
    public void propagateInt(final VarIntLS y, final int val) {
        if (this.map.get(y) == null) {
            return;
        }
        this.value = this.value - y.getOldValue() + y.getValue();
    }
    
    public void initPropagate() {
        this.value = 0;
        this.minValue = 0;
        this.maxValue = 0;
        for (int i = 0; i < this.x.length; ++i) {
            this.value += this.x[i].getValue();
            this.minValue += this.x[i].getMinValue();
            this.maxValue += this.x[i].getMaxValue();
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
    
    public int getAssignDelta(final VarIntLS y, final int val) {
        if (this.map.get(y) == null) {
            return 0;
        }
        return val - y.getValue();
    }
    
    public int getSwapDelta(final VarIntLS y1, final VarIntLS y2) {
        if (this.map.get(y1) == null) {
            return this.getAssignDelta(y2, y1.getValue());
        }
        if (this.map.get(y2) == null) {
            return this.getAssignDelta(y1, y2.getValue());
        }
        return 0;
    }
    
    public static void main(final String[] args) {
        final LocalSearchManager mgr = new LocalSearchManager();
        final int N = 5;
        final VarIntLS[] x = new VarIntLS[N];
        for (int i = 0; i < N; ++i) {
            (x[i] = new VarIntLS(mgr, 1, N)).setValue(i);
        }
        final MySum s = new MySum(x);
        mgr.close();
        for (int j = 0; j < N; ++j) {
            System.out.print(x[j].getValue());
        }
        System.out.println("init s = " + s.getValue());
        final int delta = s.getAssignDelta(x[2], 4);
        x[2].setValuePropagate(4);
        for (int k = 0; k < N; ++k) {
            System.out.print(x[k].getValue());
        }
        System.out.println("delta = " + delta + ", new s = " + s.getValue());
    }
}
