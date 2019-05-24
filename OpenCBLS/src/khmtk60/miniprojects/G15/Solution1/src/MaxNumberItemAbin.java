package khmtk60.miniprojects.G15.Solution1.src;
import java.util.*;
public class MaxNumberItemAbin extends Solution {
	int DEBUG = 0;
	ArrayList<Integer> itemsAvail = new ArrayList<Integer>();
	ArrayList<Integer> itemsNotAvail = new ArrayList<Integer>();
	public MaxNumberItemAbin(MinMaxTypeMultiKnapsackInputItem items[], MinMaxTypeMultiKnapsackInputBin bins[],
								HashSet<Integer> newBinIndices[], int seed) {
		this.items = items;
		this.bins = bins;
		this.newBinIndices = newBinIndices;
		this.seed = seed;
	}

	public double violations(int b) {
		return -10000000;
	}

    public double violations(ArrayList<Integer> a, int b) {
    	return -10000000;
    }

	private double getAssignDeltaOfMaxNumItemInABin(int oldChoice, int newChoice, StatusOfBin status, int i, int nItem) {
		double vioOld = violationsOfMaxNumItemInABin(status, nItem);
		if(oldChoice == 0 && newChoice == 1) {
			status.addItem(i);
		} else if(oldChoice == 1 && newChoice == 0) {
			status.removeItem(i);
		} else {
			return 0;
		}
		double vioNew = violationsOfMaxNumItemInABin(status, nItem);
		if(oldChoice == 0 && newChoice == 1) {
			status.removeItem(i);
		} else if(oldChoice == 1 && newChoice == 0) {
			status.addItem(i);
		}
		return vioNew - vioOld;
	}

	private double violationsOfMaxNumItemInABin(StatusOfBin status, int numItem) {
		double sumViolation = 0;
		int b = status.b;

		sumViolation += Math.max(0, status.sumW - bins[b].getCapacity());
		sumViolation += Math.max(0, bins[b].getMinLoad() - status.sumW);
		sumViolation += Math.max(0, status.sumP - bins[b].getP());
		sumViolation += Math.max(0, status.nType - bins[b].getT());
		if(bins[b].getMinLoad() - status.sumW > 0) {
			sumViolation += 0.5;
		}
		sumViolation += (double)(numItem - status.nItem)/numItem;
		//System.out.println("Hehe " +  Math.max(0, bins[b].getMinLoad() - status.sumW));

		return sumViolation;
	}

	private void restartOfMaxNumItemInABin(int[][] tabu, StatusOfBin status, ArrayList<Integer> itemsUse, int x_take[]) {
		Random rand = new Random(this.seed);
		for (int k = 0; k < itemsUse.size(); k++) {
			int i = itemsUse.get(k);
			java.util.ArrayList<Integer> L = new java.util.ArrayList<Integer>();
			for (int choice = 0; choice < 2; choice++) {
				if (getAssignDeltaOfMaxNumItemInABin(x_take[k], choice, status, i, itemsUse.size()) <= 0) L.add(choice);
			}
			
			if(L.size() == 0) continue;
			int newChoice = L.get(rand.nextInt(L.size()));
			if(x_take[k] == 0 && newChoice == 1) {
				status.addItem(i);
			} else if(x_take[k] == 1 && newChoice == 0) {
				status.removeItem(i);
			}
			x_take[k] = newChoice;
		}
		
		for (int i = 0; i < tabu.length; i++) 
			for (int j = 0; j < tabu[i].length; j++) 
				tabu[i][j] = -1;
	}

