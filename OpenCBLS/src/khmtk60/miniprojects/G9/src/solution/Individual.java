package solution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class Individual {
	
	public Gene[] Indiv;
	public double violations;
	
	// Cac tham so la tong weight, price, type, rank cua cac bin
	double[] sumWeight;
	double[] sumPrice;
	int[] sumType;
	int[] sumRank;
	
	int violWeight;
	int violLowWeight;
	int violPrice;
	int violType;
	int violRank;
	
	
	public Individual(String kieuInit, int m, int n, int mt, int mr, double[] W, double[] LW, double[] P, int[] T, int[] R, ArrayList<ArrayList<Integer>> binIndices, double[] w, double[] p, int[] t, int[] r) {
		
		sumWeight = new double[m];
		sumPrice = new double[m];
		sumType = new int[m];
		sumRank = new int[m];
		
		this.Indiv = new Gene[n];
		this.violations = 0;
		if (kieuInit == "random") {
			this.randomInit(m, n, mt, mr, W, LW, P, T, R, binIndices, w, p, t, r);
		}
		else if (kieuInit == "heuristic"){
			this.heuristicInit(m, n, mt, mr, W, LW, P, T, R, binIndices, w, p, t, r);
		}
	}
	
	public void randomInit(int m, int n, int mt, int mr, double[] W, double[] LW, double[] P, int[] T, int[] R, ArrayList<ArrayList<Integer>> binIndices, double[] w, double[] p, int[] t, int[] r) {
		Random Rand = new Random();
		
		for (int i=0; i<n; i++) {
			// Khoi tao index cua bin trong tap nhung bin co the cua item i
//			int binIndex = Rand.nextInt(binIndices.get(i).size()+1) - 1;
			int binIndex = Rand.nextInt(binIndices.get(i).size());
			
			// Neu index bin bang -1 thi cho bin la -1
			if (binIndex != -1) {
				this.Indiv[i] = new Gene(i, binIndices.get(i).get(binIndex), w[i], p[i], t[i], r[i]);
			}
			else {
				this.Indiv[i] = new Gene(i, -1, w[i], p[i], t[i], r[i]);
			}
		}
		
		this.violations = calculateViolations(m, n, mt, mr, W, LW, P, T, R);
	}
	
	public void heuristicInit(int m, int n, int mt, int mr, double[] W, double[] LW, double[] P, int[] T, int[] R, ArrayList<ArrayList<Integer>> binIndices, double[] w, double[] p, int[] t, int[] r) {
		// Code init kieu Heuristic
		double[] currentSumW = new double[m];
		double[] currentSumP = new double[m];
		int[] currentSumT = new int[m];
		int[] currentSumR = new int[m];
		
		ArrayList<Set<Integer>> typeBin = new ArrayList<>();
		ArrayList<Set<Integer>> classBin = new ArrayList<>();
		
		Random Rand = new Random();
		
		for (int i = 0; i < m; i++) {
			Set<Integer> set_type = new HashSet<>();
			Set<Integer> set_class = new HashSet<>();
			typeBin.add(set_type);
			classBin.add(set_class);
		}
		
		for (int i=0; i<n; i++) {
			ArrayList<Integer> tempBinofItem = new ArrayList<Integer>();
			tempBinofItem.addAll(binIndices.get(i));
			
			for (int tempi=0; tempi<tempBinofItem.size(); tempi++) {
//			while (tempBinofItem.size() > 0) {
//				int currentIndex = Rand.nextInt(tempBinofItem.size());
//				int idx = tempBinofItem.get(currentIndex);
				int idx = tempBinofItem.get(tempi);
				
				if (currentSumW[idx] + w[i] <= W[idx] && currentSumP[idx] + p[i] <= P[idx]) {
					if ((currentSumT[idx] > T[idx]) || (currentSumR[idx] > R[idx])) {
//						tempBinofItem.remove(currentIndex);
						continue;
					}
					else if ((!classBin.get(idx).contains(r[i]) && (currentSumR[idx] == R[idx]))) {
//						tempBinofItem.remove(currentIndex);
						continue;
					}
					else if ((!typeBin.get(idx).contains(t[i]) && (currentSumT[idx] == T[idx]))) {
//						tempBinofItem.remove(currentIndex);
						continue;
					}
					else {
						if (typeBin.get(idx).contains(t[i])) {
							Indiv[i] = new Gene(i, idx, w[i], p[i], t[i], r[i]);
//							tempBinofItem.remove(currentIndex);
							
							currentSumW[idx] += w[i];
							currentSumP[idx] += p[i];
							if (!classBin.get(idx).contains(r[i])) {
								currentSumR[idx] += 1;
								classBin.get(idx).add(r[i]);
								break;
							}
							break;
						} else {
							Indiv[i] = new Gene(i, idx, w[i], p[i], t[i], r[i]);
//							tempBinofItem.remove(currentIndex);
							
							currentSumW[idx] += w[i];
							currentSumP[idx] += p[i];
							if (!classBin.get(idx).contains(r[i])) {
								typeBin.get(idx).add(r[i]);
								currentSumR[idx] += 1;
								classBin.get(idx).add(r[i]);
								break;
							}
							break;
						}
					}
				}
			}
			
//			int indexItemKhongTimDuoc = binIndices.get(i).get(Rand.nextInt(binIndices.get(i).size()));
//			Indiv[i] = new Gene(i, indexItemKhongTimDuoc, w[i], p[i], t[i], r[i]);
			
		}
		
		typeBin.clear();
		classBin.clear();
		
		this.violations = calculateViolations(m, n, mt, mr, W, LW, P, T, R);
	}
	
	
	public double calculateViolations(int m, int n, int mt, int mr, double[] W, double[] LW, double[] P, int[] T, int[] R) {
		int alpha = 20;
		int beta = 100;
		double viols = 0;
		
		
		violWeight = 0;
		violLowWeight = 0;
		violPrice = 0;
		violType = 0;
		violRank = 0;
		
		// Huong dan khoi tao mang bang 0 Java
		
		for (int b=0; b<m; b++) {
			sumWeight[b] = 0;
			sumPrice[b] = 0;
			sumType[b] = 0;
			sumRank[b] = 0;
		}
		
		int[][] tempType = new int[mt][m];
		int[][] tempRank = new int[mr][m];
		for (int j=0; j<mt; j++) {
			for (int k=0; k<m; k++) {
				tempType[j][k] = 0;
			}
		}
		for (int j=0; j<mr; j++) {
			for (int k=0; k<m; k++) {
				tempRank[j][k] = 0;
			}
		}
		
		// Code tinh toan Violations o day






		// Convert range cua type ve tu 0 ---> 8
		// Sua tempType ve thanh mang 2D vi neu khong se bi trung giua cac bin voi nhau. TAO MANG 2D:    tempType[mt][m]
		
		
		// Code tinh lai violations cho hillClimbing thi tinh violations chi thay doi o phan tu ma ta thay doi gia tri
		
		for (int i=0; i<n; i++) {
			
			int currentBin = this.Indiv[i].bin;
			int currentType = this.Indiv[i].type;
			int currentRank = this.Indiv[i].rank;
			
			if(currentBin != -1) {
				sumWeight[currentBin] += this.Indiv[i].weight;
				sumPrice[currentBin] += this.Indiv[i].price;
			}
			
			if (currentBin != -1) {
				if(tempType[currentType][currentBin] == 0) {
					tempType[currentType][currentBin] += 1;
					sumType[currentBin] += 1;
				}
			}
			
			if (currentBin != -1) {
				if(tempRank[currentRank][currentBin] == 0) {
					tempRank[currentRank][currentBin] += 1;
					sumRank[currentBin] += 1;
				}
			}
			
		}
		
		for (int b=0; b<m; b++) {
			int countTrueBin = 0;
			
//			viols += Math.max(0, sumWeight[b] - W[b])  +  Math.max(0, LW[b] - sumWeight[b])  +  Math.max(0, sumPrice[b] - P[b])  +  Math.max(0, sumType[b] - T[b]) + Math.max(0, sumRank[b] - R[b]); 
//			viols += ((sumWeight[b] > W[b])?1:0) + ((LW[b] > sumWeight[b])?1:0) + ((sumPrice[b] > P[b])?1:0) + ((sumType[b] > T[b])?1:0) + ((sumRank[b] > R[b])?1:0);
			
			if ((sumWeight[b] <= W[b]) && (LW[b] <= sumWeight[b]) && (sumPrice[b] <= P[b]) && (sumType[b]-T[b] <= 0) && (sumRank[b] - R[b] <= 0)) {
				countTrueBin += 1;
			}
			
			viols += ((sumWeight[b] > W[b])?1:0) + ((LW[b] > sumWeight[b])?1:0) + ((sumPrice[b] > P[b])?1:0) + alpha * (((sumType[b] > T[b])?1:0) + ((sumRank[b] > R[b])?1:0)) - beta * countTrueBin;
			
			violWeight += ((sumWeight[b] > W[b])?1:0);
			violLowWeight += ((LW[b] > sumWeight[b])?1:0);
			violPrice += ((sumPrice[b] > P[b])?1:0);
			violType += ((sumType[b] > T[b])?1:0);
			violRank += ((sumRank[b] > R[b])?1:0);
		}
		
		return viols;
	}
	

}
