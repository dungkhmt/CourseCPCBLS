package khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.*;

public class MinMaxTypeMultiKnapsackSolution {
	MinMaxTypeMultiKnapsackInput I;
	public int numI; // The number of items
	public int numBin; // The number of bins
	int[] numTypeInBin; // The number of type of Items in each bin
	int[] numItemsInBin; // The number of Items in each bin
	int[] numClassInBin; // The number of class of Items in each bin
	double sumVol = Double.MAX_VALUE;
	public static MinMaxTypeMultiKnapsackInputItem[] items; // list of Items
	public static MinMaxTypeMultiKnapsackInputBin[] bins; // List of Bins
	double[] sumW; // sum of the first weight W corresponding to each Items
	double[] sumP; // sum of the first weight P corresponding to each Items
	HashMap<Integer, Integer> typePerBin[];
	HashMap<Integer, Double> SumWInbin = new HashMap<>();
	private int[] binOfItem;// binOfItem[i] = -1: item i not scheduled

	public int[] getBinOfItem() {
		return binOfItem;
	}

	public void setBinOfItem(int[] binOfItem) {
		this.binOfItem = binOfItem;
	}

	/**
	 * \ calculate the number of violations
	 * 
	 * @param solution
	 * @return numViioluation
	 */
	public double calViolation(int[] solution, int numBin, int numI) {
		double sumVio = 0;
		double[] sumW = new double[numBin];
		double[] sumP = new double[numBin];
		int[] sumClass = new int[numBin];
		int[] sumType = new int[numBin];
		ArrayList<HashSet<Integer>> types = new ArrayList<>();
		ArrayList<HashSet<Integer>> classes = new ArrayList<>();
		// initialize the empty types and classes
		for (int i = 0; i < numBin; i++) {
			types.add(new HashSet<Integer>());
			classes.add(new HashSet<Integer>());
		}

		for (int i = 0; i < numI; i++) {
			// constraint not in binIndices
			int bin = solution[i];
			int[] binIndices = items[i].getBinIndices();

			boolean flag = false;
			for (int j = 0; j < binIndices.length; j++) {
				if (bin == binIndices[j]) {
					flag = true;
				}
			}
			if (!flag) {
				// increase amount of violation if bin not in Bin Indices
				sumVio++;
			}

			sumW[bin] += items[i].getW(); // calculate sum of weight 1 W for
											// each bin
			sumP[bin] += items[i].getP(); // calculate sum of weight 2 P for
											// each bin
			// calculate sum of type per bin
			int t = items[i].getT();
			int r = items[i].getR();
			if (!types.get(bin).contains(t)) {
				sumType[bin]++;
				types.get(bin).add(t);
			}
			// calculate sum of type per bin
			if (!classes.get(bin).contains(t)) {
				sumClass[bin]++;
				classes.get(bin).add(t);
			}

		}
		// Calculate the violations of constraints
		for (int i = 0; i < numBin; i++) {
			if (sumW[i] - bins[i].getCapacity() > 0) {
				// sumVio++;
				sumVio += sumW[i] - bins[i].getCapacity();
			}
			if (sumP[i] - bins[i].getP() > 0) {
				// sumVio++;
				sumVio += sumP[i] - bins[i].getP();
			}
			if (sumType[i] - bins[i].getT() > 0) {
				// sumVio++;
				sumVio += sumType[i] - bins[i].getT();
			}
			if (sumClass[i] - bins[i].getR() > 0) {
				// sumVio++;
				sumVio += sumClass[i] - bins[i].getR();
			}
			if (bins[i].getMinLoad() - sumW[i] > 0) {
				sumVio += 0.5;
			}
		}
		return sumVio;
	}

