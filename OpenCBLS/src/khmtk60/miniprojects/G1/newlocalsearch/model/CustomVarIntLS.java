package khmtk60.miniprojects.G1.newlocalsearch.model;

import java.util.Set;
import java.util.TreeSet;

import core.VarInt;

public class CustomVarIntLS extends VarInt {
	private int _oldValue;
	private TreeSet<Integer> _domain;
	
	public CustomVarIntLS(int min, int max){
		super(min,max);
		_domain = new TreeSet<Integer>();
		for(int  v = min; v <= max; v++)
			_domain.add(v);
	}

	public CustomVarIntLS(Set<Integer> domain){
		super(1,0);
		_domain = new TreeSet<Integer>();
		int min = 0;
		int max = 0;
		for(int v : domain){
			min = v; max = v; break;
		}
		for(int v : domain){
			_domain.add(v);
			min = min < v ? min : v;
			max = max > v ? max : v;
		}
		initBound(min,max);
	}
	
	public TreeSet<Integer> getDomain(){
		return _domain;
	}
	
	public void setValuePropagate(int v){
		_oldValue = getValue();
		super.setValue(v);
	}
	public void swapValuePropagate(CustomVarIntLS y){
		int tv = this.getValue();
		setValuePropagate(y.getValue());
		y.setValuePropagate(tv);
	}
	public int getOldValue(){
		return _oldValue;
	}
}
