package cbls115676khmt61.NguyenVanSon_20163560.Search;

import java.util.HashMap;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Mysum extends AbstractInvariant implements IFunction{
	private VarIntLS[] x;
	private LocalSearchManager mgr;
	int minvalue, maxvalue, value;
	private HashMap<VarIntLS, Integer> map;
	//private boolean posted;
	public Mysum(VarIntLS[] x) {
		map = new HashMap<>();
		this.x = x;
		if(x == null || x.length ==0 ) {
			System.out.println("exeption 0-");
			return;
		}
	}
	
	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		value = 0;
		minvalue =0;
		maxvalue =0;
		for(int i = 0 ; i< x.length; i++) {
			value += x[i].getValue();
			minvalue += x[i].getMinValue();
			maxvalue += x[i].getMaxValue();
					
		}
		
	}
	@Override
	public void propagateInt(VarIntLS y, int val) {
		// TODO Auto-generated method stub
		// ham nay duoc goi sau khi bien x da duoc gan gia tri val(val = x[i].getvalue)
		
		super.propagateInt(y, val);
	}

	@Override
	public int getMinValue() {
		// TODO Auto-generated method stub
		return minvalue;
	}

	@Override
	public int getMaxValue() {
		// TODO Auto-generated method stub
		return maxvalue;
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public int getAssignDelta(VarIntLS y, int val) {
		// TODO Auto-generated method stub
		// tra vesu thay doi value neu y duoc gan gia tri value
		if(map.get(y) == null) return 0;
		
		return val - y.getValue();
	}

	@Override
	public int getSwapDelta(VarIntLS y1, VarIntLS y2) {
		// TODO Auto-generated method stub
		if(map.get(y1) == null) return getAssignDelta(y2, y1.getValue());
		if(map.get(y2) == null) return getAssignDelta(y1, y2.getValue());
		return 0;
		
	}
	public static void main(String[] args) {
		LocalSearchManager mgr = new LocalSearchManager();
		int N= 5;
		VarIntLS[] x = new VarIntLS[N];
		for(int i= 0; i< N; i++) {
			x[i] = new VarIntLS(mgr, 1,N);
			x[i].setValue(i);
		}
		Mysum s = new Mysum(x);
		mgr.close();
		System.out.println("init s = " + s.getValue());
		int delta = s.getAssignDelta(x[2], 4);
		x[2].setValuePropagate(4);
		for(int i = 0 ;i < N; i++) {
			
		}
	}

}
