package custom.functions.uniquecount;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.util.*;

public class ConditionalUniqueCount extends AbstractInvariant implements IFunction {

    private int _value;
    private int _minValue;
    private int _maxValue;
    private int _val;
    private VarIntLS[] _x;

    private LocalSearchManager _ls;
    private HashMap<VarIntLS, Integer> _map;
    private boolean _posted;

    private int[] _ref;
    private HashMap<Integer, Integer> _v2i;
    private int[] _cnt;
//    private int[] _range;


    public ConditionalUniqueCount(VarIntLS[] x, int[] ref, int val) {
        _x = x;
        _ref = ref;
        _val = val;
        _ls = x[0].getLocalSearchManager();
        _posted = false;

        post();
    }

    private void post() {
        if (_posted) return;
        _posted = true;

        HashSet<Integer> _rval = new HashSet<Integer>();
        for (int i = 0; i < _ref.length; i++) {
            _rval.add(_ref[i]);
        }
        _v2i = new HashMap<>();
        Iterator iter = _rval.iterator();
        int count=0;
        while(iter.hasNext()){
            _v2i.put((int)iter.next(), count);
            count++;
        }
        _map = new HashMap<VarIntLS, Integer>();
        for (int i = 0; i < _x.length; i++) {
            _map.put(_x[i], i);
        }
//        _range = getMinMaxValue(_ref);
        _cnt = new int[_v2i.size()];

        _minValue = 0;
        _maxValue = _v2i.size();

        _ls.post(this);

    }

//    private int[] getMinMaxValue(int[] arr) {
//        IntSummaryStatistics stat = Arrays.stream(arr).summaryStatistics();
//        int min = stat.getMin();
//        int max = stat.getMax();
//        return new int[]{min, max};
//    }

    private int uniqueCount() {
        int nv = 0;
        for (int v : _cnt) {
            if (v > 0) nv++;
        }
        return nv;
    }

    @Override
    public VarIntLS[] getVariables() {
        return _x;
    }

    @Override
    public int getMinValue() {
        return _minValue;
    }

    @Override
    public int getMaxValue() {
        return _maxValue;
    }

    @Override
    public int getValue() {
        return _value;
    }

    @Override
    public int getAssignDelta(VarIntLS x, int val) {

//        if (!x.getDomain().contains(val)) return 1000000;
        if (_map.get(x) == null) return 0;
        int nv = _value;
        int k = _map.get(x);
        if (x.getValue() == _val) {
            if (_val == val) {
                nv = nv;
            } else {
                _cnt[_v2i.get(_ref[k])]--;
                nv = uniqueCount();
                _cnt[_v2i.get(_ref[k])]++;
            }
        } else {
            if (_val == val) {
                _cnt[_v2i.get(_ref[k])]++;
                nv = uniqueCount();
                _cnt[_v2i.get(_ref[k])]--;
            }
            else {
                nv = nv;
            }
        }
        return nv - _value;
    }

    @Override
    public int getSwapDelta(VarIntLS x, VarIntLS y) {
        // TODO Auto-generated method stub
        //if(!(x.IsElement(_x))&&!(y.IsElement(_x))) return 0;
        //if((x.IsElement(_x))&&!(y.IsElement(_x))) return getAssignDelta(x,y.getValue());
        //if(!(x.IsElement(_x))&&(y.IsElement(_x))) return getAssignDelta(y, x.getValue());
//        if (!x.getDomain().contains(y.getValue()) || !y.getDomain().contains(x.getValue())) return 1000000;
        if (_map.get(x) == null && _map.get(y) == null) return 0;
        if (_map.get(x) != null && _map.get(y) == null) return getAssignDelta(x, y.getValue());
        if (_map.get(x) == null && _map.get(y) != null) return getAssignDelta(y, x.getValue());

        int nv=_value;
        int k1=_map.get(x);
        int k2=_map.get(y);
        if(x.getValue()==_val&&y.getValue()==_val)
        {
            nv=nv;
        }
        else if(x.getValue()==_val&&y.getValue()!=_val)
        {
            _cnt[_v2i.get(_ref[k1])]--;
            _cnt[_v2i.get(_ref[k2])]++;
            nv=uniqueCount();
            _cnt[_v2i.get(_ref[k1])]++;
            _cnt[_v2i.get(_ref[k2])]--;
        }
        else
        if(x.getValue()!=_val&&y.getValue()==_val)
        {
            _cnt[_v2i.get(_ref[k1])]++;
            _cnt[_v2i.get(_ref[k2])]--;
            nv=uniqueCount();
            _cnt[_v2i.get(_ref[k1])]--;
            _cnt[_v2i.get(_ref[k2])]++;
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
        int nv=_value;
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
                _cnt[_v2i.get(_ref[k])]--;
                nv=uniqueCount();
            }
        }
        else
        {
            if(x.getValue()==_val)
            {
                _cnt[_v2i.get(_ref[k])]++;
                nv=uniqueCount();
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
        for (int i=0; i<_x.length; ++i) {
            if(_x[i].getValue() == _val)
                _cnt[_v2i.get(_ref[i])]++;
        }
        _value=uniqueCount();
    }

    @Override
    public boolean verify() {
        // TODO Auto-generated method stub
        int[] _cnt_2 = new int[_v2i.size()];
        for (int i=0; i<_x.length; ++i) {
            if(_x[i].getValue() == _val)
                _cnt_2[_v2i.get(_ref[i])]++;
        }
        int nv = 0;
        for (int v : _cnt_2) {
            if (v > 0) nv++;
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
