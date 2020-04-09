package cbls115676khmt61.ngocbh_20164797.search;

public class AssignMove extends Move {
    public int i;
    public int v;
    public boolean is_legal;

    public AssignMove() {
        this.is_legal = true;
    }
    
    public AssignMove(int i, int v) {
        this.i = i;
        this.v = v;
        this.is_legal = true;
    } 
    public AssignMove(int i, int v,boolean is_moved) {
        this.i = i;
        this.v = v;
        this.is_legal = is_moved;
    } 
}