/*
// Filename: Container.java
// Description:
// Created by ngocjr7 on [27-05-2020 13:03:13]
*/
package cbls115676khmt61.NgocBH_20164797;

import cbls115676khmt61.NgocBH_20164797.search.AssignMove;
import cbls115676khmt61.NgocBH_20164797.search.HillClimbingSearch;
import cbls115676khmt61.NgocBH_20164797.search.HillClimbingSearchS;
import cbls115676khmt61.NgocBH_20164797.search.TabuSearch;
import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Container {
    int n;
    int L, W;
    int[] l, w, p;
    VarIntLS[] x, y, t;
    LocalSearchManager mgr;
    ConstraintSystem S;

    public Container() {
        init_default_data();
    }
    
    public void init_default_data() {
        L = 6; W = 4;
        n = 6;
        p = new int[]{0,1,2,3,4,5};
        w = new int[]{1,3,2,3,1,2};
        l = new int[]{4,1,2,1,4,3}; 
    }

    void state_model() {
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);

        x = new VarIntLS[n];
        y = new VarIntLS[n];
        t = new VarIntLS[n];
        for (int i = 0; i < n; i++) {
            x[i] = new VarIntLS(mgr, 0, W-1);
            y[i] = new VarIntLS(mgr, 0, L-1);
            t[i] = new VarIntLS(mgr, 0, 1);
        }
        
        // constraints
        for (int i = 0; i < n; i++) {
            S.post(new Implicate( new IsEqual(t[i], 0), 
                new LessOrEqual(new FuncPlus(x[i], w[i]) , W))); 
            S.post(new Implicate( new IsEqual(t[i], 0), 
                new LessOrEqual(new FuncPlus(y[i], l[i]) , L)));
            S.post(new Implicate( new IsEqual(t[i], 1), 
                new LessOrEqual(new FuncPlus(x[i], l[i]) , W)));
            S.post(new Implicate( new IsEqual(t[i], 1),     
                new LessOrEqual(new FuncPlus(y[i], w[i]) , L)));
        }
        
        // S.post(new IsEqual(x[0], 0));
        // S.post(new IsEqual(y[0], 0));
        // S.post(new IsEqual(t[0], 1));

        // S.post(new IsEqual(x[1], 0));
        // S.post(new IsEqual(y[1], 1));
        // S.post(new IsEqual(t[1], 0));

        // S.post(new IsEqual(x[2], 1));
        // S.post(new IsEqual(y[2], 2));
        // S.post(new IsEqual(t[2], 0));

        // S.post(new IsEqual(x[3], 3));
        // S.post(new IsEqual(y[3], 1));
        // S.post(new IsEqual(t[3], 1));



        for (int i = 0; i < n - 1; ++i) 
            for (int j = i + 1; j < n; ++j) {
                // items i and j cannot overlap
                IConstraint[] c1 = new IConstraint[2];
                c1[0] = new IsEqual(t[i], 0);
                c1[1] = new IsEqual(t[j], 0);
                IConstraint c2 = new AND(c1);
                IConstraint[] c3 = new IConstraint[3];
                c3[0] = new LessOrEqual(new FuncPlus(x[i], w[i]),x[j]);
                c3[1] = new LessOrEqual(new FuncPlus(x[j], w[j]),x[i]);
                c3[2] = new LessOrEqual(new FuncPlus(y[i], l[i]),y[j]);
                // c3[3] = new LessOrEqual(new FuncPlus(y[j], l[j]),y[i]);
                IConstraint c4 = new OR(c3);
                S.post(new Implicate(c2, c4));

                c1 = new IConstraint[2];
                c1[0] = new IsEqual(t[i], 0);
                c1[1] = new IsEqual(t[j], 1);
                c2 = new AND(c1);
                c3 = new IConstraint[3];
                c3[0] = new LessOrEqual(new FuncPlus(x[i], w[i]),x[j]);
                c3[1] = new LessOrEqual(new FuncPlus(x[j], l[j]),x[i]);
                c3[2] = new LessOrEqual(new FuncPlus(y[i], l[i]),y[j]);
                // c3[2] = new LessOrEqual(new FuncPlus(y[j], w[j]),y[i]);
                c4 = new OR(c3);
                S.post(new Implicate(c2, c4));

                c1 = new IConstraint[2];
                c1[0] = new IsEqual(t[i], 1);
                c1[1] = new IsEqual(t[j], 0);
                c2 = new AND(c1);
                c3 = new IConstraint[3];
                c3[0] = new LessOrEqual(new FuncPlus(x[i], l[i]),x[j]);
                c3[1] = new LessOrEqual(new FuncPlus(x[j], w[j]),x[i]);
                c3[2] = new LessOrEqual(new FuncPlus(y[i], w[i]),y[j]);
                // c3[3] = new LessOrEqual(new FuncPlus(y[j], l[j]),y[i]);
                c4 = new OR(c3);
                S.post(new Implicate(c2, c4));

                c1 = new IConstraint[2];
                c1[0] = new IsEqual(t[i], 1);
                c1[1] = new IsEqual(t[j], 1);
                c2 = new AND(c1);
                c3 = new IConstraint[3];
                c3[0] = new LessOrEqual(new FuncPlus(x[i], l[i]),x[j]);
                c3[1] = new LessOrEqual(new FuncPlus(x[j], l[j]),x[i]);
                c3[2] = new LessOrEqual(new FuncPlus(y[i], w[i]),y[j]);
                // c3[3] = new LessOrEqual(new FuncPlus(y[j], w[j]),y[i]);
                c4 = new OR(c3);
                S.post(new Implicate(c2, c4));

            }
        

        mgr.close();
    }

    public void print_results() {
        for (int i = 0; i < n; i++) {
            System.out.printf("(%d,%d,%d)", x[i].getValue(), y[i].getValue(), t[i].getValue());
        }
        System.out.println();
    }

    public void verify() {
        int[][] c = new int[L][W];
        for (int i = 0; i < n; i++) {
            if (t[i].getValue() == 0) {
                for (int ii = y[i].getValue(); ii < Math.min(y[i].getValue() + l[i], L); ii++)
                    for (int jj = x[i].getValue(); jj < Math.min(x[i].getValue() + w[i], W); jj++)
                        c[ii][jj] = i+1;
            } else {
                for (int ii = y[i].getValue(); ii < Math.min(y[i].getValue() + w[i], L); ii++)
                    for (int jj = x[i].getValue(); jj < Math.min(x[i].getValue() + l[i], W); jj++)
                        c[ii][jj] = i+1;
            }
        }
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < W; j++)
                if (c[i][j] > 0)
                    System.out.printf("%d", c[i][j]-1);  
                else
                    System.out.print(".");
            System.out.println();
        }

    }

    public static void main(String[] args) {
        int seed = 2;
        int max_iter = 10000;
        int tabu_size = 0;
        int max_stable = 50;

        Container prob = new Container();
        prob.state_model();
        // for (int seed = 0; seed < 100; seed++) {
        //     HillClimbingSearch searcher1 = new HillClimbingSearch(max_iter, seed);
        //     searcher1.satisfy_constraint(prob.S);
        //     System.out.printf("Ngoc: %d: %d\n", seed, prob.S.violations());
        //     if (prob.S.violations() == 0 ) {
        //         break;
        //     }
        // }

        TabuSearch searcher2 = new TabuSearch(tabu_size, max_stable, max_iter, seed);
        searcher2.satisfy_constraint(prob.S, new AssignMove());

        prob.print_results();
        prob.verify();
    }
}