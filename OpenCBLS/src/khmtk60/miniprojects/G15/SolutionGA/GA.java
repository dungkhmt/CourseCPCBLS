/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khmtk60.miniprojects.G15.SolutionGA;

import java.util.*;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.*;

/**
 *
 * @author Khang
 */
public class GA {
    public final int population = 500; //sá»‘ lÆ°á»£ng cÃ¡ thá»ƒ cá»§a quáº§n thá»ƒ
    public int n; // sá»‘ lÆ°á»£ng item
    public int m; // sá»‘ lÆ°á»£ng bin
    public MinMaxTypeMultiKnapsackInputItem items[];
    public MinMaxTypeMultiKnapsackInputBin bins[];
    Random r;
   
    /*
    Ä�á»�c dá»¯ liá»‡u
    input:
    output:
    */
    
    public void loadData(String src) {
        MinMaxTypeMultiKnapsackInput inputs = new MinMaxTypeMultiKnapsackInput();
        MinMaxTypeMultiKnapsackInput input = inputs.loadFromFile(src);
        items = input.getItems();
        bins = input.getBins();
        n = items.length;
        m = bins.length;
    }
    
    /*
    Khá»Ÿi táº¡o quáº§n thá»ƒ ban Ä‘áº§u
    input:
    output: Danh sÃ¡ch gá»“m 500 cÃ¡ thá»ƒ,má»—i cÃ¡ thá»ƒ lÃ  1 lá»�i giáº£i cá»§a bÃ i toÃ¡n
    */
    
    public ArrayList<int[]> initPopulations(){
        r = new Random();
        int[] binIndices;
        int[] solu = new int[n];
        ArrayList<int[]> solutions = new ArrayList<int[]>(population);
        for(int i = 0;i<population;i++){         
            for(int j = 0;j<n;j++){
                binIndices = items[j].getBinIndices();   
                solu[j] = binIndices[r.nextInt(binIndices.length)];         
            
            }
            solutions.add(i,solu);
        }
        return solutions;  
    };
    
    /*
    TÃ­nh toÃ¡n violation cá»§a lá»�i giáº£i
    input: má»™t lá»�i giáº£i cá»§a bÃ i toÃ¡n
    output: máº£ng gá»“m 6 pháº§n tá»­ chá»©a cÃ¡c violation: violation weight,
    violation minload,violation P,violation T,violation R,Tá»•ng cÃ¡c violation.
    */
    
    public int[] violations(int[] solutions){
        int[] violations = new int[6];
        ArrayList<ArrayList<Integer>> binUse = new ArrayList<ArrayList<Integer>>(m);
        double sumW = 0;
        double sumP = 0;
        int nType = 0;
        int nClass = 0;
        int r = 0;
        int t = 0;
        HashSet<Integer> type = new HashSet<Integer>();
        HashSet<Integer> clas = new HashSet<Integer>();
        binUse = getBinUse(solutions);
        for(int i=0;i<binUse.size();i++){
            for(int j:binUse.get(i)){
                sumW += items[j].getW();
                sumP += items[j].getP();
                t = items[j].getT();
                r = items[j].getR();
                if(!type.contains(r)){
                nClass += 1;
                type.add(r);
                }
                if(!type.contains(t)){
                nType += 1;
                type.add(t);
                }     
            }
            if(bins[i].getCapacity()<sumW)violations[0]+=1;
            if(bins[i].getMinLoad()>sumW)violations[1]+=1;
            if(bins[i].getP()<sumP)violations[2]+=1;
            if(bins[i].getT()<nType)violations[3]+=1;
            if(bins[i].getR()<nClass)violations[4]+=1;
        }
        violations[5] = violations[0]+violations[1]+violations[2]+violations[3]+violations[4];
        return violations;
    }
    
    /*
    XÃ¡c Ä‘á»‹nh tá»«ng bin chá»©a cÃ¡c item nÃ o
    input: Máº£ng chá»©a lá»�i giáº£i cá»§a bÃ i toÃ¡n
    output: Danh sÃ¡ch m bin, má»—i bin chá»©a 1 danh sÃ¡ch cÃ¡c item
    */
    
