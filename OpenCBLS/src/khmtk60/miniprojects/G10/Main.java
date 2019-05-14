package khmtk60.miniprojects.G10;

import khmtk60.miniprojects.G10.custom.constraints.basic.LessOrEqualD;
import khmtk60.miniprojects.G10.custom.functions.conditionalsum.ConditionalSumD;
import khmtk60.miniprojects.G10.custom.functions.uniquecount.ConditionalUniqueCount;
import khmtk60.miniprojects.G10.custom.constraints.basic.Implicate;
import khmtk60.miniprojects.G10.custom.constraints.basic.LessOrEqual;
import khmtk60.miniprojects.G10.custom.functions.conditionalsum.ConditionalSum;
import khmtk60.miniprojects.G10.custom.search.AlphaGreedySearch;
import khmtk60.miniprojects.G10.custom.search.BatchHillClimbing;
import khmtk60.miniprojects.G10.localsearch.model.*;

import khmtk60.miniprojects.G10.multiknapsackminmaxtypeconstraints.model.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    LocalSearchManager mgr;
    ConstraintSystem S;

    VarIntLS[] x;
    int n_i, n_b, n_t, n_r;
    int[] mmr, mmt, r, t;
    double[] w, p;
    int[][] D;

    double[] W, LW, P;
    int[] T, R;

    MinMaxTypeMultiKnapsackInput input;
    ArrayList<Integer> binloais;

    ArrayList<IConstraint> constraints;
    IFunction objective;

    void stateModel() {
        constraints = new ArrayList<>();
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);

        // Variables
        x = new VarIntLS[n_i];
        for (int i=0; i<n_i; ++i) {
            x[i] = new VarIntLS(mgr, D[i]);
            x[i].index = i;
//            x[i].setValue(n_b-1);
        }

        //Constraints
        for (int b=0; b<n_b; ++b)  {
            if(binloais.contains(b)) continue;
            ArrayList<Integer> list = new ArrayList<>();
            for(int i=0; i<n_i; ++i) {
                if (x[i].getDomain().contains(b)) {
                    list.add(i);
                }
            }
            int len = list.size();
            VarIntLS[] temp_x = new VarIntLS[len];
            double[] temp_w = new double[len];
            double[] temp_p = new double[len];
            int[] temp_r = new int[len];
            int[] temp_t = new int[len];
            int count = -1;
            for (int k: list){
                count++;
                temp_x[count] = x[k];
                temp_w[count] = w[k];
                temp_p[count] = p[k];
                temp_r[count] = r[k];
                temp_t[count] = t[k];
            }

            ConditionalSumD cs = new ConditionalSumD(temp_x, temp_w, b);
            constraints.add((new Implicate(new LessOrEqualD(0.00001, cs),new LessOrEqualD(LW[b], cs, 0.9))));
            constraints.add(new LessOrEqualD(cs, W[b]));

            cs = new ConditionalSumD(temp_x, temp_p, b);
            constraints.add(new LessOrEqualD(cs, P[b]));

            constraints.add(new LessOrEqual(new ConditionalUniqueCount(temp_x, temp_t, b), T[b]));
            constraints.add(new LessOrEqual(new ConditionalUniqueCount(temp_x, temp_r, b), R[b]));
        }
        objective = new ConditionalSum(x, n_b-1);
        constraints.add(new LessOrEqual(new ConditionalSum(x,n_b-1), 300));
        for (IConstraint constraint: constraints){
            S.post(constraint);
        }
        mgr.close();
    }


    public void search(int maxiters) {

        //initFeasibleSolution();
//        GreedyInit greedinit = new GreedyInit(w,p,r,t,D,R,T,W,P,LW);
//        greedinit.step1();
//        greedinit.printSolution();
//        greedinit.step2();
//        greedinit.printSolution();

//        greedinit.assignRandom();
//        for(int i=0; i<n_i; ++i){
//            x[i].setValuePropagate(greedinit.X[i]);
//        };
//
//        System.out.println("init violation: " + S.violations());
//        //Search
//        int it=0;
//        ArrayList<Integer> L = new ArrayList<>();
//        Random ran = new Random();
//        double alpha = 0.5;
//        while (it < maxiters && S.violations() > 0) {
//            //1. chon 1 item co kha nang nhat
//            HashMap<Integer, ArrayList<Integer>> v2i = new HashMap();
//
//            int sel_i = -1;
//            int sel_v = 0;
//            L.clear();
//            for (int i = 0; i < x.length; i++) {
//                int v = S.violations(x[i]);
//                if (!v2i.containsKey(v)) {
//                    v2i.put(v, new ArrayList<Integer>());
//                }
//                v2i.get(v).add(i);
//            }
//            Set keySet = v2i.keySet();
//            ArrayList list = new ArrayList(keySet);
//            Collections.sort(list, Collections.reverseOrder());
//            int vTypeLen = (int) ceil(alpha * list.size());
//            vTypeLen = vTypeLen == 0 ? 1 : vTypeLen;
//            sel_v = (int) list.get(ran.nextInt(vTypeLen));
//            L = v2i.get(sel_v);
//            sel_i = L.get(ran.nextInt(L.size()));
//
//            //2. chon value co kha nang nhat
//            VarIntLS chosen_var = x[sel_i];
//            ArrayList<Integer> possibleV = new ArrayList<>();
////            int chosen_value=n_b-1;
//            for (int v: chosen_var.getDomain()){
//                int assignDelta = 0;
//                if (v == chosen_var.getOldValue()) continue;
////                boolean valid = true;
//                for (IConstraint constraint: constraints){
//                    assignDelta += constraint.getAssignDelta(chosen_var, v);
////                    if (constraint.getAssignDelta(chosen_var, v) > 0){
////                        valid = false;
////                        break;
////                    }
//                }
////                if(valid){
////                    possibleV.add(v);
////                }
//                if (assignDelta < 0)possibleV.add(v);
//            }
//            if (possibleV.size() > 0){
//                int chosen_value = possibleV.get(ran.nextInt(possibleV.size()));
//                chosen_var.setValuePropagate(chosen_value);
//                System.out.printf("Step %d, chosen item %d move from %d to %d, violation %d, objective %d\n", it, sel_i,chosen_var.getOldValue(), chosen_value, S.violations(), objective.getValue());
//            }
//            ++it;
//            if((it+1) % 1 == 0){
//                clearViolatedVariable();
//            }
//        }


//        loadPreviousSolution();
        System.out.println("init violation " + S.violations());
        long start = System.currentTimeMillis();
//        AlphaGreedySearch greedySearch = new AlphaGreedySearch(0.1);
//        greedySearch.search(S, 10000, 3000, true);
        BatchHillClimbing climb = new BatchHillClimbing();
        climb.search(S, 5, 8000, 25);
        long end = System.currentTimeMillis();

        System.out.printf("Violation = %d in %d s Solution for climb search\n", S.violations(), (end-start)/1000);
        print();
        int[] clearSolution = clearViolatedVariable();
        System.out.println("\nClear solution");
        for (int v: clearSolution) System.out.print(v + " ");
    }

    private void loadPreviousSolution() {
        String csvFile = "src/khmtk60/miniprojects/G10/multiknapsackminmaxtypeconstraints/test.txt";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] arr = line.split(cvsSplitBy);

                for (int i=0; i<arr.length; ++i){
                    x[i].setValuePropagate(Integer.parseInt(arr[i]));
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    MinMaxTypeMultiKnapsackInput loadData(String path){
        input = new MinMaxTypeMultiKnapsackInput();
        input = input.loadFromFile(path);
        return input;

    }

    int[] getMinMaxValue(int[] arr) {
        IntSummaryStatistics stat = Arrays.stream(arr).summaryStatistics();
        int min = stat.getMin();
        int max = stat.getMax();
        return new int[]{min, max};
    }

    private int[] formatbin(int[] binIndices, int fakebin) {
        List<Integer> list =
                Arrays.stream(binIndices).boxed().collect(Collectors.toList());
        list.removeAll(binloais);
        list.add(fakebin);
        return list.stream().mapToInt(i -> i).toArray();
    }

    void extractNumber(String path) {
        loadbinloai();

        MinMaxTypeMultiKnapsackInput input=loadData(path);
        MinMaxTypeMultiKnapsackInputItem[] items = input.getItems();
        MinMaxTypeMultiKnapsackInputBin[] bins = input.getBins();

        n_i = items.length;
        n_b = bins.length + 1;

        r = new int[n_i];
        t = new int[n_i];
        w = new double[n_i];
        p = new double[n_i];
        D = new int[n_i][];

        for (int i=0; i<n_i; ++i) {
            r[i] = items[i].getR();
            t[i] = items[i].getT();
            w[i] = items[i].getW();
            p[i] = items[i].getP();
            D[i] = formatbin(items[i].getBinIndices(), bins.length);
        }
        mmr = getMinMaxValue(r);
        mmt = getMinMaxValue(t);
        n_r = mmr[1] - mmr[0] + 1;
        n_t = mmt[1] - mmt[0] + 1;

        W = new double[n_b];
        LW = new double[n_b];
        P = new double[n_b];
        T = new int[n_b];
        R = new int[n_b];
        for (int i = 0; i < bins.length; i++) {
//            if (binloais.contains(i)) continue;
            W[i] = bins[i].getCapacity();
            LW[i] = bins[i].getMinLoad();
            P[i] = bins[i].getP();
            T[i] = bins[i].getT();
            R[i] = bins[i].getR();
        }
        W[bins.length] = 10000000.0;
        LW[bins.length] = 0.0;
        P[bins.length] = 10000000.0;
        T[bins.length] = n_t;
        R[bins.length] = n_r;

        System.out.println("number of bins: " + bins.length);
        System.out.println("number of items: " + items.length);

        System.out.println("min R "+mmr[0] + "   max R " + mmr[1]);
        System.out.println("min T "+mmt[0] + "   max T " + mmt[1]);

    }


    void solve(String path, int maxIter){
        extractNumber(path);
        stateModel();
        search(maxIter);
//        print();
    }

    void print() {
        for(VarIntLS i: x) {
            System.out.print(i.getValue() + " ");
        }
        System.out.println();
    }


    void loadbinloai() {
        String csvFile = "src/khmtk60/miniprojects/G10/multiknapsackminmaxtypeconstraints/binloai1000.txt";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] arr = line.split(cvsSplitBy);
                binloais = new ArrayList<Integer>();
                for (int i=0; i<arr.length; ++i){
                    binloais.add(Integer.parseInt(arr[i]));
                }
                System.out.println(arr.length);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int[] clearViolatedVariable() {
        int[] X = new int[x.length];
        for (int i=0; i<x.length; ++i) {
            X[i] = x[i].getValue();
        }
        
        double[] loadWeight = new double[n_b-1];
        double[] loadP = new double[n_b-1];
        HashSet<Integer>[] loadType = new HashSet[n_b-1];
        HashSet<Integer>[] loadClass = new HashSet[n_b-1];

        for(int b = 0; b < n_b-1; b++){
            loadWeight[b] = 0;
            loadP[b] = 0;
            loadType[b] = new HashSet<Integer>();
            loadClass[b] = new HashSet<Integer>();
        }
        int nbItemNotScheduled = 0;
        int violations = 0;

        String description = "";
        ArrayList<Integer> blackList = new ArrayList<>();
        for(int i = 0; i < X.length; i++){
            if(X[i] < 0 || X[i] >= n_b-1){
                X[i] = -1;
                nbItemNotScheduled++;
            }else{
                int b = X[i];
                loadWeight[b] += w[i];
                if(loadWeight[b] > W[b]){
                    blackList.add(i);
                    loadWeight[b] -= w[i];
                    continue;
                }
                loadP[b] += p[i];
                if(loadP[b] > P[b]){
                    blackList.add(i);
                    loadP[b] -= p[i];
                    loadWeight[b] -= w[i];
                    continue;
                }
                boolean is_contain = loadType[b].contains(t[i]);
                loadType[b].add(t[i]);
                if(loadType[b].size() > T[b]){
                    blackList.add(i);
                    loadType[b].remove(t[i]);
                    loadP[b] -= p[i];
                    loadWeight[b] -= w[i];
                    continue;
                }
                loadClass[b].add(r[i]);
                if(loadClass[b].size() > R[b]){
                    blackList.add(i);
                    loadClass[b].remove(r[i]);
                    if(!is_contain)
                        loadType[b].remove(t[i]);
                    loadP[b] -= p[i];
                    loadWeight[b] -= w[i];
                    continue;
                }
            }
        }

        for(int b = 0; b < n_b-1; b++){
//            System.out.printf("bin %d, Loadweight %f, LW %f\n", b,loadWeight[b], LW[b]);
            if(loadWeight[b] > 0 && loadWeight[b] < LW[b]){
                for (int i=0; i<n_i; ++i) {
                    if(X[i] == b){
                        blackList.add(i);
                    }
                }
            }
        }

        for(int i: blackList) X[i] = -1;
        System.out.printf("nbItemNotScheduled %d, violateItem %d, scheduledItem %d\n",
                nbItemNotScheduled, blackList.size(), n_i-nbItemNotScheduled-blackList.size());
        return X;
    }

    public static void main(String[] args) {

        String path = "src\\khmtk60\\miniprojects\\G10\\multiknapsackminmaxtypeconstraints\\MinMaxTypeMultiKnapsackInput-1000.json";
        int maxIter = 10000;

        Main mk = new Main();
        mk.solve(path, maxIter);


    }
    class Assign1DMove {
        int i;
        int j;

        Assign1DMove(int _i, int _j) {
            i = _i;
            j = _j;
        }
    }
}
