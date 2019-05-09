package khmtk60.miniprojects.G4;

import java.util.HashSet;
import java.util.Set;

import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.SolutionChecker;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputBin;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputItem;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackSolution;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
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
     * 
     * @param fn
     */
    public MultiKnapsack(String fn, int alpha) {
        this.alpha = alpha; // use to cast double to integer
        this.input = (new MinMaxTypeMultiKnapsackInput()).loadFromFile(fn);
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
        for (int i = 0; i < this.numBins; i++) {
            for (int j = 0; j < this.numItems; j++) {
                this.X[i][j] = new VarIntLS(mgr, 0, 1);
            }
        }

        // Initial Y
        this.numTypes = this.findNumTypeItem(this.input.getItems());
        this.Y = new VarIntLS[this.numBins][this.numTypes];
        for (int i = 0; i < this.numBins; i++) {
            for (int j = 0; j < this.numTypes; j++) {
                this.Y[i][j] = new VarIntLS(mgr, 0, 1);
            }
        }

        // Initial Z
        this.numLayer = this.findnumLayerItem(this.input.getItems());
        this.Z = new VarIntLS[this.numBins][this.numLayer];
        for (int i = 0; i < this.numBins; i++) {
            for (int j = 0; j < this.numLayer; j++) {
                this.Z[i][j] = new VarIntLS(mgr, 0, 1);
            }
        }

        // Constraints
        S = new ConstraintSystem(mgr);

        // Weight
        int[] w = new int[this.numItems];
        for (int i = 0; i < this.numItems; i++)
            w[i] = (int) this.input.getItems()[i].getW() * this.alpha;

        for (int i = 0; i < this.numBins; i++) {
            IFunction temp = new ConditionalSum(this.X[i], w, 1);
            IConstraint a1 = new LessOrEqual((int) (this.input.getBins()[i].getMinLoad() * this.alpha), temp);
            IConstraint a2 = new IsEqual(temp, 0);
            S.post(new OR(a1, a2));
            S.post(new LessOrEqual(temp, (int) (this.input.getBins()[i].getCapacity() * this.alpha)));
        }

        // P
        int[] p = new int[this.numItems];
        for (int i = 0; i < this.numItems; i++)
            p[i] = (int) (this.input.getItems()[i].getP() * this.alpha);

        for (int i = 0; i < this.numBins; i++) {
            IFunction temp = new ConditionalSum(this.X[i], p, 1);
            S.post(new LessOrEqual(temp, (int) (this.input.getBins()[i].getP() * this.alpha)));
        }

        // types and layer
        for (int i = 0; i < this.numBins; i++)
            for (int j = 0; j < this.numItems; j++) {
                S.post(new LessOrEqual(this.X[i][j], this.Y[i][this.input.getItems()[j].getT()]));
                S.post(new LessOrEqual(this.X[i][j], this.Z[i][this.input.getItems()[j].getR()]));
            }

        for (int i = 0; i < this.numBins; i++) {
            S.post(new LessOrEqual(new Sum(this.Y[i]), this.input.getBins()[i].getT()));
            S.post(new LessOrEqual(new Sum(this.Z[i]), this.input.getBins()[i].getR()));
        }

        // one item only in one bin
        for (int i = 0; i < this.numItems; i++) {
            VarIntLS[] temp = new VarIntLS[this.numBins];
            for (int j = 0; j < this.numBins; j++) {
                temp[j] = this.X[j][i];
            }
            IFunction _sum = new Sum(temp);
            S.post(new LessOrEqual(_sum, 1));
            S.post(new LessOrEqual(1, _sum));
        }

        // each item is only in a set of bins
        for (int i = 0; i < this.numItems; i++) {
            VarIntLS[] d = new VarIntLS[input.getItems()[i].getBinIndices().length];
            for (int j = 0; j < d.length; j++) {
                d[j] = this.X[input.getItems()[i].getBinIndices()[j]][i];
            }
            S.post(new LessOrEqual(1, new Sum(d)));
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
        for (MinMaxTypeMultiKnapsackInputItem item : items) {
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
        for (int i = 0; i < this.numItems; i++)
            itemLog[i] = 0;

        for (int i = 0; i < this.numBins; i++) {
            MinMaxTypeMultiKnapsackInputBin tempBin = this.input.getBins()[i];
            System.out.println("============================================================================");
            System.out.print("Bin " + i + ": ");
            System.out.println(
                    "min weight = " + tempBin.getMinLoad() + ", max weight = " + tempBin.getCapacity() + ", max P = "
                            + tempBin.getP() + ", max types = " + tempBin.getT() + ", max layer = " + tempBin.getR());
            System.out.print("items: ");

            double totalWeight = 0;
            double totalP = 0;
            int totalTypes = 0;
            int totalLayer = 0;

            for (int j = 0; j < this.numItems; j++) {
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
            for (int j = 0; j < this.numItems; j++) {
                if (this.X[i][j].getValue() == 1) {
                    tempTypes.add(this.input.getItems()[j].getT());
                    tempLayers.add(this.input.getItems()[j].getR());
                }
            }
            totalTypes = tempTypes.size();
            totalLayer = tempLayers.size();

            System.out.println("Total items: " + "total weight = " + totalWeight + ", total P = " + totalP
                    + ", total types = " + totalTypes + ", total layers = " + totalLayer);
        }
        int numItemFail = 0;
        int numItemInBin = 0;
        for (int i = 0; i < this.numItems; i++) {
            if (itemLog[i] > 1) {
                numItemFail++;
            }
            if (itemLog[i] > 0) {
                numItemInBin++;
            }
        }
        System.out.println("\nNum item is put in bin = " + numItemInBin);
        System.out.println("Num item is put in multi bin = " + numItemFail);
        System.out.println("=============================================================");
    }

    public static void main(String[] args) {
        String fn = System.getProperty("user.dir") + 
                "/src/khmtk60/miniprojects/G4/data/MinMaxTypeMultiKnapsackInput.json";
        MultiKnapsack s = new MultiKnapsack(fn, 1000);
        System.out.println("Load data okay !");
        s.stateModel();
        System.out.println("Build state model okay !");
        System.out.println("Searching .......");
        s.search();
        s.showResult();

        
        MinMaxTypeMultiKnapsackSolution solution = new MinMaxTypeMultiKnapsackSolution();
        int[] binOfItem = new int[s.input.getItems().length];
        
        for (int i = 0; i < s.input.getItems().length; i++) {
            for (int b = 0; b < s.input.getBins().length; b++) {
                if (s.X[b][i].getValue() == 1) {
                    binOfItem[i] = b;
                    break;
                }
            }
        }
        
        solution.setBinOfItem(binOfItem);
        // Check
        SolutionChecker checker = new SolutionChecker();
        System.out.println(checker.check(s.input, solution));
    }
}