    public ArrayList<ArrayList<Integer>> getBinUse(int[] solution){
        ArrayList<ArrayList<Integer>> binUse = new ArrayList<ArrayList<Integer>>(m);
        ArrayList<Integer> b = new ArrayList<Integer>();
        for(int j = 0;j<m;j++){
            for(int i=0;i< solution.length;i++){
                if(j == solution[i]){
                    b.add(i);
                }                   
            }
        binUse.add(j,(ArrayList<Integer>) b.clone());
        b.clear();
        }
        return binUse;
    }
    
    /*
    Lai ghÃ©p 2 cÃ¡ thá»ƒ bá»‘,máº¹ báº¥t kÃ¬ Ä‘á»ƒ táº¡o ra 2 cÃ¡ thá»ƒ con tÆ°Æ¡ng á»©ng
    input: 2 máº£ng tÆ°Æ¡ng á»©ng vá»›i 2 lá»�i giáº£i.
    output: Danh sÃ¡ch 2 lá»�i giáº£i má»›i tÆ°Æ¡ng á»©ng
    */
    
    public ArrayList<int[]> crossover(int[] solution1, int[] solution2){
        Random rand = new Random();
        ArrayList<int[]> child = new ArrayList<int[]>(2);
        int index1 = rand.nextInt(n);
        int index2 = rand.nextInt(n-1);
        int[] child1 = new int[n];
        int[] child2 = new int[n];
        if(index1 == index2){
            index2 = index2++; 
        }
        if(index1 > index2){
            int swap;
            swap = index1;
            index1 = index2;
            index2 = swap;
        }
        for(int i =0;i<index1;i++){
            child1[i] = solution1[i];
            child2[i] = solution2[i];
        }
        for(int i = index1;i<index2;i++){
            child1[i] = solution2[i];
            child2[i] = solution1[i];
        }
        for(int i =index2;i<n;i++){
            child1[i] = solution1[i];
            child2[i] = solution2[i];
        }
        child.add(0, child1);
        child.add(1,child2);
        return child;
    }

    /*
    Ä�á»™t biáº¿n 1 cÃ¡ thá»ƒ báº¥t kÃ¬ Ä‘á»ƒ táº¡o ra cÃ¡ thá»ƒ con má»›i
    input: máº£ng tÆ°Æ¡ng á»©ng vá»›i lá»�i giáº£i.
    output: Danh sÃ¡ch lá»�i giáº£i má»›i tÆ°Æ¡ng á»©ng
    */
    
    public ArrayList<int[]> mutate(int n,int m,int[] solution){
        ArrayList<int[]> child = new ArrayList<int[]>(1);
        Random rand = new Random();
        int[] index = new int[10];
        int[] binIndice;
        int[] child1 = new int[n];
        for(int i = 0;i<n;i++){
            child1[i] = solution[i];
        }
        for(int j =0;j<index.length;j++){
            index[j] = rand.nextInt(n);
            binIndice = items[index[j]].getBinIndices();
            child1[index[j]] = binIndice[rand.nextInt(binIndice.length)];
        }
        child.add(0,child1);
        return child;
    }
    
    /*
    Chá»�n lá»�c 50% sá»‘ cÃ¡ thá»ƒ con cÃ³ hÃ m fitness tá»‘t nháº¥t vÃ  50%
    cÃ¡ thá»ƒ bá»‘ máº¹ ngáº«u nhiÃªn Ä‘á»ƒ táº¡o ra 500 cÃ¡ thá»ƒ cho láº§n lai ghÃ©p,Ä‘á»™t biáº¿n tiáº¿p theo
    input: Danh sÃ¡ch cÃ¡c lá»�i giáº£i bá»‘ máº¹ vÃ  danh sÃ¡ch cÃ¡c lá»�i giáº£i con.
    output: Danh sÃ¡ch cÃ¡c lá»�i giáº£i má»›i tÆ°Æ¡ng á»©ng vá»›i quáº§n thá»ƒ má»›i.
    */
    
    public ArrayList<int[]> selection(ArrayList<int[]> parents,ArrayList<int[]> child){
        double[] violation = null;
        ArrayList<int[]> newSolutions = new ArrayList<int[]>(population);
        sortSolution(child,0,population-1);
        
        //Collections.shuffle(child);
        Collections.shuffle(parents);
        for(int i = 0; i< population/2;i++){
            newSolutions.add(i,child.get(i));
        }
        for(int i = population/2;i<population;i++){
            newSolutions.add(i,parents.get(i));
        }
        return newSolutions; 
    }
     
