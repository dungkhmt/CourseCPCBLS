package planningoptimization115657k62.damtrongtuyen;

import org.chocosolver.solver.IModel;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BCACP {
    public static void main(String[] args) {
        int N = 9;
        int P = 4;
        int[] credits = {3, 2, 2, 1, 3, 3, 1, 2, 2};
        int alpha = 2;
        int beta = 4;
        int lamda = 3;
        int gamma = 7;
        int[] I = {0,0,1,2,3,4,3};
        int[] J = {1,2,3,5,6,7,8};
        int[] oneN = {1,1,1,1,1,1,1,1,1};
        int[] oneP = {1,1,1,1};
        File file = new File("F:\\Learning documentation\\20192\\planing optimization");
        try {
            Scanner scanner = new Scanner(file);

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
            I = new int[tmp];
            J = new int[tmp];
            for (int i = 0; i < tmp; i++) {
                I[i] = scanner.nextInt() - 1;
                J[i] = scanner.nextInt() - 1;
            }
            oneN =new int[N];
            for(int i = 0; i < N; i++){
                oneN[i] = 1;
            }
            oneP = new int[P];
            for (int i = 0; i < P; i++) {
                oneP[i] = 1;
            }


        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Model model = new Model("BCAP");
        IntVar[][] x = model.intVarMatrix(P, N, 0, 1);
        for (int i = 0; i < P; i++) {
            model.scalar(x[i], credits, ">=", lamda).post(); // tá»•ng sá»‘ tÃ­n chá»‰
            model.scalar(x[i], credits, "<=", gamma).post();

            model.scalar(x[i], oneN, ">=", alpha).post(); // tá»•ng sá»‘ mÃ´n há»�c
            model.scalar(x[i], oneN, "<=", beta).post();
        }
        for (int i = 0; i < N; i++) { // má»—i mÃ´n chá»‰ há»�c trong 1 kÃ¬
            IntVar[] y = new IntVar[P];
            for (int j = 0; j < P; j++) {
                y[j] = x[j][i];
            }
            model.scalar(y, oneP, "=", 1).post();
        }
        for (int i = 0; i < I.length; i++) {
            int a = I[i]; int b = J[i]; // conflicts pair
            for (int j = 0; j < P; j++) {
                for (int k = 0; k <= j; k++) {
                    model.ifThen(model.arithm(x[j][a], "=", 1), model.arithm(x[k][b], "=", 0));

                }
            }
        }
        model.getSolver().findAllSolutions();
        for (int i = 0; i < P; i++) {
            System.out.println("semester "+ i+": ");
            for (int j = 0; j < N; j++) {
                if(x[i][j].getValue()==1)
                {
                    System.out.print("course "+j+" credits "+credits[j]+", ");
                }
            }
            System.out.println();
        }
    }
}
