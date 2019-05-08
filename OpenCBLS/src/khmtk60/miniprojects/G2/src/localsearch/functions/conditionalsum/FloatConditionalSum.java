package localsearch.functions.conditionalsum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import localsearch.functions.basic.*;
import localsearch.model.FloatAbstractInvariant;
import localsearch.model.FloatLocalSearchManager;
import localsearch.model.IFloatFunction;
import localsearch.model.VarIntLS;

public class FloatConditionalSum extends FloatAbstractInvariant implements IFloatFunction {

	private IFloatFunction _f;
	private FloatLocalSearchManager _ls = null;
	
//	public FloatConditionalSum(IFloatFunction[] f, IFloatFunction[] w, IFloatFunction val){
//		
//		_f = new FloatConditionalSumFuncFuncFunc(f,w,val);
//	}
	
	public FloatConditionalSum(VarIntLS[] x, int val){
		float[] w = new float[x.length];
		for(int i = 0; i < x.length; i++)
			w[i] = 1;
		_f = new FloatConditionalSumVarInt(x,w,val);
	}
	public FloatConditionalSum(VarIntLS[] x, float[] w, int val){
		
		_f = new FloatConditionalSumVarInt(x,w,val);
	}
	void post() {
		
		
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
		return _f.getAssignDelta(x,val);
	}

	@Override
	public float getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		return _f.getSwapDelta(x,y);
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
	
	/**
	 * @param args
	 */

	@Override
	public float getAssignDelta(VarIntLS x, int valx, VarIntLS y, int valy) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public FloatLocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _f.getLocalSearchManager();
	}
	

}