    /*
    Káº¿t há»£p cÃ¡c phÆ°Æ¡ng phÃ¡p lai ghÃ©p,Ä‘á»™t biáº¿n,chá»�n lá»�c Ä‘á»ƒ táº¡o ra quáº§n thá»ƒ má»›i
    input: Danh sÃ¡ch cÃ¡c lá»�i giáº£i tÆ°Æ¡ng á»©ng vá»›i quáº§n thá»ƒ, kÃ­ch thÆ°á»›c quáº§n thá»ƒ cáº§n táº¡o
    output: Danh sÃ¡ch cÃ¡c lá»�i giáº£i tÆ°Æ¡ng á»©ng vá»›i quáº§n thá»ƒ má»›i
    */
    
    public ArrayList<int[]> generateSolutions(ArrayList<int[]> solutions,int population){
        int n = items.length;
        int m = bins.length;
        int rate_mutate = (int) (0.1*population);
        int rate_crossover = (int) (0.9*population);
        Random r = new Random();
        ArrayList<int[]> newSolutions = new ArrayList<>();
        ArrayList<int[]> childSolutions = new ArrayList<>();
        for(int i = 0;i< rate_crossover;i+=2){
            childSolutions.add(i,crossover(solutions.get(r.nextInt(population)),solutions.get(r.nextInt(population))).get(0));
            childSolutions.add(i+1,crossover(solutions.get(r.nextInt(population)),solutions.get(r.nextInt(population))).get(1));
        }
        for(int i = 0;i<rate_mutate;i++){
            childSolutions.add(rate_crossover+i,mutate(n,m,solutions.get(r.nextInt(population))).get(0));
        }
        
        newSolutions = selection(solutions,childSolutions);
        return newSolutions;
    }
    
    /*
    TÃ­nh violation tÆ°Æ¡ng á»©ng vá»›i má»—i bin
    input: chá»‰ sá»‘ cá»§a bin, danh sÃ¡ch cÃ¡c item chá»©a trong bin Ä‘Ã³
    output: tá»•ng sá»‘ violation cá»§a má»—i bin
    */
    
    public int miniViolations(int binIndex,ArrayList<Integer> itemUse){
        int miniviolations = 0;
        double sumW = 0;
        double sumP = 0;
        int nType = 0;
        int nClass = 0;
        int r = 0;
        int t = 0;
        HashSet<Integer> type = new HashSet<Integer>();
        HashSet<Integer> clas = new HashSet<Integer>();
        for(int j:itemUse){
            sumW += items[j].getW();
            sumP += items[j].getP();
            t = items[j].getT();
            r = items[j].getR();
            if(!type.contains(r)){
            nClass += 1;
            type.add(r);
        }
            if(!type.contains(t)){
                nType += 1;
                type.add(t);
            }     
        }
        if(bins[binIndex].getCapacity()<sumW)miniviolations+=1;
        if(bins[binIndex].getMinLoad()>sumW)miniviolations+=1;
        if(bins[binIndex].getP()<sumP)miniviolations+=1;
        if(bins[binIndex].getT()<nType)miniviolations+=1;
        if(bins[binIndex].getR()<nClass)miniviolations+=1;
        return miniviolations;
    }
    
    /*
    Thuáº­t toÃ¡n tham lam loáº¡i bá»�, thÃªm cÃ¡c item vÃ o cÃ¡c bin sao cho violation
    cá»§a má»—i bin há»™i tá»¥ vá»� 0
    input:  máº£ng tÆ°Æ¡ng á»©ng vá»›i lá»�i giáº£i hay cÃ¡ thá»ƒ.
    output: 
    */
    
