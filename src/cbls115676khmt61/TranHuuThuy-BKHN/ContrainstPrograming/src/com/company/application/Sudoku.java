package com.company.application;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Sudoku {
    private int N;
    private Model model;
    private IntVar[][] X;
    private int[][] x;

    public Sudoku(int n) {
        N = n;
        model = new Model("Sudoku");
        X = model.intVarMatrix(N, N, 1, N);
        x = new int[N][N];

        solver();
    }

    public int[][] getSol() {
        return x;
    }

    private void solver() {
        for (int i = 0; i < N; i++)
            model.allDifferent(X[i]).post();

        for (int i = 0; i < N; i++) {
            IntVar cols[] = new IntVar[N];
            for (int j = 0; j < N; j++) {
                cols[j] = X[j][i];
            }
            model.allDifferent(cols).post();
        }

        int l = (int) Math.sqrt(N);
        for (int i = 0; i < N; i++) {
            IntVar cells[] = new IntVar[N];

            for (int j = 0; j < N; j++) {
                cells[j] = X[i / l + j / 3][i % l * l + j % 3];
            }
            model.allDifferent(cells).post();
        }

        model.getSolver().solve();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                x[i][j] = X[i][j].getValue();
            }
        }
    }

    public void addConstraints(int[][] c) {
        if (c.length == 0 || c[0].length == 0) return;
        model = new Model("Sudoku");
        X = model.intVarMatrix(N, N, 1, N);
        x = new int[N][N];

        for (int i = 0; i < c.length; i++) {
            model.arithm(X[c[i][0]][c[i][1]], "=", c[i][2]).post();
        }

        solver();
    }

    private void printSudoku() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.printf("%3d", x[i][j]);
                if ((j + 1) % (int) (Math.sqrt(N)) == 0) System.out.print("\t\t");
                else System.out.print(" ");
            }
            if ((i + 1) % (int) (Math.sqrt(N)) == 0) System.out.println("\n");
            else System.out.println();
        }
    }


    public static void main(String[] args) {
        Sudoku app = new Sudoku(9);
        app.addConstraints(new int[][]{{1, 1, 5},{2, 8, 2}, {4, 4, 1}});
        app.printSudoku();
    }
}
