package khmtk60.miniprojects.G7.multiknapsackminmaxtypeconstraints.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import khmtk60.miniprojects.G7.multiknapsackminmaxtypeconstraints.model.Use_2d_arr.AssignMove;

import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;



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
		//I = mki.loadFromFile("/content/drive/My Drive/mini_project/MinMaxTypeMultiKnapsackInput-3000.json");

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
//		System.out.println();
//		int new_t_item[] = new int[t_item.length];
//		new_t_item = type_or_class(t_item);
		
	}

	public void test() {
		for(int i=0;i<items.length;i++) {
			System.out.println(" "+r_item[i]);
		}
		
	}
	public void use_2d_arr() {
		MinMaxTypeMultiKnapsackSolution mks = new MinMaxTypeMultiKnapsackSolution();
		mks.load_data();
		mks.stateModel();
		mks.search();
	}
	
	public void use_1d_arr() {
		Use_1d_arr us = new Use_1d_arr(bins,items,capacity,minLoad,p_bin,t_bin,r_bin,w,p_item,t_item,r_item,binIndices);
		us.gan_mien_gia_tri();
		us.stateModel();
		
	}
	public void use_GA() {
		population us = new population(bins,items,capacity,minLoad,p_bin,t_bin,r_bin,w,p_item,t_item,r_item,binIndices);
		//us.initPopulation();
		us.runGA();
	}
	public void use_1d_arr2() {
		Use_1d_arr2 us2 = new Use_1d_arr2(bins,items,capacity,minLoad,p_bin,t_bin,r_bin,w,p_item,t_item,r_item,binIndices);
		us2.gan_mien_gia_tri();
		us2.stateModel();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MinMaxTypeMultiKnapsackSolution mks = new MinMaxTypeMultiKnapsackSolution();
		long startTime = System.nanoTime();
		mks.load_data();
		//mks.test();
		//mks.use_2d_arr();
		//mks.use_1d_arr();
		//mks.use_1d_arr2();
		mks.use_GA();
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println((double) totalTime/1000000000 + "seconds");
		 
	}

}
