package khmtk60.miniprojects.G10.multiknapsackminmaxtypeconstraints;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class binLowWeightNeeded
{
	int index;
	double low_w_needed;
	public binLowWeightNeeded(int index, double low_w_needed)
	{
		this.index = index;
		this.low_w_needed = low_w_needed;
	}
}

class lowWComparator implements Comparator<binLowWeightNeeded> {

	public int compare(binLowWeightNeeded a, binLowWeightNeeded b) {
		if(b.low_w_needed < 0){
			return 1;
		}
		else{
			if(a.low_w_needed < b.low_w_needed)
				return 1;
			else return -1;
		}
//		if (a.low_w_needed > b.low_w_needed) return 1;
//		else return -1;
	}
}

public class GreedyInit {
	//Data
	int n_i, n_b, n_t, n_r;
	int[] mmr, mmt, r, t;
	double[] w, p;
	int[][] D;

	double[] W, LW, P;
	int[] T, R;

	Map<Integer, Set<Integer>> map_item_type = new HashMap<Integer, Set<Integer>>();
	Map<Integer, Set<Integer>> map_item_class = new HashMap<Integer, Set<Integer>>();
	//Decision variable
	public int[] X;
	//Variable need for greedy
//	binLowWeightNeeded[] bin_low_w_needed;
	double[] low_w_needed;
	double[] current_w;
	double[] current_p;
	double[] remain_w;
	int[] current_type;
	int[] current_class;
	int[][] current_bin_type;
	int[][] current_bin_class;
	//
	int max_num_type = 0;
	int max_num_class = 0;

	ArrayList<Integer> violation_bin;
	ArrayList<Integer> violation_item;

	public GreedyInit(double[] _w, double[] _p, int[] _r, int[] _t, int[][] _D, int[] _R, int[] _T, double[] _W, double[] _P, double[] _LW)
	{
		w = _w; p = _p; r = _r; t = _t; D = _D; R = _R; T = _T; W = _W; P = _P; LW = _LW;
		n_i = w.length;
		n_b = _W.length;
//		n_b = _W.length-1;
//		R = new int[n_b]; T = new int[n_b]; W = new double[n_b]; LW = new double[n_b]; P = new double[n_b];
//		for (int b=0; b<n_b; ++b){
//			R[b] = _R[b];
//			T[b] = _T[b];
//			W[b] = _W[b];
//			LW[b] = _LW[b];
//			P[b] = _P[b];
//
//		}


		D = new int[n_i][];
		for (int i=0; i<n_i; ++i) {
			D[i] = removeTheElement(_D[i], _D[i].length-1);
		}

		this.X = new int[n_i];
		for(int i = 0; i < n_i; i++)
			this.X[i] = n_b-1; //Mac dinh la cac item chua duoc xep vao bin nao.
		this.processData();
		violation_bin = new ArrayList<>();
		violation_item = new ArrayList<>();
	}
	
	public void processData()
	{

		for(int i = 0; i < n_i; i++)
		{
			if(t[i] > max_num_type)
				max_num_type = t[i];
			if(r[i] > max_num_class)
				max_num_class = r[i];

			if(!map_item_type.containsKey(t[i]))
			{
				Set<Integer> list_item = new HashSet<>();
				list_item.add(i);
				map_item_type.put(t[i], list_item);
			}
			else
			{
				Set<Integer> list_item = map_item_type.get(t[i]);
				list_item.add(i);
			}
			if(!map_item_class.containsKey(r[i]))
			{
				Set<Integer> list_item = new HashSet<>();
				list_item.add(i);
				map_item_class.put(r[i], list_item);
			}
			else
			{
				Set<Integer> list_item = map_item_class.get(r[i]);
				list_item.add(i);
			}
		}
		//Init variable for greedy
		this.low_w_needed = new double[n_b];
		this.current_w = new double[n_b];
		this.current_p = new double[n_b];
		this.remain_w = new double[n_b];
		this.current_type = new int[n_b];
		this.current_class = new int[n_b];
		this.current_bin_type = new int[n_b][max_num_type+1];
		this.current_bin_class = new int[n_b][max_num_class+1];
		}

	public int[] removeTheElement(int[] arr, int index)
	{
		if (arr == null
				|| index < 0
				|| index >= arr.length) {

			return arr;
		}

		// return the resultant array
		return IntStream.range(0, arr.length)
				.filter(i -> i != index)
				.map(i -> arr[i])
				.toArray();
	}

