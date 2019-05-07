package localsearch.functions.conditionalsum;

import java.util.HashMap;
import java.util.HashSet;

import localsearch.model.FloatAbstractInvariant;
import localsearch.model.FloatLocalSearchManager;
import localsearch.model.IFloatFunction;
import localsearch.model.VarIntLS;

public class FloatSumVarInt extends FloatAbstractInvariant implements IFloatFunction {

	private float _value;
	private float _minValue;
	private float _maxValue;
	private int _val;
	private int[] _tmp_i;

	private int[] _w;
	private VarIntLS[] _x;

	private FloatLocalSearchManager _ls;
	private HashMap<VarIntLS, Integer> _map;
	private boolean _posted;

	public FloatSumVarInt(VarIntLS[] x, int[] w, int val, int max_t) {
		_x = x;
		_w = w;
		_val = val;
		_tmp_i = new int[max_t];
		_ls = x[0].getLocalSearchManager();
		_posted = false;
		post(max_t);
	}

	private void post(int max_t) {
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
		_maxValue = max_t;

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
	public float getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		if (_map.get(x) == null)
			return 0;
		float nv = _value;
		int k = _map.get(x);
		if (x.getValue() == _val) {
			if (_val == val) {

			} else {
				_tmp_i[_w[k]]--;
				nv = cnt();
				_tmp_i[_w[k]]++;
			}
		} else {
			if (_val == val) {
				_tmp_i[_w[k]]++;
				nv = cnt();
				_tmp_i[_w[k]]--;
			}
		}
		return nv - _value;
	}

	private int cnt() {
		int sum = 0;
		for (int a : _tmp_i) {
			if (a > 0) {
				sum++;
			}
		}
		return sum;
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
			_tmp_i[_w[k2]]++;
			_tmp_i[_w[k1]]--;
			nv = cnt();
			_tmp_i[_w[k2]]--;
			_tmp_i[_w[k1]]++;
		} else if (x.getValue() != _val && y.getValue() == _val) {
			_tmp_i[_w[k1]]++;
			_tmp_i[_w[k2]]--;
			nv = cnt();
			_tmp_i[_w[k1]]--;
			_tmp_i[_w[k2]]++;
		} else if (x.getValue() != _val && y.getValue() != _val) {
		}
		return nv - _value;
	}

	@Override
	public VarIntLS[] getVariables() {
		return _x;
	}
	@Override
	public void propagateInt(VarIntLS x, int val) {
		if (_map.get(x) == null)
			return;
		float nv = _value;
		int t = x.getOldValue();
		int k = _map.get(x);
		if (t == _val) {
			if (x.getValue() == _val) {
			} else {
				_tmp_i[_w[k]]--;
				nv = cnt();
			}
		} else {
			if (x.getValue() == _val) {
				_tmp_i[_w[k]]++;
				nv = cnt();
			}
		}

		_value = nv;
	}

	@Override
	public void initPropagate() {
		for(int i = 0; i < _x.length; i++) {
			if(_val == _x[i].getValue()) {
				_tmp_i[_w[i]]++;
			}
		}
		_value = cnt();
	}
	
	@Override
	public boolean verify() {
		return true;
	}
	
	@Override
	public FloatLocalSearchManager getLocalSearchManager() {
		return _ls;
	}
	
	@Override
	public float getAssignDelta(VarIntLS x, int valx, VarIntLS y, int valy) {
		// TODO Auto-generated method stub
		return 0;
	}

}
