package localsearch.functions.conditionalsum;

import java.util.HashMap;
import java.util.HashSet;

import localsearch.model.FloatAbstractInvariant;
import localsearch.model.FloatLocalSearchManager;
import localsearch.model.IFloatFunction;
import localsearch.model.VarIntLS;

public class FloatConditionalSumVarInt extends FloatAbstractInvariant implements IFloatFunction {

	private float _value;
	private float _minValue;
	private float _maxValue;
	private int _val;

	private float[] _w;
	private VarIntLS[] _x;

	private FloatLocalSearchManager _ls;
	private HashMap<VarIntLS, Integer> _map;
	private boolean _posted;

	public FloatConditionalSumVarInt(VarIntLS[] x, float[] w, int val) {
		_x = x;
		_w = w;
		_val = val;
		_ls = x[0].getLocalSearchManager();
		_posted = false;
		post();
	}

	private void post() {
		if (_posted)
			return;
		_posted = true;

		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		for (int i = 0; i < _x.length; i++) {
			_S.add(_x[i]);
		}
		_map = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < _x.length; i++) {
			_map.put(_x[i], i);
		}

		_minValue = 0;
		_maxValue = 0;
		for (int i = 0; i < _w.length; i++)
			_maxValue = _maxValue + _w[i];
		
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
		if (_map.get(x) == null)
			return 0;
		float nv = _value;
		int k = _map.get(x);
		if (x.getValue() == _val) {
			if (_val == val) {
				nv = nv;
			} else {
				nv = nv - _w[k];
			}
		} else {
			if (_val == val) {
				nv = nv + _w[k];
			} else {
				nv = nv;
			}
		}
		return nv - _value;
	}

	@Override
	public float getSwapDelta(VarIntLS x, VarIntLS y) {
		if (_map.get(x) == null && _map.get(y) == null)
			return 0;
		if (_map.get(x) != null && _map.get(y) == null)
			return getAssignDelta(x, y.getValue());
		if (_map.get(x) == null && _map.get(y) != null)
			return getAssignDelta(y, x.getValue());

		float nv = _value;
		int k1 = _map.get(x);
		int k2 = _map.get(y);
		if (x.getValue() == _val && y.getValue() == _val) {
			nv = nv;
		} else if (x.getValue() == _val && y.getValue() != _val) {
			nv = nv - _w[k1] + _w[k2];
		} else if (x.getValue() != _val && y.getValue() == _val) {
			nv = nv - _w[k2] + _w[k1];
		} else if (x.getValue() != _val && y.getValue() != _val) {
			nv = nv;
		}
		return nv - _value;
	}

	@Override
	public float getAssignDelta(VarIntLS x, int valx, VarIntLS y, int valy) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		// if(!(x.IsElement(_x))) return ;
		if (_map.get(x) == null)
			return;
		float nv = _value;
		int t = x.getOldValue();

		int k = _map.get(x);
		if (t == _val) {
			if (x.getValue() == _val) {
				nv = nv;
			} else {
				nv = nv - _w[k];
			}
		} else {
			if (x.getValue() == _val) {
				nv = nv + _w[k];
			} else {
				nv = nv;
			}
		}

		_value = nv;
	}

	@Override
	public void initPropagate() {
		float tong = 0;
		for (int i = 0; i < _x.length; i++) {
			if (_val == _x[i].getValue()) {
				tong += _w[i];
			}
		}
		_value = tong;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		float nv = 0;
		for (int i = 0; i < _x.length; i++) {
			if (_x[i].getValue() == _val) {
				nv += _w[i];
			}
		}
		if (nv == _value)
			return true;
		else
			System.out.println(name() + "::verify --> failed, _value = " + _value + " which differs from new value = "
					+ nv + " by recomputation");
		return false;
	}

	@Override
	public FloatLocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

}
