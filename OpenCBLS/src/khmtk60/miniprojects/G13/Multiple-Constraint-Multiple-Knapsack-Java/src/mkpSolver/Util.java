package mkpSolver;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

class binLowWeightNeeded
{
	int index;
	double low_w_needed;
	int T = -1; //T = 0 thi khong tang T
	int R = -1; //R = 0 thi khong tang R
	public binLowWeightNeeded(int index, double low_w_needed)
	{
		this.index = index;
		this.low_w_needed = low_w_needed;
	}
	
	public binLowWeightNeeded(int index, double low_w_needed, int T, int R)
	{
		this.index = index;
		this.low_w_needed = low_w_needed;
		this.T = T;
		this.R = R;
	} 
	
	
}

class lowWComparator implements Comparator<binLowWeightNeeded> {
	public int compare(binLowWeightNeeded a, binLowWeightNeeded b) {
//		if(a.low_w_needed < 0) return -1;
		if(a.low_w_needed > b.low_w_needed) //Trong so cac bin co the xep, chon bin nao?
			return 1;
		return -1;
	}
}

class Move
{
	int index;
	int bin;
	public Move(int index, int new_bin)
	{
		this.index = index;
		this.bin = new_bin;
	}
}

class TRcomparator implements Comparator<binLowWeightNeeded> {
	public int compare(binLowWeightNeeded a, binLowWeightNeeded b) {
		if(a.R > b.R) //Trong so cac bin co the xep, chon bin nao?
			return 1;
		if(a.T > b.T)
			return 1;
		if(a.low_w_needed > b.low_w_needed) //Trong so cac bin co the xep, chon bin nao?
			return 1;
		return -1;
	}
}




public class Util {
	
	public int[] getSolution(int[] X, MinMaxTypeMultiKnapsackInput inputData, int max_num_type, int max_num_class)
	{
		MinMaxTypeMultiKnapsackInputBin[] bins = inputData.getBins();
		MinMaxTypeMultiKnapsackInputItem[] items = inputData.getItems();
		int n_bins = bins.length;
		int n_items = items.length;
		double[] cr_w = new double[n_bins];
//		double[] low_w_need = new double[n_bins];
		ArrayList<Integer> violation_bin = new ArrayList<Integer>();
		double[] cr_p = new double[n_bins];
		int[] cr_type = new int[n_bins];
		int[] cr_class = new int[n_bins];
		int[][] cr_bin_type = new int[n_bins][max_num_type+1];
		int[][] cr_bin_class = new int[n_bins][max_num_class+1];
		int[] solution = new int[n_items];
		for(int item = 0; item < n_items; item++)
		{
			if(X[item] != n_bins)
			{
				cr_w[X[item]] += items[item].getW();
				cr_p[X[item]] += items[item].getP();
				if(cr_bin_type[X[item]][items[item].getT()] == 0)
				{
					cr_bin_type[X[item]][items[item].getT()] = 1;
					cr_type[X[item]] += 1;
				}
				if(cr_bin_class[X[item]][items[item].getR()] == 0)
				{
					cr_bin_class[X[item]][items[item].getR()] = 1;
					cr_class[X[item]] += 1;
				}
			}
		}
		for(int bin = 0; bin < n_bins; bin++)
		{
			if(cr_w[bin] > 0)
			{
				if(bins[bin].getMinLoad() > cr_w[bin])
				{
					violation_bin.add(bin);
				}
			}
		}
		for(int i = 0; i < n_items; i++)
        {
			solution[i] = X[i];
        	for (int bin: violation_bin)
        	{
        		if(X[i] == bin)
        		{
        			solution[i] = n_bins;
        		}
        	}
        }
		return solution;
		
	}
	
