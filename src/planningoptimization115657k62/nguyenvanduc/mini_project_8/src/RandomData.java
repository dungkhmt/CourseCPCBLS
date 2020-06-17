package planningoptimization115657k62.nguyenvanduc.mini_project_8.src;

import java.io.*;
import java.util.Random;
import java.util.Scanner;
public class RandomData {
	public static void main(String[] args) throws IOException {
		int num;
		int[][] conflict;
		int N, M, Q, dem, min_student, max_student, min_capacity, max_capacity;
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		Random rd = new Random();
		System.out.print("Nhap N (so mon thi): ");
		N = scanner.nextInt();
		conflict = new int[N][N];
		System.out.println();
		System.out.print("Nhap M (so phong thi): ");
		M = scanner.nextInt();
		System.out.println();
		System.out.printf("Nhap Q (so luong cap conflict: ");
		Q = scanner.nextInt();
		System.out.printf("Nhap min student: ");
		min_student = scanner.nextInt();
		System.out.println();
		System.out.printf("Nhap max student: ");
		max_student = scanner.nextInt();
		System.out.println();
		System.out.printf("Nhap min capacity: ");
		min_capacity = scanner.nextInt();
		System.out.println();
		System.out.printf("Nhap max capacity: ");
		max_capacity = scanner.nextInt();
		System.out.println();
		 FileWriter fw = new FileWriter("src/planningoptimization115657k62/nguyenvanduc/mini_project_8/"
	        		+ "data/"+N +"_"+M+"_"+Q+".txt");
		dem = Q;
		fw.write(N + " " + M);
		fw.write("\r\n");
		//System.out.printf(N + " " + M);
		//System.out.println();
		for(int i = 1; i <= N; i++) {
			num = min_student + rd.nextInt(max_student - min_student + 1);
			fw.write(num + " ");
			//System.out.print(num + " ");
		}
		fw.write("\r\n");
		//System.out.println();
		fw.write(max_capacity + " ");
		for(int i = 2; i <= M; i++) {
			num = min_capacity + rd.nextInt(max_capacity - min_capacity + 1);
			fw.write(num + " ");
			//System.out.print(num + " ");
		}
		fw.write("\r\n");
		//System.out.println();
		fw.write(Q + " ");
		//System.out.println(Q);
		fw.write("\r\n");
//		while(dem > 0) {
//		for (int i = 0; i < N - 1  ; i++) {
//			for(int j = i + 1; j < N; j++) {
//				if(conflict[i][j] == 1) continue;
//				num = rd.nextInt(2);
//				if(num == 1) dem --;
//				if(dem < 0) break; 
//				conflict[i][j] = num;
//			}
//			if(dem < 0) break; 
//		}
//		}	
		for (int i = 0; i < N  ; i++) {
			for(int j = 0; j < N; j++) {
				conflict[i][j] = 0;
			}}
		int first, second;
		while(dem > 0) {
			first = rd.nextInt(N);
			second = rd.nextInt(N);
			while(second == first) second = rd.nextInt(N);
			if(conflict[first][second] == 1||conflict[second][first] == 1) continue;
			else {conflict[first][second] = 1;
			conflict[second][first] = 1;
			dem--;
			}
			}
		
		dem = 0;
		for (int i = 0; i < N -1 ; i++) {
			for(int j = i + 1; j < N; j++) {
				if(conflict[i][j] == 1) {
					dem++;
					fw.write((i + 1) + " " + (j + 1));
					//System.out.print((i + 1) + " " + (j + 1));
					fw.write("\r\n");
					//System.out.println();
				}
				if(dem == Q) break;
			}
			if(dem == Q) break;
		}

        fw.close();
        System.out.println("file saved to /data/.....");
	}
	

}

