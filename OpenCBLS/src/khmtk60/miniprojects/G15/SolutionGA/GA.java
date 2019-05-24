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
    public final int population = 500; //số lượng cá thể của quần thể
    public int n; // số lượng item
    public int m; // số lượng bin
    public MinMaxTypeMultiKnapsackInputItem items[];
    public MinMaxTypeMultiKnapsackInputBin bins[];
    Random r;
   
    /*
    Đọc dữ liệu
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
    Khởi tạo quần thể ban đầu
    input:
    output: Danh sách gồm 500 cá thể,mỗi cá thể là 1 lời giải của bài toán
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
    Tính toán violation của lời giải
    input: một lời giải của bài toán
    output: mảng gồm 6 phần tử chứa các violation: violation weight,
    violation minload,violation P,violation T,violation R,Tổng các violation.
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
    Xác định từng bin chứa các item nào
    input: Mảng chứa lời giải của bài toán
    output: Danh sách m bin, mỗi bin chứa 1 danh sách các item
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
    Lai ghép 2 cá thể bố,mẹ bất kì để tạo ra 2 cá thể con tương ứng
    input: 2 mảng tương ứng với 2 lời giải.
    output: Danh sách 2 lời giải mới tương ứng
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
    Đột biến 1 cá thể bất kì để tạo ra cá thể con mới
    input: mảng tương ứng với lời giải.
    output: Danh sách lời giải mới tương ứng
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
    Chọn lọc 50% số cá thể con có hàm fitness tốt nhất và 50%
    cá thể bố mẹ ngẫu nhiên để tạo ra 500 cá thể cho lần lai ghép,đột biến tiếp theo
    input: Danh sách các lời giải bố mẹ và danh sách các lời giải con.
    output: Danh sách các lời giải mới tương ứng với quần thể mới.
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
    Kết hợp các phương pháp lai ghép,đột biến,chọn lọc để tạo ra quần thể mới
    input: Danh sách các lời giải tương ứng với quần thể, kích thước quần thể cần tạo
    output: Danh sách các lời giải tương ứng với quần thể mới
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
    Tính violation tương ứng với mỗi bin
    input: chỉ số của bin, danh sách các item chứa trong bin đó
    output: tổng số violation của mỗi bin
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
    Thuật toán tham lam loại bỏ, thêm các item vào các bin sao cho violation
    của mỗi bin hội tụ về 0
    input:  mảng tương ứng với lời giải hay cá thể.
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
    Sắp xếp các lời giải tương ứng với giá trị violation để chọn ra
    các lời giải có violation nhỏ nhất theo thuật toán sắp xếp nhanh
    input: Danh sách các lời giải, giá trị đầu,giá trị cuối
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
    In kết quả
    input: mảng tương ứng với lời giải
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
