package multiknapsackminmaxtypeconstraints;

import localsearch.model.LocalSearchManager;
import multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;
import multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputBin;
import multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputItem;


public class Demo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalSearchManager mgr = new LocalSearchManager();
		mgr.close();
		MinMaxTypeMultiKnapsackInput input = new MinMaxTypeMultiKnapsackInput();

		String path = "src\\multiknapsackminmaxtypeconstraints\\MinMaxTypeMultiKnapsackInput.json";
		input = input.loadFromFile(path);
		MinMaxTypeMultiKnapsackInputBin[] bins = input.getBins();
		MinMaxTypeMultiKnapsackInputItem[] items = input.getItems();

		System.out.println(bins.length);
		System.out.println(items.length);

	}

}
