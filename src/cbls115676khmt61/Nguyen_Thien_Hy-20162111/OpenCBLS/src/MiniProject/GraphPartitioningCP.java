package MiniProject;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class GraphPartitioningCP {
    private int N = 9;
    private int[][] E = {
        {0 , 6 , 3},
        {0 , 8 , 4},
        {1 , 3 , 2},
        {1 , 5 , 2},
        {2 , 4 , 3},
        {2 , 7 , 4},
        {4 , 8 , 5},
        {5 , 6 , 4},
    };
    
    public void solve() {
        int[] c = new int[E.length];
        int C = 0;
        for (int i = 0; i < E.length; i++) {
            c[i] = E[i][2];
            C += c[i];
        }
        
        int K = 3; 
        
        Model model = new Model("graph partitioning");
        IntVar[] e = new IntVar[E.length];
        IntVar[][] x = new IntVar[N][K];

        IntVar obj = model.intVar("obj" , 0 , C);
        for (int k = 0; k < E.length; k++) {
            e[k] = model.intVar("e[" + k + "]", 0 , 1);
        }
        for (int i = 0; i < N; i++) {
            for(int k = 0 ; k < K ; k ++){
                x[i][k] = model.intVar("x[" + i + "][" + k + "]", 0 , 1);
            }
        }
        for (int i = 0; i < E.length; i ++) {
            for(int k = 0 ; k < K ; k ++){
                model.arithm(e[i] , ">=" , x[E[i][0]][k], "-", x[E[i][1]][k]);
                model.arithm(e[i] , ">=" , x[E[i][1]][k], "-", x[E[i][0]][k]);
                model.arithm(x[E[i][1]][k] , "+" , x[E[i][0]][k] , "<=" , e[i]);
            }
        }
        
        int[] one = new int[K];
        for (int i = 0; i < K; i ++) {
            one[i] = 1;
        }
        
        for(int v = 0 ; v < N ; v ++){
            model.scalar(x[v], one, "=" , 1).post();
        }
        
        model.scalar(e , c , "=", obj).post();

        model.setObjective(model.MAXIMIZE, obj);
        model.getSolver().solve();
        model.getSolver().printStatistics();
        
        
        for (int i = 0; i < N; i++) {
            System.out.println("e[" + i + "] = " + e[i].getValue());
        }
        
        for (int i = 0; i < N; i++) {
            for(int j = 0 ; j < K ; j ++){
                System.out.println("x[" + i + "][" + j + "] = " + x[i][j].getValue());
            }
        }
        System.out.println("obj = " + obj.getValue());
    }

    public static void main(String[] args) {
        GraphPartitioningCP app = new GraphPartitioningCP();
        app.solve();
    }

}
