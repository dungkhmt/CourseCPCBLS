package khmtk60.miniprojects.G6;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.gson.JsonIOException;

import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;

public class Solution {
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
	public Solution() throws FileNotFoundException{
		// TODO Auto-generated constructor stub
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
	
	
	
	public int[] firstFit(int[] index) throws JsonIOException, IOException {
		if (index.length != num_items) {
			throw new ArithmeticException("Index length must equals number of items");
		}
		int X[] = new int[num_items];
		double bW[] = new double[num_bins]; // bin weight 1
		double bP[] = new double[num_bins]; // bin weight 2
		boolean[][] Y = new boolean[num_bins][num_types + 1];
		boolean[][] Z = new boolean[num_bins][num_classes + 1];
		itemLoop: for (int i : index) { 
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
}
