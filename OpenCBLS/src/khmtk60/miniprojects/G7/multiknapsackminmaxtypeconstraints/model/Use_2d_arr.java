package khmtk60.miniprojects.G7.multiknapsackminmaxtypeconstraints.model;

import java.util.HashSet;
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
public class Use_2d_arr {
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
	int boiso = 10000;
	public Use_2d_arr() {
		super();
	}
	
	public Use_2d_arr(
			MinMaxTypeMultiKnapsackInputBin[] bins, MinMaxTypeMultiKnapsackInputItem[] items, double[] capacity,
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
    public void search() {
		//TabuSearch ts = new TabuSearch();
        //ts.search(S, 50, 50, 100000, 100);
		//System.out.println("ket thuc");
    	
		System.out.println(S.violations());
		HillClimbing(S, 10000);
		inKQ(x);
		//HillClimbingSearch();

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
	
}
