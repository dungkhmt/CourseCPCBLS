package khmtk60.miniprojects.G8;

import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.SolutionChecker;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputItem;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackSolution;

public class Demo_Greedy {
	MinMaxTypeMultiKnapsackInput input;
	int nItems;
    int nBins;
	MinMaxTypeMultiKnapsackSolution solution;
	Checker checker;
	SolutionChecker sC;
	
	public void init() {
		String inputFile = "./src/khmtk60/miniprojects/G8/InputOutputData/MinMaxTypeMultiKnapsackInput-3000.json";
        input = new MinMaxTypeMultiKnapsackInput().loadFromFile(inputFile);
		nItems = input.getItems().length;
        nBins = input.getBins().length;
        
        solution = new MinMaxTypeMultiKnapsackSolution();
        int[] binOfItem = new int[nItems];
        for(int i = 0; i < nItems; i++)
        	binOfItem[i] = -1;
        solution.setBinOfItem(binOfItem);
        
        checker = new Checker();
        sC = new SolutionChecker();
	}
	
	public void greedy() {
		int violationsInit = checker.violations(input, solution);
		for(int i = 0; i < nItems; i++) {
			MinMaxTypeMultiKnapsackInputItem[] items = input.getItems();
			for(int j = 0; j < items[i].getBinIndices().length; j++) {
				solution.getBinOfItem()[i] = items[i].getBinIndices()[j];
				int violations = checker.violations(input, solution);
				if(violations > violationsInit) {
					solution.getBinOfItem()[i] = -1;
				}else {
					System.out.println("binOfItem[" + i + "] = " + j);
					break;
				}
			}
		}
		
		// lay items ra khoi nhung bin bi vi pham rang buoc
		boolean[] checkBins = checker.checkBins(input, solution);
		for(int i = 0; i < nBins; i++)
			if(!checkBins[i]) {
				for(int j = 0; j < nItems; j++) {
					if(solution.getBinOfItem()[j] == i)
						solution.getBinOfItem()[j] = -1;
				}
			}
	}
	
	public void outputFile(MinMaxTypeMultiKnapsackSolution S) {
		Gson gson = new Gson();
		try (FileWriter writer = new FileWriter("./src/khmtk60/miniprojects/G8/outputGreedy.json")) {
            gson.toJson(S, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void solve() {
		init();
		greedy();
		outputFile(solution);
		System.out.println(sC.check(input, solution));
	}
	
	public static void main(String[] args) {
		long start, end;
		start = System.currentTimeMillis();
		Demo_Greedy d = new Demo_Greedy();
		d.solve();
		end = System.currentTimeMillis();
		System.out.println("Time Millis: " + (end - start));
	}
}