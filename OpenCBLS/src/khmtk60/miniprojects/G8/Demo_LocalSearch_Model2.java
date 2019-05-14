
package khmtk60.miniprojects.G8;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.SolutionChecker;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackSolution;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.SumVar;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

public class Demo_LocalSearch_Model2 {
    // mo hinh
    MinMaxTypeMultiKnapsackInput input;
    int nItems;
    int nBins;
    
    LocalSearchManager mgr;
    VarIntLS[] X;
    ConstraintSystem S;
    
    public void input() {
    	String inputFile = "./src/khmtk60/miniprojects/G8/InputOutputData/MinMaxTypeMultiKnapsackInput-16.json";
        input = new MinMaxTypeMultiKnapsackInput().loadFromFile(inputFile);
        nItems = input.getItems().length;
        nBins = input.getBins().length;
    }
    
    public void stateModel() {
        mgr = new LocalSearchManager();
        X = new VarIntLS[nItems];
        for(int i = 0; i < nItems; i++)
            X[i] = new VarIntLS(mgr, 0, nBins-1); 
        S = new ConstraintSystem(mgr);
        
        // cac bin ma mot item co the xep vao
        for(int i = 0; i < nItems; i++) {
            int[] binIndices = input.getItems()[i].getBinIndices();
            IConstraint[] I = new IConstraint[binIndices.length];
            for(int j = 0; j < binIndices.length; j++) 
                I[j] = new IsEqual(X[i], binIndices[j]);
            S.post(new OR(I));
        }
        
        // max and min loadedW
        IFunction[] W = new IFunction[nBins];
        int[] w = new int[nItems];
        for(int i = 0; i < nItems; i++)
            w[i] = (int) input.getItems()[i].getW();
        for(int j = 0; j < nBins; j++) {
            W[j] = new ConditionalSum(X, w, j);
            S.post(new LessOrEqual(W[j],(int) input.getBins()[j].getCapacity()));
            S.post(new LessOrEqual((int) input.getBins()[j].getMinLoad(), W[j]));
        }
        
        // max loadedP
        IFunction[] P = new IFunction[nBins];
        int[] p = new int[nItems];
        for(int i = 0; i < nItems; i++)
            p[i] = (int) input.getItems()[i].getP();
        for(int j = 0; j < nBins; j++) {
            P[j] = new ConditionalSum(X, p, j);
            S.post(new LessOrEqual(P[j],(int) input.getBins()[j].getP()));
        }
        
        // max loadedType
        int maxT = 0;
        for(int i = 0; i < nItems; i++) {
            if(input.getItems()[i].getT() > maxT)
                maxT = input.getItems()[i].getT();
        }
        maxT++;
        VarIntLS[][] Y = new VarIntLS[maxT][nBins];
        for(int i = 0; i < maxT; i++)
            for(int j = 0; j < nBins; j++) 
                Y[i][j] = new VarIntLS(mgr, 0, 1);
        for(int i = 0; i < nItems; i++) 
            for(int j = 0; j < nBins; j++)
                S.post(new Implicate(new IsEqual(X[i], j), new IsEqual(Y[input.getItems()[i].getT()][j], 1)));
        for(int j = 0; j < nBins; j++) {
            VarIntLS[] z = new VarIntLS[maxT];
            for(int i = 0; i < maxT; i++)
                z[i] = Y[i][j];
            S.post(new LessOrEqual(new SumVar(z), input.getBins()[j].getT()));
        }
        
        // max loadedClasses
        int maxR = 0;
        for(int i = 0; i < nItems; i++) {
            if(input.getItems()[i].getR() > maxR)
                maxR = input.getItems()[i].getR();
        }
        maxR++;
        VarIntLS[][] Z = new VarIntLS[maxR][nBins];
        for(int i = 0; i < maxR; i++)
            for(int j = 0; j < nBins; j++) 
                Z[i][j] = new VarIntLS(mgr, 0, 1);
        for(int i = 0; i < nItems; i++) 
            for(int j = 0; j < nBins; j++)
                S.post(new Implicate(new IsEqual(X[i], j), new IsEqual(Z[input.getItems()[i].getR()][j], 1)));
        for(int j = 0; j < nBins; j++) {
            VarIntLS[] z = new VarIntLS[maxR];
            for(int i = 0; i < maxR; i++)
                z[i] = Z[i][j];
            S.post(new LessOrEqual(new SumVar(z), input.getBins()[j].getR()));
        }
        
        mgr.close();
    }
    
    public void tabuSearch(int timeLimit) {
		TabuSearch ts = new TabuSearch();
		ts.search(S, 80, timeLimit, 50000, 70);
	}
    
    public void search() {
        HillClimbing h = new HillClimbing();
        h.hillClimbing(S, 10000);
        //tabuSearch(100000);
    }
    
    public void display() {
        for(int j = 0; j < nBins; j++) {
            int W = 0, P = 0, T = 0, R = 0;
            ArrayList<Integer> types = new ArrayList<Integer>();
            ArrayList<Integer> classes = new ArrayList<Integer>();
            System.out.println("Bin " + j + ": ");
            System.out.print("\tLoadedItems: ");
            for(int i = 0; i < nItems; i++) {
                if(X[i].getValue() == j){
                    System.out.print(i + " ");
                    W += input.getItems()[i].getW();
                    P += input.getItems()[i].getP();
                    if(!types.contains(input.getItems()[i].getT())) {
                        types.add(input.getItems()[i].getT());
                        T++;
                    }
                    if(!classes.contains(input.getItems()[i].getR())) {
                        classes.add(input.getItems()[i].getR());
                        R++;
                    }
                }    
            }    
            System.out.println();
            System.out.println("\tLoadedW: " + W);
            System.out.println("\tLoadedP: " + P);
            System.out.println("\tLoadedT: " + T);
            System.out.println("\tLoadedR: " + R);
        }
    }
    
    public void outputFile() {
    	MinMaxTypeMultiKnapsackSolution S = new MinMaxTypeMultiKnapsackSolution();
        int[] binOfItem = new int[X.length];
        for(int i = 0; i < X.length; i++)
        	binOfItem[i] = X[i].getValue();
        S.setBinOfItem(binOfItem);
        Gson gson = new Gson();
		try (FileWriter writer = new FileWriter("./src/khmtk60/miniprojects/G8/outputLS2.json")) {
            gson.toJson(S, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SolutionChecker SC = new SolutionChecker();
        System.out.println(SC.check(input, S));
    }
    
    public void solve() {
        input();
        stateModel();
        search();
        display();
        outputFile();
    }

    public static void main(String[] args) {
    	long start, end;
		start = System.currentTimeMillis();
        Demo_LocalSearch_Model2 demo = new Demo_LocalSearch_Model2();
        demo.solve();
        end = System.currentTimeMillis();
		System.out.println("Time Millis: " + (end - start));
    }
}
