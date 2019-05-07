package khmtk60.miniprojects.G1.newlocalsearch.search;

import khmtk60.miniprojects.G1.newlocalsearch.model.CustomVarIntLS;

public class CustomTwoVariablesSwapMove extends Move{
	private CustomVarIntLS	_x;
	private CustomVarIntLS	_y;
	private int 		_id1;
	private int 		_id2;
	
	public CustomTwoVariablesSwapMove(MoveType type, double eval, CustomVarIntLS x, CustomVarIntLS y, int id1, int id2){
		super(type,eval);
		_x = x; 
		_y = y;
		_id1 = id1;
		_id2 = id2;
	}
	public CustomVarIntLS getVar1(){ 
		return _x;
	}
	public CustomVarIntLS getVar2(){
		return _y;
	}
	public int getId1() {
		return _id1;
	}
	public int getId2() {
		return _id2;
	}
}
