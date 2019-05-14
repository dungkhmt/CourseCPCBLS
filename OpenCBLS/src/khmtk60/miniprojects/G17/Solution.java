package khmtk60.miniprojects.G17;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;

public class Solution {
	public Decompose loadFromFile(String fn) {
		try {
			Gson gson = new Gson();
			Reader reader = new FileReader(fn);
			Decompose D = gson.fromJson(reader, Decompose.class);
			return D;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void checkSolution(int[] binOfItems, String fn) {
		MinMaxTypeMultiKnapsackInput a = new MinMaxTypeMultiKnapsackInput();
		MinMaxTypeMultiKnapsackInput input = a.loadFromFile(fn);
		MinMaxTypeMultiKnapsackSolution solution = new MinMaxTypeMultiKnapsackSolution();
		solution.setBinOfItem(binOfItems);
		SolutionChecker SC = new SolutionChecker();
		System.out.println(SC.check(input, solution));
	}

	public void saveAsJson(Object a, String path) {
		Gson gson = new Gson();
		String json = gson.toJson(a);

		try {
			BufferedWriter writer;
			writer = new BufferedWriter(new FileWriter(path));
			writer.write(json);
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// item rieng le search 1
	public void solution1(int size) {
		String separatedInput = "src/khmtk60/miniprojects/G17/data/MinMaxTypeMultiKnapsackInput-" + size + ".json";

		State st = new State();

		st.initialize(separatedInput);

		int[] finalSolution = st.search();

		checkSolution(finalSolution, separatedInput);

		MinMaxTypeMultiKnapsackSolution solution = new MinMaxTypeMultiKnapsackSolution();
		solution.setBinOfItem(finalSolution);

		saveAsJson(solution, "solution1-" + size + ".json");
	}

	// item group search 1
	public void solution2(int size) {
		String groupInput = "src/khmtk60/miniprojects/G17/data/group-" + size + ".json";
		String separatedInput = "src/khmtk60/miniprojects/G17/data/MinMaxTypeMultiKnapsackInput-" + size + ".json";
		String decomposeInput = "src/khmtk60/miniprojects/G17/data/decompose-" + size + ".json";
		State st = new State();

		st.initialize(groupInput);
		int[] binOfItems = st.search();

		Decompose D = loadFromFile(decomposeInput);

		checkSolution(binOfItems, groupInput);

		int[] decomposedBinOfItems = D.decompose(binOfItems);

		st.initialize(separatedInput);
		st.loadBinOfItems(decomposedBinOfItems);
		int[] finalSolution = st.search();

		checkSolution(finalSolution, separatedInput);

		MinMaxTypeMultiKnapsackSolution solution = new MinMaxTypeMultiKnapsackSolution();
		solution.setBinOfItem(finalSolution);

		saveAsJson(solution, "solution2-" + size + ".json");
	}

	// item rieng le search 2
	public void solution3() {

	}

	// item group search 2
	public void solution4() {

	}

	public static void main(String[] args) {
		Solution s = new Solution();
		// s.solution1(1000);
		// s.solution1(3000);
		s.solution2(1000);
		// s.solution2(3000);
	}
}
