package localsearch.constraints.basic;

import java.util.HashSet;

import localsearch.functions.basic.FuncVarFloatConst;
import localsearch.model.FloatAbstractInvariant;
import localsearch.model.FloatLocalSearchManager;
import localsearch.model.IFloatConstraint;
import localsearch.model.IFloatFunction;
import localsearch.model.VarIntLS;

public class FloatNotEqual extends FloatAbstractInvariant implements IFloatConstraint{

	private float _violations;
	private IFloatFunction _f1;
	private IFloatFunction _f2;
	private VarIntLS[] _x;
	private FloatLocalSearchManager _ls;
	
	public FloatNotEqual(VarIntLS x, int val) {
		_ls = x.getLocalSearchManager();
		_f1 = new FuncVarFloatConst(x);
		_f2 = new FuncVarFloatConst(_ls, val);
		post();
	}
	
	private void post() {
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		VarIntLS[] x1 = _f1.getVariables();
		VarIntLS[] x2 = _f2.getVariables();
		if(x1 != null)
			for (int i = 0; i < _f1.getVariables().length; i++)
				_S.add(x1[i]);
		if(x2 != null)
			for (int i = 0; i < _f2.getVariables().length; i++)
				_S.add(x2[i]);
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
		return _violations;
	}

	public float violations(VarIntLS x) {
		if (_violations != 0)
			return (x.IsElement(_x)) ? 1 : 0;
		else
			return 0;
	}
	
	public VarIntLS[] getVariables() {
		return _x;
	}
	
	@Override
	public float getAssignDelta(VarIntLS x, int val) {
		if (!x.IsElement(_x)) return 0;
		float v1 = _f1.getValue() + _f1.getAssignDelta(x, val);
		float v2 = _f2.getValue() + _f2.getAssignDelta(x, val);
		int d;
		if(v1 == v2)
			d = 1;
		else d = 0;
		return 
			d - _violations;
	}
	
	@Override
	public float getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		float v1 = _f1.getValue() + _f1.getSwapDelta(x, y);
		float v2 = _f2.getValue() + _f2.getSwapDelta(x, y);
		int d;
		if(v1 == v2)
			d = 1;
		else d = 0;
		return 
			d - _violations;
	}
	
	public void propagateInt(VarIntLS x, int val) {
		if (_f1.getValue() == _f2.getValue())
			_violations = 1;
		else
			_violations = 0;
	}
	
	public void initPropagate() {
		if (_f1.getValue() == _f2.getValue())
			_violations = 1;
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
	
//	public static void main(String[] args) {
//		LocalSearchManager ls = new LocalSearchManager();
//		int n = 5;
//		VarIntLS[] x = new VarIntLS[n];
//		for(int i = 0; i < n; i++)
//			x[i] = new VarIntLS(ls,0,n-1);
//		
//		ConstraintSystem S = new ConstraintSystem(ls);
//		S.post(new NotEqual(x[0],4));
//		S.post(new NotEqual(1,x[3]));
//		S.post(new NotEqual(2,x[4]));
//		S.post(new AllDifferent(x));
//		ls.close();
//		
//		localsearch.search.TabuSearch s = new localsearch.search.TabuSearch();
//		s.search(S, 10, 1, 1000, 100);
//		
//		for(int i = 0; i < n; i++)
//			System.out.print(x[i].getValue() + " ");
//		System.out.println();
//	}
}
