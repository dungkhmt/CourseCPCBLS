package cbls115676khmt61.vudt_20164705;

import localsearch.model.*;
import localsearch.constraints.basic.*;
import localsearch.functions.basic.*;
import localsearch.selectors.MinMaxSelector;


public class Container {
    int L, W, N;
    int[] l;
    int[] w;
    LocalSearchManager mgr;
    ConstraintSystem constraints;
    VarIntLS[] x;
    VarIntLS[] y;
    VarIntLS[] direction;
    public Container() {
        w = new int[]{1, 3, 2, 3, 1, 2};
        l = new int[]{4, 1, 2, 1, 4, 3};
        W = 4;
        L = 6;
        N = 6;
        mgr = new LocalSearchManager();
        constraints = new ConstraintSystem(mgr);
        buildModel();
    }

    public void buildModel() {
        this.x = new VarIntLS[this.N];
        this.y = new VarIntLS[this.N];
        this.direction = new VarIntLS[this.N];
        for (int i = 0; i < this.N; ++i) {
            this.x[i] = new VarIntLS(this.mgr, 0, this.W - 1);
            this.y[i] = new VarIntLS(this.mgr, 0, this.L - 1);
            this.direction[i] = new VarIntLS(this.mgr, 0, 1);
        }
        this.constraints = new ConstraintSystem(this.mgr);
        for (int i = 0; i < this.N; ++i) {
            this.constraints.post((IConstraint)new Implicate(
                    (IConstraint)new IsEqual(this.direction[i], 0),
                    (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.w[i]), this.W)));
            this.constraints.post((IConstraint)new Implicate(
                    (IConstraint)new IsEqual(this.direction[i], 0),
                    (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.l[i]), this.L)));
            this.constraints.post((IConstraint)new Implicate(
                    (IConstraint)new IsEqual(this.direction[i], 1),
                    (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.l[i]), this.W)));
            this.constraints.post((IConstraint)new Implicate(
                    (IConstraint)new IsEqual(this.direction[i], 1),
                    (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.w[i]), this.L)));
        }
        for (int i = 0; i < this.N - 1; ++i) {
            for (int j = i + 1; j < this.N; ++j) {
                 IConstraint[] c1 = {
                        (IConstraint)new IsEqual(this.direction[i], 0),
                        (IConstraint)new IsEqual(this.direction[j], 0)
                };
                 IConstraint c2 = (IConstraint)new AND(c1);
                 IConstraint[] c3 = {
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.w[i]), this.x[j]),
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], this.w[j]), this.x[i]),
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.l[i]), this.y[j]),
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[j], this.l[j]), this.y[i])
                };
                 IConstraint c4 = (IConstraint)new OR(c3);
                this.constraints.post((IConstraint)new Implicate(c2, c4));

                 IConstraint[] c5 = {
                        (IConstraint)new LessThan(this.x[j], this.x[i]),
                        (IConstraint)new LessOrEqual(
                                (IFunction)new FuncPlus(this.x[i], -1),
                                (IFunction)new FuncPlus(this.x[j], this.w[j]))
                };
                 IConstraint c6 = (IConstraint) new AND(c5);
                 IConstraint[] c7 = {
                        (IConstraint)new LessThan(this.x[i], this.x[j]),
                        (IConstraint)new LessOrEqual(
                                (IFunction)new FuncPlus(this.x[j], -1),
                                (IFunction)new FuncPlus(this.x[i], this.w[i]))
                };
                 IConstraint c8 = (IConstraint) new AND(c7);
                 IConstraint c9 = (IConstraint) new OR(c6, c8);
                 IConstraint c10 = (IConstraint)new LessOrEqual(y[i], y[j]);
                 IConstraint c11 = (IConstraint) new AND(c2, c9);
                this.constraints.post((IConstraint)new Implicate(c11, c10));
            }
        }
        for (int i = 0; i < this.N - 1; ++i) {
            for (int j = i + 1; j < this.N; ++j) {
                 IConstraint[] c1 = {
                        (IConstraint)new IsEqual(this.direction[i], 0),
                        (IConstraint)new IsEqual(this.direction[j], 1)
                };
                 IConstraint c2 = (IConstraint)new AND(c1);
                 IConstraint[] c3 = {
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.w[i]), this.x[j]),
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], this.l[j]), this.x[i]),
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.l[i]), this.y[j]),
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[j], this.w[j]), this.y[i])
                };
                 IConstraint c4 = (IConstraint)new OR(c3);
                this.constraints.post((IConstraint)new Implicate(c2, c4));
            }
        }
        for (int i = 0; i < this.N - 1; ++i) {
            for (int j = i + 1; j < this.N; ++j) {
                 IConstraint[] c1 = {
                        (IConstraint)new IsEqual(this.direction[i], 1),
                        (IConstraint)new IsEqual(this.direction[j], 0)
                };
                 IConstraint c2 = (IConstraint)new AND(c1);
                 IConstraint[] c3 = {
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.l[i]), this.x[j]),
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], this.w[j]), this.x[i]),
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.w[i]), this.y[j]),
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[j], this.l[j]), this.y[i])
                };
                 IConstraint c4 = (IConstraint)new OR(c3);
                this.constraints.post((IConstraint)new Implicate(c2, c4));
            }
        }
        for (int i = 0; i < this.N - 1; ++i) {
            for (int j = i + 1; j < this.N; ++j) {
                 IConstraint[] c1 = {
                        (IConstraint)new IsEqual(this.direction[i], 1),
                        (IConstraint)new IsEqual(this.direction[j], 1)
                };
                 IConstraint c2 = (IConstraint)new AND(c1);
                 IConstraint[] c3 = {
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.l[i]), this.x[j]),
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], this.l[j]), this.x[i]),
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.w[i]), this.y[j]),
                        (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[j], this.w[j]), this.y[i])
                };
                 IConstraint c4 = (IConstraint)new OR(c3);
                this.constraints.post((IConstraint)new Implicate(c2, c4));
            }
        }

        this.mgr.close();

    }
    public void printSolution() {
         char[][] p = new char[this.W][this.L];
        for (int i = 0; i < this.W; ++i) {
            for (int j = 0; j < this.L; ++j) {
                p[i][j] = '.';
            }
        }
        for (int i = 0; i < this.N; ++i) {
            if (this.direction[i].getValue() == 0) {
                for (int j = this.x[i].getValue(); j < this.x[i].getValue() + this.w[i]; ++j) {
                    for (int k = this.y[i].getValue(); k < this.y[i].getValue() + this.l[i]; ++k) {
                        p[j][k] = (char)(i + 48);
                    }
                }
            }
            else {
                for (int j = this.x[i].getValue(); j < this.x[i].getValue() + this.l[i]; ++j) {
                    for (int k = this.y[i].getValue(); k < this.y[i].getValue() + this.w[i]; ++k) {
                        p[j][k] = (char)(i + 48);
                    }
                }
            }
        }
        for (int i = 0; i < this.W; ++i) {
            System.out.println(p[i]);
        }
    }

    public void search() {
        System.out.println("Init, constraints = " + constraints.violations());
        for (int it = 0; it < 10000 && constraints.violations() > 0; ++it) {
             MinMaxSelector mms = new MinMaxSelector((IConstraint)constraints);
             VarIntLS sel_x = mms.selectMostViolatingVariable();
             int sel_value = mms.selectMostPromissingValue(sel_x);
            sel_x.setValuePropagate(sel_value);
//            System.out.println("Step " + it + ", S = " + constraints.violations());
        }
        System.out.println("Final, constraints = " + constraints.violations());
    }

    public static void main(String[] args) {
        Container app = new Container();
        app.search();
        app.printSolution();

    }
}
