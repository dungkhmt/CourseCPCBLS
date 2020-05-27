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
    VarIntLS[] x, y, o;
    VarIntLS[][] c;
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
        o = new VarIntLS[n];
        for (int i = 0; i < n; i++) {
            x[i] = new VarIntLS(mgr, 0, L-1);
            y[i] = new VarIntLS(mgr, 0, W-1);
            o[i] = new VarIntLS(mgr, 0, 1);
        }

        c = new VarIntLS[L][W];
        for (int i = 0; i < L; i++) 
            for (int j = 0; j < W; j++) 
                c[i][j] = new VarIntLS(mgr, 0, n);
        
        // constraints
        for (int i = 0; i < n; i++) {
            S.post(new Implicate( new IsEqual(o[i], 0), 
                new AND( new LessOrEqual(new FuncPlus(x[i], l[i]) , L), 
                    new LessOrEqual(new FuncPlus(y[i], w[i]) , W))));
        }

        for (int i = 0; i < n - 1; ++i) {
            for (int j = i + 1; j < n; ++j) {
                IConstraint[] c1 = { new IsEqual(o[i], 0), new IsEqual(o[j], 0) };
                IConstraint c2 = new AND(c1);
                IConstraint[] c3 = { new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]), new LessOrEqual(new FuncPlus(y[i], l[i]), y[j]), new LessOrEqual(new FuncPlus(y[j], l[j]), y[i]) };
                IConstraint c4 = new OR(c3);
                S.post(new Implicate(c2, c4));
            }
        }
        for (int i = 0; i < n - 1; ++i) {
            for (int j = i + 1; j < n; ++j) {
                IConstraint[] c1 = { new IsEqual(o[i], 0), new IsEqual(o[j], 1) };
                IConstraint c2 = new AND(c1);
                IConstraint[] c3 = { new LessOrEqual(new FuncPlus(x[j], l[j]), x[i]), new LessOrEqual(new FuncPlus(y[i], l[i]), y[j]), new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]) };
                IConstraint c4 = new OR(c3);
                S.post(new Implicate(c2, c4));
            }
        }
        for (int i = 0; i < n - 1; ++i) {
            for (int j = i + 1; j < n; ++j) {
                IConstraint[] c1 = { new IsEqual(o[i], 1), new IsEqual(o[j], 0) };
                IConstraint c2 = new AND(c1);
                IConstraint[] c3 = { new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]), new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]), new LessOrEqual(new FuncPlus(y[j], l[j]), y[i]) };
                IConstraint c4 = new OR(c3);
                S.post(new Implicate(c2, c4));
            }
        }
        for (int i = 0; i < n - 1; ++i) {
            for (int j = i + 1; j < n; ++j) {
                IConstraint[] c1 = { new IsEqual(o[i], 1), new IsEqual(o[j], 1) };
                IConstraint c2 = new AND(c1);
                IConstraint[] c3 = { new LessOrEqual(new FuncPlus(x[j], l[j]), x[i]), new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]), new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]) };
                IConstraint c4 = new OR(c3);
                S.post(new Implicate(c2, c4));
            }
        }

        mgr.close();
    }

    public void print_results() {
        for (int i = 0; i < n; i++) {
            System.out.printf("(%d,%d,%d)", x[i].getValue(), y[i].getValue(), o[i].getValue());
        }
    }

    public static void main(String[] args) {
        int seed = 8;
        int max_iter = 1000;
        int tabu_size = 100;
        int max_stable = 10;

        Container prob = new Container();

        prob.state_model();
    
        // HillClimbingSearch searcher1 = new HillClimbingSearch(max_iter, seed);
        // searcher1.satisfy_constraint(prob.S);
        
        TabuSearch searcher2 = new TabuSearch(tabu_size, max_stable, max_iter);
        searcher2.satisfy_constraint(prob.S, new AssignMove());
        // prob.verify();

        prob.print_results();
    }
}