package localsearch.constraints.basic;

import localsearch.functions.basic.FuncVarConstD;
import localsearch.model.*;

import java.util.HashSet;

public class LessOrEqualW extends AbstractInvariant implements IConstraint {

	private IFunctionD _f1;
	private IFunctionD _f2;
	private VarIntLS[] _x;
	private int _violations;
	private LocalSearchManager _ls;
	boolean _posted;

//	public LessOrEqualD(IFunction f1, IFunction f2) {
//		this._f1 = f1;
//		this._f2 = f2;
//		_ls = f1.getLocalSearchManager();
//		post();
//	}
//
//	public LessOrEqualD(IFunction f, VarIntLS x){
//		_f1 = f;
//		_f2 = new FuncVarConst(x);
//		_ls = f.getLocalSearchManager();
//		post();
//	}
//
//	public LessOrEqualD(VarIntLS x, IFunction f){
//		_f1 = new FuncVarConst(x);
//		_f2 = f;
//		_ls = x.getLocalSearchManager();
//		post();
//	}
//
//	public LessOrEqualD(VarIntLS x1, VarIntLS x2){
//		_f1 = new FuncVarConst(x1);
//		_f2 = new FuncVarConst(x2);
//		_ls = x1.getLocalSearchManager();
//		post();
//	}
	public LessOrEqualW(IFunctionD f, double val)
	{
		_f1=f;
		_f2=new FuncVarConstD(f.getLocalSearchManager(), val);
		_ls=f.getLocalSearchManager();
		post();
	}
	public LessOrEqualW(double val, IFunctionD f)
	{
		_f1=new FuncVarConstD(f.getLocalSearchManager(), val);
		_f2=f;
		_ls=f.getLocalSearchManager();
		post();
	}
//	public LessOrEqualD(VarIntLS x, int val){
//		_ls = x.getLocalSearchManager();
//		_f1 = new FuncVarConst(x);
//		_f2 = new FuncVarConst(_ls,val);
//		post();
//	}
//	public LessOrEqualD(int val, VarIntLS x){
//		_ls = x.getLocalSearchManager();
//		_f2 = new FuncVarConst(x);
//		_f1 = new FuncVarConst(_ls,val);
//		post();
//	}
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
	public int violations() {
		// TODO Auto-generated method stub
		return _violations;
	}
	
	@Override
	public int violations(VarIntLS x) {
		if (_violations != 0)
			return _violations;
		else
			return 0;
	}

	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return _x;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub

		int v1 = (int)(_f1.getValue() + _f1.getAssignDelta(x, val));
		int v2 = (int)(_f2.getValue() + _f2.getAssignDelta(x, val));
		int nv;
		if (v1 > v2)
			nv = v1 - v2;
		else nv = 0;
		nv= nv * 4;
		return (nv - _violations);
	}
	
	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub

		int v1 = (int)(_f1.getValue() + _f1.getSwapDelta(x, y));
		int v2 = (int)(_f2.getValue() + _f2.getSwapDelta(x, y));
		int nv;
		if (v1 > v2)
			nv = v1 - v2;
		else 
			nv = 0;
		nv = nv * 4;
		return (nv - _violations);
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		if (_f1.getValue() > _f2.getValue())
			_violations = (int)(_f1.getValue() - _f2.getValue()) * 2;
		else
			_violations = 0;
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		if (_f1.getValue() > _f2.getValue())
			_violations = (int)(_f1.getValue() - _f2.getValue()) * 2;//1;
		else
			_violations = 0;
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
	
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}	
}
	
	