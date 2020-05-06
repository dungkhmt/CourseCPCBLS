package planningoptimization115657k62.phamvietbang.project.QHRB;
import java.io.*;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
public class AcademicSemester {
	static int N;
	static int P;
	static int[] c;
	static int alpha;
	static int beta;
	static int lamda;
	static int gamma;
	static int I[];
	static int J[];
	static int a[];
	static int b[];
	public static void loadFile(String data) throws Exception{
		InputStream input = new FileInputStream(data);
		BufferedReader reader= new BufferedReader(
				new InputStreamReader(input)
				);
		String space="\\s+";
		String line1=reader.readLine();
		String []key1=line1.split(space);
		N=Integer.parseInt(key1[0]);
		P=Integer.parseInt(key1[1]);
		String line2=reader.readLine();
		String key2[]=line2.split(space);
		lamda=Integer.parseInt(key2[0]);
		gamma=Integer.parseInt(key2[1]);
		String line3=reader.readLine();
		String key3[]=line3.split(space);
		alpha=Integer.parseInt(key3[0]);
		beta=Integer.parseInt(key3[1]);
		String line4=reader.readLine();
		String key4[]=line4.split(space);
		c=new int[N];
		for(int i=0;i<N;i++) {
			c[i]=Integer.parseInt(key4[i]);
		}
		int k=Integer.parseInt(reader.readLine());
		I=new int[k];
		J=new int[k];
		for(int i=0;i<k;i++) {
			String line=reader.readLine();
			String []key=line.split(space);
			I[i]=Integer.parseInt(key[0]);
			J[i]=Integer.parseInt(key[1]);
		}
		a=new int[N];
		b=new int[P];
		for(int i=0;i<N;i++)a[i]=1;
		for(int i=0;i<P;i++)b[i]=1;
	}
	public static void main(String [] args) throws Exception {
		loadFile("bacp.txt");
		Model model= new Model();
		IntVar x[][]=new IntVar[P][N];
		for(int i=0;i<P;i++) {
			for(int j=0;j<N;j++) {
				x[i][j]=model.intVar("x["+i+","+j+"]",0,1);
			}
		}
		for(int k=0;k<I.length;k++) {
			int i=I[k],j=J[k];
			for(int q=0;q<P;q++) {
				for(int p=0;p<=q;p++) {
					model.ifThen(model.arithm(x[q][i],"=",1), model.arithm(x[p][j], "=", 0));
				}
			}
		}
		for(int i=0;i<P;i++) {
			model.scalar(x[i], a, ">=", alpha).post();
			model.scalar(x[i], a, "<=", beta).post();
			model.scalar(x[i], c, ">=", lamda).post();
			model.scalar(x[i], c, "<=", gamma).post();
		}
		for(int i=0;i<N;i++) {
			IntVar[] y=new IntVar[P];
			for(int j=0;j<P;j++) y[j]=x[j][i];
			model.scalar(y, b, "=", 1).post();
			
		}
		model.getSolver().solve();
		for(int j = 0; j < P; j++){
			  System.out.print("semester " + j + ": ");
			  for(int i = 0; i < N; i++)if(x[j][i].getValue() == 1){
			    System.out.print("[course " + i + ", credit " + c[i] + "] ");
			  System.out.println();
			  }
			  }	  
	}
}
