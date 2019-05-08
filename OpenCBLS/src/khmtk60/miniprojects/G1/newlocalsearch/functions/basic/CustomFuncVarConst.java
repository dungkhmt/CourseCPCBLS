package khmtk60.miniprojects.G1.newlocalsearch.functions.basic;

import khmtk60.miniprojects.G1.newlocalsearch.model.CustomIFloatFunction;
import khmtk60.miniprojects.G1.newlocalsearch.model.CustomVarIntLS;

public class CustomFuncVarConst implements CustomIFloatFunction{
	private double _value;
	private double _minValue;
	private double _maxValue;
	private CustomVarIntLS _x;
	private boolean IsConstant;	
	
	public CustomFuncVarConst(CustomVarIntLS x) {
		_minValue = x.getMinValue();
		_maxValue = x.getMaxValue();
		_value = x.getValue();
		_x = x;		
		IsConstant=false;
	}
	
	public CustomFuncVarConst(double val){		
		_minValue = val;
		_maxValue = val;
		_value = val;
		IsConstant=true;
	}
	
	public CustomVarIntLS[] getVariables() {
		if(IsConstant) return null;
		CustomVarIntLS _X[] = new CustomVarIntLS[1];
		_X[0] = _x;
		return _X;
	}

	public void propagateInt(CustomVarIntLS x, int val) {
		if (_x != x) return;
		_value = val;		
	}

	public void initPropagate() {
		if (_x != null) 
			_value = _x.getValue();		
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
	public double getAssignDelta(CustomVarIntLS x, int val) {
		if(IsConstant) return 0;
		if (_x != x ) return 0;
		return val - _value;
	}
	
	@Override
	public double getSwapDelta(CustomVarIntLS x, CustomVarIntLS y) {
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
}
