package khmtk60.miniprojects.G8;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.SolutionChecker;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackSolution;

public class Demo_Choco {
    public void solve() {
    	String inputFile = "./src/khmtk60/miniprojects/G8/inputoutputdata/MinMaxTypeMultiKnapsackInput-1000.json";
        MinMaxTypeMultiKnapsackInput input = new MinMaxTypeMultiKnapsackInput().loadFromFile(inputFile);
        
        CPModel m = new CPModel();
        int nItems = input.getItems().length;
        int nBins = input.getBins().length;
        IntegerVariable[][] x = new IntegerVariable[nItems][nBins];
        for(int i = 0; i < nItems; i++) 
            for(int j = 0; j < nBins; j++)
                x[i][j] = Choco.makeIntVar("x"+i+j, 0, 1);
         
        // moi item chi duoc xep vao mot bin
        for(int i = 0; i < nItems; i++) {
            IntegerVariable[] y = new IntegerVariable[nBins];
            for(int j = 0; j < nBins; j++)
                y[j] = x[i][j];
            m.addConstraint(Choco.eq(Choco.sum(y), 1));
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
                    m.addConstraint(Choco.eq(x[i][j], 0));
            }
        }
        
        // max loaded w
        IntegerExpressionVariable[] loadedW = new IntegerExpressionVariable[nBins];
        for(int j = 0; j < nBins; j++) {
            loadedW[j] = Choco.plus(Choco.mult(x[0][j],(int) input.getItems()[0].getW()), 0);
            for(int i = 1; i < nItems; i++) {
                loadedW[j] = Choco.plus(loadedW[j], Choco.mult(x[i][j],(int) input.getItems()[i].getW()));
            }
            m.addConstraint(Choco.leq(loadedW[j],(int) input.getBins()[j].getCapacity()));
            m.addConstraint(Choco.leq((int) input.getBins()[j].getMinLoad(), loadedW[j]));
        }
        
        // max load p
        IntegerExpressionVariable[] loadedP = new IntegerExpressionVariable[nBins];
        for(int j = 0; j < nBins; j++) {
            loadedP[j] = Choco.plus(Choco.mult(x[0][j],(int) input.getItems()[0].getP()), 0);
            for(int i = 1; i < nItems; i++) {
                loadedP[j] = Choco.plus(loadedP[j], Choco.mult(x[i][j],(int) input.getItems()[i].getP()));
            }
            m.addConstraint(Choco.leq(loadedP[j],(int) input.getBins()[j].getP()));
        }
        
        // maxT
        int maxT = 0;
        for(int i = 0; i < nItems; i++) {
            if(input.getItems()[i].getT() > maxT)
                maxT = input.getItems()[i].getT();
        }
        maxT++;
        // max load type 
        IntegerVariable[][] t = new IntegerVariable[maxT][nBins];
        for(int i = 0; i < maxT; i++)
            for(int j = 0; j < nBins; j++) 
                t[i][j] = Choco.makeIntVar("y"+i+j, 0, 1);
        for(int i = 0; i < nItems; i++) 
            for(int j = 0; j < nBins; j++)
                m.addConstraint(Choco.implies(Choco.eq(x[i][j], 1), Choco.eq(t[input.getItems()[i].getT()][j], 1)));
        for(int j = 0; j < nBins; j++) {
            IntegerVariable[] z = new IntegerVariable[maxT];
            for(int i = 0; i < maxT; i++)
                z[i] = t[i][j];
            m.addConstraint(Choco.leq(Choco.sum(z), input.getBins()[j].getT()));
        }
        
        // max class 
        int maxR = 0;
        for(int i = 0; i < nItems; i++) {
            if(input.getItems()[i].getR() > maxR)
                maxR = input.getItems()[i].getR();
        }
        maxR++;
        IntegerVariable[][] r = new IntegerVariable[maxR][nBins];
        for(int i = 0; i < maxR; i++)
            for(int j = 0; j < nBins; j++) 
                r[i][j] = Choco.makeIntVar("r"+i+j, 0, 1);
        for(int i = 0; i < nItems; i++) 
            for(int j = 0; j < nBins; j++)
                m.addConstraint(Choco.implies(Choco.eq(x[i][j], 1), Choco.eq(r[input.getItems()[i].getR()][j], 1)));
        for(int j = 0; j < nBins; j++) {
            IntegerVariable[] z = new IntegerVariable[maxR];
            for(int i = 0; i < maxR; i++)
                z[i] = r[i][j];
            m.addConstraint(Choco.leq(Choco.sum(z), input.getBins()[j].getR()));
        }
        
        CPSolver s = new CPSolver();
        s.read(m);
	s.solve();
        s.printRuntimeStatistics();
        
        // in ket qua ra hinh
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
                W[j] += s.getVar(x[i][j]).getVal() * input.getItems()[i].getW();
                P[j] += s.getVar(x[i][j]).getVal() * input.getItems()[i].getP();
                if(s.getVar(x[i][j]).getVal() == 1) {
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
                if(s.getVar(x[i][j]).getVal() == 1)
                    System.out.print(i + " ");
            System.out.println();
            System.out.println("\tLoadedW : " + W[j]);
            System.out.println("\tLoadedP : " + P[j]);
            System.out.println("\tLoadedT : " + T[j]);
            System.out.println("\tLoadedR : " + R[j]);
        }
        
        // ghi ra file json
        MinMaxTypeMultiKnapsackSolution S = new MinMaxTypeMultiKnapsackSolution();
        int[] binOfItem = new int[nItems];
        for(int j = 0; j < nBins; j++) 
            for(int i = 0; i < nItems; i++) 
                if(s.getVar(x[i][j]).getVal() == 1)
                    binOfItem[i] = j;
        S.setBinOfItem(binOfItem);
        Gson gson = new Gson();
		try (FileWriter writer = new FileWriter("./src/khmtk60/miniprojects/G8/outputChoco.json")) {
            gson.toJson(S, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SolutionChecker SC = new SolutionChecker();
        System.out.println(SC.check(input, S));
    }
    
    public static void main(String[] args) {
    	long start, end;
		start = System.currentTimeMillis();
        Demo_Choco demo = new Demo_Choco();
        demo.solve();
        end = System.currentTimeMillis();
		System.out.println("Time Millis: " + (end - start));
    }
}
