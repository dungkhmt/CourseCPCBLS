package khmtk60.miniprojects.G4;

import java.util.HashSet;

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
		int numBinFail = 0;
		for (int b = 0; b < this.numBins; b++) {
			MinMaxTypeMultiKnapsackInputBin tempBin = this.input.getBins()[b];
			System.out.println("============================================================================");
			System.out.print("Bin " + b + ": ");
			System.out.println("min weight = " + tempBin.getMinLoad() + 
					", max weight = " + tempBin.getCapacity() + 
					", max P = " + tempBin.getP() + 
					", max types = " + tempBin.getT() + 
					", max layer = " + tempBin.getR());
			System.out.print("items: ");
			
			double totalWeight = 0;
			double totalP = 0;
			
			HashSet<Integer> tHashSet = new HashSet<Integer>();
			HashSet<Integer> rHashSet = new HashSet<Integer>();
			
			for (int i = 0; i < this.numItems; i++) {
				if (X[i].getValue() == b) {
					System.out.print(i + " ");
					totalWeight += input.getItems()[i].getW();
					totalP += input.getItems()[i].getP();
					tHashSet.add(input.getItems()[i].getT());
					rHashSet.add(input.getItems()[i].getR());
				}
			}
			
			System.out.println("\nTotal items: " + 
					"total weight = " + totalWeight +
					", total P = " + totalP +
					", total types = " + tHashSet.size() + 
					", total layers = " + rHashSet.size());
			if (rHashSet.size() > tempBin.getR() |
					tHashSet.size() > tempBin.getT() |
					totalP > tempBin.getP() |
					totalWeight > tempBin.getCapacity() |
					totalWeight < tempBin.getMinLoad()) {
				numBinFail ++;
			}
		}
		System.out.println("=========================================================================");
		System.out.println("Num bin fail : " + numBinFail);
		
	}

	public static void main(String[] args) {
//		String fn = "/home/thangnd/git/java/Optimization/data/test.json";
		String fn = "D:\\thangnd\\java\\Optimization\\data\\test.json";
		MK2 s = new MK2(fn,
						1000);
		System.out.println("Load data okay !");
		s.stateModel();
		System.out.println("Build state model okay !");
		System.out.println("Searching .......");
		s.search();
		s.showResult();
	}

}
