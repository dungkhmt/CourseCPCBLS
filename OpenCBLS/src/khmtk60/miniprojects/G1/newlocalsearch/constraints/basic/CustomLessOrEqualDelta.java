package khmtk60.miniprojects.G1.newlocalsearch.constraints.basic;

import java.util.HashSet;

import khmtk60.miniprojects.G1.newlocalsearch.functions.basic.CustomFuncVarConst;
import khmtk60.miniprojects.G1.newlocalsearch.model.CustomIFloatFunction;
import khmtk60.miniprojects.G1.newlocalsearch.model.CustomIconstraint;
import khmtk60.miniprojects.G1.newlocalsearch.model.CustomVarIntLS;

public class CustomLessOrEqualDelta implements CustomIconstraint{
	private CustomIFloatFunction _f1;
	private CustomIFloatFunction _f2;
	private CustomVarIntLS[] _x;
	private double _violations;
	
	public CustomLessOrEqualDelta(CustomIFloatFunction _f1, CustomIFloatFunction _f2) {
		this._f1 = _f1;
		this._f2 = _f2;
		post();
	}

	public CustomLessOrEqualDelta(CustomIFloatFunction _f1, double val) {
		this._f1 = _f1;
		_f2 = new CustomFuncVarConst(val);
		post();
	}
	
	public CustomLessOrEqualDelta(double val, CustomIFloatFunction _f2) {
		_f1 = new CustomFuncVarConst(val);
		this._f2 = _f2;
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
	
	@Override
	public CustomVarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return _x;
	}

	@Override
	public void propagateInt(CustomVarIntLS x, int val) {
		// TODO Auto-generated method stub
		if (_f1.getValue() > _f2.getValue())
			_violations = 1;//_f1.getValue() - _f2.getValue();
		else
			_violations = 0;
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		if (_f1.getValue() > _f2.getValue())
			_violations = 1;//_f1.getValue() - _f2.getValue();
		else
			_violations = 0;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double violations() {
		// TODO Auto-generated method stub
		return _violations;
	}

	@Override
	public double violations(CustomVarIntLS x) {
		// TODO Auto-generated method stub
		if (_violations != 0)
			return (x.IsElement(_x)) ? 1 : 0;
		else
			return 0;
	}

	@Override
	public double getAssignDelta(CustomVarIntLS x, int val) {
		// TODO Auto-generated method stub
		double v1 = _f1.getValue() + _f1.getAssignDelta(x, val);
		double v2 = _f2.getValue() + _f2.getAssignDelta(x, val);
		double nv;
		if (v1 > v2)
			nv = 1; //v1 - v2;
		else nv = 0;
		return (nv - _violations);
	}

	@Override
	public double getSwapDelta(CustomVarIntLS x, CustomVarIntLS y) {
		// TODO Auto-generated method stub
		double v1 = _f1.getValue() + _f1.getSwapDelta(x, y);
		double v2 = _f2.getValue() + _f2.getSwapDelta(x, y);
		double nv;
		if (v1 > v2)
			nv = 1; //v1 - v2;
		else 
			nv = 0;
		return (nv - _violations);
	}

}
