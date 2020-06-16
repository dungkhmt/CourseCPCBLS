package planningoptimization115657k62.damtrongtuyen.mini_project.src;
import java.io.File;
import java.util.Scanner;

public class ReadGraph {
    int N;
    int K;
    // alpha để mặc định là 3.
    int[][] c;
    public ReadGraph(int N)
    {
        this.N = N;
    }

    public void readData(String readPath){
        try{
            Scanner in = new Scanner(new File(readPath));
            this.N = in.nextInt();
            this.K = in.nextInt();
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
        GraphDataUtils g = new GraphDataUtils(10);

        g.readData(GRAPH_FOLDER + "graph_14_39.txt");
        for (int i = 0; i < g.N; i++) {
            for (int j = 0; j < g.N; j++) {
                System.out.print(g.c[i][j] + "\t");
            }
            System.out.println();
        }
    }
}

