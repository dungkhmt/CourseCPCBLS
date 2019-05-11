package khmtk60.miniprojects.G7.multiknapsackminmaxtypeconstraints.model;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import choco.kernel.model.constraints.cnf.ALogicTree.Type;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;



public class MinMaxTypeMultiKnapsackSolution {
	MinMaxTypeMultiKnapsackInput mki;
	MinMaxTypeMultiKnapsackInput I;
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
	
	public void load_data() {
		mki = new MinMaxTypeMultiKnapsackInput();
		I = mki.loadFromFile("src/khmtk60/miniprojects/multiknapsackminmaxtypeconstraints/MinMaxTypeMultiKnapsackInput-1000.json");
		//I = mki.loadFromFile("/content/drive/My Drive/mini_project/MinMaxTypeMultiKnapsackInput.json");

		bins= I.getBins();
		capacity = new double[bins.length];
		minLoad = new double[bins.length];
		p_bin = new double[bins.length];
		t_bin = new int[bins.length];
		r_bin = new int[bins.length];
		for(int i=0;i<bins.length;i++) {
			capacity[i] = bins[i].getCapacity();
			minLoad[i]	= bins[i].getMinLoad();
			p_bin[i]= bins[i].getP();
			t_bin[i]=bins[i].getT();
			r_bin[i]=bins[i].getR();
		}
		items = I.getItems();
		w = new double[items.length];
		p_item = new double[items.length];
		t_item = new int[items.length];
		r_item = new int[items.length]; 
		binIndices = new int [items.length][bins.length];
		for(int i=0;i<items.length;i++) {
			w[i] = items[i].getW();
			p_item[i] = items[i].getP();
			t_item[i]= items[i].getT();
			r_item[i]= items[i].getR();
			binIndices[i] = items[i].getBinIndices();
		}
		/******************* convert data from double to int ******************/
        //System.out.println(w.length);
        //System.out.println(bins.length);
		// ham maain day ak
		
        
	}
	
