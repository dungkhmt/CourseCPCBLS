import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.util.HashMap;

public class MySum extends AbstractInvariant implements IFunction {
    private VarIntLS[] X;
    private LocalSearchManager mgr;
    private int minValue;
    private int maxValue;
    private int value;
    private HashMap<VarIntLS, Integer> map;

    public MySum(VarIntLS[] X){
        this.X = X;
        if(X == null || X.length == 0){
            System.out.println("X must be not null");
            return;
        }
        map = new HashMap<VarIntLS, Integer>();
        for(int i = 0; i < X.length; i++){

        }
        mgr = X[0].getLocalSearchManager();
        mgr.post(this);
    }

    @Override
    public VarIntLS[] getVariables() {
        return X;
    }

    @Override
    public void propagateInt(VarIntLS varIntLS, int val) {
        // ham nay duoc goi sau khi bien X da duoc gan gia tri val(val = X[i].getValue())
       if(map.get(varIntLS) == null) return;
        value = value - varIntLS.getOldValue() + varIntLS.getValue();
    }

    @Override
    public void initPropagate() {
        value = 0;
        minValue = 0;
        maxValue = 0;
        for(int i = 0; i < X.length; i++){
            value += X[i].getValue();
            minValue += X[i].getMinValue();
            maxValue += X[i].getMaxValue();
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


    @Override
    public int getMinValue() {
        return minValue;
    }

    @Override
    public int getMaxValue() {
        return maxValue;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public int getAssignDelta(VarIntLS varIntLS, int val) {
        // tra ve su thay doi value neu varIntLS duoc gan gia tri val
        if(map.get(varIntLS) == null) return 0;

        return val - varIntLS.getValue();
    }

    @Override
    public int getSwapDelta(VarIntLS varIntLS, VarIntLS varIntLS1) {
        if(map.get(varIntLS) == null) return getAssignDelta(varIntLS1, varIntLS.getValue());
        if(map.get(varIntLS1) == null) return getAssignDelta(varIntLS, varIntLS1.getValue());
        return 0;
    }

    public static void main(String[] args) {
        LocalSearchManager mgr = new LocalSearchManager();
        int N = 5;
        VarIntLS[] x = new VarIntLS[N];
        for(int i = 0; i < N; i++){
            x[i] = new VarIntLS(mgr, 1, N);     // default value of x[i] = 1
            x[i].setValue(i);
        }
        MySum s = new MySum(x);
        mgr.close();
        for(int i = 0; i < N; i++){
            System.out.print(x[i].getValue() + " ");
        }
        System.out.println("init s = " + s.getValue());
        int delta = s.getAssignDelta(x[2], 4);
        x[2].setValuePropagate(4);
        System.out.println("delta = " + delta + ", new s = " + s.getValue());
    }
}
