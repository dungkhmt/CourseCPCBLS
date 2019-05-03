package khmtk60.miniprojects.G3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ConditionCount extends AbstractInvariant implements
		IFunction {

	private int _value;
	private int _minValue;
	private int _maxValue;
	private int _val;
	
	
	private int[]  _w;
	private VarIntLS[] _x;
	
	private LocalSearchManager _ls;
	private HashMap<VarIntLS, Integer> _map;
//	private HashMap<VarIntLS, Integer> _mapW;
	private SetCount _setW;
	private boolean _posted;
	
	
	
	
	public ConditionCount(VarIntLS[] x, int[] w, int val) {
		_x=x;
		_w=w;
		_val=val;
		_ls=x[0].getLocalSearchManager();
		_posted = false;
		post();
	}

	private void post() {
		// Count distinct w
		_setW = new SetCount();
		for(int i=0;i<_x.length;i++)
		{
			if(_val==_x[i].getValue())
			{
				_setW.add(_w[i]);
			}
		}
		
		if(_posted) return;
		_posted = true;
		
		_map=new HashMap<VarIntLS, Integer>();
		for(int i=0;i<_x.length;i++)
		{
			_map.put(_x[i],i);
		}
		
		_minValue = 0;
		_maxValue = 0;
		
		// Use for assign _maxValue
		HashSet<Integer> _S = new HashSet<Integer>();
		for(int i=0;i<_x.length;i++) {
			_S.add(_w[i]);
		}
		_maxValue = _S.size();
		_ls.post(this);
 		
	}

	@Override
	public int getMinValue() {
		// TODO Auto-generated method stub
		return _minValue;
	}

	@Override
	public int getMaxValue() {
		// TODO Auto-generated method stub
		return _maxValue;
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return _value;
	}

	@Override
	public VarIntLS[] getVariables() {
		return _x;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		//if(!(x.IsElement(_x))) return 0;
		if(_map.get(x) == null) return 0;
		int nv=_value;
		int k=_map.get(x);
		if(x.getValue()==_val) {
			if(_val==val) {
				nv=nv;
			}
			else {
//				nv=nv-_w[k];
				_setW.remove(_w[k]);
				nv = _setW.size();
				_setW.add(_w[k]);
			}
		} else {
			if(_val==val) {
//				nv=nv+_w[k];
				_setW.add(_w[k]);
				nv = _setW.size();
				_setW.remove(_w[k]);
			} else {
				nv=nv;
			}
		}
		
		return nv-_value;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if(_map.get(x) == null&&_map.get(y) == null) return 0;
		if(_map.get(x) != null&&_map.get(y) == null) return getAssignDelta(x, y.getValue());
		if(_map.get(x) == null&&_map.get(y) != null) return getAssignDelta(y, x.getValue());
		
		int nv=_value;
		int k1=_map.get(x);
		int k2=_map.get(y);
		if(x.getValue()==_val && y.getValue()==_val) {
			nv=nv;
		} else if(x.getValue()==_val && y.getValue()!=_val) {
			_setW.remove(_w[k1]);
			_setW.add(_w[k2]);
			nv = _setW.size();
			_setW.remove(_w[k2]);
			_setW.add(_w[k1]);
		} else if(x.getValue()!=_val && y.getValue()==_val) {
			_setW.remove(_w[k2]);
			_setW.add(_w[k1]);
			nv = _setW.size();
			_setW.remove(_w[k1]);
			_setW.add(_w[k2]);
		} else if(x.getValue()!=_val && y.getValue()!=_val) {
			nv=nv;
		}
		
		return nv - _value;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		//if(!(x.IsElement(_x))) return ;
		if(_map.get(x) == null) return ;
		int nv=_value;
		int t=x.getOldValue();
		
		int k=_map.get(x);
		if(t==_val) {
			if(x.getValue()==_val) {
				nv=nv;
			}
			else {
//				nv=nv-_w[k];
				_setW.remove(_w[k]);
				nv = _setW.size();
			}
		} else {
			if(x.getValue()==_val) {
//				nv=nv+_w[k];
				_setW.add(_w[k]);
				nv = _setW.size();
			}
			else {
				nv=nv;
			}
		}
			
		_value=nv;
	}

	@Override
	public void initPropagate() {
//		int tong=0;
		_setW.clear();
		for(int i=0; i<_x.length; i++)
		{
			if(_val==_x[i].getValue())
			{
				_setW.add(_w[i]);
			}
		}
		_value=_setW.size();
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int nv = 0;
		SetCount setW = new SetCount();
		for(int i = 0; i < _x.length; i++) {
			if(_x[i].getValue()==_val)
			{
				setW.add(_w[i]);
			}
		}
		nv = setW.size();
		if(nv==_value)
			return true;
		else
			System.out.println(name() + "::verify --> failed, _value = " + _value + " which differs from new value = " + nv + " by recomputation");
		return false;
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	public static void main(String[] args) {
		LocalSearchManager ls = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[1000];
		
		for (int i = 0; i < x.length; i++) {
			x[i] = new VarIntLS(ls, 0, 10);
			x[i].setValue(i);
		}
		
		Random r = new Random();
		for(int i=0;i<1000;i++) {
			x[i].setValue(r.nextInt());
		}
		
		int[] b = new int[x.length];
		for (int i = 0; i < b.length; i++) {
			b[i] = r.nextInt();
		}
		//b[3]=20;

		ConditionCount c = new ConditionCount(x, b, 2);
		ls.close();
		
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(c,100000);
	}

}
