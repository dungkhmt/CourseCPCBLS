package src;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.*;

public class SolutionPhase2 extends Solution {
	private final int DEBUG = 0;
	
	public double violations(int b) {
		double sumViolation = 0;

		if (nTypePerBin[b] == 0) {
			return 0;
		}

		sumViolation += Math.max(0, sumW[b] - bins[b].getCapacity());
		sumViolation += Math.max(0, sumP[b] - bins[b].getP());
		sumViolation += Math.max(0, nTypePerBin[b] - bins[b].getT());
		sumViolation += Math.max(0, nClassPerBin[b] - bins[b].getR());
		sumViolation += notInB[b];
		
		
		if(bins[b].getMinLoad() - sumW[b] > 0) {
			sumViolation += ((double)nItemPerBin[b])/n;
		}
		//sumViolation += Math.max(0, bins[b].getMinLoad() - sumW[b])/maxLoad;
		return sumViolation;
	}

	public double violations(ArrayList<Integer> a, int b) {
		double sumViolation = 0;
		double sumW = 0;
		double sumP = 0;
		int nType = 0;
		int nClass = 0;
		HashSet<Integer> type = new HashSet<Integer>();
		HashSet<Integer> clas = new HashSet<Integer>() ;

		int t = 0, r = 0;
		
		HashSet<Integer> binIndices; 

		for (int i: a) {
			binIndices = newBinIndices[i];
			if(!binIndices.contains(bins[b].getId())) {
				sumViolation += 1;
			}
			
			sumW += items[i].getW();
			sumP += items[i].getP();
			t = items[i].getT();
			r = items[i].getR();
			if (!type.contains(t)) {
				nType += 1;
				type.add(t);
			} 
			
			if (!clas.contains(r)) {
				nClass += 1;
				clas.add(r);
			}
		}

		sumViolation += Math.max(0, sumW - bins[b].getCapacity());
		sumViolation += Math.max(0, sumP - bins[b].getP());
		sumViolation += Math.max(0, nType - bins[b].getT());
		sumViolation += Math.max(0, nClass - bins[b].getR());
		
		if(bins[b].getMinLoad() - sumW > 0) {
			sumViolation += ((double)a.size())/n;
		}

		return sumViolation;
	}
	
