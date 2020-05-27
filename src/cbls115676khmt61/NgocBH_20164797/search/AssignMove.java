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

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;
        hash = prime * hash + (new Integer(i)).hashCode();
        hash = prime * hash + (new Integer(v)).hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssignMove other = (AssignMove) obj;
        if (i != other.i || v != other.v)
            return false;
        return true;
    }
}