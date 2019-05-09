package khmtk60.miniprojects.G1.newlocalsearch.constraints.basic;

import khmtk60.miniprojects.G1.newlocalsearch.model.CustomIconstraint;
import khmtk60.miniprojects.G1.newlocalsearch.model.CustomVarIntLS;

public class CustomLessOrEqualVarInt implements CustomIconstraint{
	private CustomVarIntLS _x;
	private double _val;
	private double _violations;
	boolean _posted;

	public CustomLessOrEqualVarInt(CustomVarIntLS x, int val){
		_x = x;
		_val = val;
		post();
	}
	
	public void post() {
			
	}
	
	public double getAssignDelta(CustomVarIntLS x, int val) {
		// TODO Auto-generated method stub
		if (!(x == _x)) 
			return 0;
		double v1 = val;
		double v2 = _val;
		double nv;
		if (v1 > v2)
			nv = v1 - v2;
		else nv = 0;
		return (nv - _violations);
	}

	public double getSwapDelta(CustomVarIntLS x, CustomVarIntLS y) {
		return 0;
	}

	public void propagateInt(CustomVarIntLS x, int val) {
		if (_x.getValue() > _val)
			_violations = _x.getValue() - _val;
		else
			_violations = 0;
	}

	public void initPropagate() {
		if (_x.getValue() > _val)
			_violations = _x.getValue() - _val;
		else
			_violations = 0;
	}

	public CustomVarIntLS[] getVariables() {
		return null;
	}
	
	public double violations() {
		return _violations;
	}

	public double violations(CustomVarIntLS x) {
		if (_violations != 0)
			return (x == _x) ? 1 : 0;
		else
			return 0;
	}
	
	public boolean verify() {
		System.out.println(_x.getValue() + " -- " + _val);
		return false;
	}
}