	public double check(MinMaxTypeMultiKnapsackInput I, int[] solution) {
		int nbBins = I.getBins().length;
		double[] loadWeight = new double[nbBins];
		double[] loadP = new double[nbBins];
		HashSet<Integer>[] loadType = new HashSet[nbBins];
		HashSet<Integer>[] loadClass = new HashSet[nbBins];

		for (int b = 0; b < nbBins; b++) {
			loadWeight[b] = 0;
			loadP[b] = 0;
			loadType[b] = new HashSet<Integer>();
			loadClass[b] = new HashSet<Integer>();
		}
		int nbItemNotScheduled = 0;
		double violations = 0;
		int[] X = solution;

		MinMaxTypeMultiKnapsackInputItem[] items = I.getItems();
		MinMaxTypeMultiKnapsackInputBin[] bins = I.getBins();

		String description = "";
		for (int i = 0; i < X.length; i++) {
			if (X[i] < 0 || X[i] >= nbBins) {
				nbItemNotScheduled++;
			} else {
				int b = X[i];
				loadWeight[b] += items[i].getW();
				loadP[b] += items[i].getP();
				loadType[b].add(items[i].getT());
				loadClass[b].add(items[i].getR());

				boolean ok = false;
				for (int j = 0; j < items[i].getBinIndices().length; j++) {
					if (b == items[i].getBinIndices()[j]) {
						ok = true;
						break;
					}
				}
				if (!ok) {
					violations++;
				}
			}
		}
		int violationW = 0;
		int violationP = 0;
		int violationT = 0;
		int violationR = 0;
		for (int b = 0; b < nbBins; b++) {
			boolean flag = false;
			if (loadWeight[b] > 0 && (loadWeight[b] > bins[b].getCapacity())) {
				violations++;
				violationW++;
//				violations+=loadWeight[b]  -bins[b].getCapacity();
			}
			if (loadWeight[b] > 0 && loadWeight[b] < bins[b].getMinLoad()) {
				violations++;
				violationW++;
//				violations+= 0.5;
				
			}
			if (loadP[b] > bins[b].getP()) {
				violations++;
				violationP++;
//				violations+= loadP[b] - bins[b].getP();
			}
			if (loadType[b].size() > bins[b].getT()) {
				violations++;
				violationT++;
//				violations+= loadType[b].size() -bins[b].getT();
			}
			if (loadClass[b].size() > bins[b].getR()) {
				violations++;
				violationR++;
//				violations+=loadClass[b].size() - bins[b].getR();
			}

		}
		// description = "items-not-scheduled: " + nbItemNotScheduled + ",\n"
		// + " violations: " + violations + ",\n"
		// + " violationW: " + violationW + ", \n"
		// + " violationP: " + violationP + ", \n"
		// + " violationT: " + violationT + ", \n"
		// + " violationR: " + violationR + ", \n";
		//
		return violations;
	}

	public int calItemsInSolution(int[] solution) {
		int numItemsInBin = 0;
		double[] sumW = new double[numBin];
		double[] sumP = new double[numBin];
		int[] sumClass = new int[numBin];
		int[] sumType = new int[numBin];
		int[] numItems = new int[numBin];
		ArrayList<HashSet<Integer>> types = new ArrayList<>();
		ArrayList<HashSet<Integer>> classes = new ArrayList<>();
		// initialize the empty types and classes
		for (int i = 0; i < numBin; i++) {
			types.add(new HashSet<Integer>());
			classes.add(new HashSet<Integer>());
		}

		for (int i = 0; i < numI; i++) {
			// constraint not in binIndices
			int bin = solution[i];
			int[] binIndices = items[i].getBinIndices();

			boolean flag = false;
			for (int j = 0; j < binIndices.length; j++) {
				if (bin == binIndices[j]) {
					flag = true;
				}
			}
			if (!flag) {
				// increase amount of violation if bin not in Bin Indices

			}

			sumW[bin] += items[i].getW(); // calculate sum of weight 1 W for
			numItems[bin]++; // each bin
			sumP[bin] += items[i].getP(); // calculate sum of weight 2 P for
											// each bin
			// calculate sum of type per bin
			int t = items[i].getT();
			int r = items[i].getR();
			if (!types.get(bin).contains(t)) {
				sumType[bin]++;
				types.get(bin).add(t);
			}
			// calculate sum of type per bin
			if (!classes.get(bin).contains(t)) {
				sumClass[bin]++;
				classes.get(bin).add(t);
			}

		}
		// Calculate the violations of constraints
		for (int i = 0; i < numBin; i++) {
			if ((sumW[i] - bins[i].getCapacity() > 0) || (sumP[i] - bins[i].getP() > 0)
					|| (sumType[i] - bins[i].getT() > 0) || (sumClass[i] - bins[i].getR() > 0)
					|| (bins[i].getMinLoad() - sumW[i] > 0)) {

				numItemsInBin += numItems[i];

			} else {
				// numItemsInBin += numItems[i];
			}
		}
		return numItemsInBin;
	}

