package planningoptimization115657k62.nguyenvanduc.mini_project_8.src;

//1 mon 1 phong
// SAT google ortools

import com.google.ortools.sat.*;


import java.io.File;
import java.util.Scanner;

public class SAT_ortools {
    static {
        System.loadLibrary("jniortools");
    }
    int N; //so mon
    int M; // so phong
    int[] d; // d[i] so luong sinh vien dang ki mon i
    int[] c; // c[i] suc chua cua phong i
    int[][] conflict; //conflict[i][j] : mon i va j khong the cung kip

    public static void main(String[] args) {
        SAT_ortools app = new SAT_ortools();
        app.input("src/planningoptimization115657k62/nguyenvanduc/mini_project_8/"
        		+ "data/10_7_15.txt");
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
                conflict[t2][t1] = 1;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void solve() {
        CpModel model = new CpModel();
        IntVar[] X = new IntVar[N+1]; //kip thi cua mon i
        IntVar[] Y = new IntVar[N+1]; //phong thi cua mon i

        for (int i = 1; i <= N; i++) {
            X[i] = model.newIntVar(1, N, "X" + i);
            Y[i] = model.newIntVar(1, M, "Y" + i);
        }

        IntVar Z = model.newIntVar(1, N, "Z");

        //constrains
        //1. not conflict (i, j) conflict => X[i] != X[j]
        for (int i = 1; i <= N-1; i++) {
            for (int j = i+1; j <= N; j++) {
                if (conflict[i][j] == 1) {
                    model.addDifferent(X[i], X[j]);
                }
            }
        }

        //2. suc chua < so sinh vien dang ki => khong chon phong nay
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= M; j++) {
                if (c[j] < d[i]) {
                    model.addDifferent(Y[i], j);
                }
            }
        }

        //3. cung kip thi khac phong
        // if X[i] = X[j] => Y[i] != Y[j]
        for (int i = 1; i <= N-1; i++) {
            for (int j = i+1; j <= N; j++) {
                //declare and implement b == (X[i] == X[j])
                IntVar b = model.newBoolVar("b");
                model.addEquality(X[i], X[j]).onlyEnforceIf(b);
                model.addDifferent(X[i], X[j]).onlyEnforceIf(b.not());

                //if b => Y[i] != Y[j]
                model.addDifferent(Y[i], Y[j]).onlyEnforceIf(b);

            }
        }

        //4. Z >= X[i]
        for (int i = 1; i <= N; i++) {
            model.addGreaterOrEqual(Z, X[i]);
        }

        model.minimize(Z);
        
//        System.out.println(model.modelStats());

        CpSolver solver = new CpSolver();
        long start = System.currentTimeMillis();
        
        CpSolverStatus status = solver.solve(model);
        System.out.println("solve status: " + status);
        
        long time = (System.currentTimeMillis() - start) / 1000;
        System.out.println("time: " + time + " s");
        
        if (status == CpSolverStatus.OPTIMAL) {
        	
            int ans = (int) solver.objectiveValue();
            System.out.println("objective value: " + ans);
            System.out.println("Kip: Mon - Phong");
            for (int k = 1; k <= ans; k++) {
            	System.out.print("Kip " + k + ": ");
            	for (int i = 1; i <= N; i++) {
            		if (solver.value(X[i]) == k) {
            			System.out.print(i + " - " + solver.value(Y[i]) + ", ");
            		}
            	}
            	System.out.println();
            }
        }
        
        
        
        
        
        //find all solutions
//        VarArraySolutionPrinter cb = new VarArraySolutionPrinter(X, Y, Z);
//        solver.searchAllSolutions(model, cb);
//        System.out.println(cb.getSolutionCount());
        

    }
    
    
    //call back for find all solution
//    static class VarArraySolutionPrinter extends CpSolverSolutionCallback {
//		public VarArraySolutionPrinter(IntVar[] X, IntVar[] Y, IntVar Z) {
//		  this.X = X;
//		  this.Y = Y;
//		  this.Z = Z;
//		}
//		
//		@Override
//		public void onSolutionCallback() {
//			System.out.printf("Solution #%d: time = %.02f s%n", solutionCount, wallTime());
//		    System.out.println("optimal ojective value: " + objectiveValue());
//		    for (int i = 0; i < X.length; i++) {
//		        System.out.println(i +  ". " +  value(X[i])+ " " + value(Y[i]));
//		    }
//		    solutionCount++;
//		}
//		
//		public int getSolutionCount() {
//			return solutionCount;
//		}
//		
//		private int solutionCount;
//		private final IntVar[] X;
//		private final IntVar[] Y;
//		private final IntVar Z;
//    }
}
