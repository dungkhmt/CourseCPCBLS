package khmtk60.miniprojects.G1.algorithm2;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gson.Gson;

import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.SolutionChecker;
import khmtk60.miniprojects.G1.ArrayIndexComparator;
import khmtk60.miniprojects.G1.newlocalsearch.search.CustomOneVariableValueMove;
import khmtk60.miniprojects.G1.newlocalsearch.search.CustomTwoVariablesSwapMove;
import khmtk60.miniprojects.G1.newlocalsearch.search.Move;
import khmtk60.miniprojects.G1.newlocalsearch.search.MoveType;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackSolution;

public class MultiKnapsack1D_minibatch extends MultiKnapsack{
	public void searchX(int[] searchIds, int startId, int endId, int[][] tabu, int tabulen, int it, double best) {
		double minDelta, delta;
		ArrayList<Move> candX = new ArrayList<>();
		minDelta = 0;
		for(int id = startId; id < endId; id++) {
			int i = searchIds[id];
			for (int v: X[i].getDomain()) {
				if(v == X[i].getValue())
					continue;
				delta = oneMoveAssignDelta(i, v);
				
				if (tabu[i][v] <= it || _violations + delta <= best) {
					if(delta < minDelta) {
						candX.clear();
						candX.add(new CustomOneVariableValueMove(MoveType.OneVariableValueAssignment, -1, X[i], v, i));
						minDelta = delta;
					}else if(delta == minDelta) {
						candX.add(new CustomOneVariableValueMove(MoveType.OneVariableValueAssignment, -1, X[i], v, i));
					}
				}
			}
		}
		if(candX.size() == 0) {
			restartMaintainConstraint(tabu);	
		}else {
			int idx = rand.nextInt(candX.size());
			Move move = candX.get(idx);
			makeOneMove(move);
			
			if(move.getType() == MoveType.OneVariableValueAssignment) {
				CustomOneVariableValueMove moveX = (CustomOneVariableValueMove) move;
				tabu[moveX.getId()][moveX.getValue()] = it + tabulen;
			}else {
				CustomTwoVariablesSwapMove moveX = (CustomTwoVariablesSwapMove) move;
				tabu[moveX.getId1()][moveX.getVar1().getValue()] = it + tabulen;
				tabu[moveX.getId2()][moveX.getVar2().getValue()] = it + tabulen;
			}
		}
	}
	
