package khmtk60.miniprojects.G11.src;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputBin;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputItem;

public class Solution1 {
	
	public static int LAMBDA = 10;
	public static int ITERATION = 1000;
	public static int RESTART_CYCLE = 10;
	
	int n, m;
	Item items[];
	ArrayList<Bin> bins;
	double violations;
	int bestResult = 0;
	int mt, mr;
	MinMaxTypeMultiKnapsackInput input;
	ArrayList<Bin> result = new ArrayList<>();
	
	public void readInput(String fileName) {
		input = new MinMaxTypeMultiKnapsackInput();
		String directory = new File(".").getAbsolutePath() + "/bin/khmtk60/miniprojects/multiknapsackminmaxtypeconstraints";
		input = input.loadFromFile(directory + "/" + fileName);
		n = input.getItems().length;
		m = input.getBins().length;
		mt = 0;
		mr = 0;
		for (MinMaxTypeMultiKnapsackInputItem item: input.getItems()) {
			mt = Math.max(mt, item.getT() + 1);
			mr = Math.max(mr, item.getR() + 1);
		}
		
		this.bins = new ArrayList<>();
		this.items = new Item[n];
		
		for (int i = 0; i < m; ++i) {
			MinMaxTypeMultiKnapsackInputBin bin = input.getBins()[i];
			bins.add(new Bin(i, bin.getCapacity(), bin.getMinLoad(), bin.getP(), bin.getT(), bin.getR(), mt, mr));
		}
		
		for (int i = 0; i < n; ++i) {
			MinMaxTypeMultiKnapsackInputItem item = input.getItems()[i];
			items[i] = new Item(i, item.getW(), item.getP(), item.getT(), item.getR());
			for (int binId: item.getBinIndices()) {
				bins.get(binId).candidateItems.add(items[i]);
				items[i].bins.add(bins.get(binId));
			}
		}
		
		
	}
	
	public void initializeRandomly() {
		violations = 0;
		Random rand = new Random();
		for (int i = 0; i < m; ++i) {
			Bin bin = bins.get(rand.nextInt(bins.size()));
			if (!bin.items.isEmpty()) {
				continue;
			}
			int sumW = 0;
			for (Item item: bin.candidateItems) {
				sumW += item.w;
			}
			if (sumW < bin.lw || bin.w < bin.lw || bin.p < 0) {
				continue;
			}
			int j = 0;
			while (j < bin.candidateItems.size()) {
				Item item = bin.candidateItems.get(j);
				if (item.bin == null && !bin.violateIfAddItem(item)) {
					this.assignValue(bin.candidateItems.get(j), bin);
				}
				j++;
			}
		}
		this.saveResult();
	}
		
	public void initialize() {
		violations = n;
		Iterator<Bin> iter = bins.iterator();
		while (iter.hasNext()) {
			
			Bin bin = iter.next();
			int sumW = 0;
			for (Item item: bin.candidateItems) {
				sumW += item.w;
			}
			if (sumW < bin.lw || bin.w < bin.lw || bin.p < 0) {
//				iter.remove();
				continue;
			}
			
			int j = 0;
			while (j < bin.candidateItems.size()) {
				Item item = bin.candidateItems.get(j);
				if (item.bin == null && !bin.violateIfAddItem(item)) {
					this.assignValue(bin.candidateItems.get(j), bin);
				}
				j++;
			}
		}
		this.saveResult();
	}
	
	private void saveResult() {
		int itemNum = 0;
		for (Bin bin: this.bins) {
			if (bin.violation < 1e-10) {
				itemNum += bin.items.size();
			}
		}
		if (itemNum > this.bestResult) {
			this.bestResult = itemNum;
			result = new ArrayList<>();
			for (Bin bin: bins) {
				result.add(bin.clone());
			}
		}
	}
	
	public Bin chooseMaxViolatedBin() {
		ArrayList<Bin> violatedBins = new ArrayList<>();
		double maxViolated = 0;
		for (Bin bin: bins) {
			if (bin.violation - maxViolated >= 10) {
				maxViolated = bin.violation;
				violatedBins = new ArrayList<>(Arrays.asList(bin));
			} else if (Math.abs(bin.violation - maxViolated) < 10) {
				violatedBins.add(bin);
			}
		}
		return violatedBins.get(new Random().nextInt(violatedBins.size()));
	}
	
	public Bin chooseRandomBin() {
		ArrayList<Bin> violatedBins = new ArrayList<>();
		for (Bin bin: bins) {
			if (bin.violation > 0) {
				violatedBins.add(bin);
			}
		}
		return violatedBins.get(new Random().nextInt(violatedBins.size()));
	}
	
	public double getAssignDelta(Item item, Bin newBin) {
		Bin oldBin = item.bin;
		double delta = 0;
		if (newBin != null) {
			delta += newBin.getAddItemDelta(item);
		}
		if (oldBin != null) {
			delta += oldBin.getRemoveItemDelta(item);
		}
		return delta;
	}
	
	public void assignValue(Item item, Bin newBin) {
		Bin oldBin = item.bin;
		item.bin = newBin;
		
		if (oldBin != null) {
			violations -= oldBin.violation;
			oldBin.removeItem(item);
			violations += oldBin.violation;
		} else {
			violations --;
		}
		
		if (newBin != null) {
			violations -= newBin.violation;
			newBin.addItem(item);
			violations += newBin.violation;
		} else {
			violations++;
		}

	}
	
