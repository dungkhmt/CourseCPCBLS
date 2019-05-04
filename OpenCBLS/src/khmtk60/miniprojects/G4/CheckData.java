package khmtk60.miniprojects.G4;

import java.util.HashSet;
import java.util.Set;

public class CheckData {
	MinMaxTypeMultiKnapsackInput input;

	public CheckData(String fn) {
		this.input = MinMaxTypeMultiKnapsackInput.loadFromFile(fn);
	}
	
	public static void main(String[] args) {
//		CheckData c = new CheckData("/home/thangnd/git/java/Optimization/data/MinMaxTypeMultiKnapsackInput-3000.json");
//		System.out.println(c.input.getItems()[0].getBinIndices());
		
		Set<Integer> a = new HashSet<Integer>();
		a.add(1);
		a.add(1);
		a.add(2);
		System.out.println(a);
		
	}

}
