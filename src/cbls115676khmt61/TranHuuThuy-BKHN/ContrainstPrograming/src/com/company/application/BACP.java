package com.company.application;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class BACP {
    private int N;
    private int P;
    private int[] credits;
    private int[][] pre;
    private int a;
    private int b;
    private int c;
    private int d;

    private IntVar X[];
    private int x[];
    Model model;

    public BACP(int n, int p, int[] credits, int[][] pre, int a, int b, int c, int d) {
        model = new Model("BACP");
        N = n;
        P = p;
        this.credits = credits;
        this.pre = pre;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;

        X = model.intVarArray("X", N, 1, P);
        x = new int[N];
        solver();
    }

    public int[] getSol() {
        return x;
    }

    private void solver() {
        for (int i = 0; i < pre.length; i++) {
            model.arithm(X[pre[i][0]], "<", X[pre[i][1]]).post();
        }

        IntVar C[] = model.intVarArray(P, 0, IntVar.MAX_INT_BOUND);
        for (int i = 1; i <= P; i++) {
            model.count(i, X, C[i - 1]).post();
            model.arithm(C[i - 1], ">=", a).post();
            model.arithm(C[i - 1], "<=", b).post();
        }

        IntVar Cost[][] = model.intVarMatrix(P, N, 0, 1);
        IntVar S[] = model.intVarArray(P, 0, IntVar.MAX_INT_BOUND);
        for (int i = 0; i < N; i++) {
            for (int j = 1; j <= P; j++) {
                model.ifThenElse(
                        model.arithm(X[i], "=", j),
                        model.arithm(Cost[j - 1][i], "=", 1),
                        model.arithm(Cost[j - 1][i], "=", 0)
                );
            }
        }

        for (int i = 0; i < P; i++) {
            model.scalar(Cost[i], credits, "=", S[i]).post();
            model.arithm(S[i], ">=", c).post();
            model.arithm(S[i], "<=", d).post();
        }

        model.getSolver().solve();
        for (int i = 0; i < N; i++) {
            x[i] = X[i].getValue();
        }
    }

    public static void main(String[] args) {
        int N = 12;
        int P = 4;
        int[] credits = {2, 1, 2, 1, 3, 2, 1, 3, 2, 3, 1, 3};
        int[][] pre = {
                {1, 0},
                {5, 8},
                {4, 5},
                {4, 7},
                {3, 10},
                {5, 11}
        };
        int a = 3;
        int b = 3;
        int c = 5;
        int d = 7;

        long s = System.currentTimeMillis();
        BACP app = new BACP(N, P, credits, pre, a, b, c, d);

        System.out.print("My solution :");
        for (int x : app.getSol()) {
            System.out.print(x + " ");
        }
        System.out.println("\nTime " + (System.currentTimeMillis() - s) / 1000.0 + "(s)");

    }
}
