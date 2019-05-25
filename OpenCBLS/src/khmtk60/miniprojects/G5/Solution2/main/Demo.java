package khmtk60.miniprojects.G5.Solution2.main;

import khmtk60.miniprojects.G5.Solution2.model.MiniProject;

public class Demo {
	public static void main(String ...args) throws InterruptedException {
		MiniProject project = new MiniProject();
		project.loadData("data/G5/data/MinMaxTypeMultiKnapsackInput-1000.json");
		project.initState();
		project.setDistTime(10000);
		project.search(0);
		project.printSolution("data/G5/data/output_1000.json");
		//best value in file output_1000_733
	}
}
