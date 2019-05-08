package khmtk60.miniprojects.G17;

import java.util.HashMap;
import java.util.HashSet;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class CountDistinct extends AbstractInvariant implements
IFunction {
	
	private int _value;
	private int _minValue;
	private int _maxValue;
	private int _val;
	
	private int[] _w;
	private VarIntLS[] _x;
	
	private HashMap<VarIntLS, Integer> _map;
	private HashMap<Integer, Integer> _sc;
	
	private LocalSearchManager _ls;
	private boolean _posted;
	
	public CountDistinct(VarIntLS[] x, int[] w, int val) {
		_x = x;
		_w = w;
		_val = val;
		_ls = x[0].getLocalSearchManager();
		_posted = false;
		post();
	}
	
	private void addValue(int k) {
		if(_sc.get(k) != null) 
			_sc.put(k, _sc.get(k) + 1);
		else 
			_sc.put(k, 1);
	}
	
	private void removeValue(int k) {
		if (_sc.get(k) == 1)
			_sc.remove(k);
		else 
			_sc.put(k, _sc.get(k) - 1);
	}
	
	private void post() {
		if(_posted) return;
		_posted = true;
		
		_map = new HashMap<VarIntLS, Integer>();
		_sc = new HashMap<Integer, Integer>();
		
		for(int i=0; i< _x.length; ++i) {
			if(_x[i].getValue() == _val)
				addValue(_w[i]);
			_map.put(_x[i], i);
		}
		
		_minValue = 0;
		_maxValue = 0;
		
		HashSet<Integer> hs= new HashSet<Integer>();
		
		for(int i=0; i < _x.length; i++)
			hs.add(_w[i]);
		
		_maxValue = hs.size();
		
		_ls.post(this);
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
	public VarIntLS[] getVariables() {
		return _x;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		if(_map.get(x) == null) return 0;
		int nv = _value;
		int k=_map.get(x);
		
		if(x.getValue()==_val) {
			if(_val != val) {
				removeValue(_w[k]);
				nv = _sc.size();
				addValue(_w[k]);
			}
		} 
		else
			if(_val==val) {
				addValue(_w[k]);
				nv = _sc.size();
				removeValue(_w[k]);
			}
		
		return nv-_value;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		if(_map.get(x) == null&&_map.get(y) == null) return 0;
		if(_map.get(x) != null&&_map.get(y) == null) return getAssignDelta(x, y.getValue());
		if(_map.get(x) == null&&_map.get(y) != null) return getAssignDelta(y, x.getValue());
		
		int nv=_value;
		int k1=_map.get(x);
		int k2=_map.get(y);
		
		if(x.getValue()==_val && y.getValue()==_val) {
		} 
		else 
			if(x.getValue()==_val && y.getValue()!=_val) {
				removeValue(_w[k1]);
				addValue(_w[k2]);
				nv = _sc.size();
				addValue(_w[k1]);
				removeValue(_w[k2]);
			} 
			else 
				if(x.getValue()!=_val && y.getValue()==_val) {
					addValue(_w[k1]);
					removeValue(_w[k2]);
					nv = _sc.size();
					removeValue(_w[k1]);
					addValue(_w[k2]);
				} 
				else 
					if(x.getValue()!=_val && y.getValue()!=_val) {
					}
		
		return nv-_value;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		if(_map.get(x) == null) return ;
		int nv=_value;
		int t=x.getOldValue();
		
		int k=_map.get(x);
		
		if(t==_val) {
			if(x.getValue() != _val) {
				removeValue(_w[k]);
				nv = _sc.size();
			}
		} else {
			if(x.getValue()==_val) {
				addValue(_w[k]);
				nv = _sc.size();
			}
		}
			
		_value=nv;
	}
	
	@Override
	public void initPropagate() {
		_sc.clear();
		for(int i=0; i<_x.length; i++)
			if(_val==_x[i].getValue())
				addValue(_w[i]);
		_value= _sc.size();
	}
	
	@Override
	public boolean verify() {
		HashMap<Integer, Integer> sc = new HashMap<Integer, Integer>();
		
		for(int i = 0; i < _x.length; i++) 
			if(_x[i].getValue()==_val)
				sc.compute(_w[i], (key, val) -> ((val == null) ? 1 : val+1));
		int nv = sc.size();
		if(nv==_value)
			return true;
		else
			System.out.println(name() + "::verify --> failed, _value = " + _value + " which differs from new value = " + nv + " by recomputation");
		return false;
	}
	
	@Override
	public LocalSearchManager getLocalSearchManager() {
		return _ls;
	}

	public static void main(String[] args) {
		
	}
}
