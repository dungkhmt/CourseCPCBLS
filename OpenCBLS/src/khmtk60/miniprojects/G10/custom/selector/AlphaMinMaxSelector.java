package khmtk60.miniprojects.G10.custom.selector;

import khmtk60.miniprojects.G10.localsearch.model.IConstraint;
import khmtk60.miniprojects.G10.localsearch.model.VarIntLS;

import java.util.*;

import static java.lang.Math.ceil;

public class AlphaMinMaxSelector {
    private IConstraint _S;
    private VarIntLS[]	_vars;
    private int	_minValue;
    private int	_maxValue;
    private Random _R;
    private ArrayList<Integer> _L;

    public AlphaMinMaxSelector(IConstraint S){
        _S = S;
        _vars = _S.getVariables();

        _R = new Random();
        _L = new ArrayList<Integer>();

        _minValue = 100000000;
        _maxValue = -_minValue;
        for(int i = 0; i < _vars.length; i++){
            if(_minValue > _vars[i].getMinValue()) _minValue = _vars[i].getMinValue();
            if(_maxValue < _vars[i].getMaxValue()) _maxValue = _vars[i].getMaxValue();
        }

    }
    public VarIntLS selectViolatingVariable(double alpha){
        HashMap<Integer, ArrayList<Integer>> v2i = new HashMap();

        int sel_i = -1;
        int sel_v = 0;
        _L.clear();
        for(int i = 0; i < _vars.length; i++) {
            int v = _S.violations(_vars[i]);
            if (!v2i.containsKey(v)) {
                v2i.put(v, new ArrayList<Integer>());
            }
            v2i.get(v).add(i);
        }
        Set keySet = v2i.keySet();
        ArrayList list = new ArrayList(keySet);
        Collections.sort(list, Collections.reverseOrder());
        int vTypeLen = (int)ceil(alpha*list.size());
        vTypeLen = vTypeLen==0?1:vTypeLen;
        sel_v = (int)list.get(_R.nextInt(vTypeLen));
        _L = v2i.get(sel_v);
        sel_i = _L.get(_R.nextInt(_L.size()));
        return _vars[sel_i];
    }
    public int selectMostPromissingValue(VarIntLS x){
        int sel_v = -1;
        int minD = 10000000;
        _L.clear();
        for(int v : x.getDomain()){
            int d = _S.getAssignDelta(x, v);
            if(minD > d){
                minD = d;
                _L.clear();
                _L.add(v);
            }else if(minD == d){
                _L.add(v);
            }
        }

        sel_v = _L.get(_R.nextInt(_L.size()));
        return sel_v;
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
}
