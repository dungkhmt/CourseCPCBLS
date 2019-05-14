package khmtk60.miniprojects.G7.multiknapsackminmaxtypeconstraints.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class GENERATE {
	int numberElement = 200;
	MinMaxTypeMultiKnapsackInputBin[] bins;
	MinMaxTypeMultiKnapsackInputItem[] items;
	double capacity[];
	double minLoad[];
	double p_bin[];
	int t_bin[];
	int r_bin[];
	double w[];
	double p_item[]; 
	int t_item[];
	int r_item[];
	int [][] binIndices;
	
	
	public GENERATE(MinMaxTypeMultiKnapsackInputBin[] bins, MinMaxTypeMultiKnapsackInputItem[] items, double[] capacity,
			double[] minLoad, double[] p_bin, int[] t_bin, int[] r_bin, double[] w, double[] p_item, int[] t_item,
			int[] r_item, int[][] binIndices) {
		super();
		this.bins = bins;
		this.items = items;
		this.capacity = capacity;
		this.minLoad = minLoad;
		this.p_bin = p_bin;
		this.t_bin = t_bin;
		this.r_bin = r_bin;
		this.w = w;
		this.p_item = p_item;
		this.t_item = t_item;
		this.r_item = r_item;
		this.binIndices = binIndices;
	}
	
	public int count (int []arr) {
		HashSet<Integer> hashSet = new HashSet<>();
		for(int i=0;i<arr.length;i++) {
			hashSet.add(arr[i]);
		}
		return hashSet.size();
	}
	public int sum(int []a) {
		int sum = 0;
		for(int i=0;i<a.length;i++) {
			sum = sum + a[i];
		}
		return sum;
	}
 
	public int dem(int[]X, int []a, int i) {
		int b[] = new int[count(a)];
		int count = 0;
		for(int j=0;j<w.length;j++) {
			if(X[j] == i) {
				b[a[j]] = 1;
			}
		}
		count = sum(b);
		return count;
	}
	public int[] type_or_class(int arr[]) {
		HashSet<Integer> hashSet = new HashSet<>();
		
		for(int i=0;i<arr.length;i++) {
			hashSet.add(arr[i]);
		}
		Integer list[] = new Integer[hashSet.size()];
		list = hashSet.toArray(list);
		int list_type_or_class[] = new int[list.length];
		for (int i=0;i<list.length;i++) {
			list_type_or_class [i]= list[i].intValue();
		}
		//list_type_or_class = sort(list_type_or_class);
		int new_arr[] = new int[arr.length];
		for(int i= 0;i<arr.length;i++) {
			for(int j=0;j<list_type_or_class.length;j++) {
				if(arr[i] == list_type_or_class[j]) {
					new_arr[i] = j;
				}
			}
		}
		return new_arr;
	}
	public int[] firstElement(int x[]) {
		int[] new_t_item = new int[t_item.length];
		new_t_item = type_or_class(t_item);
		int []new_r_item = new int[r_item.length];
		new_r_item = type_or_class(r_item);
		double[] w_bin_current = new double[bins.length];
		double[] p_bin_current = new double[bins.length];
		int [] t_bin_current = new int [bins.length];
		int [] r_bin_current = new int [bins.length];
		for(int j=0; j<items.length;j++) {
			for(int i: binIndices[j]) {
				x[j]=i;
				w_bin_current[i] += w[j];
				p_bin_current[i] += p_item[j];
				t_bin_current[i] = dem(x,new_t_item,i);
				r_bin_current[i] = dem(x,new_r_item,i);
				if(w_bin_current[i] <= capacity[i] && p_bin_current[i]<=p_bin[i] && t_bin_current[i] <= t_bin[i] && r_bin_current[i] <= r_bin[i]) {
					
					break;
				}else {
					w_bin_current[i] -= w[j];
					p_bin_current[i] -= p_item[j];
				}
				
			}
		}
		return x;
	}
	
	public int[] init(int []x) {
		int[] new_t_item = new int[t_item.length];
		new_t_item = type_or_class(t_item);
		int []new_r_item = new int[r_item.length];
		new_r_item = type_or_class(r_item);
		double[] w_bin_current = new double[bins.length];
		double[] p_bin_current = new double[bins.length];
		int [] t_bin_current = new int [bins.length];
		int [] r_bin_current = new int [bins.length];
		int count;
		for (int j = 0; j < items.length; j++) {
			for(int i: binIndices[j]) {
				x[j]=i;
				w_bin_current[i] += w[j];
				p_bin_current[i] += p_item[j];
				t_bin_current[i] = dem(x,new_t_item,i);
				r_bin_current[i] = dem(x,new_r_item,i);
				if(w_bin_current[i] <= capacity[i] && p_bin_current[i]<=p_bin[i] && t_bin_current[i] <= t_bin[i] && r_bin_current[i] <= r_bin[i]) {
					break;
				}else {
					x[j] = -1;
					w_bin_current[i] -= w[j];
					p_bin_current[i] -= p_item[j];
				}
			}
		}
		ArrayList< Integer> a = new ArrayList<Integer>();
		ArrayList< Integer> b = new ArrayList<Integer>();
		int iter = 0;
		do {
			double wb[];
			double pb[];
			Use_1d_arr us = new Use_1d_arr(bins, items, capacity, minLoad, p_bin, t_bin, r_bin, w, p_item, t_item, r_item, binIndices);
			wb = us.tinh_wb_items(x);
			pb = us.tinh_pb_items(x);
			count = 0;
			a.clear();
			b.clear();
			for(int i=0;i<bins.length;i++) {
				if(wb[i] >= minLoad[i]) {
					a.add(i);
				}
				else {
					b.add(i);
				}
			}
			for(int i=0;i<a.size();i++) {
				for(int j=0;j<items.length;j++) {
					if(x[j] == a.get(i)) {
						count++;
					}
				}
			}
			for (int i = 0; i < b.size(); i++) {
				for(int j=0;j<items.length;j++) {
					if(x[j] == b.get(i) || x[j] == -1) {
						int rnd = new Random().nextInt((binIndices[j].length - 1));
						x[j] = binIndices[j][rnd];
					}
				}
			}
			iter++;
		} while(iter < 10);
		
		System.out.println("Khoi tao duoc " +count+ " item hop le");
		System.out.println("Mang X duoc khoi tao: ");
//		oldValidCount = count;
//		newValidCount = count;
		for(int i=0;i<items.length;i++) {
			System.out.print(x[i] +" ");
		}
		return x;
	}
	int numberRandom = 100;
	public int[] generateAElement(int[] firstElement) {
		Random rand = new Random();
		int indexRandom[] = new int[numberRandom];
		for(int i=0; i<numberRandom;i++ ) {
			int index = rand.nextInt(items.length);
			indexRandom[i] = index;
		}
		for(int i=0;i<numberRandom;i++) {
			int index = rand.nextInt(binIndices[indexRandom[i]].length);
			firstElement[indexRandom[i]] = binIndices[indexRandom[i]][index];
		}
		return firstElement;
	}

	
	public ArrayList<Integer> convert_array_to_arraylist(int[] x) {
		ArrayList< Integer> a= new ArrayList<Integer>();
		for(int i=0;i<x.length;i++) {
			a.add(x[i]);
		}
		return a;
	}
	
	public int[] convert_arraylist_to_array(ArrayList< Integer> a) {
		int b[] = new int[a.size()];
		for(int i=0;i<a.size();i++) {
			b[i] = a.get(i);
		}
		return b;
	}
	public int check(int []X) {
		Use_1d_arr2 us = new Use_1d_arr2(bins, items, capacity, minLoad, p_bin, t_bin, r_bin, w, p_item, t_item, r_item, binIndices);
		us.gan_mien_gia_tri();
		us.convert_type_and_class();
		int count = us.in_ket_qua(X);
		return count;
	}
	public double caculateViolation(int []element) {
		Use_1d_arr2 us = new Use_1d_arr2(bins, items, capacity, minLoad, p_bin, t_bin, r_bin, w, p_item, t_item, r_item, binIndices);
		us.gan_mien_gia_tri();
		us.convert_type_and_class();
		us.set_X(element);
		us.initPropagate(element);
		return us.violation3();
	}
	
	ArrayList<Element> population;
	public ArrayList<Element> initPopulation() {
		//System.out.println("minload = " +minLoad[1]);
		int[] firstElement;
		firstElement = new int[items.length];
		for(int i=0;i<items.length;i++) {
			firstElement[i] = bins.length-1;
		}
		firstElement = firstElement(firstElement);
		System.out.println("Gia tri khoi tao: ");
		int count = check(firstElement);
		int element[] = new int[items.length];
		population =  new ArrayList<Element>();
		Element a = new Element(caculateViolation(element),convert_array_to_arraylist(firstElement));
		population.add(a);
		for(int i=0;i<numberElement-1;i++) {
			element = generateAElement(firstElement);
			a = new Element(caculateViolation(element),convert_array_to_arraylist(element));
			population.add(a);
		}	
		System.out.println("khoi tao violation = " +population.get(0).violation);
		/*for(int i=0;i<3;i++) {
			System.out.println();
			for(int j=0;j<items.length;j++) {
				System.out.print(population.get(i).element.get(j)+ " ");
			}
		}*/
		//System.out.println("size" + population.size());
		
		return population;
	}
	
}	
