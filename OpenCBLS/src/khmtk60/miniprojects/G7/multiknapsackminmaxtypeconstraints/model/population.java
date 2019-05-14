package khmtk60.miniprojects.G7.multiknapsackminmaxtypeconstraints.model;

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
public class population {
	
	MinMaxTypeMultiKnapsackInputBin[] bins;
	MinMaxTypeMultiKnapsackInputItem[] items;
	double capacity[];
	double minLoad[];
	double p_bin[];
	int t_bin[];
	int r_bin[];
	double w[];
	double p_item[];
	int t_item[];
	int r_item[];
	int [][] binIndices;
	
	public population(MinMaxTypeMultiKnapsackInputBin[] bins, MinMaxTypeMultiKnapsackInputItem[] items,
			double[] capacity, double[] minLoad, double[] p_bin, int[] t_bin, int[] r_bin, double[] w, double[] p_item,
			int[] t_item, int[] r_item, int[][] binIndices) {
		super();
		this.bins = bins;
		this.items = items;
		this.capacity = capacity;
		this.minLoad = minLoad;
		this.p_bin = p_bin;
		this.t_bin = t_bin;
		this.r_bin = r_bin;
		this.w = w;
		this.p_item = p_item;
		this.t_item = t_item;
		this.r_item = r_item;
		this.binIndices = binIndices;
		
	}

	int numberElement = 200;
	double chosenCrossRate = 0.8;
	double chosenMutateRata = 0.2;
	int numberMutate ;
	public void Hybridization(ArrayList<Element> population) {
		double crossRate1 = 0;
		double crossRate2 = 0;
		double mutateRate = 0;
		for(int i=0; i < numberElement-1;i++) {
			crossRate1 = Math.random();
			if(crossRate1 > chosenCrossRate) {
				for(int j = i+1; j<numberElement;j++) {
					crossRate2 = Math.random();
					if(crossRate2 > chosenCrossRate) {
						crossOver(population.get(i),population.get(j), population);
						
					}
				}
			}
		
			mutateRate = Math.random();
			if(mutateRate > chosenMutateRata) {
				//System.out.println("kich thuoc truyen vao: "+ population.get(i).element.size());
				mutation(population.get(i), population);
			}
		}	
	}
	
	public void crossOver(Element parent1, Element parent2, ArrayList<Element> population ) {
		Random rand = new Random();
		int indexCross = 1+rand.nextInt(parent1.element.size()-1);
//		int indexCross = 1+rand.nextInt(parent2.element.size()-2);
//		for(int k=0;k<items.length;k++) {			
//			System.out.print(" "+parent1.element.get(k));
//		}
		ArrayList<Integer> element1 = new ArrayList<Integer>();
		ArrayList<Integer> element2 = new ArrayList<Integer>();
		Element child1 = new Element(0,element1);
		Element child2 = new Element(0,element2);
		for(int i=0;i<indexCross;i++) {
			//System.out.println("i phia tren = "+i);
			int a = parent1.element.get(i);		
			child1.element.add(a);
			int b = parent2.element.get(i); 
			child2.element.add(b);
			
		}
		
		//System.out.println("child1 sau cai thu 1: "+  child2.element.size());
		//System.out.println("parent1.size = " + parent1.element.size());
		for(int i=indexCross;i<parent1.element.size();i++) {
			//System.out.println("i phia duoi = "+i);
			int b = parent2.element.get(i);
			child1.element.add(b);
			b = parent1.element.get(i);
			child2.element.add(b);
		}

		GENERATE gr = new GENERATE(bins, items, capacity, minLoad, p_bin, t_bin, r_bin, w, p_item, t_item, r_item, binIndices);
		child1.violation = gr.caculateViolation(gr.convert_arraylist_to_array(child1.element));
		child2.violation = gr.caculateViolation(gr.convert_arraylist_to_array(child2.element));
		population.add(child1);
		population.add(child2);

		
	}
	
