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
		return X;
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
//        int[] aaaa = new int[]
//        		{511,511,515,511,515,515,515,511,515,515,511,511,511,515,511,515,511,515,515,511,511,511,511,511,511,515,500,511,500,500,500,500,509,509,500,509,1225,1225,509,1225,51,51,1225,51,51,51,51,51,51,51,51,51,51,51,51,51,51,51,51,51,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,51,51,51,51,485,485,51,51,1234,1846,51,1846,51,51,51,51,42,1234,42,1234,42,1234,1234,1846,47,47,47,1234,42,1846,1234,1234,509,509,509,509,55,55,47,47,509,509,509,509,509,509,509,509,509,509,509,55,55,1225,6,1225,55,1225,42,55,1225,1225,1225,1234,1234,1234,1234,42,1846,1846,27,1234,1234,1234,42,1234,27,1234,42,51,51,51,51,51,51,51,51,51,51,42,51,51,51,51,51,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,51,51,51,51,1221,1221,1221,1221,1221,1221,1221,1221,1226,1226,1226,1226,1226,1226,1226,1226,42,42,42,1234,42,27,42,27,1234,1234,1234,1234,1234,1221,1221,1221,47,47,47,54,47,54,1226,54,1226,1226,42,1226,42,1234,1234,27,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,47,47,54,54,54,47,54,54,54,54,54,54,1226,1226,54,47,42,1234,1234,42,42,27,1234,1234,42,1234,1846,1234,42,1234,42,1846,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1234,1234,1234,1846,1234,42,42,42,27,42,42,1,53,0,1,1,1,1234,42,1,1,51,51,51,51,51,51,1234,1234,1234,1234,1,42,43,1,1226,1226,1226,1226,1226,1226,1226,1226,1234,1234,1234,1234,1226,1226,1226,1226,53,53,53,53,0,0,53,53,1225,55,55,1226,51,51,55,55,1754,1754,1754,1754,53,53,0,53,1754,1754,1754,1754,1754,1754,1754,1754,42,1846,1234,1234,1754,1754,1754,1754,1846,27,27,1234,1234,27,1234,1234,55,55,1226,1226,1226,1226,1226,1226,55,55,55,55,55,55,55,55,43,1,53,1,55,1225,1225,1225,1,53,0,1,53,1,53,53,509,509,509,509,509,509,509,509,509,509,509,509,509,509,509,509,51,51,51,51,51,51,51,51,51,51,51,51,51,51,1226,1226,1846,1234,42,42,42,27,1234,1846,1846,1234,1234,1234,51,1234,1234,1234,43,1,53,43,0,53,1,53,43,53,53,53,1244,509,53,1244,1,53,1225,1225,1225,1225,1225,1225,43,53,53,1,1,0,43,53,1,0,1,1,0,0,43,43,0,1,53,53,1,1,0,53,1455,1244,1455,1244,1234,1234,1234,1455,1244,1455,1244,1455,1244,1455,1244,1455,1244,1455,1244,1455,1244,1455,1244,1455,1455,1244,1225,15,1455,1244,1455,1244,1225,6,1225,1225,55,55,55,55,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1,0,53,53,53,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,55,1225,55,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1846,53,1,53,0,53,43,0,1,43,53,53,53,53,53,43,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1,43,53,53,1225,1225,1225,1,1225,1225,1225,1225,1225,1225,1225,1225,490,490,1234,1234,498,490,498,498,1234,1225,1225,1225,1234,1234,1234,1234,490,490,490,498,498,498,498,498,490,490,498,498,498,498,490,490,1225,1225,1225,1225,1225,1225,1225,1225,1846,1846,1226,1846,1225,1226,55,1225,1225,1225,1225,1225,1225,1225,1225,1225,55,1225,1225,1225,1226,1226,1226,1226,486,486,486,486,510,510,510,510,486,486,486,486,486,486,486,486,510,510,510,510,510,510,510,510,510,510,510,510,510,510,510,510,510,510,510,510,486,486,486,486,1846,1846,510,510,510,1846,510,1846,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,511,515,515,511,515,511,515,511,511,511,511,515,511,515,509,511,486,486,486,486,486,486,486,486,486,486,486,486,486,515,511,515,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,1234,1846,1234,42,1244,90,1244,1244,90,1244,1244,1244,90,90,90,90,1244,90,1244,90,1244,90,1244,90,1244,485,509,509,509,509,509,509,509,509,509,1,53,53,1,53,1,43,53,1,53,53,43,53,43,43,53,485,1846,485,485,485,485,485,485,1234,1234,1234,42,1221,42,1221,1221,1234,1234,1846,1234,1234,1846,1234,1234,1234,1846,1234,1234,1234,1234,42,1234,27,1234,1234,1234,1234,42,42,1234,1234,1234,1846,1846,1234,27,1234,1846,1662,1662,51,51,51,1662,51,1662,1662,1662,51,1662,51,51,1662,51,1244,15,15,15,1244,1244,1244,15,1662,1662,51,51,51,51,51,51,15,15,1244,1244,1244,15,15,15,1244,1244,1244,1244,15,53,53,1,0,1,53,0,53,53,0,1,53,53,43,53,1,1225,1225,1221,1221,485,485,485,485,1,53,0,1,1225,1,1225,1225,500,500,501,500,500,500,500,500,1244,1244,1846,1846,515,515,15,15,1662,1662,1662,1662,1662,51,51,51,500,500,500,500,1662,51,1662,1662,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1221,1221,1221,1221,1221,1221,1221,1226,1221,1221,1221,1221,1221,1221,1221,1221,509,514,514,514,1662,51,1221,1662,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,1662,514,514,1226,1226,1846,1226,15,15,1244,1244,53,1,43,53,53,1,53,53,53,1,43,43,53,53,53,43,53,43,1226,43,1226,1226,1226,1226,1226,1226,1226,1226,53,43,1754,1754,43,43,43,43,490,498,43,43,498,498,498,498,498,490,498,498,490,490,498,498,1244,27,1234,490,15,15,15,15,54,54,54,54,498,498,498,490,47,47,47,47,54,54,54,47,54,54,1221,54,54,54,54,54,514,514,514,514,486,486,514,486,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,498,1226,1226,1226,1226,1226,1226,1226,498,498,498,498,490,498,498,490,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,1226,1226,1226,1226,1226,1226,1221,1221,514,514,514,514,514,514,514,1226,498,490,498,498,498,498,498,490,490,486,486,55,490,490,490,490,490,490,498,498,514,514,498,514,498,498,498,490,498,498,498,498,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1754,1221,1221,1221,1221,1221,1221,1221,1221,1221,1221,1221,1221,1221,1221,1221,1221,1754,1754,1754,1754,1221,1221,1221,1221,1221,1221,1221,1221,1221,1221,1221,1221,514,514,514,514,514,500,514,514,514,514,514,514,514,514,514,514,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,1225,1225,1225,55,1225,55,1225,1225,6,55,55,55,55,55,1225,55,53,1,1846,1846,1846,1846,1846,1846,1225,1225,1225,1225,55,1225,1846,1846,1226,1226,1226,1226,1226,1226,490,490,1226,1226,1226,1226,1226,1226,1226,1226,1225,1225,1225,1225,1225,55,6,6,6,6,1225,1225,1225,1226,1226,1226,55,1234,1226,1226,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1244,1244,500,500,1244,1244,1244,15,1244,1244,15,1244,15,1244,1244,15,15,15,15,15,1244,15,1244,15,1225,1225,1244,1225,1225,1225,55,55,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,500,511,515,515,515,511,515,511,511,515,511,511,515,515,511,515,515,515,511,511,511,515,511,511,511,486,486,486,486,486,486,511,515,500,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1795,1795,1795,1795,1846,1795,1795,1782,515,511,511,515,1795,1782,1795,1795,1226,1226,1226,1226,1226,1226,1226,1226,1225,6,55,1225,1225,1225,1226,1226,1225,1225,1225,1225,1225,1225,1225,1225,1244,1244,1244,1244,1244,1225,1225,1225,15,15,1244,1244,1244,1244,15,1244,1846,1846,1846,1846,1244,498,498,1846,1846,1846,1225,1846,1846,1846,1846,1846,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,55,1225,55,1225,1225,1225,1225,1225,1225,55,55,1225,55,55,55,55,55,55,55,6,1225,1225,1225,1225,1225,55,55,6,1225,1225,1225,1225,1225,55,55,55,55,1225,55,55,55,55,55,55,1221,1221,1221,1221,1221,55,1221,55,55,55,55,55,6,6,6,6,55,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,55,55,1846,1846,15,15,15,15,15,1244,1244,1244,15,15,1244,15,1226,1226,1226,1226,1226,1226,42,1234,1226,1234,1234,1244,1846,42,1846,1846,1846,1846,1244,1244,15,1234,1226,1226,1244,1244,1226,1226,1226,1226,1226,1226,1226,1226,27,27,27,42,1234,42,1234,1234,42,1846,1234,27,42,1234,1846,1846,55,55,6,55,55,55,55,55,42,55,27,1234,55,55,55,55,1234,27,27,1234,1234,1234,1234,1234,15,55,15,1244,42,27,27,1846,1244,1244,1226,1226,1226,1226,1226,1226,42,1244,15,15,15,1244,1244,15,501,501,515,511,511,515,511,515,501,1846,1846,511,515,515,515,511,515,511,515,515,511,511,515,515,511,511,511,1782,1782,1782,1782,1782,1782,1782,1782,1244,15,15,1244,6,6,15,1244,15,1846,1846,1846,15,15,1244,1244,1846,1846,1846,1846,1846,1846,1846,1846,51,51,51,42,1226,1226,1226,1226,514,1226,1226,1226,514,514,514,514,1782,1782,514,514,1782,1782,1782,1782,1782,1782,1782,1782,1782,1782,1782,1782,1244,1244,1244,15,15,1244,15,15,15,15,1244,1244,1244,15,15,15,15,1244,1244,15,1244,1226,1226,1226,1226,1226,27,42,42,42,27,1846,54,47,54,47,6,6,1225,6,1226,1225,1226,1226,1226,1226,1226,1226,6,6,1225,1225,6,1225,6,1225,6,6,6,6,1225,6,47,54,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1846,1846,1846,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1226,1795,1795,1795,1795,1795,1795,1795,1795,54,1795,54,54,1795,1795,1795,1795,1795,1795,1795,1795,1846,1795,1795,1795,1795,1795,1795,1795,1795,1795,1795,1795,54,54,47,54,54,54,47,54,515,511,515,515,515,515,54,54,47,47,54,54,54,54,54,54,54,54,54,54,47,54,54,54,47,47,54,54,47,47,54,54,54,47,54,54,54,54,54,54,54,54,47,54,54,47,54,54,54,54,54,54,54,47,47,54,54,54,47,54,54,54,54,54,54,54,54,54,15,15,15,1244,1244,15,15,1244,15,15,1244,15,15,1244,15,1244,54,54,54,54,47,54,54,54,15,54,1244,1244,15,15,1244,15,47,54,54,54,54,54,54,47,54,54,54,54,54,54,54,54,1234,1846,42,42,54,54,47,54,54,47,47,54,47,47,54,47,1846,1846,1244,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,47,47,47,47,47,47,47,47,42,47,54,47,1234,42,1234,42,42,42,42,1846,1234,42,27,1846,42,1234,27,1234,1846,1846,27,1234,1234,1234,1234,42,47,1234,1234,1234,6,6,1225,1225,6,6,1225,6,54,47,54,47,47,47,47,47,47,54,47,6,1225,1225,6,1225,1225,1225,6,1225,1225,1225,1225,1225,1225,6,1225,6,42,1846,1234,1225,1846,1234,1234,42,27,1234,42,1234,1234,1846,42,1234,1234,1234,27,1234,15,15,1234,1244,1244,15,1244,1244,1234,1234,1234,1234,42,15,42,1234,6,6,6,6,1234,42,1234,1846,1244,1244,1244,1244,6,6,1244,15,15,15,15,1244,1244,15,15,15,1225,1225,1225,1244,1244,15,15,15,1225,1225,1225,1225,1225,1225,1225,1225,1225,1225,6,1225,1225,1225,1225,1225,1225,55,1225,1225,1225,1225,1225,1225,1221,15,1244,1244,15,15,1244,1244,1221,1221,1221,1221,1221,1221,1221,1221,15,1244,1244,1244,1244,15,1244,1244,1244,15,1244,15,1244,1244,1244,15,1221,1221,1221,1221,1221,1234,1221,1221,1244,1244,1244,1244,15,1244,15,15,1234,1234,1846,1234,1234,1234,27,1846,1234,1234,1846,1234,1846,1234,1234,27,42,42,1846,1234,1234,1846,1234,1234,1234,1234,1234,42,15,15,1244,27,1225,1225,1225,1225,1225,1225,1225,1225,1846,1846,55,1225,1846,1846,1846,1846,47,54,47,54,54,47,54,54,1225,47,1225,1225,1225,6,55,55,1221,1221,1221,1221,1221,1221,1221,1221,1221,1221,47,47,47,47,47,47,1244,1244,1244,1244,1244,1846,15,15,1846,47,1846,1846,47,1225,47,47,1846,1846,500,500,6,6,6,1846,6,6,6,6,1244,6,6,6,55,55,55,6,55,15,15,15,15,1244,1244,15,1244,15,1244,1244,1225,1225,1225,1225,1225,1225,6,55,55,55,55,6,55,55,55,55,500,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,47,47,47,47,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,55,55,55,55,1234,1846,1846,6,55,6,55,6,6,6,55,6,6,6,1846,1846,6,6,6,6,1846,1846,1846,1846,1846,1846,1846,1846,15,1244,1244,1244,1244,15,15,15,1244,1244,1244,1244,15,1244,1244,1244,6,55,6,6,6,6,6,6,1846,1846,1846,6,55,6,6,6,15,1244,1244,15,1244,15,1244,1244,15,1244,15,15,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,15,1244,15,15,1244,1244,1244,1244,15,1244,1244,15,15,1244,15,15,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1244,1244,1244,1846,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,514,1846,1846,1846,1846,515,515,515,515,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,514,514,514,1455,514,514,514,514,1221,1244,1244,1244,1455,90,90,1221,1226,1226,515,1226,1244};
//        		{486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,1846,486,1846,1846,1846,1846,483,483,1846,483,2,2,483,2,38,38,2,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,38,38,38,38,502,502,38,38,35,35,38,35,38,38,38,38,35,35,35,35,35,35,35,35,1631,1631,1631,35,35,35,35,35,483,483,483,483,2,2,1631,1631,483,483,483,483,483,483,483,483,483,483,483,2,2,2,2,2,2,2,35,2,2,2,2,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,38,38,38,38,38,38,38,38,38,38,35,38,38,38,38,38,34,34,34,34,34,34,34,34,34,34,34,34,38,38,38,38,1356,1356,1356,1356,1356,1356,1356,1356,34,34,34,34,34,34,34,34,35,35,35,35,35,35,35,35,35,35,35,35,35,1356,1356,1356,1631,1631,1631,1631,1631,1631,34,1631,34,34,35,34,35,35,35,35,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,1631,1631,1631,1631,1631,1631,1631,1631,1631,1631,1631,1631,34,34,1631,1631,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,34,34,34,34,34,34,34,34,34,34,34,34,34,35,35,35,35,35,35,35,35,35,35,35,4,4,3,3,4,3,35,35,4,4,38,38,38,38,38,38,35,35,35,35,3,35,4,4,34,34,34,34,34,34,34,34,50,50,50,50,34,34,34,34,4,4,3,4,3,4,3,3,2,2,2,34,38,38,2,2,755,755,755,755,4,4,3,4,755,755,755,755,755,755,755,755,50,50,50,50,755,755,755,755,50,50,50,50,50,50,50,50,2,2,34,34,34,34,34,34,2,2,2,2,2,2,2,2,3,3,3,4,2,2,2,2,3,4,4,4,4,4,4,3,483,483,483,483,483,483,483,483,483,483,483,483,483,483,483,483,38,38,38,38,38,38,38,37,37,37,37,37,37,37,34,34,50,50,50,50,50,50,50,50,50,50,50,50,37,50,50,50,4,4,3,3,4,4,3,4,4,4,3,4,653,483,3,653,4,4,1394,2,1394,1394,1394,1394,3,4,3,4,3,4,3,3,3,3,3,4,3,3,3,3,3,3,4,3,4,3,4,4,653,653,653,653,50,50,50,653,653,653,653,653,653,653,653,653,653,653,653,653,653,653,653,653,653,653,1394,653,653,653,653,653,1394,2,1394,2,1394,1394,2,2,1394,1394,1394,1394,1394,1394,2,1394,1394,1394,1394,1394,1394,1394,1394,1394,1394,2,1394,1394,1394,1394,1394,1394,4,4,4,3,4,1394,1394,1394,1394,1394,1394,2,1394,2,1394,1394,1394,2,1394,1394,1394,1394,1394,2,1394,1394,1394,1394,1394,1394,1394,1394,1846,4,3,4,3,4,3,4,4,3,4,4,4,4,4,3,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1394,1394,1394,1394,1394,1394,1394,1394,1394,1394,1394,1394,1394,1394,1394,1394,4,3,4,3,1394,1394,1394,4,1394,1394,1394,1394,1394,1394,2,1394,498,498,50,50,498,498,498,498,50,1692,1692,1692,50,50,50,50,498,498,498,498,498,498,498,498,498,498,498,498,498,498,498,498,1692,1692,1692,1692,1692,1692,1692,1692,1846,1846,34,1846,1692,34,1692,1692,1692,1692,1692,1692,1692,1692,1692,1692,1692,1692,1692,1692,343,343,343,343,495,495,495,495,504,504,504,504,495,495,495,495,495,495,495,495,504,504,504,504,504,504,504,504,504,504,504,504,504,504,504,504,504,504,504,504,495,495,495,495,510,510,510,510,510,510,510,510,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,486,486,486,486,486,486,486,486,486,486,486,486,486,486,483,486,495,495,495,495,495,495,495,495,495,495,495,495,495,486,486,486,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,50,50,50,50,922,922,922,922,922,922,922,922,922,922,922,922,922,922,922,922,922,922,922,922,922,502,483,483,483,483,483,483,483,483,483,3,4,4,252,252,252,252,252,252,252,252,252,252,252,252,252,502,510,502,502,502,502,502,502,50,50,50,50,1356,50,1356,1356,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,37,37,37,37,37,37,37,37,37,37,37,37,37,37};
        		//{486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,505,486,505,505,505,505,495,495,505,495,1234,1234,495,1234,36,36,1234,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,507,507,507,507,507,507,507,507,507,507,507,507,507,507,507,507,507,507,507,507,507,507,507,507,507,507,507,507,36,36,36,36,502,502,36,36,29,29,36,29,36,36,36,36,29,29,29,29,29,29,29,29,1678,1678,1678,29,29,16,16,29,495,495,495,495,1234,1234,1678,1678,495,495,495,495,495,495,495,495,495,495,495,1234,1234,1234,1234,1234,1234,1234,29,1234,1234,1234,1234,16,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,36,36,36,36,36,36,36,36,36,36,29,36,36,36,36,36,34,34,34,34,34,34,34,34,34,34,34,34,36,36,36,36,255,255,255,255,255,255,255,255,34,34,34,34,34,34,34,34,29,29,29,29,29,29,29,29,29,29,29,29,29,255,255,255,1678,1678,1678,1678,1678,1678,34,1678,34,34,29,34,29,29,29,29,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,1678,1678,1678,1678,1678,1678,1678,1678,1678,1678,1678,1678,34,34,1678,1678,29,16,29,29,29,29,29,29,29,29,29,29,29,29,29,29,34,34,34,34,34,34,34,34,34,34,34,34,34,29,29,29,29,29,29,29,29,29,16,16,3,4,4,4,3,4,29,29,4,3,36,36,36,36,36,36,29,29,16,16,3,16,3,4,34,34,34,34,34,34,34,34,29,16,16,16,34,34,34,34,4,4,4,3,4,4,3,4,1234,1234,1234,34,36,36,1234,1234,171,171,171,171,4,4,4,4,171,171,171,171,171,171,171,171,16,16,16,16,171,171,171,171,16,16,16,16,16,16,16,16,1234,1234,34,34,34,34,34,34,1234,1234,1234,1234,1234,1234,1234,1234,3,4,4,4,1234,1234,1234,1234,4,3,3,4,4,4,3,3,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,36,36,36,36,36,36,36,36,36,36,36,36,36,36,34,34,16,16,16,16,16,16,29,29,29,29,16,16,1650,16,16,16,4,4,3,4,3,3,3,3,3,4,3,3,1400,495,4,1400,4,4,1234,1234,1234,1234,1234,1234,4,4,3,4,3,4,4,3,3,3,3,4,3,3,3,3,3,3,4,4,4,4,4,4,1400,1400,1400,1400,16,16,16,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1234,1400,1400,1400,1400,1400,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,3,4,4,4,3,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,515,4,3,4,3,3,4,4,4,4,3,4,4,3,3,4,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,515,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,1234,4,3,3,4,1234,1234,1234,4,1234,1234,1234,1234,1234,1234,1234,1234,483,483,16,16,483,483,483,483,16,1234,1763,1763,16,16,16,16,483,483,483,483,483,483,483,483,483,483,483,483,483,483,483,483,1763,1763,1763,1763,1763,1763,1763,1763,515,515,34,515,1763,34,1763,1763,1763,1763,1763,1763,1763,1763,1763,1763,1763,1763,1763,1763,902,902,902,902,494,494,494,494,503,503,503,503,494,494,494,494,494,494,494,494,503,503,503,503,503,503,503,503,503,503,503,503,503,503,503,503,503,503,503,503,494,494,494,494,507,507,507,507,507,507,507,507,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,486,486,486,486,486,486,486,486,486,486,486,486,486,486,495,486,494,494,494,494,494,494,494,494,494,494,494,494,494,486,486,486,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,494,16,16,16,16,541,541,541,541,541,541,541,541,541,541,541,541,541,541,541,541,541,541,541,541,541,502,495,495,495,495,495,495,495,495,495,4,4,3,242,242,242,242,242,242,242,242,242,242,242,242,242,502,507,502,502,502,502,502,502,16,16,16,16,255,16,255,255,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,29,16,16,16,16,16,16,16,1148,1148,1148,1148,1650,1650,1650,1650,1650,1650,1650,1650,1650,1650,1650,1650,1650,1650};
