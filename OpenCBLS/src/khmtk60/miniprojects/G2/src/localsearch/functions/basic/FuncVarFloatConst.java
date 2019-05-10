package khmtk60.miniprojects.G2.src.localsearch.functions.basic;


import khmtk60.miniprojects.G2.src.localsearch.model.FloatAbstractInvariant;
import khmtk60.miniprojects.G2.src.localsearch.model.FloatLocalSearchManager;
import khmtk60.miniprojects.G2.src..model.IFloatFunction;
import khmtk60.miniprojects.G2.src.localsearch.model.VarIntLS;

public class FuncVarFloatConst extends FloatAbstractInvariant implements IFloatFunction {

	private float _value;
	private float _minValue;
	private float _maxValue;
	private VarIntLS _x;
	private FloatLocalSearchManager _ls;
	private boolean IsConstant;

	public FuncVarFloatConst(FloatLocalSearchManager ls, float val) {
		_minValue = val;
		_maxValue = val;
		_value = val;
		IsConstant = true;
		_ls = ls;
		_ls.post(this);
	}

	public FuncVarFloatConst(VarIntLS x) {
		_ls = x.getLocalSearchManager();
		_minValue =  x.getMinValue();
		_maxValue =  x.getMaxValue();
		_value =  x.getValue();
		_x = x;
		IsConstant = false;
		_ls.post(this);
	}

	@Override
	public float getMinValue() {
		// TODO Auto-generated method stub
		return _minValue;
	}

	@Override
	public float getMaxValue() {
		// TODO Auto-generated method stub
		return _maxValue;
	}

	@Override
	public float getValue() {
		// TODO Auto-generated method stub
		return _value;
	}

	@Override
	public VarIntLS[] getVariables() {
		if (IsConstant)
			return null;
		VarIntLS _X[] = new VarIntLS[1];
		_X[0] = _x;
		return _X;
	}

	@Override
	public float getAssignDelta(VarIntLS x, int val) {
		if (IsConstant)
			return 0;
		if (_x != x)
			return 0;
		return  val - _value;
	}

	@Override
	public float getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if (IsConstant)
			return 0;
		if (_x == y)
			return ((float) x.getValue() - (float) y.getValue());
		else if (_x == x)
			return (float) y.getValue() - _value;
		else
			return 0;
	}

	@Override
	public float getAssignDelta(VarIntLS x, int valx, VarIntLS y, int valy) {
		return 0;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		if (_x != x)
			return;
		_value =  val;
	}

	@Override
	public void initPropagate() {
		if (_x != null)
			_value =  _x.getValue();
	}

	@Override
	public FloatLocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	public String name() {
		return "FuncVarConst";
	}

	// @Override
	// public LocalSearchManager getLocalSearchManager() {
	// // TODO Auto-generated method stub
	// return null;
	// }

}
