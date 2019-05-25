package solution.model;

import java.io.PrintWriter;

import com.google.gson.Gson;

public class MinMaxTypeMultiKnapsackSolution {
	int X[];
	public MinMaxTypeMultiKnapsackSolution(int x[]) {
		this.X = x;
	}
	public int[] getSolution() {
		return X;
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
}