	public void search(int tabulen, int maxTime, int maxIter, int maxStable, HashSet<Integer> itemBlackList) {
		double t0 = System.currentTimeMillis();
		
		int maxV = realBinNumber;
		int minV = 0;
		
		int n = N;
		int D = maxV - minV;
		int tabu[][] = new int[n][D + 1];
		for (int i = 0; i < N; i++)
			for (int v = 0; v <= D; v++)
				tabu[i][v] = -1;
		
		int it = 0;
		maxTime = maxTime * 1000;// convert into milliseconds

		double best = _violations;
		int[] x_best = new int[N];
		
		int max_items = N - bin2Items.get(realBinNumber).size();		
		for (int vioBinId : getVioBins()) {
			max_items -= bin2Items.get(vioBinId).size();
		}
		
		for (int i = 0; i < N; i++) {
			x_best[i] = X[i].getValue();
		}

		System.out.println("TabuSearch, init S = " + _violations);
		int nic = 0;
		int batchSize = 32;
		int numMiniBatch = (int) Math.ceil(realItemNumber * 1. / batchSize);
		int[] idArr = new int[realItemNumber];
		int index = 0;
		for (int i = 0; i < N; i++) {
			if(itemBlackList.contains(i)) {
				continue;
			}
			idArr[index] = i;
			index++;
		}
		System.out.println("Num epoch: " + numMiniBatch);
		int startId, endId;
		double oldBest;
		boolean restartFlag = true;
		Utils.shuffleArray(idArr, rand);
		while ((it < maxIter && System.currentTimeMillis() - t0 < maxTime && Math.abs(_violations) > 1e-8) || 
				(it < maxIter && System.currentTimeMillis() - t0 < maxTime && max_items < realItemNumber)) {
			oldBest = best;
			for (int batchId = 0; batchId < numMiniBatch; batchId++) {
				startId = batchId*batchSize;
				endId = (int) Math.min(idArr.length, (batchId + 1)*batchSize);
				searchX(idArr, startId, endId, tabu, tabulen, it, best);

				if(_violations < oldBest) {
					nic = 0;
					restartFlag = true;
				}else {
					nic++;
				}
				if(_violations < best)
					best = _violations;				
				if(numSatisItems > max_items) {
					for (int i = 0; i < N; i++) {
						x_best[i] = X[i].getValue();
					}
					max_items = numSatisItems;
				}
			}
			Utils.shuffleArray(idArr, rand);
			
			System.out.println("Step " + it + ", S = " + _violations
					+ ", best = " + best + ", max items = " + max_items + ", nic = " + nic);
	
			if (nic > maxStable) {
				hardRestart(tabu);
				int numItems = N - bin2Items.get(realBinNumber).size();
				if(numSatisItems != numItems) {
					System.out.println("<-->");
				}
				System.out.println("Step " + it + ", S = " + _violations + ", num items: " + numSatisItems
						+ ", best = " + best + ", max items = " + max_items + ", nic = " + nic);
				if(best > _violations) {// || (best == _violations && max_items < numItems)) {
					best = _violations;
				}
				if(numItems > max_items) {
					max_items = numItems;
					for (int i = 0; i < N; i++) {
						x_best[i] = X[i].getValue();
					}
				}	
				if(nic > maxStable) {
					nic = 0;
					restartFlag = true;
				}
			} else if(restartFlag && nic > .75*maxStable) {
				restartMaintainConstraint(tabu);
				restartFlag = false;
				if(best > _violations) {
					best = _violations;
					nic = 0;
					restartFlag = true;
				}
				if(numSatisItems > max_items) {
					max_items = numSatisItems;
					for (int i = 0; i < N; i++) {
						x_best[i] = X[i].getValue();
					}
				}	
			}
			it++;
		}		
		for (int i = 0; i < N; i++) {
			makeOneMove(new CustomOneVariableValueMove(MoveType.OneVariableValueAssignment, -1, X[i], x_best[i], i));
		}		
//		System.out.println(_violations);
//		ArrayList<Integer> vioBins = getVioBins();
//		System.out.println("vio bins: " + vioBins.size());
		hardRestart(tabu);
//		System.out.println("Max items: " + max_items);
		initPropagate();
	}
	
	public void initValue1(HashSet<Integer> itemBlacklist) {
		for (int i = 0; i < N; i++) {
			if(itemBlacklist.contains(i))
				continue;
			ArrayList<Integer> arr = new ArrayList<>();
			for (int binId : X[i].getDomain()) {
				if(binId == realBinNumber)
					continue;
				arr.add(binId);
			}
			int choosenBin = arr.get(rand.nextInt(arr.size()));
			makeOneMove(new CustomOneVariableValueMove(MoveType.OneVariableValueAssignment, -1, X[i], choosenBin, i));
		}
	}
	
	public void initValue2(HashSet<Integer> itemBlacklist) {
		HashMap<Integer, HashSet<Integer>> class2Items = new HashMap<>();
		double[] sumPerClass = new double[R];
		
		for (int key : r2idx.keySet()) {
			class2Items.put(r2idx.get(key), new HashSet<>());
		}
		for (int i = 0; i < N; i++) {
			if(itemBlacklist.contains(i))
				continue;
			class2Items.get(r2idx.get(r[i])).add(i);
			sumPerClass[r2idx.get(r[i])] += w[i];
		}

		for (int i = 0; i < N; i++) {
			if(itemBlacklist.contains(i))
				continue;
			double maxValue = 0;
			double minWValue = 1e8;
			int maxTValue = 0;
			int choosenBin = realBinNumber;
			for (int binId : X[i].getDomain()) {
				int oldId = newBinId2OldId.get(binId);
				if(binId == realBinNumber || (Z[binId].getValue() > 0 && numClassPerBin[binId][r2idx.get(r[i])] == 0) || 
						minW[newBinId2OldId.get(binId)] > sumPerClass[r2idx.get(r[i])] || sumWeight1[binId].getValue() + w[i] > maxW[oldId] ||
						(Y[binId].getValue() == maxT[oldId] && numTypePerBin[binId][t2idx.get(t[i])] == 0))
					continue;
				
				if(maxTValue < maxT[oldId]) {
					maxTValue = maxT[oldId];
					maxValue = maxW[oldId];
					minWValue = minW[oldId];
					choosenBin = binId;
				}else if(maxTValue == maxT[oldId])
					if(minWValue > minW[oldId]) {
						minWValue = minW[oldId];
						maxValue = maxW[oldId];
						choosenBin = binId;
					} else if(minWValue == minW[oldId])
						if(maxValue < maxW[oldId] || (maxValue == maxW[oldId] && choosenBin > binId)) {
							maxValue = maxW[oldId];
							choosenBin = binId;
						}
			}
			makeOneMove(new CustomOneVariableValueMove(MoveType.OneVariableValueAssignment, -1, X[i], choosenBin, i));
		}
	}
	
