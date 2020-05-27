//public class Container {
//}
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.variables.IntVar;


public class Container {
    Model model;
    IntVar[] x;
    IntVar[] y;
    IntVar[] o;
    int N = 6;
    int W = 4;
    int H = 6;
    int[] w = {1,3,2,3,1,2};
    int[] h = {4,1,2,1,4,3};

    public void buildModel(){
        model = new Model("Container");
        x = new IntVar[N];
        y = new IntVar[N];
        o = new IntVar[N];


        //miền giá trị của các biến
        for (int i = 0; i <N; i++)
        {
            x[i] = model.intVar("x[" + i + "]", 0 ,W);
            y[i] = model.intVar("y[" + i + "]", 0 , H);
            o[i] = model.intVar("o[" + i + "]", 0 ,1);
        }


        for(int i = 0; i <N; i++)
        {
            Constraint c1 = model.arithm(model.intOffsetView(x[i], w[i]),"<=",W);
            Constraint c2 = model.arithm(model.intOffsetView(y[i], h[i]),"<=",H);
            model.ifThen(model.arithm(o[i], "=", 0), model.and(c1,c2));

            Constraint c3 = model.arithm(model.intOffsetView(x[i], h[i]),"<=",W);
            Constraint c4 = model.arithm(model.intOffsetView(y[i], w[i]),"<=",H);
            model.ifThen(model.arithm(o[i], "=", 1), model.and(c3,c4));

        }

        for (int i = 0; i <N; i++)
        {
            for (int j = i +1; j<N; j++)
            {
                Constraint c1 = model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 0));
                Constraint c2 = model.arithm(model.intOffsetView(x[i], w[i]),"<=",x[j]);
                Constraint c3 = model.arithm(model.intOffsetView(x[j], w[j]),"<=",x[i]);
                Constraint c4 = model.arithm(model.intOffsetView(y[i], h[i]),"<=",y[j]);
                Constraint c5 = model.arithm(model.intOffsetView(y[j], h[j]),"<=",y[i]);
                model.ifThen(c1, model.or(c2,c3,c4,c5));

                c1 = model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 1));
                c2 = model.arithm(model.intOffsetView(x[i], w[i]),"<=",x[j]);
                c3 = model.arithm(model.intOffsetView(x[j], w[j]),"<=",x[i]);
                c4 = model.arithm(model.intOffsetView(y[i], h[i]),"<=",y[j]);
                c5 = model.arithm(model.intOffsetView(y[j], h[j]),"<=",y[i]);
                model.ifThen(c1, model.or(c2,c3,c4,c5));

                c1 = model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 0));
                c2 = model.arithm(model.intOffsetView(x[i], w[i]),"<=",x[j]);
                c3 = model.arithm(model.intOffsetView(x[j], w[j]),"<=",x[i]);
                c4 = model.arithm(model.intOffsetView(y[i], h[i]),"<=",y[j]);
                c5 = model.arithm(model.intOffsetView(y[j], h[j]),"<=",y[i]);
                model.ifThen(c1, model.or(c2,c3,c4,c5));

                c1 = model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 1));
                c2 = model.arithm(model.intOffsetView(x[i], w[i]),"<=",x[j]);
                c3 = model.arithm(model.intOffsetView(x[j], w[j]),"<=",x[i]);
                c4 = model.arithm(model.intOffsetView(y[i], h[i]),"<=",y[j]);
                c5 = model.arithm(model.intOffsetView(y[j], h[j]),"<=",y[i]);
                model.ifThen(c1, model.or(c2,c3,c4,c5));
            }
        }

        //điều kiện thoả mãn giao hàng có thứ tự

        for(int i1 = 0; i1< N -1; i1++ )
            for (int i2 = i1 +1; i2 < N; i2++)
            {
                Constraint c1 =  model.arithm(y[i1] , "<=", y[i2] );
                Constraint c2 =  model.arithm(x[i1] , "<=", x[i2] );

                model.or(c1, c2).post();

            }


    }
    public  void  search(){
        Solver s = model.getSolver();
        s.solve();
            for (int i = 0; i < N; i++) {
                System.out.println("Kiện hàng " + i +" : " + x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
        }

    }



    public static void  main(String[] args){
        Container app = new Container();
        app.buildModel();
        app.search();
    }

}