	int int_w[];
	int int_capacity[];
	int int_p_item[];
	int int_p_bin[];
	int int_minload[];
	int boiso = 1000;
	
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
	public int[] sort(int[]arr) {
		int temp = arr[0];
        for (int i = 0 ; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    temp = arr[j];
                    arr[j] = arr[i];
                    arr[i] = temp;
                }
            }
        }
        return arr;
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
		list_type_or_class = sort(list_type_or_class);
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
	
	
	/****************************use 2D array *************************/
	LocalSearchManager mgr;
	ConstraintSystem S;
	private VarIntLS[][] x;
    private VarIntLS[][] Y;
    private VarIntLS[][] Z;
    int n;
    int m;
    int mt;
    int mr;
    class AssignMove{
	    int i;
	    int v;

	    public AssignMove(int i, int v) {
	        this.i = i;
	        this.v = v;
	    } 
	}
    public void HillClimbing(IConstraint c, int maxIter) {
        VarIntLS[] y = c.getVariables();
        ArrayList<AssignMove> cand = new ArrayList<AssignMove>();
        int it = 0;
        Random R = new Random();
        while(it < maxIter && c.violations() > 0) {
            cand.clear();
            float minDelta = Integer.MAX_VALUE;
            for(int i = 0 ; i < y.length ; i++) {
                for( int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) {
                    float d = c.getAssignDelta(y[i], v);
                    if(d < minDelta ) {
                        cand.clear();
                        cand.add(new AssignMove(i, v));
                        minDelta = d;
                    }
                    else if(d == minDelta) {
                        cand.add(new AssignMove(i, v));
					}
				}
            }
            int idx= R.nextInt(cand.size());
            AssignMove m  = cand.get(idx);
            y[m.i].setValuePropagate(m.v);;
            System.out.println("Step " +it + " violations = " + c.violations());
            it++;
        }
    }
	public void stateModel() {
		 mgr = new LocalSearchManager();
		 S = new ConstraintSystem(mgr);
		 int n = items.length;
		 int m = bins.length;
		 int mt = count(t_item);
		 int mr = count(r_item);
	     x = new VarIntLS[n][m];
	     Y = new VarIntLS[mt][m];
	     Z = new VarIntLS[mr][m];
	     Random rd = new Random();
	     for (int i = 0 ; i < n; i++){
	            for (int j = 0; j < m; j++) {
	                x[i][j] = new VarIntLS(mgr, 0, 1);
	                //x[i][j].setValue(rd.nextInt(2));

	            }
	        }
	        //init Y
	        for (int i = 0 ; i < mt; i++){
	            for (int j= 0; j < m; j++) {
	                Y[i][j] = new VarIntLS(mgr, 0 , 1);
	                //Y[i][j].setValue(rd.nextInt(2));
	            }
	        }
	        //init Z
	        for (int i = 0 ; i < mr; i++){
	            for (int j = 0; j < m; j++) {
	                Z[i][j] = new VarIntLS(mgr,0,1);
	                //Z[i][j].setValue(rd.nextInt(2));
	            }
	        }
	        
	        for (int i = 0; i < n; i++){
	            S.post(new IsEqual(new Sum(x[i]), 1));
	            IFunction[] f = new IFunction[binIndices[i].length];
	            for(int j=0 ;j<binIndices[i].length;j++) {
	            	f[j] = new FuncPlus( x[i][binIndices[i][j]],0);
	            	
	            }
	            S.post(new LessOrEqual(new Sum(f), 1));
	            //S.post(new IsEqual(new FuncPlus(x[i][binIndices[i][0]], x[i][binIndices[i][1]]), 1));
	            //S.post(new LessOrEqual(1, new FuncPlus(x[i][D[i][0]], x[i][D[i][1]])));
	        }
	        
	        convert_data_from_double_to_int();
	        
	        IFunction[] f1 = new IFunction[n];
	        for (int j =0; j < m; j++){
	            for(int i = 0; i < n; i++){
	                f1[i] =  new FuncMult(x[i][j], int_w[i]);
	            }
	            S.post(new LessOrEqual(new Sum(f1), int_capacity[j]));
	            S.post(new LessOrEqual((int) (minLoad[j]*boiso)	, new Sum(f1)));
	        }

	        IFunction[] f2 = new IFunction[n];
	        for (int j =0; j < m; j++){
	            for(int i = 0; i < n; i++){
	                f2[i] = new FuncMult(x[i][j], (int)(p_item[i]*boiso));
	            }
	            S.post(new LessOrEqual(new Sum(f2), (int) (p_bin[j]*boiso)));
	        }
	        
	        
	        for (int j= 0; j < m; j++){
	            VarIntLS[] t = new VarIntLS[mt];
	            for (int i =0; i < mt; i++){
	                t[i]= Y[i][j];
	            }
	            S.post(new LessOrEqual(new Sum(t), t_bin[j]));
	        }

	        for (int j= 0; j < m; j++){
	            VarIntLS[] r = new VarIntLS[mr];
	            for (int i =0; i < mr; i++){
	                r[i]= Z[i][j];
	            }
	            S.post(new LessOrEqual(new Sum(r), r_bin[j]));
	        }

	        
	        t_item = type_or_class(t_item);
	        r_item = type_or_class(r_item);

	        for(int i = 0; i < n; i++){
	            for (int j = 0; j < m; j++){
	                S.post(new LessOrEqual(x[i][j],Y[t_item[i]][j]));
	            }
	        }

	        for(int i = 0; i < n; i++){
	            for (int j = 0; j <m; j++){
	                S.post(new LessOrEqual(x[i][j],Z[r_item[i]][j]));
	            }
	        }
	        mgr.close();
	}
	/********************************use 1D array ********************************/
