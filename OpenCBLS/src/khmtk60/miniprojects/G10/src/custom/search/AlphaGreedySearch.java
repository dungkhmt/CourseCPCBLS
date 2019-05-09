package custom.search;

import custom.selector.AlphaMinMaxSelector;
import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;

import java.util.*;

public class AlphaGreedySearch {
    private double _alpha;

    public AlphaGreedySearch(double alpha){
        _alpha = alpha;
    }


    public String name(){
        return "AlphaGreedySearch";
    }
    public void search(IConstraint S, int maxTime, int maxIter, boolean verbose){
        VarIntLS[] x = S.getVariables();

        int it = 0;
        maxTime = maxTime * 1000;
        double t0 = System.currentTimeMillis();
        AlphaMinMaxSelector mms = new AlphaMinMaxSelector(S);

        int best = S.violations();
        int[] x_best = new int[x.length];
        while(it < maxIter && System.currentTimeMillis() - t0 < maxTime &&S.violations()>0){
            VarIntLS sel_x = mms.selectViolatingVariable(_alpha);
            int sel_v = mms.selectMostPromissingValue(sel_x);

            sel_x.setValuePropagate(sel_v);
            if(verbose)
                System.out.println(name() + "::search --> Step " + it +", S = " + S.violations());

            if(S.violations() < best){
                best = S.violations();
                for(int i = 0; i < x.length; i++)
                    x_best[i] = x[i].getValue();
            }
            it++;
        }

        for(int i = 0; i < x.length; i++)
            x[i].setValuePropagate(x_best[i]);
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }
}
