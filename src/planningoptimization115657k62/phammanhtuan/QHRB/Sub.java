package planningoptimization115657k62.phammanhtuan.QHRB;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.jgrapht.alg.scoring.AlphaCentrality;

public class Sub {
	static int P;
	static int N;
	static int[] credits;
	static int alpha;
	static int beta ;
	static int lamda;
	static int gamma;
	static int[] I ;
	static int[] J ;

	static int[] oneN ;
	static int[] oneP ;
	public static void load (String s) throws Exception {
		InputStream in = new FileInputStream(s) ;
		BufferedReader br = new BufferedReader(
					new InputStreamReader(in)
				);
		N = Integer.parseInt(br.readLine());
		P = Integer.parseInt(br.readLine());
		lamda = Integer.parseInt(br.readLine());
		gamma = Integer.parseInt(br.readLine());
		alpha = Integer.parseInt(br.readLine());
		beta = Integer.parseInt(br.readLine());
		credits = new int[N];
		String delims = "\\s+";
		String line = br.readLine();
		String[] tokens = line.split(delims);
		for(int i = 0; i < N; i++) {
			credits[i] = Integer.parseInt(tokens[i]);
		}
		int k = Integer.parseInt(br.readLine());
		I = new int[k];
		J = new int[k];
		for (int i = 0;i<k;i++) {
			String line1 = br.readLine();
			String[] token = line1.split(delims);
			I[i]= Integer.parseInt(token[0]);
			J[i]= Integer.parseInt(token[1]);
		}
		oneN = new int[N];
		oneP = new int[P];
		for (int i=0;i<N;i++) oneN[i]=1;
		for (int i=0;i<P;i++) oneP[i]=1;
	}
	public static void main(String[] args) throws Exception {
		load("E:\\Java\\eclipse\\bacp.txt");
		Model model = new Model("Sub");
		IntVar[][] X = new IntVar[P][N];
		for (int i = 0;i<P;i++) {
			for (int j = 0;j<N;j++) {
				X[i][j] = model.intVar("X["+i+"]["+j+"]",0,1);
			}
		}
		
		for (int i=0;i<P;i++) {
			model.scalar(X[i], credits, ">=",lamda).post();
			model.scalar(X[i], credits, "<=",gamma).post();
			model.scalar(X[i], oneN, ">=", alpha).post();
			model.scalar(X[i], oneN, "<=", beta).post();
		}
		for (int i=0;i<N;i++) {
			IntVar[] y = new IntVar[P];
			for (int j = 0;j<P;j++) y[j]=X[j][i];
			model.scalar(y, oneP, "=", 1).post();
		}
		for (int k=0;k<I.length;k++) {
			for (int i=0;i<P;i++)
				for (int j=0;j<=i;j++) {
					model.ifThen(model.arithm(X[i][I[k]-1], "=", 1), model.arithm(X[j][J[k]-1], "=", 0));
				}
		}
		model.getSolver().solve();
		for (int i=0;i<P;i++) {
			System.out.print("Semester"+i+": ");
			for (int j=0;j<N;j++) {
				if (X[i][j].getValue()==1) {
					System.out.print((j+1)+" ");
				}
			}
			System.out.println();
		}
	}
}
