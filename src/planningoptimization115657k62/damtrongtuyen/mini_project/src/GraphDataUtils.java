package planningoptimization115657k62.damtrongtuyen.mini_project.src;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GraphDataUtils {
    int N;
    int K;
    int bound = 100; // right bound
    int[][] c;
    Random R = new Random();

    public GraphDataUtils(int N)
    {
        this.N = N;
//        this.bound = bound;
    }
    public void genData(String savePath, int N, int M, int K){
        File file = new File(savePath);
        // N: nof nodes, max M: nof edges
        try{
            PrintWriter out = new PrintWriter(savePath);
            out.print(N+" ");
            out.println(K);
            // add K here
            c = new int[N][N];
            for(int i = 0; i < N; i++)
                for(int j = 0; j < N; j++)
                    c[i][j] = 0;
            for(int k = 1; k <= M; k++){
                int i = R.nextInt(N);
                int j = R.nextInt(N);
                if(i != j){
                    int w = R.nextInt(bound) + 1;
                    c[i][j] = w; c[j][i] = w;
                }
            }
            for(int i = 0; i < N; i++){
                for(int j =0; j < N; j++)
                    out.print(c[i][j] + " ");
                out.println();
            }
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void readData(String readPath){
        try{
            Scanner in = new Scanner(new File(readPath));
            N = in.nextInt();
            K = in.nextInt();
            this.c = new int[N][N];
            for(int i = 0; i < N; i++){
                for(int j = 0; j < N; j++)
                    c[i][j] = in.nextInt();
            }
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String GRAPH_FOLDER = "C:\\Users\\damtr\\IdeaProjects\\Test\\src\\graph_data\\";
//        Random R = new Random(); // for random gen data
        GraphDataUtils g = new GraphDataUtils(10);

        g.readData(GRAPH_FOLDER + "graph_14_39.txt");
        for (int i = 0; i < g.N; i++) {
            for (int j = 0; j < g.N; j++) {
                System.out.print(g.c[i][j] + "\t");
            }
            System.out.println();
        }


//        String fileName = "graph_10_50.txt"; //sinh data 5 đỉnh
//        g.genData(GRAPH_FOLDER+fileName, 10, 50, 3);


        // gendata here
//        int[] listN = {14, 22, 30, 40, 80, 120, 150, 200, 250, 300, 400, 500, 800, 1000};
//        double[] listScale = {0.2, 0.4, 0.7};
//
//        int n, e;
//        double m;
//        int K;
//        for (int i = 0; i < listN.length; i++) {
//            for (int j = 0; j < listScale.length; j++) {
//                n = listN[i];
//                m = listScale[j];
//
//                double x = n*n*m;
//                e = (int) x;
//                GraphDataUtils g = new GraphDataUtils(n);
//                String save_path = GRAPH_FOLDER+"graph_"+n+"_"+e+".txt";
//                int lowK = (int) (0.15 * n);
//                int upK = (int) (0.25 * n);
//                K = R.nextInt(upK-lowK) + lowK;
//
//                g.genData(save_path, n, e, K);
//
//            }
//        }

    }
}
