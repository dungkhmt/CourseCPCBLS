package multiknapsackminmaxtypeconstraints;

public class Pair implements Comparable<Pair> {
    public final int index;
    public final double value;

    public Pair(int index, double value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public int compareTo(Pair other) {
        //multiplied to -1 as the author need descending sort order
        return Double.valueOf(this.value).compareTo(other.value);
    }

}

