package khmtk60.miniprojects.G4;

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
		SetCount s = new SetCount();
		s.add(5);
		s.add(5);
		s.add(5);
		s.add(6);
		s.add(7);
		s.add(7);
		System.out.println(s.size());
		s.remove(6);
		System.out.println(s.size());
	}
}
