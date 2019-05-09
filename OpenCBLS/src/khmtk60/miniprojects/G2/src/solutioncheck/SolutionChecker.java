package solutioncheck;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashSet;

import com.google.gson.Gson;

public class SolutionChecker {

	public String check(String inputJson, String outputJson){
		try{
			System.out.println(inputJson);
			Reader readerInput = new FileReader(inputJson);
			Reader readerOutput = new FileReader(outputJson);
			Gson gson = new Gson();
			MinMaxTypeMultiKnapsackInput I = gson.fromJson(readerInput, MinMaxTypeMultiKnapsackInput.class);
			MinMaxTypeMultiKnapsackSolution S = gson.fromJson(readerOutput, MinMaxTypeMultiKnapsackSolution.class);
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
				System.out.println("b = " + b);
				System.out.println("loadP = " + loadP[b]);
				System.out.println("P = " + bins[b].getP());
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
		SolutionChecker checker = new SolutionChecker();
		System.out.println(checker.check("result_submit/input-3000.json", "result_submit/output-3000.json"));
//		System.out.println(checker.check("./dataset/submit/MinMaxTypeMultiKnapsackInput-3000-input.json", "./dataset/submit/MinMaxTypeMultiKnapsackInput-3000-output.json"));
	}

}