	public void initValue3(HashSet<Integer> itemBlacklist) {
		HashMap<Integer, HashSet<Integer>> class2Items = new HashMap<>();
		double[] sumPerClass = new double[R];
		
		for (int key : r2idx.keySet()) {
			class2Items.put(r2idx.get(key), new HashSet<>());
		}
		for (int i = 0; i < N; i++) {
			if(itemBlacklist.contains(i))
				continue;
			class2Items.get(r2idx.get(r[i])).add(i);
			sumPerClass[r2idx.get(r[i])] += w[i];
		}

		for (int i = 0; i < N; i++) {
			if(itemBlacklist.contains(i))
				continue;
			double maxValue = 0;
			double minWValue = 1e8;
			int maxTValue = 0;
			int choosenBin = realBinNumber;
			for (int binId : X[i].getDomain()) {
				int oldId = newBinId2OldId.get(binId);
				if(binId == realBinNumber || (Z[binId].getValue() > 0 && numClassPerBin[binId][r2idx.get(r[i])] == 0) || 
						minW[newBinId2OldId.get(binId)] > sumPerClass[r2idx.get(r[i])] || sumWeight1[binId].getValue() + w[i] > maxW[oldId] ||
						(Y[binId].getValue() == maxT[oldId] && numTypePerBin[binId][t2idx.get(t[i])] == 0))
					continue;
				
				if(maxTValue < maxT[oldId]) {
					maxTValue = maxT[oldId];
					maxValue = maxW[oldId];
					minWValue = minW[oldId];
					choosenBin = binId;
				}else if(maxTValue == maxT[oldId])
					if(maxValue < maxW[oldId]) {
						maxValue = maxW[oldId];
						minWValue = minW[oldId];
						choosenBin = binId;
					} else if(maxValue == maxW[oldId]) {
						if(minWValue > minW[oldId] || (minWValue == minW[oldId] && choosenBin > binId)) {
							minWValue = minW[oldId];
							choosenBin = binId;
						}
					}
			}
			makeOneMove(new CustomOneVariableValueMove(MoveType.OneVariableValueAssignment, -1, X[i], choosenBin, i));
		}
	}
	
	public void additionalPut(HashSet<Integer> itemBlacklist) {
		ArrayList<Integer> items = new ArrayList<>();
		for (int i : bin2Items.get(realBinNumber)) {
			if(!itemBlacklist.contains(i))
				items.add(i);
		}
		Double[] weights = new Double[items.size()];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = w[items.get(i)];
		}
		ArrayIndexComparator comparator = new ArrayIndexComparator(weights);
		Integer[] indexes = comparator.createIndexArray();
    	Arrays.sort(indexes, comparator);

