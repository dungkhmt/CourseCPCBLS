package cbls115676khmt61.vudt_20164705;

import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class LiquidStoring {
    private LocalSearchManager mgr;
    private ConstraintSystem constraints;
    private int[] liquids;
    private int[] binCapacity;
    // bin[i][j] means volume of liquid j in bin[i]
    private VarIntLS[][] bins;
    private int nbOfBins;
    private int nbOfLiquids;
    private int[][] liquidViolations;


    public LiquidStoring() {
        liquids = new int[] {20, 15, 10, 20, 20, 25, 30, 15, 10, 10,
                20, 25, 20, 10, 30, 40, 25, 35, 10, 10
            };
        binCapacity = new int[] {60, 70, 90, 80, 100};
        nbOfBins = 5;
        nbOfLiquids = 20;
        liquidViolations = new int[][]{
                {0, 1},
                {7, 8},
                {12, 17},
                {8, 9},
                {1, 2, 9},
                {0, 9, 12}
        };

        mgr = new LocalSearchManager();
        bins = new VarIntLS[nbOfBins][nbOfLiquids];
        for(int i = 0; i < nbOfBins; i++) {
            for(int j = 0; j < nbOfLiquids; j++) {
                bins[i][j] = new VarIntLS(mgr, 0, binCapacity[i]);
            }
        }
        constraints = new ConstraintSystem(mgr);
        for(int i = 0; i < nbOfBins; i++) {
            constraints.post(new LessOrEqual(new Sum(bins[i]), binCapacity[i]));
        }
        for(int i = 0; i < nbOfLiquids; i++) {
            VarIntLS[] liquid = new VarIntLS[nbOfBins];
            for(int j = 0; j < nbOfBins; j++) {
                liquid[j] = bins[j][i];
            }
            constraints.post(new IsEqual(new Sum(liquid), liquids[i]));
        }
        for(int i = 0; i < liquidViolations.length; i++) {
            int[] liquidViolation = liquidViolations[i];
            IConstraint[] ct = new IConstraint[liquidViolation.length];
            for (int j = 0; j < liquidViolation.length; j++) {
            }
        }

        mgr.close();

    }

    public void search() {
        MinMaxSelector mns = new MinMaxSelector(constraints);
        int it = 0;
        int MAX_ITER = 10000;
        while (it < MAX_ITER && constraints.violations() > 0) {
            VarIntLS queenPos = mns.selectMostViolatingVariable();
            int queenMove = mns.selectMostPromissingValue(queenPos);
            queenPos.setValuePropagate(queenMove);
            it++;
        }
    }

    public void printSolution() {
        for(int i = 0; i < nbOfBins; i++) {
            System.out.println("Bin " + i + ":");
            for(int j = 0; j < nbOfLiquids; j++) {
                if(bins[i][j].getValue() > 0) {
                    System.out.println("Liquid " + j + ": " + bins[i][j].getValue());
                }
            }
            System.out.println("--------------------------------");
        }
    }

    public static void main(String[] args) {
        LiquidStoring app = new LiquidStoring();
        app.search();
        app.printSolution();

    }


}