	public void step1()
	{
		Comparator<binLowWeightNeeded> comparator = new lowWComparator();
		for(Integer key_type: map_item_type.keySet())
		{
			Set<Integer> list_items_type = map_item_type.get(key_type);
			for(Integer key_class: map_item_class.keySet())
			{
				Set<Integer> list_items_class = map_item_class.get(key_class);
				Set<Integer> current_item = new HashSet<Integer>(list_items_type);
				current_item.retainAll(list_items_class);

				Pair[] sortedW = new Pair[current_item.size()];
				int count = 0;
				for (int i: current_item) {
					sortedW[count] = new Pair(i, w[i]);
					count++;
				}
				Arrays.sort(sortedW); //tang dan w

				PriorityQueue<binLowWeightNeeded> bin_candidate = new PriorityQueue<binLowWeightNeeded>(this.n_b, comparator);
				for(Pair pair: sortedW)
				{
					int item = pair.index;
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
					if(X[item] == (this.n_b-1)) //Chua xep duoc
					{
						for(int bin_index: D[item])
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
//		this.updateViolation();
	}


	public void step2(){
		// tim bin vi pham
		List<Integer> violation_bin = new ArrayList<Integer>();
		for(int i = 0; i < this.n_b; i++)
		{
			if(this.low_w_needed[i] > 0.0)
				violation_bin.add(i);
		}

		//tim nhung item thuoc bin cuoi hoac bin vi pham

		List<Integer> assign_item = new ArrayList<Integer>();
		for(int i = 0; i < this.n_i; i++)
		{
			if(this.X[i] == this.n_b-1)
			{
				assign_item.add(i);
				continue;
			}
			for (int bin: violation_bin)
			{
				if(this.X[i] == bin)
					assign_item.add(i);
			}
		}
		//tim nhung bin ko vi pham != bin cuoi
		List<Integer> assign_bin = new ArrayList<>();
		for (int b=0; b<n_b-1; ++b) {
			if(!violation_bin.contains(b)){
				assign_bin.add(b);
			}
		}
		//xep item vao cac bin do neu duoc
		for (int item: assign_item){
			for (int bin: D[item]){
				if (checkValidBin(bin, item)){
					this.X[item] = bin;
					this.updateConstraint(bin, item);
				}
			}
		}
		this.updateViolation();
	}

	public void updateViolation()
	{
		this.violation_bin.clear();
		this.violation_item.clear();
		for(int i = 0; i < this.n_b; i++)
		{
			if(this.low_w_needed[i] > 0.0)
			{//Reset bin
				this.violation_bin.add(i);
				this.current_w[i] = 0;
				this.current_p[i] = 0;
				this.low_w_needed[i] = 0;
				this.current_class[i] = 0;
				this.current_type[i] = 0;
				Arrays.fill(this.current_bin_class[i], 0);
				Arrays.fill(this.current_bin_type[i], 0);
			}
		}
		for(int i = 0; i < this.n_i; i++)
		{
			if(this.X[i] == this.n_b-1)
			{
				this.violation_item.add(i);
				continue;
			}
			for (int bin: violation_bin)
			{
				if(this.X[i] == bin)
				{
					violation_item.add(i);
					X[i] = this.n_b-1;
				}
			}
		}
	}

	public void assignRandom(){
		Random ran = new Random();
		for(int i = 0; i < this.n_b; i++)
		{
			if(this.low_w_needed[i] > 0.0 || this.low_w_needed[i]==LW[i])
				violation_bin.add(i);
		}
		for(int i = 0; i < this.n_i; i++)
		{
			if (this.X[i] == n_b-1 || violation_bin.contains((X[i]))){
				if(D[i].length > 0)
					System.out.printf("item id %d, len domain %d\n", i, D[i].length);
				Set<Integer> s1 = new HashSet<Integer>( Arrays.stream(D[i]).boxed().collect(Collectors.toList()));
				Set<Integer> s2 = new HashSet<Integer>(violation_bin);
				s1.retainAll(s2);
				if (s1.size() > 0){
					int idx = ran.nextInt(s1.size());
					int count=0;
					for (int possV: s1){
						if (count == idx) {
							this.X[i] = possV;
							System.out.printf("Assign item %d random value %d\n", i, possV);
						}

						count++;
					}
				} else {
					this.X[i] = n_b-1;
				}

			}
		}

	}

	public boolean checkValidBin(int bin_index, int item_index)
	{
		//Check constraint type, class
		if(current_bin_type[bin_index][t[item_index]] == 0)
		{
			if(current_type[bin_index] + 1 > T[bin_index])
				return false;
		}
		if(current_bin_class[bin_index][r[item_index]] == 0)
		{
			if(current_class[bin_index] + 1 > R[bin_index])
				return false;
		}
		if(current_w[bin_index] + w[item_index] > W[bin_index])
			return false;
		if(current_p[bin_index] + p[item_index] > P[bin_index])
			return false;
		return true;
	}
	
	public void updateConstraint(int bin_index, int item_index)
	{
		if(current_bin_type[bin_index][t[item_index]] == 0)
		{
			current_bin_type[bin_index][t[item_index]] = 1;
			current_type[bin_index] += 1;
		}
		if(current_bin_class[bin_index][r[item_index]] == 0)
		{
			current_bin_class[bin_index][r[item_index]] = 1;
			current_class[bin_index] += 1;
		}
		current_w[bin_index] += w[item_index];
		current_p[bin_index] += p[item_index];
		this.low_w_needed[bin_index] = LW[bin_index] - current_w[bin_index];
	}
	
	public void printSolution()
	{
		int scheduled = 0;
		for(int bin: X) {
			System.out.print(bin + " ");
			if (bin != n_b-1) scheduled++;
		}
		System.out.println();
		System.out.println("xep " + scheduled + " item");
		List<Integer> violation_bin = new ArrayList<Integer>();
        for(int i = 0; i < this.n_b; i++)
        {
        	if(this.low_w_needed[i] > 0.0)
        		violation_bin.add(i);
        }
        List<Integer> violation_item = new ArrayList<Integer>();
        for(int i = 0; i < this.n_i; i++)
        {
        	if(this.X[i] == this.n_b-1)
        	{
        		violation_item.add(i);
        		continue;
        	}
        	for (int bin: violation_bin)
        	{
        		if(this.X[i] == bin)
        			violation_item.add(i);
        	}
        }
        for (int i: violation_item) this.X[i] = n_b-1;
        int num_item = this.n_i - violation_item.size();
        System.out.println("\nNum item: " + num_item);		
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
					System.out.print(item + " ");
				}
				List<Integer> current_bin;
				System.out.print("\n");
			}
			System.out.print("\n");
		}
		System.out.println("NI: " + ni);
	}
	
	public static void main(String[] args) {

	}
}