	public double getSwapDelta(int b_x, int b_y, ArrayList<Integer> binXNew, ArrayList<Integer> binYNew) {
		binXNew.clear();
		binYNew.clear();
		
		ArrayList<Integer> binXOld = new ArrayList<Integer>();
		ArrayList<Integer> binYOld = new ArrayList<Integer>();
		
		for(int i: itemsUse) {
			if(binOfItem[i] == b_x) {
				binXOld.add(i);
			} else if(binOfItem[i] == b_y) {
				binYOld.add(i);
			}
		}
		int rBxOld = -1, rByOld = -1;
		if(binXOld.size() > 0) rBxOld = items[binXOld.get(0)].getR();
		if(binYOld.size() > 0) rByOld = items[binYOld.get(0)].getR();
				
		if(rBxOld == -1 && rByOld == -1) {
			return 100000;
		}
		double newViolation = 0;
		if(rBxOld != -1 && rByOld != -1 && rByOld != rBxOld) {
			for(int i: binXOld) binYNew.add(i);
			for(int i: binYOld) binXNew.add(i);
			newViolation = violations(binXNew, b_x) + violations(binYNew, b_y);
		} else {
			binXNew.clear();
			binYNew.clear();
			if(rBxOld == -1 && rByOld != -1) {
				//System.out.println("Max number items in bin " + b_x);
				ArrayList<Integer> allItemTwoBin = new ArrayList<Integer>();
				for(int i: binXOld) allItemTwoBin.add(i);
				for(int i: binYOld) allItemTwoBin.add(i);

				MaxNumberItemAbin sol = new MaxNumberItemAbin(items, bins, newBinIndices, seed);
				sol.search(5, 5000, 1500, 100, b_x, allItemTwoBin, binXNew, binYNew);
				newViolation = violations(binXNew, b_x) + violations(binYNew, b_y);
			} else if(rBxOld != -1 && rByOld == -1) {
				//System.out.println("Max number items in bin " + b_y);
				ArrayList<Integer> allItemTwoBin = new ArrayList<Integer>();
				for(int i: binXOld) allItemTwoBin.add(i);
				for(int i: binYOld) allItemTwoBin.add(i);
				
				MaxNumberItemAbin sol = new MaxNumberItemAbin(items, bins, newBinIndices, seed);
				sol.search(5, 5000, 1500, 100, b_y, allItemTwoBin, binYNew, binXNew);
				newViolation = violations(binXNew, b_x) + violations(binYNew, b_y);
			} else {
				//System.out.println("Balance 2 bin " + b_x + "-" + b_y);
				// Chi su dung 1 bin x
				ArrayList<Integer> allItemTwoBin = new ArrayList<Integer>();
				for(int i: binXOld) allItemTwoBin.add(i);
				for(int i: binYOld) allItemTwoBin.add(i);

				ArrayList<Integer> binXNew1 = new ArrayList<Integer>();
				ArrayList<Integer> binYNew1 = new ArrayList<Integer>();
				MaxNumberItemAbin sol1 = new MaxNumberItemAbin(items, bins, newBinIndices, seed);
				sol1.search(5, 5000, 1500, 100, b_x, allItemTwoBin, binXNew1, binYNew1);
				double newViolation1 = violations(binXNew1, b_x) + violations(binYNew1, b_y);

				// Chi su dung 1 bin y	
				ArrayList<Integer> binXNew2 = new ArrayList<Integer>();
				ArrayList<Integer> binYNew2 = new ArrayList<Integer>();
				MaxNumberItemAbin sol2 = new MaxNumberItemAbin(items, bins, newBinIndices, seed);
				sol2.search(5, 5000, 1500, 100, b_y, allItemTwoBin, binYNew2, binXNew2);
				double newViolation2 = violations(binXNew2, b_x) + violations(binYNew2, b_y);
				
				binXNew.clear();
				binYNew.clear();
				if(newViolation1 < newViolation2) {
					newViolation = newViolation1;
					for(int i: binXNew1) binXNew.add(i);
					for(int i: binYNew1) binYNew.add(i);
				} else {
					newViolation = newViolation2;
					for(int i: binXNew2) binXNew.add(i);
					for(int i: binYNew2) binYNew.add(i);
				}
			}
		}
		double oldViolation = violations(binXOld, b_x) + violations(binYOld, b_y);	
		
		return newViolation - oldViolation;
	}
	
	private void restartMaintainConstraint(int[][] tabu) {
		ArrayList<Integer> binXNew = new ArrayList<Integer>();
		ArrayList<Integer> binYNew = new ArrayList<Integer>();
		Collections.shuffle(binsUse, new Random(seed));
		int len = binsUse.size();
		for(int k = 0; k < len/2; k++) {
			binXNew.clear();
			binYNew.clear();
			double delta = getSwapDelta(binsUse.get(k), binsUse.get(len - k - 1), binXNew, binYNew);
			if(delta <= 0) {
				for(int i: binXNew) binOfItem[i] = binsUse.get(k);
				for(int i: binYNew) binOfItem[i] = binsUse.get(len - k - 1);
			}		
		}
		
		for (int i = 0; i < tabu.length; i++) {
			for (int j = 0; j < tabu[i].length; j++)
				tabu[i][j] = -1;
		}
		
	}
	
	public void updateBest(){
		
	}
	
