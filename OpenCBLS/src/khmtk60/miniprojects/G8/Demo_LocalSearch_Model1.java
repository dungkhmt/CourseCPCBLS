
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
import localsearch.functions.basic.FuncMult;
import localsearch.functions.sum.SumFun;
import localsearch.functions.sum.SumVar;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Demo_LocalSearch_Model1 {
    // mo hinh
    MinMaxTypeMultiKnapsackInput input;
    int nItems;
    int nBins;
    
    public void input() {
    	String inputFile = "./src/khmtk60/miniprojects/G8/InputOutputData/MinMaxTypeMultiKnapsackInput-16.json";
        input = new MinMaxTypeMultiKnapsackInput().loadFromFile(inputFile);
    }
    
    LocalSearchManager mgr;
    VarIntLS[][] X;
    ConstraintSystem S;
    
    public void stateModel() {
        nItems = input.getItems().length;
        nBins = input.getBins().length;
        mgr = new LocalSearchManager();
        X = new VarIntLS[nItems][nBins];
        for(int i = 0; i < nItems; i++)
            for(int j = 0; j < nBins; j++)
                X[i][j] = new VarIntLS(mgr, 0, 1);
        S = new ConstraintSystem(mgr);
        
        // moi item chi duoc xep vao mot bin
        for(int i = 0; i < nItems; i++) {
            VarIntLS[] y = new VarIntLS[nBins];
            for(int j = 0; j < nBins; j++) 
                y[j] = X[i][j];
            S.post(new IsEqual(new SumVar(y), 1));
        }
        
        // cac bin ma moi item co the xep vao
        for(int i = 0; i < nItems; i++) {
            for(int j = 0; j < nBins; j++) {
                boolean check = false;
                int[] binIndices = input.getItems()[i].getBinIndices();
                for(int k = 0; k < binIndices.length; k ++) {
                    if(binIndices[k] == j) {
                        check = true;
                        break;
                    }    
                }
                if(check == false)
                    S.post(new IsEqual(X[i][j], 0));
            }
        }
        
        // max and min load w
        for(int j = 0; j < nBins; j++) {
            VarIntLS[] y = new VarIntLS[nItems];
            for(int i = 0; i < nItems; i++)
                y[i] = X[i][j];
            IFunction[] f = new IFunction[nItems];
            for(int i = 0; i < nItems; i++) {
                f[i] = new FuncMult(y[i],(int) input.getItems()[i].getW());
            }
            S.post(new LessOrEqual(new SumFun(f),(int) input.getBins()[j].getCapacity()));
            S.post(new LessOrEqual((int) input.getBins()[j].getMinLoad(), new SumFun(f)));
        }
        
        // max load p
        for(int j = 0; j < nBins; j++) {
            VarIntLS[] y = new VarIntLS[nItems];
            for(int i = 0; i < nItems; i++)
                y[i] = X[i][j];
            IFunction[] f = new IFunction[nItems];
            for(int i = 0; i < nItems; i++) {
                f[i] = new FuncMult(y[i],(int) input.getItems()[i].getP());
            }
            S.post(new LessOrEqual(new SumFun(f),(int) input.getBins()[j].getP()));
        }
        
        // max type
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
                S.post(new Implicate(new IsEqual(X[i][j], 1), new IsEqual(Y[input.getItems()[i].getT()][j], 1)));
        for(int j = 0; j < nBins; j++) {
            VarIntLS[] z = new VarIntLS[maxT];
            for(int i = 0; i < maxT; i++)
                z[i] = Y[i][j];
            S.post(new LessOrEqual(new SumVar(z), input.getBins()[j].getT()));
        }
        
        // max class 
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
                S.post(new Implicate(new IsEqual(X[i][j], 1), new IsEqual(Z[input.getItems()[i].getR()][j], 1)));
        for(int j = 0; j < nBins; j++) {
            VarIntLS[] z = new VarIntLS[maxR];
            for(int i = 0; i < maxR; i++)
                z[i] = Z[i][j];
            S.post(new LessOrEqual(new SumVar(z), input.getBins()[j].getR()));
        }
        
        mgr.close();
    }
    
    public void search() {
        HillClimbing h = new HillClimbing();
        h.hillClimbing(S, 10000);
    }
    
    public void display() {  
        int[] W = new int[nBins];
        int[] P = new int[nBins];
        int[] T = new int[nBins];
        int[] R = new int[nBins];
        for(int i = 0; i < nBins; i++) {
            W[i] = 0;
            P[i] = 0;
            T[i] = 0;
            R[i] = 0;
        }
        for(int j = 0; j < nBins; j++) {
            ArrayList<Integer> types = new ArrayList<Integer>();
            ArrayList<Integer> classes = new ArrayList<Integer>();
            for(int i = 0; i < nItems; i++) {
                W[j] += X[i][j].getValue() * input.getItems()[i].getW();
                P[j] += X[i][j].getValue() * input.getItems()[i].getP();
                if(X[i][j].getValue() == 1) {
                    if(!types.contains(input.getItems()[i].getT())) {
                        T[j]++;
                        types.add(input.getItems()[i].getT());
                    }
                    if(!classes.contains(input.getItems()[i].getR())) {
                        R[j]++;
                        classes.add(input.getItems()[i].getR());
                    }
                }
            }
        }
        for(int j = 0; j < nBins; j++) {
            System.out.println("Bin " + j + ": ");
            System.out.print("\tLoadedItems: ");
            for(int i = 0; i < nItems; i++) 
                if(X[i][j].getValue() == 1)
                    System.out.print(i + " ");
            System.out.println();
            System.out.println("\tLoadedW : " + W[j]);
            System.out.println("\tLoadedP : " + P[j]);
            System.out.println("\tLoadedT : " + T[j]);
            System.out.println("\tLoadedR : " + R[j]);
        }
    }
    
    public void outputFile() {
    	MinMaxTypeMultiKnapsackSolution S = new MinMaxTypeMultiKnapsackSolution();
        int[] binOfItem = new int[nItems];
        for(int j = 0; j < nBins; j++) 
            for(int i = 0; i < nItems; i++) 
                if(X[i][j].getValue() == 1)
                    binOfItem[i] = j;
        S.setBinOfItem(binOfItem);
        Gson gson = new Gson();
		try (FileWriter writer = new FileWriter("./src/khmtk60/miniprojects/G8/outputLS1.json")) {
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
        Demo_LocalSearch_Model1 d = new Demo_LocalSearch_Model1();
        d.solve();
        end = System.currentTimeMillis();
		System.out.println("Time Millis: " + (end - start));
    }
}