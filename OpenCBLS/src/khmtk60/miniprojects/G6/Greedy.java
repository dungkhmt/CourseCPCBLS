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
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackSolution;

public class Greedy extends Solution{
	
	
	public Greedy() throws FileNotFoundException{
		// TODO Auto-generated constructor stub
		super();
	}

	/**
	 * Chọn bin đầu tiên thỏa mãn
	 * 
	 * @throws JsonIOException
	 * @throws IOException
	 */
	public int[] firstFit() throws JsonIOException, IOException {
		int X[] = new int[num_items];
		double bW[] = new double[num_bins]; // bin weight 1
		double bP[] = new double[num_bins]; // bin weight 2
		boolean[][] Y = new boolean[num_bins][num_types + 1];
		boolean[][] Z = new boolean[num_bins][num_classes + 1];
		itemLoop: for (int i = 0; i < num_items; i++) { 
			for (int j : D[i]) { 
				if (bW[j] + w[i] <= W[j] && bP[j] + p[i] <= P[j]) { 
					boolean old_type_value = Y[j][t[i]];
					boolean old_class_value = Z[j][r[i]];
					if (old_type_value == false) {
						int type_sum = 0;
						for (int k = 0; k < num_types + 1; k++) {
							if (Y[j][k] == true) {
								type_sum += 1;
							}
						}
						if (type_sum + 1 > T[j]) {
							continue;
						}
					}
					if (old_class_value == false) {
						int class_sum = 0;
						for (int k = 0; k < num_classes + 1; k++) {
							if (Z[j][k] == true) {
								class_sum += 1;
							}
						}
						if (class_sum + 1 > R[j]) {
							continue;
						}
					}
//					System.out.println("Item " + i + " is added to bin " + j);
					X[i] = j;
					bW[j] += w[i];
					bP[j] += p[i];
					Y[j][t[i]] = true;
					Z[j][r[i]] = true;
					continue itemLoop;
				}
			}
//			System.out.println("Item " + i + " cannot be added to any bins");
			X[i] = -1;
		}
//		for(int i = 0; i < num_bins; i++) {
//			System.out.println("Bin "+ i + " has weight 1 = " + bW[i] + ", Bin weight 2 = " + bP[i]);
//		}
		int unallocated_item = 0;
		for (int i = 0; i < num_items; i++) {
			if (X[i] == -1) {
				unallocated_item += 1;
			}
		}
//		System.out.println("There are " + unallocated_item + " item(s) not added to any bins");
		return X;
	}



	public float fitness(int[] solution) {
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
		int[] X = solution;

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
			if (loadWeight[b] > 0 && loadWeight[b] < bins[b].getMinLoad()) {
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
	
	
	public boolean isValid(int[] solution) {
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
		int[] X = solution;

		MinMaxTypeMultiKnapsackInputItem[] items = input.getItems();
		MinMaxTypeMultiKnapsackInputBin[] bins = input.getBins();

		for (int i = 0; i < X.length; i++) {
			if (X[i] > 0 && X[i] <= nbBins) {
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
					return false;
				}
			}
		}
		for (int b = 0; b < nbBins; b++) {
			if (loadWeight[b] > 0 && (loadWeight[b] > bins[b].getCapacity())) {
				return false;
			}
			if (loadP[b] > bins[b].getP()) {
				return false;
			}
			if (loadType[b].size() > bins[b].getT()) {
				return false;
			}
			if (loadClass[b].size() > bins[b].getR()) {
				return false;
			}
		}
		return true;
	}
	
	
	public void greedy(int[] firstfit) throws FileNotFoundException {
		int X[] = firstfit;
		for (int i = 0; i < num_items; i++) { // duyệt qua tất cả item
			float bestScore = fitness(X);
			int bestBin = X[i];
			X[i] = -1;
			float score = fitness(X);
			if(score > bestScore) {
				bestBin = X[i];
				bestScore = score;
			}
			for (int j : D[i]) { // duyệt qua tất cả bin thỏa mãn
				// kiểm tra ràng buộc
				X[i] = j;
				if(isValid(X)) {
					score = fitness(X);
					if(score > bestScore) {
						bestBin = X[i];
						bestScore = score;
					}
				}
			}
			X[i] = bestBin;
			System.out.println("X[" + i + "] = " + bestBin);
		}
		Utilities.printJson(X, "outputGreedy.json");
	}

	

	public static void main(String[] args) throws JsonIOException, IOException {
		Greedy hoa = new Greedy();
		GeneticAlgorithm ga = new GeneticAlgorithm();
		int[] sol;
		sol = hoa.firstFit();
	}
}
