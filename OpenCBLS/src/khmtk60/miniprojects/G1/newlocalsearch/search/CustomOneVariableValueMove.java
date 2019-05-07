package khmtk60.miniprojects.G1.newlocalsearch.search;

import khmtk60.miniprojects.G1.newlocalsearch.model.CustomVarIntLS;

public class CustomOneVariableValueMove extends Move{
	private CustomVarIntLS	_x;
	private int			_value;
	private int 		_id;
	
	public CustomOneVariableValueMove(MoveType type, double eval, CustomVarIntLS x, int value, int id){
		super(type, eval);
		_x = x; 
		_value = value;
		_id = id;
	}
	public CustomVarIntLS getVariable(){ 
		return _x;
	}
	
	public int getValue(){
		return _value;
	}
	
	public int getId() {
		return _id;
	}
}
