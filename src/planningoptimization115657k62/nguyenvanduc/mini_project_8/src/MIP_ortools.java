package planningoptimization115657k62.nguyenvanduc.mini_project_8.src;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.io.File;
import java.util.Scanner;

public class MIP_ortools {
    static {
        System.loadLibrary("jniortools");
    }


    int N; //so mon
    int M; // so phong
    int[] d; // d[i] so luong sinh vien dang ki mon i
    int[] c; // c[i] suc chua cua phong i
    int[][] conflict; //conflict[i][j] : mon i va j khong the cung kip


    MPVariable[][] X = null;
    MPVariable[][] Y = null;
    MPSolver solver = null;
    int BIGINT = 10;


    public static void main(String[] args) {
        MIP_ortools app = new MIP_ortools();
        app.input("src/planningoptimization115657k62/nguyenvanduc/mini_project_8/"
        		+ "data/10_6_4.txt");
        app.solve();
    }

    private void input(String file) {
        try {
            File f = new File(file);
            Scanner scanner = new Scanner(f);

            N = scanner.nextInt();
            M = scanner.nextInt();

            d = new int[N+1];
            for (int i = 1; i <= N; i++) {
                d[i] = scanner.nextInt();
            }

            c = new int[M+1];
            for (int i = 1; i <= M; i++) {
                c[i] = scanner.nextInt();
            }

            int Q = scanner.nextInt();
            conflict = new int[N+1][N+1];
            for (int i = 1; i <= N; i++) {
                for (int j = 1; j <= N; j++) {
                    conflict[i][j] = 0;
                }
            }
            for (int i = 0; i < Q; i++) {
                int t1 = scanner.nextInt();
                int t2 = scanner.nextInt();
                conflict[t1][t2] = 1;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public  void  solve() {
        solver = new MPSolver("abc",
                MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
        X = new MPVariable[N+1][M+1];
        Y = new MPVariable[N+1][N+1];

        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= M; j++) {
                if (c[j] < d[i]) {  //suc chua phong j < so luong sv dki mon i
                    X[i][j] = solver.makeIntVar(0, 0, "X[" + i + ", " + j +" ]");
                } else  {
                    X[i][j] = solver.makeIntVar(0, 1, "X[" + i + ", " + j +" ]");
                }
            }
        }

        for (int i1 = 1; i1 <= N-1; i1++) {
            for (int i2 = i1 + 1; i2 <= N; i2++) {
                if (conflict[i1][i2] == 1) {
                    //System.out.println(i1 + " - " + i2);
                    Y[i1][i2] = solver.makeIntVar(0, 0, "Y[" + i1 + ", " + i2 +" ]");
                } else {
                    Y[i1][i2] = solver.makeIntVar(0, 1, "Y[" + i1 + ", " + i2 +" ]");
                }
            }
        }

        //constraints
        //1. 1 mon 1 phong
        for (int i = 1; i <= N; i++) {
            MPConstraint c = solver.makeConstraint(1, 1);
            for (int j = 1; j <= M; j++) {
                c.setCoefficient(X[i][j], 1);
            }
        }


        //2. cung phong khac kip
        for (int i1 = 1; i1 <= N-1; i1++) {
            for (int i2 = i1+1; i2 <= N; i2++) {
                for (int j = 1; j <= M; j++) {
                    MPConstraint c = solver.makeConstraint(0, 2);
                    c.setCoefficient(Y[i1][i2], 1);
                    c.setCoefficient(X[i1][j], 1);
                    c.setCoefficient(X[i2][j], 1);
                }
            }
        }

        //3. i1 i2 cung kip, i1 i3 cung kip => i1 i3 cung kip
        for (int i1 = 1; i1 <= N-3; i1++) {
            for (int i2 = i1 + 1; i2 <= N-1; i2++) {
                for (int i3 = i2 + 1; i3 <= N; i3++) {
                    MPVariable b = solver.makeIntVar(0, 1, "b" + i1 + i2 + i3);

                    MPConstraint fc1 = solver.makeConstraint(3,BIGINT+1);
                    fc1.setCoefficient(Y[i1][i2], 1);
                    fc1.setCoefficient(Y[i1][i3], 1);
                    fc1.setCoefficient(Y[i2][i3], 1);
                    fc1.setCoefficient(b, BIGINT);
                }
            }
        }


        //OBJ
        MPObjective obj =  solver.objective();
        for (int i1 = 1; i1 <= N-1; i1++) {
            for (int i2 = i1 + 1; i2 <= N; i2++) {
                obj.setCoefficient(Y[i1][i2], 1);
            }
        }
        obj.setMaximization();
        
        long start = System.currentTimeMillis();
        MPSolver.ResultStatus rs = solver.solve();
        long time = (System.currentTimeMillis() - start) / 1000;
        if (rs != MPSolver.ResultStatus.OPTIMAL) {
            System.out.println("cannot find optimal solution");
        } else {
        	System.out.println("time: " + time +" s");
            print_solutions();
            //printS();
        }


    }
    public void printS() {
        //X
        for (int i = 1; i <= N; i++) {
            System.out.println();
            for (int j = 1; j <= M; j++ ) {
                System.out.print(X[i][j].solutionValue() + " ");
            }
        }

        //Y
        for (int i1 = 1; i1 <= N; i1++) {
            System.out.println();
            for (int i2 = 1; i2 <= N; i2++) {
                if (i2 > i1) {
                    System.out.print(Y[i1][i2].solutionValue() + " ");
                } else {
                    System.out.print(0.0 + " ");
                }
            }
        }
    }

    public void print_solutions() {
    	System.out.println("kip: mon - phong");
        boolean[] mark = new boolean[N+1];
        for (int i = 1; i <= N; i++) mark[i] = false;

        int kip = 0;
        for (int i = 1; i <= N; i++) {
            if (!mark[i]) {
            	kip++;
                System.out.println();
                System.out.print("kip " + kip + ": ");
                mark[i] = true;
                System.out.print(i + " - ");
                //in phong thi mon i
                for (int j = 1; j <= M; j++) {
                    if (X[i][j].solutionValue() == 1) {
                        System.out.print(j +", ");
                        break;
                    }
                }

                for (int i2 = i+1; i2 <= N; i2++) {
                    if (!mark[i2] && Y[i][i2].solutionValue() == 1) {
                        mark[i2] = true;
                        System.out.print(i2 + " - ");
                        //in phong thi mon i2
                        for (int j = 1; j <= M; j++) {
                            if (X[i2][j].solutionValue() == 1) {
                                System.out.print(j +", ");
                                break;
                            }
                        }
                    }
                }
            }

        }
    }


}
