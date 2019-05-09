package custom.functions.conditionalsum;

import custom.model.IFunctionD;

import localsearch.model.AbstractInvariant;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.util.HashMap;
import java.util.HashSet;

public class ConditionalSumVarIntD extends AbstractInvariant implements IFunctionD {
    private double _value;
    private double _minValue;
    private double _maxValue;
    private int _val;


    private double[]  _w;
    private VarIntLS[] _x;

    private LocalSearchManager _ls;
    private HashMap<VarIntLS, Integer> _map;
    private boolean _posted;

    public ConditionalSumVarIntD(VarIntLS[] x, double[] w, int val) {
        _x=x;
        _w=w;
        _val=val;
        _ls=x[0].getLocalSearchManager();
        _posted = false;
        post();
    }

    private void post() {
        if(_posted) return;
        _posted = true;

        HashSet<VarIntLS> _S=new HashSet<VarIntLS>();
        for(int i=0;i<_x.length;i++)
        {
            _S.add(_x[i]);
        }
        _map=new HashMap<VarIntLS, Integer>();
        for(int i=0;i<_x.length;i++)
        {
            _map.put(_x[i],i);
        }

        _minValue = 0;
        _maxValue = 0;
        for(int i = 0; i < _w.length; i++)
            _maxValue = _maxValue + _w[i];

        _ls.post(this);

    }


    @Override
    public double getMinValue() {
        // TODO Auto-generated method stub
        return _minValue;
    }

    @Override
    public double getMaxValue() {
        // TODO Auto-generated method stub
        return _maxValue;
    }

    @Override
    public double getValue() {
        // TODO Auto-generated method stub
        return _value;
    }

    @Override
    public VarIntLS[] getVariables() {
        return _x;
    }

    @Override
    public double getAssignDelta(VarIntLS x, int val) {
        //if(!(x.IsElement(_x))) return 0;
        if(_map.get(x) == null) return 0;
        double nv=_value;
        int k=_map.get(x);
        if(x.getValue()==_val)
        {
            if(_val==val)
            {
                nv=nv;
            }
            else
            {
                nv=nv-_w[k];
            }
        }
        else
        {
            if(_val==val)
            {
                nv=nv+_w[k];
            }
            else
            {
                nv=nv;
            }
        }

        return nv-_value;
    }

    @Override
    public double getSwapDelta(VarIntLS x, VarIntLS y) {
        // TODO Auto-generated method stub
        //if(!(x.IsElement(_x))&&!(y.IsElement(_x))) return 0;
        //if((x.IsElement(_x))&&!(y.IsElement(_x))) return getAssignDelta(x,y.getValue());
        //if(!(x.IsElement(_x))&&(y.IsElement(_x))) return getAssignDelta(y, x.getValue());
        if(_map.get(x) == null&&_map.get(y) == null) return 0;
        if(_map.get(x) != null&&_map.get(y) == null) return getAssignDelta(x,y.getValue());
        if(_map.get(x) == null&&_map.get(y) != null) return getAssignDelta(y, x.getValue());

        double nv=_value;
        int k1=_map.get(x);
        int k2=_map.get(y);
        if(x.getValue()==_val&&y.getValue()==_val)
        {
            nv=nv;
        }
        else if(x.getValue()==_val&&y.getValue()!=_val)
        {
            nv=nv-_w[k1]+_w[k2];
        }
        else
        if(x.getValue()!=_val&&y.getValue()==_val)
        {
            nv=nv-_w[k2]+_w[k1];
        }
        else if(x.getValue()!=_val&&y.getValue()!=_val)
        {
            nv=nv;
        }
        return nv-_value;
    }

    @Override
    public void propagateInt(VarIntLS x, int val) {
        //if(!(x.IsElement(_x))) return ;
        if(_map.get(x) == null) return ;
        double nv=_value;
        int t=x.getOldValue();

        int k=_map.get(x);
        if(t==_val)
        {
            if(x.getValue()==_val)
            {
                nv=nv;
            }
            else
            {
                nv=nv-_w[k];
            }
        }
        else
        {
            if(x.getValue()==_val)
            {
                nv=nv+_w[k];
            }
            else
            {
                nv=nv;
            }
        }

        _value=nv;
    }

    @Override
    public void initPropagate() {
        double tong=0;
        for(int i=0;i<_x.length;i++)
        {
            if(_val==_x[i].getValue())
            {
                tong+=_w[i];
            }
        }
        _value=tong;
    }

    @Override
    public boolean verify() {
        // TODO Auto-generated method stub
        double nv=0;
        for(int i=0;i<_x.length;i++)
        {
            if(_x[i].getValue()==_val)
            {
                nv+=_w[i];
            }
        }
        if(nv==_value)
            return true;
        else
            System.out.println(name() + "::verify --> failed, _value = " + _value + " which differs from new value = " + nv + " by recomputation");
        return false;
    }

    @Override
    public LocalSearchManager getLocalSearchManager() {
        // TODO Auto-generated method stub
        return _ls;
    }

}
