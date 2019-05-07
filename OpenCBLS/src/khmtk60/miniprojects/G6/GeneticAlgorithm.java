package khmtk60.miniprojects.G6;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import com.google.gson.JsonIOException;

import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputBin;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputItem;

public class GeneticAlgorithm {
	int num_items;
	int num_bins;
	int num_types = 0;
	int num_classes = 0;
	double[] w, p; // trọng số 1, 2
	int[] t, r; // thể loại lớp
	int[][] D; // Các bin mà item có thể thêm vào
	double[] W, LW, P; // Max và min trọng số 1, max trọng số 2
	int[] T, R; // Max số thể loại và số lớp 
	MinMaxTypeMultiKnapsackInput input;

	public GeneticAlgorithm() throws FileNotFoundException {
		input = new MinMaxTypeMultiKnapsackInput().loadFromFile(
				"src\\khmtk60\\miniprojects\\multiknapsackminmaxtypeconstraints\\MinMaxTypeMultiKnapsackInput-3000.json");
		num_items = input.getItems().length;
		num_bins = input.getBins().length;
		w = new double[num_items];
		p = new double[num_items];
		t = new int[num_items];
		r = new int[num_items];
		D = new int[num_items][];

		W = new double[num_bins];
		LW = new double[num_bins];
		P = new double[num_bins];
		T = new int[num_bins];
		R = new int[num_bins];

		for (int i = 0; i < num_items; i++) {
			w[i] = input.getItems()[i].getW();
			p[i] = input.getItems()[i].getP();
			t[i] = input.getItems()[i].getT();
			if (t[i] > num_types) {
				num_types = t[i];
			}
			r[i] = input.getItems()[i].getR();
			if (r[i] > num_classes) {
				num_classes = r[i];
			}
			D[i] = input.getItems()[i].getBinIndices();
		}

		for (int i = 0; i < num_bins; i++) {
			W[i] = input.getBins()[i].getCapacity();
			LW[i] = input.getBins()[i].getMinLoad();
			P[i] = input.getBins()[i].getP();
			T[i] = input.getBins()[i].getT();
			R[i] = input.getBins()[i].getR();
		}
	}

	public int[] gene() {
		Random r = new Random();
		int[] X = new int[num_items];
		for (int i = 0; i < num_items; i++) {
//			int roll = r.nextInt(D[i].length + 1);
//			if(roll == D[i].length) {
//				X[i] = -1;
//			} else {
//				X[i] = D[i][roll];
//			}
			int roll = r.nextInt(100);
			if (roll < 10) {
				X[i] = -1;
			} else {
				X[i] = D[i][r.nextInt(D[i].length)];
			}
		}
		return X;
	}

	/**
	 * 
	 * @param gene
	 * @return giá trị fitness, càng cao càng tốt
	 */
	public float fitness(int[] gene) {
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
		float violations = 0;
		int[] X = gene;

		MinMaxTypeMultiKnapsackInputItem[] items = input.getItems();
		MinMaxTypeMultiKnapsackInputBin[] bins = input.getBins();

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
		for (int b = 0; b < nbBins; b++) {
			if (loadWeight[b] > 0 && (loadWeight[b] > bins[b].getCapacity())) {
				violations += (loadWeight[b] - bins[b].getCapacity()) / 10;
			}
			else if (loadWeight[b] > 0 && loadWeight[b] < bins[b].getMinLoad()) {
				violations += (bins[b].getMinLoad() - loadWeight[b]) / 10;
			}
			if (loadP[b] > bins[b].getP()) {
				violations++;
			}
			if (loadType[b].size() > bins[b].getT()) {
				violations++;
			}
			if (loadClass[b].size() > bins[b].getR()) {
				violations++;
			}
		}
		return 100000 - (violations + nbItemNotScheduled);

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
		int[] id = r.ints(2, 0, leng).toArray();
		for (int i : id) {
			for (int j = 0; j <= D[i].length; j++) {
				if (j != D[i].length) {
					gene[i] = D[i][j];
					if (fitness(gene) > bestFitness) {
						bestGen[i] = gene[i];
					}
				} else {
					gene[i] = -1;
					if (fitness(gene) > bestFitness) {
						bestGen[i] = -1;
					}
				}
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
		int[] breedPool = new int[pop.length - elitismSize];
		float[] roulette = new float[elitismSize];
		roulette[0] = fitness(pop[0]);
		for (int i = 1; i < elitismSize; i++) {
			roulette[i] = roulette[i - 1] + fitness(pop[i]);
		}
		Random r = new Random();
		for (int i = 0; i < pop.length - elitismSize; i++) {
			float val = r.nextFloat() * roulette[elitismSize - 1];
			for (int j = 0; j < elitismSize; j++) {
				if (val <= roulette[j]) {
					breedPool[i] = j;
					break;
				}
			}
		}
		for (int i = elitismSize; i < pop.length; i = i + 2) {
			int[][] offspring = breed(pop[breedPool[i - elitismSize]], pop[breedPool[i + 1 - elitismSize]]);
			if (r.nextFloat() <= mutationRate) {
				pop[i] = mutation(offspring[0]);
			} else {
				pop[i] = offspring[0];
			}
			if (r.nextFloat() <= mutationRate) {
				pop[i + 1] = mutation(offspring[1]);
			} else {
				pop[i + 1] = offspring[1];
			}
		}
	}

	public void printJson(int[] X) throws FileNotFoundException {
		PrintStream fileOut = new PrintStream("outputGA.json");
		System.setOut(fileOut);
		System.out.print("{\"binOfItem\":[");
		for (int i = 0; i < num_items; i++) {
			System.out.print(X[i]);
			if (i != num_items - 1)
				System.out.print(",");
		}
		System.out.print("]}");
	}

	public void solve(int generation, int populationSize) {
		int[][] pop = new int[populationSize][];
		for (int i = 0; i < 100; i++) {
			pop[i] = gene();
		}
		Arrays.sort(pop, (a, b) -> Float.compare(fitness(b), fitness(a))); // sort pop in descending order
		int[] bestGene = pop[0];
		System.out.println("Breeding");
		for (int i = 0; i < 100; i++) {
			newGeneration(pop, 70, 0.05f);
			Arrays.sort(pop, (a, b) -> Float.compare(fitness(b), fitness(a))); // sort pop in descending order
			bestGene = pop[0];
			System.out.println("Best population " + fitness(bestGene) + " at generation " + i);
		}
		try {
			printJson(bestGene);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws JsonIOException, IOException {
		GeneticAlgorithm hoa = new GeneticAlgorithm();
		hoa.solve(50, 100);
	}
}
