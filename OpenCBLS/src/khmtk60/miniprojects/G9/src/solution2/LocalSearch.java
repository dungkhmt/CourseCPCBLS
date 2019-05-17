package solution2;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class AssignMove{
    int i;
    int v;
    public AssignMove(int i, int v) {
        this.i = i;
        this.v = v;
    }
}

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

public class LocalSearch {

    private int[] X;

    private int n;
    private int m;

    private double[] p,w;
    private int[] t, r;
    private ArrayList<ArrayList<Integer>> binIndices;

    private double[] W;//capacity 1
    private double[] LW;//minimum capacity
    private double[] P;//capacity 2
    private int[] T;//max type of item
    private int[] R;//max class of item
    private HashMap<Integer,Bin> list;

    public void readDataJson(String fn) {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fn)) {
            Object obj = jsonParser.parse(reader);
            JSONObject jo = (JSONObject) obj;
            JSONArray bins = (JSONArray)jo.get("bins");
            JSONArray items = (JSONArray)jo.get("items");

            n = items.size();
            m = bins.size();

            w = new double[n];
            p = new double[n];
            t = new int[n];
            r = new int[n];
            binIndices = new ArrayList<ArrayList<Integer>>();

            for(int i = 0; i < items.size(); i++) {
                JSONObject item = (JSONObject)items.get(i);
                w[i] = (double)item.get("w");
                p[i] = (double)item.get("p");
                t[i] = (int)(long)item.get("t");
                r[i] = (int)(long)item.get("r");
                JSONArray binArray = (JSONArray)item.get("binIndices");
                ArrayList<Integer> idx = new ArrayList<Integer>();
                for(int j = 0; j < binArray.size(); j++) {
                    idx.add((int)(long)(binArray.get(j)));
                }
                binIndices.add(idx);
            }

            W = new double[m];
            LW = new double[m];
            P = new double[m];
            T = new int[m];
            R = new int[m];
            for(int j = 0; j < bins.size(); j++) {
                JSONObject bin = (JSONObject)bins.get(j);
                W[j] = (double)bin.get("capacity");
                LW[j] = (double)bin.get("minLoad");
                P[j] = (double)bin.get("p");
                T[j] = (int)(long)bin.get("t");
                R[j] = (int)(long)bin.get("r");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Bin addItem(HashMap<Integer , Bin> hashMap, Integer key, double w, double p, int t, int r) {
        Bin item = hashMap.get(key);
        if(item == null) {
            item = new Bin(w,p);
        } else {
            item.chageW(w);
            item.chageP(p);
        }
        item.getT().add(t);
        item.getR().add(r);
        return item;
    }

    public Bin removeItem(HashMap<Integer , Bin> hashMap, Integer key, double w, double p, int t, int r) {
        Bin item = hashMap.get(key);
        if(item == null) {
            item = new Bin(w,p);
        } else {
            item.chageW(w);
            item.chageP(p);
        }
        item.getT().remove(item.getT().indexOf(t));
        item.getR().remove(item.getR().indexOf(r));
        return item;
    }

    public int violations(){
        int totalViol = 0;
        list = new HashMap<>();
        for (int i =0 ; i < n; i++){
            list.put(X[i], addItem(list, X[i], w[i], p[i], t[i], r[i]));
        }

        for (Integer i : list.keySet()){
            if (list.get(i).getW() < LW[i]) totalViol++;
            if (list.get(i).getW() > W[i]) totalViol++;
            if (list.get(i).getP() > P[i]) totalViol++;
            if (list.get(i).getSizeT() > T[i]) totalViol++;
            if (list.get(i).getSizeR() > R[i]) totalViol++;
        }
        return totalViol;
    }

    public void initX(){
        X= new int[n];
        HashMap<Integer, Bin> map = new HashMap<>();
        for(int i = 0; i < n; i++) {
            for(int bin : binIndices.get(i)) {
                X[i]=bin;
                map.put(bin, addItem(map, bin, w[i],p[i],t[i],r[i]));
                Bin b = map.get(bin);
                if (b.getW() <= W[bin] && b.getP() <=P[bin]
                        && b.getSizeT()<=T[bin] && b.getSizeR() <= R[bin]) {
                    break;
                }else {
                    X[i]=-1;
                    map.put(bin, removeItem(map, bin, -w[i], -p[i], t[i], r[i]));
                }
            }
        }
        
      


    }

    public int getAssignDelta(HashMap<Integer,Bin> map,int idx, int bin){
        int violIdxT=0;
        int violBinT=0;
        int violIdxS=0;
        int violBinS=0;
        if (map.get(bin) == null){
            Bin b = map.get(X[idx]);
            if(b.getW() <LW[X[idx]]) violIdxT++;
            if(b.getW() > W[X[idx]]) violIdxT++;
            if(b.getP() > P[X[idx]]) violIdxT++;
            if(b.getSizeT() > T[X[idx]]) violIdxT++;
            if(b.getSizeR() > R[X[idx]]) violIdxT++;

            if(b.getW()-w[idx] <LW[X[idx]]) violIdxS++;
            if(b.getW()-w[idx] > W[X[idx]]) violIdxS++;
            if(b.getP()-p[idx] > P[X[idx]]) violIdxS++;
            b.getT().remove(b.getT().indexOf(t[idx]));
            if(b.getSizeT() > T[X[idx]]) violIdxS++;
            b.getR().remove(b.getR().indexOf(r[idx]));
            if(b.getSizeR() > R[X[idx]]) violIdxS++;
            return violIdxS+1-violBinT-violIdxT;
        }else {
            Bin b1 = map.get(X[idx]);
            Bin b2 = map.get(bin);
            if(b1.getW() <LW[X[idx]]) violIdxT++;
            if(b1.getW() > W[X[idx]]) violIdxT++;
            if(b1.getP() > P[X[idx]]) violIdxT++;
            if(b1.getSizeT() > T[X[idx]]) violIdxT++;
            if(b1.getSizeR() > R[X[idx]]) violIdxT++;

            if(b2.getW() <LW[bin]) violBinT++;
            if(b2.getW() > W[bin]) violBinT++;
            if(b2.getP() > P[bin]) violBinT++;
            if(b2.getSizeT() > T[bin]) violBinT++;
            if(b2.getSizeR() > R[bin]) violBinT++;

            if(b1.getW()-w[idx] <LW[X[idx]]) violIdxS++;
            if(b1.getW()-w[idx] > W[X[idx]]) violIdxS++;
            if(b1.getP()-p[idx] > P[X[idx]]) violIdxS++;
            b1.getT().remove(b1.getT().indexOf(t[idx]));
            if(b1.getSizeT() > T[X[idx]]) violIdxS++;
            b1.getR().remove(b1.getR().indexOf(r[idx]));
            if(b1.getSizeR() > R[X[idx]]) violIdxS++;

            if(b2.getW()+w[idx] <LW[bin]) violBinS++;
            if(b2.getW()+w[idx] > W[bin]) violBinS++;
            if(b2.getP()+p[idx] > P[bin]) violBinS++;
            b2.getT().add(t[idx]);
            if(b2.getSizeT() > T[bin]) violBinS++;
            b2.getR().add(r[idx]);
            if(b2.getSizeR() > R[bin]) violBinS++;
            return violIdxS+violBinS-violBinT-violIdxT;
        }
    }

    public void HillClimbing(int maxIter) {
        ArrayList<AssignMove> cand = new ArrayList<AssignMove>();
        int it = 0;
        Random R = new Random();

        while(it < maxIter) {
            int currentViol = violations();
            if (currentViol <= 0) break;

            cand.clear();
            int minDelta = Integer.MAX_VALUE;
            for(int i = 0 ; i < n ; i++) {
                for( int j = 0; j < binIndices.get(i).size(); j++) {
                    HashMap<Integer, Bin> map = new HashMap<>();
                    for(Integer k : list.keySet()){
                        Bin b = new Bin(list.get(k).getW(), list.get(k).getP());
                        List<Integer> l =new ArrayList<>();
                        l.addAll(list.get(k).getT());
                        b.setT(l);

                        List<Integer> g =new ArrayList<>();
                        g.addAll(list.get(k).getR());
                        b.setR(g);
                        map.put(k,b);
                    }
                    int d = getAssignDelta(map,i, binIndices.get(i).get(j));
                    if(d < minDelta ) {
                        cand.clear();
                        cand.add(new AssignMove(i, binIndices.get(i).get(j)));
                        minDelta = d;
                    }
                    else if(d == minDelta) {
                        cand.add(new AssignMove(i, binIndices.get(i).get(j)));
                    }
                }
            }
            int idx= R.nextInt(cand.size());
            AssignMove m  = cand.get(idx);
            X[m.i]=m.v;

            System.out.println("Step " +it + " violations = " + (currentViol+minDelta));
            it++;
        }
    }

    public void printOutPut(){
        for (int i=0; i<n;i++){
            System.out.println(X[i]+" : ");
        }
//        JSONObject json = new JSONObject();
//        for (int i=0; i<n;i++){
//            json.put(i,X[i]);
//        }
//        try (FileWriter file = new FileWriter("src/result/result.json")) {
//
//            file.write(json.toJSONString());
//            file.flush();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void solve(){
        readDataJson("data/MinMaxTypeMultiKnapsackInput-1000.json");
        initX();
        HillClimbing(20);
        checkResult();
//        printOutPut();
    }

    public List<Integer> addList(HashMap<Integer , List<Integer>> hashMap, Integer key, Integer value) {
        List<Integer> item = hashMap.get(key);
        if(item == null) {
            item = new ArrayList<>();
            item.add(value);
        } else {
            item.add(value);
        }
        return item;
    }

    public void checkResult(){
        HashMap<Integer, List<Integer>> hashMap = new HashMap<>();
        for(int i =0; i<n; i++){
            hashMap.put(X[i], addList(hashMap, X[i], i));
        }

        List<Integer> listBin = new ArrayList<>();
        for (Integer i : hashMap.keySet()){
            double sumw=0;
            double sump=0;
            Set<Integer> sett =new HashSet<>();
            Set<Integer> setr = new HashSet<>();
            for (int j=0; j < hashMap.get(i).size();j++){
                sumw+=w[hashMap.get(i).get(j)];
                sump+=p[hashMap.get(i).get(j)];
                sett.add(t[hashMap.get(i).get(j)]);
                setr.add(r[hashMap.get(i).get(j)]);
            }
            if((sumw>W[i]) || (sump>P[i]) ||(sumw<LW[i]) || (sett.size()>T[i]) || (setr.size()>R[i]))
                listBin.add(i);
        }

        int count =0;
        for (int i : listBin){

            count+=hashMap.get(i).size();
        }
        System.out.println(count);
    }


    public static void main(String[] args) {
        LocalSearch m = new LocalSearch();
        m.solve();
    }

}
