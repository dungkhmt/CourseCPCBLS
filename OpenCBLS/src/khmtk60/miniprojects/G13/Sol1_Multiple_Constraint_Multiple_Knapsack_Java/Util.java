package khmtk60.miniprojects.G13.Sol1_Multiple_Constraint_Multiple_Knapsack_Java;

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

/*
 package mkpSolver;

import java.util.*;

public class HillClimbing {
	//Data
	MinMaxTypeMultiKnapsackInput inputData;
	int n_items;
	int n_bins;
	MinMaxTypeMultiKnapsackInputItem[] items;
	public MinMaxTypeMultiKnapsackInputBin[] bins;
	Map<Integer, Set<Integer>> map_item_type;
	Map<Integer, Set<Integer>> map_item_class;
	//Decision variable
	public int[] X; // Thay doi sau moi vong lap
	//Variable need for constraint
	double[] low_w_needed; //Thay doi lien tuc trong qua trinh tim kiem
	Map<Integer, Double> current_violation_bin; //Luu lai cac bin dang vi pham va low_w_need cua no
	Map<Integer, Set<Integer>> current_violation_item; //Cac item dang vi pham va vi tri bin cua no
	double[] current_w;
	double[] current_p;
	int[] current_type;
	int[] current_class;
	int[][] current_bin_type;
	int[][] current_bin_class;
	int current_num_item;
	double violation; //Sum of all low_w_need
	int most_violation_bin;
	//Violation
	List<Integer> violation_bin;
	List<Integer> violation_item;
	List<Integer> list_items_unpack;
	List<Integer> empty_bin;
	//
	int max_num_type = 0;
	int max_num_class = 0;
	
	public HillClimbing(MinMaxTypeMultiKnapsackInput inputData)
	{
		this.inputData = inputData;
		this.items = this.inputData.getItems();
		this.bins = this.inputData.getBins();
		this.n_items = this.items.length;
		this.n_bins = this.bins.length;
		this.map_item_type = new HashMap<Integer, Set<Integer>>();
		this.map_item_class = new HashMap<Integer, Set<Integer>>();
		this.X = new int[n_items];
		for(int i = 0; i < n_items; i++)
			this.X[i] = this.n_bins; //Mac dinh la cac item chua duoc xep vao bin nao.
		this.violation_bin = new ArrayList<Integer>();
		this.violation_item = new ArrayList<Integer>();
		this.list_items_unpack = new ArrayList<Integer>();
		this.empty_bin = new ArrayList<Integer>();
		this.processData();
		//Init variable constraint
		this.low_w_needed = new double[this.n_bins+1];
		this.current_w = new double[this.n_bins];
		this.current_p = new double[this.n_bins];
		this.current_type = new int[this.n_bins];
		this.current_class = new int[this.n_bins];
		this.current_bin_type = new int[this.n_bins][this.max_num_type+1];
		this.current_bin_class = new int[this.n_bins][this.max_num_class+1];
		this.current_num_item = 0;
		this.current_violation_bin = new HashMap<Integer, Double>();
		this.current_violation_item = new HashMap<Integer, Set<Integer>>();
		this.most_violation_bin = 0;
	}
	
	public void processData()
	{
		for(int i = 0; i < n_items; i++)
		{
			if(this.items[i].getT() > this.max_num_type)
				this.max_num_type = items[i].getT();
			if(this.items[i].getR() > this.max_num_class)
				this.max_num_class = this.items[i].getR();
			Integer type_key = Integer.valueOf(this.items[i].getT());
			if(!this.map_item_type.containsKey(type_key))
			{
				Set<Integer> list_item = new HashSet<>();
				list_item.add(Integer.valueOf(i));
				this.map_item_type.put(type_key, list_item);
			}
			else
			{
				Set<Integer> list_item = this.map_item_type.get(type_key);
				list_item.add(Integer.valueOf(i));
			}
			Integer class_key = Integer.valueOf(this.items[i].getR());
			if(!this.map_item_class.containsKey(class_key))
			{
				Set<Integer> list_item = new HashSet<>();
				list_item.add(Integer.valueOf(i));
				this.map_item_class.put(class_key, list_item);
			}
			else
			{
				Set<Integer> list_item = this.map_item_class.get(class_key);
				list_item.add(Integer.valueOf(i));
			}
		}
	}
	
	public int[] neighborsSearch()
	{
		List<Move> possible_move = new ArrayList<>();
		Random random_move = new Random();
		Comparator<binLowWeightNeeded> tr_comparator = new TRcomparator();
		PriorityQueue<binLowWeightNeeded> list_move_heuristic = new PriorityQueue<binLowWeightNeeded>(tr_comparator);
		for(int i = 0; i < this.n_items; i++)
		{
			int bin_selected = -1;
			double delta = 0;
			list_move_heuristic.clear();
			possible_move.clear();
			if(X[i] == this.n_bins)
			{
				for(int bin: this.items[i].getBinIndices())
				{
					if(this.checkValidBin(bin, i))
					{
						Move move = new Move(i, bin);
						possible_move.add(move);
						Move new_move = new Move(i, bin);
						possible_move.add(new_move);
						if(this.checkIncreaseTypeClass(bin, i) == 0)
						{
							binLowWeightNeeded binLw = new binLowWeightNeeded(bin, this.low_w_needed[bin], 0, 0);
							list_move_heuristic.add(binLw);
						}
						else if(this.checkIncreaseTypeClass(bin, i) == 1)
						{
							binLowWeightNeeded binLw = new binLowWeightNeeded(bin, this.low_w_needed[bin], -1, 0);
							list_move_heuristic.add(binLw);
						}
						else if(this.checkIncreaseTypeClass(bin, i) == 2)
						{
							binLowWeightNeeded binLw = new binLowWeightNeeded(bin, this.low_w_needed[bin], 0, -1);
							list_move_heuristic.add(binLw);
						}
						else if(this.current_w[bin] != 0)
						{
							binLowWeightNeeded binLw = new binLowWeightNeeded(bin, this.low_w_needed[bin], -1, -1);
							list_move_heuristic.add(binLw);
						}
						double temp_violation = this.getAssignDelta(i, bin, X[i]); 
						if(temp_violation < delta)
						{
							delta = temp_violation;
							bin_selected = bin;
						}
						
					}
				}
				if(bin_selected != -1)
				{
					int old_bin_index = X[i];
					X[i] = bin_selected;
					this._updateConstraint(i, bin_selected, old_bin_index);
				}
				else if(list_move_heuristic.size() > 0)
				{
					binLowWeightNeeded new_bin = list_move_heuristic.remove();
					bin_selected = new_bin.index;
					int old_bin_index = X[i];
					X[i] = bin_selected;
					this._updateConstraint(i, bin_selected, old_bin_index);
				}
				else if(possible_move.size() > 0)
				{
					int index = random_move.nextInt(possible_move.size());
					Move m = possible_move.get(index);
					bin_selected = m.bin;
					int old_bin_index = X[i];
					X[i] = bin_selected;
					this._updateConstraint(i, bin_selected, old_bin_index);
				}
			}
			else
			{
				for(int bin: this.items[i].getBinIndices())
				{
					if(this.checkValidBin(bin, i) && X[i] != bin)
					{
						{
							Move move = new Move(i, bin);
							possible_move.add(move);
							Move new_move = new Move(i, bin);
							possible_move.add(new_move);
							if(this.checkIncreaseTypeClass(bin, i) == 0)
							{
								binLowWeightNeeded binLw = new binLowWeightNeeded(bin, -this.current_w[bin], 0, 0);
								list_move_heuristic.add(binLw);
							}
							else if(this.checkIncreaseTypeClass(bin, i) == 1)
							{
								binLowWeightNeeded binLw = new binLowWeightNeeded(bin, -this.current_w[bin], -1, 0);
								list_move_heuristic.add(binLw);
							}
							else if(this.checkIncreaseTypeClass(bin, i) == 2)
							{
								binLowWeightNeeded binLw = new binLowWeightNeeded(bin, -this.current_w[bin], 0, -1);
								list_move_heuristic.add(binLw);
							}
							else if(this.current_w[bin] != 0)
							{
								binLowWeightNeeded binLw = new binLowWeightNeeded(bin, -this.current_w[bin], -1, -1);
								list_move_heuristic.add(binLw);
							}
						}
						double temp_violation = this.getAssignDelta(i, bin, X[i]); 
						if(temp_violation < delta)
						{
							delta = temp_violation;
							bin_selected = bin;
						}
					}
				}
				if(bin_selected != -1)
				{
					int old_bin_index = X[i];
					X[i] = bin_selected;
					this._updateConstraint(i, bin_selected, old_bin_index);
				}
				else if(list_move_heuristic.size() > 0)
				{
					binLowWeightNeeded new_bin = list_move_heuristic.remove();
					bin_selected = new_bin.index;
					int old_bin_index = X[i];
					X[i] = bin_selected;
					this._updateConstraint(i, bin_selected, old_bin_index);
				}
			}
			
		}
		return X;
	}
	
	public void add_item_to_bin()
	{
		for(int item : this.violation_item)
//		for(int item : this.violation_item)
		{
			double min_c_w = Double.MAX_VALUE;
			int bin_selected = this.n_bins;
			for(int bin: this.items[item].getBinIndices())
			{
				if(this.checkValidBin(bin, item)
						&& !this.violation_bin.contains(Integer.valueOf(bin))
						)
				{
					if(min_c_w > this.bins[bin].getCapacity())
					{
						min_c_w = this.bins[bin].getCapacity();
						bin_selected = bin;
					}
				}
			}
			if(bin_selected < this.n_bins)
			{
				int old_bin_index = X[item];
				X[item] = bin_selected;
				this._updateConstraint(item, bin_selected, old_bin_index);
			}
		}
	}
	
	public double getAssignDelta(int item_index, int new_bin_index, int old_bin_index)
	{
		MinMaxTypeMultiKnapsackInputItem item = this.items[item_index];
		MinMaxTypeMultiKnapsackInputBin new_bin = this.bins[new_bin_index];
		double temp_violation = this.violation;
		if(this.low_w_needed[new_bin_index] > 0)
			temp_violation -= this.low_w_needed[new_bin_index];
		double temp_low_w_need = new_bin.getMinLoad() - (this.current_w[new_bin_index]+item.getW());
		if(temp_low_w_need > 0)
			temp_violation += temp_low_w_need;
		if(old_bin_index != this.n_bins)
		{
			MinMaxTypeMultiKnapsackInputBin old_bin = this.bins[old_bin_index];
			temp_low_w_need = this.low_w_needed[old_bin_index];
			if(temp_low_w_need > 0)
				temp_violation -= temp_low_w_need;
			double temp_current_w = this.current_w[old_bin_index];
			if(temp_current_w == 0)
				temp_low_w_need = 0;
			else
				temp_low_w_need = old_bin.getMinLoad() - temp_current_w;
			if(temp_low_w_need > 0)
				temp_violation += temp_low_w_need;
		}
		return temp_violation-this.violation;
	}
	
	public int[] greedy_search()
	{
		Arrays.fill(this.X, this.n_bins);
		Comparator<binLowWeightNeeded> comparator = new lowWComparator();
		for(Integer key_type: map_item_type.keySet())
		{
			Set<Integer> list_items_type = map_item_type.get(key_type);
			for(Integer key_class: map_item_class.keySet())
			{
				Set<Integer> list_items_class = map_item_class.get(key_class);
				Set<Integer> current_item_set = new HashSet<Integer>(list_items_type);
				current_item_set.retainAll(list_items_class);
				List<Integer> current_item = new ArrayList<Integer>();
				for(Integer item: current_item_set)
					current_item.add(item);
				PriorityQueue<binLowWeightNeeded> bin_candidate = new PriorityQueue<binLowWeightNeeded>(this.n_bins, comparator);
				for(Integer item: current_item)
				{
					int old_bin_index = X[item];
					PriorityQueue<binLowWeightNeeded> bins = new PriorityQueue<binLowWeightNeeded>(bin_candidate);
					while(bins.size() != 0)
					{
						binLowWeightNeeded bin = bins.remove();
						int bin_index = bin.index;
						if(checkValidBin(bin_index, item))
						{
							X[item] = bin_index;
							this._updateConstraint(item, bin_index, old_bin_index);
							break;
						}
					}
					if(X[item] == (this.n_bins)) //Chua xep duoc
					{
						for(int bin_index: this.inputData.getItems()[item].getBinIndices())
						{
							if(checkValidBin(bin_index, item))
							{
								this.X[item] = bin_index;
								this._updateConstraint(item, bin_index, old_bin_index);
								binLowWeightNeeded bin = new binLowWeightNeeded(bin_index, low_w_needed[bin_index]);
								bin_candidate.add(bin);
								break;
							}
						}
					}
				}
			}
		}
		System.out.println("Current N_items: " + this.getCurrentNumItem() + " Violation: " + this.violation);
//		this.resetAllBinViolation();
		return this.X;
	}
	
	public int[] distribute_item()
	{
		Comparator<binLowWeightNeeded> comparator = new lowWComparator();
		for(Integer key_type: map_item_type.keySet())
		{
			Set<Integer> list_items_type = map_item_type.get(key_type);
			for(Integer key_class: map_item_class.keySet())
			{
				Set<Integer> list_items_class = map_item_class.get(key_class);
				Set<Integer> current_item_set = new HashSet<Integer>(list_items_type);
				current_item_set.retainAll(list_items_class);
				List<Integer> current_item = new ArrayList<Integer>();
				for(Integer item: current_item_set)
					current_item.add(item);
				PriorityQueue<binLowWeightNeeded> bin_candidate = new PriorityQueue<binLowWeightNeeded>(this.n_bins, comparator);
				for(Integer item: current_item)
				{
					int old_bin_index = X[item];
					int bin_selected = -1;
					double delta = 0;
					PriorityQueue<binLowWeightNeeded> bins = new PriorityQueue<binLowWeightNeeded>(bin_candidate);
					while(bins.size() != 0)
					{
						binLowWeightNeeded bin = bins.remove();
						int bin_index = bin.index;
						if(checkValidBin(bin_index, item))
						{
							double temp_violation = this.getAssignDelta(item, bin_index, X[item]); 
							if(temp_violation < delta)
							{
								delta = temp_violation;
								bin_selected = bin_index;
							}
						}
					}
					if(bin_selected != -1)
					{
						this.X[item] = bin_selected;
						this._updateConstraint(item, bin_selected, old_bin_index);
					}
					if(X[item] == (this.n_bins)) //Chua xep duoc
					{
						for(int bin_index: this.inputData.getItems()[item].getBinIndices())
						{
							if(checkValidBin(bin_index, item))
							{
								double temp_violation = this.getAssignDelta(item, bin_index, X[item]); 
								if(temp_violation < delta)
								{
									delta = temp_violation;
									bin_selected = bin_index;
								}
//								this.X[item] = bin_index;
//								this._updateConstraint(item, bin_index, old_bin_index);
//								binLowWeightNeeded bin = new binLowWeightNeeded(bin_index, low_w_needed[bin_index]);
//								bin_candidate.add(bin);
//								break;
							}
						}
						if(bin_selected != -1)
						{
							this.X[item] = bin_selected;
							this._updateConstraint(item, bin_selected, old_bin_index);
							binLowWeightNeeded bin = new binLowWeightNeeded(bin_selected, low_w_needed[bin_selected]);
							bin_candidate.add(bin);
						}
					}
				}
			}
		}
		System.out.println("Current N_items: " + this.getCurrentNumItem() + " Violation: " + this.violation);
//		this.resetAllBinViolation();
		return X;
	}
	
	public boolean checkValidBin(int bin_index, int item_index)
	{
		MinMaxTypeMultiKnapsackInputItem item = this.items[item_index];
		MinMaxTypeMultiKnapsackInputBin bin = this.bins[bin_index];
		//Check constraint type, class
		if(current_bin_type[bin_index][item.getT()] == 0)
		{
			if(current_type[bin_index] + 1 > bin.getT())
				return false;
		}
		if(current_bin_class[bin_index][item.getR()] == 0)
		{
			if(current_class[bin_index] + 1 > bin.getR())
				return false;
		}
		if(current_w[bin_index] + item.getW() > bin.getCapacity())
			return false;
		if(current_p[bin_index] + item.getP() > bin.getP())
			return false;
		return true;
	}
	
	public int checkIncreaseTypeClass(int bin_index, int item_index)
	{
		int check_type = 0;
		int check_class = 0;
		if(this.current_bin_type[bin_index][this.items[item_index].getT()] == 0)
			check_type = 1; //Tang T
		if(this.current_bin_class[bin_index][this.items[item_index].getR()] == 0)
			check_class = 2; //Tang R
		return check_type+check_class;
	}

	public void _updateConstraint(int item_index, int new_bin_index, int old_bin_index)
	{
		if(this.low_w_needed[new_bin_index] > 0)
			this.violation -= this.low_w_needed[new_bin_index];
		MinMaxTypeMultiKnapsackInputItem item = this.items[item_index];
		MinMaxTypeMultiKnapsackInputBin bin = this.bins[new_bin_index];
		this.current_w[new_bin_index] += item.getW();
		this.current_p[new_bin_index] += item.getP();
		this.low_w_needed[new_bin_index] = bin.getMinLoad() - this.current_w[new_bin_index];
		if(this.current_bin_type[new_bin_index][item.getT()] == 0)
			this.current_type[new_bin_index] += 1;
		this.current_bin_type[new_bin_index][item.getT()] += 1;
		if(this.current_bin_class[new_bin_index][item.getR()] == 0)
			this.current_class[new_bin_index] += 1;
		this.current_bin_class[new_bin_index][item.getR()] += 1;
		if(this.low_w_needed[new_bin_index] > 0)
		{
			this.violation += this.low_w_needed[new_bin_index];
			this.current_violation_bin.put(Integer.valueOf(new_bin_index), this.low_w_needed[new_bin_index]);
			if(this.low_w_needed[new_bin_index] > this.low_w_needed[this.most_violation_bin])
			{
				this.most_violation_bin = new_bin_index;
			}
//			Set<Integer> list_item = this.current_violation_item.get(new_bin_index);
//			list_item.add(item_index);
//			this.current_violation_item.put(Integer.valueOf(new_bin_index), list_item);
		}
		else if(this.current_violation_bin.containsKey(Integer.valueOf(new_bin_index)))
		{ 
			this.current_violation_bin.remove(Integer.valueOf(new_bin_index));
		}
		if(old_bin_index != this.n_bins) //Neu bin cu cua item khong phai la bin cuoi thi phai cap nhat rang buoc
		{
			if(this.low_w_needed[old_bin_index] > 0)
				this.violation -= this.low_w_needed[old_bin_index];
			MinMaxTypeMultiKnapsackInputBin old_bin = this.bins[old_bin_index];
			this.current_w[old_bin_index] -= item.getW();
			this.current_p[old_bin_index] -= item.getP();
			if(this.current_w[old_bin_index] == 0)
				this.low_w_needed[old_bin_index] = 0;
			else
				this.low_w_needed[old_bin_index] = old_bin.getMinLoad() - this.current_w[old_bin_index];
			this.current_bin_type[old_bin_index][item.getT()] -= 1;
			this.current_bin_class[old_bin_index][item.getR()] -= 1;
			if(this.current_bin_type[old_bin_index][item.getT()] == 0)
				this.current_type[old_bin_index] -= 1;
			if(this.current_bin_class[old_bin_index][item.getR()] == 0)
				this.current_class[old_bin_index] -= 1;
			if(this.low_w_needed[old_bin_index] > 0)
			{
				this.violation += this.low_w_needed[old_bin_index];
				this.current_violation_bin.put(Integer.valueOf(old_bin_index), this.low_w_needed[old_bin_index]);
//				Set<Integer> list_item = this.current_violation_item.get(old_bin_index);
//				list_item.add(item_index);
//				this.current_violation_item.put(Integer.valueOf(old_bin_index), list_item);
			}
			else
			{
				this.current_violation_bin.remove(Integer.valueOf(old_bin_index));
			}
		}
//		this.violation = this.getCurrentViolation();
//		this.current_num_item = this.getCurrentNumItem();
	}
	
	public int getCurrentNumItem()
	{
		List<Integer> vi = new ArrayList<>(); 
		for(int i = 0; i < this.n_items; i++)
        {
        	if(this.X[i] == this.n_bins)
        	{
        		vi.add(i);
        		continue;
        	}
        	for (int bin: this.current_violation_bin.keySet())
        	{
        		if(this.X[i] == bin)
        		{
        			vi.add(i);
        		}
        	}
        }
		return this.n_items - vi.size();
	}
	
	public int selectMostViolationBin()
	{
		int bin_selected = -1;
		double most_violation = 0;
		for(int bin: this.current_violation_bin.keySet())
		{
			if(this.low_w_needed[bin] > most_violation)
			{
				most_violation = this.low_w_needed[bin];
				bin_selected = bin;
			}
		}
		return bin_selected;
	}
	
	public int selectMostWBin()
	{
		int bin_selected = -1;
		double most_w = 0;
		for(int bin = 0; bin < this.n_bins; bin++)
		{
			if(this.current_w[bin] > most_w)
			{
				most_w = this.current_w[bin];
				bin_selected = bin;
			}
		}
		return bin_selected;
	}
	
	public double getCurrentViolation()
	{
		double result = 0;
		for(Integer bin: this.current_violation_bin.keySet())
			result += this.current_violation_bin.get(bin);
		return result;
	}
	
	public void resetAllBinViolation() //Su dung khi su dung chien luoc tim kiem doc lap moi, cho cac item chua xep
										//duoc vao bin cuoi
	{
		this.violation_bin.clear();
		this.violation_item.clear();
		this.current_violation_bin.clear();
		this.violation = 0;
        for(int i = 0; i < this.n_bins; i++)
        {
        	if(this.current_w[i] == 0)
        	{
        		this.empty_bin.add(i);
        		continue;
        	}
        	if(this.low_w_needed[i] > 0.0)
        	{//Reset bin
        		this.violation_bin.add(i);
        		this.empty_bin.add(i);
        		this.current_w[i] = 0;
        		this.current_p[i] = 0;
        		this.low_w_needed[i] = 0;
        		this.current_class[i] = 0;
        		this.current_type[i] = 0;
        		Arrays.fill(this.current_bin_class[i], 0);
        		Arrays.fill(this.current_bin_type[i], 0); 
        	}       		
        }
        for(int i = 0; i < this.n_items; i++)
        {
        	if(this.X[i] == this.n_bins)
        	{
        		this.violation_item.add(i);
        		continue;
        	}
        	for (int bin: violation_bin)
        	{
        		if(this.X[i] == bin)
        		{
        			violation_item.add(i);
        			X[i] = this.n_bins;
        		}
        	}
        }
        this.list_items_unpack = this.violation_item;
        this.violation = 0;
		this.current_num_item = this.getCurrentNumItem();
	}
	
	public void resetBin(int bin_index)
	{
		this.current_violation_bin.remove(Integer.valueOf(bin_index));
		this.current_w[bin_index] = 0;
		this.current_p[bin_index] = 0;
		this.low_w_needed[bin_index] = 0;
		this.current_class[bin_index] = 0;
		this.current_type[bin_index] = 0;
		Arrays.fill(this.current_bin_class[bin_index], 0);
		Arrays.fill(this.current_bin_type[bin_index], 0);
		for(int i = 0; i < this.n_items; i++)
		{
			if(X[i] == bin_index)
				X[i] = this.n_bins;
		}
	}
	
	public int[] search(int max_iter, int time_not_change)
	{
		int current_solution = 0;
		int[] result = new int[this.n_items];
		int[] temp = new int[this.n_items];
		for(int i = 0; i < 4000; i++)
		{
			System.out.println("Step " + i + ": ");
			this.distribute_item();
//			this.greedy_search();
//			this.add_item_to_bin();
			this.resetAllBinViolation();
			temp = this.neighborsSearch();
			System.out.println("Violation: " + this.violation);
			this.current_num_item = this.getCurrentNumItem();
			if(current_solution < this.current_num_item)
			{
				System.arraycopy(temp, 0, result, 0, temp.length);
				current_solution = this.current_num_item;
				time_not_change = 0;
			}
			System.out.println("Max item so far: " + current_solution);
			time_not_change++;
			if(time_not_change == 200)
			{
				int bin_selected = this.selectMostViolationBin(); 
				if(bin_selected != -1)
					this.resetBin(bin_selected);
			}
			if(time_not_change == 400)
			{
				int bin_selected = this.selectMostWBin(); 
				if(bin_selected != -1)
					this.resetBin(bin_selected);
			}
//			if(time_not_change == 450)
//			{
//				System.arraycopy(result, 0, this.X, 0, temp.length);
//			}
//				this.resetAllBinViolation();
			if(false
					|| time_not_change == 800
					)
				break;
		}
		System.arraycopy(result, 0, this.X, 0, temp.length);
		this.resetAllBinViolation();
		for(int i = 0; i < 10; i++)
		{
			this.add_item_to_bin();
			this.resetAllBinViolation();
		}
		return this.X;
	}
	
	public static void main(String[] args) {
		//Load data
		MinMaxTypeMultiKnapsackInput inputData = new MinMaxTypeMultiKnapsackInput();
		Util utils = new Util();
		int promise = 0;
		inputData = inputData.loadFromFile("data/sample.json");
//		inputData = inputData.loadFromFile("data/MinMaxTypeMultiKnapsackInput.json");
		inputData = inputData.loadFromFile("data/MinMaxTypeMultiKnapsackInput-1000.json");
		promise = 690;
		inputData = inputData.loadFromFile("data/MinMaxTypeMultiKnapsackInput-3000.json");
		promise = 2700;
		
		//Search
		HillClimbing solver = new HillClimbing(inputData);
		System.out.println("Searching....");
		int current_solution = 0;
		int[] result = new int[solver.n_items]; 
		int[] temp = new int[solver.n_items];
//		solver.greedy_search();
//		solver.greedy_search();
		int time_not_change = 0;
		for(int i = 0; i < 4000; i++)
		{
			System.out.println("Step " + i + ": ");
			solver.distribute_item();
//			solver.greedy_search();
//			solver.add_item_to_bin();
			solver.resetAllBinViolation();
			temp = solver.neighborsSearch();
			System.out.println("Violation: " + solver.violation);
			solver.current_num_item = solver.getCurrentNumItem();
			if(current_solution < solver.current_num_item)
			{
				System.arraycopy(temp, 0, result, 0, temp.length);
				current_solution = solver.current_num_item;
				time_not_change = 0;
			}
			System.out.println("Max item so far: " + current_solution);
			time_not_change++;
			if(time_not_change == 200)
			{
				int bin_selected = solver.selectMostViolationBin(); 
				if(bin_selected != -1)
					solver.resetBin(bin_selected);
			}
			if(time_not_change == 400)
			{
				int bin_selected = solver.selectMostWBin(); 
				if(bin_selected != -1)
					solver.resetBin(bin_selected);
			}
//			if(time_not_change == 450)
//			{
//				System.arraycopy(result, 0, solver.X, 0, temp.length);
//			}
//				solver.resetAllBinViolation();
			if(current_solution >= promise 
					|| time_not_change == 800
					)
				break;
		}
		System.arraycopy(result, 0, solver.X, 0, temp.length);
		solver.resetAllBinViolation();
		for(int i = 0; i < 10; i++)
		{
			solver.add_item_to_bin();
			solver.resetAllBinViolation();
		}
//		solver.add_item_to_bin();
//		solver.resetAllBinViolation();
		
		System.out.println("Optimize: " + solver.getCurrentNumItem());
		
		int[] z = utils.getSolution(solver.X, inputData, solver.max_num_type, solver.max_num_class);
		int[] a = utils.getSolution(result, inputData, solver.max_num_type, solver.max_num_class);
		//Print
        utils.printSolution(a, current_solution);
        utils.printSolution(z, current_solution);
        System.out.println("Last " + solver.getCurrentNumItem());
        System.out.println("\nDone! \nOptimize: " + 
        		utils.checkSolution(z, inputData, solver.max_num_type, solver.max_num_class));
        System.out.println("\nDone! \nError code: " + 
        		utils.checkSolution(a, inputData, solver.max_num_type, solver.max_num_class));
	}
}
 */
