package planningoptimization115657k62.nguyenvanduc.baitap;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;

public class Liquid {
    int N = 5; //so thung chua
    int M = 20; //so loai chat long
    int Q = 6; //so luong conflict
    int[] c = {60, 70, 90, 80, 100};
    int[] v = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10,
                20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
    ArrayList<Integer>[] Ls = null;





    IntVar[][] X = null; //X[i][j] = 1 chat i chua trong phong j
    int[] oneN = {1, 1, 1, 1, 1};
    int[] oneM = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    int[] one3 = {1, 1, 1};

    public static void main(String[] args) {
        Liquid app = new Liquid();
        app.solve();

    }

    public void solve() {
        //cho nay du lieu nho thi lam thu cong cho nhanh
        //neu du lieu lon thi lam vong lap
        Ls = new ArrayList[Q];
        Ls[0] = new ArrayList<Integer>();
        Ls[0].add(0); Ls[0].add(1);

        Ls[1] = new ArrayList<Integer>();
        Ls[1].add(7); Ls[1].add(8);

        Ls[2] = new ArrayList<Integer>();
        Ls[2].add(12); Ls[2].add(17);

        Ls[3] = new ArrayList<Integer>();
        Ls[3].add(8); Ls[3].add(9);

        Ls[4] = new ArrayList<Integer>();
        Ls[4].add(1); Ls[4].add(2); Ls[4].add(9);

        Ls[5] = new ArrayList<Integer>();
        Ls[5].add(0); Ls[5].add(9); Ls[5].add(12);


        Model model = new Model();
        X = model.intVarMatrix(M, N, 0, 1);

        //1, 1 chat chi chua o 1 thung
        for (int i = 0; i < M; i++) {
            model.scalar(X[i], oneN, "=", 1).post();
        }

        //3. <= tong suc chua
        for (int j = 0; j < N; j++) {

            IntVar[] tmp = new IntVar[M];
            for (int i = 0; i < M; i++) {
                tmp[i] = X[i][j];
            }
            model.scalar(tmp, v, "<=", c[j]).post();
        }

        //2 conflict
        for (int q = 0; q < Q; q++) {
            if (Ls[q].size() == 2) {
                int i1 = Ls[q].get(0);
                int i2 = Ls[q].get(1);
                for (int j = 0; j < N; j++) {
                    model.arithm(X[i1][j], "+", X[i2][j], "<=", 1).post();
                }
            } else {
                int i1 = Ls[q].get(0);
                int i2 = Ls[q].get(1);
                int i3 = Ls[q].get(2);
                for (int j = 0; j < N; j++) {
                    IntVar[] tmp = new IntVar[3];
                    tmp[0] = X[i1][j];
                    tmp[1] = X[i2][j];
                    tmp[2] = X[i3][j];
                    model.scalar(tmp, one3, "<=", 2).post();
                }
            }
        }

        model.getSolver().solve();

        for (int j = 0; j < N; j++) {
            System.out.println();
            System.out.print("thung " + j + ": ");
            for (int i = 0; i < M; i++) {
                if (X[i][j].getValue() == 1) {
                    System.out.print(i + ", ");
                }
            }
        }


    }
}
