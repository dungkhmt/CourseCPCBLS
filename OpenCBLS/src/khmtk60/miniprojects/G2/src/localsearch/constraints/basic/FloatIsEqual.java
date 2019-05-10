package khmtk60.miniprojects.G2.src.localsearch.costraints.basic;

import java.util.HashSet;

import khmtk60.miniprojects.G2.src.localsearch.functions.basic.FuncVarFloatConst;
import khmtk60.miniprojects.G2.src.localsearch.model.FloatAbstractInvariant;
import khmtk60.miniprojects.G2.src.localsearch.model.FloatLocalSearchManager;
import khmtk60.miniprojects.G2.src.localsearch.model.IFloatConstraint;
import khmtk60.miniprojects.G2.src.localsearch.model.IFloatFunction;
import khmtk60.miniprojects.G2.src.localsearch.model.VarIntLS;

public class FloatIsEqual extends FloatAbstractInvariant implements IFloatConstraint {

	private float _violations;
	private IFloatFunction _f1;
	private IFloatFunction _f2;
	private VarIntLS[] _x;
	private FloatLocalSearchManager _ls;

	public FloatIsEqual(IFloatFunction f1, IFloatFunction f2) {
		_ls = f1.getLocalSearchManager();
		_f1 = f1;
		_f2 = f2;
		post();
	}

	public FloatIsEqual(IFloatFunction f, int val) {
		_ls = f.getLocalSearchManager();
		_f1 = f;
		_f2 = new FuncVarFloatConst(_ls, (float) val);
		post();
	}

	public FloatIsEqual(VarIntLS x, int val) {
		_ls = x.getLocalSearchManager();
		_f1 = new FuncVarFloatConst(x);
		_f2 = new FuncVarFloatConst(_ls, (float) val);
		post();
	}

	public FloatIsEqual(VarIntLS x, VarIntLS y) {
		_ls = x.getLocalSearchManager();
		_f1 = new FuncVarFloatConst(x);
		_f2 = new FuncVarFloatConst(y);
		post();
	}

	private void post() {
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		VarIntLS[] x1 = _f1.getVariables();
		VarIntLS[] x2 = _f2.getVariables();
		if (x1 != null) {
			for (int i = 0; i < _f1.getVariables().length; i++)
				_S.add(x1[i]);
		}
		if (x2 != null) {
			for (int i = 0; i < _f2.getVariables().length; i++)
				_S.add(x2[i]);
		}
		_x = new VarIntLS[_S.size()];
		int i = 0;
		for (VarIntLS e : _S) {
			_x[i] = e;
			i++;
		}
		_ls.post(this);
	}

	@Override
	public float violations() {
		return _violations;
	}

	public float violations(VarIntLS x) {
		if (_violations != 0f)
			return (x.IsElement(_x)) ? 1 : 0f;
		else
			return 0f;
	}

	public VarIntLS[] getVariables() {
		return _x;
	}

	@Override
	public float getAssignDelta(VarIntLS x, int val) {
		if (!x.IsElement(_x)) return 0;
		float v1 = _f1.getValue() + _f1.getAssignDelta(x, val);
		float v2 = _f2.getValue() + _f2.getAssignDelta(x, val);
		float nv;
		if(v1 != v2) {
			nv = 1;
		}
		else {
			nv = 0;
		}
		return nv - _violations;
	}

	@Override
	public float getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		float v1 = _f1.getValue() + _f1.getSwapDelta(x, y);
		float v2 = _f2.getValue() + _f2.getSwapDelta(x, y);
		float nv;
		if(v1 != v2 ) {
			nv = 1;
		}
		else {
			nv = 0;
		}
		return nv - _violations;
	}

	public void propagateInt(VarIntLS x, int val) {
		_violations = Math.abs(_f1.getValue() - _f2.getValue()) > 0 ? 1 : 0;
	}

	public void initPropagate() {
		_violations = Math.abs(_f1.getValue() - _f2.getValue()) > 0 ? 1 : 0;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FloatLocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
}
