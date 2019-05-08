package khmtk60.miniprojects.G17;


import java.util.HashSet;
import java.util.Set;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Solution2 {
	private MinMaxTypeMultiKnapsackInput input;
	private MinMaxTypeMultiKnapsackSolution solution;
	private MinMaxTypeMultiKnapsackInputItem[] items;
	private MinMaxTypeMultiKnapsackInputBin[] bins;
	private int maxCategories;
	private int maxClasses;
	
	private int coef = 1;
	
	LocalSearchManager mgr;
	ConstraintSystem S;

    VarIntLS[] x;
    IFunction[] loadedW;
    IFunction[] loadedP;
    IFunction[] selectedT;
    IFunction[] selectedR;
    
    int[][] D;
    int[] W;
    int[] P;
    int[] T;
    int[] R;
    int[] capacityW;
    int[] minLoadW;
    int[] capacityP;
    
    public static VarIntLS[] getColumn(VarIntLS[][] array, int index){
    	VarIntLS[] column = new VarIntLS[array.length];
        for(int i=0; i < array.length; i++){
        	column[i] = array[i][index];
        }
        return column;
    }
    
    public static int[] getColumn(int[][] array, int index){
    	int[] column = new int[array.length];
        for(int i=0; i < array.length; i++){
        	column[i] = array[i][index];
        }
        return column;
    }
    
    public void stateModel() {
    	mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);

    	x = new VarIntLS[items.length];
    	loadedW = new IFunction[bins.length];
    	loadedP = new IFunction[bins.length];
    	selectedT = new IFunction[bins.length];
    	selectedR = new IFunction[bins.length];
    	
    	for(int i = 0; i < items.length; ++i) {
    		Set<Integer> set = new HashSet<Integer>();
    		for(int v: items[i].getBinIndices())
    			set.add(v);
    		set.add(bins.length);
    		x[i] = new VarIntLS(mgr, set);
    	}
    	
    	for(int i = 0; i < items.length; ++i) 
    		S.post(new LessOrEqual(x[i], bins.length-1));
    	
        for(int j = 0; j < bins.length; ++j)
        {
        	loadedW[j] = new ConditionalSum(x, W, j);
        	loadedP[j] = new ConditionalSum(x, P, j);
//        	selectedT[j] = new CountDistinct(x, T, j);
//        	selectedR[j] = new CountDistinct(x, R, j);
        	
        	S.post(new LessOrEqual(loadedW[j], capacityW[j]));
        	S.post(new LessOrEqual(minLoadW[j], loadedW[j]));
        	S.post(new LessOrEqual(loadedP[j], capacityP[j]));
//        	S.post(new LessOrEqual(selectedT[j], (int) bins[j].getT()));
//        	S.post(new LessOrEqual(selectedR[j], (int) bins[j].getR()));
        }
        
        mgr.close();
    }
    
    public void prepareData(String fn) {
		MinMaxTypeMultiKnapsackInput a = new MinMaxTypeMultiKnapsackInput();
		input = a.loadFromFile(fn);
		
		items = input.getItems();
		bins = input.getBins();
		maxClasses = 0;
		maxCategories = 0;
				
		D = new int[items.length][bins.length];
		W = new int[items.length];
		P = new int[items.length];
		T = new int[items.length];
		R = new int[items.length];
		minLoadW = new int[bins.length];
		capacityW = new int[bins.length];
		capacityP = new int[bins.length];

		for(int i = 0; i < items.length; ++i) {
			maxCategories = Math.max(maxCategories, items[i].getT()+1);
			maxClasses = Math.max(maxClasses, items[i].getR()+1);
			int[] d = items[i].getBinIndices();
			for(int j = 0; j < d.length; ++j)
				D[i][d[j]] = 1;
			W[i] = (int) (coef * items[i].getW());
			P[i] = (int) (coef * items[i].getP());
			T[i] = (int) (coef * items[i].getT());
			R[i] = (int) (coef * items[i].getR());
		}
		
		for(int j = 0; j < bins.length; ++j) {
			minLoadW[j] = (int) (coef * bins[j].getMinLoad());
			capacityW[j] = (int) (coef * bins[j].getCapacity());
			capacityP[j] = (int) (coef * bins[j].getP());
		}
    }
    
    public void search() {
//		TabuSearch ts = new TabuSearch();
//		ts.search(S, 30, 10, 10000, 100);
    	HillClimbing s = new HillClimbing();
        s.hillClimbing(S, 1000);
	}
    
    public void check() {
    	solution = new MinMaxTypeMultiKnapsackSolution();
        int[] binOfItems = new int[items.length];
        for(int i = 0; i<items.length; ++i) {
        	if(x[i].getValue() == bins.length)
        		binOfItems[i] = -1;
        	else
        		binOfItems[i] = x[i].getValue();
        }
        solution.setBinOfItem(binOfItems);
        SolutionChecker SC = new SolutionChecker();
        System.out.println(SC.check(input, solution));
    }
    
    public static void main(String[] args) {
		String fn = "src/khmtk60/miniprojects/multiknapsackminmaxtypeconstraints/MinMaxTypeMultiKnapsackInput-1000.json";

    	Solution2 s = new Solution2();
    	s.prepareData(fn);
    	s.stateModel();
    	System.out.println("state done");
		s.search();
//    	s.check();
    }
}
