package khmtk60.miniprojects.G17;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class State {
	Bin[] bins;
	Item[] items;
	MinMaxTypeMultiKnapsackInput input;
	HashSet<Integer> itemUsed;
	HashSet<Integer> binUsed;

	public void initialize(String filepath) {
		MinMaxTypeMultiKnapsackInput a = new MinMaxTypeMultiKnapsackInput();
		input = a.loadFromFile(filepath);
		MinMaxTypeMultiKnapsackInputItem[] its = input.getItems();
		MinMaxTypeMultiKnapsackInputBin[] bns = input.getBins();

		items = new Item[its.length];
		bins = new Bin[bns.length];
		itemUsed = new HashSet<Integer>();
		binUsed = new HashSet<Integer>();

		double[] cumulatedW = new double[19];

		for (int i = 0; i < its.length; ++i)
			cumulatedW[its[i].getR()] += its[i].getW();

		double maxW = -1;

		for (int i = 0; i < 19; ++i)
			maxW = Math.max(maxW, cumulatedW[i]);

		for (int i = 0; i < its.length; ++i)
			items[i] = new Item(its[i].getW(), its[i].getP(), its[i].getT(), its[i].getR(), its[i].getBinIndices(), i);

		for (int j = 0; j < bns.length; ++j)
			bins[j] = new Bin(bns[j].getCapacity(), bns[j].getMinLoad(), bns[j].getP(), bns[j].getT(), bns[j].getR(),
					j, bns[j].getMinLoad() < maxW);

		System.out.println(violations());
	}

	public void loadBinOfItems(int[] binOfItems) {
		for(int i = 0; i < items.length; ++i)
			if (binOfItems[i] != -1)
				bins[binOfItems[i]].addItem(items[i]);
	}

	public double violations() {
		double violations = 0;

		for (int i = 0; i < items.length; ++i)
			violations += items[i].violations();

		for (int j = 0; j < bins.length; ++j)
			violations += bins[j].violations();

		return violations;
	}

	public double getAssignViolationDelta(Item item, Bin bin) {
		double vioB = 0, vioA = 0;

		if (item.getAssignTo() == bin.getIdx())
			return 0;
		if (item.getAssignTo() == -1) {
			vioB = item.violations() + bin.violations();

			bin.addItem(item);
			vioA = item.violations() + bin.violations();
			
			bin.removeItem(item);
		}
		else {
			int prevBin = item.getAssignTo();

			vioB = bins[prevBin].violations() + item.violations() + bin.violations();

			bins[prevBin].removeItem(item);
			bin.addItem(item);

			vioA = bins[prevBin].violations() + item.violations() + bin.violations();

			bin.removeItem(item);
			bins[prevBin].addItem(item);
		}
		
		return vioB - vioA;
	}

	public boolean maintainViolationMove(Item item, Bin bin) {
		double wBefore = 0, wAfter = 0;

		if (item.getAssignTo() == bin.getIdx())
			return false;
		
		int prevBin = item.getAssignTo();

		wBefore = Math.max(bin.spareW(), bins[prevBin].spareW());

		bins[prevBin].removeItem(item);
		bin.addItem(item);
		
		wAfter = Math.max(bin.spareW(), bins[prevBin].spareW());

		if (bin.violations() != 0 || bins[prevBin].violations() != 0) {
			bin.removeItem(item);
			bins[prevBin].addItem(item);
			return false;
		}

		bin.removeItem(item);
		bins[prevBin].addItem(item);
		return wAfter > wBefore;
	}

	public void moveItem(int i, int b) {
		if (items[i].getAssignTo() == bins[b].getIdx())
			return;

		if (items[i].getAssignTo() == -1)
			bins[b].addItem(items[i]);
		else {
			bins[items[i].getAssignTo()].removeItem(items[i]);
			bins[b].addItem(items[i]);
		}
		items[i].setAssignTo(b);
	}

	// reset cac bin violation > 0 de giai quyet viec search bi tac
	public void clearViolatedBins() {
		itemUsed.clear();
		binUsed.clear();

		for (int j = 0; j < bins.length; ++j)
			if (bins[j].violations() == 0) {
				binUsed.add(j);
				for (int i = 0; i < items.length; ++i)
					if (items[i].getAssignTo() == j)
						itemUsed.add(i);
			}

		for (int i = 0; i < items.length; ++i)
			if (!itemUsed.contains(i))
				if (items[i].getAssignTo() != -1)
					bins[items[i].getAssignTo()].removeItem(items[i]);
	}

	public void hillClimbing(int maxIter) {
		ArrayList<AssignMove> cand = new ArrayList<AssignMove>();

		Random R = new Random();
		int it = 0;
		HashSet<Integer> excludedBin = new HashSet<Integer>();

		while (it < maxIter) {
			cand.clear();
			double maxDelta = Double.MIN_VALUE;

			// tim bin co violatin thap de put item vao
			ArrayList<Integer> binCand = new ArrayList<Integer>();
			double minVio = Double.MAX_VALUE;
			for (int i = 0; i < bins.length; i++)
			{
				double binVio = bins[i].violations();
				if (binVio > 0 && !excludedBin.contains(i) && bins[i].isUsable()) {
					if (binVio < minVio) {
						binCand.clear();
						binCand.add(i);
						minVio = binVio;
					} else if (binVio == minVio) {
						binCand.add(i);
					}
				}
			}

			if (binCand.size() == 0)
				break;
			int binIdx = binCand.get(R.nextInt(binCand.size()));

			// search item de put vao bin
			for (int i = 0; i < items.length; i++) {
				int[] binIndices = items[i].getBinIndices();
				boolean in = false;
				for (int j = 0; j < binIndices.length; ++j)
					if (binIndices[j] == binIdx)
						in = true;
				if (in) {
					if (items[i].getAssignTo() == -1)
					{
						double d = getAssignViolationDelta(items[i], bins[binIdx]);
						if (d >= 0) {
							if (d > maxDelta) {
								cand.clear();
								cand.add(new AssignMove(i, binIdx));
								maxDelta = d;
							} else if (d == maxDelta) {
								cand.add(new AssignMove(i, binIdx));
							}
						}
					}
					else {
						double oldVio = bins[items[i].getAssignTo()].violations();
						double d = getAssignViolationDelta(items[i], bins[binIdx]);
						if (d <= oldVio && d >= 0) {
							if (d > maxDelta) {
								cand.clear();
								cand.add(new AssignMove(i, binIdx));
								maxDelta = d;
							} else if (d == maxDelta) {
								cand.add(new AssignMove(i, binIdx));
							}
						}
					}
				}
			}

			if (cand.size() == 0) {
				excludedBin.add(binIdx);
				continue;
			}
			int candIdx = R.nextInt(cand.size());
			AssignMove m = cand.get(candIdx);

			moveItem(m.i, m.b);
			// System.out.println(bins[m.b].violations());
			// System.out.println("Step " + it + ", violations = " + violations() + ", size
			// = " + cand.size());

			it++;
		}
	}

	public void increaseSpareW() {
		boolean movable = true;

		while (movable) {
			movable = false;
			for (int i = 0; i < items.length; ++i)
				if (items[i].getAssignTo() != -1)
					for (int j : items[i].getBinIndices())
						if (bins[j].violations() == 0)
							if (maintainViolationMove(items[i], bins[j])) {
								movable = true;
								moveItem(i, j);
							}
		}
	}

	public void optimize() {
		Random R = new Random();

		for (int i = 0; i < items.length; ++i)
			if (items[i].getAssignTo() == -1) {
				ArrayList<Integer> cand = new ArrayList<Integer>();
				for (int j : items[i].getBinIndices())
					if (bins[j].violations() == 0) {
						bins[j].addItem(items[i]);
						if (bins[j].violations() == 0)
							cand.add(j);
						bins[j].removeItem(items[i]);
					}
				if (cand.size() > 0) {
					int candIdx = R.nextInt(cand.size());
					bins[cand.get(candIdx)].addItem(items[i]);
					itemUsed.add(i);
				}
			}
	}

	public int[] getBinOfItems() {
		int[] binOfItems = new int[items.length];
		for (int i = 0; i < items.length; ++i)
			binOfItems[i] = items[i].getAssignTo();
		return binOfItems;
	}

	public int[] search() {
		int it = 0;
		int cnt = 0, maxItemUsed = 0;
		while (true) {
			System.out.println("[" + it + "]");
			hillClimbing(10000);

			clearViolatedBins();

			if (itemUsed.size() > maxItemUsed) {
				cnt = 1;
				maxItemUsed = itemUsed.size();
			} else
				cnt++;

			System.out.println(itemUsed.size());

			if (cnt > 150)
				break;
			
			it++;
		}

		maxItemUsed -= 1;

		while (itemUsed.size() > maxItemUsed) {
			maxItemUsed = itemUsed.size();
			increaseSpareW();
			optimize();
		}
		return getBinOfItems();
	}

	public void check() {
		int[] binOfItems = new int[items.length];
		Random R = new Random();
		String fileName = "src/khmtk60/miniprojects/G17/result/group-3000.out";
		try {

			FileReader fileReader = new FileReader(fileName);

			String line = null;

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			line = bufferedReader.readLine();
			int idx = 0;
			for (String w : line.split("\\s", 0)) {
				if (Integer.valueOf(w) != -1 && Integer.valueOf(w) < bins.length)
					bins[Integer.valueOf(w)].addItem(items[idx]);
				idx++;
				if (idx == items.length)
					break;
			}


			// Always close files.
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}

		for (int i = 0; i < items.length; ++i)
			if (items[i].getAssignTo() == -1) {
				ArrayList<Integer> cand = new ArrayList<Integer>();
				for (int j : items[i].getBinIndices())
					if (bins[j].violations() == 0) {
						bins[j].addItem(items[i]);
						if (bins[j].violations() == 0)
							cand.add(j);
						bins[j].removeItem(items[i]);
					}
				if (cand.size() > 0) {
					int candIdx = R.nextInt(cand.size());
					bins[cand.get(candIdx)].addItem(items[i]);
					itemUsed.add(i);
				}
			}

		for (int i = 0; i < items.length; ++i) {
			System.out.print(items[i].getAssignTo() + " ");
			binOfItems[i] = items[i].getAssignTo();
		}
		System.out.println("");
		MinMaxTypeMultiKnapsackSolution solution = new MinMaxTypeMultiKnapsackSolution();
		solution.setBinOfItem(binOfItems);
		SolutionChecker SC = new SolutionChecker();
		System.out.println(SC.check(input, solution));
	}

	public static void main(String[] args) {
		String filein = "src/khmtk60/miniprojects/G17/data/MinMaxTypeMultiKnapsackInput-1000.json";
		filein = "src/khmtk60/miniprojects/G17/data/group-1000.json";

		State st = new State();
		st.initialize(filein);
		// st.search();
		st.check();
	}
}
