package planningoptimization115657k62.phamthanhdong;

import org.chocosolver.solver.Model;

import org.chocosolver.solver.Model;
//import org.chocosolver.solver.search.strategy.Search;
//import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
//import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.variables.IntVar;
//import org.chocosolver.solver.*;
//import java.util.*;
//import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
//import org.chocosolver.solver.search.strategy.Search;
//import org.chocosolver.solver.variables.IntVar;
/*
 ý tưởng:
 tương tự bài phân bổ môn học
 Các hoá chất ( môn) phân vào các thùng (kì học)
 các thùng có giới hạn thể tích V ( các kì chỉ có tối đa số môn )
 không thể trùng nhau giữa 2 hoá chất ( 2 môn không học cùng 1 kì ) ở đây biểu diễn các chất không cùng nhau  bởi 2 mảng No1 và No2
 như vậy chỉ giải được trường hợp 2 hoá chất không cùng 1 thùng, với 3 hoá chất, em đang nghĩ thêm
 */

public class Bai1 {

	int N = 5;// so thung
	int M = 20;// so hoa chat

	int V[] = { 60, 70, 80, 90, 100 };
	int P[] = { 20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10 };

// ta có điều kiện ko chung thùng, chia các thùng thành 2 mảng, thay vì No = { { 0, 1 }, { 7, 8 }, { 12, 17 }, { 8, 9 } };
	int No1[] = { 0, 7, 12, 8 };
	int No2[] = { 1, 8, 17, 9 };
	
	int No3[] = {1,0};
	int No4[] = {2,9};
	int No5[] = {9,12};//thay vì {1,2,9},{0,9,12}

	public void Display() {
		System.out.println("Dung tich các thùng:");
		for (int i = 0; i < N; i++)
			System.out.print("Thùng " + (i + 1) + ": " + V[i] + "; ");
		System.out.println("\n");

		System.out.println("Hoá chất:");
		for (int i = 0; i < M; i++)
			System.out.println("Chất " + (i + 1) + ": " + P[i] + "; ");
		System.out.println("\n");
	}

	public void solver() {
		Model m = new Model("Bai1");
		IntVar[][] X = new IntVar[N][M]; // ma trận có số hàng là thùng, số cột lưu các loại hoá chất
		int[] OneN = new int[N];// one có giá trị 0/1 quyết định xem có cho vào thùng hay không
		int[] OneM = new int[M];

		// khởi tạo
		for (int i = 0; i < N; i++)
			OneN[i] = 1;
		for (int i = 0; i < M; i++)
			OneM[i] = 1;

		// X[p,i] = {0, 1}
		for (int i = 0; i < N; i++)
			for (int j = 0; j < M; j++)
				X[i][j] = m.intVar(0, 1);

		//
		for (int p = 0; p < N; p++) {

			
//          m.scalar(X[p], OneM, "<=", V[p]).post();
			// rõ ràng tổng lượng hoá chất phải nhỏ hơn thể tích các thùng
			m.scalar(X[p], P, "<=", V[p]).post();

		}

		// X[q,i] = 1 voi moi i = 0...N-1
		for (int i = 0; i < M; i++) {
			IntVar[] y = new IntVar[N];
			for (int j = 0; j < N; j++)
				y[j] = X[j][i];
			m.scalar(y, OneN, "=", 1).post();
		}

		//điều kiện 2 chất ko trùng nhau
		for (int k = 0; k < No1.length; k++) {
			int i = No1[k];
			int j = No2[k];
			for (int ki = 0; ki < N; ki++)
				for (int ki2 = 0; ki2 <= ki; ki2++)
					m.ifThen(m.arithm(X[ki][i], "=", 1), // nếu đã có chất thuộc N01 thì thôi chất thuộc No2
							m.arithm(X[ki2][j], "=", 0));
		}

		//điều kiện 3 chat ko trùng nhau
		m.getSolver().solve();

		for (int i = 1; i <= N; i++) {
			System.out.print("Thùng " + i + ": ");
			for (int j = 1; j <= M; j++) {
				if (X[i - 1][j - 1].getValue() == 1)
					System.out.print("hoá chất " + j + " / ");
			}
			System.out.println("");
		}
	}

	public static void main(String[] args) {

		Bai1 app = new Bai1();
		app.Display();
		app.solver();

	}
}
