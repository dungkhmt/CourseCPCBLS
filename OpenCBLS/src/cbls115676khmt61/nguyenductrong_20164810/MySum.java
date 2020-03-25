package BT;

import java.util.HashMap;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class MySum extends AbstractInvariant implements IFunction {

	private VarIntLS[] x;
	private LocalSearchManager mgr;
	int minValue;
	int maxValue;
	int value;
	private HashMap <VarIntLS, Integer> map;

	public MySum(VarIntLS[] x) {
		this.x = x;
		if (x == null || x.length == 0) {
			System.out.println("x must be not null");
			return;
		}
		map = new HashMap<VarIntLS, Integer>();
		mgr = x[0].getLocalSearchManager();
		mgr.post(this);
	}

	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void propagateInt(VarIntLS y, int val) {
		// TODO Auto-generated method stub
		if (map.get(y) == null)
			return;
		value = value - y.getOldValue() + y.getValue();
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		value = 0;
		minValue = 0;
		maxValue = 0;
		for (int i = 0; i < x.length; i++) {
			value += x[i].getValue();
			minValue += x[i].getMinValue();
			maxValue += x[i].getMaxValue();
		}
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMinValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAssignDelta(VarIntLS y, int val) {
		// TODO Auto-generated method stub
		// tra ve su thay doi neu y duoc gan gia tri val
		if (map.get(y) == null)
			return 0;
		return val - y.getValue();
	}

	@Override
	public int getSwapDelta(VarIntLS y1, VarIntLS y2) {
		// TODO Auto-generated method stub
		//tra ve su thay doi khi doi cho y1 y2
		if(map.get(y1) == null) return getAssignDelta(y2, y1.getValue());
		if(map.get(y2) == null) return getAssignDelta(y1, y2.getValue());
		
		return 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalSearchManager mgr = new LocalSearchManager();
		int N = 5;
		VarIntLS[] x = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			x[i] = new VarIntLS(mgr, 1, N);
			x[i].setValue(i);
		}

		MySum s = new MySum(x);
		mgr.close();

		for (int i = 0; i < N; i++)
			System.out.print(x[i].getValue() + " ");
		System.out.println("init s = " + s.getValue());
		int delta = s.getAssignDelta(x[2], 4);
		x[2].setValuePropagate(4);
		for (int i = 0; i < N; i++)
			System.out.print(x[i].getValue() + " ");
		System.out.println("delta = " + delta + ", new s = " + s.getValue());
	}

}
