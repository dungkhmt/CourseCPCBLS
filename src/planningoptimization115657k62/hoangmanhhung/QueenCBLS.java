
import cbls115676khmt61.NamThangNguyen_20163848.QueenOpenCBLS;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class QueenCBLS {

	public int n;
	public QueenCBLS(int n) {
		this.n = n;
	}
	
	public void search() {
		// Khởi tạo manager
		LocalSearchManager ls = new LocalSearchManager();
		ConstraintSystem S = new ConstraintSystem(ls);
		
		// Khởi tạo biến
		// x[i] la hang cua quan hau tren cot i
		VarIntLS[] x = new VarIntLS[n];
		for (int i = 0; i < n; i++) {
			x[i] = new VarIntLS(ls, 0, n-1);
		}
		
		// Thêm các ràng buộc
		S.post(new AllDifferent(x));
		
		IFunction[] f1 = new IFunction[n];
		for (int i = 0; i < n; i++) {
			f1[i] = new FuncPlus(x[i], i);
		}
		S.post(new AllDifferent(f1));
		
		IFunction[] f2 = new IFunction[n];
		for (int i = 0; i < n; i++) {
			f2[i] = new FuncPlus(x[i], -i);
		}
		S.post(new AllDifferent(f2));
		
		ls.close();
		
		System.out.println("Init S = " + S.violations());
		MinMaxSelector mms = new MinMaxSelector(S);
		
		int count = 0;
		while(count < 10000 && S.violations() > 0) {
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_v = mms.selectMostPromissingValue(sel_x);
			
			sel_x.setValuePropagate(sel_v);
			System.out.println("Step " + count + ", S = " + S.violations());
			count++;
		}
		System.out.println(S.violations());
	}
	
	public static void main(String[] args) {
		QueenOpenCBLS queen = new QueenOpenCBLS(1000);
		queen.search();
	}

}