	public int calItems(MinMaxTypeMultiKnapsackInput I, int[] solution) {
		int nbBins = I.getBins().length;
		double[] loadWeight = new double[nbBins];
		double[] loadP = new double[nbBins];
		int[] numItems = new int[nbBins];
		int numvioITems = 0;
		HashSet<Integer>[] loadType = new HashSet[nbBins];
		HashSet<Integer>[] loadClass = new HashSet[nbBins];

		for (int b = 0; b < nbBins; b++) {
			loadWeight[b] = 0;
			loadP[b] = 0;
			loadType[b] = new HashSet<Integer>();
			loadClass[b] = new HashSet<Integer>();
		}
		int nbItemNotScheduled = 0;
		double violations = 0;
		int[] X = solution;

		MinMaxTypeMultiKnapsackInputItem[] items = I.getItems();
		MinMaxTypeMultiKnapsackInputBin[] bins = I.getBins();

		String description = "";
		for (int i = 0; i < X.length; i++) {
			if (X[i] < 0 || X[i] >= nbBins) {
				nbItemNotScheduled++;
			} else {
				int b = X[i];
				loadWeight[b] += items[i].getW();
				loadP[b] += items[i].getP();
				loadType[b].add(items[i].getT());
				loadClass[b].add(items[i].getR());
				numItems[b]++;

				boolean ok = false;
				for (int j = 0; j < items[i].getBinIndices().length; j++) {
					if (b == items[i].getBinIndices()[j]) {
						ok = true;
						break;
					}
				}
				if (!ok) {
					violations++;
				}
			}
		}
		int violationW = 0;
		int violationP = 0;
		int violationT = 0;
		int violationR = 0;
//		for (int b = 0; b < nbBins; b++) {
//			boolean flag = false;
//			if (loadWeight[b] > 0 && (loadWeight[b] > bins[b].getCapacity()) && !flag) {
//				numvioITems += numItems[b];
//				flag = true;
//			}
//			if (loadWeight[b] > 0 && loadWeight[b] < bins[b].getMinLoad() && !flag) {
//				numvioITems += numItems[b];
//				flag = true;
//			}
//			if (loadP[b] > bins[b].getP() && !flag) {
//				numvioITems += numItems[b];
//				flag = true;
//			}
//			if (loadType[b].size() > bins[b].getT() && !flag) {
//				numvioITems += numItems[b];
//				flag = true;
//			}
//			if (loadClass[b].size() > bins[b].getR() && !flag) {
//				numvioITems += numItems[b];
//				flag = true;
//			}
//
//		}
		for (int b = 0; b < nbBins; b++) {
//			System.out.println(numItems[b]);
//			if (loadWeight[b] > 0 && (loadWeight[b] > bins[b].getCapacity())) {
//				violations++;
//				violationW++;
//				numvioITems+=numItems[b];
//				continue;
//			}
			if (loadWeight[b] > 0 && loadWeight[b] < bins[b].getMinLoad()) {
				violations++;
				violationW++;
				numvioITems+=numItems[b];
				continue;
			}
//			if (loadP[b] > bins[b].getP()) {
//				violations++;
//				violationP++;
//				numvioITems+=numItems[b];
//				continue;
//			}
//			if (loadType[b].size() > bins[b].getT()) {
//				violations++;
//				violationT++;
//				numvioITems+=numItems[b];
//				continue;
//			}
//			if (loadClass[b].size() > bins[b].getR()) {
//				violations++;
//				violationR++;
//				numvioITems+=numItems[b];
//				continue;
//			}
//			System.out.println(numvioITems);

		}
		 description = "items-not-scheduled: " + nbItemNotScheduled + ",\n"
		 + " violations: " + violations + ",\n"
		 + " violationW: " + violationW + ", \n"
		 + " violationP: " + violationP + ", \n"
		 + " violationT: " + violationT + ", \n"
		 + " violationR: " + violationR + ", \n";
		System.out.println(description);
		return numvioITems;
	}

