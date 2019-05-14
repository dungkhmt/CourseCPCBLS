package khmtk60.miniprojects.G6;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Random;

public class Utilities {
	public static void printJson(int[] X, String fileName) throws FileNotFoundException {
		PrintStream fileOut = new PrintStream(fileName);
		int num_items = X.length;
		System.setOut(fileOut);
		System.out.print("{\"binOfItem\":[");
		for (int i = 0; i < num_items; i++) {
			System.out.print(X[i]);
			if (i != num_items - 1)
				System.out.print(",");
		}
		System.out.print("]}");
		System.setOut(System.out);
	}
	
	public static int[] arrayShuffle(int[] array) {
		Random rgen = new Random(); // Random number generator

		for (int i = 0; i < array.length; i++) {
			int randomPosition = rgen.nextInt(array.length);
			int temp = array[i];
			array[i] = array[randomPosition];
			array[randomPosition] = temp;
		}
		return array;
	}
}
