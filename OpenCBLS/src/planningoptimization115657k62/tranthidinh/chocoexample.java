import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.io.File;
import java.util.Scanner;

public class chocoexample {
    int N = 9;// number of courses: 0,1,2,...,8
    int P = 4;// number of semesters: 0,1,2,3
    int[] credits = {3, 2, 2, 1, 3, 3, 1, 2, 2};
    int alpha = 2;
    int beta = 4;
    int lamda = 3;
    int gamma = 7;
    int[] A = {0,0,1,2,3,4,3};
    int[] B = {1,2,3,5,6,7,8};// prerequisites

    int[] oneN = {1,1,1,1,1,1,1,1,1};
    int[] oneP = {1,1,1,1};


    public static void main(String[] args) {
        chocoexample app = new chocoexample();
        app.solve();

    }


    public void solve() {
//        input("src/data-bacp/bacp.in30");
        Model model = new Model();
        IntVar[][] x = model.intVarMatrix(P, N, 0, 1);
        //x[p, i] = 1 mon i duoc phan vao ki p

        for (int i = 0; i < A.length; i++) {
            for (int p = 0; p < P; p++) {
                for (int q = p; q < P; q++) {
                    model.ifThen(model.arithm(x[q][A[i]], "=", 1), model.arithm(x[p][B[i]], "=", 0));
                }
            }
        }

        for (int i = 0; i < N; i++) {
            IntVar[] temp = new IntVar[P];
            for (int j = 0; j < P; j++) {
                temp[j] = x[j][i];
            }
            model.scalar(temp, oneP, "=", 1).post();
            //tong cot = 1 : 1 mon hoc trong dung 1  ki
        }

        for (int p = 0; p < P; p++) {
            model.scalar(x[p], oneN, ">=", alpha).post();
            model.scalar(x[p], oneN, "<=", beta).post();

            model.scalar(x[p], credits, ">=", lamda).post();
            model.scalar(x[p], credits, "<=", gamma).post();
        }

        model.getSolver().solve();
        for (int p = 0; p <P; p++) {
            for (int n = 0; n < N; n++) {
                if (x[p][n].getValue() == 1) {
                    System.out.println(p + " - " + n);
                }
            }
        }


    }


}