import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class ortools1 {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int N=5;
		int M=20;
		int Len[]= {60,70,80,90,100};
		int V[]= {20,15,10,20,20,25,30,15,10,10,20,25,20,10,30,40,25,35,10,10};
		
		int oneM[]= {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		int oneN[]= {1,1,1,1,1};
		
		int P=6;
		int conflict[][]= {{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//0
				{0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0},//1
				{0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0},//2
				{0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0},//3
				{0,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},//4
				{1,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0},//5
				};
		int nConflic[]= {2,2,2,2,3,3};
		Model model=new Model("BT1");
		IntVar[][] x =new IntVar[N][M];// chat long j duoc dua vao thung i
		
		for(int i=0;i<N;i++) {
			for(int j=0;j<M;j++) {
				x[i][j]= model.intVar("x[" + i + "," + j + "]",0,1);
			}
		}
		
		for(int i=0;i<N;i++) {
			model.scalar(x[i], V, "<=", Len[i]).post();// kha nang cua binh
//			model.scalar(x[i], V, ">", 0).post();
		}
		

//		for(int i=0;i<N;i++) {
//			
//			for(int j=0;j<M;j++) {
//				IntVar[] y =new IntVar[M];
//				for(int k=0;k<M;k++) {
//					y[j]=x[k][j];
//				}
//				model.scalar(y, oneM, "=", 1).post();
//			}
//		}
		
		for(int i=0;i<M;i++) {
			IntVar[] y =new IntVar[N];
			for(int j=0;j<N;j++) {
				y[j]=x[j][i];
			}
			model.scalar(y, oneN, "=", 1).post();
		}


		
		for(int i=0;i<N;i++) {
			for(int j=0;j<P;j++) {
				model.scalar(x[i], conflict[j], "<",nConflic[j] ).post();;
			}
			
		}
		model.getSolver().solve();
		for(int i=0;i<N;i++) {
			System.out.print("thung "+i+" : ");
			for(int j=0;j<M;j++) {
				if(x[i][j].getValue()==1) {
					System.out.print(" "+j+" ,");
				}
			}
			System.out.println();
		}
		
	}

}
