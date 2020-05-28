/*
// Filename: LiquidStoring.java
// Description:
// Created by ngocjr7 on [22-05-2020 16:21:41]
*/
package cbls115676khmt61.NgocBH_20164797;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import cbls115676khmt61.NgocBH_20164797.search.AssignMove;
import cbls115676khmt61.NgocBH_20164797.search.HillClimbingSearch;
import cbls115676khmt61.NgocBH_20164797.search.SwapMove;
import cbls115676khmt61.NgocBH_20164797.search.TabuSearch;
import cbls115676khmt61.NgocBH_20164797.search.LocalSearch;

public class LiquidStoring {
    int m;
    int[] cap;
    int n;
    int[] vol;
    int[][] invalid;
    VarIntLS[] x;
    LocalSearchManager mgr;
    ConstraintSystem S;

    LiquidStoring() {
        init_default_input();
    }

    void init_default_input() {
        m = 5;
        cap = new int[]{60,70,80,90,100};
        n = 20;
        vol = new int[]{20,15,10,20,20,25,30,15,10,10,20,25,20,10,30,40,25,35,10,10};
        invalid = new int[][]{{0,1}, {7,8}, {12,17}, {8,9}, {1,2,9}, {0,9,12}};
    }


    void printResults() {
        for (int i = 0; i < n; i++)
            System.out.printf("%d ", x[i].getValue());
        System.out.println();
        for (int i = 0; i < m; i++) {
            System.out.printf("Bin %d:", i);
            for (int j = 0; j < n; j++) {
                if ( x[j].getValue() == i ) 
                    System.out.printf("%d ", j);
            }
            System.out.println();
        }
    }

    void stateModel() {
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);
        x = new VarIntLS[n];
        for (int i = 0; i < n; i++) 
            x[i] = new VarIntLS(mgr, 0, m-1);

        for (int i = 0; i < m; i++) {
            S.post(new LessOrEqual(new ConditionalSum(x, vol, i), cap[i]));
        }
        
        
        for (int i = 0; i < invalid.length; i++) {
            IConstraint[] c = new IConstraint[invalid[i].length-1];
            for (int j = 1; j < invalid[i].length; j++) {
                c[j-1] = new NotEqual(x[invalid[i][j]], x[invalid[i][j-1]]);
            }
            S.post(new OR(c));
        }
        
        mgr.close();
    }

    public static void main(String[] args) {
        int seed = 8;
        int max_iter = 1000;
        int tabu_size = 100;
        int max_stable = 10;

        LiquidStoring prob = new LiquidStoring();

        prob.stateModel();
        

        // HillClimbingSearch searcher1 = new HillClimbingSearch(max_iter, seed);
        // searcher1.satisfy_constraint(prob.S);
        
        TabuSearch searcher2 = new TabuSearch(tabu_size, max_stable, max_iter, seed);
        searcher2.satisfy_constraint(prob.S, new AssignMove());
        // prob.verify();

        prob.printResults();
    }
}