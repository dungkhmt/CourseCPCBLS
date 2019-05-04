package khmtk60.miniprojects.G3;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

public class MK2 {
	MinMaxTypeMultiKnapsackInput input;
	int numItems;
	int numBins;
	int alpha;
	
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[] X;
	
	/**
	 * Constructor
	 */
	public MK2(String fn, int alpha) {
		this.alpha = alpha; // use to cast double to integer
		this.input = MinMaxTypeMultiKnapsackInput.loadFromFile(fn);
		this.numItems = input.getItems().length;
		this.numBins = input.getBins().length;
		System.out.println("the number of items : " + this.numItems);
		System.out.println("the number of bins :  " + this.numBins);
//		this.numBins = 10;
//		this.numItems = 1000;
	}
	
	public void stateModel() {
		this.mgr = new LocalSearchManager();
		// Initial X
		this.X = new VarIntLS[this.numItems];
		for (int i = 0; i < this.numItems; i ++) {
			this.X[i] = new VarIntLS(mgr, 0, this.numBins - 1);
		}
		// Constraints
		S = new ConstraintSystem(mgr);
		
		// Weight
		int[] w = new int[this.numItems];
		for (int i = 0; i < this.numItems; i++) 
			w[i] = (int) input.getItems()[i].getW()*alpha;
		for (int b = 0; b < this.numBins; b++) {
			IFunction tempW = new ConditionalSum(X, w, b);
			S.post(new LessOrEqual(tempW, 
								  (int) input.getBins()[b].getCapacity()*alpha));
			S.post(new LessOrEqual((int) input.getBins()[b].getMinLoad()*alpha,
									tempW));	
		}
		
		// P
		int[] p = new int[this.numItems];
		for (int i = 0; i < this.numItems; i++)
			p[i] = (int) input.getItems()[i].getP()*alpha;
		for (int b = 0; b < this.numBins; b++) {
			IFunction tempP = new ConditionalSum(X, p, b);
			S.post(new LessOrEqual(tempP,
								   (int) input.getBins()[b].getP()*alpha));
		}
		
		// num types
		int[] t = new int[this.numItems];
		for (int i = 0; i < this.numItems; i ++)
			t[i] = input.getItems()[i].getT();
		for (int b = 0; b < this.numBins; b++) {
			IFunction tempT = new ConditionCount(X, t, b);
			S.post(new LessOrEqual(tempT,
								   input.getBins()[b].getT()));
		}
		
		// num layers
		int[] r = new int[this.numItems];
		for (int i = 0; i < this.numItems; i++)
			r[i] = input.getItems()[i].getR();
		for (int b = 0; b < this.numBins; b++) {
			IFunction tempR = new ConditionCount(X, r, b);
			S.post(new LessOrEqual(tempR,
								   input.getBins()[b].getR()));
		}
		// item i in the bins of D[i]
		for (int i = 0; i < this.numItems; i++) {
			S.post(new IsIn(X[i], input.getItems()[i].getBinIndices()));
		}
		
		this.mgr.close();
	}
	
	public void search() {
		TabuSearch tabu = new TabuSearch();
		int tabulen = 100;
		int maxIter = 500;
		int maxTime = 1000;
		int maxStable = 100;
		tabu.search(this.S, tabulen, maxTime, maxIter, maxStable);
	}
	
	public void showResult() {
		for (int b = 0; b < this.numBins; b++) {
			System.out.println("\n=============================");
			System.out.println("Bin " + b + ": ");
			for (int i = 0; i < this.numItems; i++) {
				if (X[i].getValue() == b) {
					System.out.print(i + " ");
				}
			}	
		}
	}

	public static void main(String[] args) {
		MK2 s = new MK2("/home/thangnd/git/java/Optimization/data/test.json",
						1000);
		System.out.println("Load data okay !");
		s.stateModel();
		System.out.println("Build state model okay !");
		System.out.println("Searching .......");
		s.search();
		s.showResult();
	}

}
