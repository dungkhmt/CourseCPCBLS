package com.company.application;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;


public class CSP {
    public static void main(String[] args) {
        Model model = new Model("CSP");

        IntVar X[] = model.intVarArray("X", 5, 1, 5);

        model.arithm(X[1], "-", X[2], "!=", 3).post();

        model.arithm(X[3], "<=", X[4]).post();

        model.arithm(X[2], "+", X[3], "=", model.intOffsetView(X[0], 1)).post();

        model.arithm(X[4], "<=", 3).post();

        model.arithm(X[1], "+", X[4], "=", 7);

        model.ifThen(
                model.arithm(X[2], "=", 1),
                model.arithm(X[4], "!=", 2)
        );

        // hàm mục tiêu
        IntVar F = model.intVar("Objective",5,25);
        model.sum(X, "+", F).post();

        model.setObjective(true, F);

        Solver solver = model.getSolver();


//        if (solver.solve()){
//            System.out.print("My Solution :");
//            for (IntVar x : X) {
//                System.out.print(x.getValue() + " ");
//            }
//        }else {
//            System.out.println("Not Perfect");
//        }

        while (solver.solve()) {
            System.out.print("F = " + F.getValue() + " : ");
            for (IntVar x : X) {
                System.out.print(x.getValue() + " ");
            }
            System.out.println();
        }


    }
}
