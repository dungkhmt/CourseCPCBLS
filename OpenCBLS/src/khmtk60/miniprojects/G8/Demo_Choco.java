package khmtk60.miniprojects.G8;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import java.util.ArrayList;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;

public class Demo_Choco {
    public void solve() {
        MinMaxTypeMultiKnapsackInput data = new MinMaxTypeMultiKnapsackInput().loadFromFile(
                    "E:\\TaiLieuHocTap\\LocalSearch\\MiniProject\\CourseCPCBLS\\OpenCBLS\\src\\khmtk60\\miniprojects\\multiknapsackminmaxtypeconstraints\\MinMaxTypeMultiKnapsackInput.json");
        
        CPModel m = new CPModel();
        int nItems = data.getItems().length;
        int nBins = data.getBins().length;
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
                int[] binIndices = data.getItems()[i].getBinIndices();
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
            loadedW[j] = Choco.plus(Choco.mult(x[0][j],(int) data.getItems()[0].getW()), 0);
            for(int i = 1; i < nItems; i++) {
                loadedW[j] = Choco.plus(loadedW[j], Choco.mult(x[i][j],(int) data.getItems()[i].getW()));
            }
            m.addConstraint(Choco.leq(loadedW[j],(int) data.getBins()[j].getCapacity()));
            m.addConstraint(Choco.leq((int) data.getBins()[j].getMinLoad(), loadedW[j]));
        }
        
        // max load p
        IntegerExpressionVariable[] loadedP = new IntegerExpressionVariable[nBins];
        for(int j = 0; j < nBins; j++) {
            loadedP[j] = Choco.plus(Choco.mult(x[0][j],(int) data.getItems()[0].getP()), 0);
            for(int i = 1; i < nItems; i++) {
                loadedP[j] = Choco.plus(loadedP[j], Choco.mult(x[i][j],(int) data.getItems()[i].getP()));
            }
            m.addConstraint(Choco.leq(loadedP[j],(int) data.getBins()[j].getP()));
        }
        
        // maxT
        int maxT = 0;
        for(int i = 0; i < nItems; i++) {
            if(data.getItems()[i].getT() > maxT)
                maxT = data.getItems()[i].getT();
        }
        maxT++;
        // max load type 
        IntegerVariable[][] t = new IntegerVariable[maxT][nBins];
        for(int i = 0; i < maxT; i++)
            for(int j = 0; j < nBins; j++) 
                t[i][j] = Choco.makeIntVar("y"+i+j, 0, 1);
        for(int i = 0; i < nItems; i++) 
            for(int j = 0; j < nBins; j++)
                m.addConstraint(Choco.implies(Choco.eq(x[i][j], 1), Choco.eq(t[data.getItems()[i].getT()][j], 1)));
        for(int j = 0; j < nBins; j++) {
            IntegerVariable[] z = new IntegerVariable[maxT];
            for(int i = 0; i < maxT; i++)
                z[i] = t[i][j];
            m.addConstraint(Choco.leq(Choco.sum(z), data.getBins()[j].getT()));
        }
        
        // max class 
        int maxR = 0;
        for(int i = 0; i < nItems; i++) {
            if(data.getItems()[i].getR() > maxR)
                maxR = data.getItems()[i].getR();
        }
        maxR++;
        IntegerVariable[][] r = new IntegerVariable[maxR][nBins];
        for(int i = 0; i < maxR; i++)
            for(int j = 0; j < nBins; j++) 
                r[i][j] = Choco.makeIntVar("r"+i+j, 0, 1);
        for(int i = 0; i < nItems; i++) 
            for(int j = 0; j < nBins; j++)
                m.addConstraint(Choco.implies(Choco.eq(x[i][j], 1), Choco.eq(r[data.getItems()[i].getR()][j], 1)));
        for(int j = 0; j < nBins; j++) {
            IntegerVariable[] z = new IntegerVariable[maxR];
            for(int i = 0; i < maxR; i++)
                z[i] = r[i][j];
            m.addConstraint(Choco.leq(Choco.sum(z), data.getBins()[j].getR()));
        }
        
        CPSolver s = new CPSolver();
        s.read(m);
	s.solve();
        s.printRuntimeStatistics();
        
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
                W[j] += s.getVar(x[i][j]).getVal() * data.getItems()[i].getW();
                P[j] += s.getVar(x[i][j]).getVal() * data.getItems()[i].getP();
                if(s.getVar(x[i][j]).getVal() == 1) {
                    if(!types.contains(data.getItems()[i].getT())) {
                        T[j]++;
                        types.add(data.getItems()[i].getT());
                    }
                    if(!classes.contains(data.getItems()[i].getR())) {
                        R[j]++;
                        classes.add(data.getItems()[i].getR());
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
    }
    
    public static void main(String[] args) {
        Demo_Choco demo = new Demo_Choco();
        demo.solve();
    }
}
