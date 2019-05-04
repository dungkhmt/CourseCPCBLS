package khmtk60.miniprojects.G4;

import java.util.HashSet;
import java.util.Set;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

public class MultiKnapsack {
	MinMaxTypeMultiKnapsackInput input;
	int numItems;
	int numBins;
	int numTypes;
	int numLayer;
	int alpha;
	
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[][] X;
	VarIntLS[][] Y;
	VarIntLS[][] Z;
	
	/**
	 * Constructor
	 * @param fn
	 */
	public MultiKnapsack(String fn, int alpha) {
		this.alpha = alpha; // use to cast double to integer
		this.input = MinMaxTypeMultiKnapsackInput.loadFromFile(fn);
		this.numItems = input.getItems().length;
		this.numBins = input.getBins().length;
		System.out.println("the number of items : " + this.numItems);
		System.out.println("the number of bins :  " + this.numBins);
//		this.numBins = 10;
//		this.numItems = 100;
	}

	public void stateModel() {
		this.mgr = new LocalSearchManager();
		
		// Initial X
		this.X = new VarIntLS[this.numBins][this.numItems];
		for (int i = 0; i < this.numBins; i ++) {
			for (int j = 0; j < this.numItems; j ++) {
				this.X[i][j] = new VarIntLS(mgr, 0, 1);
			}
		}
		
		// Initial Y
		this.numTypes = this.findNumTypeItem(this.input.getItems());
		this.Y = new VarIntLS[this.numBins][this.numTypes];
		for (int i = 0; i < this.numBins; i ++) {
			for (int j = 0; j < this.numTypes; j ++) {
				this.Y[i][j] = new VarIntLS(mgr, 0, 1);
			}
		}
		
		// Initial Z
		this.numLayer = this.findnumLayerItem(this.input.getItems());
		this.Z = new VarIntLS[this.numBins][this.numLayer];
		for (int i = 0; i < this.numBins; i ++) {
			for (int j = 0; j < this.numLayer; j++) {
				this.Z[i][j] = new VarIntLS(mgr, 0, 1);
			}
		}
		
		// Constraints
		S = new ConstraintSystem(mgr);
		
		// Weight
		int[] w = new int[this.numItems];
		for (int i = 0; i < this.numItems; i++)
			w[i] = (int) this.input.getItems()[i].getW()*this.alpha;
		
		for (int i = 0; i < this.numBins; i++) {
			IFunction temp = new ConditionalSum(this.X[i], w, 1);
			S.post(new LessOrEqual((int) (this.input.getBins()[i].getMinLoad()*this.alpha), temp));
			S.post(new LessOrEqual(temp, (int) (this.input.getBins()[i].getCapacity()*this.alpha)));
		}
		
		// P
		int[] p = new int[this.numItems];
		for (int i = 0; i < this.numItems; i++)
			p[i] = (int) (this.input.getItems()[i].getP()*this.alpha);
		
		for (int i = 0; i < this.numBins; i++) {
			IFunction temp = new ConditionalSum(this.X[i], p, 1);
			S.post(new LessOrEqual(temp, (int) (this.input.getBins()[i].getP()*this.alpha)));
		}
		
		// types and layer
		for (int i = 0; i < this.numBins; i ++)
			for (int j = 0; j < this.numItems; j++) {
				S.post(new LessOrEqual(this.X[i][j], 
										this.Y[i][this.input.getItems()[j].getT()]));
				S.post(new LessOrEqual(this.X[i][j],
										this.Z[i][this.input.getItems()[j].getR()]));
			}
		
		
		for (int i = 0; i < this.numBins; i++) {
			S.post(new LessOrEqual(new Sum(this.Y[i]), this.input.getBins()[i].getT()));
			S.post(new LessOrEqual(new Sum(this.Z[i]), this.input.getBins()[i].getR()));
		}
		
		// one item only in one bin
		for (int i = 0; i < this.numItems; i++) {
			VarIntLS[] temp = new VarIntLS[this.numBins];
			for (int j = 0; j < this.numBins; j ++) {
				temp[j] = this.X[j][i];
			}
			IFunction _sum = new Sum(temp);
			S.post(new LessOrEqual(_sum, 1));
			S.post(new LessOrEqual(1, _sum));
		}
		
		this.mgr.close();
	}
	