	public int checkSolution(int[] solution, MinMaxTypeMultiKnapsackInput inputData, int max_num_type, int max_num_class)
	{
		MinMaxTypeMultiKnapsackInputBin[] bins = inputData.getBins();
		MinMaxTypeMultiKnapsackInputItem[] items = inputData.getItems();
		int n_bins = bins.length;
		int n_items = items.length;
		double[] cr_w = new double[n_bins];
		double[] cr_p = new double[n_bins];
		int[] cr_type = new int[n_bins];
		int[] cr_class = new int[n_bins];
		int[][] cr_bin_type = new int[n_bins][max_num_type+1];
		int[][] cr_bin_class = new int[n_bins][max_num_class+1];
		int solution_n_items = 0;
		for(int item = 0; item < n_items; item++)
		{
			if(solution[item] != n_bins)
			{
				cr_w[solution[item]] += items[item].getW();
				cr_p[solution[item]] += items[item].getP();
				if(cr_bin_type[solution[item]][items[item].getT()] == 0)
				{
					cr_bin_type[solution[item]][items[item].getT()] = 1;
					cr_type[solution[item]] += 1;
				}
				if(cr_bin_class[solution[item]][items[item].getR()] == 0)
				{
					cr_bin_class[solution[item]][items[item].getR()] = 1;
					cr_class[solution[item]] += 1;
				}
				solution_n_items++;
			}
			
		}
		for(int bin = 0; bin < n_bins; bin++)
		{
			if(cr_w[bin] > bins[bin].getCapacity() && bins[bin].getCapacity() > 0)
			{
				System.out.print(bin+" " + cr_w[bin] + "/" + bins[bin].getCapacity() );
				return 1;
			}
			if(cr_w[bin] > 0 && cr_w[bin] < bins[bin].getMinLoad())
			{
				System.out.print(bin+": " + cr_w[bin] + "/" + bins[bin].getMinLoad());
				return 2;
			}
			if(cr_p[bin] > bins[bin].getP() && bins[bin].getP() > 0)
			{
				System.out.print(bin+" " + cr_p[bin] + "/" + bins[bin].getP() );
				return 3;
			}
			if(cr_type[bin] > bins[bin].getT())
				return 4;
			if(cr_class[bin] > bins[bin].getR())
			{
				System.out.print(bin);
				return 5;
			}
		}
		return solution_n_items;
		
	}
	
	public void printMap(Map<Integer, Set<Integer>> map_item_type, Map<Integer, Set<Integer>> map_item_class)
	{
		System.out.println("Type:");
		for(Integer key: map_item_type.keySet())
		{
			System.out.print(key + ": ");
			for(Integer item: map_item_type.get(key))
			{
				System.out.print(item + " ");
			}
			System.out.print("\n");
		}
		System.out.println("Class:");
		for(Integer key: map_item_class.keySet())
		{
			System.out.print(key + ": ");
			for(Integer item: map_item_class.get(key))
			{
				System.out.print(item + " ");
			}
			System.out.print("\n");
		}
		
	}
	
	public void printInter(Map<Integer, Set<Integer>> map_item_type, Map<Integer, Set<Integer>> map_item_class)
	{
		int ni = 0; //num_item
		for(Integer key_type: map_item_type.keySet())
		{
			Set<Integer> list_items_type = map_item_type.get(key_type);
			for(Integer key_class: map_item_class.keySet())
			{
				System.out.print(key_type + "," + key_class + ": ");
				Set<Integer> list_items_class = map_item_class.get(key_class);
				Set<Integer> current_item = new HashSet<Integer>(list_items_type);
				current_item.retainAll(list_items_class);
				ni += current_item.size();
				for(Integer item: current_item)
				{
					System.out.print(item + ",");
				}
				System.out.print("\n");
			}
			System.out.print("\n");
		}
		System.out.println("NI: " + ni);
	}
	
	public int printSolution(int[] a, int result)
	{
		for(int bin: a)
			System.out.print(bin + ",");
		System.out.println("\nMax item: " + result);
		return result;
	}
}
