package khmtk60.miniprojects.G5.Solution1.solution.source;


import com.google.gson.Gson;
import khmtk60.miniprojects.G5.Solution1.solution.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.G5.Solution1.solution.model.MinMaxTypeMultiKnapsackInputBin;
import khmtk60.miniprojects.G5.Solution1.solution.model.MinMaxTypeMultiKnapsackInputItem;
import khmtk60.miniprojects.G5.Solution1.solution.model.MinMaxTypeMultiKnapsackSolution;
import khmtk60.miniprojects.G5.Solution1.localsearch.model.LocalSearchManager;
import khmtk60.miniprojects.G5.Solution1.localsearch.constraints.basic.*;
import khmtk60.miniprojects.G5.Solution1.localsearch.functions.occurrence.Occurrence;
import khmtk60.miniprojects.G5.Solution1.localsearch.model.ConstraintSystem;
import khmtk60.miniprojects.G5.Solution1.localsearch.model.VarIntLS;
import khmtk60.miniprojects.G5.Solution1.localsearch.selectors.MinMaxSelector;

import java.io.FileReader;
import java.util.*;


public class MiniProject1 {

    int N;
    int M;
    LocalSearchManager mgr;
    ConstraintSystem S;
    VarIntLS[] x;
    int[] solutionCong;
    int[] solutionCongConvert;

    // Data sample
    double[] w;
    double[] p;
    int[] t;
    public static int[] r;
    int[][] D;

    double[] W;
    double[] canWInBin;
    static ArrayList<Integer>[] rInBin;
    double[] LW;
    double[] P;
    int[] T;
    public static int[] R;
    ArrayList<Integer> mapBinOldNews;
    MinMaxTypeMultiKnapsackInput input;
    MinMaxTypeMultiKnapsackInputBin[] bins;
    MinMaxTypeMultiKnapsackInputItem[] items;

    int mT;
    int mR;

    void initData() {
        System.gc();
        String fn = "solution/MinMaxTypeMultiKnapsackInput-1000.json";
        input = new MinMaxTypeMultiKnapsackInput().loadFromFile(fn);
        bins = input.getBins();
        items = input.getItems();

        ClearData clearData = new ClearData();
        clearData.init(fn);
        mapBinOldNews = clearData.listLWThoa();
        MinMaxTypeMultiKnapsackInputBin[] tmpBins = new MinMaxTypeMultiKnapsackInputBin[mapBinOldNews.size()];
        for (int i = 0; i < mapBinOldNews.size(); i++) {
            tmpBins[i] = bins[mapBinOldNews.get(i)];
        }
        bins = tmpBins;
        bins = Arrays.copyOf(bins, bins.length + 1);
        bins[bins.length - 1] = new MinMaxTypeMultiKnapsackInputBin();
        bins[bins.length - 1].setCapacity(Double.MAX_VALUE);
        bins[bins.length - 1].setMinLoad(0);
        bins[bins.length - 1].setP(Double.MAX_VALUE);
        bins[bins.length - 1].setR(Integer.MAX_VALUE);
        bins[bins.length - 1].setT(Integer.MAX_VALUE);

        // convert solution cong
        solutionCongConvert = new int[items.length];
        for (int i = 0; i < solutionCongConvert.length; i++) {
            if (solutionCong[i] == -1)
                solutionCongConvert[i] = bins.length - 1;
            else
                solutionCongConvert[i] = mapBinOldNews.indexOf(solutionCong[i]);
        }


        for (int i = 0; i < items.length; i++) {
            if (items[i].getT() > mT)
                mT = items[i].getT();
            if (items[i].getR() > mR)
                mR = items[i].getR();
        }
        mR++;
        mT++;

        w = new double[items.length];
        p = new double[items.length];
        t = new int[items.length];
        r = new int[items.length];
        D = new int[items.length][];
        for (int i = 0; i < items.length; i++) {
            w[i] = items[i].getW();
            p[i] = items[i].getP();
            t[i] = items[i].getT();
            r[i] = items[i].getR();
            D[i] = items[i].getBinIndices();
            ArrayList<Integer> tmpD = new ArrayList<Integer>();
            for (int j = 0; j < D[i].length; j++) {
                if (mapBinOldNews.contains(D[i][j]))
                    tmpD.add(mapBinOldNews.indexOf(D[i][j]));
            }
            tmpD.add(bins.length - 1);
            D[i] = tmpD.stream().filter(Objects::nonNull).mapToInt(Integer::intValue).toArray();
        }

        W = new double[bins.length];
        LW = new double[bins.length];
        P = new double[bins.length];
        T = new int[bins.length];
        R = new int[bins.length];
        canWInBin = new double[bins.length];
        rInBin = new ArrayList[bins.length];
        for (int i = 0; i < bins.length; i++) {
            canWInBin[i] = bins[i].getCapacity();
            rInBin[i] = new ArrayList<>();
            W[i] = bins[i].getCapacity();
            LW[i] = bins[i].getMinLoad();
            P[i] = bins[i].getP();
            T[i] = bins[i].getT();
            R[i] = bins[i].getR();
        }


        N = items.length;
        M = bins.length;
    }

