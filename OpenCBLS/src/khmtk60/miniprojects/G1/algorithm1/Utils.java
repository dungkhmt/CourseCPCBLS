package khmtk60.miniprojects.G1.algorithm1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Utils {
	public static HashSet<Integer> getBlackList(Multiknapsack knapsack){
		HashSet<Integer> blackList = new HashSet<>();
		for (int j = 0; j < knapsack.M; j++) {
			if(knapsack.minW[j] < 0 || knapsack.maxW[j] < 0 || knapsack.maxP[j] < 0) {
				blackList.add(j);
				continue;
			}
				
			double sumW = 0.;
			double[] sumPerType = new double[knapsack.T];
			double[] sumPerclass = new double[knapsack.R];
			for (int i = 0; i < knapsack.N; i++) {
				for (int bin : knapsack.D.get(i)) {
					if(j == bin) {						
						sumW += knapsack.w[i];
						sumPerType[knapsack.t2idx.get(knapsack.t[i])] += knapsack.w[i];
						sumPerclass[knapsack.r2idx.get(knapsack.r[i])] += knapsack.w[i];
						break;
					}
				}
			}
			
			boolean flag = false;
			if(knapsack.maxR[j] == 1) {
				for (int c = 0; c < knapsack.R; c++) {
					if(sumPerclass[c] >= knapsack.minW[j]) {
						flag = true;
						break;
					}
				}
			}else {
				flag = true;
			}
			
			boolean flag2 = false;
			if(knapsack.maxT[j] == 2) {
				double max2Type = 0.;
				for (int type = 0; type < knapsack.T-1; type++) {
					for (int type2 = type+1; type2 < knapsack.T; type2++) {
						if(sumPerType[type] + sumPerType[type2] > max2Type) {
							max2Type = sumPerType[type] + sumPerType[type2];
						}
					}
				}
				if(max2Type >= knapsack.minW[j]) {
					flag2 = true;
				}
			}else {
				flag2 = true;
			}
			
			if(!(sumW >= knapsack.minW[j] && flag && flag2)) {
				blackList.add(j);			
			} 
		}
		System.out.println("blacklist size: " + blackList.size());
		return blackList;
	}
	
	public static HashSet<Integer> getBlackListOfItem(Multiknapsack knapsack, HashSet<Integer> blackList){
		HashSet<Integer> result = new HashSet<>();
		HashMap<Integer, ArrayList<Integer>> class2Items = new HashMap<>();
		HashMap<Integer, Double> sumPerClass = new HashMap<>();
		for(int key: knapsack.r2idx.keySet()) {
			class2Items.put(key, new ArrayList<>());
		}
		for (int i = 0; i < knapsack.N; i++) {
			class2Items.get(knapsack.r[i]).add(i);
		}
		
		for (int key : class2Items.keySet()) {
			double sumClass = 0;
			double minW1 = 1e8;
			HashSet<Integer> domain = new HashSet<>();
			for (int item : class2Items.get(key)) {
				sumClass += knapsack.w[item];
				for (int bin : knapsack.D.get(item)) {
					if(blackList.contains(bin)) {
						continue;
					}
					domain.add(bin);
				}
			}
			for (int bin : domain) {
				minW1 = minW1 > knapsack.minW[bin] ? knapsack.minW[bin] : minW1;  
			}
			sumPerClass.put(key, sumClass);
			if(sumClass < minW1) {
				for (int item : class2Items.get(key)) {
					result.add(item);
				}
			}
//			System.out.println(knapsack.r2idx.get(key) + ": " + sumClass + " / " + minW1);
		}
		
//		for (int c : result) {
//			System.out.println(knapsack.r2idx.get(c) + ": " + class2Items.get(c).size());
//		}
		System.out.println();
		return result;
	}
	
	public static HashMap<Integer, Integer> getRemapBinId(int M, HashSet<Integer> blackList){
		HashMap<Integer, Integer> result = new HashMap<>();
		int count = 0;
		for (int i = 0; i <= M; i++) {
			if(!blackList.contains(i)) {
				result.put(i, count);
				count++;
			}
		}
		return result;
	}
	
	public static HashMap<Integer, Integer> invertMap(HashMap<Integer, Integer> map){
		HashMap<Integer, Integer> result = new HashMap<>();
		for (Integer key : map.keySet()) {
			result.put(map.get(key), key);
		}
		return result;
	}
	
	public static void printSolution(Multiknapsack knapsack, HashSet<Integer> blackList) {
		for(int j = 0; j < knapsack.realBinNumber+1; j++) {
			System.out.println(" Bin " + j + ": " + knapsack.bin2Items.get(j).size() + " items");
			if(knapsack.sumWeight1[j].getValue() > 0) {
				System.out.print(knapsack.weight1LowerConstraints[j].violations() + "_ ");
				System.out.println();
			}
//			System.out.println("Type vio: " + knapsack.typeConstraint[j].violations());
//			System.out.println("Class vio: " + knapsack.classConstraint[j].violations());
			knapsack.weight1LowerConstraints[j].verify();			
			double sum = 0., sumP = 0.;
			System.out.print("\t");
			for (int i = 0; i < knapsack.N; i++) {
				if(knapsack.X[i].getValue() == j) {
					
					sum += knapsack.w[i];
					sumP += knapsack.p[i];
					System.out.println("* " + i + "(w: " + knapsack.w[i] + ", p: " + knapsack.p[i] +  ", type: " + knapsack.t2idx.get(knapsack.t[i]) + ", class: " + knapsack.r2idx.get(knapsack.r[i]) + ")");	
					System.out.print("\t");
					for (int binId2 : knapsack.D.get(i)) {
						if(!blackList.contains(binId2))
							System.out.print(knapsack.oldBinId2NewId.get(binId2) + " ");
					}
					System.out.println();
					System.out.print("\t");
				}
			}

			int sumT, sumR;
			sumT = knapsack.Y[j].getValue();
			sumR = knapsack.Z[j].getValue();
			int oldBinId = knapsack.newBinId2OldId.get(j);
			System.out.print("max W: " + knapsack.maxW[oldBinId] + ", min W:" + knapsack.minW[oldBinId] + ": " + sum);
			if(j < knapsack.realBinNumber) {
				System.out.print(" == " + knapsack.sumWeight1[j].getValue());
			}
			System.out.println();
			System.out.print("\t max P " + knapsack.maxP[oldBinId] + ": " + sumP);
			if(j < knapsack.realBinNumber) {
				System.out.print(" == " + knapsack.sumWeight2[j].getValue());
			}
			System.out.println();
			System.out.println("\t max T: " + sumT + "/" + knapsack.maxT[oldBinId] + ", max R: " + sumR + "/" + knapsack.maxR[oldBinId]);
			if(sumT > knapsack.maxT[oldBinId]) {
				System.out.println("vio type");
			}
			if(sumR > knapsack.maxR[oldBinId]) {
				System.out.println("vio class");
			}
			for (int type = 0; type < knapsack.T; type++) {
				if(knapsack.numTypePerBin[j][type] > 0) {
					System.out.print("type " + type + ": " + knapsack.numTypePerBin[j][type] + ", ");
				}
			}
			System.out.println();
			
			for (int c = 0; c < knapsack.R; c++) {
				if(knapsack.numClassPerBin[j][c] > 0) {
					System.out.print("class " + c + ": " + knapsack.numClassPerBin[j][c] + ", ");
				}
			}
			System.out.println();
			System.out.println();
		}
	}
	
	public static void shuffleArray(int[] arr, Random rand){
		for (int i = arr.length - 1; i > 0; i--) {
			int index = rand.nextInt(i + 1);
			// Simple swap
			int a = arr[index];
			arr[index] = arr[i];
			arr[i] = a;
		}
	}
}
