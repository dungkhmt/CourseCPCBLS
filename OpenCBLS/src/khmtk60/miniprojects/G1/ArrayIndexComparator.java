package khmtk60.miniprojects.G1;

import java.util.Arrays;
import java.util.Comparator;

public class ArrayIndexComparator implements Comparator<Integer>{
    private final Double[] array;

    public ArrayIndexComparator(Double[] array){
        this.array = array;
    }

    public Integer[] createIndexArray(){
        Integer[] indexes = new Integer[array.length];
        for (int i = 0; i < array.length; i++){
            indexes[i] = i; // Autoboxing
        }
        return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2){
         // Autounbox from Integer to int to use as array indexes
        return array[index1].compareTo(array[index2]);
    }
    
    public static void main(String[] args) {
    	Double[] countries = {3., 1., 6., 7., 8., 2.};
    	ArrayIndexComparator comparator = new ArrayIndexComparator(countries);
    	Integer[] indexes = comparator.createIndexArray();
    	Arrays.sort(indexes, comparator);
    	for (Integer i : indexes) {
			System.out.print(i + " ");
		}
	}
}
