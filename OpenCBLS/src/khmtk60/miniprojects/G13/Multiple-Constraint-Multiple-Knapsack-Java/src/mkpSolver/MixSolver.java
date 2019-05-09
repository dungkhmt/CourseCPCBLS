package mkpSolver;

import java.util.*;

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

class A
{
	private Integer[] items;
	public A(Integer[] items)
	{
		super();
		this.items = items;
//		this.bins = bins;
	}
	public Integer[] getItems() {
		return items;
	}
}

public class MixSolver {
	//Data
	MinMaxTypeMultiKnapsackInput inputData;
	int n_items;
	int n_bins;
	MinMaxTypeMultiKnapsackInputItem[] items;
	public MinMaxTypeMultiKnapsackInputBin[] bins;
	Map<Integer, Set<Integer>> map_item_type;
	Map<Integer, Set<Integer>> map_item_class;
	//Decision variable
	public int[] X;
	//Variable need for constraint
	double[] low_w_needed;
	double[] current_w;
	double[] current_p;
	int[] current_type;
	int[] current_class;
	int[][] current_bin_type;
	int[][] current_bin_class;
	//Violation
	List<Integer> violation_bin;
	List<Integer> violation_item;
	List<Integer> list_items_unpack;
	List<Integer> empty_bin;
	//
	int max_num_type = 0;
	int max_num_class = 0;
	
	public MixSolver(MinMaxTypeMultiKnapsackInput inputData)
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
		this.low_w_needed = new double[this.n_bins];
		this.current_w = new double[this.n_bins];
		this.current_p = new double[this.n_bins];
		this.current_type = new int[this.n_bins];
		this.current_class = new int[this.n_bins];
		this.current_bin_type = new int[this.n_bins][this.max_num_type+1];
		this.current_bin_class = new int[this.n_bins][this.max_num_class+1];
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
	
