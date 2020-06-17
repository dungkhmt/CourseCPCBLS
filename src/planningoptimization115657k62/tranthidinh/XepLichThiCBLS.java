import com.sun.org.apache.xpath.internal.functions.FuncSum;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.functions.sum.SumFun;
import localsearch.functions.sum.SumVarConstraints;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;
public class XepLichThiCBLS {
    int N = 9; //số lớp thi
    int M = 3; // số phòng thi

    int[] c = {40, 50, 60}; //số chỗ ngồi của mỗi phòng
    int[] d = {30, 40, 20, 55, 60, 45, 35, 30, 58}; //số số sinh viên đăng kí dự thi ở từng lớp

    //cặp các môn học không thể xếp cùng kíp + ngày
    int[] I = {1, 1, 4, 3};
    int[] J = {2, 3, 7, 8};
//    int[] oneP = {1, 1, 1};
    LocalSearchManager mgr = null;
    VarIntLS[]X = null;
    VarIntLS[][] Y = null;
    ConstraintSystem S = null;
    public  void solve()
    {
        mgr = new LocalSearchManager();
        X = new VarIntLS[N];
        for (int i = 0;i < N; i++)
        {
            X[i] = new VarIntLS(mgr,0, N-1);
        }
        Y = new VarIntLS[N][M];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                Y[i][j] = new VarIntLS(mgr, 0, 1);
            }
        }

        S = new ConstraintSystem(mgr);

        //các môn conflict thì không xếp cùng kíp
        for (int i = 0; i < I.length; i++)
        {
            S.post(new NotEqual(X[I[i]], X[J[i]]));
        }

        //chỉ xếp vào phòng có sức chứa phù hợp
        //nếu xếp lớp i cho phòng j thì d[i] <= c[j]
        for (int i = 0; i < N; i ++)
        {
            for (int j = 0; j < M; j++)
            {
//                S.post(new Implicate(new IsEqual(Y[i][j], 1),
//                        new LessOrEqual(new FuncMult(Y[i][j], d[i]), c[j])));

                S.post(new LessOrEqual(new FuncMult(Y[i][j], d[i]), c[j]));

            }

        }

        //một môn chỉ xếp vào đúng 1 phòng
        for (int i = 0; i < N ; i++)
        {
            S.post(new IsEqual(new Sum(Y[i]), 1));
        }

        //2 môn cùng kíp thì không cùng phòng
        for(int j = 0; j< M ; j ++)
        for (int i1 = 0; i1 < N ; i1++)
        for(int i2 = i1+1; i2 < N; i2++)
        {
                S.post(new Implicate(new IsEqual(X[i1], X[i2]),
                        new LessOrEqual(new FuncPlus(Y[i1][j], Y[i2][j]), 1)));
        }

        mgr.close();
        HillClimbingSearch s = new HillClimbingSearch();
        s.search(S, 1000);
        if (S.violations() == 0) {
            for (int i = 0; i < N; ++i) {
                System.out.print("Lop " + i + ": Kíp" + X[i].getValue() +" ");
                for (int j = 0; j < M; j ++)
                {
                    if(Y[i][j].getValue() == 1)
                    {
                        System.out.print("phòng: " + j );
                    }

                }
                System.out.println();
            }
        }


    }



    public static void main(String[] args) {
        XepLichThiCBLS app = new XepLichThiCBLS();
        app.solve();
    }

}
