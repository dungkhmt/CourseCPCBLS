package planningoptimization115657k62.phamthanhdong;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Init {
	Random generator = new Random();
	
	int S = 10;
	int need = 5;
	
	int rows = S;
	int columns = S;
	


	
	int min_distance = 1;
	int max_distance = 25;
	int range_dis = max_distance - min_distance;
	
	int min_unit = 0;
	int max_unit = 15;
	int range_unit = max_unit - min_unit;
	
	int min_need = 2;
	int max_need = 25;
	int range_need = max_need - min_need;
	
	public void Gen() throws IOException {
		
	int have[][] = new int[need+1][S];
	int need_arr[] = new int[need+1];
	int d[][] = new int[rows+1][columns+1];
	// general data for distance
	for(int i = 0; i < rows + 1; i++) {
		for(int j = 0; j < columns + 1 ; j++) {
			if(i == j)
				d[i][j] = 0;
			else {
				d[i][j] =  generator.nextInt((max_distance - min_distance) + 1) + min_distance;
			}
		}
	}
	
	// general for need
	for(int i = 0; i < need_arr.length; i++) {
		if(i == 0) need_arr[i] = 0;
		else {
		need_arr[i] = generator.nextInt((max_need - min_need) + 1) + min_need;
		}
	}
	

	// general data for Q
	for(int i = 0; i < have.length; i++)
		for(int j = 0; j < S; j++) {
			if(i == 0) {
				have[i][j] = 0;
			}else {
				have[i][j] = generator.nextInt((max_unit- min_unit) + 1) + min_unit;
			}
		}
	
	
	/* write file */
	// file distance
	String filePath = new File("").getAbsolutePath();
	
	StringBuilder builder = new StringBuilder();
	for(int i = 0; i < d.length; i++)//for each row
	{
	   for(int j = 0; j < columns+1; j++)//for each column
	   {
		  
			  builder.append(d[i][j]+" ");//append to the output string
	   }
	   if(i != d.length - 1)
		   builder.append("\n");//append new line at the end of the row
	  
	}
	BufferedWriter writer = new BufferedWriter(new FileWriter(filePath+"/src/planningoptimization115657k62/phamthanhdong/distance.txt"));
	writer.write(builder.toString());//save the string representation of the board
	writer.close();
	
	
	// write file Q
	
	StringBuilder builder_Q = new StringBuilder();
	for(int i = 0; i < have.length; i++)//for each row
	{
	   for(int j = 0; j < S; j++)//for each column
	   {
	      builder_Q.append(have[i][j]+" ");//append to the output string
	     
	   }
	   if(i != have.length-1)
		   builder_Q.append("\n");//append new line at the end of the row

	   
	}
	BufferedWriter writer_Q = new BufferedWriter(new FileWriter(filePath+"/src/planningoptimization115657k62/phamthanhdong/Q.txt"));
	writer_Q.write(builder_Q.toString());//save the string representation of the board
	writer_Q.close();
	
	
	// wire file need
	 BufferedWriter outputWriter = null;
	  outputWriter = new BufferedWriter(new FileWriter(filePath+"/src/planningoptimization115657k62/phamthanhdong/need.txt"));
	  for (int i = 0; i < need_arr.length; i++) {
	  
	    outputWriter.write(need_arr[i] +" ");
	    //outputWriter.newLine();
	  }
	  outputWriter.flush();  
	  outputWriter.close();
	
	}
}
