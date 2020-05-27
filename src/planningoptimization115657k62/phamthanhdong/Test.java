package planningoptimization115657k62.phamthanhdong;

import java.util.*;

public class Test {
	public static void main(String args[]) {

		int rows = 100;
		int colum = 100;
		int b[][] = new int[rows + 1][colum + 1];

		int max = 100;
		int min = 0;
		int range = max - min + 1;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < colum; j++) {
				int rand = (int) (Math.random() * range);
				b[i][j] = rand;
				// System.out.print(" " + rand);
			}

		}
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < colum; j++) {
				System.out.print(b[i][j] + " ");
				// System.out.println("\n");
			}
			System.out.println("\n");
		}

		try {
			Formatter f = new Formatter(
					"C:\\Users\\dong.pt173020\\eclipse-workspace\\ToiUuLapKeHoach\\src\\planningoptimization115657k62\\phamthanhdong\\distance.txt");
			for (int i = 0; i < colum; i++)
				for (int j = 0; j < rows; j++) {
					f.format();
				}
			f.close();
		} catch (Exception e) {
			System.out.println("Error");
		}
	}
}