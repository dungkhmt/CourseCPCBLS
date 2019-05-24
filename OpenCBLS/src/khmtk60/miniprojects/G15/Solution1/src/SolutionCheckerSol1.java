package khmtk60.miniprojects.G15.Solution1.src;

import java.util.HashSet;
import java.io.FileReader;
import java.io.Reader;
import com.google.gson.Gson;

import khmtk60.miniprojects.G15.Solution1.src.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.G15.Solution1.src.MinMaxTypeMultiKnapsackInputBin;
import khmtk60.miniprojects.G15.Solution1.src.MinMaxTypeMultiKnapsackInputItem;
import khmtk60.miniprojects.G15.Solution1.src.MinMaxTypeMultiKnapsackSolutionSol1;

public class SolutionCheckerSol1 {

	public String check(String inputJson, String outputJson){
		try{
			System.out.println(inputJson);
			Reader readerInput = new FileReader(inputJson);
			Reader readerOutput = new FileReader(outputJson);
			Gson gson = new Gson();
			MinMaxTypeMultiKnapsackInput I = gson.fromJson(readerInput, MinMaxTypeMultiKnapsackInput.class);
			MinMaxTypeMultiKnapsackSolutionSol1 S = gson.fromJson(readerOutput, MinMaxTypeMultiKnapsackSolutionSol1.class);
			return check(I,S);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return "N/A";
	}
	public String check(MinMaxTypeMultiKnapsackInput I, MinMaxTypeMultiKnapsackSolutionSol1 S){
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
				
				if(!items[i].getBinIndices().contains(b)){
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
			}
			if(loadWeight[b] > 0 && loadWeight[b] < bins[b].getMinLoad()){
				violations++;
				violationW++;
			}
			if(loadP[b] > bins[b].getP()){
				System.out.println("bin co P am: " + b);
				System.out.println("loadP = " + loadP[b]);
				System.out.println("P = " + bins[b].getP());
				System.out.println("Dan toi mac dinh bi 1 violation ve P.");
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
		String dataset3000 = "src/khmtk60/miniprojects/G15/Solution1/dataset/MinMaxTypeMultiKnapsackInput-3000.json";
		String inputObject3000 = "src/khmtk60/miniprojects/G15/Solution1/dataset/submit/MinMaxTypeMultiKnapsackInput-3000-input.json";
        String solutionObject3000 = "src/khmtk60/miniprojects/G15/Solution1/dataset/submit/MinMaxTypeMultiKnapsackInput-3000-output.json";
		String submitFile3000 = "src/khmtk60/miniprojects/G15/Solution1/dataset/submit/MinMaxTypeMultiKnapsackInput-3000.out";
		
		String dataset1000 = "src/khmtk60/miniprojects/G15/Solution1/dataset/MinMaxTypeMultiKnapsackInput-1000.json";
		String inputObject1000 = "src/khmtk60/miniprojects/G15/Solution1/dataset/submit/MinMaxTypeMultiKnapsackInput-1000-input.json";
        String solutionObject1000 = "src/khmtk60/miniprojects/G15/Solution1/dataset/submit/MinMaxTypeMultiKnapsackInput-1000-output.json";
		String submitFile1000 = "src/khmtk60/miniprojects/G15/Solution1/dataset/submit/MinMaxTypeMultiKnapsackInput-1000.out";
        if(OSValidator.isWindows()) {
        	dataset3000 = dataset3000.replace("/","\\");
        	inputObject3000 = inputObject3000.replace("/","\\");
        	solutionObject3000 = solutionObject3000.replace("/","\\");
        	submitFile3000 = submitFile3000.replace("/","\\");
        	dataset1000 = dataset1000.replace("/","\\");
        	inputObject1000 = inputObject1000.replace("/","\\");
        	solutionObject1000 = solutionObject1000.replace("/","\\");
        	submitFile1000 = submitFile1000.replace("/","\\");
        	
        }
		MinMaxTypeMultiKnapsackInput input1 = MinMaxTypeMultiKnapsackInput.loadFromFile(dataset3000);
		input1.writeToFile(inputObject3000);
		MinMaxTypeMultiKnapsackSolutionSol1 sol1 = new MinMaxTypeMultiKnapsackSolutionSol1();
		sol1.loadSubmit(submitFile3000);
		sol1.writeToFile(solutionObject3000);

		MinMaxTypeMultiKnapsackInput input2 = MinMaxTypeMultiKnapsackInput.loadFromFile(dataset1000);
		input2.writeToFile(inputObject1000);
		MinMaxTypeMultiKnapsackSolutionSol1 sol2 = new MinMaxTypeMultiKnapsackSolutionSol1();
		sol2.loadSubmit(submitFile1000);
		sol2.writeToFile(solutionObject1000);

		// TODO Auto-generated method stub
		SolutionCheckerSol1 checker = new SolutionCheckerSol1();
        if(OSValidator.isWindows()) {
        	System.out.println(checker.check("src\\khmtk60\\miniprojects\\G15\\Solution1\\dataset\\submit\\MinMaxTypeMultiKnapsackInput-1000-input.json", "src\\khmtk60\\miniprojects\\G15\\Solution1\\dataset\\submit\\MinMaxTypeMultiKnapsackInput-1000-output.json"));
    		System.out.println(checker.check("src\\khmtk60\\miniprojects\\G15\\Solution1\\dataset\\submit\\MinMaxTypeMultiKnapsackInput-3000-input.json", "src\\khmtk60\\miniprojects\\G15\\Solution1\\dataset\\submit\\MinMaxTypeMultiKnapsackInput-3000-output.json"));
        } else {
        	System.out.println(checker.check("src/khmtk60/miniprojects/G15/Solution1/dataset/submit/MinMaxTypeMultiKnapsackInput-1000-input.json", "src/khmtk60/miniprojects/G15/Solution1/dataset/submit/MinMaxTypeMultiKnapsackInput-1000-output.json"));
    		System.out.println(checker.check("src/khmtk60/miniprojects/G15/Solution1/dataset/submit/MinMaxTypeMultiKnapsackInput-3000-input.json", "src/khmtk60/miniprojects/G15/Solution1/dataset/submit/MinMaxTypeMultiKnapsackInput-3000-output.json"));
        }
		
	}

}
