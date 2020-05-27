package planningoptimization115657k62.damtrongtuyen;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;
public class Liquid2 {
    public static void main(String[] args) {
        int capacity[] = {60,70,90,80,100};;

        int V[] = {20,15,10,20,20,25,30,15,10,10,
                20,25,20,10,30,40,25,35,10,10};

        int c1[][] = {{0, 1}, {7, 8}, {12, 17}, {8, 9}};
        int c2[][] = {{1, 2, 9}, {1, 9, 2}, {2, 9, 1},
                {0, 9, 12}, {0, 12, 9}, {9, 12, 0}};
        int conflict1[][] = new int[20][20];
        for (int i = 0; i < c1.length; i++) {
            int a0 = c1[i][0];
            int a1 = c1[i][1];
            conflict1[a0][a1] = 1;
            conflict1[a1][a0] = 1;
        }

        int oneC[] = new int[20];
        for (int i = 0; i < 20; i++) {
            oneC[i] = 1;
        }
        int oneV[] = new int[5];
        for (int i = 0; i < 5; i++) {
            oneV[i] = 1;
        }

        Model model = new Model("Liquid");
        IntVar x[][] = new IntVar[5][20];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 20; j++) {
                x[i][j] = model.intVar("x["+i+"]["+j+"]",0, 1);
            }
        }

        for (int i = 0; i < 20; i++) { // 1 chat long phan vao 1 thung
            IntVar[] y = new IntVar[5];
            for (int j = 0; j < 5; j++) {
                y[j] = x[j][i];
            }
            model.scalar(y, oneV, "=", 1).post();
        }

        for (int i = 0; i < 20; i++) {// truong hop 2 chat long
            for (int j = 0; j < 20; j++) {
                if(conflict1[i][j] == 1)
                {
                    for (int k = 0; k < 5; k++) {
                        model.ifThen(model.arithm(x[k][i], "=", 1), model.arithm(x[k][j], "!=", 1));
//
                    }
                }
            }
        }

        for (int i = 0; i < c2.length; i++) {// truong hop 3 chat long
            int a0, a1, a2;
            a0 = c2[i][0];
            a1 = c2[i][1];
            a2 = c2[i][2];
            for (int j = 0; j < 5; j++) {
                model.ifThen(model.and(model.arithm(x[j][a0], "=", 1), model.arithm(x[j][a1], "=", 1)), model.arithm(x[j][a2], "!=", 1));

            }

        }
        for (int i = 0; i < 5; i++) {//tong chat long nho hon dung tich
            model.scalar(x[i], V, "<=", capacity[i]).post();
        }
        model.getSolver().solve();

        for (int i = 0; i < 5; i++) {
            System.out.println("thung "+i+": ");
            for (int j = 0; j < 20; j++) {
                if(x[i][j].getValue()==1)
                {
                    System.out.print(j+"\t");
                }
            }
            System.out.println();
        }
    }
}
