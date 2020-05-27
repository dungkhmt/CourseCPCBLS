/*
// Filename: Container.java
// Description:
// Created by ngocjr7 on [27-05-2020 13:03:13]
*/
package cbls115676khmt61.ngocbh_20164797;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.OR;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.model.IConstraint;
import localsearch.functions.basic.ConstraintViolations;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.IFunction;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.max_min.Max;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import cbls115676khmt61.ngocbh_20164797.search.AssignMove;
import cbls115676khmt61.ngocbh_20164797.search.HillClimbingSearch;
import cbls115676khmt61.ngocbh_20164797.search.SwapMove;
import cbls115676khmt61.ngocbh_20164797.search.TabuSearch;
import cbls115676khmt61.ngocbh_20164797.search.LocalSearch;

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
                new AND( new LessOrEqual(new FuncPlus(x[i], w[i]) , W), 
                    new LessOrEqual(new FuncPlus(y[i], l[i]) , L) ) ));
            S.post(new Implicate( new IsEqual(t[i], 1), 
                new AND( new LessOrEqual(new FuncPlus(x[i], l[i]) , W), 
                    new LessOrEqual(new FuncPlus(y[i], w[i]) , L))));
        }

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
                // c3[3] = new LessOrEqual(new FuncPlus(y[j], w[j]),y[i]);
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
    }

    public void verify() {
        int[][] c = new int[L][W];
        for (int i = 0; i < n; i++) {
            System.out.printf("(%d,%d,%d)\n", x[i].getValue(), y[i].getValue(), t[i].getValue());
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
                System.out.printf("%d", c[i][j]);  
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int seed = 7;
        int max_iter = 100;
        int tabu_size = 1000;
        int max_stable = 10;

        Container prob = new Container();

        prob.state_model();
    
        // HillClimbingSearch searcher1 = new HillClimbingSearch(max_iter, seed);
        // searcher1.satisfy_constraint(prob.S);
        
        TabuSearch searcher2 = new TabuSearch(tabu_size, max_stable, max_iter, seed);
        searcher2.satisfy_constraint(prob.S, new AssignMove());

        prob.print_results();
        prob.verify();
    }
}