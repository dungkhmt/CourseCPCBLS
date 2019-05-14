package khmtk60.miniprojects.G10.custom.constraints.basic;

import java.util.HashSet;

import khmtk60.miniprojects.G10.custom.functions.basic.FuncVarConstD;
import khmtk60.miniprojects.G10.custom.model.*;
import khmtk60.miniprojects.G10.localsearch.model.AbstractInvariant;

import khmtk60.miniprojects.G10.localsearch.model.IConstraint;
import khmtk60.miniprojects.G10.localsearch.model.LocalSearchManager;
import khmtk60.miniprojects.G10.localsearch.model.VarIntLS;

import static java.lang.Math.ceil;

public class LessOrEqualD extends AbstractInvariant implements IConstraint {

    private IFunctionD _f1;
    private IFunctionD _f2;
    private VarIntLS[] _x;
    private int _violations;
    private LocalSearchManager _ls;
    boolean _posted;
    private double _factor;

    public LessOrEqualD(IFunctionD f1, IFunctionD f2) {
        _factor=1;
        this._f1 = f1;
        this._f2 = f2;
        _ls = f1.getLocalSearchManager();
        post();
    }


    public LessOrEqualD(IFunctionD f, double val)
    {
        _factor = 1;
        _f1=f;
        _f2=new FuncVarConstD(f.getLocalSearchManager(), val);
        _ls=f.getLocalSearchManager();
        post();
    }

    public LessOrEqualD(IFunctionD f, double val, double factor)
    {
        _factor = factor;
        _f1=f;
        _f2=new FuncVarConstD(f.getLocalSearchManager(), val);
        _ls=f.getLocalSearchManager();
        post();
    }

    public LessOrEqualD(double val, IFunctionD f)
    {
        _f2=f;
        _f1=new FuncVarConstD(f.getLocalSearchManager(), val);
        _ls=f.getLocalSearchManager();
        post();
        _factor = 1;
    }

    public LessOrEqualD(double val, IFunctionD f, double factor)
    {
        _factor = factor;
        _f2=f;
        _f1=new FuncVarConstD(f.getLocalSearchManager(), val);
        _ls=f.getLocalSearchManager();
        post();
    }

    public void post() {
        HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
        VarIntLS[] x1 = _f1.getVariables();
        VarIntLS[] x2 = _f2.getVariables();
        if(x1!=null)
        {
            for (int i = 0; i < _f1.getVariables().length; i++)
                _S.add(x1[i]);
        }
        if(x2!=null)
        {
            for (int i = 0; i < _f2.getVariables().length; i++)
                _S.add(x2[i]);
        }
        _x = new VarIntLS[_S.size()];
        int i = 0;
        for (VarIntLS e : _S){
            _x[i] = e;
            i++;
        }
        _ls.post(this);
    }

    @Override
    public int violations() {
        // TODO Auto-generated method stub
        return _violations;
    }

    @Override
    public int violations(VarIntLS x) {
        if (_violations != 0)
            return (x.IsElement(_x)) ? _violations : 0;
//            return _violations;
        else
            return 0;
    }

    @Override
    public VarIntLS[] getVariables() {
        // TODO Auto-generated method stub
        return _x;
    }

    @Override
    public int getAssignDelta(VarIntLS x, int val) {
        // TODO Auto-generated method stub
//        if (!x.IsElement(_x))
//            return 0;
        double v1 = _f1.getValue() + _f1.getAssignDelta(x, val);
        double v2 = _f2.getValue() + _f2.getAssignDelta(x, val);
        int nv;
        if (v1 > v2)
            nv = (int) (ceil((v1 - v2)*_factor));
//            nv = 1;
//            nv = (int)(ceil(v1 - v2-_factor*(_f1.getValue()- _f2.getValue())));
        else nv = 0;
        return (nv - _violations);
    }

    @Override
    public int getSwapDelta(VarIntLS x, VarIntLS y) {
        // TODO Auto-generated method stub
        //if (!x.IsElement(_x))
        //return 0;
        double v1 = _f1.getValue() + _f1.getSwapDelta(x, y);
        double v2 = _f2.getValue() + _f2.getSwapDelta(x, y);
        int nv;
        if (v1 > v2)
            nv = (int) (ceil((v1 - v2)*_factor));
//            nv = 1;
//            nv = (int)(ceil(v1 - v2-_factor*(_f1.getValue()- _f2.getValue())));
        else
            nv = 0;
        return (nv - _violations);
    }

    @Override
    public void propagateInt(VarIntLS x, int val) {
        // TODO Auto-generated method stub
        if (_f1.getValue() > _f2.getValue())
            _violations = (int)(ceil((_f1.getValue() - _f2.getValue())*_factor));
//            _violations = 1;
        else
            _violations = 0;
    }

    @Override
    public void initPropagate() {
        // TODO Auto-generated method stub
        if (_f1.getValue() > _f2.getValue())
            _violations = (int)(ceil((_f1.getValue() - _f2.getValue())*_factor));//1;
//            _violations = 1;
        else
            _violations = 0;
    }

    @Override
    public LocalSearchManager getLocalSearchManager() {
        // TODO Auto-generated method stub
        return _ls;
    }

    @Override
    public boolean verify() {
        // TODO Auto-generated method stub
        return false;
    }

//    public static void main(String[] args) {
//        // TODO Auto-generated method stub
//    }
}

