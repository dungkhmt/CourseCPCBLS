package ConstrainProgramming;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;

public class sportschedulechocosolver {

    int N = 4;// number of teams 0,1,...N-1
    int[][] d = {{0, 3, 2, 4}, {9, 0, 2, 3}, {1, 2, 0, 7},
            {1, 1, 4, 0}};
    int T = 6;// 2N-2

    IntVar X[][][];
    IntVar Y[][][];
    IntVar F[][][][];
    IntVar D[];
    IntVar obj;

    Model model;

    public void buildModel() {
        model = new Model("Sport Scheduling");

        X = new IntVar[N][N][T + 1];
        Y = new IntVar[N][N][T + 1];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int h = 0; h <= T; h++) {
                    X[i][j][h] = model.intVar(0, 1);
                    Y[i][j][h] = model.intVar(0, 1);
                }
            }
        }


        F = new IntVar[N][N][N][T + 1];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int h = 0; h < N; h++) {
                    for (int k = 0; k <= T; k++) {
                        F[i][j][h][k] = model.intVar(0, 1);
                    }
                }
            }
        }

        D = model.intVarArray("D", N, 0, 10000);

        // contrainst
        //----------------------------------------------------------------
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int t = 1; t <= T; t++) {
                    model.arithm(X[i][j][t], "=", Y[j][i][t]).post();
                }
            }
        }

//        for (int t = 0; t <= T; t++) {
//            ArrayList<IntVar> array = new ArrayList<>();
//            for (int i = 0; i < N; i++) {
//                for (int j = 0; j < N; j++) {
//                    if (i != j) array.add(X[i][j][t]);
//                }
//            }
//            IntVar[] vars = new IntVar[array.size()];
//            for (int i = 0; i < vars.length; i++)
//                vars[i] = array.get(i);
//            model.sum(vars, "=", 1).post();
//        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i != j) {
                    ArrayList<IntVar> array = new ArrayList<>();
                    for (int t = 0; t <= T; t++) {
                        array.add(X[i][j][t]);
                    }
                    IntVar s[] = new IntVar[array.size()];
                    for (int h = 0; h < s.length; h++) {
                        s[h] = array.get(h);
                    }
                    model.sum(s, "=", 1).post();

                }
            }
        }


        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) {
                    for (int t = 0; t < T; t++) {
                        model.arithm(Y[i][j][t], "+", Y[i][k][t + 1], "<=", model.intOffsetView(model.intScaleView(F[i][j][k][t], 2), +1)).post();
                    }
                }
            }
        }

        for (int i = 0; i < N; i++) {
            for (int t = 0; t <= T; t++) {
                IntVar[] s = new IntVar[N];
                for (int j = 0; j < N; j++)
                    s[j] = Y[j][i][t];
                model.sum(s, "=", Y[i][i][t]).post();
            }
        }


        for (int i = 0; i < N; i++) {
            for (int t = 0; t <= T; t++) {
                ArrayList<IntVar> s1 = new ArrayList<>();
                ArrayList<IntVar> s2 = new ArrayList<>();
                for (int j = 0; j < N; j++) {
                    if (i != j) {
                        s1.add(X[i][j][t]);
                        s2.add(Y[i][j][t]);
                    }
                }
                IntVar s11[] = new IntVar[s1.size()];
                IntVar s12[] = new IntVar[s2.size()];
                for (int h = 0; h < s11.length; h++) {
                    s11[h] = s1.get(h);
                }

                for (int h = 0; h < s12.length; h++) {
                    s12[h] = s2.get(h);
                }
                IntVar sum1 = model.intVar(0, 10000);
                IntVar sum2 = model.intVar(0, 10000);
                model.sum(s11, "=", sum1).post();
                model.sum(s12, "=", sum2).post();
                model.arithm(sum1, "+", sum2, "=", 1).post();
            }
        }

        for (int i = 0; i < N; i++) {
            ArrayList<IntVar> array = new ArrayList<>();
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) {
                    if (i != j && j != k) {
                        for (int t = 0; t <= T; t++) {
                            array.add(model.intScaleView(F[i][j][k][t], d[j][k]));
                        }
                    }
                }
            }

            IntVar[] s = new IntVar[array.size()];
            for (int j = 0; j < s.length; j++) {
                s[j] = array.get(j);
            }
            model.sum(s, "=", D[i]).post();
        }
        //-----------------------------------------------------

        obj = model.intVar(0, 10000);
        model.sum(D, "=", obj).post();
        model.setObjective(false, obj);

    }

    public void search() {
        Solver solver = model.getSolver();
        while (solver.solve()) {
            System.out.println("Objective " + obj.getValue());
        }

    }

    public static void main(String[] args) {
        sportschedulechocosolver sport = new sportschedulechocosolver();
        sport.buildModel();
        sport.search();
    }
}