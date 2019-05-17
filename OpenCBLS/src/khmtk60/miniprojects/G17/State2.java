package khmtk60.miniprojects.G17;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class State2 {
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
			bins[j] = new Bin(bns[j].getCapacity(), bns[j].getMinLoad(), bns[j].getP(), bns[j].getT(), bns[j].getR(), j,
					bns[j].getMinLoad() < maxW);

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
		} else {
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

	public void hillClimbing2(int maxIter) {
		ArrayList<AssignMove> cand = new ArrayList<AssignMove>();
		// HashSet<Pair<Integer, Integer>> tabu = new HashSet<Pair>();
		Random R = new Random();
		int it = 0;
		HashSet<Integer> excludedBin = new HashSet<Integer>();

		while (it < maxIter) {
			cand.clear();
			double maxDelta = Double.MIN_VALUE;
			double total_violation = 0;
			// tim bin co violatin thap de put item vao
			ArrayList<Integer> binCand = new ArrayList<Integer>();
			double minVio = Double.MAX_VALUE;
			for (int i = 0; i < bins.length; i++) {
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
			ArrayList<Integer> binCand_tmp = new ArrayList<Integer>();
			if (binCand.size() == 1) {
				binCand_tmp.clear();
				double minVio2 = Double.MAX_VALUE;
				for (int i = 0; i < bins.length; i++) {
					double binVio = bins[i].violations();
					if (i == binCand.get(0))
						continue;
					if (binVio > 0 && !excludedBin.contains(i)) {
						if (binVio < minVio2) {
							binCand_tmp.clear();
							binCand_tmp.add(i);
							minVio = binVio;
						} else if (binVio == minVio2) {
							binCand_tmp.add(i);
						}
					}
				}
				// System.out.println("binCand_tmp = "+binCand_tmp.size());
				if (binCand_tmp.size() == 0)
					break;
				int tmp = binCand_tmp.get(R.nextInt(binCand_tmp.size()));
				binCand.add(tmp);
			}

			// System.out.println("i = " + it + " binCand = " + binCand.size());
			if (binCand.size() < 2)
				break;
			// lay 2 bin ra neu co
			int binIdx = -1;
			int binIdx2 = -1;
			binIdx = binCand.get(R.nextInt(binCand.size()));
			binIdx2 = binCand.get(R.nextInt(binCand.size()));
			while (binIdx2 == binIdx) {
				binIdx2 = binCand.get(R.nextInt(binCand.size()));
			}
			// System.out.println("bin1 = " + binIdx + " bin2 = " + binIdx2 + "\n");
			// search item de put vao bin
			int item1 = -1;
			int item2 = -1;
			boolean in1 = false;
			boolean in2 = false;
			boolean in3 = false;
			boolean in4 = false;
			// Ä‘ang xÃ©t trÆ°á»�ng há»£p mÃ tá»“n táº¡i 2 bin nhÃ©.
			for (int i = 0; i < items.length * 2; i++) {
				item1 = R.nextInt(items.length);
				// for (int ii = i + 1; ii < items.length; ii++) {
				// item2 = ii;
					item2 = R.nextInt(items.length);
				while (item2 == item1)
					item2 = R.nextInt(items.length);
				int[] binIndices1 = items[item1].getBinIndices();
				int[] binIndices2 = items[item2].getBinIndices();
				for (int j = 0; j < binIndices1.length; j++) {
					if (binIndices1[j] == binIdx) {
						in1 = true;
					}
					if (binIndices1[j] == binIdx2) {
						in2 = true;
					}
					}
				for (int j = 0; j < binIndices2.length; j++) {
					if (binIndices2[j] == binIdx) {
						in3 = true;
						}
					if (binIndices2[j] == binIdx2) {
						in4 = true;
						}
				}

				// truong hop 2 item deu co the xep vao bat ky bin nao
				if (in1 && in2 && in3 && in4) {
					// truong hop ca 2 item deu chua duoc xep vao bin nao
					if (items[item1].getAssignTo() == -1 && items[item2].getAssignTo() == -1) {
						// System.out.println("truong hop 1");
						// d cang lon cang tot, nghia la cang giam duoc nhieu violation

						double d1 = getAssignViolationDelta(items[item1], bins[binIdx]);
						double d2 = getAssignViolationDelta(items[item2], bins[binIdx]);
						double d3 = getAssignViolationDelta(items[item1], bins[binIdx2]);
						double d4 = getAssignViolationDelta(items[item2], bins[binIdx2]);
						ArrayList<Double> d = new ArrayList<Double>();
						d.add(d1);
						d.add(d2);
						d.add(d3);
						d.add(d4);
						double max = Collections.max(d);
						// khi max = d1, nghia la se xep item1 vao bin[binIdx] va item2 vao bin[binIdx]
						if ((max == d1 && d4 >= 0 && d1 >= 0) || (max == d4 && d1 >= 0 && d4 >= 0)) {
							total_violation = d1 + d4;
							// kiem tra dieu kien, khi them no vao thi loi giai se tot len
							if (total_violation > maxDelta) {
								cand.clear();
								cand.add(new AssignMove(item1, binIdx));
								cand.add(new AssignMove(item2, binIdx2));
								maxDelta = total_violation;
							} else if (total_violation == maxDelta) {
								cand.add(new AssignMove(item1, binIdx));
								cand.add(new AssignMove(item2, binIdx2));
							}

						} else if ((max == d2 && d3 >= 0 && d2 >= 0) || (max == d3 && d2 >= 0 && d3 >= 0)) {
							total_violation = d2 + d3;
							// kiem tra dieu kien, khi them no vao thi loi giai se tot len
							if (total_violation > maxDelta) {
								// se tot hon phuong an truoc do
								cand.clear();
								cand.add(new AssignMove(item2, binIdx));
								cand.add(new AssignMove(item1, binIdx2));
								maxDelta = total_violation;
							} else if (total_violation == maxDelta) {
								cand.add(new AssignMove(item2, binIdx));
								cand.add(new AssignMove(item1, binIdx2));
							}

							}

					} else if (items[item1].getAssignTo() != -1 && items[item2].getAssignTo() == -1) {
						// System.out.println("truong hop 2");
						// item1 da duoc xep vao bin truoc do roi
						double oldVio = bins[items[item1].getAssignTo()].violations();
						double d1 = getAssignViolationDelta(items[item1], bins[binIdx]);
						double d2 = getAssignViolationDelta(items[item1], bins[binIdx2]);
						double d3 = getAssignViolationDelta(items[item2], bins[binIdx]);
						double d4 = getAssignViolationDelta(items[item2], bins[binIdx2]);
						ArrayList<Double> d = new ArrayList<Double>();
						// d.add(oldVio);
						d.add(d1);
						d.add(d2);
						d.add(d3);
						d.add(d4);
						double max = Collections.max(d);
						if ((max == d1 && d1 < oldVio && d4 >= 0) || (max == d4 && d1 < oldVio && d1 >= 0)) {
							// xep item1 vao bin1
							// xep item2 vao bin2
							total_violation = d1 + d4;
							// kiem tra dieu kien, khi them no vao thi loi giai se tot len
							if (total_violation > maxDelta) {
								// se tot hon phuong an truoc do
								cand.clear();
								cand.add(new AssignMove(item1, binIdx));
								cand.add(new AssignMove(item2, binIdx2));
								maxDelta = total_violation;
							} else if (total_violation == maxDelta) {
								cand.add(new AssignMove(item1, binIdx));
								cand.add(new AssignMove(item2, binIdx2));
							}
						} else if ((max == d2 && d2 < oldVio && d3 >= 0) || (max == d3 && d2 < oldVio && d2 >= 0)) {
							total_violation = d2 + d3;
							// kiem tra dieu kien, khi them no vao thi loi giai se tot len
							if (total_violation > maxDelta) {
								// se tot hon phuong an truoc do
								cand.clear();
								cand.add(new AssignMove(item2, binIdx));
								cand.add(new AssignMove(item1, binIdx2));
								maxDelta = total_violation;
							} else if (total_violation == maxDelta) {
								cand.add(new AssignMove(item2, binIdx));
								cand.add(new AssignMove(item1, binIdx2));
							}
							}

					} else if (items[item1].getAssignTo() == -1 && items[item2].getAssignTo() != -1) {
						// item2 da duoc xep vao bin truoc do roi
						// System.out.println("truong hop 3");
						double oldVio = bins[items[item2].getAssignTo()].violations();
						double d1 = getAssignViolationDelta(items[item2], bins[binIdx]);
						double d2 = getAssignViolationDelta(items[item2], bins[binIdx2]);
						double d3 = getAssignViolationDelta(items[item1], bins[binIdx]);
						double d4 = getAssignViolationDelta(items[item1], bins[binIdx2]);
						ArrayList<Double> d = new ArrayList<Double>();
						// d.add(oldVio);
						d.add(d1);
						d.add(d2);
						d.add(d3);
						d.add(d4);
						double max = Collections.max(d);
						// if (max == oldVio) {
						// // item2 khong duoc xep vao bin nao ca , khi Ä‘Ã³ khÃ´ng xáº¿p item21ná»¯a,
						// vÃ¬ khÃ´ng
						// // tá»“n táº¡i cáº·p
						// } else
						if ((max == d1 && d1 < oldVio && d4 >= 0) || (max == d4 && d1 < oldVio && d1 >= 0)) {
							// xep item2 vao bin1
							// xep item1 vao bin2
							total_violation = d1 + d4;
							// kiem tra dieu kien, khi them no vao thi loi giai se tot len
							if (total_violation > maxDelta) {
								// se tot hon phuong an truoc do
								cand.clear();
								cand.add(new AssignMove(item2, binIdx));
								cand.add(new AssignMove(item1, binIdx2));
								maxDelta = total_violation;
							} else if (total_violation == maxDelta) {
								cand.add(new AssignMove(item2, binIdx));
								cand.add(new AssignMove(item1, binIdx2));
							}
						} else if ((max == d2 && d2 < oldVio && d3 >= 0) || (max == d3 && d2 < oldVio && d2 >= 0)) {
							total_violation = d2 + d3;
							// kiem tra dieu kien, khi them no vao thi loi giai se tot len
							if (total_violation > maxDelta) {
								// se tot hon phuong an truoc do
								cand.clear();
								cand.add(new AssignMove(item1, binIdx));
								cand.add(new AssignMove(item2, binIdx2));
								maxDelta = total_violation;
							} else if (total_violation == maxDelta) {
								cand.add(new AssignMove(item1, binIdx));
								cand.add(new AssignMove(item2, binIdx2));
							}
							}
					} else if (items[item1].getAssignTo() != -1 && items[item2].getAssignTo() != -1) {
						// ca 2 item deu duoc xep vao bin truoc do roi
						// System.out.println("truong hop 4");
						double oldVio1 = bins[items[item1].getAssignTo()].violations();
						double oldVio2 = bins[items[item2].getAssignTo()].violations();
						double d1 = getAssignViolationDelta(items[item1], bins[binIdx]);
						double d2 = getAssignViolationDelta(items[item1], bins[binIdx2]);
						double d3 = getAssignViolationDelta(items[item2], bins[binIdx]);
						double d4 = getAssignViolationDelta(items[item2], bins[binIdx2]);
						// System.out.println("d1 = " + d1+ " d2 = " +d2+ " d3 = " +d3+ " d4 = "+d4);
						// System.out.println("oldVio1= " + oldVio1 + " oldVio2 = " + oldVio2);
						ArrayList<Double> d = new ArrayList<Double>();
						d.add(d1);
						d.add(d2);
						d.add(d3);
						d.add(d4);
						double max = Collections.max(d);
						if ((max == d1 && d1 < oldVio1 && d2 < oldVio2 && d4 >= 0 && d1 >= 0)
								|| (max == d4 && d1 < oldVio1 && d2 < oldVio2 && d1 >= 0 && d2 >= 0)) {
							// xep item1 vao bin1
							// xep item2 vao bin2
							total_violation = d1 + d4;
							// System.out.println("total_violation = " + total_violation);
							// System.out.println("maxDelta = "+ maxDelta);
							// kiem tra dieu kien, khi them no vao thi loi giai se tot len
							if (total_violation > maxDelta) {
								// se tot hon phuong an truoc do
								cand.clear();
								cand.add(new AssignMove(item1, binIdx));
								cand.add(new AssignMove(item2, binIdx2));
								maxDelta = total_violation;
							} else if (total_violation == maxDelta) {
								cand.add(new AssignMove(item1, binIdx));
								cand.add(new AssignMove(item2, binIdx2));
							}

						} else if ((max == d2 && d2 < oldVio1 && d1 < oldVio2 && d3 >= 0 && d2 >= 0)
								|| (max == d3 && d2 < oldVio1 && d1 < oldVio2 && d2 >= 0 && d1 >= 0)) {
							total_violation = d2 + d3;
							// kiem tra dieu kien, khi them no vao thi loi giai se tot len
							if (total_violation > maxDelta) {
								// se tot hon phuong an truoc do
								cand.clear();
								cand.add(new AssignMove(item2, binIdx));
								cand.add(new AssignMove(item1, binIdx2));
								maxDelta = total_violation;
							} else if (total_violation == maxDelta) {
								cand.add(new AssignMove(item2, binIdx));
								cand.add(new AssignMove(item1, binIdx2));
							}
							}
						}
				} else if (in1 && in4) {
						// truong hop 1 chi xep vao 1 , 2 chi xep vao 2
						if (items[item1].getAssignTo() == -1 && items[item2].getAssignTo() == -1) {
							// truong hop 1 . 2 tiem chua xep vao dau
							double d1 = getAssignViolationDelta(items[item1], bins[binIdx]);
							double d2 = getAssignViolationDelta(items[item2], bins[binIdx2]);
							if (d1 >= 0 && d2 >= 0) {
								total_violation = d1 + d2;
								if (total_violation > maxDelta) {
									// se tot hon phuong an truoc do
									cand.clear();
									cand.add(new AssignMove(item1, binIdx));
									cand.add(new AssignMove(item2, binIdx2));
									maxDelta = total_violation;
								} else if (total_violation == maxDelta) {
									cand.add(new AssignMove(item1, binIdx));
									cand.add(new AssignMove(item2, binIdx2));
								}

							}
						} else if (items[item1].getAssignTo() != -1 && items[item2].getAssignTo() == -1) {
							// item1 da duoc xep vao bin truoc do roi
							double oldVio = bins[items[item1].getAssignTo()].violations();
							double d1 = getAssignViolationDelta(items[item1], bins[binIdx]);
							double d2 = getAssignViolationDelta(items[item2], bins[binIdx2]);
							if (d1 < oldVio && d2 >= 0 && d1 >= 0) {
								// xep item1 vao bin1
								// xep item2 vao bin2
								total_violation = d1 + d2;
								// kiem tra dieu kien, khi them no vao thi loi giai se tot len
								if (total_violation > maxDelta) {
									// se tot hon phuong an truoc do
									cand.clear();
									cand.add(new AssignMove(item1, binIdx));
									cand.add(new AssignMove(item2, binIdx2));
									maxDelta = total_violation;
								} else if (total_violation == maxDelta) {
									cand.add(new AssignMove(item1, binIdx));
									cand.add(new AssignMove(item2, binIdx2));
								}
							}
						} else if (items[item1].getAssignTo() == -1 && items[item2].getAssignTo() != -1) {
							// item2 da duoc xep vao bin truoc do roi
							double oldVio = bins[items[item2].getAssignTo()].violations();
							double d1 = getAssignViolationDelta(items[item1], bins[binIdx]);
							double d2 = getAssignViolationDelta(items[item2], bins[binIdx2]);
							if (d2 < oldVio && d1 >= 0 && d2 >= 0) {
								total_violation = d2 + d1;
								// kiem tra dieu kien, khi them no vao thi loi giai se tot len
								if (total_violation > maxDelta) {
									// se tot hon phuong an truoc do
									cand.clear();
									cand.add(new AssignMove(item1, binIdx));
									cand.add(new AssignMove(item2, binIdx2));
									maxDelta = total_violation;
								} else if (total_violation == maxDelta) {
									cand.add(new AssignMove(item1, binIdx));
									cand.add(new AssignMove(item2, binIdx2));
								}
							}
						} else if (items[item1].getAssignTo() != -1 && items[item2].getAssignTo() != -1) {
							// ca 2 item deu duoc xep vao bin truoc do roi
						// System.out.println("truong hop 4");
							double oldVio1 = bins[items[item1].getAssignTo()].violations();
							double oldVio2 = bins[items[item2].getAssignTo()].violations();
							double d1 = getAssignViolationDelta(items[item1], bins[binIdx]);
							double d2 = getAssignViolationDelta(items[item2], bins[binIdx2]);
							if (d1 < oldVio1 && d2 < oldVio2 && d1 >= 0 && d2 >= 0) {
								// xep item1 vao bin1
								// xep item2 vao bin2
								total_violation = d1 + d2;
								// kiem tra dieu kien, khi them no vao thi loi giai se tot len
								if (total_violation > maxDelta) {
									// se tot hon phuong an truoc do
									cand.clear();
									cand.add(new AssignMove(item1, binIdx));
									cand.add(new AssignMove(item2, binIdx2));
									maxDelta = total_violation;
								} else if (total_violation == maxDelta) {
									cand.add(new AssignMove(item1, binIdx));
									cand.add(new AssignMove(item2, binIdx2));
								}

							}
						}
				} else if (in2 && in3) {
						// truong hop 1 chi xep vao 2 , 2 chi xep vao 1
						if (items[item1].getAssignTo() == -1 && items[item2].getAssignTo() == -1) {
							// truong hop 1 . 2 tiem chua xep vao dau
							double d1 = getAssignViolationDelta(items[item2], bins[binIdx]);
							double d2 = getAssignViolationDelta(items[item1], bins[binIdx2]);
							if (d1 >= 0 && d2 >= 0) {
								total_violation = d1 + d2;
								if (total_violation > maxDelta) {
									// se tot hon phuong an truoc do
									cand.clear();
									cand.add(new AssignMove(item2, binIdx));
									cand.add(new AssignMove(item1, binIdx2));
									maxDelta = total_violation;
								} else if (total_violation == maxDelta) {
									cand.add(new AssignMove(item2, binIdx));
									cand.add(new AssignMove(item1, binIdx2));
								}

							}
						} else if (items[item1].getAssignTo() != -1 && items[item2].getAssignTo() == -1) {
							// item1 da duoc xep vao bin truoc do roi
							double oldVio = bins[items[item1].getAssignTo()].violations();
							double d1 = getAssignViolationDelta(items[item1], bins[binIdx2]);
							double d2 = getAssignViolationDelta(items[item2], bins[binIdx]);
							if (d1 < oldVio && d2 >= 0 && d1 >= 0) {
								// xep item1 vao bin1
								// xep item2 vao bin2
								total_violation = d1 + d2;
								// kiem tra dieu kien, khi them no vao thi loi giai se tot len
								if (total_violation > maxDelta) {
									// se tot hon phuong an truoc do
									cand.clear();
									cand.add(new AssignMove(item1, binIdx2));
									cand.add(new AssignMove(item2, binIdx));
									maxDelta = total_violation;
								} else if (total_violation == maxDelta) {
									cand.add(new AssignMove(item1, binIdx2));
									cand.add(new AssignMove(item2, binIdx));
								}
							}
						} else if (items[item1].getAssignTo() == -1 && items[item2].getAssignTo() != -1) {
							// item2 da duoc xep vao bin truoc do roi
							double oldVio = bins[items[item2].getAssignTo()].violations();
							double d1 = getAssignViolationDelta(items[item1], bins[binIdx2]);
							double d2 = getAssignViolationDelta(items[item2], bins[binIdx]);
							if (d2 < oldVio && d1 >= 0 && d2 >= 0) {
								total_violation = d2 + d1;
								// kiem tra dieu kien, khi them no vao thi loi giai se tot len
								if (total_violation > maxDelta) {
									// se tot hon phuong an truoc do
									cand.clear();
									cand.add(new AssignMove(item1, binIdx2));
									cand.add(new AssignMove(item2, binIdx));
									maxDelta = total_violation;
								} else if (total_violation == maxDelta) {
									cand.add(new AssignMove(item1, binIdx2));
									cand.add(new AssignMove(item2, binIdx));
								}
							}
						} else if (items[item1].getAssignTo() != -1 && items[item2].getAssignTo() != -1) {
							// ca 2 item deu duoc xep vao bin truoc do roi
						// System.out.println("truong hop 4");
							double oldVio1 = bins[items[item1].getAssignTo()].violations();
							double oldVio2 = bins[items[item2].getAssignTo()].violations();
							double d1 = getAssignViolationDelta(items[item1], bins[binIdx2]);
							double d2 = getAssignViolationDelta(items[item2], bins[binIdx]);
							if (d1 < oldVio1 && d2 < oldVio2 && d1 >= 0 && d2 >= 0) {
								// xep item1 vao bin1
								// xep item2 vao bin2
								total_violation = d1 + d2;
								// kiem tra dieu kien, khi them no vao thi loi giai se tot len
								if (total_violation > maxDelta) {
									// se tot hon phuong an truoc do
									cand.clear();
									cand.add(new AssignMove(item1, binIdx2));
									cand.add(new AssignMove(item2, binIdx));
									maxDelta = total_violation;
								} else if (total_violation == maxDelta) {
									cand.add(new AssignMove(item1, binIdx2));
									cand.add(new AssignMove(item2, binIdx));
								}

							}
						}
					}
				// }
			}
			// System.out.println("KÃ­ch thÆ°á»›c cá»§a máº£ng lÆ°u cÃ¡c á»©ng viÃªn item =
			// " +
			// cand.size());
			// System.out.print("Item Ä‘Æ°á»£c chá»�n = ( ");
			// for (int m = 0; m < cand.size(); m++) {
			// System.out.print(" " + cand.get(m).i);
			// }
			// System.out.print(" )");
			// System.out.println();
			if (cand.size() == 0) {
				excludedBin.add(binIdx);
				excludedBin.add(binIdx2);
				continue;
			}
			int candIdx = R.nextInt(cand.size());
			if (candIdx % 2 == 0) {

			} else {
				candIdx -= 1;
			}
			AssignMove m1 = cand.get(candIdx);
			AssignMove m2 = cand.get(candIdx + 1);
			moveItem(m1.i, m1.b);
			moveItem(m2.i, m2.b);
			// System.out.println("Hai item Ä‘Ã³ lÃ = " + m1.i + " " + m2.i);
			System.out.println(bins[m1.b].violations());
			System.out.println(bins[m2.b].violations());
			System.out.println("Step " + it + ", violations = " + violations() + ", size = " + cand.size());

			it++;
		}
		// System.out.println("Step " + it + ", violations = " + violations() + ", size
		// = " + cand.size());
		// System.out.println("size = "+excludedBin.size());
	}

	public int[] getBinOfItems() {
		int[] binOfItems = new int[items.length];
		for (int i = 0; i < items.length; ++i)
			binOfItems[i] = items[i].getAssignTo();
		return binOfItems;
	}

	public void loadBinOfItems(int[] binOfItems) {
		for (int i = 0; i < items.length; ++i)
			if (binOfItems[i] != -1)
				bins[binOfItems[i]].addItem(items[i]);
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

	public int[] search2() {
		// TODO Auto-generated method stub
		int it = 0;
		int cnt = 0, maxItemUsed = 0;
		while (true) {
			System.out.println("Lan " + "[" + it + "]");
			hillClimbing2(10000);

			clearViolatedBins();

			System.out.println("Sá»‘ lÆ°á»£ng item xáº¿p Ä‘Æ°á»£c  = " + itemUsed.size());

			for (int i = 0; i < items.length; ++i)
				System.out.print(items[i].getAssignTo() + " ");

			System.out.println("");
			if (itemUsed.size() > maxItemUsed) {
				cnt = 1;
				maxItemUsed = itemUsed.size();
			} else {
				cnt++;
			}
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
		System.out.println("Sá»‘ lÆ°á»£ng item xáº¿p Ä‘Æ°á»£c  = " + itemUsed.size());

		for (int i = 0; i < items.length; ++i)
			System.out.print(items[i].getAssignTo() + " ");

		System.out.println("");
		return getBinOfItems();
	}

	public void check() {
		int[] binOfItems = new int[items.length];
		Random R = new Random();
		String fileName = "src/khmtk60/miniprojects/G17/result/group-1000.out";
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
		// String filein =
		// "src/khmtk60/miniprojects/G17/data/MinMaxTypeMultiKnapsackInput-1000.json";
		String filein = "src/khmtk60/miniprojects/G17/data/group-1000.json";
		try {
			System.setOut(new PrintStream(new File("src/khmtk60/miniprojects/G17/result/check.txt")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		State2 st = new State2();
		st.initialize(filein);
		// st.search();
		st.search2();
		//// st.check();
	}
}