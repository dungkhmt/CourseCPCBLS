package com.company.ConstraintPrograming;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Graph_Partitioning {

    public static int c[] = new int[]{
            0, 0, 5, 0, 1, 0, 7, 0, 2, 8,
            0, 0, 8, 2, 0, 0, 0, 3, 0, 0,
            5, 8, 0, 8, 7, 0, 0, 4, 6, 0,
            0, 2, 8, 0, 0, 1, 0, 0, 5, 0,
            1, 0, 7, 0, 0, 0, 8, 9, 0, 0,
            0, 0, 0, 1, 0, 0, 0, 0, 4, 5,
            7, 0, 0, 0, 8, 0, 0, 0, 0, 4,
            0, 3, 4, 0, 9, 0, 0, 0, 0, 0,
            2, 0, 6, 5, 0, 4, 0, 0, 0, 3,
            8, 0, 0, 0, 0, 5, 4, 0, 3, 0};

    public static void main(String[] args) {
        Model model = new Model("Graph Partitioning");
        int N = 10;
        IntVar X[] = new IntVar[N];
        for (int i = 0; i < N; i++) {
            X[i] = model.intVar("X" + i, 0, 1);
        }

        IntVar Z[] = model.intVarArray("Z", N*N, 0, 1);


        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                model.ifThen(
                        model.arithm(Z[i * N + j], "=", 1),
                        model.arithm(X[i], "+", X[j], "=", 1)
                );

                model.ifThen(
                        model.arithm(X[i], "+", X[j], "=", 1),
                        model.arithm(Z[i * N + j], "=", 1)
                );
            }
        }

        model.sum(X, "+", N / 2).post();

        IntVar F = model.intVar("F", 0, IntVar.MAX_INT_BOUND);


        model.scalar(Z, c, "=", F).post();


        model.setObjective(false, F);

        while (model.getSolver().solve()) {
            System.out.println("My solution");
            for (int i = 0; i < N; i++) {
                System.out.print(X[i].getValue() + " ");
            }
            System.out.println("\nCost :" + F.getValue());
        }

    }

}
