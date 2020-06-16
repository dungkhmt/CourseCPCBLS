import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
public class XepLichThiChoco {


    int N = 9; //số lớp thi
    int M = 3; // số phòng thi

    int[] c = {40, 50, 60}; //số chỗ ngồi của mỗi phòng
    int[] d = {30, 40, 20, 55, 60, 45, 35, 30, 58}; //số số sinh viên đăng kí dự thi ở từng lớp

    //cặp các môn học không thể xếp cùng kíp + ngày
    int[] I = {1, 1, 4, 3};
    int[] J = {2, 3, 7, 8};
    int[] oneP = {1, 1, 1};


    public void solve() {
        Model model = new Model("XepLichThi");


        IntVar[] X = new IntVar[N];  //X[i]  kíp thi của lớp i

        IntVar[][] Y = new IntVar[N][M]; //Y[j][i] = 1 tức là xếp lớp i vào phòng j

        //miền giá trị của X
        for(int i = 0; i < N; i++)
            X[i] = model.intVar("X[" + i + "]", 0, N -1);


        //miền giá trị của Y
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
                Y[i][j] = model.intVar("X[" + i + "," + j + "]", 0, 1);



        //chỉ xếp vào phòng có sức chứa phù hợp
        for (int i = 0; i < N; i++) {
            model.scalar(Y[i], c, ">=", d[i]).post();
        }

        //2 môn conflict thì không cùng kíp
        for (int i = 0; i < I.length; i++)
        {
             model.arithm(X[I[i]], "!=", X[J[i]]).post();
        }

        //2 môn cùng kíp thì không cùng phòng
        for(int j = 0; j < M; j ++)
        {
            for(int i1 = 0; i1< N -1 ; i1++)
                for(int i2 = i1 + 1; i2 < N; i2 ++ )
                {
                    model.ifThen(
                            model.arithm(X[i1],  "=", X[i2]),
                            model.arithm(Y[i1][j], "+", Y[i2][j] , "<", 2)
                    );
                }
        }


        //một môn xếp 1 phòng duy nhất
        for (int i = 0; i < N; i++) {
            model.scalar(Y[i], oneP, "=", 1).post();
        }

        IntVar z = model.intVar(0, N -1);
        for (int i = 0; i <N ; i++)
        {
            model.arithm(z ,">=", X[i]).post();
        }
        

        model.setObjective(Model.MINIMIZE, z);
        Solver s = model.getSolver();

        while (s.solve())
        {
            System.out.println("Số kíp tối thiểu: " + z.getValue());
            for (int i = 0; i < N; i++) {
                System.out.print(" Lớp thi " + i + ": ");
                System.out.print(" Kíp thi: " + X[i].getValue());
                for (int j = 0; j < M; j++)
                    if (Y[i][j].getValue() == 1) {
                        System.out.print(" Phòng :" + j);
                    }
                System.out.println();
            }
        };




   }


    public static void main(String[] args) {
        XepLichThiChoco app = new XepLichThiChoco();
        app.solve();

    }



}
