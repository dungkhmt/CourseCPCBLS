package khmtk60.miniprojects.G4;

import java.util.HashSet;

import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.SolutionChecker;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputBin;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackSolution;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

public class MultiKnapsack2 {
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
    public MultiKnapsack2(String fn, int alpha) {
        this.alpha = alpha; // use to cast double to integer
        this.input = (new MinMaxTypeMultiKnapsackInput()).loadFromFile(fn);
        this.numItems = input.getItems().length;
        this.numBins = input.getBins().length;
        System.out.println("the number of items : " + this.numItems);
        System.out.println("the number of bins :  " + this.numBins);
    }

    public void stateModel() {
        this.mgr = new LocalSearchManager();
        // Initial X
        this.X = new VarIntLS[this.numItems];
        for (int i = 0; i < this.numItems; i++) {
            this.X[i] = new VarIntLS(mgr, 0, this.numBins - 1);
        }
        // Constraints
        S = new ConstraintSystem(mgr);

        // Weight
        int[] w = new int[this.numItems];
        for (int i = 0; i < this.numItems; i++)
            w[i] = (int) input.getItems()[i].getW() * alpha;
        for (int b = 0; b < this.numBins; b++) {
            IFunction tempW = new ConditionalSum(X, w, b);
            IConstraint a1 = new LessOrEqual((int) input.getBins()[b].getMinLoad() * alpha, tempW);
            IConstraint a2 = new IsEqual(tempW, 0);
            S.post(new OR(a1, a2));
            S.post(new LessOrEqual(tempW, (int) input.getBins()[b].getCapacity() * alpha));
        }

        // P
        int[] p = new int[this.numItems];
        for (int i = 0; i < this.numItems; i++)
            p[i] = (int) input.getItems()[i].getP() * alpha;
        for (int b = 0; b < this.numBins; b++) {
            IFunction tempP = new ConditionalSum(X, p, b);
            S.post(new LessOrEqual(tempP, (int) input.getBins()[b].getP() * alpha));
        }

        // num types
        int[] t = new int[this.numItems];
        for (int i = 0; i < this.numItems; i++)
            t[i] = input.getItems()[i].getT();
        for (int b = 0; b < this.numBins; b++) {
            IFunction tempT = new ConditionCount(X, t, b);
            S.post(new LessOrEqual(tempT, input.getBins()[b].getT()));
        }

        // num layers
        int[] r = new int[this.numItems];
        for (int i = 0; i < this.numItems; i++)
            r[i] = input.getItems()[i].getR();
        for (int b = 0; b < this.numBins; b++) {
            IFunction tempR = new ConditionCount(X, r, b);
            S.post(new LessOrEqual(tempR, input.getBins()[b].getR()));
        }
        // item i in the bins of D[i]
        for (int i = 0; i < this.numItems; i++) {
            S.post(new IsIn(X[i], input.getItems()[i].getBinIndices()));
        }

        this.mgr.close();
    }

    public void search() {
        TabuSearch tabu = new TabuSearch();
        int tabulen = 10000;
        int maxIter = 500;
        int maxTime = 1000;
        int maxStable = 100;
        tabu.search(this.S, tabulen, maxTime, maxIter, maxStable);
    }

    public void showResult() {
        for (int b = 0; b < this.numBins; b++) {
            MinMaxTypeMultiKnapsackInputBin tempBin = this.input.getBins()[b];
            System.out.println("============================================================================");
            System.out.print("Bin " + b + ": ");
            System.out.println(
                    "min weight = " + tempBin.getMinLoad() + ", max weight = " + tempBin.getCapacity() + ", max P = "
                            + tempBin.getP() + ", max types = " + tempBin.getT() + ", max layer = " + tempBin.getR());
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

            System.out.println("\nTotal items: " + "total weight = " + totalWeight + ", total P = " + totalP
                    + ", total types = " + tHashSet.size() + ", total layers = " + rHashSet.size());
        }
        System.out.println("=========================================================================");
    }

    public static void main(String[] args) {
        String fn = System.getProperty("user.dir") + 
                "/src/khmtk60/miniprojects/G4/data/MinMaxTypeMultiKnapsackInput.json";
        MultiKnapsack2 s = new MultiKnapsack2(fn, 1000);
        System.out.println("Load data okay !");
        s.stateModel();
        System.out.println("Build state model okay !");
        System.out.println("Searching .......");
        s.search();
        s.showResult();
        
        MinMaxTypeMultiKnapsackSolution solution = new MinMaxTypeMultiKnapsackSolution();
        int[] binOfItem = new int[s.X.length];
        for (int i = 0; i < s.X.length; i++) {
            binOfItem[i] = s.X[i].getValue();
        }
        solution.setBinOfItem(binOfItem);
        // Check
        SolutionChecker checker = new SolutionChecker();
        System.out.println(checker.check(s.input, solution));
    }

}
