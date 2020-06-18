package my_work;

import java.io.*;
import java.util.Random;

public class random_init {
    
    private int c[], d[], I1[], I2[]; // cac du lieu muon khoi tao:
    int N, M, numOfConflict;
    
    public void create_data(int N, int M, int conflictNum) {
    	Random generator = new Random();
    	c = new int[M];
    	d = new int[N];
    	I1 = new int[conflictNum];
    	I2 = new int[conflictNum];
	cMax = -1;
    	
    	for(int i = 0; i<M; i++) {
    		c[i] = generator.nextInt(300);
		if (cMax > c[i]) cMax = c[i];
    	}
    	
    	for(int i = 0; i<N; i++) {
    		d[i] = generator.nextInt(cMax);
    	}
    	
    	for(int i = 0; i<conflictNum; i++) {
    		I1[i] = generator.nextInt(N); // gioi han trong so luong lop thi
    		I2[i] = generator.nextInt(N);
    		
    		while (I2[i] == I1[i]) {
    			I2[i] = generator.nextInt(N);
    		}
    		
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
    
    public void writeFile(int N, int M, int conflictNum) throws IOException {
    	String rootpath = "/media/quyentran/A23622BE36229379/A.School/"
    			+ "Toi_uu_lap_KH/Code_/Or-tools/examples/java/my_work/data/"; // sua root path cho phu hop
    	String filename = N + "class-" + M + "room-" + conflictNum + "conflict.txt";
    	System.out.println("File name: " + filename);
    	
    	File file = new File(rootpath + filename); // cho nay can sua path de cho phu hop. Vd: rootpath + filename
        OutputStream outputStream;
			outputStream = new FileOutputStream(file);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
			
			//ghi N, M tren dong dau tien:
			outputStreamWriter.write(N + " ");
			outputStreamWriter.write(M + " ");
			outputStreamWriter.write(conflictNum + "\n");
			
			//ghi tiep d[i] tren dong tiep
			String temp = "";
	        for(int i = 0; i<N; i++) {
	        	temp += d[i] + " ";
	        }
	        outputStreamWriter.write(temp + "\n");
	        
	        //ghi c[j] tren dong tiep theo:
	        temp = "";
	        for(int i = 0; i<M; i++) {
	        	temp += c[i] + " ";
	        }
	        outputStreamWriter.write(temp + "\n");
	        
	        //ghi cac mang chua thong tin lop conflict - moi mang I1, I2 tren 1 hang:
	        temp = "";
	        for(int i = 0; i<conflictNum; i++) {
	        	temp += I1[i] + " ";
	        }
	        outputStreamWriter.write(temp + "\n");
	        
	        temp = "";
	        for(int i = 0; i<conflictNum; i++) {
	        	temp += I2[i] + " ";
	        }
	        outputStreamWriter.write(temp);

	        outputStreamWriter.flush();
    }
    
    public static void main(String[] args) {
    	
    	random_init run = new random_init();
    	
    	//cac tham so N, M, numOfConflict co the chinh sua:
	///// Co the sua cho nay de tao ra cac file voi du lieu khac nhau
    	int N = run.N = 3;
    	int M = run.M = 4;
    	int numOfConflict = run.numOfConflict = 2;
    	
    	
    	run.create_data(N, M, numOfConflict);
    	run.result();
    	try {
    		/// File kqua se co dang Vd: 3class-4room-2conflict.txt
    		///Can sua them rootpath de xac dinh vi tri luu file
			run.writeFile(N, M, numOfConflict); ////
			System.out.println("Da xuat du lieu xong!!!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
