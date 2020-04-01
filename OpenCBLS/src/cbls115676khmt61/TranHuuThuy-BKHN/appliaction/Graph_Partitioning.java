package com.company.appliaction;

import com.company.localsearch.HillClimbingSeachObjectiveFunction;
import localsearch.constraints.basic.*;
import localsearch.functions.basic.ConstraintViolations;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.Sum;
import localsearch.model.*;

import java.util.Random;

public class Graph_Partitioning {

    int N;
    int c[][] = new int[][]{
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

    VarIntLS[][] Z;

    LocalSearchManager mgr;
    ConstraintSystem S;

    VarIntLS[] P;

    IFunction F;

    IFunction f;
    Random r = new Random();

    public void stateModel() {
        N = 10;
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);

        P = new VarIntLS[N];

        Z = new VarIntLS[N][N];

        for (int i = 0; i < N; i++) {
            P[i] = new VarIntLS(mgr, 0, 1);

            for (int j = 0; j < N; j++) {
                Z[i][j] = new VarIntLS(mgr, 0, 1);
            }
        }

        // ràng buộc 2 tập bằng nhau
        IFunction f0 = new ConditionalSum(P, 1);
        IFunction f1 = new ConditionalSum(P, 0);
        S.post(new IsEqual(f0, f1));


        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (c[i][j] > 0) {
                    S.post(new Implicate(new NotEqual(P[i], P[j]), new IsEqual(Z[i][j], 1)));
                } else S.post(new IsEqual(Z[i][j], 0));
            }
        }

        //  hàm tổng trọng số 2 tập
        IFunction fu[] = new IFunction[N];
        for (int i = 0; i < N; i++) {
            fu[i] = new ConditionalSum(Z[i], c[i], 1);
        }

        this.f = new Sum(fu);


        IFunction s = new ConstraintViolations(S);
        F = new FuncPlus(new FuncMult(s, 1000), new FuncPlus(this.f, 1));

        mgr.close();

    }


    public void search(int maxIter) {

        HillClimbingSeachObjectiveFunction hill = new HillClimbingSeachObjectiveFunction(F, S);
        hill.search(maxIter);

        System.out.print("\n P = ");
        for (int i = 0; i < N; i++)
            System.out.print(P[i].getValue() + " ");
        System.out.println();
    }

    public static void main(String[] args) {

        Graph_Partitioning graph = new Graph_Partitioning();
        graph.stateModel();
        graph.search(1000);
    }
}