	public void greedy_search()
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
					PriorityQueue<binLowWeightNeeded> bins = new PriorityQueue<binLowWeightNeeded>(bin_candidate);
					while(bins.size() != 0)
					{
						binLowWeightNeeded bin = bins.remove();
						int bin_index = bin.index;
						if(checkValidBin(bin_index, item))
						{
							X[item] = bin_index;
							this.updateConstraint(bin_index, item);
							break;
						}
					}
					if(X[item] == (this.n_bins)) //Chua xep duoc
					{
						for(int bin_index: inputData.getItems()[item].getBinIndices())
						{
							if(checkValidBin(bin_index, item))
							{
								X[item] = bin_index;
								this.updateConstraint(bin_index, item);
								binLowWeightNeeded bin = new binLowWeightNeeded(bin_index, low_w_needed[bin_index]);
								bin_candidate.add(bin);
								break;
							}
						}
					}
				}
			}
		}
		this.updateViolation();
	}
	
	public void replace_one_one_increase_lw()
	{
		for(int i = 0; i < this.n_items; i++)
		{
			
		}
	}

	public void replace_one_one_all()
	{
		
	}
	
	public void add_item_to_bin()
	{//Item khac da xep duoc roi, bay gio chi xet cac item chua xep duoc
		List<Integer> unpack = new ArrayList<>(this.violation_item);
		for(int item : this.violation_item)
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
				X[item] = bin_selected;
				this.updateConstraint(bin_selected, item);
				unpack.remove(Integer.valueOf(item));
			}
		}
	}
	
	public void hillClimbing()
	{
		Comparator<binLowWeightNeeded> tr_comparator = new TRcomparator();
		PriorityQueue<binLowWeightNeeded> list_move_heuristic = new PriorityQueue<binLowWeightNeeded>(tr_comparator);
//		List<Move> list_move_heuristic = new ArrayList<>();
		List<Move> possible_move = new ArrayList<>();
		Random random_move = new Random();
		int iter = 0;
		int max_step = 1000;
		while(iter < max_step)
		{
			System.out.println("Step: " + iter);
			for(int i = 0; i < this.n_items; i++)
			{
				list_move_heuristic.clear();
				possible_move.clear();
				if(X[i] == this.n_bins)
				{// TH item chua xep duoc vao bin
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
						}
					}
					if(list_move_heuristic.size() > 0)
					{
						binLowWeightNeeded new_bin = list_move_heuristic.remove();
						int bin_index = new_bin.index;
						X[i] = bin_index;
						this.updateConstraint(bin_index, i);
					}
					else if(possible_move.size() > 0 && iter%100 == 0)
					{
						int index = random_move.nextInt(possible_move.size());
						Move m = possible_move.get(index);
						X[m.index] = m.bin;
						this.updateConstraint(m.bin, m.index);
					}
				}
				else
				{//TODO Thay doi cac item da duoc xep vao cac bin khac de tang kha nang chua them
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
						}
						if(list_move_heuristic.size() > 0)
						{
							binLowWeightNeeded new_bin = list_move_heuristic.remove();
							int old_bin_index = X[i];
							X[i] = new_bin.index;
							this.swapItem(X[i], old_bin_index, i);
//							this.updateConstraint(bin_index, i);
						}
					}
				}
			}
			iter++;
		}
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
	
	public void updateConstraint(int bin_index, int item_index)
	{
		MinMaxTypeMultiKnapsackInputItem item = this.items[item_index];
		MinMaxTypeMultiKnapsackInputBin bin = this.bins[bin_index];
		this.current_w[bin_index] += item.getW();
		this.current_p[bin_index] += item.getP();
		this.low_w_needed[bin_index] = bin.getMinLoad() - this.current_w[bin_index];
//		this.current_type[bin_index] += 1 - current_bin_type[bin_index][item.getT()];
		if(this.current_bin_type[bin_index][item.getT()] == 0)
			this.current_type[bin_index] += 1;
		this.current_bin_type[bin_index][item.getT()] += 1;
//		this.current_class[bin_index] += 1 - current_bin_class[bin_index][item.getR()];
		if(this.current_bin_class[bin_index][item.getR()] == 0)
			this.current_class[bin_index] += 1;
		this.current_bin_class[bin_index][item.getR()] += 1;
	}
	
	public void swapItem(int new_bin_index, int old_bin_index, int item_index)
	{
		MinMaxTypeMultiKnapsackInputItem item = this.items[item_index];
		MinMaxTypeMultiKnapsackInputBin old_bin = this.bins[old_bin_index];
		this.current_w[old_bin_index] -= item.getW();
		this.current_p[old_bin_index] -= item.getP();
		this.low_w_needed[old_bin_index] = old_bin.getMinLoad() + item.getW();
		this.current_bin_type[old_bin_index][item.getT()] -= 1;
		this.current_bin_class[old_bin_index][item.getR()] -= 1;
		if(this.current_bin_type[old_bin_index][item.getT()] == 0)
			this.current_type[old_bin_index] -= 1;
		if(this.current_bin_class[old_bin_index][item.getR()] == 0)
			this.current_class[old_bin_index] -= 1;
		this.updateConstraint(new_bin_index, item_index);
	}
	
	public void updateViolation()
	{
		this.violation_bin.clear();
		this.violation_item.clear();
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
	}
	
	public void printSolution(int[] a)
	{
		for(int bin: a)
			System.out.print(bin + ",");
		int result = this.n_items - this.violation_item.size();
		System.out.println("\nMax item: " + result);
	}
	
	public void printMap()
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
		return 0;
		
	}
	
	public void printInter()
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
	
	public static void main(String[] args) {
		//Load data
		MinMaxTypeMultiKnapsackInput inputData = new MinMaxTypeMultiKnapsackInput();
		inputData = inputData.loadFromFile("data/sample.json");
//		inputData = inputData.loadFromFile("data/MinMaxTypeMultiKnapsackInput.json");
//		inputData = inputData.loadFromFile("data/MinMaxTypeMultiKnapsackInput-1000.json");
		inputData = inputData.loadFromFile("data/MinMaxTypeMultiKnapsackInput-3000.json");
		
		//Search
		MixSolver solver = new MixSolver(inputData);
		System.out.println("Searching....");
    	solver.greedy_search();
    	solver.hillClimbing();
		solver.updateViolation();
       	for(int i = 0; i < 10; i++)
    	{
    		System.out.println("Step: " + i);
    		solver.add_item_to_bin();
			solver.updateViolation();
    	}
    	solver.updateViolation();
        //Print
        solver.printSolution(solver.X);
        System.out.println("\nDone! \nError code: " + 
        		solver.checkSolution(solver.X, inputData, solver.max_num_type, solver.max_num_class) + 
        		" Unpack: " + solver.violation_item.size() + " Empty bin: " + solver.empty_bin.size()
        		);     
	}
}