	public void mutation(Element parent, ArrayList<Element> population) {
		Random rand = new Random();
		GENERATE gr = new GENERATE(bins, items, capacity, minLoad, p_bin, t_bin, r_bin, w, p_item, t_item, r_item, binIndices);
		//System.out.println("kich thuoc: " +parent.element.size());
		Element child = new Element(parent.violation,parent.element);
		int numberMutate = 100 * 10/items.length;
		for(int i=0;i<numberMutate;i++) {
			int indexMutate = rand.nextInt(items.length);
			int indexGiaTri = rand.nextInt(binIndices[indexMutate].length);
			Integer a = new Integer(binIndices[indexMutate][indexGiaTri]);			
			child.element.set(indexMutate,a); 
			
		}
	
		
		child.violation = gr.caculateViolation(gr.convert_arraylist_to_array(child.element));
		population.add(child);
		
	}
	public void quickSort(ArrayList<Element> population, int begin, int end) {
		int partitionIndex = partition(population, begin, end);
		if(partitionIndex-1>begin) {				
			quickSort(population, begin, partitionIndex-1);
		}
		if(partitionIndex+1<end ) {
			quickSort(population, partitionIndex + 1, end);
		}
		
	}
	public int partition(ArrayList<Element> population, int begin, int end) {
		double pivot = population.get(end).violation;
		//int i = begin;
		
		for (int j=begin; j<end; j++) {
			if (population.get(j).violation < pivot) {
				//Collections.swap(list, begin, j);
				//i++;
				Element temp = new Element();
				temp = population.get(begin);
				population.set(begin,population.get(j));
				population.set(j, temp);
				begin++;
			}
		}	
		//Collections.swap(list, begin, end);
		Element temp = new Element();
		temp = population.get(begin);
		population.set(begin, population.get(end));
		population.set(end, temp);
		return begin;
	}
	
	
	public void bubbleSort(ArrayList<Element> population) {
		for(int i=0;i<population.size()-1;i++) {
			for(int j=0;j<population.size()-1-i;j++) {
				
				if(population.get(j).violation>population.get(j+1).violation) {
					Collections.swap(population, j, j+1);
				}
			}
		}
	}
	
	public void selection(ArrayList<Element> population) {// ham quicksort co van de
		System.out.println("kich thuoc pop trc khi sort : " +population.size());
		quickSort(population, 0, population.size()-1);
//		bubbleSort(population);
		ArrayList<Element> newPopu = new ArrayList<Element>();
		newPopu.addAll(population);
		for (int i=0; i<numberElement/2; i++) {
			newPopu.remove(i);
		}
		for (int i=population.size()-1;i>numberElement/2-1 ;i--) {
			population.remove(i);
		}
		
		for (int i=0; i<numberElement/2; i++) {
			Random rand = new Random();
			int index = rand.nextInt(newPopu.size());
			population.add(newPopu.get(i));
		}
		//System.out.println("kich thuoc Sau khi add: " + population.size());
		
	}
	public void runGA() {
		GENERATE gr = new GENERATE(bins, items, capacity, minLoad, p_bin, t_bin, r_bin, w, p_item, t_item, r_item, binIndices);	
		ArrayList<Element>population = new ArrayList<Element>();
		population = gr.initPopulation();

		for(int i=0;i<7;i++) {
			Hybridization(population);
			selection(population);
			System.out.println("step "+i+ "co violation cao nhat= "+population.get(0).violation);
			System.out.println("step "+i+ "co violation thap nhat= "+population.get(population.size()-1).violation);
		}
		System.out.println("Ket qua cuoi cung: ");
//		for(int i=0;i<items.length;i++) {
//			System.out.print(population.get(0).element.get(i)+ " ");
//		}
		int count = gr.check(gr.convert_arraylist_to_array(population.get(0).element));
		System.out.println("Xep duoc: " +count);
	}

}
