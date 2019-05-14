package khmtk60.miniprojects.G2.src.localsearch.functions.sum;

//import localsearch.constraints.basic.LessOrEqual;
import khmtk60.miniprojects.G2.src.localsearch.functions.basic.*;
//import localsearch.functions.basic.FuncVarConst;
//import localsearch.functions.conditionalsum.ConditionalSum;
import khmtk60.miniprojects.G2.src.localsearch.model.*;

public class FloatSum extends FloatAbstractInvariant implements IFloatFunction {

	private IFloatFunction _f;

	public FloatSum(VarIntLS[] x) {
		_f = new FloatSumVar(x);
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
	public float getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		return _f.getAssignDelta(x, val);
	}

	@Override
	public float getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		return _f.getSwapDelta(x, y);
	}

	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return _f.getVariables();
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public boolean verify() {
		return _f.verify();
	}

	@Override
	public FloatLocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _f.getLocalSearchManager();
	}

	@Override
	public float getAssignDelta(VarIntLS x, int valx, VarIntLS y, int valy) {
		// TODO Auto-generated method stub
		return 0;
	}

}