//	int n;
//	int m;
//	int mt;
//	int mr;
//	
//	public int sum(int []a) {
//		int sum = 0;
//		for(int i=0;i<a.length;i++) {
//			sum = sum + a[i];
//		}
//		return sum;
//	}
// 
//	public int dem(int[]X, int []a, int i) {
//		int b[] = new int[count(a)];
//		int count = 0;
//		for(int j=0;j<w.length;j++) {
//			if(X[j] == i) {
//				b[a[j]] = 1;
//			}
//		}
//		count = sum(b);
//		return count;
//	}
//	
//	public int[] tinh_wb_items(int []X) {
//		int[]wb = new int[m];
//		for(int i=0;i<n;i++) {
//			wb[X[i]] = wb[X[i]] + int_w[i];
//		}
//		
//		return wb;
//	}
//	
//	public void gan_mien_gia_tri() {
//		n = items.length;
//		m = bins.length;
//		mt = count(t_item);
//		mr = count(r_item);
//	}
//	public int[] tinh_pb_items(int []X) {
//		int[]pb = new int[m];		
//		for(int i=0;i<n;i++) {
//			pb[X[i]] = pb[X[i]] + int_p_item[i];
//		}
//		return pb;
//	}
//	int new_t_item[];
//	int new_r_item[];
//	int wb[];
//	int pb[];
//	public int violations(int[]X) {
//		int count = 0;
//		wb = new int[m];
//		wb = tinh_wb_items(X);
//		pb = new int[m];
//		pb = tinh_pb_items(X);			
//		for(int i=0;i<bins.length;i++) {
//			if(wb[i]>int_capacity[i]) {
//				count++;			
//			}
//			if(wb[i]<int_minload[i]) {
//				count++;
//			}
//			if(pb[i]>int_p_bin[i]) {
//				count++;		
//			}	
//			if(dem(X,new_t_item,i) >t_bin[i]) {
//				count++;				
//			}				
//			if(dem(X,new_r_item,i)> r_bin[i]) {
//				count++;				
//			}
//				
//		}
//		return count;
//	}
//	public int getAssignDelta(int[]X, int index, int gia_tri_moi) {
//		int count = 0;
//		int gia_tri_cu = X[index];
//		
//		/*int cac_gt_ban_dau[] = new int[8];
//		cac_gt_ban_dau[0]=pb[gia_tri_moi];
//		cac_gt_ban_dau[1]=wb[gia_tri_moi];
//		cac_gt_ban_dau[2]=dem(X,new_t_item,gia_tri_moi);
//		cac_gt_ban_dau[3]=dem(X,new_r_item,gia_tri_moi);
//		cac_gt_ban_dau[4]=pb[gia_tri_cu];
//		cac_gt_ban_dau[5]=wb[gia_tri_cu];
//		cac_gt_ban_dau[6]=dem(X,new_t_item,gia_tri_cu);
//		cac_gt_ban_dau[7]=dem(X,new_r_item,gia_tri_cu);
//		
//		
//		
//		
//		X[index] = gia_tri_moi;
//		pb[gia_tri_moi] = pb[gia_tri_moi] + int_p_item[index];
//		wb[gia_tri_moi] = wb[gia_tri_moi] + int_w[index];
//		int count_type_new = dem(X,new_t_item,gia_tri_moi);
//		int count_class_new = dem(X,new_r_item,gia_tri_moi);
//		if(pb[gia_tri_moi] > int_capacity[gia_tri_moi] && cac_gt_ban_dau[0]<int_capacity[gia_tri_moi]) count++;
//		if(wb[gia_tri_moi] > int_p_bin[gia_tri_moi] && cac_gt_ban_dau[1]<int_p_bin[gia_tri_moi]) count++;
//		if(count_type_new>t_bin[gia_tri_moi] && cac_gt_ban_dau[2]<t_bin[gia_tri_moi]) count++;
//		if(count_class_new>r_bin[gia_tri_moi] && cac_gt_ban_dau[3]<r_bin[gia_tri_moi]) count++;
//		
//		pb[gia_tri_cu] = pb[gia_tri_cu] - int_p_item[index];
//		wb[gia_tri_cu] = wb[gia_tri_cu] - int_w[index];
//		int count_type_old = dem(X,new_t_item,gia_tri_cu);
//		int count_class_old = dem(X,new_r_item,gia_tri_cu);
//		if(pb[gia_tri_cu]< int_capacity[gia_tri_cu] && cac_gt_ban_dau[4]>int_capacity[gia_tri_cu]) count--;
//		if(wb[gia_tri_cu]<int_p_bin[gia_tri_cu] && cac_gt_ban_dau[5]>int_p_bin[gia_tri_moi]) count--;
//		if(count_type_old<t_bin[gia_tri_cu] && cac_gt_ban_dau[6]>t_bin[gia_tri_cu]) count--;
//		if(count_class_old<r_bin[gia_tri_cu] && cac_gt_ban_dau[7]>r_bin[gia_tri_cu]) count--;
//		*/
//		X[index] = gia_tri_moi;
//		count = violations(X);
//		
//		X[index] = gia_tri_cu;
//		return count;
//	}
	
	
	