	public void readData(String path) {
		// path =
		// "src/khmtk60/miniprojects/multiknapsackminmaxtypeconstraints/MinMaxTypeMultiKnapsackInput-3000.json";
		// load Data from file.
		I = MinMaxTypeMultiKnapsackInput.loadFromFile(path);
		bins = I.getBins();
		items = I.getItems();
		numI = items.length;
		numBin = bins.length;
		System.out.println("items indices");
		for (int j = 0; j < items.length; j++) {
			System.out.println(j + "\t" + items[j].getBinIndices().length);
			// for (int j2 = 0; j2 < items[j].getBinIndices().length; j2++) {
			// System.out.print(items[j].getBinIndices()[j2] + ",");
			// }
			// System.out.println();
		}
	}

	/**
	 * 
	 * @param numI
	 * @param r
	 * @param popSize
	 * @return
	 */
	public ArrayList<Individual> initialPopulation(int numI, Random r, int popSize) {
		ArrayList<Individual> population = new ArrayList<>();
		for (int i = 0; i < popSize; i++) {
			int[] solution = new int[numI];
			for (int j = 0; j < numI; j++) {
				int[] binIndices = items[j].getBinIndices();
				solution[j] = binIndices[r.nextInt(items[j].getBinIndices().length)];
				// solution[j] = r.nextInt(numBin);
			}
			Individual indv = new Individual();
			indv.setChromosome(solution);
			// indv.setFitness(calViolation(solution, numBin, numI));
			indv.setFitness(check(I, solution));
			population.add(indv);
		}
		return population;
	}

	public int[] stepHighestClimbing(int numI, int[] solution, Random r) {
		// double bestFitness = calViolation(solution, numBin, numI);
		double bestFitness = check(I, solution);
		// System.out.println(bestFitness);
		int[] copysol = new int[numI];
		for (int i = 0; i < numI; i++) {
			copysol[i] = solution[i];
		}
		int[] move = new int[2];
		move[0] = -1;
		move[1] = -1;
		for (int j = 0; j < numI; j++) {
			int[] binIndices = items[j].getBinIndices();

			// for (int i = 0; i < binIndices.length; i++) {
			// if (solution[j] != binIndices[i]) {
			// copysol[j] = binIndices[i];
			// if (calViolation(copysol, numBin, numI) < bestFitness) {
			// bestFitness = calViolation(copysol, numBin, numI);
			// move[0] = j;
			// move[1] = binIndices[i];
			// System.out.println(bestFitness);
			// }
			// }
			//
			// }
			int index = r.nextInt(binIndices.length);
			if (binIndices.length < 2) {
				continue;
			}
			while (solution[j] == binIndices[index]) {
				index = r.nextInt(binIndices.length);
			}
			copysol[j] = binIndices[index];
			// if (calViolation(copysol, numBin, numI) < bestFitness) {
			if (check(I, copysol) < bestFitness) {
				// bestFitness = calViolation(copysol, numBin, numI);
				bestFitness = check(I, copysol);
				move[0] = j;
				move[1] = binIndices[index];
				System.out.println(bestFitness);
			}

			copysol[j] = solution[j];
		}
		if (move[0] != -1) {
			copysol[move[0]] = move[1];
		}
		return copysol;
	}

	/**
	 * 
	 * @param numI
	 * @param solution
	 * @param maxInter
	 * @return
	 */
	public int[] climbing(int numI, int[] solution, int maxInter, Random r) {
		// double bestFitness = calViolation(solution, numBin, numI);
		double bestFitness = check(I, solution);
		int inter = 0;
		while (inter < maxInter) {
			solution = stepHighestClimbing(numI, solution, r);
			// if(calViolation(solution, numBin, numI) <bestFitness){
			if (check(I, solution) < bestFitness) {
				// bestFitness = calViolation(solution, numBin, numI);
				bestFitness = check(I, solution);
				inter = 0;
			} else {
				inter++;
			}
		}
		return solution;
	}

	public void test() {
		MinMaxTypeMultiKnapsackSolution minMaxType = new MinMaxTypeMultiKnapsackSolution();
		MinMaxTypeMultiKnapsackInput I = MinMaxTypeMultiKnapsackInput
				.loadFromFile("src/khmtk60/miniprojects/multiknapsackminmaxtypeconstraints/model/41525782483156.json");
		MinMaxTypeMultiKnapsackInputBin[] bins = I.getBins();
		MinMaxTypeMultiKnapsackInputItem[] items = I.getItems();
		System.out.println("items indices");
		for (int j = 0; j < items.length; j++) {
			System.out.print(j + "\t");
			for (int j2 = 0; j2 < items[j].getBinIndices().length; j2++) {
				System.out.print(items[j].getBinIndices()[j2] + ",");
			}
			System.out.println();
		}
		System.out.println(items.length);
		System.out.println(bins.length);
	}

