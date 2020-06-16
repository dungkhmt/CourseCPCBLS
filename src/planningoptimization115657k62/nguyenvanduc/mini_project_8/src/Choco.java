package planningoptimization115657k62.nguyenvanduc.mini_project_8.src;

//mo hinh bang Choco, moi lop thi 1 phong

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Choco {


    int N; //so mon
    int M; // so phong
    int[] d; // d[i] so luong sinh vien dang ki mon i
    int[] c; // c[i] suc chua cua phong i
    int[][] conflict; //conflict[i][j] : mon i va j khong the cung kip
    
    IntVar[] X = null;
    IntVar[] Y = null;
    IntVar Z = null;

    public static void main(String[] args) {
        Choco app = new Choco();
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

    /**
     * 
     */
    public void solve() {
        // check xem co mon nao qua nhieu sinh  vien,  khong the thi o bat ki phong nao
        //

        Model model = new Model();
      
        //X[i]  kip thi cua mon i
        X = new IntVar[N+1];
        for (int i = 1; i <= N; i++) {
        	X[i] = model.intVar("X" + i, 1, N);
        }
        
        IntVar[] Y = new IntVar[N+1];  // Y[i] phong thi cua mon i
        for (int i = 1; i <= N; i++) {
            // luu cac phong co suc chua c[j] >=  d[i] so sinh vien mon i
            ArrayList<Integer> tlist = new ArrayList<>();
            for (int j = 1; j <= M; j++) {
                if (c[j] >= d[i]) {
                    tlist.add(j);
                }
            }
            int[] tarr = new int[tlist.size()];
            for (int v = 0; v < tlist.size();  v++) tarr[v] = tlist.get(v);
            Y[i] = model.intVar("y" + i, tarr);
        }

        IntVar Z = model.intVar("z", 1, N);

        //constrain
        //1. not conflict (i, j) conflict => X[i] != X[j]
        for (int i1 = 1; i1 <= N-1; i1++) {
            for (int i2 = i1+1; i2 <= N; i2++) {
                if (conflict[i1][i2] == 1) {
                    model.arithm(X[i1], "!=", X[i2]).post();
                }
            }
        }

        //2.suc chua >= so sinh vien: da the hien o phan khai bao bien Y[i]

        //3. mon i, j cung kip => khac phong, hoac nguoc lai
        // if X[i] = X[j] => Y[i] != Y[j]
        for (int i1 = 1; i1 <= N-1; i1++) {
            for (int i2 = i1+1; i2 <= N; i2++) {
                model.ifThen(
                    model.arithm(X[i1], "=", X[i2]),
                    model.arithm(Y[i1], "!=", Y[i2])
                );
            }
        }

        //4. Z >= X[i]
        for (int i = 1; i <= N; i++) {
            model.arithm(Z, ">=", X[i]).post();
        }

        model.setObjective(Model.MINIMIZE, Z);
       
        

        Solver solver = model.getSolver();
        

        long start = System.currentTimeMillis();
        while (solver.solve()) {
            //stop when find an optimal solution
            System.out.println("---------------------------");
            long time = (System.currentTimeMillis() - start) / 1000;
            System.out.println("time: " + time + " s");
            int ans = (int) solver.getBestSolutionValue();
            System.out.println("objective value: " + ans);
            System.out.println("Kip: Mon - Phong");
            for (int k = 1; k <= ans; k++) {
            	System.out.print("Kip " + k + ": ");
            	for (int i = 1; i <= N; i++) {
            		if (X[i].getValue() == k) {
            			System.out.print(i + " - " + Y[i].getValue() + ", ");
            		}
            	}
            	System.out.println();
            }
        }
        
        

//        Find All Solutions
        
//        List<Solution> solutions = solver.findAllOptimalSolutions(Z, false);
//        System.out.println("number of optimal solutions: " + solutions.size());
//        for (Solution s : solutions) {
//            System.out.println("---------------------------");
//            System.out.println("optimal objective value: " + s.getIntVal(Z));
//            for (int i = 0; i < N; i++) {
//                System.out.println(i +  ". " +  s.getIntVal(X[i]) + " " + s.getIntVal(Y[i]));
//            }
//        }
        
//        solver.printStatistics();


    }
}
