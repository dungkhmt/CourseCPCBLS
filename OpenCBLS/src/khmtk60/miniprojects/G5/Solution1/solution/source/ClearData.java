package solution.source;


import solution.model.MinMaxTypeMultiKnapsackInput;
import solution.model.MinMaxTypeMultiKnapsackInputBin;
import solution.model.MinMaxTypeMultiKnapsackInputItem;

import java.util.ArrayList;

public class ClearData {

//    String fn;
    MinMaxTypeMultiKnapsackInput input;
    MinMaxTypeMultiKnapsackInputItem[] items;
    MinMaxTypeMultiKnapsackInputBin[] bins;

    void init(String fn) {
//        fn = "/home/source-pc/Java Project/MiniProject/src/main/java/localsearch/applications/vietnv/solution/MinMaxTypeMultiKnapsackInput-1000.json";
        input = new MinMaxTypeMultiKnapsackInput();
        items = input.loadFromFile(fn).getItems();
        bins = input.loadFromFile(fn).getBins();
    }

    void clear() {
        ArrayList<Integer> tmp = this.listLWThoa();
        MinMaxTypeMultiKnapsackInputBin[] tmp_bins = new MinMaxTypeMultiKnapsackInputBin[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            tmp_bins[i] = bins[tmp.get(i)];
        }

        bins = tmp_bins;
    }

    ArrayList<Integer> listLWThoa() {
        ArrayList<Integer> thoa = new ArrayList<>();
        double maxSumW = maxWofItemsWithOneRtype();
        for (int i = 0; i < bins.length; i++) {
            if (bins[i].getCapacity() <= 0)
                continue;
            if (maxSumW > bins[i].getMinLoad()) {
                thoa.add(i);
            }
        }
        System.out.println("so Bin thoa: " + thoa.size());
        return thoa;
    }

    double maxWofItemsWithOneRtype() {
        double[] sumWofEachR = new double[100];
        for (MinMaxTypeMultiKnapsackInputItem item : items) {
            sumWofEachR[item.getR()] += item.getW();
        }
        double maxW = 0;
        for (double i : sumWofEachR) {
            if (maxW < i)
                maxW = i;
        }
        return maxW;
    }

    void maxTR() {
        int m = 0;
        int mR = 0;
        for (MinMaxTypeMultiKnapsackInputBin bin : bins) {
            if (m < bin.getT()) {
                m = bin.getT();
            }
            if (mR < bin.getR()) {
                mR = bin.getR();
            }
            if (bin.getR() == 0) {
                System.out.println("false");
            }
            if (bin.getT() == 0) {
                System.out.println("false");
            }
        }
        System.out.println("maxT: " + m + " \nmaxR: " + mR);
    }

    void maxW(){
        double m = 0;
        for (MinMaxTypeMultiKnapsackInputBin i: bins){
            if (m < i.getMinLoad()){
                m=i.getMinLoad();
            }
        }
        System.out.println(m);
        for (MinMaxTypeMultiKnapsackInputItem i: items){
            if (m > i.getW()){
                m=i.getW();
            }
        }
        System.out.println(m);
    }


    void printMin(int d) {
        int[] idx = new int[]{
                40, 71, 248, 251, 265, 295, 436, 486, 490, 494, 495, 498, 503, 504, 505, 508, 598, 807, 824,
                849, 853, 862, 872, 993, 1014, 1091, 1105, 1113, 1119, 1128, 1192, 1295, 1325, 1362, 1494,
                1531, 1549, 1740, 1840, 1846
        };
//        for (int i:idx){
//            System.out.println(bins[i].getMinLoad());
//        }
        System.out.println("huhuhu  " + bins[d].getMinLoad());
    }

    public static void main(String[] args) {
        String fn = "/home/source-pc/Java Project/MiniProject/src/main/java/localsearch/applications/vietnv/solution/MinMaxTypeMultiKnapsackInput-3000.json";

        ClearData clearData = new ClearData();
        clearData.init(fn);
        clearData.maxTR();
        clearData.clear();
//        clearData.maxW();
//
//        clearData.printMin(1049);

    }
}
