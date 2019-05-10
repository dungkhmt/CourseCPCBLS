package khmtk60.miniprojects.G2.src.localsearch.functions.basic;

import java.util.HashSet;

import khmtk60.miniprojects.G2.src.localsearch.model.*;

public class FloatFuncPlus extends FloatAbstractInvariant implements IFloatFunction {

	private float _value;
	private float _minValue;
	private float _maxValue;
	private IFloatFunction _f1;
	private IFloatFunction _f2;
	private VarIntLS[] _x;
	private FloatLocalSearchManager _ls;

	public FloatFuncPlus(VarIntLS x, int val) {
		_f1 = new FuncVarFloatConst(x);
		_f2 = new FuncVarFloatConst(x.getLocalSearchManager(), val);
		_ls = x.getLocalSearchManager();
		post();
	}

	private void post() {
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		VarIntLS[] x1 = _f1.getVariables();
		VarIntLS[] x2 = _f2.getVariables();
		if (x1 != null) {
			for (int i = 0; i < x1.length; i++)
				_S.add(x1[i]);
		}
		if (x2 != null) {
			for (int i = 0; i < x2.length; i++)
				_S.add(x2[i]);
		}
		_x = new VarIntLS[_S.size()];
		int i = 0;
		for (VarIntLS e : _S) {
			_x[i] = e;
			i++;
		}
		_value = _f1.getValue() + _f2.getValue();
		_maxValue = _f1.getMaxValue() + _f2.getMaxValue();
		_minValue = _f1.getMinValue() + _f2.getMinValue();
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
		return _x;
	}

	@Override
	public float getAssignDelta(VarIntLS x, int val) {
		return (!(x.IsElement(_x))) ? 0 : _f1.getAssignDelta(x, val) + _f2.getAssignDelta(x, val);
	}

	@Override
	public float getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if ((!(x.IsElement(_x))) && (!(y.IsElement(_x))))
			return 0;
		return _f1.getSwapDelta(x, y) + _f2.getSwapDelta(x, y);
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		_value = _f1.getValue() + _f2.getValue();
	}

	@Override
	public void initPropagate() {
		_value = _f1.getValue() + _f2.getValue();
	}

	@Override
	public FloatLocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	@Override
	public float getAssignDelta(VarIntLS x, int valx, VarIntLS y, int valy) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

}
