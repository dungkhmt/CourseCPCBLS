package project;

import localsearch.constraints.basic.*;
import localsearch.functions.basic.ConstraintViolations;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BinPackingLS {
    private final ArrayList<Move> candidate = new ArrayList<Move>();
    private final Random R = new Random();
    //Input
    // 3, 1, 2, 3, 3, 3, 1, 2, 3, 3, 3, 1, 2, 3, 3, 3, 1, 2, 3, 3, 3, 1, 2, 3, 3, 3, 1, 2, 3, 3, 3, 1, 2, 3, 3, 3, 1};
    //Variables
    int n = 50;
    int k = 25;
    int[] W = {4, 5, 4, 5, 4, 5, 4, 5, 4, 5, 4, 5, 1, 5, 4, 5, 4, 5, 4, 5, 4, 5, 4, 5, 4};
    int[] H = {4, 6, 5, 6, 5, 6, 5, 6, 5, 6, 5, 6, 3, 6, 5, 6, 5, 6, 5, 6, 5, 6, 5, 6, 5};
    int[] w = {2, 3, 1, 2, 3, 1, 3, 1, 2, 3, 2, 3, 1, 2, 3, 1, 3, 1, 2, 3, 2, 3, 1, 2, 3, 1, 3, 1, 2, 3, 2, 3, 1, 2, 3, 1, 3, 1, 2, 3, 2, 3, 1, 2, 3, 1, 3, 1, 2, 3};
    int[] h = {3, 1, 2, 2, 2, 1, 5, 1, 3, 1, 3, 1, 2, 2, 2, 1, 5, 1, 3, 1, 3, 1, 2, 2, 2, 1, 5, 1, 3, 1, 3, 1, 2, 2, 2, 1, 5, 1, 3, 1, 3, 1, 2, 2, 2, 1, 5, 1, 3, 1};
    int[] c = {2, 3, 3, 3, 1, 2, 3, 3, 3, 1, 2, 3, 3, 3, 1, 2, 3, 3, 3, 1, 2, 3, 3, 3, 1};

    VarIntLS[] x = new VarIntLS[n];
    VarIntLS[] y = new VarIntLS[n];
    VarIntLS[] r = new VarIntLS[n];
    VarIntLS[] assigned = new VarIntLS[n];
    VarIntLS[] used = new VarIntLS[k];
    //Local search manager
    LocalSearchManager mgr;
    ConstraintSystem S;

    private IFunction obj;
    private IFunction F;
    private IConstraint s;

    public static void main(String[] args) {
        BinPackingLS binPackingLS = new BinPackingLS();
        binPackingLS.stateModel();
        double t0 = System.currentTimeMillis();
        binPackingLS.search2();
        
        double t = System.currentTimeMillis() - t0;
        for (int i = 0; i < binPackingLS.n; i++)
            System.out.println("x" + i + " = " + binPackingLS.x[i].getValue() + "\t\t\t y" + i + " = " + binPackingLS.y[i].getValue() + "\t\t\t assigned[" + i + "] = " + binPackingLS.assigned[i].getValue() + "\t\t\t r[" + i + "] = " + binPackingLS.r[i].getValue());
        for (int i = 0; i < binPackingLS.k; i++)
            System.out.println("used[" + i + "] = " + binPackingLS.used[i].getValue());
        System.out.println("t = " + t * 0.001 + " s");
    }

    public void stateModel() {
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);

        for (int i = 0; i < n; i++) {
            x[i] = new VarIntLS(mgr, 0, 99);
            y[i] = new VarIntLS(mgr, 0, 99);
            r[i] = new VarIntLS(mgr, 0, 1);
            assigned[i] = new VarIntLS(mgr, 0, k - 1);
        }
        for (int i = 0; i < k; i++)
            used[i] = new VarIntLS(mgr, 0, 1);

        /*Constraints*/
        ArrayList<IConstraint> list = new ArrayList<IConstraint>();

        /* Inside */
        for (int i = 0; i < n; i++)
            for (int j = 0; j < k; j++) {
                IConstraint[] c = new IConstraint[10];

                c[0] = new IsEqual(assigned[i], j);

                c[1] = new LessOrEqual(new FuncPlus(x[i], w[i]), W[j]);
                c[2] = new LessOrEqual(new FuncPlus(y[i], h[i]), H[j]);

                c[3] = new LessOrEqual(new FuncPlus(x[i], h[i]), W[j]);
                c[4] = new LessOrEqual(new FuncPlus(y[i], w[i]), H[j]);

                c[5] = new IsEqual(r[i], 0);
                c[6] = new IsEqual(r[i], 1);

                c[7] = new Implicate(new AND(c[0], c[5]), new AND(c[1], c[2]));
                c[8] = new Implicate(new AND(c[0], c[6]), new AND(c[3], c[4]));

                list.add(c[7]);
                list.add(c[8]);
                S.post(c[7]);
                S.post(c[8]);
            }

        /* Not overlap */
        for (int i = 0; i < n - 1; i++)
            for (int j = i + 1; j < n; j++) {
                IConstraint[] c = new IConstraint[5];
                IConstraint[] sc = new IConstraint[4];

                c[0] = new IsEqual(assigned[i], assigned[j]);

                c[1] = new AND(new IsEqual(r[i], 0), new IsEqual(r[j], 0));
                sc[0] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]);
                sc[1] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]);
                sc[2] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
                sc[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]);
                list.add(new Implicate(new AND(c[0], c[1]), new OR(sc)));
                S.post(new Implicate(new AND(c[0], c[1]), new OR(sc)));

                c[2] = new AND(new IsEqual(r[i], 0), new IsEqual(r[j], 1));
                sc[0] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]);
                sc[1] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]);
                sc[2] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]);
                sc[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]);
                list.add(new Implicate(new AND(c[0], c[2]), new OR(sc)));
                S.post(new Implicate(new AND(c[0], c[2]), new OR(sc)));

                c[3] = new AND(new IsEqual(r[i], 1), new IsEqual(r[j], 0));
                sc[0] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]);
                sc[1] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]);
                sc[2] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
                sc[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]);
                list.add(new Implicate(new AND(c[0], c[3]), new OR(sc)));
                S.post(new Implicate(new AND(c[0], c[3]), new OR(sc)));

                c[4] = new AND(new IsEqual(r[i], 1), new IsEqual(r[j], 1));
                sc[0] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]);
                sc[1] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]);
                sc[2] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]);
                sc[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]);
                list.add(new Implicate(new AND(c[0], c[4]), new OR(sc)));
                S.post(new Implicate(new AND(c[0], c[4]), new OR(sc)));
            }

        /* assigned & used */
        for (int i = 0; i < n; i++)
            for (int j = 0; j < k; j++) {
                IConstraint c1 = new Implicate(new IsEqual(assigned[i], j), new IsEqual(used[j], 1));
                list.add(c1);
                S.post(c1);
            }

        for (int j = 0; j < k; j++) {
            IConstraint[] c = new IConstraint[n];
            for (int i = 0; i < n; i++) {
                c[i] = new NotEqual(assigned[i], j);
            }
            IConstraint c0 = new AND(c);
            list.add(new Implicate(c0, new IsEqual(used[j], 0)));
            S.post(new Implicate(c0, new IsEqual(used[j], 0)));
        }

        IConstraint[] arr = new IConstraint[list.size()];
        for (int i = 0; i < list.size(); i++)
            arr[i] = list.get(i);
        s = new AND(arr);


        /*Objective*/
        obj = new FuncMult(used[0], c[0]);
        for (int i = 1; i < k; i++)
            obj = new FuncPlus(obj, new FuncMult(used[i], c[i]));


        F = new FuncPlus(new FuncMult(new ConstraintViolations(s), 1800), new FuncMult(obj, 100));

        mgr.close();
    }

    public void hillClimbingSearch() {
        HillClimbingSearch hillClimbingSearch = new HillClimbingSearch(S);
        hillClimbingSearch.search(1000);

    }

    void exploreNeighborhoodConstraint(VarIntLS[] x, VarIntLS[] y, VarIntLS[] r, VarIntLS[] assigned, VarIntLS[] used, IConstraint s, List<Move> candidate) {
        int minDelta = Integer.MAX_VALUE;
        candidate.clear();
        for (int i = 0; i < n; i++) {
            for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
                int delta = s.getAssignDelta(x[i], v);
                if (delta < 0) {
                    if (delta < minDelta) {
                        candidate.add(new Move(i, v));
                        minDelta = delta;
                    } else if (delta == minDelta) {
                        candidate.add(new Move(i, v));
                    }
                }
            }
            for (int v = 0; v <= 99; v++) {
                int delta = s.getAssignDelta(y[i], v);
                if (delta < 0) {
                    if (delta < minDelta) {
                        candidate.add(new Move(i + n, v));
                        minDelta = delta;
                    } else if (delta == minDelta) {
                        candidate.add(new Move(i + n, v));
                    }
                }
            }
            for (int v = 0; v <= 1; v++) {
                int delta = s.getAssignDelta(r[i], v);
                if (delta < 0) {
                    if (delta < minDelta) {
                        candidate.add(new Move(i + n + n, v));
                        minDelta = delta;
                    } else if (delta == minDelta) {
                        candidate.add(new Move(i + n + n, v));
                    }
                }
            }
            for (int v = 0; v <= k - 1; v++) {
                int delta = s.getAssignDelta(assigned[i], v);
                if (delta < 0) {
                    if (delta < minDelta) {
                        candidate.add(new Move(i + n + n + n, v));
                        minDelta = delta;
                    } else if (delta == minDelta) {
                        candidate.add(new Move(i + n + n + n, v));
                    }
                }
            }
        }
        for (int i = 0; i < k; i++) {
            for (int v = 0; v <= 1; v++) {
                int delta = s.getAssignDelta(used[i], v);
                if (delta < 0) {
                    if (delta < minDelta) {
                        candidate.add(new Move(i + n + n + n + n, v));
                        minDelta = delta;
                    } else if (delta == minDelta) {
                        candidate.add(new Move(i + n + n + n + n, v));
                    }
                }
            }
        }
    }

    void exploreNeighborhoodMaintainConstraint(VarIntLS[] x, VarIntLS[] y, VarIntLS[] r, VarIntLS[] assigned, VarIntLS[] used, IConstraint s, IFunction obj, List<Move> candidate) {
        candidate.clear();
        int minDeltaF = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            for (int v = 0; v < 100; v++) {
                int deltaC = s.getAssignDelta(x[i], v);
                int deltaF = obj.getAssignDelta(x[i], v);
                if (deltaC > 0) continue;
                if (deltaF < 0) {
                    if (deltaF < minDeltaF) {
                        candidate.clear();
                        candidate.add(new Move(i, v));
                        minDeltaF = deltaF;
                    } else if (deltaF == minDeltaF) {
                        candidate.add(new Move(i, v));
                    }
                }
            }
            for (int v = 0; v < 100; v++) {
                int deltaC = s.getAssignDelta(y[i], v);
                int deltaF = obj.getAssignDelta(y[i], v);
                if (deltaC > 0) continue;
                if (deltaF < 0) {
                    if (deltaF < minDeltaF) {
                        candidate.clear();
                        candidate.add(new Move(i + n, v));
                        minDeltaF = deltaF;
                    } else if (deltaF == minDeltaF) {
                        candidate.add(new Move(i + n, v));
                    }
                }
            }
            for (int v = 0; v < 2; v++) {
                int deltaC = s.getAssignDelta(r[i], v);
                int deltaF = obj.getAssignDelta(r[i], v);
                if (deltaC > 0) continue;
                if (deltaF < 0) {
                    if (deltaF < minDeltaF) {
                        candidate.clear();
                        candidate.add(new Move(i + n + n, v));
                        minDeltaF = deltaF;
                    } else if (deltaF == minDeltaF) {
                        candidate.add(new Move(i + n + n, v));
                    }
                }
            }
            for (int v = 0; v < k; v++) {
                int deltaC = s.getAssignDelta(assigned[i], v);
                int deltaF = obj.getAssignDelta(assigned[i], v);
                if (deltaC > 0) continue;
                if (deltaF < 0) {
                    if (deltaF < minDeltaF) {
                        candidate.clear();
                        candidate.add(new Move(i + n + n + n, v));
                        minDeltaF = deltaF;
                    } else if (deltaF == minDeltaF) {
                        candidate.add(new Move(i + n + n + n, v));
                    }
                }
            }
        }
        for (int i = 0; i < k; i++) {
            for (int v = 0; v < 2; v++) {
                int deltaC = S.getAssignDelta(used[i], v);
                int deltaF = obj.getAssignDelta(used[i], v);
                if (deltaC > 0) continue;
                if (deltaF < 0) {
                    if (deltaF < minDeltaF) {
                        candidate.clear();
                        candidate.add(new Move(i + n + n + n + n, v));
                        minDeltaF = deltaF;
                    } else if (deltaF == minDeltaF) {
                        candidate.add(new Move(i + n + n + n + n, v));
                    }
                }
            }
        }
    }

    void exploreNeighborhoodFunction(VarIntLS[] x, VarIntLS[] y, VarIntLS[] r, VarIntLS[] assigned, VarIntLS[] used, IFunction F, List<Move> candidate) {
        int minDelta = Integer.MAX_VALUE;
        candidate.clear();
        for (int i = 0; i < n; i++) {
            for (int v = assigned[i].getMinValue(); v <= assigned[i].getMaxValue(); v++) {
                int delta = F.getAssignDelta(assigned[i], v);
                if (delta < 0) {
                    if (delta < minDelta) {
                        candidate.clear();
                        candidate.add(new Move(i + n + n + n, v));
                        minDelta = delta;
                    } else if (delta == minDelta) {
                        candidate.add(new Move(i + n + n + n, v));
                    }
                }
            }
            for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
                int delta = F.getAssignDelta(x[i], v);
                if (delta < 0) {
                    if (delta < minDelta) {
                        candidate.clear();
                        candidate.add(new Move(i, v));
                        minDelta = delta;
                    } else if (delta == minDelta) {
                        candidate.add(new Move(i, v));
                    }
                }
            }
            for (int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) {
                int delta = F.getAssignDelta(y[i], v);
                if (delta < 0) {
                    if (delta < minDelta) {
                        candidate.clear();
                        candidate.add(new Move(i + n, v));
                        minDelta = delta;
                    } else if (delta == minDelta) {
                        candidate.add(new Move(i + n, v));
                    }
                }
            }
            for (int v = r[i].getMinValue(); v <= r[i].getMaxValue(); v++) {
                int delta = F.getAssignDelta(r[i], v);
                if (delta < 0) {
                    if (delta < minDelta) {
                        candidate.clear();
                        candidate.add(new Move(i + n + n, v));
                        minDelta = delta;
                    } else if (delta == minDelta) {
                        candidate.add(new Move(i + n + n, v));
                    }
                }
            }
        }

        for (int i = 0; i < k; i++) {
            for (int v = 0; v <= 1; v++) {
                int delta = F.getAssignDelta(used[i], v);
                if (delta < 0) {
                    if (delta < minDelta) {
                        candidate.clear();
                        candidate.add(new Move(i + n + n + n + n, v));
                        minDelta = delta;
                    } else if (delta == minDelta) {
                        candidate.add(new Move(i + n + n + n + n, v));
                    }
                }
            }
        }
    }

    public void search1() {
        int it = 0;
        while (it < 1000) {
            exploreNeighborhoodFunction(x, y, r, assigned, used, F, candidate);
            if (candidate.size() == 0) {
                System.out.println("Local optimum");
                break;
            }
            move();
            System.out.println("Step " + it + ": violations = " + S.violations() + ", obj = " + obj.getValue());
            it++;
        }
        System.out.println("\nOBJ = " + obj.getValue());
    }

    public void search2() {
        int it = 0;
        while (it < 1000 && s.violations() > 0) {
            exploreNeighborhoodConstraint(x, y, r, assigned, used, s, candidate);
            if (candidate.size() == 0) {
                System.out.println("Local optimum");
                break;
            }
            move();
            System.out.println("Step " + it + ": violations = " + s.violations() +
                    ", obj = " + obj.getValue());
            it++;
        }

        System.out.println("Phase 2");
        it = 0;
        while (it < 100) {
            exploreNeighborhoodMaintainConstraint(x, y, r, assigned, used, s, obj, candidate);
            if (candidate.size() == 0) {
                System.out.println("Local optimum");
                break;
            }
            move();
            System.out.println("Step " + it + ": violations = " + s.violations() +
                    ", obj = " + obj.getValue());
            it++;
        }
    }

    public void move() {
        Move m = candidate.get(R.nextInt(candidate.size()));
        if (0 <= m.i && m.i < n - 1) x[m.i].setValuePropagate(m.v);
        else if (n <= m.i && m.i < 2 * n - 1) y[m.i - n].setValuePropagate(m.v);
        else if (2 * n <= m.i && m.i < 3 * n - 1) r[m.i - 2 * n].setValuePropagate(m.v);
        else if (3 * n <= m.i && m.i < 4 * n - 1) assigned[m.i - 3 * n].setValuePropagate(m.v);
        else if (4 * n <= m.i) used[m.i - 4 * n].setValuePropagate(m.v);
    }

    class Move {
        int i;
        int v;

        public Move(int i, int v) {
            this.i = i;
            this.v = v;
        }
    }
}
