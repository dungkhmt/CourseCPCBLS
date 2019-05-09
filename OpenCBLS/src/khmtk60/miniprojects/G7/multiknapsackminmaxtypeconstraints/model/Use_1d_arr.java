package khmtk60.miniprojects.G7.multiknapsackminmaxtypeconstraints.model;

import java.util.*;
public class Use_1d_arr {
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
	int int_w[];
	int int_capacity[];
	int int_p_item[];
	int int_p_bin[];
	int int_minload[];
	int boiso = 1000;
	int n;
	public Use_1d_arr(MinMaxTypeMultiKnapsackInputBin[] bins, MinMaxTypeMultiKnapsackInputItem[] items,
			double[] capacity, double[] minLoad, double[] p_bin, int[] t_bin, int[] r_bin, double[] w, double[] p_item,
			int[] t_item, int[] r_item, int[][] binIndices) {
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
	int m;
	int mt;
	int mr;
	
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
	
	

	public void convert_data_from_double_to_int() {
		int_w = new int[w.length];
		int_capacity = new int [capacity.length];
		int_minload = new int [minLoad.length];
		int_p_item = new int [p_item.length];
		int_p_bin = new int [p_bin.length];
        for(int i=0;i<w.length;i++) {
        	int_w[i] = (int) (w[i]*boiso);
        	int_p_item[i] = (int) (p_item[i]*boiso);
        }      
        
        for(int i=0;i<p_bin.length;i++) {
        	int_p_bin[i] = (int)(p_bin[i]*boiso);
        	int_capacity[i] = (int) (capacity[i]*boiso);
        	int_minload[i] = (int) (minLoad[i]*boiso);
        }
	}
	
	public int count (int []arr) {
		HashSet<Integer> hashSet = new HashSet<>();
		for(int i=0;i<arr.length;i++) {
			hashSet.add(arr[i]);
		}
		return hashSet.size();
	}
	public void gan_mien_gia_tri() {
		n = items.length;
		m = bins.length;
		mt = count(t_item);
		mr = count(r_item);
	}
	public double[] tinh_wb_items(int []X) {
		double[]wb = new double[m];
		for(int i=0;i<n;i++) {
			wb[X[i]] = wb[X[i]] + w[i];
		}
		
		return wb;
	}
	public double[] tinh_pb_items(int []X) {
		double []pb = new double[m];		
		for(int i=0;i<n;i++) {
			pb[X[i]] = pb[X[i]] + p_item[i];
		}
		return pb;
	}
	int new_t_item[];
	int new_r_item[];
	
	
	/*ArrayList <Double> luu_gt = new A
	class bien_luu_gia_tri{
		double violations;
		
	}*/
	double wb[];
	double pb[];
	int dem_so_type;
	int dem_so_class;
	public double violations(int[]X) {
//		double[] wb = new double[m];
//		wb = tinh_wb_items(X);
//		double[] pb = new double[m];
//		pb = tinh_pb_items(X);
//		double violation = 0;
//		for(int i=0;i<bins.length;i++) {
//			violation += Math.max(0,wb[i]-capacity[i])+ Math.max(0, minLoad[i]-wb[i])+Math.max(0, pb[i]-p_bin[i])
//			+Math.max(0, dem(X,new_t_item,i)-t_bin[i]) + Math.max(0, dem(X,new_r_item,i) - r_bin[i]);
//		}
//		return violation;
		
		wb = new double[m];
		wb = tinh_wb_items(X);
		pb = new double[m];
		pb = tinh_pb_items(X);
		double violation = 0;
		for(int i=0;i<bins.length;i++) {
			int tBinCount = dem(X,new_t_item,i);
			int rBinCount = dem(X,new_r_item,i);
			violation += Math.max(0,wb[i]-capacity[i])+ Math.max(0, minLoad[i]-wb[i])+Math.max(0, pb[i]-p_bin[i])
					+Math.max(0, tBinCount-t_bin[i]) + Math.max(0, rBinCount - r_bin[i]);

		}
		return violation;
		
	}
	public double getAssignDelta(int[]X, int index, int bin_moi) {
		double [] wb1;
		double [] pb1;
		int bin_cu = X[index];
		/*wb1 = tinh_wb_items(X);	
		pb1 = tinh_pb_items(X);
		double tong_trong_so_bin_cu_truoc_khi_assign = Math.max(0,wb1[bin_cu]-capacity[bin_cu])
				+ Math.max(0, minLoad[bin_cu]-wb1[bin_cu])
				+Math.max(0, pb1[bin_cu]-p_bin[bin_cu])
				+Math.max(0, dem(X,new_t_item,bin_cu)-t_bin[bin_cu]) 
				+ Math.max(0, dem(X,new_r_item,bin_cu) - r_bin[bin_cu]);
		double tong_trong_so_bin_moi_truoc_khi_assign = Math.max(0,wb1[bin_moi]-capacity[bin_moi])
				+ Math.max(0, minLoad[bin_moi]-wb1[bin_moi])
				+Math.max(0, pb1[bin_moi]-p_bin[bin_moi])
				+Math.max(0, dem(X,new_t_item,bin_moi)-t_bin[bin_moi]) 
				+ Math.max(0, dem(X,new_r_item,bin_moi) - r_bin[bin_moi]);*/
		double tong_trong_so_bin_cu_truoc_khi_assign = Math.max(0, minLoad[bin_cu]-wb[bin_cu]);
		double tong_trong_so_bin_moi_truoc_khi_assign = Math.max(0, minLoad[bin_moi]-wb[bin_moi]);


		X[index] = bin_moi;

		wb1 = tinh_wb_items(X);
		pb1 = tinh_pb_items(X);
		double tong_trong_so_bin_cu_sau_khi_assign = Math.max(0, minLoad[bin_cu]-wb1[bin_cu]);
		double tong_trong_so_bin_moi_sau_khi_assign = Math.max(0, minLoad[bin_moi]-wb1[bin_moi]);
		double vio_change = (tong_trong_so_bin_moi_sau_khi_assign + tong_trong_so_bin_cu_sau_khi_assign) - (tong_trong_so_bin_cu_truoc_khi_assign + tong_trong_so_bin_moi_truoc_khi_assign);
		//System.out.println("vio_change: " +vio_change);
		//System.out.println("tong_trong_so_bin_moi_truoc_khi_assign: " +tong_trong_so_bin_cu_truoc_khi_assign);
		//System.out.println("tong_trong_so_bin_moi_sau_khi_assign: " +tong_trong_so_bin_moi_sau_khi_assign);
		X[index] = bin_cu;
		return vio_change;	
		/*int bin_cu = X[index];
		X[index] = bin_moi;
		vio_change = violations(X);
		X[index] = bin_cu;
		return vio_change;*/
	}
	ArrayList<Integer> a  = new ArrayList<Integer>();
	public void check(int []X) {
		double wb[] = new double[m];
		double pb[] = new double[m];
		wb = tinh_wb_items(X);
		pb = tinh_pb_items(X);

		for(int i=0;i<m;i++) {
			System.out.println(" wb= "+wb[i]+ " pb= " +pb[i]+ " so type= "+dem(X,new_r_item,i) );
		}
		for(int i=0;i<n;i++) {
			System.out.print(X[i] +" ");
		}
		for(int i=0;i<n;i++) {
			if( (wb[X[i]] >= minLoad[X[i]]) && (wb[X[i]]<=capacity[X[i]]) && pb[X[i]]<=p_bin[X[i]] && (dem(X,new_t_item,X[i]) <= t_bin[X[i]]) 
					&& (dem(X,new_r_item,X[i]) <= r_bin[X[i]]) ) {
				
			}else {
				X[i] = -1;
			}
		}
		System.out.println();
		System.out.println("gia tri sau khi check: ");
		for(int i=0;i<n;i++) {
			System.out.print(X[i] +" ");
		}
	}
	
	class AssignMove{
	    int i;
	    int v;

	    public AssignMove(int i, int v) {
	        this.i = i;
	        this.v = v;
	    } 
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
public void test() {
	double pb[] = new double[m];
	pb = tinh_pb_items(X);
	for(int i=0;i<m;i++) {
		System.out.println(p_item[i]);
	}
}

	
	int X[];

	public void stateModel() {
		X = new int[n];
		Arrays.fill(X, -1);
		Random rand = new Random();
		greedyInitializeX();
		// randomInitializeX(rand);
		//convert_data_from_double_to_int();
		new_t_item = new int[t_item.length];
		new_t_item = type_or_class(t_item);
		new_r_item = new int[r_item.length];
		new_r_item = type_or_class(r_item);
		//test();
		System.out.println(violations(X));
		int it = 0;
		ArrayList<AssignMove> am = new ArrayList<AssignMove>();
		while(it < 10000 && violations(X) > 0) {
			am.clear();
			//double vio = violations(X);
			double min_delta = Integer.MAX_VALUE;
			for(int i = 0; i < items.length; i++) {
				for(int v: binIndices[i]) {
					double delta = getAssignDelta(X, i, v);
					//System.out.println(delta);
					if (delta < min_delta) {
						min_delta = delta;
						am.clear();
						am.add(new AssignMove(i, v));
					} else if(delta == min_delta) {
						if(!am.contains(new AssignMove(i, v))) {
							am.add(new AssignMove(i, v));
						}
					}
				}
			}
			//System.out.println(min_delta);
			int idx = rand.nextInt(am.size());
			//System.out.println(idx);
			AssignMove m = am.get(idx);
			//System.out.println("i= "+m.i + "v= "+m.v);
			X[m.i]=m.v;
			System.out.println("Step " + it + ", violations  = " + violations(X));
			it++;
		}
		
		
		check(X);
		
		
	}

	public void greedyInitializeX() {
		float[] current_w_bin = new float[m];
		float[] current_p_bin = new float[m];
		int[] current_t_bin = new int[m];
		int[] current_r_bin = new int[m];

		ArrayList<Set> tBin = new ArrayList<>();
		ArrayList<Set> rBin = new ArrayList<>();

		for (int i = 0; i < m; i++) {
			Set<Integer> tSet = new HashSet<>();
			Set<Integer> rSet = new HashSet<>();
			tBin.add(tSet);
			rBin.add(rSet);
		}

		for (int i = 0; i < n; i++) {
			int[] binDomain = binIndices[i];
			for (int bin : binDomain) {
				if (current_w_bin[bin] + w[i] <= capacity[bin] && current_p_bin[bin] + p_item[i] <= p_bin[bin] && current_w_bin[bin] < minLoad[bin]) {
					if ((current_t_bin[bin] > t_bin[bin]) || (current_r_bin[bin] > r_bin[bin])) {
						//Do nothing
					}
					else if ((!rBin.get(bin).contains(r_item[i])) && (current_r_bin[bin] == r_bin[bin])) {
						//Do nothing
					}
					else if ((!tBin.get(bin).contains(t_item[i])) && (current_t_bin[bin] == t_bin[bin])) {
						//Do nothing
					}
					else {
						if (!tBin.get(bin).contains(t_item[i])) {
							X[i] = bin;
							current_w_bin[bin] += w[i];
							current_p_bin[bin] += p_item[i];
							if (!rBin.get(bin).contains(r_item[i])) {
								current_r_bin[bin] += 1;
								rBin.get(bin).add(r_item[i]);
								break;
							}
							break;
						}
						else {
							X[i] = bin;
							current_w_bin[bin] += w[i];
							current_p_bin[bin] += p_item[i];
							if (!rBin.get(bin).contains(r_item[i])) {
								current_t_bin[bin] += 1;
								tBin.get(bin).add(t_item[i]);
								current_r_bin[bin] += 1;
								rBin.get(bin).add(r_item[i]);
								break;
							}
							break;

						}
					}
				}
			}
		}
		tBin.clear();
		rBin.clear();
	}

	private void randomInitializeX(Random rand) {
		for(int i=0;i<n;i++) {
			int idx = rand.nextInt(binIndices[i].length);

			X[i] = binIndices[i][idx];
		}
	}
}