	public void printOutput(String fileName) {
		int binOfItems[] = new int[n];
		for (int i = 0; i < binOfItems.length; ++i) {
			binOfItems[i] = -1;
		}
		int itemNumber = 0;
		for (Bin bin: result) {
			
			if (!bin.items.isEmpty()) {
				if (bin.violation < 1e-10) {
					for (Item item: bin.items) {
						binOfItems[item.index] = bin.index;
					}
					itemNumber += bin.items.size();
				}
				System.out.print("Bin " + bin.index + ": ");
				System.out.print("S = " + bin.violation + ", "
						+ "w = " + bin.getW() + "/" + bin.lw + "->" + bin.w 
						+ ", p = " + bin.getP() + "/" + bin.p
						+ ", t = " + bin.currentT + "/" + bin.t
						+ ", r = " + bin.currentR + "/" + bin.r + ": ");
				for (Item item: bin.items) {
					System.out.print(item.index + ", ");
				}
				System.out.println();
			}
			
		}
		
		System.out.println("Total item: " + itemNumber);
		String directory = new File(".").getAbsolutePath() + "/src/khmtk60/miniprojects/G11/result/";
		try (FileWriter fileWriter = new FileWriter(directory + fileName)) {
			fileWriter.write("{\"binOfItem\":[");
			boolean first = true;
			for (int binOfItem: binOfItems) {
				if (first) {
					first = false;
				} else {
					fileWriter.write(",");
				}
				fileWriter.write(binOfItem + "");
			}
			fileWriter.write("]}");
			fileWriter.close();
        } catch (Exception e) {
            System.out.println(e);
        }
		
	}
	
	public RemoveBinMove moveBinToOtherBin() {
		Bin binToRemove = this.chooseMaxViolatedBin();
		Random rand = new Random();
		Bin[] newBins = new Bin[binToRemove.items.size()];
		
		int i = 0;
//		ArrayList<Item> items = new ArrayList<>();
//		for (Item item: binToRemove.items) {
//			items.add(item);
//		}
		for (Item item: binToRemove.items) {
			double minViolations = Double.MAX_VALUE;
			ArrayList<Bin> candidate = new ArrayList<>();
			
			for (Bin bin: item.bins) {
				if (bin.index != binToRemove.index && !bin.violateIfAddItem(item)) {
					double tempViolationDelta = this.getAssignDelta(item, bin);
					if (tempViolationDelta < minViolations) {
						candidate = new ArrayList<>(Arrays.asList(bin));
						minViolations = tempViolationDelta;
					} else if (tempViolationDelta == minViolations) {
						candidate.add(bin);
					}
				}
			}
			if (!candidate.isEmpty()) {
				newBins[i++] = candidate.get(rand.nextInt(candidate.size()));
			} else {
				newBins[i++] = null;
			}
		}
		return new RemoveBinMove(binToRemove, newBins);
	}
	
	public AssignMove moveItemToOtherBin() {
		Bin chosenBin = this.chooseRandomBin();
		Item chosenItem;
		Random rand = new Random();
		
		double minViolations = Double.MAX_VALUE;
		ArrayList<Item> candidate = new ArrayList<>();
		for (Item item: chosenBin.candidateItems) {
			if (item.in(chosenBin)) {
				continue;
			}
			
			double tempViolationDelta = this.getAssignDelta(item, chosenBin);
			if (tempViolationDelta < minViolations) {
				candidate = new ArrayList<>(Arrays.asList(item));
				minViolations = tempViolationDelta;
			} else if (tempViolationDelta == minViolations) {
				candidate.add(item);
			}
		}
		
		if (!candidate.isEmpty()) {
			chosenItem = candidate.get(rand.nextInt(candidate.size()));
			return new AssignMove(chosenItem, chosenBin);
		}
		return new AssignMove();
	}
	
	public void breakBinAndReassign() {
		Bin chosenBin = this.chooseMaxViolatedBin();
		violations -= chosenBin.violation;
		for (Item item: chosenBin.items) {
			for (Bin bin: this.bins) {
				if (bin.index != chosenBin.index) {
					
				}
			}
		}
		violations += chosenBin.violation;
	}
	
	public void search() {
		System.out.println("S=" + this.violations);
		
		int it = 0;
		while (it < ITERATION && violations > 0) {
			it++;
			if (it % RESTART_CYCLE == 0) {
				RemoveBinMove move = this.moveBinToOtherBin();
				System.out.print("Step " + it + ": S = " + this.violations + ", bin " + move.binToRemove.index + " removed, ");
				ArrayList<Item> items = new ArrayList<>();
				for (int i = 0; i < move.binToRemove.items.size(); ++i) {
					items.add(move.binToRemove.items.get(i));
					if (move.newBins[i] != null) {
						System.out.print(move.binToRemove.items.get(i) + "->" + move.newBins[i].index + ", ");
					} else {
						System.out.print(move.binToRemove.items.get(i) + " removed ");
					}
				}
				int i = 0;
				for (Item item: items) {
					this.assignValue(item, move.newBins[i++]);
				}
				System.out.println();
				
				this.saveResult();
			} else {
				AssignMove move = this.moveItemToOtherBin();
				if (move.bin == null) {
					continue;
				}
				this.assignValue(move.item, move.bin);
				System.out.println("Step " + it + ": S = " + this.violations + ", x[" + move.item.index + "] = " + move.bin.index);
				this.saveResult();
			}
		}
		
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution1 demo = new Solution1();
		demo.readInput("MinMaxTypeMultiKnapsackInput.json");
		demo.initialize();
		demo.search();
		demo.printOutput("MinMaxTypeMultiKnapsackOutput-solution1-100.json");
	}

}
