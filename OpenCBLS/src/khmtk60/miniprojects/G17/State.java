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

		for (int i = 0; i < its.length; ++i)
			items[i] = new Item(its[i].getW(), its[i].getP(), its[i].getT(), its[i].getR(), its[i].getBinIndices(), i);

		for (int j = 0; j < bns.length; ++j)
			bins[j] = new Bin(bns[j].getCapacity(), bns[j].getMinLoad(), bns[j].getP(), bns[j].getT(), bns[j].getR(),
					j);
		System.out.println(violations());
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

	public void hillClimbing(int maxIter) {
		ArrayList<AssignMove> cand = new ArrayList<AssignMove>();
		// HashSet<Pair<Integer, Integer>> tabu = new HashSet<Pair>();
		Random R = new Random();
		int it = 0;
		HashSet<Integer> excludedBin = new HashSet<Integer>();

		while (it < maxIter) {
			cand.clear();
			double maxDelta = Double.MIN_VALUE;
			ArrayList<Integer> binCand = new ArrayList<Integer>();
			double minVio = Double.MAX_VALUE;
			for (int i = 0; i < bins.length; i++)
			{
				double binVio = bins[i].violations();
				if (binVio > 0 && !excludedBin.contains(i)) {
					if (binVio < minVio) {
						binCand.clear();
						binCand.add(i);
						minVio = binVio;
					} else if (binVio == minVio) {
						binCand.add(i);
					}
				}
			}
//			int bestType = 100;
//			double bestVio = Double.MAX_VALUE;
//			for (int i = 0; i < bins.length; i++)
//				if (bins[i].getMinLoad() < 1000) {
//					double binVio = bins[i].violations();
//					int type = bins[i].getT();
//					if (binVio > 0 && !excludedBin.contains(i)) {
//						if (type < bestType) {
//							binCand.clear();
//							binCand.add(i);
//							bestType = type;
//							bestVio = binVio;
//						} else {
//							if (binVio < bestVio) {
//								binCand.clear();
//								binCand.add(i);
//								bestVio = binVio;
//							} else if (binVio == bestVio) {
//								binCand.add(i);
//							}
//						}
//					}
//				}
			if (binCand.size() == 0)
				break;
			int binIdx = binCand.get(R.nextInt(binCand.size()));
			// System.out.println(bins[binIdx].getT());
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
			// System.out.println(m.i + " " + m.b);
			moveItem(m.i, m.b);
			// System.out.println(bins[m.b].violations());
			// System.out.println("Step " + it + ", violations = " + violations() + ", size
			// = " + cand.size());

			it++;
		}
	}

	public void search() {
		int maxit = 1000, it = 0;
		Random R = new Random();

		while(it < maxit) {
			System.out.println("[" + it + "]");
			hillClimbing(10000);
			itemUsed.clear();
			binUsed.clear();
			// for (int i = 0; i < items.length; ++i)
			// System.out.print(bins[items[i].getAssignTo()].violations() + " ");
			//
			// for (int i = 0; i < items.length; ++i)
			// System.out.println(bins[items[i].getAssignTo()]._w + " " +
			// bins[items[i].getAssignTo()].getMinLoad());
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
			
			// for (int i = 0; i < items.length; ++i)
			// if (items[i].getAssignTo() == -1) {
			// ArrayList<Integer> cand = new ArrayList<Integer>();
			// for (int j : items[i].getBinIndices())
			// if (bins[j].violations() == 0) {
			// bins[j].addItem(items[i]);
			// if (bins[j].violations() == 0)
			// cand.add(j);
			// bins[j].removeItem(items[i]);
			// }
			// if (cand.size() > 0) {
			// int candIdx = R.nextInt(cand.size());
			// bins[cand.get(candIdx)].addItem(items[i]);
			// itemUsed.add(i);
			// }
			// }
			
			System.out.println(itemUsed.size());

			for (int i = 0; i < items.length; ++i)
				System.out.print(items[i].getAssignTo() + " ");

			System.out.println("");

			it++;
		}
		// System.out.println(bins[j]._w);

		// System.out.println(items[5].getAssignTo());
		//
		// bins[1].addItem(items[5]);
		//
		// System.out.println(items[5].getAssignTo());

		// 0, 1, 0, 1, 0, 2, 2, 1, 0, 2, 2, 0, 0, 1, 1, 2,
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
