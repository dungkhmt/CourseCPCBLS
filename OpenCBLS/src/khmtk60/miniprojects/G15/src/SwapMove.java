package src;
import java.util.*;

public class SwapMove {
    int b_x;
    int b_y;
    ArrayList<Integer> binXNew;
    ArrayList<Integer> binYNew;

    public SwapMove(int b_x, int b_y) {
        this.b_y = b_y;
        this.b_x = b_x;
    }

    public SwapMove(int b_x, int b_y, ArrayList<Integer> binXNew,
                ArrayList<Integer> binYNew) {
        this.b_y = b_y;
        this.b_x = b_x;
        this.binXNew = new ArrayList<Integer>(binXNew);
        this.binYNew = new ArrayList<Integer>(binYNew);
    }
}