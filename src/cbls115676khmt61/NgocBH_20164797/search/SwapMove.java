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

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;
        hash = prime * hash + (new Integer(u)).hashCode();
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
        SwapMove other = (SwapMove) obj;
        if (u != other.u || v != other.v)
            return false;
        return true;
    }
}