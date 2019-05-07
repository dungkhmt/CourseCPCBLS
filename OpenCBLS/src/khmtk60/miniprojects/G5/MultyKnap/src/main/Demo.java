package main;

import model.MiniProject;

public class Demo {
	public static void main(String ...args) throws InterruptedException {
		MiniProject project = new MiniProject();
		project.loadData("data/MinMaxTypeMultiKnapsackInput-1000.json");
		project.initState();
		project.setDistTime(90000);
		project.search(0);
		project.printSolution("data/output-733_1000.json");
	}
}
