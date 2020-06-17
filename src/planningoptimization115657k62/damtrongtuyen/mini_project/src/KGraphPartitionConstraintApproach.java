package planningoptimization115657k62.damtrongtuyen.mini_project.src;
import core.VarInt;
import localsearch.constraints.basic.*;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.max_min.Max;
import localsearch.functions.max_min.Min;
import localsearch.model.*;
import localsearch.search.TabuSearch;
import org.chocosolver.solver.constraints.Constraint;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class KGraphPartitionConstraintApproach {
    // sử dụng contraint programming cho Kgraph partitioning problem

    public static void main(String[] args) {
        int N = 12;
        int K = 2;
        int ALPHA = 10;
        LocalSearchManager lsm = new LocalSearchManager();
        ConstraintSystem s = new ConstraintSystem(lsm);

        VarIntLS x[] = new VarIntLS[N]; // x[i] = k=> node i thuộc cụm k
        VarInt m[] = new VarInt[K];
        for (int i = 0; i < N; i++) {
            x[i] = new VarIntLS(lsm, 0, K-1);
        }
        int countV[] = new int[N];
        for (int i = 0; i < N; i++) {
            countV[i] = 1; // for conditional sum
        }
        ConditionalSum[] f = new ConditionalSum[K];

        for (int i = 0; i < K; i++) {
            // todo: create condition sum ntn?
            //s.post(new IsEqual(new ConditionalSum(x, countV, i).getValue(), m[i].getValue()));
            f[i] = new ConditionalSum(x, countV, i);
        }
        Max max = new Max(f);
        Min min = new Min(f);
        IFunction b = new FuncMinus(max,min);
        s.post(new LessOrEqual(b, ALPHA));


        TabuSearch ts = new TabuSearch();
//        ts.searchMaintainConstraintsFunctionMinimize();

    }
}
