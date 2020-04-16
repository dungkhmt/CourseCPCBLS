/*
* Problem: GraphParitioning.java
* Description: 
* Created by ngocjr7 on [2020-04-01 12:51:03]
*/
package cbls115676khmt61.ngocbh_20164797;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.functions.basic.ConstraintViolations;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
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

public class GraphPartitioning {
    int n;
    public int[][] edges;
    VarIntLS[] x;
    VarIntLS[][] z;
    LocalSearchManager mgr;
    ConstraintSystem S;
    int max_c;
    IFunction obj;

    public GraphPartitioning() {
        this.max_c = 100;
    }

    public GraphPartitioning(int n) {
        this.n = n;
        this.edges = new int[n][n];
        this.max_c = 100;
    }

    public void stateModel() {
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);
        x = new VarIntLS[n];
        for (int i = 0; i < n; i++)
            x[i] = new VarIntLS(mgr, 0, 1);
        z = new VarIntLS[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                z[i][j] = new VarIntLS(mgr, 0, 1);

        // for (int i = 0; i < n; i++)
        // for (int j = 0; j < n; j++) {
        // S.post(new Implicate( new AND(new IsEqual(x[i], 1), new IsEqual(x[j], 0)),
        // new IsEqual(z[i][j], 1) ));
        // // System.out.print(edges[i][j]);
        // }

        S.post(new IsEqual(new Sum(x), n / 2));
        IFunction[] t = new IFunction[n * n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                IFunction[] xij = { new FuncMinus(x[i], x[j]), new FuncMinus(x[j], x[i]) };
                IFunction cost = new FuncMult(new Max(xij), edges[i][j]);
                t[i * n + j] = cost;
            }
        // IFunction
        obj = new Sum(t);
        mgr.close();
    }

    void printResults() {
        System.out.printf("objective = %d and violations = %d\n", obj.getValue() / 2, S.violations());
        for (int i = 0; i < n; i++)
            System.out.printf("%d ", x[i].getValue());
        System.out.println();
        for (int i = 0; i < n; i++)
            if (x[i].getValue() == 1)
                System.out.printf("%d ", i);
        System.out.println();
        for (int i = 0; i < n; i++)
            if (x[i].getValue() == 0)
                System.out.printf("%d ", i);
        System.out.println();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++)
                if ((x[i].getValue() == 1) ^ (x[j].getValue() == 1) == true)
                    if (edges[i][j] != 0)
                        System.out.printf("%d %d %d\n", i, j, edges[i][j]);
        }
    }

    void search_alpha_beta(LocalSearch searcher)
    {
        int alpha = 1000;
        int beta = 1;
        IFunction fc = new FuncPlus(new FuncMult(new ConstraintViolations(this.S), alpha), new FuncMult(this.obj, beta));
        VarIntLS[] y = fc.getVariables();
        for (int i = 0; i < y.length; i++) 
            System.out.printf("%d ", y[i].getValue());
        IFunction x = new FuncMult(new FuncPlus(new ConstraintViolations(this.S), 1) ,alpha);
        System.out.printf(" obj = %d\n", x.getValue());
        searcher.minimize_objective(fc, new AssignMove());
    }

    public void read_from_file(String filename) {
        try {
            int n = 10;  
            int[][] edges;
            Scanner in = new Scanner(new File(filename)); 
            n = in.nextInt();
            edges = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++) 
                    edges[i][j] = in.nextInt();
            in.close();
            this.n = n;
            this.edges = edges;
        } catch(Exception e) {
            System.out.print("Something went wrong");
        }
    }

    public void read_default_input()
    {
        this.n = 10;
        this.edges = new int[][]{
            {0, 0, 5, 0, 1, 0, 7, 0, 2, 8},
            {0, 0, 8, 2, 0, 0, 0, 3, 0, 0},
            {5, 8, 0, 8, 7, 0, 0, 4, 6, 0},
            {0, 2, 8, 0, 0, 1, 0, 0, 5, 0},
            {1, 0, 7, 0, 0, 0, 8, 9, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0, 4, 5},
            {7, 0, 0, 0, 8, 0, 0, 0, 0, 4},
            {0, 3, 4, 0, 9, 0, 0, 0, 0, 0},
            {2, 0, 6, 5, 0, 4, 0, 0, 0, 3},
            {8, 0, 0, 0, 0, 5, 4, 0, 3, 0}};
    }

    public static void main(String[] args) {
        
        GraphPartitioning prob = new GraphPartitioning();
        prob.read_from_file("data/GraphPartitioning/gp-10.txt");
        // prob.read_default_input();
        int seed = 1;
        int max_iter = 100;
        int tabu_size = 10;
        int max_stable = 10;

        prob.stateModel();

        HillClimbingSearch searcher1 = new HillClimbingSearch(max_iter, seed);
        // searcher1.search_two_phase(prob.S, new AssignMove(), prob.obj, new SwapMove());
        // searcher1.minimize_objective_with_constraint(prob.obj,prob.S);
        // prob.search_alpha_beta(searcher1);
        
        TabuSearch searcher2 = new TabuSearch(tabu_size, max_stable, max_iter, seed);
        // searcher2.satisfy_constraint(prob.S, new AssignMove());
        // searcher2.search_two_phase(prob.S, new AssignMove(), prob.obj, new SwapMove());
        // searcher2.minimize_objective_with_constraint(prob.obj,prob.S, new AssignMove());
        prob.search_alpha_beta(searcher2);
        prob.printResults();
    }
}


        