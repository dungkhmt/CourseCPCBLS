package BT;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class GraphPartitioningCost extends AbstractInvariant implements IFunction {
	
	private int n;
	private int[][] c;
	private LocalSearchManager mgr;
	private VarIntLS[] X;
	private int value;
	
	public GraphPartitioningCost(VarIntLS[] X, int[][] c) {
		// TODO Auto-generated constructor stub
		this.X = X;
		this.c = c;
		this.n = X.length;
		this.mgr = X[0].getLocalSearchManager();
		this.mgr.post(this);
		
	}
	
	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return X;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		if(val != x.getOldValue())
		for(int i = 0;i < X.length;i++) if(i != x.getID()) {
			if(X[i].getValue() != val) {
				this.value += c[i][x.getID()];
			} else {
				this.value -= c[i][x.getID()];
			}
		}
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		value = 0;
		for (int i = 0; i < n; i++)
			for (int j = i + 1; j < n; j++) {
				if (X[i].getValue() != X[j].getValue()) {
					value += c[i][j];
				}
			}
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return mgr;
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
		return this.value;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		int delta = 0;
		if (x.getValue() == val)
			delta = 0;
		else {
			for (int i = 0; i < n; i++)
				if (x.getID() != i) {
					if(x.getValue() != X[i].getValue()) delta -= c[i][x.getID()];
					else delta += c[i][x.getID()];
				}
		}
		return delta;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
//		for(int i=0;i<X.length;i++) System.out.print(X[i].getValue() + " ");
//		System.out.println();
		int delta = 0;
		for (int i = 0; i < n; i++) {
			if (i != y.getID()) {
				if (X[i].getValue() != y.getValue())
					delta -= c[i][y.getID()];
				else
					delta += c[i][y.getID()];
			}

			if (i != x.getID()) {
				if (X[i].getValue() != x.getValue())
					delta -= c[i][x.getID()];
				else
					delta += c[i][x.getID()];
			}
		}

		return delta;
	}


}