	/**
	 * 
	 * @param numI
	 * @param popSize
	 * @param numGeneration
	 * @param muRate
	 * @param crossRate
	 * @param r
	 */

	public void GA(int numI, int popSize, int numGeneration, double muRate, double crossRate, Random r) {
		ArrayList<Individual> pop = new ArrayList<>();
		pop = initialPopulation(numI, r, popSize);
		for (int i = 0; i < numGeneration; i++) {
			ArrayList<Individual> offPop = new ArrayList<>();
			double[] popFitness = populationFitness(pop);
			// get the index of the best Individual of old Population
			double bestFiness = popFitness[0];
			int bestFinessIndex = 0;
			for (int t = 0; t < popSize; t++) {
				if (popFitness[t] < bestFiness) {
					bestFiness = popFitness[t];
					bestFinessIndex = t;
				}

			}

			for (int j = 0; j < popSize / 2; j++) {
				// int father = touranmentSelection(popFitness, 2, popSize, r);
				int father = bestFinessIndex;
				int mother = touranmentSelection(popFitness, 2, popSize, r);
				while (father == mother) {
					mother = touranmentSelection(popFitness, 2, popSize, r);
				}
				Individual offs1 = new Individual();
				Individual offs2 = new Individual();
				double ran = r.nextDouble();

				if (ran < crossRate) {
					ArrayList<Individual> childs = onePointCrossover(pop.get(father), pop.get(mother), r);
					offs1 = childs.get(0);
					offs2 = childs.get(1);
					if (ran < muRate) {
						offs1 = mutation(offs1, muRate, numI, r);
						offs2 = mutation(offs2, muRate, numI, r);
					}
					offPop.add(offs1);
					offPop.add(offs2);
				} else {
					offPop.add(pop.get(father));
					offPop.add(pop.get(mother));
				}

			}
//			System.out.println(bestFinessIndex);
			int numITems = calItems(I, pop.get(bestFinessIndex).getChromosome());
			System.out.println("Gen " + i + ":\t " + bestFiness + ":\t" + numITems);

			// printSolution( pop.get(bestFinessIndex).getChromosome());
//			 pop.get(bestFinessIndex).setChromosome(climbing(numI,
//			 pop.get(bestFinessIndex).getChromosome(), 20, r));
			// add the best Individual of old Population to new
			// Population
			offPop.set(r.nextInt(popSize), pop.get(bestFinessIndex));
			pop = offPop;
		}
		double[] popFitness = populationFitness(pop);
		double bestFiness = popFitness[0];
		int bestFinessIndex = 0;
		for (int t = 0; t < popSize; t++) {
			if (popFitness[t] < bestFiness) {
				bestFiness = popFitness[t];
				bestFinessIndex = t;
			}

		}
		filePrintOut(pop.get(bestFinessIndex).getChromosome(),"1000.txt" );
		System.out.println("Best Fitness:\t " + bestFiness);
	}

	public ArrayList<Individual> onePointCrossover(Individual par1, Individual par2, Random r) {
		ArrayList<Individual> offsprings = new ArrayList<>();
		int[] offs1 = new int[numI];
		int[] offs2 = new int[numI];
		int point = r.nextInt(numI);
		for (int i = 0; i < numI; i++) {
			if (i < point) {
				offs1[i] = par1.getChromosome()[i];
				offs2[i] = par2.getChromosome()[i];
			} else {
				offs1[i] = par2.getChromosome()[i];
				offs2[i] = par1.getChromosome()[i];
			}
		}
		Individual indiv1 = new Individual();
		Individual indiv2 = new Individual();
		indiv1.setChromosome(offs1);
		indiv2.setChromosome(offs2);
		offsprings.add(indiv1);
		offsprings.add(indiv2);
		return offsprings;
	}

