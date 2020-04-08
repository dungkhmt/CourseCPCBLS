import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Sudoku {
    public static void main(String[] args) {
        Sudoku app = new Sudoku();
        app.solve(9);
    }
    void solve(int n) {
        if (n%3 != 0) {
            System.out.println("n phai chia het cho 3!");
            return;
        }

        Model model = new Model();
        IntVar[][] x = model.intVarMatrix(n, n, 1, 9);

        //in row are different
        for (int i = 0; i < n; i++) {
            model.allDifferent(x[i]).post();
        }

        //in column are different
        IntVar[][] xT = new IntVar[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                xT[i][j] = x[j][i];
            }
        }
        for (int i = 0; i < n; i++){
            model.allDifferent(xT[i]).post();
        }

        IntVar[] tmp = new IntVar[9];
        for (int i = 0; i < n; i+=3) {
            for (int j = 0; j < n; j+=3) {
                tmp[0] = x[i][j];
                tmp[1] = x[i][j+1];
                tmp[2] = x[i][j+2];
                tmp[3] = x[i+1][j];
                tmp[4] = x[i+1][j+1];
                tmp[5] = x[i+1][j+2];
                tmp[6] = x[i+2][j];
                tmp[7] = x[i+2][j+1];
                tmp[8] = x[i+2][j+2];
                model.allDifferent(tmp).post();
            }
        }


        model.getSolver().solve();
        for (int i = 0; i < n; i++) {
            System.out.println();
            for (int j = 0; j < n; j++) {
                System.out.print(x[i][j].getValue() + " ");
            }
        }

    }
}
