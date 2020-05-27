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
        buildModel();
    }

    public void buildModel() {
        mgr = new LocalSearchManager();
        constraints = new ConstraintSystem(mgr);
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
            this.constraints.post(new Implicate(
                    new IsEqual(this.direction[i], 0),
                    new LessOrEqual(new FuncPlus(this.x[i], this.w[i]), this.W)));
            this.constraints.post(new Implicate(
                    new IsEqual(this.direction[i], 0),
                    new LessOrEqual(new FuncPlus(this.y[i], this.l[i]), this.L)));
            this.constraints.post(new Implicate(
                    new IsEqual(this.direction[i], 1),
                    new LessOrEqual(new FuncPlus(this.x[i], this.l[i]), this.W)));
            this.constraints.post(new Implicate(
                    new IsEqual(this.direction[i], 1),
                    new LessOrEqual(new FuncPlus(this.y[i], this.w[i]), this.L)));
        }
        for (int i = 0; i < this.N - 1; ++i) {
            for (int j = i + 1; j < this.N; ++j) {
                 IConstraint[] c1 = {
                        new IsEqual(this.direction[i], 0),
                        new IsEqual(this.direction[j], 0)
                };
                 IConstraint c2 = new AND(c1);
                 IConstraint[] c3 = {
                        new LessOrEqual(new FuncPlus(this.x[i], this.w[i]), this.x[j]),
                        new LessOrEqual(new FuncPlus(this.x[j], this.w[j]), this.x[i]),
                        new LessOrEqual(new FuncPlus(this.y[i], this.l[i]), this.y[j]),
                        new LessOrEqual(new FuncPlus(this.y[j], this.l[j]), this.y[i])
                };
                 IConstraint c4 = new OR(c3);
                this.constraints.post(new Implicate(c2, c4));

                 IConstraint[] c5 = {
                        new LessThan(this.x[j], this.x[i]),
                        new LessOrEqual(
                                new FuncPlus(this.x[i], -1),
                                new FuncPlus(this.x[j], this.w[j]))
                };
                 IConstraint c6 =  new AND(c5);
                 IConstraint[] c7 = {
                        new LessThan(this.x[i], this.x[j]),
                        new LessOrEqual(
                                new FuncPlus(this.x[j], -1),
                                new FuncPlus(this.x[i], this.w[i]))
                };
                 IConstraint c8 =  new AND(c7);
                 IConstraint c9 =  new OR(c6, c8);
                 IConstraint c10 = new LessOrEqual(y[i], y[j]);
                 IConstraint c11 =  new AND(c2, c9);
                this.constraints.post(new Implicate(c11, c10));
            }
        }
        for (int i = 0; i < this.N - 1; ++i) {
            for (int j = i + 1; j < this.N; ++j) {
                 IConstraint[] c1 = {
                        new IsEqual(this.direction[i], 0),
                        new IsEqual(this.direction[j], 1)
                };
                 IConstraint c2 = new AND(c1);
                 IConstraint[] c3 = {
                        new LessOrEqual(new FuncPlus(this.x[i], this.w[i]), this.x[j]),
                        new LessOrEqual(new FuncPlus(this.x[j], this.l[j]), this.x[i]),
                        new LessOrEqual(new FuncPlus(this.y[i], this.l[i]), this.y[j]),
                        new LessOrEqual(new FuncPlus(this.y[j], this.w[j]), this.y[i])
                };
                IConstraint c4 = new OR(c3);
                this.constraints.post(new Implicate(c2, c4));
                IConstraint[] c5 = {
                        new LessThan(this.x[j], this.x[i]),
                        new LessOrEqual(new FuncPlus(this.x[i], -1),
                                new FuncPlus(this.x[j], this.l[j]))
                };
                IConstraint c6 =  new AND(c5);
                IConstraint[] c7 = {
                        new LessThan(this.x[i], this.x[j]),
                        new LessOrEqual(
                                new FuncPlus(this.x[j], -1),
                                new FuncPlus(this.x[i], this.w[i]))
                };
                IConstraint c8 =  new AND(c7);
                IConstraint c9 =  new OR(c6, c8);
                IConstraint c10 = new LessOrEqual(y[i], y[j]);
                IConstraint c11 =  new AND(c2, c9);
                constraints.post(new Implicate(c11, c10));
            }
        }
        for (int i = 0; i < this.N - 1; ++i) {
            for (int j = i + 1; j < this.N; ++j) {
                 IConstraint[] c1 = {
                        new IsEqual(this.direction[i], 1),
                        new IsEqual(this.direction[j], 0)
                };
                 IConstraint c2 = new AND(c1);
                 IConstraint[] c3 = {
                        new LessOrEqual(new FuncPlus(this.x[i], this.l[i]), this.x[j]),
                        new LessOrEqual(new FuncPlus(this.x[j], this.w[j]), this.x[i]),
                        new LessOrEqual(new FuncPlus(this.y[i], this.w[i]), this.y[j]),
                        new LessOrEqual(new FuncPlus(this.y[j], this.l[j]), this.y[i])
                };
                IConstraint c4 = new OR(c3);
                constraints.post(new Implicate(c2, c4));
                IConstraint[] c5 = {
                        new LessThan(this.x[j], this.x[i]),
                        new LessOrEqual(
                                new FuncPlus(this.x[i], -1),
                                new FuncPlus(this.x[j], this.l[j]))
                };
                IConstraint c6 =  new AND(c5);
                IConstraint[] c7 = {
                        new LessThan(this.x[i], this.x[j]),
                        new LessOrEqual(
                                new FuncPlus(this.x[j], -1) ,
                                new FuncPlus(this.x[i], this.w[i]))
                };
                IConstraint c8 =  new AND(c7);
                IConstraint c9 =  new OR(c6, c8);
                IConstraint c10 = new LessOrEqual(y[i], y[j]);
                IConstraint c11 =  new AND(c2, c9);
                constraints.post(new Implicate(c11, c10));
            }
        }
        for (int i = 0; i < this.N - 1; ++i) {
            for (int j = i + 1; j < this.N; ++j) {
                IConstraint[] c1 = {
                         new IsEqual(this.direction[i], 1),
                         new IsEqual(this.direction[j], 1)
                };
                IConstraint c2 = new AND(c1);
                IConstraint[] c3 = {
                       new LessOrEqual(new FuncPlus(this.x[i], this.l[i]), this.x[j]),
                       new LessOrEqual(new FuncPlus(this.x[j], this.l[j]), this.x[i]),
                       new LessOrEqual(new FuncPlus(this.y[i], this.w[i]), this.y[j]),
                       new LessOrEqual(new FuncPlus(this.y[j], this.w[j]), this.y[i])
                };
                IConstraint c4 = new OR(c3);
                this.constraints.post(new Implicate(c2, c4));
                IConstraint[] c5 = {
                        new LessThan(this.x[j], this.x[i]),
                        new LessOrEqual(this.x[i],
                                new FuncPlus(this.x[j], this.l[j])) };
                IConstraint c6 =  new AND(c5);
                IConstraint[] c7 = {
                        new LessThan(this.x[i], this.x[j]),
                        new LessOrEqual(this.x[j],
                                new FuncPlus(this.x[i], this.l[i]))
                };
                IConstraint c8 =  new AND(c7);
                IConstraint c9 =  new OR(c6, c8);
                IConstraint c10 = new LessOrEqual(y[i], y[j]);
                IConstraint c11 =  new AND(c2, c9);
//                constraints.post(new Implicate(c11, c10));
            }
        }

        this.mgr.close();

    }
    public void printSolution() {
         char[][] map = new char[this.W][this.L];
        for (int i = 0; i < this.W; ++i) {
            for (int j = 0; j < this.L; ++j) {
                map[i][j] = '.';
            }
        }
        for (int i = 0; i < this.N; ++i) {
            if (this.direction[i].getValue() == 0) {
                for (int j = this.x[i].getValue(); j < this.x[i].getValue() + this.w[i] && j < W; ++j) {
                    for (int k = this.y[i].getValue(); k < this.y[i].getValue() + this.l[i] && k < L; ++k) {
                        map[j][k] = (char)(i + 48);
                    }
                }
            }
            else {
                for (int j = this.x[i].getValue(); j < this.x[i].getValue() + this.l[i] && j < W; ++j) {
                    for (int k = this.y[i].getValue(); k < this.y[i].getValue() + this.w[i] && k < L; ++k) {
                        map[j][k] = (char)(i + 48);
                    }
                }
            }
        }
        for (int i = 0; i < this.W; ++i) {
            System.out.println(map[i]);
        }
    }

    public void search() {
        System.out.println("Init, constraints = " + constraints.violations());
        for (int it = 0; it < 10000 && constraints.violations() > 0; ++it) {
             MinMaxSelector mms = new MinMaxSelector(constraints);
             VarIntLS sel_x = mms.selectMostViolatingVariable();
             int sel_value = mms.selectMostPromissingValue(sel_x);
            sel_x.setValuePropagate(sel_value);
//            System.out.println("Step " + it + ", S = " + constraints.violations());
        }
        System.out.println("Final, constraints = " + constraints.violations());
    }

    public static void main(String[] args) {
        Container app = new Container();
        for(int i=0; i < 20 && app.constraints.violations() > 0; i++) {
            app.buildModel();
            app.search();
        }
        app.printSolution();

    }
}
