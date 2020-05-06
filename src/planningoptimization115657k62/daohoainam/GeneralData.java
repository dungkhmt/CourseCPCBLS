package planningoptimization115657k62.daohoainam;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GeneralData {
	Random generator = new Random();
	
	int S = 2;
	int columns = S+1;
	int rows = columns;
	int need = 2;
	


	
	int min_distance = 1;
	int max_distance = 100;
	int range_dis = max_distance - min_distance;
	
	int min_unit = 0;
	int max_unit = 10;
	int range_unit = max_unit - min_unit;
	
	int min_need = 0;
	int max_need = 35;
	int range_need = max_need - min_need;
	
	public void Gen() throws IOException {
		
	int have[][] = new int[need][columns-1];
	int need_arr[] = new int[need];
	int d[][] = new int[rows][columns];
	// general data for distance
	for(int i = 0; i < rows; i++) {
		for(int j = 0; j < columns; j++) {
			if(i == j)
				d[i][j] = 0;
			else {
				d[i][j] =  generator.nextInt((max_distance - min_distance) + 1) + min_distance;
			}
		}
	}
	
	// general for need
	for(int i = 0; i < need; i++) {
		need_arr[i] = generator.nextInt((max_need - min_need) + 1) + min_need;
	}
	

	// general data for Q
	for(int i = 0; i < need; i++)
		for(int j = 0; j < S; j++) {
			have[i][j] = generator.nextInt((max_unit- min_unit) + 1) + min_unit;
		}
	
	/* write file */
	// file distance
	String filePath = new File("").getAbsolutePath();
	
	StringBuilder builder = new StringBuilder();
	for(int i = 0; i < rows; i++)//for each row
	{
	   for(int j = 0; j < columns; j++)//for each column
	   {
		  
			  builder.append(d[i][j]+" ");//append to the output string
	   }
	   builder.append("\n");//append new line at the end of the row
	  
	}
	BufferedWriter writer = new BufferedWriter(new FileWriter(filePath+"/src/planningoptimization115657k62/daohoainam/distance.txt"));
	writer.write(builder.toString());//save the string representation of the board
	writer.close();
	
	
	// write file Q
	
	StringBuilder builder_Q = new StringBuilder();
	for(int i = 0; i < need; i++)//for each row
	{
	   for(int j = 0; j < S; j++)//for each column
	   {
	      builder_Q.append(have[i][j]+" ");//append to the output string
	     
	   }
	   builder_Q.append("\n");//append new line at the end of the row

	   
	}
	BufferedWriter writer_Q = new BufferedWriter(new FileWriter(filePath+"/src/planningoptimization115657k62/daohoainam/Q.txt"));
	writer_Q.write(builder_Q.toString());//save the string representation of the board
	writer_Q.close();
	
	
	// wire file need
	
	
	 BufferedWriter outputWriter = null;
	  outputWriter = new BufferedWriter(new FileWriter(filePath+"/src/planningoptimization115657k62/daohoainam/need.txt"));
	  for (int i = 0; i < need_arr.length; i++) {
	  
	    outputWriter.write(need_arr[i]+" ");

	    //outputWriter.newLine();
	  }
	  outputWriter.flush();  
	  outputWriter.close();
	
	}
}
