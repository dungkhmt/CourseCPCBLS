package khmtk60.miniprojects.G15.Solution1.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.*;

public abstract class Solution {
    public final int USE = 1;
    public final int NOT_USE_FOREVER = -1;
    int seed = 0;
    public void setSeed(int seed) {
		this.seed = seed;
	}

	int binOfItem[];
    public int[] getBinOfItem() {
        return binOfItem;
    }

    public void setBinOfItem(int[] binOfItem) {
        this.binOfItem = binOfItem;
    }

    int n;
    int m;
    MinMaxTypeMultiKnapsackInputItem items[];
    MinMaxTypeMultiKnapsackInputBin bins[];

    ArrayList<Integer> binsUse = new ArrayList<Integer>();
    ArrayList<Integer> itemsUse = new ArrayList<Integer>();

    double sumW[];
    double sumP[];
    HashMap<Integer, Integer> typePerBin[];
    HashMap<Integer, Integer> classPerBin[];
    int nTypePerBin[];
    int nItemPerBin[];
    int nClassPerBin[];
    int notInB[];
    double sumV = 0;
    java.util.Random rand = null;
    HashMap<Integer, Double> sumWPerR = new HashMap<Integer, Double>();
    HashMap<Integer, ArrayList<Integer>> itemPerR = new HashMap<Integer, ArrayList<Integer>>();
    HashMap<Integer, HashSet<Integer>> binsPerR = new HashMap<Integer, HashSet<Integer>>();
    HashSet<Integer> itemPerBin[];
    HashSet<Integer> newBinIndices[];

    ArrayList<Integer> availR = new ArrayList<Integer>();

    double maxWR = -1;
    double maxLoad = -1;

    String inputPath;
    String outputPath;

    public void setAvailR(int r) {
        this.availR.clear();
        this.availR.add(r);
    }

    public ArrayList<Integer> getItemsUse() {
        return itemsUse;
    }

    public ArrayList<Integer> getBinsUse() {
        return binsUse;
    }

    public ArrayList<Integer> getItemsUseR(int r) {
        return itemPerR.get(r);
    }

    public ArrayList<Integer> getBinsUseR(int r) {
        return new ArrayList<Integer>(binsPerR.get(r));
    }

    public int[] getTake() {
        return binOfItem;
    }
    public int getN() {
        return n;
    }

