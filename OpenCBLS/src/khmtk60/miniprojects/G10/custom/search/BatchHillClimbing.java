package khmtk60.miniprojects.G10.custom.search;

import khmtk60.miniprojects.G10.localsearch.model.IConstraint;
import khmtk60.miniprojects.G10.localsearch.model.VarIntLS;

import java.util.*;

public class BatchHillClimbing {
    private Random rand = null;
    private double t0;

    public BatchHillClimbing() {
        rand = new Random();
    }

    public void search(IConstraint S, int maxIter, int maxTime, int bs){

        t0 = System.currentTimeMillis();
        maxTime *= 1000;
        ArrayList<VarIntLS> y = new ArrayList<VarIntLS>(Arrays.asList(S.getVariables()));
        List<VarIntLS> batch = null;
        int miniStep = y.size() / bs;

        ArrayList<AssignMove> cand = new ArrayList<AssignMove>();
        int it = 0;
        Random R = new Random();
        System.out.println(S.violations());

        while(it < maxIter && System.currentTimeMillis() - t0 < maxTime &&S.violations()>0) {
            Collections.shuffle(y);
            for (int count=0; count<miniStep; ++count) {
                cand.clear();
                int minDelta = Integer.MAX_VALUE;
                batch = y.subList(count*bs, (count+1)*bs);
                for(VarIntLS yy: batch) {
                    for(int v: yy.getDomain()) {
                        int d = S.getAssignDelta(yy, v);
                        if(d < minDelta) {
                            cand.clear();
                            cand.add(new AssignMove(yy, v));
                            minDelta = d;

                        } else if(d == minDelta) {
                            cand.add(new AssignMove(yy, v));
                        }
                    }
                }

                int idx = R.nextInt(cand.size());
                AssignMove m = cand.get(idx);
                m.x.setValuePropagate(m.v);
                System.out.println("MiniStep " + it + "/" + count + ", vio: " + S.violations());
            }
//            System.out.println("Step " + it + ", vio: " + S.violations());
            ++it;
        }
    }

    class AssignMove {
        VarIntLS x;
        int v;

        AssignMove(VarIntLS _x, int _v) {
            x=_x;
            v=_v;
        }
    }
}
