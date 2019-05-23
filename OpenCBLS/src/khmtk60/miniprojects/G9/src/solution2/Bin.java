package solution2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Bin{
    private double w;
    private double p;
    private List<Integer> t;
    private List<Integer> r;
    public Bin(double w, double p) {
        this.w = w;
        this.p = p;
        t = new ArrayList<>();
        r = new ArrayList<>();
    }

    public List<Integer> getT() {
        return t;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public List<Integer> getR() {
        return r;
    }

    public void setT(List<Integer> t) {
        this.t = t;
    }

    public void setR(List<Integer> r) {
        this.r = r;
    }

    public void chageW(double w){
        this.w +=w;
    }

    public void chageP(double p){
        this.p +=p;
    }

    public int getSizeT(){
        Set<Integer> set = new HashSet<>();
        set.addAll(this.t);
        return set.size();
    }

    public int getSizeR(){
        Set<Integer> set = new HashSet<>();
        set.addAll(this.r);
        return set.size();
    }

}