    public void info() {
        HashMap<Integer, HashSet<Integer>> intersection = new HashMap<Integer, HashSet<Integer>>();
        double tmp = 0;
        int maxR = -1;
        int r = -1;
        double w = -1;
        HashSet<Integer> binIndices;

        double minLoad = Double.MAX_VALUE;


        for (int b = 0; b < m; b++) {
            if (maxR < bins[b].getR()) {
                maxR = bins[b].getR();
            }
            if (minLoad > bins[b].getMinLoad()) {
                minLoad = bins[b].getMinLoad();
            }
            if (maxLoad < bins[b].getMinLoad()) {
                maxLoad = bins[b].getMinLoad();
            }
        }
        sumWPerR.clear();
        itemPerR.clear();
        intersection.clear();

        for (int i = 0; i < n; i++) {
            w = items[i].getW();
            r = items[i].getR();
            binIndices = items[i].getBinIndices();
            tmp += w;
            if (!sumWPerR.containsKey(r)) {
                sumWPerR.put(r, w);
                itemPerR.put(r, new ArrayList<Integer>());
                intersection.put(r, new HashSet<Integer>(binIndices));
            } else {
                sumWPerR.replace(r, sumWPerR.get(r) + w);
                intersection.get(r).retainAll(binIndices);
            }
            itemPerR.get(r).add(i);
        }
        
        for (Map.Entry<Integer, HashSet<Integer>> entry : intersection.entrySet()) {
            r = entry.getKey();
            double minLoadR = Double.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (items[i].getR() == r) {
                    for (int x : items[i].getBinIndices()) {
                        if (minLoadR > bins[x].getMinLoad()) {
                            minLoadR = bins[x].getMinLoad();
                        }
                    }
                }
            }
            System.out.print("R = " + r + ", W = " + sumWPerR.get(r) + ", minLoadR = " + minLoadR + ", Indices = ");
            for (int b : entry.getValue()) {
                System.out.print(String.format("%.1f", bins[b].getMinLoad()) + " ");
            }
            System.out.println();

        }
        System.out.println("Number of items = " + n);
        System.out.println("Number of bins = " + m);
        System.out.println("Sum W (all Bins) = " + tmp);
        System.out.println("Max R (all Bins) = " + maxR);
        System.out.println("Min minload (all Bins) = " + minLoad);
    }

    public void preprocess() {

        rand = new Random(seed);
        int r;
        double w;
        HashSet<Integer> binIndices;
        sumWPerR.clear();
        availR.clear();
        itemPerR.clear();
        binsUse.clear();
        itemsUse.clear();

        for (int i = 0; i < n; i++) {
            w = items[i].getW();
            r = items[i].getR();
            binIndices = items[i].getBinIndices();
            if (!sumWPerR.containsKey(r)) {
                sumWPerR.put(r, w);
                itemPerR.put(r, new ArrayList<Integer>());
            } else {
                sumWPerR.replace(r, sumWPerR.get(r) + w);
            }
            itemPerR.get(r).add(i);
        }

        for (Map.Entry<Integer, Double> entry : sumWPerR.entrySet()) {
            r = entry.getKey();
            double minLoadR = Double.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (items[i].getR() == r) {
                    for (int x : items[i].getBinIndices()) {
                        if (minLoadR > bins[x].getMinLoad()) {
                            minLoadR = bins[x].getMinLoad();
                        }
                    }
                }
            }

            if (entry.getValue() >= minLoadR) {
                availR.add(r);
            }
        }

        newBinIndices = new HashSet[n];
        for(int i = 0; i < n; i++) {
            newBinIndices[i] = new HashSet<Integer>();
        }

        HashMap<Integer, Double> sumWRPerBin = new HashMap<Integer, Double>();
        
        int numBinUse = 0;
        int numItemUse = 0;
        for(int b = 0; b < m; b++) {
            int flagUse = 0;
            sumWRPerBin.clear();
            for(int i = 0; i < n; i++) {
                r = items[i].getR();

                if (availR.contains(r)) {
                    w = items[i].getW();
                    binIndices = items[i].getBinIndices();
                    if(binIndices.contains(b)) {
                        if(!sumWRPerBin.containsKey(r)) {
                            sumWRPerBin.put(r, w);
                        } else {
                            sumWRPerBin.replace(r, sumWRPerBin.get(r) + w);
                        }
                    }
                }
            }
            double minload = bins[b].getMinLoad();
            for (Map.Entry<Integer, Double> entry : sumWRPerBin.entrySet()) {
                r = entry.getKey();
                double sumWR = entry.getValue();
                if(sumWR >= minload) {
                    for(int i: itemPerR.get(r)) {
                        binIndices = items[i].getBinIndices();
                        if(binIndices.contains(b)) {
                            flagUse = 1;
                            newBinIndices[i].add(b);
                        }
                    }
                }
            }
            if (flagUse == 1) {
                numBinUse++;
                binsUse.add(b);
            } else {
                bins[b].setUse(NOT_USE_FOREVER);
            }
        }

        for (int i = 0; i < n; i++) {
            if (!availR.contains(items[i].getR()) || newBinIndices[i].isEmpty()) {
                binOfItem[i] = NOT_USE_FOREVER;
            } else {
                itemsUse.add(i);
                numItemUse++;
            }
        }

        for(int i: itemsUse) {
            r = items[i].getR();
            if(!binsPerR.containsKey(r)) {
                binsPerR.put(r, new HashSet<Integer>());
            }
            binsPerR.get(r).addAll(newBinIndices[i]);
        }

        System.out.println("");
        System.out.println(availR);
        System.out.println("Number bins is used: " + numBinUse);
        System.out.println("Number items is used: " + numItemUse);
    }

    public void initModel() {
        HashMap<Integer, Double> sumWPerBin = new HashMap<Integer, Double>();
        Collections.shuffle(itemsUse, new Random(seed));
        Collections.shuffle(binsUse, new Random(seed));

        for (int i : itemsUse) {
            double w = items[i].getW();
            ArrayList<Integer>  binIndices = new ArrayList<Integer>(newBinIndices[i]);
            Collections.shuffle(binIndices, new Random(seed));
            for (int b : binIndices) {
                if (!sumWPerBin.containsKey(b)) {
                    sumWPerBin.put(b, w);
                } else {
                    if (sumWPerBin.get(b) + w > bins[b].getCapacity()) {
                        continue;
                    }
                    sumWPerBin.replace(b, sumWPerBin.get(b) + w);
                }
                binOfItem[i] = b;
                break;
            }
        }

        //printSolution();
        System.out.println("Init S = " + violations());
    }

    public void resetArray(double a[]) {
        for (int i = 0; i < a.length; i++) {
            a[i] = 0;
        }
    }

    public void resetArray(int a[]) {
        for (int i = 0; i < a.length; i++) {
            a[i] = 0;
        }
    }

    public double violations() {
        double sumViolation = 0;
        resetArray(sumW);
        resetArray(sumP);
        resetArray(notInB);
        resetArray(nTypePerBin);
        resetArray(nClassPerBin);
        resetArray(nItemPerBin);

        int t = 0, r = 0, b = 0;
        for (b = 0; b < m; b++) {
            typePerBin[b].clear();
            classPerBin[b].clear();
            itemPerBin[b].clear();
        }
        HashSet<Integer> binIndices;

        for (int i: itemsUse) {
            b = binOfItem[i];
            itemPerBin[b].add(i);

            nItemPerBin[b] += 1;
            binIndices = newBinIndices[i];
            if (!binIndices.contains(bins[b].getId())) {
                notInB[b] += 1;
            }

            sumW[b] += items[i].getW();
            sumP[b] += items[i].getP();
            t = items[i].getT();
            r = items[i].getR();
            if (!typePerBin[b].containsKey(t)) {
                nTypePerBin[b] += 1;
                typePerBin[b].put(t, 1);
            } else {
                typePerBin[b].replace(t, typePerBin[b].get(t) + 1);
            }
            if (!classPerBin[b].containsKey(r)) {
                nClassPerBin[b] += 1;
                classPerBin[b].put(r, 1);
            } else {
                classPerBin[b].replace(r, classPerBin[b].get(r) + 1);
            }
        }

        for (int bin : binsUse) {
            sumViolation += violations(bin);
        }
        return sumViolation;
    }

    public abstract double violations(int b);

    public abstract double violations(ArrayList<Integer> a, int b);

    public void loadData(String src) {
        inputPath = src;
        MinMaxTypeMultiKnapsackInput input = MinMaxTypeMultiKnapsackInput.loadFromFile(src);
        items = input.getItems();
        bins = input.getBins();

        n = items.length;
        m = bins.length;
        for (int b = 0; b < m; b++) {
            bins[b].setId(b);
            bins[b].setUse(USE);
        }
        
        binOfItem = new int[n];
        sumW = new double[m];
        sumP = new double[m];
        itemPerBin = new HashSet[m];
        typePerBin = new HashMap[m];
        classPerBin = new HashMap[m];
        nTypePerBin = new int[m];
        nItemPerBin = new int[m];
        nClassPerBin = new int[m];
        notInB = new int[m];

        for (int b = 0; b < m; b++) {
            typePerBin[b] = new HashMap<Integer, Integer>();
            classPerBin[b] = new HashMap<Integer, Integer>();
            itemPerBin[b] = new HashSet<Integer>();
        }
    }

    public class StatusOfBin {
        public HashMap<Integer, Integer> type;
        public HashMap<Integer, Integer> clas;
        public int nType;
        public int nItem;
        public int nClass;
        public double sumW;
        public double sumP;
        public int b;
        public int notInB;
        public StatusOfBin(int b, HashMap<Integer, Integer> type, int nType, int nItem, double sumW, double sumP,
                                int nClass, HashMap<Integer, Integer> clas) {
            this.type = type;
            this.nType = nType;
            this.nItem = nItem;
            this.sumW = sumW;
            this.sumP = sumP;
            this.b = b;
            this.nClass = nClass;
            this.clas = clas;
        }

        public StatusOfBin(int b) {
            this.b = b;
            this.notInB = 0;
            this.nItem = 0;
            this.nType = 0;
            this.nClass = 0;
            this.sumW = 0;
            this.sumP = 0;
            this.type = new HashMap<Integer, Integer>();
            this.clas = new HashMap<Integer, Integer>();
        }

        public StatusOfBin(ArrayList<Integer> itemsUse, int b) {
            this.b = b;
            this.notInB = 0;
            this.nItem = itemsUse.size();
            this.nType = 0;
            this.nClass = 0;
            this.sumW = 0;
            this.sumP = 0;
            this.type = new HashMap<Integer, Integer>();
            this.clas = new HashMap<Integer, Integer>();

            int t = 0, r = 0;

            HashSet<Integer> binIndices;

            for (int i : itemsUse) {
                binIndices = newBinIndices[i];
                if (!binIndices.contains(bins[b].getId())) {
                    this.notInB += 1;
                }

                this.sumW += items[i].getW();
                this.sumP += items[i].getP();
                t = items[i].getT();
                r = items[i].getR();

                if (!this.type.containsKey(t)) {
                    this.nType += 1;
                    this.type.put(t, 1);
                } else {
                    this.type.replace(t, this.type.get(t) + 1);
                }
                if (!this.clas.containsKey(r)) {
                    this.nClass += 1;
                    this.clas.put(r, 1);
                } else {
                    this.clas.replace(r, this.clas.get(r) + 1);
                }
            }

        }

        public void addItem(int i) {
            if (!newBinIndices[i].contains(bins[b].getId())) {
                this.notInB += 1;
            }
            this.nItem += 1;
            this.sumW += items[i].getW();
            this.sumP += items[i].getP();
            int t = items[i].getT();
            int r = items[i].getR();
            if (!this.type.containsKey(t)) {
                this.nType += 1;
                this.type.put(t, 1);
            } else {
                this.type.replace(t, this.type.get(t) + 1);
            }
            if (!this.clas.containsKey(r)) {
                this.nClass += 1;
                this.clas.put(r, 1);
            } else {
                this.clas.replace(r, this.clas.get(r) + 1);
            }
        }

        public void removeItem(int i) {
            if (!newBinIndices[i].contains(bins[b].getId())) {
                this.notInB -= 1;
            }
            this.nItem -= 1;
            this.sumW -= items[i].getW();
            this.sumP -= items[i].getP();
            int t = items[i].getT();
            int r = items[i].getR();

            if (this.type.get(t) == 1) {
                this.nType -= 1;
                this.type.remove(t);
            } else {
                this.type.replace(t, this.type.get(t) - 1);
            }
            if (this.clas.get(r) == 1) {
                this.nClass -= 1;
                this.clas.remove(r);
            } else {
                this.clas.replace(r, this.clas.get(r) - 1);
            }
        }
    }

    public void loadPretrainedModel() {
    	String dataset = inputPath.split("\\.")[0].split("-")[1];
        String preTrainPath = "src/khmtk60/miniprojects/G15/Solution1/dataset/pretrain/MinMaxTypeMultiKnapsackInput-" + dataset + ".out";
        if(OSValidator.isWindows()) {
        	preTrainPath = preTrainPath.replace("/","\\");
        }
        System.out.println(preTrainPath);
        File file = new File(preTrainPath);
        try {
            Scanner sc = new Scanner(file);
            int i = 0;
            while (sc.hasNextInt()) {
                int b = sc.nextInt();
                if (availR.contains(items[i].getR())) {
                    binOfItem[i] = b;
                } else {
                    binOfItem[i] = NOT_USE_FOREVER;
                }

                i++;
            }

            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeSolution() {
    	char delimeter = '/';
    	String[] tmp = inputPath.split("/");
        if(OSValidator.isWindows()) {
        	delimeter = '\\';
        	tmp = inputPath.split("\\\\");
        }
        System.out.println(inputPath);
        System.out.println(inputPath.lastIndexOf(delimeter));
        String fileName = tmp[tmp.length - 1].split("\\.")[0] + ".out";  
        outputPath = inputPath.substring(0, inputPath.lastIndexOf(delimeter)) + delimeter + "pretrain" + delimeter + fileName;
        System.out.println(outputPath);
        try (FileWriter fileWriter = new FileWriter(outputPath)) {
            for (int i = 0; i < n; i++) {
                if (binOfItem[i] == NOT_USE_FOREVER) {
                    fileWriter.write(binOfItem[i] + " ");
                } else {
                    fileWriter.write(bins[binOfItem[i]].getId() + " ");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void writeSubmit() {
    	char delimeter = '/';
    	String[] tmp = inputPath.split("/");
        if(OSValidator.isWindows()) {
        	delimeter = '\\';
        	tmp = inputPath.split("\\\\");
        }
        String fileName = tmp[tmp.length - 1].split("\\.")[0] + ".out";
        outputPath = inputPath.substring(0, inputPath.lastIndexOf(delimeter)) + delimeter + "submit" + delimeter + fileName;
        System.out.println(outputPath);
        
        HashSet<Integer> binsNotUse = new HashSet<Integer>();
        for (int b = 0; b < m; b++) {
            if (violations(b) > 0) {
                binsNotUse.add(b);
            }
        }
        try (FileWriter fileWriter = new FileWriter(outputPath)) {
            for (int i = 0; i < n; i++) {
                if (binsNotUse.contains(binOfItem[i]) || binOfItem[i] == NOT_USE_FOREVER) {
                    fileWriter.write("-1 ");
                } else {
                    fileWriter.write(bins[binOfItem[i]].getId() + " ");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void printSolution() {
        violations();
        int sum_not_use = 0;
        System.out.println("n = " + n );
        HashMap<Integer, ArrayList<Integer>> result = new HashMap<Integer, ArrayList<Integer>>();

        for (int i = 0; i < n; i++) {
            if (binOfItem[i] == NOT_USE_FOREVER) {
                sum_not_use += 1;
            } else {
                int b = binOfItem[i];
                if (!result.containsKey(b)) {
                    result.put(b, new ArrayList<Integer>());
                }
                result.get(b).add(i);
            }
        }

        for (int b = 0; b < m; b++) {
            if (violations(b) > 0) {
                sum_not_use += nItemPerBin[b];
            }
        }

        System.out.println("Not use " + sum_not_use + " items");


        double vioR = 0, vioT = 0, vioMinLoad = 0, vioCapacity = 0, vioIndices = 0, vioP = 0;
        for (Map.Entry<Integer, ArrayList<Integer>> entry : result.entrySet()) {
            int b = entry.getKey();
            System.out.print("B = " + bins[b].getId() + ": ");
            System.out.println("");
            System.out.print(entry.getValue().size() + ": ");
            for (int item : entry.getValue()) {
                System.out.print(item + " ");
            }
            System.out.println("");
            System.out.print("W: ");
            for (int item : entry.getValue()) {
                System.out.print(items[item].getW() + " ");
            }
            System.out.println("");
            System.out.print("R: ");
            for (int item : entry.getValue()) {
                System.out.print(items[item].getR() + " ");
            }
            System.out.println("");
            System.out.print("T: ");
            for (int item : entry.getValue()) {
                System.out.print(items[item].getT() + " ");
            }
            System.out.println("");
            System.out.print("P: ");
            for (int item : entry.getValue()) {
                System.out.print(items[item].getP() + " ");
            }
            System.out.println("");
            violations();
            System.out.println("Min load: " + bins[b].getMinLoad());
            System.out.println("R: " + bins[b].getR());
            System.out.println("T: " + bins[b].getT());
            System.out.println("P: " + bins[b].getP());
            System.out.println("Capacity: " + bins[b].getCapacity());
            System.out.println("- Capacity violation = " + Math.max(0, sumW[b] - bins[b].getCapacity()));
            System.out.println("- Min load violation = " + Math.max(0, bins[b].getMinLoad() - sumW[b]));
            System.out.println("- P violation = " + Math.max(0, sumP[b] - bins[b].getP()));
            System.out.println("- Type violation = " + Math.max(0, nTypePerBin[b] - bins[b].getT()));
            System.out.println("- Class violation = " + Math.max(0, nClassPerBin[b] - bins[b].getR()));
            System.out.println("- Indies violation = " + notInB[b]);
            vioR += Math.max(0, nClassPerBin[b] - bins[b].getR());
            vioT += Math.max(0, nTypePerBin[b] - bins[b].getT());
            if (bins[b].getMinLoad() - sumW[b] > 0) {
                vioMinLoad += 0.5;
            }

            vioCapacity += Math.max(0, sumW[b] - bins[b].getCapacity());
            vioIndices += notInB[b];
            vioP += Math.max(0, sumP[b] - bins[b].getP());
            System.out.println("\n*****************************************************\n");
        }

        System.out.println("Total violation of R: " + vioR);
        System.out.println("Total violation of T: " + vioT);
        System.out.println("Total violation of Min load: " + vioMinLoad);
        System.out.println("Total violation of Capacity: " + vioCapacity);
        System.out.println("Total violation of Indices: " + vioIndices);
        System.out.println("Total violation of P: " + vioP);
        System.out.println("Not use " + sum_not_use + " items");
        System.out.println("Seed = " + seed);
    }

    public MinMaxTypeMultiKnapsackInputItem[] getItems() {
        return items;
    }
}
