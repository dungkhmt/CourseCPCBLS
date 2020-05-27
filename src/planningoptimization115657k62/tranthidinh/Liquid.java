import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
public class Liquid {
    int N = 20;
    int M = 5;
    int[] c = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
    int[] d = {60, 70, 90, 80, 100};

    int[] I = { 0, 7, 12, 8};
    int[] J = {1, 8, 17, 9};
//    int[] oneN = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};

    public void solve() {
        Model model = new Model("Liquid");


        IntVar[][] X = new IntVar[M][N];  //X[j][i]  chất i đựng trong thùng j

        //miền giá trị của X
        for (int j = 0; j < M; j++)
            for (int i = 0; i < N; i++)
                X[j][i] = model.intVar("X[" + j + "," + i + "]", 0, 1);

        //các chất trong mỗi thùng không vượt quá dung tích của thùng
        for (int i = 0; i < M; i++) {
            model.scalar(X[i], c, "<=", d[i]).post();
        }

        //nếu các chất conflict thì không đựng chung thùng
        for (int j = 0; j <M; j++)
            for(int i = 0; i < I.length; i++)
                {
                    model.ifThen(
                            model.arithm(X[j][I[i]] , "=", 1),
                            model.arithm(X[j][J[i]], "=", 0));
                }
        //1 chất chỉ đựng trong 1 thùng

        for(int i = 0; i< N; i++)
            for(int j1 = 0; j1 < M -1 ; j1 ++ )
                for (int j2= j1 + 1; j2 < M; j2++)
                {
                    model.ifThen(
                            model.arithm(X[j1][i] , "=", 1),
                            model.arithm(X[j2][i], "=", 0));
                }

        //trường hợp 3 chất lỏng không được chứa đồng thời
        for (int j =0; j< M; j++)
        {
            model.ifThen(
                    model.arithm(X[j][2], "+", X[j][9] , "=", 2),
                    model.arithm(X[j][1],  "=", 0)

            );
            model.ifThen(
                    model.arithm(X[j][1], "+", X[j][9] , "=", 2),
                    model.arithm(X[j][2],  "=", 0)

            );
            model.ifThen(
                    model.arithm(X[j][1], "+", X[j][2] , "=", 2),
                    model.arithm(X[j][9],  "=", 0)

            );

            model.ifThen(
                    model.arithm(X[j][9], "+", X[j][12] , "=", 2),
                    model.arithm(X[j][0],  "=", 0)

            );

            model.ifThen(
                    model.arithm(X[j][0], "+", X[j][12] , "=", 2),
                    model.arithm(X[j][9],  "=", 0)

            );

            model.ifThen(
                    model.arithm(X[j][0], "+", X[j][9] , "=", 2),
                    model.arithm(X[j][12],  "=", 0)

            );


        }

        Solver s = model.getSolver();
        int dem = 0;
        s.solve();

//        while (s.solve() || dem > 1)
//        {
            for (int j = 0; j < M; j++) {
                System.out.println(" Thùng  " + j + "có dung tích " +d[j]+ " chứa:");
                for (int i = 0; i < N; i++)
                    if (X[j][i].getValue() == 1) {
                        System.out.println(" Chất :" + i + " thể tích " + c[i]);
                    }
                System.out.println();
            }
//            dem += 1;
//        };


    }

    public static void main(String[] args) {
        Liquid app = new Liquid();
        app.solve();

    }

}