	/**
	 * 
	 * @param par
	 * @param muRate
	 * @param numI
	 * @param r
	 * @return
	 */
	public Individual mutation(Individual par, double muRate, int numI, Random r) {
		int index = r.nextInt(numI);
		int[] choromosome = par.getChromosome();
		int curBin = choromosome[index];
		int[] binIndices = items[index].getBinIndices();
		while (binIndices.length < 1) {
			index = r.nextInt(numI);
			curBin = choromosome[index];
			binIndices = items[index].getBinIndices();
		}
		int bin = r.nextInt(binIndices.length);
		while (binIndices[bin] == curBin) {
			bin = r.nextInt(binIndices.length);
		}
		choromosome[index] = binIndices[bin];
		par.setChromosome(choromosome);
		return par;
	}

	/**
	 * 
	 * @param pop
	 * @return
	 */

	public double[] populationFitness(ArrayList<Individual> pop) {
		int popSize = pop.size();
		double[] popFitness = new double[popSize];
		for (int i = 0; i < popSize; i++) {
			// popFitness[i] = calViolation(pop.get(i).getChromosome(), numBin,
			// numI);
			popFitness[i] = check(I, pop.get(i).getChromosome());
		}
		// System.out.println();
		return popFitness;
	}

	/**
	 * 
	 * @param populationfitness
	 * @param tournament_size
	 * @param popSize
	 * @param r
	 * @return
	 */
	public int touranmentSelection(double[] populationfitness, int tournament_size, int popSize, Random r) {
		int[] tournamentGroup = new int[tournament_size];

		for (int i = 0; i < tournament_size; i++) {
			tournamentGroup[i] = r.nextInt(popSize);
		}
		int index = tournamentGroup[0];
		double tempTour = populationfitness[tournamentGroup[0]];

		for (int i = 1; i < tournament_size; i++) {
			if (populationfitness[tournamentGroup[i]] < tempTour) {
				tempTour = populationfitness[tournamentGroup[i]];
				index = tournamentGroup[i];
			}
		}
		return index;

	}

	public void printSolution(int[] solution) {
		for (int i = 0; i < solution.length; i++) {
			System.out.print(solution[i] + ",");
		}
		System.out.println();
	}
	public int[] readFileOut(String fileName){
		int[] solution = new int[numI];
		Scanner sc;
		try {
			sc = new Scanner(new File(fileName));
			for (int i = 0; i < numI; i++) {
				solution[i] = sc.nextInt();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return solution;
	}
	public void filePrintOut(int[] solution , String fileName){
		PrintWriter  pw = null;
		try {
			pw= new PrintWriter(new FileWriter(fileName), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < numI; i++) {
			pw.print(solution[i]+ " ");
		}
		pw.close();
		
	}

	public static void main(String[] args) {

		Random rand = new Random();
		rand.setSeed(4);
		int popSize = 300;
		String path = "MinMaxTypeMultiKnapsackInput-1000.json";
		String path1 = "src/khmtk60/miniprojects/multiknapsackminmaxtypeconstraints/model/MinMaxTypeMultiKnapsackInput-1000.json";
		String path2 = "src/khmtk60/miniprojects/multiknapsackminmaxtypeconstraints/model/50items.json";
		String out = "src/khmtk60/miniprojects/multiknapsackminmaxtypeconstraints/model/MinMaxTypeMultiKnapsackInput-1000.out";
		MinMaxTypeMultiKnapsackSolution minMaxType = new MinMaxTypeMultiKnapsackSolution();
		minMaxType.readData(path1);
//		minMaxType.GA(minMaxType.numI, popSize, 500, 0.3, 0.8, rand);
		int[] sol = minMaxType.readFileOut("1000.txt");
		for (int i = 0; i < sol.length; i++) {
			if(sol[i] < 0 ){
				
			}
			
		}
		System.out.println("Minload: "+ minMaxType.bins[0].getMinLoad());
		System.out.println("Capacity: " + minMaxType.bins[0].getCapacity());
		System.out.println("R: " + minMaxType.bins[0].getR());
		System.out.println("t: " + minMaxType.bins[0].getT());
		
		System.out.println(minMaxType.calItems(minMaxType.I, sol));
	
		// ArrayList<Individual> pop =
		// minMaxType.initialPopulation(minMaxType.numI, rand, popSize);
		//
		// double voil = minMaxType.calViolation(pop.get(0).getChromosome(),
		// minMaxType.numBin, minMaxType.numI);
		// System.out.println(voil);

	}

}
