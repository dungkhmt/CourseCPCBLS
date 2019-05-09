package khmtk60.miniprojects.G1.newlocalsearch.functions.conditionalsum;

import java.util.HashMap;

import khmtk60.miniprojects.G1.newlocalsearch.model.CustomIFloatFunction;
import khmtk60.miniprojects.G1.newlocalsearch.model.CustomVarIntLS;

public class CustomConditionalSumVarInt implements CustomIFloatFunction{
	private double _value;
	private double _minValue;
	private double _maxValue;
	private int _val;
	
	
	private double[]  _w;
	private CustomVarIntLS[] _x;
	private HashMap<CustomVarIntLS, Integer> _map;
	private boolean _posted;
	
	public CustomConditionalSumVarInt(CustomVarIntLS[] x, double[] w, int val) {
		// TODO Auto-generated constructor stub
		_x=x;
		_w=w;
		_val=val;
		_posted = false;
		post();
	}
	
	public CustomConditionalSumVarInt(CustomVarIntLS[] x, double[] w, int val, HashMap<CustomVarIntLS, Integer> map) {
		// TODO Auto-generated constructor stub
		_x=x;
		_w=w;
		_val=val;
		_posted = false;
		_map = map;
		post();
	}
	
	private void post() {
		if(_posted) return;
		_posted = true;
		
		_minValue = 0;
		_maxValue = 0;
		for(int i = 0; i < _w.length; i++)
			_maxValue = _maxValue + _w[i];
		
//		_ls.post(this);
 		
	}
	
	public double getMinValue() {
		// TODO Auto-generated method stub
		return _minValue;
	}

	public double getMaxValue() {
		// TODO Auto-generated method stub
		return _maxValue;
	}

	public double getValue() {
		// TODO Auto-generated method stub
		return _value;
	}

	public CustomVarIntLS[] getVariables() {
		return _x;
	}
	
	public double getAssignDelta(CustomVarIntLS x, int val) {
		// TODO Auto-generated method stub
		if(_map.get(x) == null) return 0;
		double nv=_value;
		int k=_map.get(x);
		if(x.getValue()==_val) {
			if(_val!=val){
				nv = nv - _w[k];
			}
		}
		else {
			if(_val==val) {
				nv = nv + _w[k];
			} 
		}		
		return nv - _value;
	}

	public double getSwapDelta(CustomVarIntLS x, CustomVarIntLS y) {
		// TODO Auto-generated method stub
		if(_map.get(x) == null && _map.get(y) == null) return 0;
		if(_map.get(x) != null && _map.get(y) == null) return getAssignDelta(x, y.getValue());
		if(_map.get(x) == null && _map.get(y) != null) return getAssignDelta(y, x.getValue());
		
		double nv = _value;
		int k1=_map.get(x);
		int k2=_map.get(y);
		if(x.getValue()==_val && y.getValue()==_val) {
			
		} else if(x.getValue() == _val && y.getValue() != _val) {
			nv = nv - _w[k1] + _w[k2]; 
		} else
			if(x.getValue()!=_val&&y.getValue()==_val) {
				nv = nv - _w[k2] + _w[k1];
			} 
		return nv - _value;
	}

	public void propagateInt(CustomVarIntLS x, int val) {
		//if(!(x.IsElement(_x))) return ;
		if(_map.get(x) == null) return ;
		double nv = _value;
		int t = x.getOldValue();
		
		int k = _map.get(x);
		if(t==_val) {
			if(x.getValue() == _val) {
			} else {
				nv = nv-_w[k];
			}
		}
		else {
			if(x.getValue()==_val) {
				nv = nv+_w[k];
			} else {
			}
		}
		_value = nv;
	}
	
	public void initPropagate() {
		double tong = 0.;
		for(int i=0;i<_x.length;i++){
			if(_val == _x[i].getValue()) {
				tong += _w[i];
			}
		}
		_value = tong;
	}

	public boolean verify() {
		// TODO Auto-generated method stub
		double nv=0;
		for(int i=0; i<_x.length; i++)
		{
			if(_x[i].getValue() == _val)
			{
				nv += _w[i];
			}
		}
		if(nv == _value)
			return true;
		else
		return false;
	}
	
	public void resetValue() {
		_value = 0.;
	}
}
