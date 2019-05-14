package khmtk60.miniprojects.G10.multiknapsackminmaxtypeconstraints;

import khmtk60.miniprojects.G10.localsearch.model.LocalSearchManager;
import khmtk60.miniprojects.G10.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.G10.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputBin;
import khmtk60.miniprojects.G10.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputItem;


public class Demo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalSearchManager mgr = new LocalSearchManager();
		mgr.close();
		MinMaxTypeMultiKnapsackInput input = new MinMaxTypeMultiKnapsackInput();

		String path = "src\\khmtk60.miniprojects.G10.multiknapsackminmaxtypeconstraints\\MinMaxTypeMultiKnapsackInput.json";
		input = input.loadFromFile(path);
		MinMaxTypeMultiKnapsackInputBin[] bins = input.getBins();
		MinMaxTypeMultiKnapsackInputItem[] items = input.getItems();

		System.out.println(bins.length);
		System.out.println(items.length);

	}

}
