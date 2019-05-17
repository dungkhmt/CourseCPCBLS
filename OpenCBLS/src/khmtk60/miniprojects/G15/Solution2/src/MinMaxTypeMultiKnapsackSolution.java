package src;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import com.google.gson.Gson;
public class MinMaxTypeMultiKnapsackSolution {
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
		MinMaxTypeMultiKnapsackInput input1 = MinMaxTypeMultiKnapsackInput.loadFromFile("./dataset/MinMaxTypeMultiKnapsackInput-3000.json");
		input1.writeToFile("./dataset/submit/MinMaxTypeMultiKnapsackInput-3000-input.json");
		MinMaxTypeMultiKnapsackSolution sol1 = new MinMaxTypeMultiKnapsackSolution();
		sol1.loadSubmit("./dataset/submit/MinMaxTypeMultiKnapsackInput-3000.out");
		sol1.writeToFile("./dataset/submit/MinMaxTypeMultiKnapsackInput-3000-output.json");

		MinMaxTypeMultiKnapsackInput input2 = MinMaxTypeMultiKnapsackInput.loadFromFile("./dataset/MinMaxTypeMultiKnapsackInput-1000.json");
		input2.writeToFile("./dataset/submit/MinMaxTypeMultiKnapsackInput-1000-input.json");
		MinMaxTypeMultiKnapsackSolution sol2 = new MinMaxTypeMultiKnapsackSolution();
		sol2.loadSubmit("./dataset/submit/MinMaxTypeMultiKnapsackInput-1000.out");
		sol2.writeToFile("./dataset/submit/MinMaxTypeMultiKnapsackInput-1000-output.json");
	}

}
