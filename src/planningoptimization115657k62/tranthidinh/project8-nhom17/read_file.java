package my_work;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class read_file {
	
	private int N, M, numOfConflict, c[], d[], I1[], I2[]; // cac du lieu muon lay
	
	public void readFile() throws FileNotFoundException {
		//Doan nay co the sua de lay duoc file phu hop: 
		// Co the dung dong file de doc lien tiep cac file de thu (ls > names_file)
		String rootpath = "/media/quyentran/A23622BE36229379/A.School/"
    			+ "Toi_uu_lap_KH/Code_/Or-tools/examples/java/my_work/data/";
    	String filename = "3class-4room-2conflict.txt";
    	System.out.println("File name: " + filename); 
    	
    	File file = new File(rootpath + filename);
    	Scanner scanner = new Scanner(file);
    	
    	N = scanner.nextInt();
    	M = scanner.nextInt();
    	numOfConflict =scanner.nextInt();
    	
    	c = new int[M];
    	d = new int[N];
    	I1 = new int[numOfConflict];
    	I2 = new int[numOfConflict];
    	
    	for(int i = 0; i<N; i++) {
    		d[i] = scanner.nextInt();
    	}
    	
    	for(int j = 0; j<M; j++) {
    		c[j] = scanner.nextInt();
    	}
    	
    	for(int i = 0; i<numOfConflict; i++) {
    		I1[i] = scanner.nextInt();
    	}
    	
    	for(int i = 0; i<numOfConflict; i++) {
    		I2[i] = scanner.nextInt();
    	}
    	
	}
	
	public void result() {
		System.out.println(N + " " + M + " " + numOfConflict);
		
		for(int i = 0; i<N; i++) {
			System.out.print(d[i] + " ");
		}
		System.out.println();
		
		for(int i = 0; i<M; i++) {
			System.out.print(c[i] + " ");
		}
		System.out.println();
		
		for(int i = 0; i<numOfConflict; i++) {
			System.out.print(I1[i] + " ");
		}
		System.out.println();
		
		for(int i = 0; i<numOfConflict; i++) {
			System.out.print(I2[i] + " ");
		}
	}
	
	public static void main(String[] args) {
		read_file run = new read_file();
		try {
			run.readFile();
			run.result();
			System.out.println("Done!!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
