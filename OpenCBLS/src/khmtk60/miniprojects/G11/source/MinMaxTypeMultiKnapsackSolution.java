package khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.*;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.ArrayUtils;

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
	ArrayList<Integer> binUse;
	ArrayList<Integer> itemUse;

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
	public double violationCal(MinMaxTypeMultiKnapsackInput I, int[] solution) {
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
				// violations++;
				// violationW++;
				violations += loadWeight[b] - bins[b].getCapacity();
			}
			if (loadWeight[b] > 0 && loadWeight[b] < bins[b].getMinLoad()) {
				// violations++;
				// violationW++;
				violations += 0.5;

			}
			if (loadP[b] > bins[b].getP()) {
				// violations++;
				// violationP++;
				violations += loadP[b] - bins[b].getP();
			}
			if (loadType[b].size() > bins[b].getT()) {
				// violations++;
				// violationT++;
				violations += loadType[b].size() - bins[b].getT();
			}
			if (loadClass[b].size() > bins[b].getR()) {
				// violations++;
				// violationR++;
				violations += loadClass[b].size() - bins[b].getR();
			}

		}
		return violations;
	}

	public boolean isRaizeVio(int b, int[] solution, int item) {
		double loadWeight = 0;
		double loadP = 0;
		HashSet<Integer> loadType = new HashSet<Integer>();
		HashSet<Integer> loadClass = new HashSet<Integer>();

		double violations = 0;
		int[] X = solution;
		for (int i = 0; i < X.length; i++) {
			if (X[i] == b) {
				loadWeight += items[i].getW();
				loadP += items[i].getP();
				loadType.add(items[i].getT());
				loadClass.add(items[i].getR());
			}

		}
		loadType.add(items[item].getT());
		loadClass.add(items[item].getR());

		if (loadWeight + items[item].getW() > bins[b].getCapacity()) {
			return true;
		}
		if (loadP + items[item].getP() > bins[b].getP()) {
			return true;
		}
		if (loadType.size() > bins[b].getT()) {
			return true;
		}
		if (loadClass.size() > bins[b].getR()) {
			return true;
			// violations+=loadClass[b].size() - bins[b].getR();
		}
		return false;
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
		int[] X = solution;

		MinMaxTypeMultiKnapsackInputItem[] items = I.getItems();
		MinMaxTypeMultiKnapsackInputBin[] bins = I.getBins();

		for (int i = 0; i < X.length; i++) {
			if (X[i] < 0 || X[i] >= nbBins) {
			} else {
				int b = X[i];
				loadWeight[b] += items[i].getW();
				loadP[b] += items[i].getP();
				loadType[b].add(items[i].getT());
				loadClass[b].add(items[i].getR());
				numItems[b]++;
			}
		}
		for (int b = 0; b < nbBins; b++) {
			// System.out.println(numItems[b]);
			if (loadWeight[b] > 0 && (loadWeight[b] > bins[b].getCapacity())
					|| (loadWeight[b] > 0 && loadWeight[b] < bins[b].getMinLoad()) || (loadP[b] > bins[b].getP())
					|| (loadType[b].size() > bins[b].getT()) || (loadClass[b].size() > bins[b].getR())) {
				numvioITems += numItems[b];
			}
		}
		return numI - numvioITems;
	}

	public void readData(String path) {
		// load Data from file.
		I = MinMaxTypeMultiKnapsackInput.loadFromFile(path);
		bins = I.getBins();
		items = I.getItems();
		numI = items.length;
		numBin = bins.length;
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
			int[] solution = initialize(r);
			if (i == 0) {
				solution = initialize();
			}
			Individual indv = new Individual();
			indv.setChromosome(solution);
			indv.setFitness(violationCal(I, solution));
			population.add(indv);
		}
		return population;
	}

	public int[] initialize(Random r) {
		// duyet cac bin trong
		int[] solution = new int[numI];
		for (int i = 0; i < numI; i++) {
			solution[i] = -1;
		}
		boolean[] binTake = new boolean[numBin];
		boolean[] itemsTake = new boolean[numI];
		int countBin = 0;
		while (countBin < numBin) {
			int bin = r.nextInt(numBin);
			while (binTake[bin]) {
				bin = r.nextInt(numBin);
			}
			ArrayList<Integer> itemInBins = new ArrayList<>();
			for (int i = 0; i < numI; i++) {
				if (!itemsTake[i]) {
					for (int j = 0; j < items[i].getBinIndices().length; j++) {
						if (items[i].getBinIndices()[j] == bin) {
							itemInBins.add(i);
							break;
						}
					}
				}
			}
			double w = 0;
			for (int i = 0; i < itemInBins.size(); i++) {
				w += items[itemInBins.get(i)].getW();
			}
			if (w < bins[bin].getMinLoad() || bins[bin].getCapacity() < bins[bin].getMinLoad() || bins[bin].getP() < 0
					|| bins[bin].getCapacity() < 0) {
				countBin++;
				binTake[bin] = true;
				// System.out.println("loai");
				continue;
			}

			int j = 0;
			while (j < itemInBins.size()) {
				int item = itemInBins.get(j);
				if (!itemsTake[item] && !isRaizeVio(bin, solution, item)) {
					solution[item] = bin;
					itemsTake[item] = true;
				}
				j++;
			}
			binTake[bin] = true;
			countBin++;
		}
		return solution;
	}

	public int[] initialize() {
		// duyet cac bin trong
		int[] solution = new int[numI];
		for (int i = 0; i < numI; i++) {
			solution[i] = -1;
		}
		boolean[] itemsTake = new boolean[numI];
		for (int bin = 0; bin < numBin; bin++) {
			ArrayList<Integer> itemInBins = new ArrayList<>();
			for (int i = 0; i < numI; i++) {
				if (!itemsTake[i]) {
					for (int j = 0; j < items[i].getBinIndices().length; j++) {
						if (items[i].getBinIndices()[j] == bin) {
							itemInBins.add(i);
							break;
						}
					}
				}
			}
			double w = 0;
			for (int i = 0; i < itemInBins.size(); i++) {
				w += items[itemInBins.get(i)].getW();
			}
			if (w < bins[bin].getMinLoad() || bins[bin].getCapacity() < bins[bin].getMinLoad() || bins[bin].getP() < 0
					|| bins[bin].getCapacity() < 0) {
				continue;
			}

			int j = 0;
			while (j < itemInBins.size()) {
				int item = itemInBins.get(j);
				if (!itemsTake[item] && !isRaizeVio(bin, solution, item)) {
					solution[item] = bin;
					itemsTake[item] = true;
				}
				j++;
			}
		}
		return solution;
	}

	public ArrayList<Integer> getItemsIndicesUse(int[] binIndices) {
		ArrayList<Integer> Indices = new ArrayList<>();
		for (int i = 0; i < binIndices.length; i++) {
			if (bins[binIndices[i]].isUse()) {
				Indices.add(binIndices[i]);
			}

		}
		return Indices;
	}

	public int[] stepHighestClimbing(int numI, int[] solution, Random r) {
		double bestFitness = violationCal(I, solution);
		int[] copysol = new int[numI];
		for (int i = 0; i < numI; i++) {
			copysol[i] = solution[i];
		}
		int[] move = new int[2];
		move[0] = -1;
		move[1] = -1;
		for (int j = 0; j < numI; j++) {
			if (items[j].isUse()) {
				int[] binIndices = items[j].getBinIndices();
				ArrayList<Integer> Indices = getItemsIndicesUse(binIndices);
				int index = r.nextInt(Indices.size());
				if (Indices.size() < 2) {
					continue;
				}
				while (solution[j] == Indices.get(index)) {
					index = r.nextInt(Indices.size());
				}
				copysol[j] = Indices.get(index);
				if (violationCal(I, copysol) < bestFitness) {
					bestFitness = violationCal(I, copysol);
					move[0] = j;
					move[1] = Indices.get(index);
				}

				copysol[j] = solution[j];
			}
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
		double bestFitness = violationCal(I, solution);
		int inter = 0;
		while (inter < maxInter) {
			solution = stepHighestClimbing(numI, solution, r);
			// if(calViolation(solution, numBin, numI) <bestFitness){
			if (violationCal(I, solution) < bestFitness) {
				// bestFitness = calViolation(solution, numBin, numI);
				bestFitness = violationCal(I, solution);
				inter = 0;
				System.out.println(bestFitness);
			} else {
				inter++;
			}
		}

		return solution;
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
		System.out.println("intitial done");
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
				int father = touranmentSelection(popFitness, 2, popSize, r);
//				int father = bestFinessIndex;
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
			int numITems = calItems(I, pop.get(bestFinessIndex).getChromosome());
			System.out.println("Gen " + i + ":\t " + bestFiness + ":\t" + numITems);

			// printSolution( pop.get(bestFinessIndex).getChromosome());
			pop.get(bestFinessIndex).setChromosome(climbing(numI, pop.get(bestFinessIndex).getChromosome(), 1000, r));
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
		filePrintOut(pop.get(bestFinessIndex).getChromosome(), "3000ha.txt");
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

		while ((binIndices.length < 2) || (curBin == -1)) {
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
			popFitness[i] = violationCal(I, pop.get(i).getChromosome());
		}
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

	public int[] readFileOut(String fileName) {
		int[] solution = new int[numI];
		Scanner sc;
		try {
			sc = new Scanner(new File(fileName));
			for (int i = 0; i < numI; i++) {
				solution[i] = sc.nextInt();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return solution;
	}

	public void filePrintOut(int[] solution, String fileName) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(fileName), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < numI; i++) {
			pw.print(solution[i] + " ");
		}
		pw.close();

	}

	public int[] argSortBinCap() {
		float[] binArray = new float[numBin];
		for (int i = 0; i < numBin; i++) {
			binArray[i] = (float) bins[i].getCapacity();
		}
		ArrayUtils arr = new ArrayUtils();
		return arr.argsort(binArray);
	}

	public static void main(String[] args) {
		Random rand = new Random();
		rand.setSeed(2);
		int popSize = 200;
		String path = "MinMaxTypeMultiKnapsackInput-1000.json";
		String path1 = "src/khmtk60/miniprojects/multiknapsackminmaxtypeconstraints/model/MinMaxTypeMultiKnapsackInput-1000.json";
		MinMaxTypeMultiKnapsackSolution minMaxType = new MinMaxTypeMultiKnapsackSolution();
		minMaxType.readData(path1);
		minMaxType.GA(minMaxType.numI, popSize, 500, 0.3, 0.8, rand);
		int[] sol = minMaxType.readFileOut("1000.txt");
		System.out.println(minMaxType.calItems(minMaxType.I, sol));
	}

}