    public void greedyAlgorithm(int[] solutions){
        Random r = new Random();
        ArrayList<ArrayList<Integer>> binUse = new ArrayList<ArrayList<Integer>>(m);
        binUse = getBinUse(solutions);
        ArrayList<Integer> BinG = new ArrayList<Integer>(n);
        ArrayList<Integer> BinGG = new ArrayList<Integer>(n);
        int Sumviolations =0;
        int Sumitems = 0;
        for(int i =0;i<binUse.size();i++){   
            while(miniViolations(i,binUse.get(i))!=0 && binUse.get(i).size() != 0){
                int k = r.nextInt(binUse.get(i).size());
                BinG.add(binUse.get(i).get(k));
                binUse.get(i).remove(k); 
            }
        }
        for(int i =0;i<binUse.size();i++){
            while(miniViolations(i,binUse.get(i))!=0 && binUse.get(i).size() == 0){
                int l = r.nextInt(BinG.size());
                BinGG.add(BinG.get(l));
                binUse.add((ArrayList<Integer>) BinGG.clone());
                BinGG.clear();
                BinG.remove(l); 
            }
            Sumviolations+=miniViolations(i,binUse.get(i)); 
            System.out.println("Bin "+i+" have items: ");
            for(int j:binUse.get(i)){
                System.out.print(j+" ");
            }
            System.out.print("\n");
        }
        System.out.println("Sum violations: "+Sumviolations);
        System.out.println("Sum items not use: "+BinG.size());
    }
    
    /*
    Sáº¯p xáº¿p cÃ¡c lá»�i giáº£i tÆ°Æ¡ng á»©ng vá»›i giÃ¡ trá»‹ violation Ä‘á»ƒ chá»�n ra
    cÃ¡c lá»�i giáº£i cÃ³ violation nhá»� nháº¥t theo thuáº­t toÃ¡n sáº¯p xáº¿p nhanh
    input: Danh sÃ¡ch cÃ¡c lá»�i giáº£i, giÃ¡ trá»‹ Ä‘áº§u,giÃ¡ trá»‹ cuá»‘i
    output: 
    */
    
    public void sortSolution(ArrayList<int[]> solution,int begin,int end){
        if (begin<end) {
            int partitionIndex = partition(solution, begin, end);		
            sortSolution(solution, begin, partitionIndex-1);
            sortSolution(solution, partitionIndex+1, end);
	}
    }
    
    public int partition(ArrayList<int[]> solution, int begin, int end){
    	double pivot = violations(solution.get(end))[5];
	int i = begin -1;
	for (int j=begin; j<end; j++) {
            if (violations(solution.get(j))[5] < pivot) {
		i++;		
		Collections.swap(solution, i, j);
            }
	}	
	Collections.swap(solution, i+1, end);	
	return i+1;
    }
    
    /*
    In káº¿t quáº£
    input: máº£ng tÆ°Æ¡ng á»©ng vá»›i lá»�i giáº£i
    output: 
    */
    
    public void printResult(int[] solutions){
        ArrayList<ArrayList<Integer>> binUse = new ArrayList<ArrayList<Integer>>(m);
        binUse = getBinUse(solutions);
        System.out.println("violations weight: "+violations(solutions)[0]);
        System.out.println("violations minload: "+violations(solutions)[1]);
        System.out.println("violations P: "+violations(solutions)[2]);
        System.out.println("violations Type: "+violations(solutions)[3]);
        System.out.println("violations Class: "+violations(solutions)[4]);
        System.out.println("Sum violations: "+violations(solutions)[5]);
        for(int i=0;i<binUse.size();i++){
            System.out.println("Bin "+i+" have items: ");
            for(int j:binUse.get(i)){
                System.out.print(j+" ");
            }
            System.out.print("\n"); 
        }
    }

    public static void main(String[] args){
    	
        int num = 500;
        int k=0;
        ArrayList<int[]> solutions = new ArrayList<int[]>(500);
        GA ga = new GA();
        double [] violations = new double[ga.population];
        int flag =0;
        //String dataset_path = "dataset/test.json";
        String dataset_path = "src\\khmtk60\\miniprojects\\multiknapsackminmaxtypeconstraints\\MinMaxTypeMultiKnapsackInput-1000.json";
        ga.loadData(dataset_path);
        
        solutions = ga.initPopulations();
       
        for(int i =0;i< num;i++){
            solutions = ga.generateSolutions(solutions,ga.population);
            
            ga.sortSolution(solutions,0,ga.population-1);
            
            System.out.println("Step: "+(i+1)+",Minviolations: "+ga.violations(solutions.get(0))[5]);
            for(int j =0;j<ga.population;j++){
                violations[i] = ga.violations(solutions.get(j))[5];
                if(violations[i] == 0){
                    flag = 1;
                    break;
                }
            }
            if(flag ==1) break;
        }
        ga.printResult(solutions.get(0));
        System.out.println("Use GreedyAlgorithm:");
        ga.greedyAlgorithm(solutions.get(0));
    }
    
    
}
