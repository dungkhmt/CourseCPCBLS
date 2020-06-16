package planningoptimization115657k62.nguyenthinhung;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class demo {
	public static void main(String[] args) {
		File folder = new File("./data/BinPacking2D/");
		File[] listOfFiles = folder.listFiles();
		 
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        System.out.println(file.getName());
		    }
		}
}
}
