package mkpSolver;

import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;


import com.google.gson.Gson;

public class MinMaxTypeMultiKnapsackInput {
	private MinMaxTypeMultiKnapsackInputItem[] items;
	private MinMaxTypeMultiKnapsackInputBin[] bins;
	
	
	public MinMaxTypeMultiKnapsackInput(
			MinMaxTypeMultiKnapsackInputItem[] items,
			MinMaxTypeMultiKnapsackInputBin[] bins) {
		super();
		this.items = items;
		this.bins = bins;
	}


	public MinMaxTypeMultiKnapsackInputItem[] getItems() {
		return items;
	}


	public void setItems(MinMaxTypeMultiKnapsackInputItem[] items) {
		this.items = items;
	}


	public MinMaxTypeMultiKnapsackInputBin[] getBins() {
		return bins;
	}


	public void setBins(MinMaxTypeMultiKnapsackInputBin[] bins) {
		this.bins = bins;
	}


	public MinMaxTypeMultiKnapsackInput() {
		super();
		// TODO Auto-generated constructor stub
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
	public MinMaxTypeMultiKnapsackInput loadFromFile(String fn){
		try{
			Gson gson = new Gson();
			Reader reader = new FileReader(fn);
			MinMaxTypeMultiKnapsackInput I = gson.fromJson(reader, MinMaxTypeMultiKnapsackInput.class);
			return I;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
}
