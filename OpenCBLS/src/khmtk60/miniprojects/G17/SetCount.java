package khmtk60.miniprojects.G17;

import java.util.HashMap;

public class SetCount {
	private HashMap<Integer, Integer> x;
	
	public SetCount() {
		this.x = new HashMap<Integer, Integer>();
	}
	
	public void add(int i) {
		x.compute(i, (key, oldValue) -> ((oldValue == null) ? 1 : oldValue+1));
	}

	public void remove(int i) {
		if (x.get(i) == 1) {
			x.remove(i);
		} else {
			x.put(i, x.get(i) - 1);
		}
	}
	
	public int size() {
		return x.size();
	}
	
	public void clear() {
		x.clear();
	}
	
	public static void main(String[] args) {
	}
}