	public void search(int tabulen, int maxTime, int maxIter, int maxStable, int b, 
				ArrayList<Integer> itemsUse, ArrayList<Integer> newBin, ArrayList<Integer> otherBin) {
		
		for(int i: itemsUse) {
			if(newBinIndices[i].contains(bins[b].getId())) {
				itemsAvail.add(i);
			} else {
				itemsNotAvail.add(i);
			}
		}

		int numItem = itemsAvail.size();
		
		double t0 = System.currentTimeMillis();
		
		// System.out.println("n = " + n + ", D = " + D);
		int tabu[][] = new int[numItem][2];
		for (int i = 0; i < numItem; i++) 
			for (int j = 0; j < 2; j++) 
				tabu[i][j] = -1;

		int it = 0;
		maxTime = maxTime * 1000;// convert into milliseconds

		int[] x_best = new int[numItem];
		int[] x_take = new int[numItem];
		
		for (int k = 0; k < numItem; k++) x_take[k] = 1;
		for (int k = 0; k < numItem; k++) x_best[k] = x_take[k];
		StatusOfBin status = new StatusOfBin(itemsAvail, b);
		double best = violationsOfMaxNumItemInABin(status, numItem);
		double sumV = best;
		if (DEBUG == 1)
			System.out.println("Init S = " + sumV);

		int nic = 0;
		ArrayList<AssignMove> moves = new ArrayList<AssignMove>();
		Random R = new Random(this.seed);
		
		while (it < maxIter && System.currentTimeMillis() - t0 < maxTime
				&& sumV  > 0) {

			int sel_i = -1;
			int sel_v = -1;
			double minDelta = Double.MAX_VALUE;
			moves.clear();
			
			for (int k = 0; k < numItem; k++) {
				int i = itemsAvail.get(k);
				for (int choice = 0; choice < 2; choice++) {
					double delta = getAssignDeltaOfMaxNumItemInABin(x_take[k], choice, status, i, numItem);
					if(tabu[k][choice] <= it || sumV + delta < best) {
						if (delta < minDelta) {
							minDelta = delta;
							moves.clear();
							moves.add(new AssignMove(choice, x_take[k], k));
						} else if (delta == minDelta) {
							moves.add(new AssignMove(choice, x_take[k], k));
						}
					}
				}
			}
			
			if (moves.size() <= 0) {
				if(DEBUG == 1) {
					System.out.println("maxNumItemInABin::TabuSearch::restart.....");
				}
				
				restartOfMaxNumItemInABin(tabu, status, itemsAvail, x_take);
				nic = 0;
			} else {
				// perform the move
				AssignMove m = moves.get(R.nextInt(moves.size()));
				sel_i = m.i;
				sel_v = m.newBin;
				if(x_take[sel_i] == 0 && sel_v == 1) {
					status.addItem(itemsAvail.get(sel_i));
				} else if(x_take[sel_i] == 1 && sel_v == 0) {
					status.removeItem(itemsAvail.get(sel_i));
				}
				x_take[sel_i] = sel_v;
				
				tabu[sel_i][sel_v] = it + tabulen;
				sumV = violationsOfMaxNumItemInABin(status, numItem);
				if(DEBUG == 1) {
					System.out.println("maxNumItemInABin::Step " + it + ", "
							+ "S = " + sumV
							+ ", best = " + best + ", delta = " + minDelta
							+ ", nic = " + nic);
				}
				
				// update best
				if (sumV < best) {
					best = sumV;
					for (int k = 0; k < numItem; k++) x_best[k] = x_take[k];
				}

				//if (minDelta >= 0) {
				if(sumV >= best){
					nic++;
					if (nic > maxStable) {
						if(DEBUG == 1) {
							System.out.println("maxNumItemInABin::TabuSearch::restart.....");
						}
						
						restartOfMaxNumItemInABin(tabu, status, itemsAvail, x_take);
						nic = 0;
					}
				} else {
					nic = 0;
				}
			}
			it++;
		}
		newBin.clear();
		otherBin.clear();
		for (int k = 0; k < numItem; k++) {
			if(x_best[k] == 1) {
				newBin.add(itemsAvail.get(k));
			} else {
				otherBin.add(itemsAvail.get(k));
			}
		}
		for(int i: itemsNotAvail) {
			otherBin.add(i);
		}
		//System.out.println("Max number items in bin " + b);
	}	
}