		for (int i : indexes) {
			for (int bin : X[items.get(i)].getDomain()) {
				if(oneMoveAssignDelta(items.get(i), bin) + 1 <= 0) {
//					System.out.println(items.get(i) + "( " + w[items.get(i)] + "): " + X[items.get(i)].getValue() + " --> " + bin);
//					System.out.println("*** " + _violations);
					makeOneMove(new CustomOneVariableValueMove(MoveType.OneVariableValueAssignment, -1, X[items.get(i)], bin, items.get(i)));
					break;
				}
			}
		}
	}
	
	public void optimalReplace(HashSet<Integer> itemBlacklist) {
		HashSet<Integer> tmp = new HashSet<>(bin2Items.get(realBinNumber));
		for (int i : tmp) {
			if(itemBlacklist.contains(i))
				continue;
			ArrayList<Integer> cand = new ArrayList<>();
			for (int bin : X[i].getDomain()) {
				if(bin == realBinNumber)
					continue;
				HashSet<Integer> bin2ItemClone = new HashSet<Integer>(bin2Items.get(bin));
				
				for (int itemId : bin2ItemClone) {
					if(w[i] < w[itemId] && replaceMoveAssignDelta(i, itemId) <= 0) {
						cand.add(itemId);
					}
				}
			}
			if(cand.size() == 0)
				continue;
			int choosenItem = cand.get(0);
			double maxWItem = w[choosenItem];
			for (int itemId : cand) {
				if(w[itemId] > maxWItem) {
					maxWItem = w[itemId];
					choosenItem = itemId;
				}
			}
			double oldVio = _violations;
			makeOneMove(new CustomOneVariableValueMove(MoveType.OneVariableValueAssignment, -1, X[i], X[choosenItem].getValue(), i));
			makeOneMove(new CustomOneVariableValueMove(MoveType.OneVariableValueAssignment, -1, X[choosenItem], realBinNumber, choosenItem));
//			System.out.println(i + " <--> " + choosenItem);
			if(oldVio < _violations) {
				System.out.println("wrong: " + i + " " + choosenItem);
				return;
			}
		}
	}
	
	public void solve(int initStrategy) {
		HashSet<Integer> blackList = Utils.getBlackList(this);
		HashSet<Integer> itemBlacklist = Utils.getBlackListOfItem(this, blackList);
		stateModel(blackList, itemBlacklist);
		System.out.println("run 3000-32");
		
		if(initStrategy == 2) {
			System.out.println("Init strategy 2:...");
			initValue2(itemBlacklist);				
		}else if(initStrategy == 3){
			System.out.println("Init strategy 3:...");
			initValue3(itemBlacklist);
		}else {
			System.out.println("Init strategy 1:...");
			initValue1(itemBlacklist);
		}
		search(5, 3600, 1000, 1000, itemBlacklist);
		System.out.println("Violation: " + (_violations - (bin2Items.get(realBinNumber).size() - N + realItemNumber)));
		System.out.println("Num item: " + numSatisItems);
		System.out.println("----------------------");
		System.out.println("optimal step");
		optimalReplace(itemBlacklist);
		additionalPut(itemBlacklist);
		initPropagate();
		System.out.println("Violation: " + (_violations - (bin2Items.get(realBinNumber).size() - N + realItemNumber)));
		System.out.println("Num item: " + numSatisItems);		
//		Utils.printSolution(this, blackList);
	}

	public static void main(String[] args) {
		MultiKnapsack1D_minibatch multiKnapsack1D = new MultiKnapsack1D_minibatch();
		String inputJson = "src/khmtk60/miniprojects/multiknapsackminmaxtypeconstraints/MinMaxTypeMultiKnapsackInput-1000.json";
		if(args.length > 0) {
			multiKnapsack1D.readData(args[0]);
		} else {
			multiKnapsack1D.readData(inputJson);
		}
		multiKnapsack1D.solve(2);
		
		int[] sol = multiKnapsack1D.getSolution();
		MinMaxTypeMultiKnapsackSolution S = new MinMaxTypeMultiKnapsackSolution();
		S.setBinOfItem(sol);
		SolutionChecker checker = new SolutionChecker();
		MinMaxTypeMultiKnapsackInput tmp = new MinMaxTypeMultiKnapsackInput();
		MinMaxTypeMultiKnapsackInput I = tmp.loadFromFile(inputJson);
		System.out.println("Checking:.......");
		System.out.println(checker.check(I, S));
		
//		String outputJson = "src/khmtk60/miniprojects/G1/result/MinMaxTypeMultiKnapsackInput-3000-result.json";
//		multiKnapsack1D.writeResultToFile(outputJson);
		

//		String inputJson = "src/khmtk60/miniprojects/multiknapsackminmaxtypeconstraints/MinMaxTypeMultiKnapsackInput-3000.json";
//		String outputJson = "src/khmtk60/miniprojects/G1/result/MinMaxTypeMultiKnapsackInput-3000-result.json";
//		System.out.println(checker.check(inputJson, outputJson));	
	}
}

