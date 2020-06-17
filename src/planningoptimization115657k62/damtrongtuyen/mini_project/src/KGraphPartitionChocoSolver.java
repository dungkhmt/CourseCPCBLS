package planningoptimization115657k62.damtrongtuyen.mini_project.src;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import java.io.File;
import java.util.Date;
import java.util.Scanner;

public class KGraphPartitionChocoSolver {
    int n;
    int k;
    int alpha = 1;
    int[][] c;
    int maxWeight = -1000000;
    Model model;

    public KGraphPartitionChocoSolver(int n) {
        this.n = n;
    }

    public void readData(String readPath){
        try{
            Scanner in = new Scanner(new File(readPath));
            n = in.nextInt();
            k = in.nextInt();
            c = new int[n][n];

            for(int i = 0; i < n; i++){
                for(int j = 0; j < n; j++) {
                    c[i][j] = in.nextInt();
                    maxWeight = Math.max(maxWeight, c[i][j]);
                }
            }

            in.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void solve() {
        model = new Model("Graph partition");

        IntVar[] x = new IntVar[n]; // x[i] = j: vertex i belongs to cluster k
        IntVar[] nVetexOfCluster = new IntVar[k]; // nVetexOfCluster[i]: the number of vertices of cluster i
        IntVar[] w = new IntVar[n *(n -1)/2];
        IntVar max = model.intVar("max", n / k -1, n); // the largest number of vertices in a cluster
        IntVar min = model.intVar("min", 0, n / k +1); // the smallest number of vertices in a cluster
        IntVar objectiveFunction = model.intVar("Min total weight", 0, maxWeight*n*(n-1)/2);

        // Initialize variables
        for (int i = 0; i< n; i++) {
            x[i] = model.intVar("x[" + i + "]", 0, k -1);
        }

        for (int i=0; i<w.length; i++) {
            w[i] = model.intVar("w[" + i + "]", 0, maxWeight);
        }

        // Constraints
        for (int i = 0; i<k; i++) {
            nVetexOfCluster[i] = model.intVar("nVetexOfCluster[" + i + "]", 0, n);
            model.count(i, x, nVetexOfCluster[i]).post(); // tính tổng số đỉnh của 1 cụm
        }

        // Constraint 1
        model.max(max, nVetexOfCluster).post(); // max lưu giá trị lớn nhất của mảng nVetexOfCluster
        model.min(min, nVetexOfCluster).post();
        model.arithm(max, "-", min, "<=", alpha).post();

        // Constraint 2
        int index = 0;
        for (int i = 0; i < n-1; i++) {
            for (int j = i+1; j < n; j++) { // nếu x[i]!=x[j] thì w[idx] = c[i][j] else = 0
                model.ifThenElse(model.arithm(x[i], "!=", x[j]),
                            model.arithm(w[index], "=", c[i][j]),
                            model.arithm(w[index], "=", 0));
                index++;
            }
        }

        model.sum(w, "=", objectiveFunction).post(); // objectiveFunction = sum(w)
        model.setObjective(Model.MINIMIZE, objectiveFunction);

        // Solves
        Solver solver = model.getSolver();

        while(solver.solve()){
            System.out.println("\nSolution: ");
            for (int i = 0; i < k; i++) {
                System.out.println("member of cluster "+i+": ");
                for (int j = 0; j < n; j++) {
                    if(x[j].getValue()==i)
                    {
                        System.out.print(j+"\t");
                    }
                }
                System.out.println();
            }

            System.out.println(objectiveFunction);
        }
    }

    public static void main(String[] args) {
        String GRAPH_FOLDER = "C:\\Users\\damtr\\IdeaProjects\\Test\\src\\graph_data\\";
        KGraphPartitionChocoSolver g = new KGraphPartitionChocoSolver(10);

        g.readData(GRAPH_FOLDER + "graph_14_137.txt");
        long start = new Date().getTime();
        g.solve();
        long end = new Date().getTime();
        System.out.println("total time: "+ (end-start) + "ms");
    }
}