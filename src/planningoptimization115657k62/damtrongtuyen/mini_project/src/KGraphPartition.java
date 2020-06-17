package planningoptimization115657k62.damtrongtuyen.mini_project.src;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import org.chocosolver.util.objects.graphs.IGraph;

import java.io.File;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class KGraphPartition {
    static{
        System.loadLibrary("jniortools");
    }
    int[][] graph;// = new int[n][n];
    int N;
    int K;
    public void init_graph(int n)
    {
        graph = new int[n][n];
        Random rn = new Random();

        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++) {
                if(i>j)
                {
                    graph[i][j] = rn.nextInt(5);
                    graph[j][i] =  graph[i][j];
                }
                if(i==j)
                    graph[i][j] = 0;
            }
        }

    }
    public void loadData(String filePath)
    {
        try{
            Scanner in = new Scanner(new File(filePath));
            N = in.nextInt();
            K = in.nextInt();
//            System.out.println("load data: K: "+K+",N: "+N);

            graph = new int[N][N];
            for(int i = 0; i < N; i++){
                for(int j = 0; j < N; j++)
                    graph[i][j] = in.nextInt();
            }
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        String GRAPH_FOLDER = "C:\\Users\\damtr\\IdeaProjects\\Test\\src\\graph_data\\";
        String fileName = "graph_10_50.txt"; // graph_14_39 886ms, optimal value: 313, k = 4;=> 20s, 1140

        KGraphPartition kgp = new KGraphPartition();
        kgp.loadData(GRAPH_FOLDER+fileName);

        int k = kgp.K; // number of partition
//        k = 4;
        int n = kgp.N;
//        System.out.println("n: "+n+",k:"+k);
//        System.out.println("K: "+kgp.K+",N: "+kgp.N);
        int alpha = 2; // alternative
        int balance_size = n/k;
        int lower_bound = balance_size - (int)Math.ceil(alpha/2.0);
        int upper_bound = balance_size + (int)Math.ceil(alpha/2.0);

//        kgp.init_graph(n);
        int [][] graph = kgp.graph;
// modeling
        MPSolver solver = new MPSolver("k graph partition", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
        MPVariable[][] e = new MPVariable[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(graph[i][j] > 0)
                {
                    e[i][j] = solver.makeIntVar(0, 1, "e["+i+","+j+"]"); // is edge is cut edge?
                }
            }
        }

        MPVariable[][] x = new MPVariable[n][k];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++) {
                x[i][j] = solver.makeIntVar(0, 1, "x["+i+","+j+"]"); // is n in partition k?
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(graph[i][j] > 0)
                {
                    for (int l = 0; l < k; l++) {
//                        if(l!=i && l!= j)
//                        {
                            // e(u,v) >= abs(x(u,k) - x(v, k))
                            MPConstraint c = solver.makeConstraint(0, MPSolver.infinity());
                            c.setCoefficient(e[i][j], 1);
                            c.setCoefficient(x[i][l], 1);
                            c.setCoefficient(x[j][l], -1);

                            MPConstraint c1 = solver.makeConstraint(0, MPSolver.infinity());
                            c1.setCoefficient(e[i][j], 1);
                            c1.setCoefficient(x[i][l], -1);
                            c1.setCoefficient(x[j][l], 1);

//                        }
                    }
                }
            }
        }


//        MPConstraint c = solver.makeConstraint() xik + xjk <= 2 - yij for  1+1<=2-0

        for (int i = 0; i < k; i++) {
            MPConstraint cBound = solver.makeConstraint(lower_bound, upper_bound);
            for (int j = 0; j < n; j++) {
                cBound.setCoefficient(x[j][i], 1);
            }
        }

        for (int i = 0; i < n; i++) {
            MPConstraint cOne = solver.makeConstraint(1, 1);
            for (int j = 0; j < k; j++) {
                cOne.setCoefficient(x[i][j], 1);
            }
        }

        MPObjective objective = solver.objective();
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                if(graph[i][j] > 0)
                {
                    objective.setCoefficient(e[i][j], graph[i][j]);
                }
            }
        }
        objective.setMinimization();

        long start = new Date().getTime();
        MPSolver.ResultStatus rs = solver.solve();
        long end = new Date().getTime();

        System.out.println("total time: "+ (end-start) + "ms");
        if(rs != MPSolver.ResultStatus.OPTIMAL)
        {
            System.out.println("can't not find optimal solution");
        }
        else
        {
            System.out.println("FIND A SOLUTION");
            System.out.println("graph: ");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    System.out.print(graph[i][j] + "\t");
                }
                System.out.println();
            }
            System.out.println("===========================");
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < k; j++) {
//                    System.out.print(x[i][j].solutionValue() + "\t");
//                }
//                System.out.println();
//            }
            for (int i = 0; i < k; i++) {
                System.out.println("member of cluster "+i+": ");
                for (int j = 0; j < n; j++) {
                    if(x[j][i].solutionValue()==1)
                    {
                        System.out.print(j+"\t");
                    }
                }
                System.out.println();
            }
            System.out.println("===========================");
            System.out.println("optimal value: " + objective.value());
        }

    }

}
