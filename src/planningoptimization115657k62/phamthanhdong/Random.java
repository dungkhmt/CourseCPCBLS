package planningoptimization115657k62.phamthanhdong;

import java.io.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

 

public class Random {
    Random generator = new Random();
    
    int rows = 101;
    int columns = 101;
    int need = 100;
    
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
        for(int j = 0; j < columns-1; j++) {
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
          if(i != rows -1)
              builder.append(d[i][j]+" ");//append to the output string
          else
              builder.append(d[i][j]);
       
       }
      
    }
    BufferedWriter writer = new BufferedWriter(new FileWriter(filePath+"/src/planningoptimization115657k62/phamthanhdong/distance.txt"));
    writer.write(builder.toString());//save the string representation of the board
    writer.close();
    
    
    // write file Q
    
    StringBuilder builde_Q = new StringBuilder();
    for(int i = 0; i < need; i++)//for each row
    {
       for(int j = 0; j < columns; j++)//for each column
       {
    
          builder.append(d[i][j]+" ");//append to the output string
         
       }
       
    }
    BufferedWriter writer_Q = new BufferedWriter(new FileWriter(filePath+"/src/planningoptimization115657k62/phamthanhdong/Q.txt"));
    writer_Q.write(builder.toString());//save the string representation of the board
    writer_Q.close();
    
    
    // wire file need
    
    
     BufferedWriter outputWriter = null;
      outputWriter = new BufferedWriter(new FileWriter(filePath+"/src/planningoptimization115657k62/pamthanhdong/need.txt"));
      for (int i = 0; i < need_arr.length; i++) {
        // Maybe:
        outputWriter.write(need_arr[i]+" ");
        // Or:
        outputWriter.write(Integer.toString(need_arr[i]));
//        outputWriter.newLine();
      }
      outputWriter.flush();  
      outputWriter.close();
    
    }
}
 
//
public class Random {
	public static void main(String args[]) throws IOException {
		// System.out.println("Random");
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

		// print
//		try {
//			FileWriter fw = new FileWriter("C:\\Users\\dong.pt173020\\eclipse-workspace\\ToiUuLapKeHoach\\src\\planningoptimization115657k62\\phamthanhdong\\distance.txt");
//			
//			fw.write("Welco jkhfhme to java.");
//			fw.close();
//		} catch (Exception e) {
//			System.out.println("wtf");
//		}

		final String FILE_URL = "/src/planningoptimization115657k62/phamthanhdong/distance.txt";
		File file = new File(FILE_URL);
		OutputStream outputStream = new FileOutputStream(file,true);
	
		@SuppressWarnings("resource")
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < colum; j++) {
				outputStreamWriter.write(b[i][j]);
				// Dùng để xuống hàng
				//outputStreamWriter.write("\n");
			}
			//outputStreamWriter.write("\n");
		}
		// Đây là phương thức quan trọng!
		// Nó sẽ bắt chương trình chờ ghi dữ liệu xong thì mới kết thúc chương trình.
		outputStreamWriter.flush();
	}
}
