package planningoptimization115657k62.nguyenvanduc;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.io.File;
import java.util.Scanner;

public class BACP {
    int N ; //so mon hoc
    int P ; // so ki
    int alpha;
    int beta ;
    int lamda ;
    int gamma ;
    int[] credits;
    int[] A ;
    int[] B ;
    int[] oneN;
    int[] oneP;


    public static void main(String[] args) {
        BACP app = new BACP();
        app.solve();

    }


    public void solve() {
        input("data/BACP/bacp.in30");
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

    private void input(String file_name) {
        try {
            File f = new File(file_name);
            Scanner scanner = new Scanner(f);

            N = scanner.nextInt();
            P = scanner.nextInt();
            lamda = scanner.nextInt();
            gamma = scanner.nextInt();
            alpha = scanner.nextInt();
            beta = scanner.nextInt();

            credits = new int[N];
            for (int i = 0; i < N; i++) {
                credits[i] = scanner.nextInt();
            }

            int tmp = scanner.nextInt();
            A = new int[tmp];
            B = new int[tmp];
            for (int i = 0; i < tmp; i++) {
                A[i] = scanner.nextInt() - 1;
                B[i] = scanner.nextInt() - 1;
            }
            oneN =new int[N];
            for(int i = 0; i < N; i++){
                oneN[i] = 1;
            }
            oneP = new int[P];
            for (int i = 0; i < P; i++) {
                oneP[i] = 1;
            }


        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