	public void search() {
		TabuSearch tabu = new TabuSearch();
		int tabulen = 100;
		int maxIter = 5000;
		int maxTime = 1000;
		int maxStable = 100;
		tabu.search(this.S, tabulen, maxTime, maxIter, maxStable);
		
//		HillClimbingSearch.hillClimbing(this.S, 1000);
		
	}
	
	private int findNumTypeItem(MinMaxTypeMultiKnapsackInputItem[] items) {
		numTypes = 0;
		for (MinMaxTypeMultiKnapsackInputItem item: items) {
			if (item.getT() > numTypes) {
				numTypes = item.getT(); 
			}
		}
		return numTypes + 1;
	}
	
	private int findnumLayerItem(MinMaxTypeMultiKnapsackInputItem[] items) {
		numLayer = 0;
		for (MinMaxTypeMultiKnapsackInputItem item : items) {
			if (item.getR() > numLayer) {
				numLayer = item.getR();
			}
		}
		return numLayer + 1;
	}
	
	public void showResult() {
		
		int[] itemLog = new int[this.numItems];
		for (int i = 0; i < this.numItems; i ++) itemLog[i] = 0;
		
		int numBinFail = 0;
		
		for (int i = 0; i < this.numBins; i ++) {
			MinMaxTypeMultiKnapsackInputBin tempBin = this.input.getBins()[i];
			System.out.println("============================================================================");
			System.out.print("Bin " + i + ": ");
			System.out.println("min weight = " + tempBin.getMinLoad() + 
					", max weight = " + tempBin.getCapacity() + 
					", max P = " + tempBin.getP() + 
					", max types = " + tempBin.getT() + 
					", max layer = " + tempBin.getR());
			System.out.print("items: ");
			
			double totalWeight = 0;
			double totalP = 0;
			int totalTypes = 0;
			int totalLayer = 0;
			
			for (int j = 0; j < this.numItems; j ++) {
				if (this.X[i][j].getValue() == 1) {
					itemLog[j] += 1;
					System.out.print(j + ", ");
					totalWeight += this.input.getItems()[j].getW();
					totalP += this.input.getItems()[j].getP();
				}
			}
			System.out.println();

			Set<Integer> tempTypes = new HashSet<Integer>();
			Set<Integer> tempLayers = new HashSet<Integer>();
			for (int j = 0; j < this.numItems; j ++) {
				if (this.X[i][j].getValue() == 1) {
					tempTypes.add(this.input.getItems()[j].getT());
					tempLayers.add(this.input.getItems()[j].getR());
				}
			}
			totalTypes = tempTypes.size();
			totalLayer = tempLayers.size();
			
			System.out.println("Total items: " + 
							"total weight = " + totalWeight +
							", total P = " + totalP +
							", total types = " + totalTypes + 
							", total layers = " + totalLayer);
			
			if (totalLayer > tempBin.getR() |
					totalTypes > tempBin.getT() |
					totalP > tempBin.getP() |
					totalWeight > tempBin.getCapacity() |
					totalWeight < tempBin.getMinLoad()) {
				numBinFail ++;
			}
		}
		
		System.out.println("\n\nNum bin fail = " + numBinFail);
		
		int numItemFail = 0;
		int numItemInBin = 0;
		for (int i = 0; i < this.numItems; i ++) {
			if (itemLog[i] > 1) {
				numItemFail ++;
			}
			if (itemLog[i] > 0) {
				numItemInBin ++;
			}
		}
		System.out.println("Num item is put in bin = " + numItemInBin);
		System.out.println("Num item is put in multi bin = " + numItemFail);
	}
	
	public static void main(String[] args) {
//		String fn = "/home/thangnd/git/java/Optimization/data/test.json";
		String fn = "D:\\thangnd\\java\\Optimization\\data\\test.json";
		MultiKnapsack s = new MultiKnapsack(fn,
											1000);
		System.out.println("Load data okay !");
		s.stateModel();
		System.out.println("Build state model okay !");
		System.out.println("Searching .......");
		s.search();
		s.showResult();
		
	}
}
