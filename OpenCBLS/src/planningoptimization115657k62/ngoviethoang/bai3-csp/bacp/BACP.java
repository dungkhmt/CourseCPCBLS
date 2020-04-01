package bacp;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class BACP {
//	int n = 9;// number of courses: 0,1,2,...,8
//	int p = 4;// number of semesters: 0,1,2,3
//	int[] credits = {3, 2, 2, 1, 3, 3, 1, 2, 2};
//	int alpha = 2;
//	int beta = 4;
//	int lamda = 3;
//	int gamma = 7;
//	int[] I = {0,0,1,2,3,4,3};
//	int[] J = {1,2,3,5,6,7,8};// prerequisites
	int n;
	int p;
	int[] credits;
	int alpha;
	int beta;
	int lamda;
	int gamma;
	int[] I;
	int[] J;
	int[] oneN; //= {1,1,1,1,1,1,1,1,1};
	int[] oneP; // = {1,1,1,1};
	Model model = new Model("BACP");
	IntVar[][] x;
	public void readData(String file) {
		try {
			File fi = new File(file);
			Scanner s = new Scanner(fi);
			n = s.nextInt(); p = s.nextInt();
			lamda = s.nextInt(); gamma = s.nextInt();
			alpha = s.nextInt(); beta = s.nextInt();
			credits = new int[n];
			for (int i = 0; i < n; i++) {
				credits[i] = s.nextInt();
			}
			int temp = s.nextInt();
			I = new int[temp]; J = new int[temp];
			for (int i = 0; i < temp; i++) {
				I[i] = s.nextInt()-1;
				J[i] = s.nextInt()-1;
			}
			oneN = new int[n];
			for (int i = 0; i < n; i++) oneN[i] = 1;
			oneP = new int[p]; 
			for (int i = 0;i < p; i++) oneP[i] = 1;
			s.close();
		} catch (IOException e){
			System.out.println("An error occured.");
			e.printStackTrace();
		}
		
	}
	
	public void bacp( ) {
		readData("./data/csp/data-bacp/bacp.in01");
		//readData("bacp.in01");
		 x = new IntVar[p][n];
		for (int i = 0; i < p; i++) {
			for (int j = 0; j < n; j++) {
				x[i][j] = model.intVar("x["+i+"]["+j+"]", 0,1);
			}
		}
		
		for (int i = 0; i < p; i++) {
			model.scalar(x[i], credits, ">=", lamda).post();
			model.scalar(x[i], credits, "<=", gamma).post();
			
			model.scalar(x[i], oneN, ">=", alpha).post();
			model.scalar(x[i], oneN, "<=", beta).post();
		}
		
		for (int i = 0; i < n; i++) {
			IntVar[] y = new IntVar[p];
			for (int j = 0; j < p; j++) {
				y[j] = x[j][i];
			}
			model.scalar(y, oneP, "=", 1).post();
		}
		
		for (int k = 0; k < I.length; k++) {
			int i = I[k];
			int j = J[k];
			for (int q = 0; q < p; q++)
				for (int l = 0; l <= q; l++)
					model.ifThen(
							model.arithm(x[q][i], "=", 1), 
							model.arithm(x[l][j], "=", 0)
					);
		}
		
		model.getSolver().solve();
		
		for(int j = 0; j < p; j++){
			System.out.println("semester " + j + ": ");
			for(int i = 0; i < n; i++) {
				if(x[j][i].getValue() == 1){
					System.out.print("[course " + (i+1) + ", credit " + credits[i] + "]");
					System.out.println();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		BACP app = new BACP();
		app.bacp();
	}
}