//	class AssignMove{
//	    int i;
//	    int v;
//
//	    public AssignMove(int i, int v) {
//	        this.i = i;
//	        this.v = v;
//	    } 
//	}
//	int X[];
//	public void stateModel2() {
//		X = new int[n];
//		Random rand = new Random();
//		for(int i=0;i<n;i++) {
//			int gt = rand.nextInt(1846);
//			X[i] = gt;
//		}
//		convert_data_from_double_to_int();
//		new_t_item = new int[t_item.length];
//		new_t_item = type_or_class(t_item);
//		new_r_item = new int[r_item.length];
//		new_r_item = type_or_class(r_item);
//		System.out.println(violations(X));
//		int it = 0;
//		ArrayList<AssignMove> am = new ArrayList<AssignMove>();
//		while(it < 100 && violations(X) > 0) {
//			am.clear();
//			int vio = violations(X);
//			int min_delta = Integer.MAX_VALUE;
//			for(int i = 0; i < items.length; i++) {
//				for(int v: binIndices[i]) {
//					int delta = getAssignDelta(X, i, v);
//					if (delta < min_delta) {
//						//System.out.println("vao delta<min_delta");
//						min_delta = delta;
//						am.clear();
//						am.add(new AssignMove(i, v));
//					} else if(delta == min_delta) {
//						//System.out.println("vao delta = min_delta");
//						am.add(new AssignMove(i, v));
//					} else {
//						//System.out.println("khong vao dau ca");
//					}
//				}
//			}
//			//System.out.println(min_delta);
//			//System.out.println(am.size());
//			int idx = rand.nextInt(am.size());
//			//System.out.println(idx);
//			AssignMove m = am.get(idx);
//			//System.out.println("i= "+m.i + "v= "+m.v);
//			X[m.i]=m.v;
//			System.out.println("Step " + it + ", violations  = " + violations(X));
//			it++;
//		}
//		
//		
//		for(int t: X) {
//			System.out.print(" "+ t);
//		}
//		
//		
//	}
	
	
	
 
//	public int dem(VarIntLS[]X, int []a, int i) {
//		int b[] = new int[count(a)];
//		int count = 0;
//		for(int j=0;j<w.length;j++) {
//			if(X[j].getValue() == i) {
//				b[a[j]] = 1;
//			}
//		}
//		count = sum(b);
//		//X[5].setValue(14);
//		return count;
//	}
//	 /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	LocalSearchManager mgr;
//	ConstraintSystem S;
//	VarIntLS []x;
//    public void stateModel_Use1D() {
//    	 mgr = new LocalSearchManager();
//		 S = new ConstraintSystem(mgr);
//		 int n = items.length;
//		 int m = bins.length;
//		 int mt = count(t_item);
//		 int mr = count(r_item);
//		 x = new VarIntLS[n]; 
//		 Random rand = new Random();
//		 for(int i=0;i<n;i++) {
//			 x[i] = new VarIntLS(mgr, 0,m-1);
//			 int idx = rand.nextInt(m);
//			 x[i].setValue(idx);
//		 }
//		 
//		 convert_data_from_double_to_int();
//		 //ConditionalSum wb1 = new ConditionalSum(x, int_w, 0);
//		 ConditionalSum wb[] = new ConditionalSum[m]; // tong trong so 1 cua cac item trong moi bin
//		 ConditionalSum pb[] = new ConditionalSum[m]; // tong trong so 2 cua cac item trong moi bin
//		 
//		 /*int[]wb = new int[m];
//		 int[]pb = new int[m];
//		 for(int i=0;i<m;i++) {
//			 wb[i]=0;
//			 pb[i]=0;
//		 }*/
////		 int zero[] = new int[m];
////		 for(int i=0;i<m;i++) {
////			 //wb[i] = new ConditionalSum(x, zero,i);
////			 
////		 }
//		 /*for(int i=0;i<m;i++) {	
//			 for(int j=0;j<n;j++) {
//				 if(x[j].getValue() == i) {
//					 //wb[i] = wb[i] +  int_w[j];
//					 //pb[i] = pb[i] +  int_p_item[j];
//					 wb[j] = new ConditionalSum(x, int_w,i);
//					 pb[j] = new ConditionalSum(x, int_p_item,i);
//				 }
//			 }		
//		 }*/
//		 for(int i=0;i<m;i++) {
//			 wb[i] = new ConditionalSum(x, int_w,i);
//			 pb[i] = new ConditionalSum(x, int_p_item,i);
//		 }
//		 
//		 int new_t_item[] = new int[t_item.length];
//		 new_t_item = type_or_class(t_item);
//		 int new_r_item[] = new int[r_item.length];
//		 new_r_item = type_or_class(r_item);
//		 
//		 VarIntLS a = new VarIntLS(mgr, 0,200);
//		 /*for(int i= 0;i<n;i++) {
//			 S.post(new LessOrEqual(wb[i], int_capacity[x[i].getValue()]));
//			 S.post(new LessOrEqual(pb[i], int_p_bin[x[i].getValue()]));
//			 S.post(new LessOrEqual(int_minload[x[i].getValue()], wb[i]));
//		 }*/
//		 
//		 for(int i=0;i<m;i++) {	
//			 	//a.setValue(wb[i]);
//				//S.post(new LessOrEqual(a, int_capacity[i]));
//				//S.post(new LessOrEqual(int_minload[i], a));
//				//a.setValue(pb[i]);
//				//S.post(new LessOrEqual(a, int_p_bin[i]));
//				
//			 	S.post(new LessOrEqual(wb[i], int_capacity[i]));
//			 	S.post(new LessOrEqual(pb[i], int_p_bin[i]));
//			 	S.post(new LessOrEqual(int_minload[i], pb[i]));
//				
//				a.setValue(t_bin[i]);
//				S.post(new LessOrEqual( dem(x,new_t_item,i),a));
//				a.setValue(r_bin[i]);
//				S.post(new LessOrEqual( dem(x,new_r_item,i),a)); 
//		}
//		mgr.close();
//    }
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//   
//	
//	
	
	
//	public void HillClimbingSearch() {
//		int it = 0;
//		ArrayList<AssignMove> am = new ArrayList<AssignMove>();
//		while(it < 1000 && violations(X) > 0) {
//			am.clear();
//			int min_delta = Integer.MAX_VALUE;
//			for(int i = 0; i < items.length; i++) {
//				for(int v:binIndices[i]) {
//					int delta = getAssignDelta(X, v,i);
//					if (delta < min_delta) {
//						min_delta = delta;
//						am.clear();
//						am.add(new AssignMove(i, v));
//					} else if(delta == min_delta) {
//						am.add(new AssignMove(i, v));
//					}
//				}
//			}
//			Random rand = new Random();
//			int idx = rand.nextInt(am.size());
//			AssignMove m = am.get(idx);
//			X[m.i]= m.v ;
//			System.out.println("Step " + it + ", violations  = " + violations(X));
//			it++;
//		}
//	}
	public void inKQ(VarIntLS X[][]) {
		int[] KQ = new int[bins.length];
		for(int i=0;i<items.length;i++) {
			for(int j=0;j<bins.length;j++) {
				if(X[i][j].getValue() == 1) {
					KQ[i] = j;
				}
			}
		}
		System.out.println("Danh sach item thu duoc la: ");
		for(int i=0;i<items.length;i++) {
			System.out.print(KQ[i] + " ");
		}
	}
	public void search() {
		//TabuSearch ts = new TabuSearch();
        //ts.search(S, 50, 50, 100000, 100);
		//System.out.println("ket thuc");
		System.out.println(S.violations());
		HillClimbing(S, 20000);
		inKQ(x);
		//HillClimbingSearch();
//		for(int i=0;i<items.length;i++) {
//			System.out.print(" "+ x[i].getValue());
//		}
		/*System.out.println();
		int new_t_item[] = new int[t_item.length];
		new_t_item = type_or_class(t_item);
		for(int i=0; i<bins.length;i++) {
			System.out.print(" "+ dem(x,new_t_item,i));
		}*/
	}
	
	public void test() {
		//VarIntLS []x = S.getVariables();
		//System.out.println(x.length);
		for(int i=0;i<items.length;i++) {
			System.out.println(t_item[i]);
		}
		
	}
	public void use_2d_arr() {
		Use_2d_arr us = new Use_2d_arr(bins,items,capacity,minLoad,p_bin,t_bin,r_bin,w,p_item,t_item,r_item,binIndices);
		us.stateModel();
		us.search();
		
	}
	
	public void use_1d_arr() {
		Use_1d_arr us = new Use_1d_arr(bins,items,capacity,minLoad,p_bin,t_bin,r_bin,w,p_item,t_item,r_item,binIndices);
		us.gan_mien_gia_tri();
		us.stateModel();
		//us.search();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MinMaxTypeMultiKnapsackSolution mks = new MinMaxTypeMultiKnapsackSolution();
		long startTime = System.nanoTime();
		mks.load_data();
		//mks.use_2d_arr();
		mks.use_1d_arr();
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println((double) totalTime/1000000000 + "seconds");
		//mks.gan_mien_gia_tri();
		//mks.stateModel();
		//mks.stateModel_Use1D();
		//mks.test();
		//mks.stateModel2();
		//mks.search();
		
		 
	}

}
