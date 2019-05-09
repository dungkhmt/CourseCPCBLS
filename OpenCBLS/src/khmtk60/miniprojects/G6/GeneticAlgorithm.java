package khmtk60.miniprojects.G6;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import com.google.gson.JsonIOException;

import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputBin;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputItem;

public class GeneticAlgorithm extends Solution {
	Greedy greedy;

	public GeneticAlgorithm() throws FileNotFoundException {
		super();
	}

	public int[] gene() throws JsonIOException, IOException {
		int[] X = new int[num_items];
//		Random r = new Random();
//		for (int i = 0; i < num_items; i++) {
//			int roll = r.nextInt(100);
//			if (roll < 10) {
//				X[i] = -1;
//			} else {
//				X[i] = D[i][r.nextInt(D[i].length)];
//			}
//
//		}
//		return X;
		for(int i = 0; i < num_items; i++) {
			X[i] = i;
		}
		X = Utilities.arrayShuffle(X);
		int[] sol = firstFit(X);
		return sol;
	}

	/**
	 * 
	 * @param gene
	 * @return giá trị fitness, càng cao càng tốt
	 */
	public int fitness(int[] gene) {
//		int nbBins = input.getBins().length;
//		double[] loadWeight = new double[nbBins];
//		double[] loadP = new double[nbBins];
//		HashSet<Integer>[] loadType = new HashSet[nbBins];
//		HashSet<Integer>[] loadClass = new HashSet[nbBins];
//
//		for (int b = 0; b < nbBins; b++) {
//			loadWeight[b] = 0;
//			loadP[b] = 0;
//			loadType[b] = new HashSet<Integer>();
//			loadClass[b] = new HashSet<Integer>();
//		}
//		int nbItemNotScheduled = 0;
//		float violations = 0;
//		int[] X = gene;
//
//		MinMaxTypeMultiKnapsackInputItem[] items = input.getItems();
//		MinMaxTypeMultiKnapsackInputBin[] bins = input.getBins();
//
//		for (int i = 0; i < X.length; i++) {
//			if (X[i] < 0 || X[i] >= nbBins) {
//				nbItemNotScheduled++;
//			} else {
//				int b = X[i];
//				loadWeight[b] += items[i].getW();
//				loadP[b] += items[i].getP();
//				loadType[b].add(items[i].getT());
//				loadClass[b].add(items[i].getR());
//
//				boolean ok = false;
//				for (int j = 0; j < items[i].getBinIndices().length; j++) {
//					if (b == items[i].getBinIndices()[j]) {
//						ok = true;
//						break;
//					}
//				}
//				if (!ok) {
//					violations++;
//				}
//			}
//		}
//		for (int b = 0; b < nbBins; b++) {
//			if (loadWeight[b] > 0 && (loadWeight[b] > bins[b].getCapacity())) {
//				violations += (loadWeight[b] - bins[b].getCapacity()) / 10;
//			} else if (loadWeight[b] > 0 && loadWeight[b] < bins[b].getMinLoad()) {
//				violations += (bins[b].getMinLoad() - loadWeight[b]) / 10;
//			}
//			if (loadP[b] > bins[b].getP()) {
//				violations++;
//			}
//			if (loadType[b].size() > bins[b].getT()) {
//				violations++;
//			}
//			if (loadClass[b].size() > bins[b].getR()) {
//				violations++;
//			}
//		}
//		return 20000 - (violations + nbItemNotScheduled);
		return getLoadedItem(gene);
	}

	public int getLoadedItem(int[] solution) {
		int loadedItem = solution.length;
		int[] filtered = filterViolatedItem(solution);
		for (int i : filtered) {
			if (i == -1)
				loadedItem--;
		}
		return loadedItem;
	}

	public int[] filterViolatedItem(int[] solution) {
		int nbBins = input.getBins().length;
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
		int[] X = solution.clone();

		MinMaxTypeMultiKnapsackInputItem[] items = input.getItems();
		MinMaxTypeMultiKnapsackInputBin[] bins = input.getBins();

		for (int i = 0; i < X.length; i++) {
			if (X[i] != -1) {
				int b = X[i];
				loadWeight[b] += items[i].getW();
				loadP[b] += items[i].getP();
				loadType[b].add(items[i].getT());
				loadClass[b].add(items[i].getR());
			}
		}
		for (int b = 0; b < nbBins; b++) { // if bin b violates one of the below, pull all items out of it
			boolean violated = false;
			if (loadWeight[b] > 0 && (loadWeight[b] > bins[b].getCapacity())) {
				violated = true;
			} else if (loadWeight[b] > 0 && loadWeight[b] < bins[b].getMinLoad()) {
				violated = true;
			} else if (loadP[b] > bins[b].getP()) {
				violated = true;
			} else if (loadType[b].size() > bins[b].getT()) {
				violated = true;
			} else if (loadClass[b].size() > bins[b].getR()) {
				violated = true;
			}
			if (violated == true) { // empty bin b
				for (int i = 0; i < num_items; i++) {
					if (X[i] == b)
						X[i] = -1;
				}
			}
		}
		return X;
	}

