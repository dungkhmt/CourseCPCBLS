package khmtk60.miniprojects.G3;

import localsearch.constraints.basic.LessThan;
import localsearch.model.AbstractInvariant;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class IsIn extends AbstractInvariant implements IConstraint{

	private VarIntLS _x;
	private int[] _arr;
	private int _violations;
	private LocalSearchManager _ls;

	public IsIn(VarIntLS x, int[] arr){
		this._x = x;
		this._arr = arr;
		_ls = _x.getLocalSearchManager();
		_ls.post(this);
	}
	
	public boolean isInArray(int x, int[] arr) {
		for (int i: arr) {
			if (i == x) return true;
		}
		return false;
	}
	
	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		VarIntLS[] temp = new VarIntLS[1];
		temp[0] = this._x;
		return temp;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		//System.out.println("LessThan::propagateInt");
		if(x != _x) return;
		if (isInArray(_x.getValue(), this._arr)) {
			_violations = 0;
		} else _violations = 10;
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		//System.out.println("LessThan::initPropagate");
		if (isInArray(_x.getValue(), this._arr)) {
			_violations = 0;
		} else _violations = 10;
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int nv = 0;
		if (isInArray(_x.getValue(), this._arr)) {
			nv = 0;
		} else nv = 10;
		return nv == _violations;
	}

	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return _violations;
	}

	@Override
	public int violations(VarIntLS x) {
		// TODO Auto-generated method stub
		if(x == _x) return _violations;
		return 0;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		if(x != _x) return 0;
		int nv = 0;
		if (isInArray(val, this._arr)) {
			nv = 0;
		} else nv = 10;

		return nv - _violations;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		if (this._x == x) return getAssignDelta(x, y.getValue());
		if (this._x == y) return getAssignDelta(y, x.getValue());
		return 0;
	}
	public static void main(String[] args){
		LocalSearchManager ls = new LocalSearchManager();
		VarIntLS x = new VarIntLS(ls,1,10);
		int[] y = {1,2,3,4,5,6};
		VarIntLS z = new VarIntLS(ls,1,10);
		ConstraintSystem S = new ConstraintSystem(ls);
		S.post(new IsIn(x,y));
		ls.close();
		
		
		
		x.setValuePropagate(3);
		System.out.println("S = " + S.violations());
		int d = S.getSwapDelta(x, z);
		x.swapValuePropagate(z);
		System.out.println("Delta = " + d + ", S = " + S.violations());
		
		d = S.getAssignDelta(z, 7);
		z.setValuePropagate(7);
		System.out.println("Delta = " + d + ", S = " + S.violations());
		
		d = S.getSwapDelta(x, z);
		x.swapValuePropagate(z);
		System.out.println("Delta = " + d + ", S = " + S.violations());
		
		
	}

}
