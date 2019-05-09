package custom.functions.basic;

import java.util.HashSet;

import custom.model.IFunctionD;
import localsearch.model.*;

public class FuncVarConstD extends AbstractInvariant implements IFunctionD {

    private double _value;
    private double _minValue;
    private double _maxValue;
    private VarIntLS _x;
    //private HashSet<VarIntLS>    _S;
    private LocalSearchManager _ls;
    private boolean IsConstant;

    public FuncVarConstD(VarIntLS x) {
        _ls = x.getLocalSearchManager();
        _minValue = x.getMinValue();
        _maxValue = x.getMaxValue();
        _value = x.getValue();
        _x = x;
        IsConstant=false;
        _ls.post(this);
    }

    public FuncVarConstD(LocalSearchManager ls, double val){

        _minValue = val;
        _maxValue = val;
        _value = val;
        IsConstant=true;
        _ls = ls;
        _ls.post(this);




    }

    @SuppressWarnings("unused")
    private void post() {
    }

    @Override
    public double getMinValue() {
        return _minValue;
    }

    @Override
    public double getMaxValue() {
        return _maxValue;
    }

    @Override
    public double getValue() {
        return _value;
    }

    @Override
    public VarIntLS[] getVariables() {
        if(IsConstant) return null;
        VarIntLS _X[] = new VarIntLS[1];
        _X[0] = _x;
        return _X;
    }

    @Override
    public double getAssignDelta(VarIntLS x, int val) {
        if(IsConstant) return 0;
        if (_x != x ) return 0;
        return val - _value;
    }

    @Override
    public double getSwapDelta(VarIntLS x, VarIntLS y) {
        // TODO Auto-generated method stub
        if(IsConstant) return 0;
        if (_x == y)
            return (x.getValue() - y.getValue());
        else
        if (_x == x)
            return y.getValue() - _value;
        else
            return 0;
    }

    @Override
    public boolean verify() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void propagateInt(VarIntLS x, int val) {
        if (_x != x) return;
        _value = val;
    }

    @Override
    public void initPropagate() {
        if (_x != null)
            _value = _x.getValue();
    }

    @Override
    public LocalSearchManager getLocalSearchManager() {
        // TODO Auto-generated method stub
        return _ls;
    }

    public String name(){
        return "FuncVarConst";
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stubs
        LocalSearchManager _ls = new LocalSearchManager();
        VarIntLS x = new VarIntLS(_ls,-500,500);
        VarIntLS y = new VarIntLS(_ls,-500,500);
        FuncVarConstD fy = new FuncVarConstD(y);
        x.setValue(10);
        y.setValue(15);
        _ls.close();
        System.out.println(fy.getValue());
    }
}