	/**
	 * Lai ghép
	 * 
	 * @param gene1
	 * @param gene2
	 * @return 1 cặp gene
	 */
	public static int[][] breed(int[] gene1, int[] gene2) {
		int leng1 = gene1.length;
		int[][] offSpr = new int[2][leng1];
		offSpr[0] = Arrays.copyOf(gene1, leng1);
		offSpr[1] = Arrays.copyOf(gene2, leng1);
		Random r = new Random();
		int p1 = r.nextInt(leng1);
		int p2 = r.nextInt(leng1 - p1) + p1;
		for (int i = p1; i <= p2; i++) {
			int temp = offSpr[0][i];
			offSpr[0][i] = offSpr[1][i];
			offSpr[1][i] = temp;
		}
		return offSpr;
	}

	/**
	 * Đột biến
	 * 
	 * @param gene
	 * @return gene bị đột biến
	 */
	public int[] mutation(int[] gene) {
		Random r = new Random();
		int leng = gene.length;
		int[] bestGen = Arrays.copyOf(gene, leng);
		float bestFitness = fitness(gene);
		int[] id = r.ints(r.nextInt(2), 0, leng).toArray();
		for (int i : id) {
//			for (int j = 0; j <= D[i].length; j++) {
//				if (j != D[i].length) {
//					gene[i] = D[i][j];
//					if (fitness(gene) > bestFitness) {
//						bestGen[i] = gene[i];
//					}
//				} else {
//					gene[i] = -1;
//					if (fitness(gene) > bestFitness) {
//						bestGen[i] = -1;
//					}
//				}
//			}
			int roll = r.nextInt(100);
			if (roll < 10) {
				bestGen[i] = -1;
			} else {
				bestGen[i] = D[i][r.nextInt(D[i].length)];
			}
		}
		return bestGen;
	}

	/**
	 * Lai tạo một quần thể mới
	 * 
	 * @param pop          quần thể
	 * @param elitismSize  số cá thể cũ được giữ lại
	 * @param mutationRate tỉ lệ đột biến
	 */
	public void newGeneration(int[][] pop, int elitismSize, float mutationRate) {
		int popSize = pop.length;
		int[] breedPool = new int[popSize - elitismSize];
		float[] roulette = new float[popSize];
		roulette[0] = fitness(pop[0]);
		for (int i = 1; i < popSize; i++) {
			float score = fitness(pop[i]);
			if(score > 0)
				roulette[i] = roulette[i - 1] + score;
			else roulette[i] = roulette[i - 1] + 1;
		}
		Random r = new Random();
//		for (int i = 0; i < popSize - elitismSize; i++) {
//			float val = r.nextFloat() * roulette[popSize - 1];
//			for (int j = 0; j < popSize; j++) {
//				if (val <= roulette[j]) {
//					breedPool[i] = j;
//					break;
//				}
//			}
//		}
		double[] vals = r.doubles(popSize - elitismSize, 0, roulette[popSize - 1]).toArray();
		for(int i = 0; i < popSize - elitismSize; i++) {
			for(int j = 0; j < popSize; j++) {
				if(vals[i] <= roulette[j]) {
					breedPool[i] = j;
					break;
				}
			}
		}

		int[][] newGen = new int[popSize - elitismSize][];
		for (int i = 0; i < popSize - elitismSize; i = i + 2) {
			int[][] offspring = breed(pop[breedPool[i]], pop[breedPool[i + 1]]);
			if (r.nextFloat() <= mutationRate) {
				newGen[i] = mutation(offspring[0]);
			} else {
				newGen[i] = offspring[0];
			}
			if (r.nextFloat() <= mutationRate) {
				newGen[i + 1] = mutation(offspring[1]);
			} else {
				newGen[i + 1] = offspring[1];
			}
		}
		// Giu lai @elitismSize ca the tot nhat, thay toan bo so con lai bang cac ca the moi duoc sinh ra
		for(int i = 0; i < newGen.length; i++) {
			pop[i + elitismSize] = newGen[i];
		}
	}

	public void solve(int generation, int populationSize) throws JsonIOException, IOException {
		int[][] pop = new int[populationSize][];
		for (int i = 0; i < populationSize; i++) {
			pop[i] = gene();
		}
		Arrays.sort(pop, (a, b) -> Float.compare(fitness(b), fitness(a))); // sort pop in descending order
		int[] bestGene = pop[0];
		for(int j = 0; j < populationSize; j++) {
			System.out.print(fitness(pop[j]) + " ");
		}
		System.out.println();
		System.out.println("Breeding");
		for (int i = 0; i < generation; i++) {
			newGeneration(pop, 20, 0.1f);
			Arrays.sort(pop, (a, b) -> Float.compare(fitness(b), fitness(a))); // sort pop in descending order
			bestGene = pop[0];
			System.out.println("Population best: " + getLoadedItem(bestGene) + " item at generation " + i);
			for(int j = 0; j < populationSize; j++) {
				System.out.print(fitness(pop[j]) + " ");
				if(j == 19)
					System.out.print("| ");
			}
			System.out.println();
		}		
		int[] bestFilteredSol = filterViolatedItem(bestGene);
		try {
			Utilities.printJson(bestFilteredSol, "outputGA2.json");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		GeneticAlgorithm hoa = new GeneticAlgorithm();
		hoa.solve(100, 100);

	}
}
