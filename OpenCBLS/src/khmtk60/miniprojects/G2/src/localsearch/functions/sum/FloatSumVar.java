package khmtk60.miniprojects.G2.src.localsearch.functions.sum;

import java.util.HashMap;

import khmtk60.miniprojects.G2.src.localsearch.model.FloatAbstractInvariant;
import khmtk60.miniprojects.G2.src.localsearch.model.FloatLocalSearchManager;
import khmtk60.miniprojects.G2.src.localsearch.model.IFloatFunction;
import khmtk60.miniprojects.G2.src.localsearch.model.VarIntLS;

public class FloatSumVar extends FloatAbstractInvariant implements IFloatFunction {
	private float _value;
	private float _minValue;
	private float _maxValue;
	private VarIntLS[] _x;
	private int[] _a;
	private FloatLocalSearchManager _ls;
	private boolean _posted;
	private HashMap<VarIntLS, Integer> _map;

	public FloatSumVar(VarIntLS[] x) {
		if (x.length == 0) {
			System.out.println(name() + "::constructor exception, input array is null");
			assert (false);
		}
		_x = x;
		_ls = x[0].getLocalSearchManager();
		_posted = false;
		post();

	}

	void post() {
		if (_posted)
			return;
		_posted = true;
		_map = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < _x.length; i++) {
			_map.put(_x[i], i);
		}

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
		// TODO Auto-generated method stub
		if (_map.get(x) == null)
			return 0;
		return val - x.getValue();
	}

	@Override
	public float getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if (_map.get(x) == null && _map.get(y) == null)
			return 0;
		if (_map.get(y) == null && _map.get(x) != null)
			return getAssignDelta(x, y.getValue());
		if (_map.get(y) != null && _map.get(x) == null)
			return getAssignDelta(y, x.getValue());
		return 0;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		if (_map.get(x) == null)
			return;
		_value = _value + (float)val - (float)x.getOldValue();

	}

	@Override
	public void initPropagate() {
		_value = 0;
		for (int i = 0; i < _x.length; i++) {
			_value += (float)_x[i].getValue();
			_minValue += (float)_x[i].getMinValue();
			_maxValue += (float)_x[i].getMaxValue();
		}

	}

	@Override
	public FloatLocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	public String name() {
		return "SumVar";
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		float nv = 0;
		for (int i = 0; i < _x.length; i++) {
			nv += (float)_x[i].getValue();
		}
		if (nv == _value)
			return true;
		else
			return false;
	}

	@Override
	public float getAssignDelta(VarIntLS x, int valx, VarIntLS y, int valy) {
		// TODO Auto-generated method stub
		return 0;
	}

}
