package project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class read_data {
	public static int C, A, N;
	public static int[] a;
	public static int[] c;
	public static int[] m;
	public static int[] f;
	
	public static void readFile(int N) throws Exception {
		String workingDir = System.getProperty("user.dir") + "\\data\\data_manu_";
		File file = new File(workingDir + N + ".txt");

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(file);
		C = scanner.nextInt(); // //limit of cost
		A = scanner.nextInt(); // limit of area
		// area needed for 1 unit of product
		for (int i = 0; i < N; i++) {
			a[i] = scanner.nextInt();
		}
		// cost needed for 1 unit of product
		for (int i = 0; i < N; i++) {
			c[i] = scanner.nextInt();
		}
		// Minimum number of product units
		for (int i = 0; i < N; i++) {
			m[i] = scanner.nextInt();
		}
		// profit for 1 unit of product
		for (int i = 0; i < N; i++) {
			f[i] = scanner.nextInt();
		}
	}

	public static void writerCSV(int phuongphap, int data ,long time) throws IOException {
		String workingDir = System.getProperty("user.dir") + "\\data\\statisics.csv";
		File file = new File(workingDir);

        FileWriter writer = new FileWriter(file);
        String NEW_LINE_SEPARATOR = "\n";
        String COMMA_DELIMITER = ",";

        String pp = null;
        if (phuongphap == 1) {
			pp = "ortools";
		}else if (phuongphap == 2) {
			pp = "choco";
		}else if (phuongphap == 3) {
			pp = "localsearch";
		}else if(phuongphap == 4) {
			pp = "lns";
		}
        System.out.println("hello");
        // Write the CSV file header
        writer.append(pp);
        writer.append(COMMA_DELIMITER);
        writer.append(String.valueOf(data));
        writer.append(COMMA_DELIMITER);
        writer.append(String.valueOf(time));
        writer.append(COMMA_DELIMITER);
        writer.append(NEW_LINE_SEPARATOR);
        writer.close();
	}
	public static void main(String[] args) throws IOException {
		int N= 300;
		String workingDir = System.getProperty("user.dir") + "\\data\\data_manu_" +N;
		File file = new File(workingDir + ".txt");
		FileWriter fw = new FileWriter(file);
		
		fw.write("2147000000 2147000000\n");

		Random R = new Random();
		// a
		System.out.println(workingDir + ".txt");
		for (int i = 0; i < N; i++) {
			fw.write((R.nextInt(50) + 10) + " ");
		}
		fw.write('\n');
		// c
		for (int i = 0; i < N; i++) {
			fw.write((R.nextInt(100) + 10) + " ");
		}
		fw.write('\n');
		// m
		for (int i = 0; i < N; i++) {
			fw.write((R.nextInt(30) + 10) + " ");
		}
		fw.write('\n');
		// f
		for (int i = 0; i < N; i++) {
			fw.write((R.nextInt(10) +10) + " ");
		}
		fw.close();
	}

}
