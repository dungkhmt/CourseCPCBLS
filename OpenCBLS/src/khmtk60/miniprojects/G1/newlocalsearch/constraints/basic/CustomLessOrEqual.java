package khmtk60.miniprojects.G1.newlocalsearch.constraints.basic;

import java.util.HashSet;

import khmtk60.miniprojects.G1.newlocalsearch.functions.basic.CustomFuncVarConst;
import khmtk60.miniprojects.G1.newlocalsearch.model.CustomIFloatFunction;
import khmtk60.miniprojects.G1.newlocalsearch.model.CustomIconstraint;
import khmtk60.miniprojects.G1.newlocalsearch.model.CustomVarIntLS;

public class CustomLessOrEqual implements CustomIconstraint{
	private CustomIFloatFunction _f1;
	private CustomIFloatFunction _f2;
	private CustomVarIntLS[] _x;
	private double _violations;
	boolean _posted;
	
	public CustomLessOrEqual(CustomIFloatFunction f1, CustomIFloatFunction f2) {
		this._f1 = f1;
		this._f2 = f2;
		post();
	}
	
	public CustomLessOrEqual(CustomIFloatFunction f, double val) {
		_f1 = f;
		_f2 = new CustomFuncVarConst(val);
		post();
	}
	
	public CustomLessOrEqual(double val, CustomIFloatFunction f)
	{
		_f1=new CustomFuncVarConst(val);
		_f2=f;
		post();
	}
	
	public void post() {
		HashSet<CustomVarIntLS> _S = new HashSet<CustomVarIntLS>();
		CustomVarIntLS[] x1 = _f1.getVariables();
		CustomVarIntLS[] x2 = _f2.getVariables();
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
		_x = new CustomVarIntLS[_S.size()];
		int i = 0;
		for (CustomVarIntLS e : _S){
			_x[i] = e;
			i++;
		}			
	}
	
	public CustomVarIntLS[] getVariables() {
		return _x;
	}
	
	public double violations() {
		return _violations;
	}

	public double violations(CustomVarIntLS x) {
		if (_violations != 0)
			return (x.IsElement(_x)) ? 1 : 0;
		else
			return 0;
	}

	public double getAssignDelta(CustomVarIntLS x, int val) {
//		if (!x.IsElement(_x)) 
//			return 0;
		double v1 = _f1.getValue() + _f1.getAssignDelta(x, val);
		double v2 = _f2.getValue() + _f2.getAssignDelta(x, val);
		double nv;
		if (v1 > v2)
			nv = 1; //Math.min(0.1, v1 - v2);
		else nv = 0;
		return (nv - _violations);
	}

	public double getSwapDelta(CustomVarIntLS x, CustomVarIntLS y) {
		// TODO Auto-generated method stub
		double v1 = _f1.getValue() + _f1.getSwapDelta(x, y);
		double v2 = _f2.getValue() + _f2.getSwapDelta(x, y);
		double nv;
		if (v1 > v2)
			nv = 1; //Math.min(0.1, v1 - v2);
		else 
			nv = 0;
		return (nv - _violations);
	}

	public void propagateInt(CustomVarIntLS x, int val) {
		if (_f1.getValue() > _f2.getValue())
			_violations = 1; //Math.min(0.1, _f1.getValue() - _f2.getValue());
		else
			_violations = 0;
	}

	public void initPropagate() {
		// TODO Auto-generated method stub
		if (_f1.getValue() > _f2.getValue())
			_violations = 1; //Math.min(0.1, _f1.getValue() - _f2.getValue());
		else
			_violations = 0;
	}

	@Override
	public boolean verify() {
		return false;
	}
}
