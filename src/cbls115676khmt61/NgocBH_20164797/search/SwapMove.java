package cbls115676khmt61.ngocbh_20164797.search;

public class SwapMove extends Move {
    public int u;
    public int v;
    public boolean is_legal;

    public SwapMove() {
        this.is_legal = true;
    }
    public SwapMove(int u, int v) {
        this.u = u;
        this.v = v;
        this.is_legal = true;
    } 

    public SwapMove(int u, int v, boolean is_legal) {
        this.u = u;
        this.v = v;
        this.is_legal = is_legal;
    } 
}