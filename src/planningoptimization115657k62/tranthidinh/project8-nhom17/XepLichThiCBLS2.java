import localsearch.constraints.basic.*;
import localsearch.functions.basic.ConstraintViolations;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.max_min.Max;
import localsearch.functions.sum.Sum;
import localsearch.model.*;
import localsearch.search.TabuSearch;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class XepLichThiCBLS2  {


    public int N, M, numOfConflict, c[], d[], I[], J[]; // cac du lieu muon lay

    public void readFile() throws FileNotFoundException {
        String rootpath = "D:\\java\\tranthidinh\\src\\data\\";
        String filename = "25class-5room-4conflict.txt";
        System.out.println("File name: " + filename);

        File file = new File(rootpath + filename);
        Scanner scanner = new Scanner(file);

        N = scanner.nextInt();
        M = scanner.nextInt();
        numOfConflict =scanner.nextInt();

        c = new int[M];
        d = new int[N];
        I = new int[numOfConflict];
        J = new int[numOfConflict];

        for(int i = 0; i<N; i++) {
            d[i] = scanner.nextInt();
        }

        for(int j = 0; j<M; j++) {
            c[j] = scanner.nextInt();
        }

        for(int i = 0; i<numOfConflict; i++) {
            I[i] = scanner.nextInt();
        }

        for(int i = 0; i<numOfConflict; i++) {
            J[i] = scanner.nextInt();
        }

    }

    long startTime;
    long endTime;

    LocalSearchManager mgr = null;
    VarIntLS[]X = null; //biến kíp
    VarIntLS[][] Y = null; //xếp phòng j cho lớp thi i
    IConstraint S;
    IFunction obj;
    IFunction F;


    public void stateModel() {
        startTime = System.currentTimeMillis();

        mgr = new LocalSearchManager();

        X = new VarIntLS[N];
        for (int i = 0; i < N; i++) {
            X[i] = new VarIntLS(mgr, 0, N - 1);
        }
        Y = new VarIntLS[N][M];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                Y[i][j] = new VarIntLS(mgr, 0, 1);
            }
        }

        ArrayList<IConstraint> list = new ArrayList<IConstraint>();

        //các môn conflict thì không xếp cùng kíp
        for (int i = 0; i < I.length; i++) {
            list.add(new NotEqual(X[I[i]], X[J[i]]));
        }

        //chỉ xếp vào phòng có sức chứa phù hợp
        //nếu xếp lớp i cho phòng j thì d[i] <= c[j]
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                list.add(new LessOrEqual(new FuncMult(Y[i][j], d[i]), c[j]));
            }

        }

        //một môn chỉ xếp vào đúng 1 phòng
        for (int i = 0; i < N; i++) {
            list.add(new IsEqual(new Sum(Y[i]), 1));
        }

        //2 môn cùng kíp thì không cùng phòng
        for (int j = 0; j < M; j++)
            for (int i1 = 0; i1 < N; i1++)
                for (int i2 = i1 + 1; i2 < N; i2++) {
                    list.add(new Implicate(new IsEqual(X[i1], X[i2]),
                            new LessOrEqual(new FuncPlus(Y[i1][j], Y[i2][j]), 1)));
                }

        IConstraint[] arr = new IConstraint[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        S = new AND(arr);

        obj = new Max(X);


        F = new FuncPlus(new FuncMult(new ConstraintViolations(S), 1000),
                new FuncMult(obj, 1)); //hàm mục tiêu


        mgr.close();

        TabuSearch ts = new TabuSearch();
        ts.searchMaintainConstraintsMinimize(F, S, 20, 1000, 10000, 200);


        System.out.println("Số kíp tối thiểu: " + obj.getValue());
        for (int i = 0; i < N; i++) {
                System.out.print(" Lớp thi " + i + ": ");
                System.out.print(" Kíp thi: " + X[i].getValue());
                for (int j = 0; j < M; j++)
                    if (Y[i][j].getValue() == 1) {
                        System.out.print(" Phòng :" + j);
                    }
                System.out.println();
            }


        endTime = System.currentTimeMillis();
        System.out.println("Run time: " + (endTime - startTime) / 1000.0 + "s");

    }


    public static void main(String[] args) {
        XepLichThiCBLS2 run = new XepLichThiCBLS2();
        try {
            run.readFile();
            run.stateModel();

//            run.printSolution();
            System.out.println("Done!");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
