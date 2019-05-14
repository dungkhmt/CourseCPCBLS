package solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Population {

	int numPopu = 200;
	int numMutate = 0;
	double fixedCrossRate = 0.5;
	double fixedMutateRate = 0.1;
	
	ArrayList<Individual> Popu;
	
	public Population(int m, int n, int mt, int mr, double[] W, double[] LW, double[] P, int[] T, int[] R, ArrayList<ArrayList<Integer>> binIndices, double[] w, double[] p, int[] t, int[] r) {
		numMutate = n * 50/100;
		
		this.Popu = new ArrayList<Individual>();
		for (int i=0; i<numPopu/2; i++) {
			Individual newIndi = new Individual("random", m, n, mt, mr, W, LW, P, T, R, binIndices, w, p, t, r);
			this.Popu.add(newIndi);
		}
		for (int i=numPopu/2; i<numPopu; i++) {
			Individual newIndi = new Individual("heuristic", m, n, mt, mr, W, LW, P, T, R, binIndices, w, p, t, r);
			this.Popu.add(newIndi);
		}
	}
	
	public void Hybridization(int m, int n, int mt, int mr, double[] W, double[] LW, double[] P, int[] T, int[] R, ArrayList<ArrayList<Integer>> binIndices, double[] w, double[] p, int[] t, int[] r) {
		double crossrate1 = 0;
		double crossrate2 = 0;
		double mutateRate = 0;
		ArrayList<Individual> inputPopu = new ArrayList<Individual>();
		inputPopu.addAll(this.Popu);
		
		for (int i=0; i<numPopu; i++) {
			// Crossover
			crossrate1 = Math.random();
			if(crossrate1 > fixedCrossRate) {
				for (int j=i+1; j<numPopu; j++) {
					crossrate2 = Math.random();
					if(crossrate2 > fixedCrossRate) {
						crossover(inputPopu, Popu.get(i), Popu.get(j), m, n, mt, mr, W, LW, P, T, R, binIndices, w, p, t, r);
					}
				}
			}
			
			//Mutation
			mutateRate = Math.random();
			if(mutateRate > fixedMutateRate) {
				mutate(inputPopu, Popu.get(i), m, n, mt, mr, W, LW, P, T, R, binIndices, w, p, t, r);
			}
		}
		
		selection(inputPopu);
		this.Popu.clear();
		this.Popu.addAll(inputPopu);
	}
	
	public void crossover(ArrayList<Individual> inputPopu, Individual father, Individual mother, int m, int n, int mt, int mr, double[] W, double[] LW, double[] P, int[] T, int[] R, ArrayList<ArrayList<Integer>> binIndices, double[] w, double[] p, int[] t, int[] r) {
		Random Rand = new Random();
		
		int crossoverGene = Rand.nextInt(father.Indiv.length);
		Individual newBorn1 = new Individual("random", m, n, mt, mr, W, LW, P, T, R, binIndices, w, p, t, r);
		Individual newBorn2 = new Individual("random", m, n, mt, mr, W, LW, P, T, R, binIndices, w, p, t, r);
		
		for (int i=0; i<crossoverGene; i++) {
			newBorn1.Indiv[i].bin = father.Indiv[i].bin;
			newBorn2.Indiv[i].bin = mother.Indiv[i].bin;
		}
		
		for (int i=crossoverGene; i<newBorn1.Indiv.length; i++) {
			// Kiem tra xem bin cua father hoac mother co nam trong binIndices cua gene con hay ko
//			if (binIndices.get(i).contains(mother.Indiv[i].bin)) {
//				
//			}
			newBorn1.Indiv[i].bin = mother.Indiv[i].bin;
			newBorn2.Indiv[i].bin = father.Indiv[i].bin;
		}
		newBorn1.violations = newBorn1.calculateViolations(m, n, mt, mr, W, LW, P, T, R);
		newBorn2.violations = newBorn2.calculateViolations(m, n, mt, mr, W, LW, P, T, R);
		
		if(Math.random() > 0.5) {
			inputPopu.add(newBorn1);
		}
		else {
			inputPopu.add(newBorn2);
		}
		
//		inputPopu.add(newBorn1);
//		inputPopu.add(newBorn2);
		
	}
	
	public void mutate(ArrayList<Individual> inputPopu, Individual hooman, int m, int n, int mt, int mr, double[] W, double[] LW, double[] P, int[] T, int[] R, ArrayList<ArrayList<Integer>> binIndices, double[] w, double[] p, int[] t, int[] r) {
		Random Rand = new Random();
		
		int[] mutateGene = new int[numMutate];
		for (int i=0; i<numMutate; i++) {
			mutateGene[i] = Rand.nextInt(hooman.Indiv.length);
		}
		Individual XMen = new Individual("random", m, n, mt, mr, W, LW, P, T, R, binIndices, w, p, t, r);
		
		for (int i=0; i<numMutate; i++) {
//			int binIndex = Rand.nextInt(binIndices.get(mutateGene[i]).size()+1) - 1;
			int binIndex = Rand.nextInt(binIndices.get(mutateGene[i]).size());
			
			if (binIndex != -1 && binIndices.get(mutateGene[i]).get(binIndex) != hooman.Indiv[mutateGene[i]].bin) {
				XMen.Indiv[mutateGene[i]].bin = binIndices.get(mutateGene[i]).get(binIndex);
			}
			else if (binIndex == -1) {
				XMen.Indiv[mutateGene[i]].bin = -1;
			}
		}
		
		XMen.violations = XMen.calculateViolations(m, n, mt, mr, W, LW, P, T, R);
		
		inputPopu.add(XMen);
	}
	
	public void selection(ArrayList<Individual> inputPopu) {
		// Giu lai 50% ca the tot nhat con lai lay random
		quickSort(inputPopu, 0, inputPopu.size()-1);
		
		ArrayList<Individual> newPopu = new ArrayList<Individual>();
		newPopu.addAll(inputPopu);
		for (int i=0; i<numPopu/2; i++) {
			newPopu.remove(i);
		}
		// Tron cac phan tu trong newPopu
		Collections.shuffle(newPopu);
		
		
		for (int i=numPopu/2; i<inputPopu.size(); i++) {
			inputPopu.remove(i);
		}
		
		for (int i=0; i<numPopu/2; i++) {
			inputPopu.add(newPopu.get(i));
		}
		
//		for (int i=numPopu; i<inputPopu.size(); i++) {
//			inputPopu.remove(i);
//		}
	}
	
	
	public ArrayList<ArrayList<Integer>> computeResult(int m, int n, int mt, int mr, double[] W, double[] LW, double[] P, int[] T, int[] R, ArrayList<ArrayList<Integer>> binIndices, double[] w, double[] p, int[] t, int[] r) {
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

		ArrayList<Integer> itemNotSave = new ArrayList<Integer>();
		ArrayList<Integer> binNotSatis = new ArrayList<Integer>();
		ArrayList<Integer> listBin = new ArrayList<>();
		ArrayList<Integer> itemSave = new ArrayList<Integer>();
		
		Individual currentIndiv = new Individual("random", m, n, mt, mr, W, LW, P, T, R, binIndices, w, p, t, r);
		currentIndiv = this.Popu.get(0);

		for (int i = 0; i < m; i++) {
//			VarIntLS[] y = new VarIntLS[n];
//			float sum_w = 0;
//			float sum_p = 0;
//			float sum_t = 0;
//			float sum_r = 0;
//			for (int j = 0; j < n; j++) {
//				y[j] = X[j][i];
//			}
//			Set<Integer> ty = new HashSet<>();
//			Set<Integer> cl = new HashSet<>();
//			for (int k = 0; k < y.length; k++) {
//				int value = y[k].getValue();
//				if (value == 1) {
//					sum_w += w[k];
//					sum_p += p[k];
//					ty.add(t[k]);
//					cl.add(r[k]);
//					sum_t = ty.size();
//					sum_r = cl.size();
//				}
//			}

			if (currentIndiv.sumWeight[i] <= W[i] && currentIndiv.sumWeight[i] >= LW[i] && currentIndiv.sumPrice[i] <= P[i] && currentIndiv.sumType[i] <= T[i] && currentIndiv.sumRank[i] <= R[i]) {
				listBin.add(i);
			} else {
				binNotSatis.add(i);
			}

		}
		
		int sum = 0;
//		for (int i = 0; i < listBin.size(); i++) {
//			for (int j = 0; j < n; j++) {
//				if (X[j][listBin.get(i)].getValue() == 1) {
//					sum++;
//					itemSave.add(j);
//				}
//			}
//		}
		for (int b=0; b<listBin.size(); b++) {
			for (int i=0; i<n; i++) {
				if (currentIndiv.Indiv[i].bin == listBin.get(b)) {
					sum++;
					itemSave.add(i);
				}
			}
		}
		for (int i = 0; i < n; i++) {
			if (!itemSave.contains(i))
				itemNotSave.add(i);
		}
		System.out.println("So Item xep dung : " + sum);
		result.add(binNotSatis);
		result.add(itemNotSave);
		result.add(listBin);
		result.add(itemSave);
		return result;
}
	
	
	public void printInfo(int m, int n, int mt, int mr, double[] W, double[] LW, double[] P, int[] T, int[] R, ArrayList<ArrayList<Integer>> binIndices, double[] w, double[] p, int[] t, int[] r) {
//		for (int i=0; i<Popu.get(0).Indiv.length; i++) {
//			System.out.print(Popu.get(0).Indiv[i].bin + "\t");
//		}
//		System.out.println();		
		
		System.out.println(Popu.get(0).violWeight);
		System.out.println(Popu.get(0).violLowWeight);
		System.out.println(Popu.get(0).violPrice);
		System.out.println(Popu.get(0).violType);
		System.out.println(Popu.get(0).violRank);
		
		ArrayList<ArrayList<Integer>> result = computeResult(m, n, mt, mr, W, LW, P, T, R, binIndices, w, p, t, r);
		for (int i=0; i<Popu.get(0).Indiv.length; i++) {
			if (result.get(3).contains(i)) {
				System.out.print(Popu.get(0).Indiv[i].bin + "\t");
			}
			else {
				System.out.print("-1" + "\t");
			}
		}
	}
	
	
	
	// Ham quick sort phu tro
	public void quickSort(ArrayList<Individual> list, int begin, int end) {
		if (begin<end) {
			int partitionIndex = partition(list, begin, end);
			
			quickSort(list, begin, partitionIndex-1);
			quickSort(list, partitionIndex+1, end);
		}
	}
	
	public int partition(ArrayList<Individual> list, int begin, int end) {
		double pivot = list.get(end).violations;
		int i = begin - 1;
		
		for (int j=begin; j<end; j++) {
			if (list.get(j).violations < pivot) {
				i++;
				
				Collections.swap(list, i, j);
			}
		}
		
		Collections.swap(list, i+1, end);
		
		return i+1;
	}
}
