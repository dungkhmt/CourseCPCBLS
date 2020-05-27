package example;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.*;

public class BinPacking {
	
	//data
    int N = 6;
    int[] w = {1, 3, 2, 3, 1, 2};
    int[] l = {4, 1, 2, 1, 4, 3};
    int W = 4, L = 6;

    public void solve() {
        Model model = new Model();
        IntVar[] o = model.intVarArray(N, 0, 1);//co xoay hay khong
        IntVar[] x = model.intVarArray(N, 0, W-1); //hoanh do goc tren trai
        IntVar[] y = model.intVarArray(N, 0, L-1); // tung do goc tren trai
        //IntVar[] z = model.intVarArray(N, 0, 6); // goi hang nam o lop thu may?

        //goi hang nam trong trong thung xe:
//        for (int i = 0; i < N; i++) {
//            model.scalar(new IntVar[] {x[i], o[i]}, new int[] {1, l[i] - w[i]}, "<=", W - w[i]).post();
//            model.scalar(new IntVar[] {y[i], o[i]}, new int[] {1, -l[i] + w[i]}, "<=", L - l[i]).post();
//        }
        
        for(int i = 0; i <N; i++)
        {
            Constraint c1 = model.arithm(model.intOffsetView(x[i], w[i]),"<=",W);
            Constraint c2 = model.arithm(model.intOffsetView(y[i], l[i]),"<=",L);
            model.ifThen(model.arithm(o[i], "=", 0), model.and(c1,c2));

            Constraint c3 = model.arithm(model.intOffsetView(x[i], l[i]),"<=",W);
            Constraint c4 = model.arithm(model.intOffsetView(y[i], w[i]),"<=",L);
            model.ifThen(model.arithm(o[i], "=", 1), model.and(c3,c4));

        }
        
        //Hai goi hang khong duoc chen len nhau:
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++){
            	Constraint c1 = model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 0));
                Constraint c2 = model.arithm(model.intOffsetView(x[i], w[i]),"<=",x[j]);
                Constraint c3 = model.arithm(model.intOffsetView(x[j], w[j]),"<=",x[i]);
                Constraint c4 = model.arithm(model.intOffsetView(y[i], l[i]),"<=",y[j]);
                Constraint c5 = model.arithm(model.intOffsetView(y[j], l[j]),"<=",y[i]);
                model.ifThen(c1, model.or(c2,c3,c4,c5));
//              for (int i = 0; i < N; i++) {
//              model.scalar(new IntVar[] {x[i], o[i]}, new int[] {1, l[i] - w[i]}, "<=", W - w[i]).post();
//              model.scalar(new IntVar[] {y[i], o[i]}, new int[] {1, -l[i] + w[i]}, "<=", L - l[i]).post();
//          }
                c1 = model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 1));
                c2 = model.arithm(model.intOffsetView(x[i], w[i]),"<=",x[j]);
                c3 = model.arithm(model.intOffsetView(x[j], l[j]),"<=",x[i]);
                c4 = model.arithm(model.intOffsetView(y[i], l[i]),"<=",y[j]);
                c5 = model.arithm(model.intOffsetView(y[j], w[j]),"<=",y[i]);
                model.ifThen(c1, model.or(c2,c3,c4,c5));

                c1 = model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 0));
                c2 = model.arithm(model.intOffsetView(x[i], l[i]),"<=",x[j]);
                c3 = model.arithm(model.intOffsetView(x[j], w[j]),"<=",x[i]);
                c4 = model.arithm(model.intOffsetView(y[i], w[i]),"<=",y[j]);
                c5 = model.arithm(model.intOffsetView(y[j], l[j]),"<=",y[i]);
                model.ifThen(c1, model.or(c2,c3,c4,c5));

                c1 = model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 1));
                c2 = model.arithm(model.intOffsetView(x[i], l[i]),"<=",x[j]);
                c3 = model.arithm(model.intOffsetView(x[j], l[j]),"<=",x[i]);
                c4 = model.arithm(model.intOffsetView(y[i], w[i]),"<=",y[j]);
                c5 = model.arithm(model.intOffsetView(y[j], w[j]),"<=",y[i]);
                model.ifThen(c1, model.or(c2,c3,c4,c5));
            }
        }
        
        //goi nao giao truoc thi o ngoai hon goi giao sau: (toa do y lon hon)
        for (int i1 = 0; i1<N; i1++) {
        	for(int i2 = 0; i2<N; i2++)if(i2 > i1) {
        		Constraint c1 = model.arithm(y[i1], ">=", y[i2]);
        		Constraint c2 = model.arithm(model.intOffsetView(x[i1], w[i1]), "<=", x[i2]);
        		Constraint c3 = model.arithm(model.intOffsetView(x[i2], w[i2]), "<=", x[i1]);
        		model.or(c1, c2, c3).post();
        	}
        }

        model.getSolver().solve();
        System.out.println("Toa do tren trai va trang thai xoay:");
        for (int i = 0; i < N; i++) {
        	System.out.print("Goi hang thu " + i + ": ");
        	System.out.print("(" + x[i].getValue() + ", " + y[i].getValue() + ")");
        	if (o[i].getValue() == 1) {
        		System.out.println(" - Co xoay!");
        	}else {
        		System.out.println(" - Khong xoay!");
        	}
        }

    }

    public static void main(String[] args) {
        BinPacking app = new BinPacking();
        app.solve();
    }
}
