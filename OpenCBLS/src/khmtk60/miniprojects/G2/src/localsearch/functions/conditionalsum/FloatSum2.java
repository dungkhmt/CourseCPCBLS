package khmtk60.miniprojects.G2.src.localsearch.functions.conditionalsum;

import khmtk60.miniprojects.G2.src.localsearch.model.FloatAbstractInvariant;
import khmtk60.miniprojects.G2.src.localsearch.model.FloatLocalSearchManager;
import khmtk60.miniprojects.G2.src.localsearch.model.IFloatFunction;
import khmtk60.miniprojects.G2.src.localsearch.model.VarIntLS;

public class FloatSum2 extends FloatAbstractInvariant implements IFloatFunction{
	
	private IFloatFunction _f;
	private FloatLocalSearchManager _ls = null;
	
	public FloatSum2(VarIntLS[] x, int[] w, int val, int v) {
		_f = new FloatSumVarInt(x, w, val, v);
	}

	public String name(){
		return "ConditionalSum";
	}
	
	@Override
	public float getMinValue() {
		// TODO Auto-generated method stub
		return _f.getMinValue();
	}

	@Override
	public float getMaxValue() {
		// TODO Auto-generated method stub
		return _f.getMaxValue();
	}

	@Override
	public float getValue() {
		// TODO Auto-generated method stub
		return _f.getValue();
	}

	@Override
	public VarIntLS[] getVariables() {
		return _f.getVariables();
	}
	
	@Override
	public float getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		return _f.getAssignDelta(x, val);
	}

	@Override
	public float getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		return _f.getSwapDelta(x, y);
	}


	@Override
	public void propagateInt(VarIntLS x, int val) {
		// DO NOTHING
	}

	@Override
	public void initPropagate() {
		// DO NOTHING

	}
	
	@Override
	public boolean verify() {
		return _f.verify();
	}
	
	@Override
	public FloatLocalSearchManager getLocalSearchManager() {
		return _f.getLocalSearchManager();
	}
	
	@Override
	public float getAssignDelta(VarIntLS x, int valx, VarIntLS y, int valy) {
		// TODO Auto-generated method stub
		return 0;
	}

}
