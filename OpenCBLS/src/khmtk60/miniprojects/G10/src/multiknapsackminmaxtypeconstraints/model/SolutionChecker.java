package multiknapsackminmaxtypeconstraints.model;

import com.google.gson.Gson;

import java.util.HashSet;

public class SolutionChecker {
    public String check(String inputJson, String outputJson){
        try{
            Gson gson = new Gson();
            MinMaxTypeMultiKnapsackInput I = gson.fromJson(inputJson, MinMaxTypeMultiKnapsackInput.class);
            MinMaxTypeMultiKnapsackSolution S = gson.fromJson(outputJson, MinMaxTypeMultiKnapsackSolution.class);
            return check(I,S);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return "N/A";
    }
    public String check(MinMaxTypeMultiKnapsackInput I, MinMaxTypeMultiKnapsackSolution S){
        int nbBins = I.getBins().length;
        double[] loadWeight = new double[nbBins];
        double[] loadP = new double[nbBins];
        HashSet<Integer>[] loadType = new HashSet[nbBins];
        HashSet<Integer>[] loadClass = new HashSet[nbBins];

        for(int b = 0; b < nbBins; b++){
            loadWeight[b] = 0;
            loadP[b] = 0;
            loadType[b] = new HashSet<Integer>();
            loadClass[b] = new HashSet<Integer>();
        }
        int nbItemNotScheduled = 0;
        int violations = 0;
        int[] X = S.getBinOfItem();

        MinMaxTypeMultiKnapsackInputItem[] items = I.getItems();
        MinMaxTypeMultiKnapsackInputBin[] bins = I.getBins();

        String description = "";
        for(int i = 0; i < X.length; i++){
            if(X[i] < 0 || X[i] >= nbBins){
                nbItemNotScheduled++;
            }else{
                int b = X[i];
                loadWeight[b] += items[i].getW();
                loadP[b] += items[i].getP();
                loadType[b].add(items[i].getT());
                loadClass[b].add(items[i].getR());

                boolean ok = false;
                for(int j = 0; j < items[i].getBinIndices().length; j++){
                    if(b == items[i].getBinIndices()[j]){
                        ok = true; break;
                    }
                }
                if(!ok){
                    violations++;
                }
            }
        }
        int violationW = 0;
        int violationP = 0;
        int violationT = 0;
        int violationR = 0;
        for(int b = 0; b < nbBins; b++){
            if(loadWeight[b] > 0 && (loadWeight[b] > bins[b].getCapacity())){
                violations++;
                violationW++;
                System.out.println("vio W at bin "+b);
            }
            if(loadWeight[b] > 0 && loadWeight[b] < bins[b].getMinLoad()){
                violations++;
                violationW++;
                System.out.println("vio LW at bin "+b);

            }
            if(loadP[b] > bins[b].getP()){
                violations++;
                violationP++;
            }
            if(loadType[b].size() > bins[b].getT()){
                violations++;
                violationT++;
            }
            if(loadClass[b].size() > bins[b].getR()){
                violations++;
                violationR++;
            }

        }
        description = "items-not-scheduled: " + nbItemNotScheduled + ",\n"
                + " violations: " + violations + ",\n"
                + " violationW: " + violationW + ", \n"
                + " violationP: " + violationP + ", \n"
                + " violationT: " + violationT + ", \n"
                + " violationR: " + violationR + ", \n";

        return description;
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
}