    void readInit(String fn) {
        try {
            Gson gson = new Gson();
            MinMaxTypeMultiKnapsackSolution S = gson.fromJson(new FileReader(fn), MinMaxTypeMultiKnapsackSolution.class);
            this.solutionCong = S.getSolution();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean checkR(int r, int binIdx) {
        rInBin[binIdx].add(r);
        Set<Integer> tmp = new HashSet<Integer>(rInBin[binIdx]);
        if (tmp.size() > R[binIdx]) {
            rInBin[binIdx].remove(new Integer(r));
            return false;
        } else {
            rInBin[binIdx].remove(new Integer(r));
            return true;
        }
    }

    public int countRInBin(int idx) {
        return new HashSet<Integer>(rInBin[idx]).size();
    }

    void initConstraint() {
        System.out.println("M: " + M + "\nN: " + N);
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);
        x = new VarIntLS[N];
        Random ran = new Random();
        for (int i = 0; i < N; i++) {
            x[i] = new VarIntLS(mgr, D[i]);
            x[i].index = i;
//
//          solu1: init random in domain of variable
//            int idxChoice = ran.nextInt(D[i].length);
//            if (canWInBin[idxChoice] > w[i] && checkR(r[i], D[i][idxChoice])) {
//                x[i].setValue(D[i][idxChoice]);
//                canWInBin[D[i][idxChoice]] -= w[i];
//                rInBin[D[i][idxChoice]].add(r[i]);
//            } else {
//                x[i].setValue(D[i][D[i].length - 1]);
//            }
            x[i].setValue(solutionCongConvert[i]);
            canWInBin[solutionCongConvert[i]] -= w[i];
            rInBin[solutionCongConvert[i]].add(r[i]);
        }

        int maxT = 0;
        for (int m : t) {
            if (maxT < m) {
                maxT = m;
            }
        }
        System.out.println("Max T: " + maxT);
        int maxR = 0;
        for (int m : r) {
            if (maxR < m) {
                maxR = m;
            }
        }
        System.out.println("Max R: " + maxR);


        // Constraint
        for (int b = 0; b < M - 1; b++) {
            // remove after add canInBin
//            S.post(new LessOrEqualW(new ConditionalSumD(x, w, b), W[b]));
            S.post(new Implicate(new LessOrEqual(1, new Occurrence(x, b)), new LessOrEqualLW(LW[b], new ConditionalSumD(x, w, b))));


//            S.post(new LessOrEqualD(new ConditionalSumD(x, p, b), P[b]));

            S.post(new LessOrEqualTR(new ConditionalSum(x, t, b, maxT + 1), T[b]));
            // remove after add rInBin
//            S.post(new LessOrEqualTR(new ConditionalSum(x, r, b, maxR + 1), R[b]));
        }
        S.post(new LessOrEqual(new Occurrence(x, M - 1), 0));
        System.out.println("Done constrain");

        mgr.close();
        System.out.println("Done mgr close");

    }

    //So Vat Thoa Man: 752
    //Time end: 496122
    //Done at step: 3653

    void searchV2() {
        System.out.println("Search");
        MinMaxSelector mms = new MinMaxSelector(S);
        int it = 0;
        int maxVatThoa = 0;
        int[] solu = new int[x.length];
        long time = System.currentTimeMillis();
        while (it < 100000 & S.violations() > 0) {

            VarIntLS sel_x = mms.selectMostViolatingVariableVietnv();
            int sel_v = mms.selectMostPromissingValue(sel_x, canWInBin, w[sel_x.index]);
            sel_x.setValuePropagate(sel_v);


            canWInBin[sel_v] -= w[sel_x.index];
            rInBin[sel_v].add(r[sel_x.index]);

            canWInBin[sel_x.getOldValue()] += w[sel_x.index];
            rInBin[sel_x.getOldValue()].remove(new Integer(r[sel_x.index]));


            System.out.println("::search --> Step " + it + ", x[..] := " + sel_v + ", S = " + S.violations() + " Weight_remaining: " + canWInBin[sel_v] + " r_contained: " + countRInBin(sel_v));
            it++;

            // true -> print, false -> don't print
            int t = maxItemOfSolutionAndPrint(false);
            if (maxVatThoa < t) {
                maxVatThoa = t;
                for (int i = 0; i < x.length; i++) {
                    solu[i] = x[i].getValue();
                }
                writeSolution("solution/output.json", solu);
                // 751 in ~ step 4000
                if (maxVatThoa >= 751) {
                    System.out.println("Time end: " + (System.currentTimeMillis() - time));
                    System.out.println("Done at step: " + (it-1));
                    maxItemOfSolutionAndPrint(true);
                    printDetail();
                    return;
                }
            }
            System.out.println(" So vat Max: " + maxVatThoa);
        }
        System.out.println("=======================================");
    }


    public int maxItemOfSolutionAndPrint(boolean show) {
        int[] solution = new int[N];
        for (int i = 0; i < N; i++) {
            solution[i] = x[i].getValue();
        }
        ArrayList<Integer> bin[] = new ArrayList[M];
        int t[][] = new int[M][mT];
        for (int i = 0; i < M; i++)
            for (int j = 0; j < mT; j++) {
                t[i][j] = 0;
            }
        int r[][] = new int[M][mR];
        for (int i = 0; i < M; i++)
            for (int j = 0; j < mR; j++) {
                r[i][j] = 0;
            }
        double w[] = new double[M];
        double p[] = new double[M];
        for (int i = 0; i < M; i++) {
            w[i] = 0;
            p[i] = 0;
            bin[i] = new ArrayList<Integer>();
        }

        for (int i = 0; i < N; i++) {
            w[solution[i]] += this.w[i];
            p[solution[i]] += items[i].getP();
            bin[solution[i]].add(i);
            t[solution[i]][items[i].getT()] = 1;
            r[solution[i]][items[i].getR()] = 1;

        }

        int num_items = 0;
        for (int i = 0; i < M; i++) {
            if (bin[i].size() > 0) {
                int numT = 0;
                int numR = 0;
                for (int j = 0; j < mT; j++)
                    numT += t[i][j];
                for (int j = 0; j < mR; j++)
                    numR += r[i][j];

                if (show) {
                    System.out.println("bin " + i);
                    for (int j : bin[i])
                        System.out.print(" ," + j);

                    System.out.printf("\n\n =>n:%d W:%f/(%f/%f), P:%f/%f t: %d/%d r:%d/%d\n\n",
                            bin[i].size(),
                            w[i],
                            bins[i].getMinLoad(),
                            bins[i].getCapacity(),
                            p[i],
                            bins[i].getP(),
                            numT,
                            bins[i].getT(),
                            numR,
                            bins[i].getR());
                }

                if (w[i] <= W[i] && w[i] >= LW[i] && p[i] <= P[i] && numT <= T[i] && numR <= R[i] && i < M-1) {
                    num_items += bin[i].size();
                }
            }


        }
        System.out.print("So Vat Thoa Man: " + num_items);
        return num_items;
    }


    public void writeSolution(String fn, int[] solution) {
        int solutionConvert[] = new int[N];


        for (int i = 0; i < N; i++) {
            if (solution[i] == M - 1)
                solutionConvert[i] = -1;
            else
                solutionConvert[i] = mapBinOldNews.get(solution[i]);
        }

        MinMaxTypeMultiKnapsackSolution s = new MinMaxTypeMultiKnapsackSolution(solutionConvert);
        s.writeToFile(fn);
    }

    void printDetail() {
        for (VarIntLS i : x) {
            System.out.print(i.getValue() + ",");
        }
    }

    public static void main(String[] args) {
        MiniProject1 miniProject = new MiniProject1();
        miniProject.readInit("solution/output-733_1000.json");
        miniProject.initData();
        miniProject.initConstraint();
        miniProject.searchV2();
        miniProject.maxItemOfSolutionAndPrint(true);

    }
}