	public void tabuSearch(int tabulen, int maxTime, int maxIter, int maxStable, ArrayList<Integer> binsUse, ArrayList<Integer> itemsUse) {
		double sumV = 0;
		double t0 = System.currentTimeMillis();
		int minB = Integer.MAX_VALUE, maxB = Integer.MIN_VALUE;
		int minI = Integer.MAX_VALUE, maxI = Integer.MIN_VALUE;
		ArrayList<Integer> binXNew = new ArrayList<Integer>();
		ArrayList<Integer> binYNew = new ArrayList<Integer>();
		
		binsUse.sort(new Comparator<Integer>() {
			@Override
			public int compare(Integer b1, Integer b2) {
				// TODO Auto-generated method stub
				double vio1 = violations(b1);
				double vio2 = violations(b2);
				double r = vio2 - vio1;
				if (r > 0) {
					return 1;
				} else if (r < 0) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		
		for(int b: binsUse) {
			if(minB > b) minB = b;
			if(maxB < b) maxB = b;
		}
		for(int i: itemsUse) {
			if(minI > i) minI = i;
			if(maxI < i) maxI = i;
		}
		int DB = maxB - minB;
		int DI = maxI - minI;
		// System.out.println("n = " + n + ", D = " + D);
		int tabu[][] = new int[DB + 1][DB + 1];
		for (int i = 0; i <= DB; i++)
			for (int v = 0; v <= DB; v++)
				tabu[i][v] = -1;

		int it = 0;
		maxTime = maxTime * 1000;// convert into milliseconds

		double best = violations();
		int[] x_best = new int[maxI + 1];
		for (int i:itemsUse)
			x_best[i] = binOfItem[i];

		System.out.println("TabuSearch, init S = " + best);
		int nic = 0;
		ArrayList<SwapMove> moves = new ArrayList<SwapMove>();
		Random R = new Random(seed);
		ArrayList<Integer> binsUseVail = new ArrayList<Integer>(binsUse);
		System.out.println("bin use size: " + binsUse.size());
		while (it < maxIter && System.currentTimeMillis() - t0 < maxTime
				&& (sumV = violations()) > 0) {
			double minDelta = Double.MAX_VALUE;
			moves.clear();
			
			ArrayList<Integer> maxVioBin = new ArrayList<Integer>();
			double maxVio = -1;
			if(binsUseVail.size() == 0) {
				binsUseVail.addAll(binsUse);
			}
			for (int b: binsUseVail) {
				double vio = violations(b);
				
				//if(maxVio < vio) {
					//maxVio = vio;
					//maxVioBin.clear();
					//maxVioBin.add(b);
				if(vio > 0) {
					maxVioBin.add(b);
				}
			}
			System.out.println(maxVioBin);
			System.out.println("max vio = " + maxVio);
			int b_ucv = maxVioBin.get(R.nextInt(maxVioBin.size()));
			
			//binsUseVail.remove(new Integer(b_ucv));
			//b_ucv = 505;
			binXNew.clear();
			binYNew.clear();
			
			for(int b: binsUse) {
				//if(b == b_ucv) continue;
				//System.out.println("Processing bin " + b_ucv + " and " + b);
				
				binXNew.clear();
				binYNew.clear();
				double delta = getSwapDelta(b_ucv, b, binXNew, binYNew);	
				
				
				//System.out.println("Processing bin " + b_ucv + " and " + b + ": delta = " + delta);
				
				if(delta < 0) {
					System.out.println("Press Enter to continue " + delta);
					try{System.in.read();}
					catch(Exception e){}
				}
				
				
				if (tabu[b_ucv - minB][b - minB] <= it || sumV + delta < best) {
					if (delta < minDelta) {
						minDelta = delta;
						moves.clear();
						moves.add(new SwapMove(b_ucv, b, binXNew, binYNew));
					} else if (delta == minDelta) {
						moves.add(new SwapMove(b_ucv, b, binXNew, binYNew));
					}
				}
			}
			

			if (moves.size() <= 0) {
				System.out.println("TabuSearch::restart.....");
				restartMaintainConstraint(tabu);
				nic = 0;
			} else {
				// perform the move
				SwapMove m = moves.get(R.nextInt(moves.size()));
				int b_x = m.b_x;
				int b_y = m.b_y;
				
				for(int i: m.binXNew) binOfItem[i] = b_x;
				for(int i: m.binYNew) binOfItem[i] = b_y;
				
				tabu[b_x - minB][b_y - minB] = it + tabulen;
				sumV = violations();
				System.out.println("Step " + it + ", S = " + sumV
						+ ", best = " + best + ", delta = " + minDelta
						+ ", nic = " + nic);
				System.out.println("Balance bx = " + b_x + " by = " + b_y);
				// update best
				if (sumV < best) {
					best = sumV;
					for (int i:itemsUse) x_best[i] = binOfItem[i];
				}

				//if (minDelta >= 0) {
				if(sumV >= best){
					nic++;
					if (nic > maxStable) {
						System.out.println("TabuSearch::restart.....");
						restartMaintainConstraint(tabu);
						nic = 0;
					}
				} else {
					nic = 0;
				}
			}
			it++;
		}
		System.out.println("Step " + it + ", S = " + violations());
		for (int i:itemsUse) binOfItem[i] = x_best[i];
	}
	
	public void loadPretrainedModel(String path, int r) {
		System.out.println(path);
		File file = new File(path);
		
		try {
	        Scanner sc = new Scanner(file);
	        int i = 0;
	        
	        while (sc.hasNextLine()) {
	        	String[] tmp = sc.nextLine().split(" ");
	        	int b = Integer.parseInt(tmp[0]);
	        	for(int k = 1; k < tmp.length; k++) {
	        		i = Integer.parseInt(tmp[k]);
	        		binOfItem[i] = b;
	        	}
	        }
	        
	        sc.close();
	    } 
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }

	}
	/*
	public double testSwapDelta(int b_x, int b_y) {
		ArrayList<Integer> binXNew = new ArrayList<Integer>();
		ArrayList<Integer> binYNew = new ArrayList<Integer>();
		
		ArrayList<Integer> binXOld = new ArrayList<Integer>();
		ArrayList<Integer> binYOld = new ArrayList<Integer>();
		
		for(int i: itemsUse) {
			if(binOfItem[i] == b_x) {
				binXOld.add(i);
			} else if(binOfItem[i] == b_y) {
				binYOld.add(i);
			}
		}
		int rBxOld = -1, rByOld = -1;
		if(binXOld.size() > 0) rBxOld = items[binXOld.get(0)].getR();
		if(binYOld.size() > 0) rByOld = items[binYOld.get(0)].getR();
		if(rBxOld == -1 && rByOld == -1) {
			return 0;
		}
		double newViolation = 0;
		
		if(rBxOld != -1 && rByOld != -1 && rByOld != rBxOld) {
			System.out.println("Swap bin: " + rByOld);
			for(int i: binXOld) {
				binYNew.add(i);
			}
			for(int i: binYOld) {
				binXNew.add(i);
			}
			newViolation = violations(binXNew, b_x) + violations(binYNew, b_y);
		} else {
			binXNew.clear();
			binYNew.clear();
			if(rBxOld == -1 && rByOld != -1) {
				if(bins[b_x].getMinLoad() >= bins[b_y].getMinLoad()) {
					return 0;
				}
				System.out.println("Max item:");
				ArrayList<Integer> allItemTwoBin = new ArrayList<Integer>();
				for(int i: binXOld) allItemTwoBin.add(i);
				for(int i: binYOld) allItemTwoBin.add(i);
				
				maxNumItemInABin(5, 5000, 500, 100, b_x, allItemTwoBin, binXNew, binYNew);
				newViolation = violations(binXNew, b_x) + violations(binYNew, b_y);
			} else if(rBxOld != -1 && rByOld == -1) {
				if(bins[b_x].getMinLoad() <= bins[b_y].getMinLoad()) {
					return 0;
				}
				System.out.println("Max item:");
				ArrayList<Integer> allItemTwoBin = new ArrayList<Integer>();
				for(int i: binXOld) allItemTwoBin.add(i);
				for(int i: binYOld) allItemTwoBin.add(i);
				//System.out.println(binXOld);
				maxNumItemInABin(5, 5000, 500, 100, b_y, allItemTwoBin, binYNew, binXNew);
				//System.out.println(binXNew);
				//System.out.println(binYNew);
				//System.out.println(violations(binXNew, b_x));
				//System.out.println(violations(binYNew, b_y));
				newViolation = violations(binXNew, b_x) + violations(binYNew, b_y);
			} else {
				System.out.println("Balance 2 bin " + b_x + "-" + b_y);
				// Chi su dung 1 bin x
				ArrayList<Integer> allItemTwoBin = new ArrayList<Integer>();
				for(int i: binXOld) allItemTwoBin.add(i);
				for(int i: binYOld) allItemTwoBin.add(i);
				ArrayList<Integer> binXNew1 = new ArrayList<Integer>();
				ArrayList<Integer> binYNew1 = new ArrayList<Integer>();
				maxNumItemInABin(5, 5000, 1000, 100, b_x, allItemTwoBin, binXNew1, binYNew1);
				double newViolation1 = violations(binXNew1, b_x) + violations(binYNew1, b_y);

				// Chi su dung 1 bin y	
				ArrayList<Integer> binXNew2 = new ArrayList<Integer>();
				ArrayList<Integer> binYNew2 = new ArrayList<Integer>();
				maxNumItemInABin(5, 5000, 1000, 100, b_y, allItemTwoBin, binYNew2, binXNew2);
				double newViolation2 = violations(binXNew2, b_x) + violations(binYNew2, b_y);
				
				int use = -1;
				if(newViolation1 < newViolation2) {
					newViolation = newViolation1;
					use = b_x;
				} else {
					newViolation = newViolation2;
					use = b_y;
				}
				
				// Su dung ca 2 bin
				balanceTwoBin(5, 5000, 1000,
						100, b_x, b_y, 
						binXOld, binYOld,
						binXNew, binYNew) ;
				
				double newViolation3 = violations(binXNew, b_x) + violations(binYNew, b_y);
				
				System.out.println(newViolation);
				System.out.println(newViolation3);
				if(newViolation3 < newViolation) {
					newViolation = newViolation3;
				} else {
					binXNew.clear();
					binYNew.clear();
					if(use == b_x) {
						for(int i: binXNew1) binXNew.add(i);
						for(int i: binYNew1) binYNew.add(i);
					} else {
						for(int i: binXNew2) binXNew.add(i);
						for(int i: binYNew2) binYNew.add(i);
					}
				}
			}
			
		}
		
		double oldViolation = violations(binXOld, b_x) + violations(binYOld, b_y);
		System.out.println(oldViolation);
		return newViolation - oldViolation;
	}
	*/
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
		SolutionPhase2  solution = new SolutionPhase2();

		// solution.loadData("src/khmtk60/miniprojects/multiknapsackminmaxtypeconstraints/MinMaxTypeMultiKnapsackInput.json");
		solution.loadData(
				"./dataset/MinMaxTypeMultiKnapsackInput-1000.json");
		solution.preprocess();
		solution.loadPretrainedModel();
		/*
		for(int b: solution.getBinsUse()) {
			System.out.println(b + " Test result: " + solution.testSwapDelta(1639, b));
		}*/
		//System.out.println(" Test result: " + solution.testSwapDelta(505, 514));
		
		solution.tabuSearch(10, 5000, 100, 10, solution.getBinsUse(), solution.getItemsUse()); // Cho tap du lieu 51004418316727.json
		solution.writeSolution();
		solution.writeSubmit();
		solution.printSolution();
	}

}
