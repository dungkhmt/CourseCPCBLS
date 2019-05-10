package khmtk60.miniprojects.G7.multiknapsackminmaxtypeconstraints.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;



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
	int boiso = 100;
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
	int new_t_item[];
	int new_r_item[];
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
	
	
	
	/*ArrayList <Double> luu_gt = new A
	class bien_luu_gia_tri{
		double violations;
		
	}*/
	double wb[];
	double pb[];
	int dem_so_type;
	int dem_so_class;
	
	
	public double getAssignDelta(int[]X, int index, int bin_moi) {
		double vio_change = 0;
		double [] wb1 = new double[m];
		double [] pb1 = new double[m];
		int bin_cu = X[index];
		
		double tong_trong_so_bin_cu_truoc_khi_assign = Math.max(0,wb[bin_cu]-capacity[bin_cu])
				+ Math.max(0, minLoad[bin_cu]-wb[bin_cu])
				+Math.max(0, pb[bin_cu]-p_bin[bin_cu])
				+boiso*Math.max(0, dem(X,new_t_item,bin_cu)-t_bin[bin_cu]) 
				+boiso* Math.max(0, dem(X,new_r_item,bin_cu) - r_bin[bin_cu]);
		double tong_trong_so_bin_moi_truoc_khi_assign = Math.max(0,wb[bin_moi]-capacity[bin_moi])
				+ Math.max(0, minLoad[bin_moi]-wb[bin_moi])
				+Math.max(0, pb[bin_moi]-p_bin[bin_moi])
				+boiso * Math.max(0, dem(X,new_t_item,bin_moi)-t_bin[bin_moi]) 
				+ boiso* Math.max(0, dem(X,new_r_item,bin_moi) - r_bin[bin_moi]);
//		if(wb[bin_cu]>capacity[bin_cu]) vio_change--;
//		if(minLoad[bin_cu]>wb[bin_cu]) vio_change--;
//		if(pb[bin_cu]>p_bin[bin_cu]) vio_change--;
//		if(dem(X,new_t_item,bin_cu)>t_bin[bin_cu]) vio_change--;
//		if(dem(X,new_r_item,bin_cu)>r_bin[bin_cu]) vio_change--;
//		
//		if(wb[bin_moi]>capacity[bin_moi]) vio_change--;
//		if(minLoad[bin_moi]>wb[bin_moi]) vio_change--;
//		if(pb[bin_moi]>p_bin[bin_moi]) vio_change--;
//		if(dem(X,new_t_item,bin_moi)>t_bin[bin_moi]) vio_change--;
//		if(dem(X,new_r_item,bin_moi)>r_bin[bin_moi]) vio_change--;
		X[index] = bin_moi;
//		if(wb[bin_cu]>capacity[bin_cu]) vio_change++;
//		if(minLoad[bin_cu]>wb[bin_cu]) vio_change++;
//		if(pb[bin_cu]>p_bin[bin_cu]) vio_change++;
//		if(dem(X,new_t_item,bin_cu)>t_bin[bin_cu]) vio_change++;
//		if(dem(X,new_r_item,bin_cu)>r_bin[bin_cu]) vio_change++;
//		
//		if(wb[bin_moi]>capacity[bin_moi]) vio_change++;
//		if(minLoad[bin_moi]>wb[bin_moi]) vio_change++;
//		if(pb[bin_moi]>p_bin[bin_moi]) vio_change++;
//		if(dem(X,new_t_item,bin_moi)>t_bin[bin_moi]) vio_change++;
//		if(dem(X,new_r_item,bin_moi)>r_bin[bin_moi]) vio_change++;
		wb1 = tinh_wb_items(X);
		pb1 = tinh_pb_items(X);
		double tong_trong_so_bin_cu_sau_khi_assign = Math.max(0,wb1[bin_cu]-capacity[bin_cu])
				+ Math.max(0, minLoad[bin_cu]-wb1[bin_cu])
				+Math.max(0, pb1[bin_cu]-p_bin[bin_cu])
				+boiso*Math.max(0, dem(X,new_t_item,bin_cu)-t_bin[bin_cu]) 
				+ boiso*Math.max(0, dem(X,new_r_item,bin_cu) - r_bin[bin_cu]);
		double tong_trong_so_bin_moi_sau_khi_assign = Math.max(0,wb1[bin_moi]-capacity[bin_moi])
				+ Math.max(0, minLoad[bin_moi]-wb1[bin_moi])
				+Math.max(0, pb1[bin_moi]-p_bin[bin_moi])
				+boiso*Math.max(0, dem(X,new_t_item,bin_moi)-t_bin[bin_moi]) 
				+boiso* Math.max(0, dem(X,new_r_item,bin_moi) - r_bin[bin_moi]);
		vio_change = (tong_trong_so_bin_moi_sau_khi_assign + tong_trong_so_bin_cu_sau_khi_assign) 
				-(tong_trong_so_bin_cu_truoc_khi_assign+ tong_trong_so_bin_moi_truoc_khi_assign);
		
		
		
		
		X[index] = bin_cu;
		return vio_change;	
	}
	class violation{
		int W_violation;
		double LW_violation;
		int P_violation;
		int maxT_violation;
		int maxR_violation;
		public violation(int w_violation, double lW_violation, int p_violation, int maxT_violation, int maxR_violation) {
			
			this.W_violation = w_violation;
			this.LW_violation = lW_violation;
			this.P_violation = p_violation;
			this.maxT_violation = maxT_violation;
			this.maxR_violation = maxR_violation;
		}
		
	}
	class weight{
		double wb;
		double pb;
		int numberType;
		int numberClass;
		public weight(double wb, double pb, int numberType, int numberClass) {
			this.wb = wb;
			this.pb = pb;
			this.numberType = numberType;
			this.numberClass = numberClass;
		}
		
	}
	ArrayList<violation> abc= new ArrayList<Use_1d_arr.violation>();
	public double violation3() {
		double violation =0;
		
		for(int i=0;i<bins.length;i++) {
			
			violation += (abc.get(i).W_violation+ 3*abc.get(i).LW_violation+ abc.get(i).P_violation
					+ 2*abc.get(i).maxT_violation+ 3*abc.get(i).maxR_violation);
		}
		return violation;
	}
	
	public double getAssignDelta3(int index, int bin_moi) {
		double gia_tri=0;
		int bin_cu = X[index];
		setPropagate(index, bin_moi);		
		gia_tri = violation3();
		setPropagate(index, bin_cu);
		return gia_tri;
		
	}
	//ArrayList<Integer> a  = new ArrayList<Integer>();
	public void in_ket_qua(int []X) {
		double wb[] = new double[m];
		double pb[] = new double[m];
		wb = tinh_wb_items(X);
		pb = tinh_pb_items(X);

		int count = 0;
		for(int i=0;i<n;i++) {
			if( (wb[X[i]] >= minLoad[X[i]]) && (wb[X[i]]<=capacity[X[i]]) && pb[X[i]]<=p_bin[X[i]] && (dem(X,new_t_item,X[i]) <= t_bin[X[i]]) 
					&& (dem(X,new_r_item,X[i]) <= r_bin[X[i]]) ) {
				count++;
			}else {
				//X[i] = -1;
			}
		}
		System.out.println();
		System.out.println("gia tri sau khi check: ");
		for(int i=0;i<n;i++) {
			System.out.print(X[i] +" ");
		}
		System.out.println();
		System.out.println("Xep duoc tong so: " +count+ "item");
	}
	
	public ArrayList<Integer> index_train() {
		double wb[] = new double[m];
		double pb[] = new double[m];
		wb = tinh_wb_items(X);
		pb = tinh_pb_items(X);
		ArrayList< Integer> a = new ArrayList<Integer>();
		//int count = 0;
		for(int i=0;i<n;i++) {
			if( (wb[X[i]] >= minLoad[X[i]]) && (wb[X[i]]<=capacity[X[i]]) && pb[X[i]]<=p_bin[X[i]] && (dem(X,new_t_item,X[i]) <= t_bin[X[i]]) 
					&& (dem(X,new_r_item,X[i]) <= r_bin[X[i]]) ) {
				
			}else {
				a.add(i);
			}
		}
		return a;
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
		
		for(int i=0;i<bins.length;i++) {
			System.out.println(t_bin[i]);
		}
	}
	
	public int[] init(int []x) {
		double[] w_bin_current = new double[bins.length];
		double[] p_bin_current = new double[bins.length];
		int [] t_bin_current = new int [bins.length];
		int [] r_bin_current = new int [bins.length];
		int count =0;
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
					x[j] = -1;
					w_bin_current[i] -= w[j];
					p_bin_current[i] -= p_item[j];
				}
				
			}
		}
		ArrayList< Integer> a = new ArrayList<Integer>();
		for(int i=0;i<bins.length;i++) {
			if(w_bin_current[i] >= minLoad[i]) {
				a.add(i);
			}
		}
		for(int i=0;i<a.size();i++) {
			for(int j=0;j<items.length;j++) {
				if(x[j] == a.get(i)) {
					count++;
				}
			}
		}
		
		System.out.println("Khoi tao duoc " +count+ " item hop le");
		System.out.println("Mang X duoc khoi tao: ");
		for(int i=0;i<n;i++) {
			
			System.out.print(x[i] +" ");
		}
		return x;
	}
	
	ArrayList< weight> Weight = new ArrayList<Use_1d_arr.weight>();
	
	public void initPropagate(int []X) {
		double wb[] = new double[bins.length];
		double pb[] = new double[bins.length];
		wb = tinh_wb_items(X);
		pb = tinh_pb_items(X);
		for(int i = 0; i<m;i++) {
			Weight.add(new weight(wb[i], pb[i], dem(X,new_t_item,i), dem(X,new_r_item,i)));
		}
		int W_violation[] = new int[bins.length];
		double LW_violation[] = new double[bins.length];
		int P_violation[] = new int [bins.length];
		int maxR_violation[] = new int[bins.length];
		int maxT_violation[] = new int[bins.length];
		
		for(int i=0;i<bins.length;i++) {
			if(Weight.get(i).wb>capacity[i]) {
				W_violation[i] = 1;
			}else {
				W_violation[i]=0;
			}
			if(Weight.get(i).wb<minLoad[i]) {
				LW_violation[i] = 1;
			}else {
				LW_violation[i]=0;
			}
			if(Weight.get(i).pb>p_bin[i]) {
				P_violation[i] = 1;
			}else {
				P_violation[i]=0;
			}
			if(Weight.get(i).numberType>t_bin[i]) {
				maxT_violation[i] = 1;
			}else {
				maxT_violation[i]=0;
			}
			if(Weight.get(i).numberClass>r_bin[i]) {
				maxR_violation[i] = 1;
			}else {
				maxR_violation[i]=0;
			}
			//maxT_violation[i] = Math.max(0, Weight.get(i).numberType - t_bin[i]);
			//maxR_violation[i] = Math.max(0, Weight.get(i).numberClass - r_bin[i]);	
		}
		for(int i=0;i<bins.length;i++) {
			abc.add(new violation(W_violation[i], LW_violation[i],P_violation[i],maxT_violation[i],maxR_violation[i]));
		}
	}
	
	public void setPropagate(int index, int bin_moi) {
		int bin_cu = X[index];
		X[index] = bin_moi;
		Weight.get(bin_cu).wb -= w[index];
		Weight.get(bin_cu).pb -= p_bin[index];
		Weight.get(bin_cu).numberType = dem(X,new_t_item,index);
		Weight.get(bin_cu).numberClass = dem(X,new_r_item,index);
		
		Weight.get(bin_moi).wb += w[index];
		Weight.get(bin_moi).pb += p_bin[index];
		Weight.get(bin_moi).numberType = dem(X,new_t_item,index);
		Weight.get(bin_moi).numberClass = dem(X,new_r_item,index);
		
		if(Weight.get(bin_cu).wb>capacity[bin_cu]) {
			abc.get(bin_cu).W_violation = 1;
		}else {
			abc.get(bin_cu).W_violation=0;
		}
		if(Weight.get(bin_cu).wb<minLoad[bin_cu]) {
			abc.get(bin_cu).LW_violation = 1;
		}else {
			abc.get(bin_cu).LW_violation=0;
		}
		if(Weight.get(bin_cu).pb>p_bin[bin_cu]) {
			abc.get(bin_cu).P_violation = 1;
		}else {
			abc.get(bin_cu).P_violation=0;
		}
		if(Weight.get(bin_cu).numberType>t_bin[bin_cu]) {
			abc.get(bin_cu).maxT_violation = 1;
		}else {
			abc.get(bin_cu).maxT_violation=0;
		}
		if(Weight.get(bin_cu).numberClass>r_bin[bin_cu]) {
			abc.get(bin_cu).maxR_violation = 1;
		}else {
			abc.get(bin_cu).maxR_violation=0;
		}
		//abc.get(bin_cu).maxT_violation = Math.max(0, Weight.get(bin_cu).numberType - t_bin[bin_cu]);
		//abc.get(bin_cu).maxR_violation = Math.max(0, Weight.get(bin_cu).numberClass - r_bin[bin_cu]);
		
		
		if(Weight.get(bin_moi).wb>capacity[bin_moi]) {
			abc. get(bin_moi).W_violation = 1;
		}else {
			abc.get(bin_moi).W_violation=0;
		}
		if(Weight.get(bin_moi).wb<minLoad[bin_moi]) {
			abc.get(bin_moi).LW_violation = 1;
		}else {
			abc.get(bin_moi).LW_violation=0;
		}
		if(Weight.get(bin_moi).pb>p_bin[bin_moi]) {
			abc.get(bin_moi).P_violation = 1;
		}else {
			abc.get(bin_moi).P_violation=0;
		}
		if(Weight.get(bin_cu).numberType>t_bin[bin_cu]) {
			abc.get(bin_cu).maxT_violation = 1;
		}else {
			abc.get(bin_cu).maxT_violation=0;
		}
		if(Weight.get(bin_cu).numberClass>r_bin[bin_cu]) {
			abc.get(bin_cu).maxR_violation = 1;
		}else {
			abc.get(bin_cu).maxR_violation=0;
		}
		//abc.get(bin_moi).maxT_violation = Math.max(0, Weight.get(bin_moi).numberType - t_bin[bin_moi]);
		//abc.get(bin_moi).maxR_violation = Math.max(0, Weight.get(bin_moi).numberClass - r_bin[bin_moi]);
	}
	int X[];
	public void stateModel() {
		new_t_item = new int[t_item.length];
		new_t_item = type_or_class(t_item);
		new_r_item = new int[r_item.length];
		new_r_item = type_or_class(r_item);
		X = new int[n];
		Random rand = new Random();
		for(int i=0;i<n;i++) {
			int idx = rand.nextInt(binIndices[i].length);
			
			X[i] = binIndices[i][idx];
		}
		
		for(int i=0;i<n;i++) {
			X[i] = bins.length-1;
		}
		X = init(X);		
		//test();
		initPropagate(X);
		System.out.println();
		System.out.println(violation3());
//		for(int i=0;i<bins.length;i++) {
//			System.out.println(abc.get(i).LW_violation); 
//		}
		int it = 0;
		ArrayList<AssignMove> am = new ArrayList<AssignMove>();
		
		while(it < 100 && violation3() > 0) {
			//int vio = violation3(X);
			double min_delta = Double.MAX_VALUE;
			for(int i=0;i<items.length;i++) {
				for(int v: binIndices[i]) {
					double delta = getAssignDelta3(i, v);	
					//System.out.println(delta);
					if (delta < min_delta) {						
						min_delta = delta;
						am.clear();
						am.add(new AssignMove(i, v));
					} else if(delta == min_delta) {
						
						am.add(new AssignMove(i, v));
					} 
				}
			}
			int idx = rand.nextInt(am.size());
			AssignMove m = am.get(idx);
			setPropagate(m.i, m.v);
			System.out.println("Step " + it + ", violations  = " + violation3());
			it++;
		}
		
		/*int it = 0;
		int start_search = 0;
		ArrayList<AssignMove> am = new ArrayList<AssignMove>();
		while(it < 3 && violations(X) > 0) {
			
			double min_delta = Double.MAX_VALUE;
			for(int i = start_search; i < (start_search + items.length/4); i++) {
				//System.out.println(start_search+items.length/4);
				for(int v: binIndices[i]) {
					double delta = getAssignDelta2(X, i, v);					
					if (delta < min_delta) {						
						min_delta = delta;
						am.clear();
						am.add(new AssignMove(i, v));
					} else if(delta == min_delta) {
						
						am.add(new AssignMove(i, v));
					} 
				}
				
			}
			if((start_search + items.length/4) == items.length) {
				start_search = 0;
			}else {
				start_search += items.length/4;
			}
			
			int idx = rand.nextInt(am.size());
			AssignMove m = am.get(idx);
			X[m.i]=m.v;
			System.out.println("Step " + it + ", violations  = " + violations(X));
			
			it++;
		}*/
		
		
		in_ket_qua(X);
		
		
	}
}