//        		{1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1245,1245,1846,1245,37,37,1245,37,37,37,37,37,37,37,37,37,37,37,37,37,37,37,37,37,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,37,37,37,37,502,502,37,37,20,20,37,9,37,37,37,37,9,9,20,20,20,20,9,9,1846,1846,1846,9,20,20,9,9,1846,1846,1846,1846,1245,1245,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1245,1245,1245,1245,1245,1245,1245,20,1245,1245,1245,1245,20,9,20,9,20,9,20,20,20,20,9,9,20,20,20,20,37,37,37,37,37,37,37,37,37,37,9,37,37,37,37,37,34,34,34,34,34,34,34,34,34,34,34,34,37,37,37,37,1846,1846,1846,1846,1846,1846,1846,1846,34,34,34,34,34,34,34,34,20,9,9,20,20,20,9,20,9,9,20,20,20,1846,1846,1846,1846,1846,1846,1846,1846,1846,34,1846,34,34,9,34,20,20,9,9,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,34,34,1846,1846,9,20,9,9,9,9,9,9,9,20,20,9,20,20,9,9,34,34,34,34,34,34,34,34,34,34,34,34,34,9,9,9,9,9,20,20,9,9,20,20,4,3,4,3,4,4,20,9,4,4,37,37,37,37,37,37,9,9,9,20,4,9,4,4,34,34,34,34,34,34,34,34,9,9,9,9,34,34,34,34,4,4,4,3,4,4,3,3,1245,1245,1245,34,37,37,1245,1245,1846,1846,1846,1846,4,3,4,4,1846,1846,1846,1846,1846,1846,1846,1846,20,20,9,20,1846,1846,1846,1846,9,9,20,20,20,9,9,9,1245,1245,34,34,34,34,34,34,1245,1245,1245,1245,1245,1245,1245,1245,4,3,3,4,1245,1245,1245,1245,4,3,4,3,4,4,4,4,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,37,37,37,37,37,37,37,37,37,37,37,37,37,37,34,34,20,20,20,20,9,9,20,9,9,20,9,20,1846,20,20,9,3,3,4,3,4,3,3,4,3,4,4,4,1846,1846,4,1846,4,4,1245,1245,1245,1245,1245,1245,4,4,3,3,3,4,3,3,3,3,3,3,3,3,3,3,3,3,3,4,4,4,3,4,1846,1846,1846,1846,9,9,9,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1245,1846,1846,1846,1846,1846,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,4,4,3,4,4,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1846,4,3,4,3,4,4,3,4,4,3,4,4,4,3,3,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,1245,4,4,4,4,1245,1245,1245,4,1245,1245,1245,1245,1245,1245,1245,1245,1846,1846,9,9,1846,1846,1846,1846,9,1245,1762,1762,9,20,20,9,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1762,1762,1762,1762,1762,1762,1762,1762,1846,1846,34,1846,1762,34,1762,1762,1762,1762,1762,1762,1762,1762,1762,1762,1762,1762,1762,1762,1846,1846,1846,1846,495,495,495,495,509,509,509,509,495,495,495,495,495,495,495,495,509,509,509,509,509,509,509,509,509,509,509,509,509,509,509,509,509,509,509,509,495,495,495,495,1846,1846,1846,1846,1846,1846,1846,1846,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,495,495,495,495,495,495,495,495,495,495,495,495,495,1846,1846,1846,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,495,9,9,20,9,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,502,1846,1846,1846,1846,1846,1846,1846,1846,1846,4,4,3,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,502,1846,502,502,502,502,502,502,20,20,20,20,1846,20,1846,1846,9,20,9,20,20,9,20,9,20,20,20,9,20,9,9,9,20,20,9,20,20,9,20,20,20,20,9,20,20,20,9,20,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846,1846};
        System.out.println("Last " + solver.getCurrentNumItem());
        System.out.println("\nDone! \nOptimize: " + 
        		utils.checkSolution(z, inputData, solver.max_num_type, solver.max_num_class));
        System.out.println("\nDone! \nError code: " + 
        		utils.checkSolution(a, inputData, solver.max_num_type, solver.max_num_class));
	}
}