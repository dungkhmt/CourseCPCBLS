package planningoptimization115657k62.nguyenvanduc;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TSPDynSEC{
    static {
        System.loadLibrary("jniortools");
    }

    int N = 5;
    int[][] c = {
            {0,4,2,5,6},
            {2,0,5,2,7},
            {1,2,0,6,3},
            {7,5,8,0,3},
            {1,2,4,3,0}
    };

    MPVariable[][] X;
    MPSolver solver;
    MPObjective obj;

    public static void main(String[] args) {
        TSPDynSEC app = new TSPDynSEC();
        app.solveTSPDynSEC();
    }

    public  void solveTSPDynSEC() {
        init();
        boolean found = false;
        int count = 1;
        while (!found) {
            System.out.println("solve: " + count++ + "th");
            MPSolver.ResultStatus resultStatus = solver.solve();
            if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
                System.err.println("does not have solution!");
                return;
            }
            System.out.println("obj = " + obj.value());

            ArrayList<Integer> checkCycle = extractCycle(0);
            if (checkCycle.size() == N) {
                found = true;
                System.out.println("Global tour detected, solution found!");
                System.out.println(checkCycle);
            } else {
                HashSet<ArrayList<Integer>> subs = findAllSub();
                createSEC(subs);
                for (ArrayList<Integer> sub : subs) {
                    System.out.println("Sub tour detected!: ");
                    System.out.println(sub);
                }
            }
        }

    }



    public void init() {
        solver = new MPSolver("TSPDynSECsolver", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);

        X = new MPVariable[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
               X[i][j] = solver.makeIntVar(0, 1, "x[" + i + ", " + j +"]");
            }
        }
        for (int i = 0; i < N; i++) {
            //tong theo hang = 1
            MPConstraint fc1 = solver.makeConstraint(1, 1);
            for (int j = 0; j < N; j++) {
                if (j != i) {
                    fc1.setCoefficient(X[i][j], 1);
                }
            }

            //tong the cot = 1
            MPConstraint fc2 = solver.makeConstraint(1, 1);
            for (int j = 0; j < N; j++) {
                if (j != i) {
                    fc2.setCoefficient(X[j][i], 1);
                }
            }
        }

        obj = solver.objective();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i != j) {
                    obj.setCoefficient(X[i][j], c[i][j]);
                }
            }
        }
        obj.setMinimization();
    }

    public void createSEC(HashSet<ArrayList<Integer>> Subs) {
        for (ArrayList<Integer> sub : Subs) {
            MPConstraint subConstraint = solver.makeConstraint(0, sub.size() - 1);
            for (int i : sub) {
                for (int j : sub) {
                    if (i != j) {
                        subConstraint.setCoefficient(X[i][j], 1);
                    }
                }
            }
        }
    }
    // return all subtour;
    HashSet<ArrayList<Integer>> findAllSub() {
        boolean[] mark = new boolean[N];
        HashSet<ArrayList<Integer>> subs = new HashSet<>();
        for (int i = 0; i < N; i++) mark[i] = false;
        for (int i = 0; i < N; i++) {
            if (!mark[i]) {
                mark[i] = true;
                ArrayList<Integer> cycle = extractCycle(i);
                if (cycle != null) {
                    subs.add(cycle);
                    for (int j : cycle) mark[j] = true;
                }
            }
        }

        return subs;
    }

    //return cycle include s,  if doest not find return null
    ArrayList<Integer> extractCycle(int s) {
        ArrayList<Integer> tempList = new ArrayList<>();
        int x = s;
        int rep = -1;
        while (rep == -1) {
            tempList.add(x);
            x = findNext(x);
            if (x == -1) return null;
            for (int i = 0; i < tempList.size(); i++) {
                if (x == tempList.get(i)){
                    rep = i;
                    break;
                }
            }
        }

        ArrayList<Integer> cycle = new ArrayList<>();
        for (int i = rep; i < tempList.size(); i++) cycle.add(tempList.get(i));
        return cycle;

    }

    int findNext(int s) {
        for (int i = 0; i < N; i++) {
            if ( s != i && X[s][i].solutionValue() == 1) return i;
        }
        return -1;
    }

}