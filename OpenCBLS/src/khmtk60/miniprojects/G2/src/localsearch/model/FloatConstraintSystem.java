package localsearch.model;

import java.util.*;

public class FloatConstraintSystem extends FloatAbstractInvariant implements IFloatConstraint{

	FloatLocalSearchManager _mgr = null;
	private ArrayList<FloatInvariant> _constraints = new ArrayList<FloatInvariant>();
	private VarIntLS[] _x;
	private float _totalviolations;
	
	private HashMap<VarIntLS, HashSet<IFloatConstraint>> mapVarIntLSConstraints = null;
	private float[] _violations;
	private HashMap<IFloatConstraint, Integer> mapC;
	
	public FloatConstraintSystem(FloatLocalSearchManager mgr){
		this._mgr = mgr;
		mgr.post(this);
	}
	
	public void post(FloatInvariant c){
		_constraints.add(c);		
	}
	
	public String name(){ return "ConstraintSystem";}
	
	public void close(){
		HashSet<VarIntLS> S = new HashSet<VarIntLS>();
		mapVarIntLSConstraints = new HashMap<VarIntLS, HashSet<IFloatConstraint>>();
		for(int i = 0; i < _constraints.size(); i++){
			IFloatConstraint c = (IFloatConstraint)_constraints.get(i);
			VarIntLS[] x = c.getVariables();
			for(int j = 0; j < x.length; j++)
				S.add(x[j]);
		}
		_x = new VarIntLS[S.size()];
		Iterator<VarIntLS> it = S.iterator();
		int idx = -1;
		while(it.hasNext()){
			VarIntLS x = (VarIntLS)it.next();
			idx++;
			_x[idx] = x;
			mapVarIntLSConstraints.put(x, new HashSet<IFloatConstraint>());
		}
		for(int i = 0; i < _constraints.size(); i++){
			IFloatConstraint c = (IFloatConstraint)_constraints.get(i);
			VarIntLS[] x = c.getVariables();
			for(int j = 0; j < x.length; j++){
				mapVarIntLSConstraints.get(x[j]).add(c);
			}
				
		}
		mapC = new HashMap<IFloatConstraint, Integer>();
		for(int i = 0; i < _constraints.size(); i++)
			mapC.put((IFloatConstraint)_constraints.get(i), i);
		_violations = new float[_constraints.size()];
	}
		
	@Override
	public float violations() {
		// TODO Auto-generated method stub
		return _totalviolations;
	}

	@Override
	public float violations(VarIntLS x) {
		HashSet<IFloatConstraint> C = mapVarIntLSConstraints.get(x);
		if(C == null) return 0;
		float v = 0;
		Iterator<IFloatConstraint> it = C.iterator();
		while(it.hasNext()){
			IFloatConstraint c = (IFloatConstraint)it.next();
			v += c.violations(x);
		}
		return v;
	}


	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return _x;
	}
	
	@Override
	public float getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		float delta = 0;
		HashSet<IFloatConstraint> C = mapVarIntLSConstraints.get(x);
		if(C == null) return 0;
		Iterator<IFloatConstraint> it = C.iterator();
		while(it.hasNext()){
			IFloatConstraint c = (IFloatConstraint)it.next();
			delta += c.getAssignDelta(x, val);
		}
		return delta;
	}

	public float getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		float delta = 0;
		//HashSet<IConstraint> C = mapVarIntLSConstraints.get(x);
		HashSet<IFloatConstraint> C = new HashSet<IFloatConstraint>();
		HashSet<IFloatConstraint> Cx = mapVarIntLSConstraints.get(x);
		HashSet<IFloatConstraint> Cy = mapVarIntLSConstraints.get(y);
		
		if((Cx == null) && (Cy == null)){
			//System.out.println(name() + "::getSwapDelta, Error:: 2 variables not in Constraint System ");
			return 0;
		}
		
		if(Cx != null){
			for (IFloatConstraint c : Cx) {
				C.add(c);
			}
		}
		
		if(Cy != null){
			for (IFloatConstraint c : Cy) {
				C.add(c);
			}
		}
		
		Iterator<IFloatConstraint> it = C.iterator();
		while(it.hasNext()){
			IFloatConstraint c = (IFloatConstraint)it.next();
			delta += c.getSwapDelta(x, y);
		}
		return delta;
	}
	
	@Override
	public void propagateInt(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		//System.out.println(name() + "::propagateInt(x[" + x.getID() + "], " + val + ")");
		HashSet<IFloatConstraint> C = mapVarIntLSConstraints.get(x);
		if(C == null) return;
		Iterator<IFloatConstraint> it = C.iterator();
		while(it.hasNext()){
			IFloatConstraint c = (IFloatConstraint)it.next();
			int idc = mapC.get(c);
			_totalviolations += (c.violations() - _violations[idc]);
			_violations[idc] = c.violations();
		}
	}
	
	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		//System.out.println(name() + "::initPropagate");
		_totalviolations = 0;
		for(int i = 0; i < _constraints.size(); i++){
			_violations[i] = ((IFloatConstraint)_constraints.get(i)).violations();
			_totalviolations += _violations[i];
		}
	}
//	@Override
//	public FloatLocalSearchManager getFloatLocalSearchManager() {
//		// TODO Auto-generated method stub
//		return _mgr;
//	}
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		for(int i = 0; i < _constraints.size(); i++){
			if(!_constraints.get(i).verify()) return false;
		}
		return true;
	}

	@Override
	public FloatLocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _mgr;
	}

	public void post(IFloatFunction a) {
		// TODO Auto-generated method stub
		
	}

}
