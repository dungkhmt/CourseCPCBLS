package planningoptimization115657k62.NguyenVanTien;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class Routing {

	int K = 2;// number of routes
    int N = 6;// number of clients
    int capacity = 11;
    int[] demand = {0,4,2,5,2,3,5};
    int[][] d = {
            {0,3,2,1,4,3,7},
            {2,0,2,3,5,3,9},
            {1,3,0,2,4,2,4},
            {5,3,2,0,1,1,7},
            {3,1,5,1,0,3,6},
            {6,3,2,4,4,0,9},
            {2,3,2,1,2,8,0}
    };	
	public void solver() {
		Model model = new Model("Routing");
		IntVar ans = model.intVar("ans", 0, IntVar.MAX_INT_BOUND);
		// l: khoang cach tich luy
		IntVar[] l = new IntVar[N+2*K+1];
		// w: trong luong tich luy
		IntVar[] w = new IntVar[N+2*K+1];
		// ir: index route 
		IntVar[] ir = new IntVar[N+2*K +1];
		// x: diem tiep theo cua i
		IntVar[] x = new IntVar[N+K +1];
		
		for (int s = N+1; s <= N+K; s++) {
			l[s] = model.intVar("l["+s+"]",0,0);
			w[s] = model.intVar("w["+s+"]",0,0);
		}
		
		for (int i = 1; i <= K; i++) {
			model.arithm(ir[N+i], "=", ir[N+K+i]).post();
		}
		
		for (int i = 1; i <= N+K-1; i++) {
			for (int j = i+1; j <= N+K; j++) {
				model.arithm(x[i],"!=",x[j]).post();				
			}
			model.arithm(x[i],"!=",i).post();
		}
		model.arithm(x[N+K],"!=",N+K).post();
		
		for (int i = 1; i <= N+K; i++) {
			for (int j = 1; j <= N+2*K; j++) {
				model.ifThen(model.arithm(x[i],"=",j), model.arithm(ir[i],"=",ir[j]));
				model.ifThen(model.arithm(x[i],"=",j), model.arithm(model.intOffsetView(l[i],d[i][j]),"=",l[j]));
				model.ifThen(model.arithm(x[i],"=",j), model.arithm(model.intOffsetView(w[i],demand[j]),"=",w[j]));
			}
		}
		
		for (int i = N+K+1; i < N+2*K; i++) {
			model.arithm(w[i],"<=",capacity).post();
		}

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Routing app = new Routing();
		app.solver();
	}

}
