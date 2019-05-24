package khmtk60.miniprojects.G15.Solution2.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.*;

import khmtk60.miniprojects.G15.Solution1.src.OSValidator;


public class SolutionSol2 {
    private int DEBUG = 1;
    public final int USE = 1;
    public int NOT_USE = -1000;
    public final int NOT_USE_FOREVER = -1;
    int seed = 0;
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

    public int[] getTake() {
        return binOfItem;
    }

    public void preprocess() {
        rand = new Random(System.nanoTime());
        //seed = rand.nextInt(100000);
        //seed = 89424;
        seed = 56130;
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
        binsUse.add(NOT_USE);

        for (int i = 0; i < n; i++) {
            if (!availR.contains(items[i].getR()) || newBinIndices[i].isEmpty()) {
                binOfItem[i] = NOT_USE_FOREVER;
            } else {
                itemsUse.add(i);
                newBinIndices[i].add(NOT_USE);
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
        HashSet<Integer> tabu = new HashSet<Integer>();
        for (int i : itemsUse) {
            binOfItem[i] = NOT_USE;
        }
        for (int i : itemsUse) {
            double w = items[i].getW();
            ArrayList<Integer>  binIndices = new ArrayList<Integer>(newBinIndices[i]);
            Collections.shuffle(binIndices, new Random(seed));
            for (int b : binIndices) {
                if(b == NOT_USE) continue;
                if(!tabu.contains(b)) {
                    binOfItem[i] = b;
                    tabu.add(b);
                    break;
                }
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
        itemPerBin[m].clear();
        HashSet<Integer> binIndices;

        for (int i: itemsUse) {
            b = binOfItem[i];
            if(b == NOT_USE) {
                nItemPerBin[b]++;
                itemPerBin[b].add(i);
                continue;
            }
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
        NOT_USE = m;
        binOfItem = new int[n];
        sumW = new double[m];
        sumP = new double[m];
        itemPerBin = new HashSet[m + 1];
        typePerBin = new HashMap[m];
        classPerBin = new HashMap[m];
        nTypePerBin = new int[m];
        nItemPerBin = new int[m + 1];
        nClassPerBin = new int[m];
        notInB = new int[m];

        for (int b = 0; b < m; b++) {
            typePerBin[b] = new HashMap<Integer, Integer>();
            classPerBin[b] = new HashMap<Integer, Integer>();
            itemPerBin[b] = new HashSet<Integer>();
        }
        itemPerBin[m] = new HashSet<Integer>();
    }

    public void writeSolution() {
    	char delimeter = '/';
    	String[] tmp = inputPath.split("/");
        if(OSValidator.isWindows()) {
        	delimeter = '\\';
        	tmp = inputPath.split("\\\\");
        }
        String fileName = tmp[tmp.length - 1].split("\\.")[0] + ".out";
        outputPath = inputPath.substring(0, inputPath.lastIndexOf(delimeter)) + delimeter + "pretrain" + delimeter + fileName;
        System.out.println(outputPath);
        try (FileWriter fileWriter = new FileWriter(outputPath)) {
            for (int i = 0; i < n; i++) {
                fileWriter.write(binOfItem[i] + " ");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void writeSubmit() {
        violations();
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
                if ((binOfItem[i] == NOT_USE) || binOfItem[i] == NOT_USE_FOREVER) {
                    fileWriter.write("-1 ");
                } else {
                    fileWriter.write(binOfItem[i]+ " ");
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
            if (binOfItem[i] == NOT_USE_FOREVER || binOfItem[i] == NOT_USE) {
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
    public void setDebug(int v) {
        this.DEBUG = v;
    }

    public double violations(int b) {
    	if(b == NOT_USE) {
    		return nItemPerBin[b]*0.25;
    	}
        double sumViolation = 0;

        if (nTypePerBin[b] == 0) {
            return 0;
        }

        sumViolation += Math.max(0, sumW[b] - bins[b].getCapacity());
        sumViolation += Math.max(0, sumP[b] - bins[b].getP());
        sumViolation += Math.max(0, nTypePerBin[b] - bins[b].getT());
        sumViolation += Math.max(0, nClassPerBin[b] - bins[b].getR());
        sumViolation += notInB[b];
        sumViolation *= 10;

        if (bins[b].getMinLoad() - sumW[b] > 0) {
            sumViolation += 5;
            sumViolation -= 0.01*nItemPerBin[b];
        }
        //sumViolation += Math.max(0, bins[b].getMinLoad() - sumW[b])/maxLoad;
        return sumViolation;
    }
 
    public double violations(ArrayList<Integer> a, int b) {
        double sumViolation = 0;
        double sumW = 0;
        double sumP = 0;
        int nType = 0;
        int nClass = 0;
        HashSet<Integer> type = new HashSet<Integer>();
        HashSet<Integer> clas = new HashSet<Integer>() ;

        int t = 0, r = 0;

        HashSet<Integer> binIndices;

        for (int i : a) {
            binIndices = newBinIndices[i];
            if (!binIndices.contains(bins[b].getId())) {
                sumViolation += 1;
            }

            sumW += items[i].getW();
            sumP += items[i].getP();
            t = items[i].getT();
            r = items[i].getR();
            if (!type.contains(t)) {
                nType += 1;
                type.add(t);
            }

            if (!clas.contains(r)) {
                nClass += 1;
                clas.add(r);
            }
        }

        sumViolation += Math.max(0, sumW - bins[b].getCapacity());
        sumViolation += Math.max(0, sumP - bins[b].getP());
        sumViolation += Math.max(0, nType - bins[b].getT());
        sumViolation += Math.max(0, nClass - bins[b].getR());

        if (bins[b].getMinLoad() - sumW > 0) {
            sumViolation += 0.5;
        }

        return sumViolation;
    }

    public double getAssignDelta(int newBin, int i) {
        int oldBin = binOfItem[i];
        if (oldBin == newBin) {
            return 0;
        }

        double newSumVOfB = 0, oldSumVOfB = 0;
        double newSumVOfA = 0, oldSumVOfA = 0;
        int t = items[i].getT();
        int r = items[i].getR();
        int deltaTypeB = 0, deltaTypeA = 0;
        int deltaClassB = 0, deltaClassA = 0;
        int deltaNotInB = 0, deltaNotInA = 0;
        HashSet<Integer> binIndices = newBinIndices[i];
        oldSumVOfB = violations(oldBin);
        if (oldBin != NOT_USE) {
            
            sumW[oldBin] -= items[i].getW();
            sumP[oldBin] -= items[i].getP();
            if (!binIndices.contains(bins[oldBin].getId())) {
                notInB[oldBin] -= 1;
                deltaNotInB = 1;
            }
            if (typePerBin[oldBin].get(t) == 1) {
                nTypePerBin[oldBin] -= 1;
                deltaTypeB = 1;
            }
            if (classPerBin[oldBin].get(r) == 1) {
                nClassPerBin[oldBin] -= 1;
                deltaClassB = 1;
            }
        }
        nItemPerBin[oldBin]--;
        newSumVOfB = violations(oldBin);

        if(oldBin != NOT_USE && newBin == NOT_USE) {
        	oldSumVOfA = violations(newBin);
        	nItemPerBin[newBin]++;
        	newSumVOfA = 10*(deltaNotInB + deltaTypeB + deltaClassB) + violations(newBin);
        } else {
        	oldSumVOfA = violations(newBin);
	        sumW[newBin] += items[i].getW();
	        sumP[newBin] += items[i].getP();
	        if (!binIndices.contains(bins[newBin].getId())) {
	            notInB[newBin] += 1;
	            deltaNotInA = -1;
	        }
	        if (!typePerBin[newBin].containsKey(t)) {
	            nTypePerBin[newBin] += 1;
	            deltaTypeA = -1;
	        }
	        if (!classPerBin[newBin].containsKey(r)) {
	            nClassPerBin[newBin] += 1;
	            deltaClassA = -1;
	        }    
	        
	        nItemPerBin[newBin]++;
	        newSumVOfA = violations(newBin);
        }

        if (oldBin != NOT_USE) {
            sumW[oldBin] += items[i].getW();
            sumP[oldBin] += items[i].getP();
            nTypePerBin[oldBin] += deltaTypeB;
            nClassPerBin[oldBin] += deltaClassB;
            notInB[oldBin] += deltaNotInB;  
        }
        nItemPerBin[oldBin]++;

        if (newBin != NOT_USE) {
            sumW[newBin] -= items[i].getW();
            sumP[newBin] -= items[i].getP();
            nTypePerBin[newBin] += deltaTypeA;
            nClassPerBin[newBin] += deltaClassA;
            notInB[newBin] += deltaNotInA;  
        }
        nItemPerBin[newBin]--;

        return (newSumVOfB + newSumVOfA) - (oldSumVOfB + oldSumVOfA);
    }

    private void restartMaintainConstraint(int[][] tabu, ArrayList<Integer> binsUse, ArrayList<Integer> itemsUse) {
        ArrayList <Integer> subItemsUse = new ArrayList <Integer>();
        if (itemsUse.size() > 1000) {
            Collections.shuffle(itemsUse, new Random(seed));

            for (int k = 0; k < 250; k++) {
                subItemsUse.add(itemsUse.get(k));
            }
        } else {
            subItemsUse.addAll(itemsUse);
        }
        Collections.shuffle(subItemsUse, new Random(seed));

        Random rand = new Random(seed);
        HashSet<Integer> maxVioBin = new HashSet<Integer>();
        ArrayList<Integer> binsNotChoosed = new ArrayList<Integer>();
 
        for (int b : binsUse) {
            if(nItemPerBin[b] == 0) {
                binsNotChoosed.add(b);
            }
        }

        HashSet<Integer> violationBins = new HashSet<Integer>();

        for(int b: binsUse) {
        	if(b == NOT_USE) continue;
        	double vio = violations(b);

        	if(vio > 0) {
        		violationBins.add(b);
        	}
        }
        
        for (int i : subItemsUse) {
            java.util.ArrayList<Integer> L = new java.util.ArrayList<Integer>();
            ArrayList<Integer>  binIndices = new ArrayList<Integer>(newBinIndices[i]);
            Collections.shuffle(binIndices, new Random(seed));
            for (int b : binIndices) {
                if (getAssignDelta(b, i) <= 0) L.add(b);
            }

            if (L.size() == 0) continue;

            int idx = rand.nextInt(L.size());
            assignPropagate(i, L.get(idx));
        }

        int k = 2;
        for (int i : subItemsUse) {
        	if(binsNotChoosed.size() == 0) {
        		break;
        	}
        	if(k == 5) {
        		break;
        	}
        	int idx = rand.nextInt(binsNotChoosed.size());
            int b_ucv = binsNotChoosed.get(idx);
            assignPropagate(i, b_ucv);
            binsNotChoosed.remove(idx);
            k++;
        }

        for (int i : subItemsUse) {
            if(violationBins.contains(binOfItem[i])) {
            	assignPropagate(i, NOT_USE);
            }
        }

        for (int i = 0; i < tabu.length; i++) {
            for (int j = 0; j < tabu[i].length; j++)
                tabu[i][j] = -1;
        }

    }

    public void updateBest() {

    }

    public void assignPropagate(int i, int newBin) {
        int oldBin = binOfItem[i];
        binOfItem[i] = newBin;

        int t = items[i].getT();
        int r = items[i].getR();
        HashSet<Integer> binIndices = newBinIndices[i];
        if(oldBin != NOT_USE) {
        	sumW[oldBin] -= items[i].getW();
	        sumP[oldBin] -= items[i].getP();
	        if (!binIndices.contains(bins[oldBin].getId())) {
	            notInB[oldBin] -= 1;
	        }
	        if (typePerBin[oldBin].get(t) == 1) {
	            nTypePerBin[oldBin] -= 1;
	            typePerBin[oldBin].remove(t);
	        } else {
	            typePerBin[oldBin].replace(t, typePerBin[oldBin].get(t) - 1);
	        }
	        if (classPerBin[oldBin].get(r) == 1) {
	            nClassPerBin[oldBin] -= 1;
	            classPerBin[oldBin].remove(r);
	        } else {
	            classPerBin[oldBin].replace(r, classPerBin[oldBin].get(r) - 1);
	        }   	
        }
        itemPerBin[oldBin].remove(i);
        nItemPerBin[oldBin]--;

        if(newBin != NOT_USE) {
        	sumW[newBin] += items[i].getW();
	        sumP[newBin] += items[i].getP();
	        if (!binIndices.contains(bins[newBin].getId())) {
	            notInB[newBin] += 1;
	        }
	        if (!typePerBin[newBin].containsKey(t)) {
	            nTypePerBin[newBin] += 1;
	            typePerBin[newBin].put(t, 1);
	        } else {
	            typePerBin[newBin].replace(t, typePerBin[newBin].get(t) + 1);
	        }
	        
	        if (!classPerBin[newBin].containsKey(r)) {
	            nClassPerBin[newBin] += 1;
	            classPerBin[newBin].put(r, 1);
	        } else {
	            classPerBin[newBin].replace(r, classPerBin[newBin].get(r) + 1);
	        }   
        }
        itemPerBin[newBin].add(i);
        nItemPerBin[newBin]++;
    }

    public void tabuSearch(int tabulen, int maxTime, int maxIter, int maxStable, ArrayList<Integer> binsUse, ArrayList<Integer> itemsUse) {
        this.binsUse = binsUse;
        this.itemsUse = itemsUse;
        double t0 = System.currentTimeMillis();
        int minB = Integer.MAX_VALUE, maxB = Integer.MIN_VALUE;
        int minI = Integer.MAX_VALUE, maxI = Integer.MIN_VALUE;

        for (int b : binsUse) {
            if (minB > b) minB = b;
            if (maxB < b) maxB = b;
        }
        for (int i : itemsUse) {
            if (minI > i) minI = i;
            if (maxI < i) maxI = i;
        }
        int DB = maxB - minB;
        int DI = maxI - minI;
        // System.out.println("n = " + n + ", D = " + D);
        int tabu[][] = new int[DI + 1][DB + 1];
        for (int i = 0; i <= DI; i++)
            for (int v = 0; v <= DB; v++)
                tabu[i][v] = -1;

        int it = 0;
        maxTime = maxTime * 1000;// convert into milliseconds

        double best = violations();
        double sumV = best;
        int[] x_best = new int[maxI + 1];

        for (int i : itemsUse) {
            int b = binOfItem[i];
            x_best[i] = b;
            if(b != NOT_USE_FOREVER) itemPerBin[b].add(i);
        }

        System.out.println("TabuSearch, init S = " + sumV);
        int nic = 0;
        ArrayList<AssignMove> moves = new ArrayList<AssignMove>();
        Random R = new Random(seed);
        while (it < maxIter && System.currentTimeMillis() - t0 < maxTime
                && sumV > 0) {
            double minDelta = Double.MAX_VALUE;
            moves.clear();

            ArrayList<Integer> maxVioBin = new ArrayList<Integer>();
            double maxVio = -1;
            for (int b : binsUse) {
                double vio = violations(b);

                if (maxVio < vio) {
                    maxVio = vio;
                    maxVioBin.clear();
                    maxVioBin.add(b);
                } else if (maxVio == vio) {
                    maxVioBin.add(b);
                }
            }
            int b_ucv = maxVioBin.get(R.nextInt(maxVioBin.size()));
            ArrayList<Integer> itemsUcv = new ArrayList<Integer>(itemPerBin[b_ucv]);
            int item_ucv = itemsUcv.get(R.nextInt(itemsUcv.size()));
            for (int b : newBinIndices[item_ucv]) {
                double delta = getAssignDelta(b, item_ucv);

                if (tabu[item_ucv - minI][b - minB] <= it || sumV + delta < best) {
                    if (delta < minDelta) {
                        minDelta = delta;
                        moves.clear();
                        moves.add(new AssignMove(b, binOfItem[item_ucv], item_ucv));
                    } else if (delta == minDelta) {
                        moves.add(new AssignMove(b, binOfItem[item_ucv], item_ucv));
                    }
                }
            }

            if (moves.size() <= 0) {
                restartMaintainConstraint(tabu, binsUse, itemsUse);
                sumV = violations();
                if(DEBUG == 1)
                    System.out.println("TabuSearch::restart..... S = " + sumV);
                nic = 0;
            } else {
                // perform the move
                AssignMove m = moves.get(R.nextInt(moves.size()));
                int sel_i = m.i;
                int sel_v = m.newBin;
                assignPropagate(sel_i, sel_v);
                tabu[sel_i - minI][sel_v - minB] = it + tabulen;
                
                //if(Math.abs(minDelta) < 1e-6) minDelta = 0;

                sumV += minDelta;
                if (it % 100 == 0) {
                    if(DEBUG == 1)
                        System.out.println("Step " + it + ", S = " + sumV
                                       + ", best = " + best + ", delta = " + minDelta
                                       + ", nic = " + nic);
                }

                // update best
                if (sumV < best) {
                    best = sumV;
                    for (int i : itemsUse) x_best[i] = binOfItem[i];
                }

                //if (minDelta >= 0) {
                if (sumV >= best) {
                    nic++;
                    if (nic > maxStable) {
                        restartMaintainConstraint(tabu, binsUse, itemsUse);
                        sumV = violations();
                        if(DEBUG == 1)
                            System.out.println("TabuSearch::restart..... S = " + sumV);
                        nic = 0;
                    }
                } else {
                    nic = 0;
                }
            }
            it++;
        }
        System.out.println("Step " + it + ", S = " + sumV);
        for (int i : itemsUse) binOfItem[i] = x_best[i];
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
    	long startTime = System.nanoTime();
        // solution.loadData("src/khmtk60/miniprojects/multiknapsackminmaxtypeconstraints/MinMaxTypeMultiKnapsackInput.json");
        String dataset_path = "src/khmtk60/miniprojects/G15/Solution2/dataset/MinMaxTypeMultiKnapsackInput-1000.json";
        if(OSValidator.isWindows()) {
        	dataset_path = dataset_path.replace("/","\\");
        }
        int tabulen = 10;
        SolutionSol2 solution = new SolutionSol2();
        solution.loadData(
            dataset_path);
        solution.preprocess();
        solution.initModel();

        solution.tabuSearch(5, 5000, 1000000, 1000, solution.getBinsUse(), solution.getItemsUse()); // Cho tap du lieu 51004418316727.json
        solution.writeSolution();
        solution.writeSubmit();
        solution.printSolution();
		long endTime   = System.nanoTime();
		double totalTime = (endTime - startTime)*(1e-9);
		System.out.println("Total runtime: " + totalTime + " s");
    }
}
// Seed = 42974: 231 item
// Seed = 79715: 231 item
// Seed = 56130: 229 item