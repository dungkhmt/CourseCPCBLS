package khmtk60.miniprojects.G2.src.localsearch.costraints.basic;

import java.util.HashSet;

import khmtk60.miniprojects.G2.src.localsearch.functions.basic.FuncVarFloatConst;
import khmtk60.miniprojects.G2.src.localsearch.model.FloatAbstractInvariant;
import khmtk60.miniprojects.G2.src.localsearch.model.FloatLocalSearchManager;
import khmtk60.miniprojects.G2.src.localsearch.model.IFloatConstraint;
import khmtk60.miniprojects.G2.src.localsearch.model.IFloatFunction;
import khmtk60.miniprojects.G2.src.localsearch.model.VarIntLS;

public class LessOrEqualFloat extends FloatAbstractInvariant implements IFloatConstraint{

	private IFloatFunction _f1;
	private IFloatFunction _f2;
	private VarIntLS[] _x;
	private float _violations;
	private FloatLocalSearchManager _ls;
	boolean _posted;
	
	public LessOrEqualFloat(IFloatFunction f, float val) {
		_f1 = f;
		_f2 = new FuncVarFloatConst(f.getLocalSearchManager(), val);
		_ls = f.getLocalSearchManager();
		post();
	}
	
	public LessOrEqualFloat(float val, IFloatFunction f) {
		_f1 = new FuncVarFloatConst(f.getLocalSearchManager(), val);
		_f2 = f;
		_ls = f.getLocalSearchManager();
		post();
	}
	
	public void post() {
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		VarIntLS[] x1 = _f1.getVariables();
		VarIntLS[] x2 = _f2.getVariables();
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
		_x = new VarIntLS[_S.size()];
		int i = 0;
		for (VarIntLS e : _S){
			_x[i] = e;
			i++;
		}		
		_ls.post(this);		
	}
	
	@Override
	public float violations() {
		// TODO Auto-generated method stub
		return _violations;
	}

	@Override
	public float violations(VarIntLS x) {
		if (_violations != 0) {
			return (x.IsElement(_x)) ? 1 : 0;

		}else {
			return 0;
		}
	}
	
	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return _x;
	}
	
	@Override
	public float getAssignDelta(VarIntLS x, int val) {
		if (!x.IsElement(_x)) {
			return 0;
		}
		float v1 = _f1.getValue() + (float)_f1.getAssignDelta(x, val);
		float v2 = _f2.getValue() + (float)_f1.getAssignDelta(x, val);
		float nv;
		if (v1 > v2) {
			nv = 1;
		}else {
			nv = 0;
		}
		return (nv - _violations);
	}

	@Override
	public float getSwapDelta(VarIntLS x, VarIntLS y) {
		float v1 = _f1.getValue() + (float)_f1.getSwapDelta(x, y);
		float v2 = _f2.getValue() + (float)_f2.getSwapDelta(x, y);
		float nv;
		if (v1 > v2) {
			nv = 1;
		}else {
			nv = 0;
		}
		return (nv - _violations);
	}
	@Override
	public void propagateInt(VarIntLS x, int val) {
		if (_f1.getValue() > _f2.getValue())
			_violations = _f1.getValue() - _f2.getValue() > 0 ? 1 : 0;
		else
			_violations = 0;
	}
	@Override
	public void initPropagate() {
		if (_f1.getValue() > _f2.getValue())
			_violations = _f1.getValue() - _f2.getValue() > 0 ? 1 : 0;
		else
			_violations = 0;
	}
	@Override
	public FloatLocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

}
