package planningoptimization115657k62.ngoviethoang.prj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
public class DataGenerator {
	public void generate() { 
		Random r = new Random();
		r.setSeed(18);
		
		int N = 3 + r.nextInt(10);
		int K;
		do {
			K = 2 + r.nextInt(8);
		} while (K >= N);
		
		String folder = "data/";
		String filename = folder + String.valueOf(N) + "x" + String.valueOf(K) + ".vrp";
		
		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				Logger.getLogger(DataGenerator.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
        FileOutputStream fos = null;	
        try {
            fos = new FileOutputStream(filename);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintWriter pw = new PrintWriter(fos);
        pw.println(N + " " + K);
		for (int i = 0; i <= N; i++) {
			for (int j = 0; j <= N; j++) {
				if (i == j) {
					pw.print("0 ");
				} else {
					pw.print((2 + r.nextInt(20)) + " ");					
				}
			}
			pw.println();
		}
		pw.close();
	}
	public static void main(String [] arg) {
		DataGenerator app = new DataGenerator();
		app.generate();
	}
}
