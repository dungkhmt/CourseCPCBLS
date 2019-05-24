package khmtk60.miniprojects.G15.Solution2.src;

import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import com.google.gson.Gson;
public class MinMaxTypeMultiKnapsackSolutionSol2 {
	private int[] binOfItem;// binOfItem[i] = -1: item i not scheduled
	
	public int[] getBinOfItem() {
		return binOfItem;
	}

	public void setBinOfItem(int[] binOfItem) {
		this.binOfItem = binOfItem;
	}

	public void loadSubmit(String path) {
		File file = new File(path);
		
		try {
            Scanner sc = new Scanner(file);
            
            ArrayList<Integer> tmp = new ArrayList<Integer>();
            while (sc.hasNextInt()) {
            	tmp.add(sc.nextInt());
                
            }
            binOfItem = new int[tmp.size()];
            for(int i = 0; i < tmp.size(); i++) {
            	binOfItem[i] = tmp.get(i);
            }

            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

	}

	public void writeToFile(String fn){
		try{
			PrintWriter out = new PrintWriter(fn);
			Gson gson = new Gson();
			String json = gson.toJson(this);
			out.print(json);
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dataset3000 = "src/khmtk60/miniprojects/G15/Solution2/dataset/MinMaxTypeMultiKnapsackInput-3000.json";
		String inputObject3000 = "src/khmtk60/miniprojects/G15/Solution2/dataset/submit/MinMaxTypeMultiKnapsackInput-3000-input.json";
        String solutionObject3000 = "src/khmtk60/miniprojects/G15/Solution2/dataset/submit/MinMaxTypeMultiKnapsackInput-3000-output.json";
		String submitFile3000 = "src/khmtk60/miniprojects/G15/Solution2/dataset/submit/MinMaxTypeMultiKnapsackInput-3000.out";
		
		String dataset1000 = "src/khmtk60/miniprojects/G15/Solution2/dataset/MinMaxTypeMultiKnapsackInput-1000.json";
		String inputObject1000 = "src/khmtk60/miniprojects/G15/Solution2/dataset/submit/MinMaxTypeMultiKnapsackInput-1000-input.json";
        String solutionObject1000 = "src/khmtk60/miniprojects/G15/Solution2/dataset/submit/MinMaxTypeMultiKnapsackInput-1000-output.json";
		String submitFile1000 = "src/khmtk60/miniprojects/G15/Solution2/dataset/submit/MinMaxTypeMultiKnapsackInput-1000.out";
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
		MinMaxTypeMultiKnapsackSolutionSol2 sol1 = new MinMaxTypeMultiKnapsackSolutionSol2();
		sol1.loadSubmit(submitFile3000);
		sol1.writeToFile(solutionObject3000);

		MinMaxTypeMultiKnapsackInput input2 = MinMaxTypeMultiKnapsackInput.loadFromFile(dataset1000);
		input2.writeToFile(inputObject1000);
		MinMaxTypeMultiKnapsackSolutionSol2 sol2 = new MinMaxTypeMultiKnapsackSolutionSol2();
		sol2.loadSubmit(submitFile1000);
		sol2.writeToFile(solutionObject1000);
		System.out.println("Finishing");
	}
}
