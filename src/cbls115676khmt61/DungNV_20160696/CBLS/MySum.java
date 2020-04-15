package CBLS;

import java.util.HashMap;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class MySum extends AbstractInvariant implements IFunction {
	private VarIntLS[] x;
	private LocalSearchManager mgr;
	private int minValue;
	private int maxValue;
	private int value;
	private HashMap<VarIntLS, Integer> map;
	
	public MySum(VarIntLS[] x) {
		this.x = x;
		if(x == null || x.length == 0) {
			System.out.println("x must be not null");
			return;
		}
		
		map = new HashMap<VarIntLS, Integer>();
		for(int i = 0; i < x.length; i++)
			map.put(x[i], i);
		
		mgr = x[0].getLocalSearchManager();
		
		mgr.post(this);
	}
	
	@Override
	public VarIntLS[] getVariables() {
		return x;
	}

	@Override
	public void propagateInt(VarIntLS y, int val) {
		//Ham nay duoc goi sau khi bien x da duoc gan gia tri val = x[i].getValue
		if(map.get(y) == null) return;
		value = value - y.getOldValue() + y.getValue();
	}

	@Override
	public void initPropagate() {
		value = 0;
		minValue = 0;
		maxValue = 0;
		for(int i = 0; i < x.length; i++) {
			value += x[i].getValue();
			minValue += x[i].getMinValue();
			maxValue += x[i].getMaxValue();
		}
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		return mgr;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMinValue() {
		return minValue;
	}

	@Override
	public int getMaxValue() {
		return maxValue;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public int getAssignDelta(VarIntLS y, int val) {
		//Tra ve su thay doi value NEU y duoc gan gia tri val
		if(map.get(y) == null) return 0;
		
		return val - y.getValue();
	}

	@Override
	public int getSwapDelta(VarIntLS y1, VarIntLS y2) {
		if(map.get(y1) == null) return getAssignDelta(y2, y1.getValue());
		if(map.get(y2) == null) return getAssignDelta(y1, y2.getValue());
		return 0; //Tong? k doi? khi swap 2 phan tu cua mang
	}
	
	public static void main(String[] args) {
		LocalSearchManager mgr = new LocalSearchManager();
		int N = 5;
		VarIntLS[] x = new VarIntLS[N];
		for(int i = 0; i < N; i++) {
			x[i] = new VarIntLS(mgr, 1, N);
			x[i].setValue(1);
		}
		MySum s = new MySum(x);
		mgr.close();
		
		for(int i = 0; i < N; i++) System.out.print(x[i].getValue() + ", ");
		
		System.out.println("init s = "+ s.getValue());
		int delta = s.getAssignDelta(x[2], 4);
		
	}

}
