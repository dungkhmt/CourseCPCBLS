package cbls115676khmt61.TranHuyHung_20164777.old;

import localsearch.constraints.basic.OR;
import localsearch.constraints.basic.AND;
import localsearch.model.IConstraint;
import localsearch.constraints.basic.Implicate;
import localsearch.model.IFunction;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.constraints.basic.IsEqual;
import localsearch.model.VarIntLS;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;

public class BinPacking2D
{
    int W;
    int H;
    int N;
    int[] w;
    int[] h;
    LocalSearchManager mgr;
    ConstraintSystem S;
    VarIntLS[] x;
    VarIntLS[] y;
    VarIntLS[] o;
    
    public BinPacking2D() {
        this.W = 4;
        this.H = 6;
        this.N = 3;
        this.w = new int[] { 3, 2, 1 };
        this.h = new int[] { 2, 4, 6 };
    }
    
    private void stateModel() {
        this.mgr = new LocalSearchManager();
        this.x = new VarIntLS[this.N];
        this.y = new VarIntLS[this.N];
        this.o = new VarIntLS[this.N];
        for (int i = 0; i < this.N; ++i) {
            this.x[i] = new VarIntLS(this.mgr, 0, this.W - 1);
            this.y[i] = new VarIntLS(this.mgr, 0, this.H - 1);
            this.o[i] = new VarIntLS(this.mgr, 0, 1);
        }
        this.S = new ConstraintSystem(this.mgr);
        for (int i = 0; i < this.N; ++i) {
            this.S.post((IConstraint)new Implicate((IConstraint)new IsEqual(this.o[i], 0), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.w[i]), this.W)));
            this.S.post((IConstraint)new Implicate((IConstraint)new IsEqual(this.o[i], 0), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.h[i]), this.H)));
            this.S.post((IConstraint)new Implicate((IConstraint)new IsEqual(this.o[i], 1), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.h[i]), this.W)));
            this.S.post((IConstraint)new Implicate((IConstraint)new IsEqual(this.o[i], 1), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.w[i]), this.H)));
        }
        for (int i = 0; i < this.N - 1; ++i) {
            for (int j = i + 1; j < this.N; ++j) {
                final IConstraint[] c1 = { (IConstraint)new IsEqual(this.o[i], 0), (IConstraint)new IsEqual(this.o[j], 0) };
                final IConstraint c2 = (IConstraint)new AND(c1);
                final IConstraint[] c3 = { (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.w[i]), this.x[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], this.w[j]), this.x[i]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.h[i]), this.y[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[j], this.h[j]), this.y[i]) };
                final IConstraint c4 = (IConstraint)new OR(c3);
                this.S.post((IConstraint)new Implicate(c2, c4));
            }
        }
        for (int i = 0; i < this.N - 1; ++i) {
            for (int j = i + 1; j < this.N; ++j) {
                final IConstraint[] c1 = { (IConstraint)new IsEqual(this.o[i], 0), (IConstraint)new IsEqual(this.o[j], 1) };
                final IConstraint c2 = (IConstraint)new AND(c1);
                final IConstraint[] c3 = { (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.w[i]), this.x[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], this.h[j]), this.x[i]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.h[i]), this.y[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[j], this.w[j]), this.y[i]) };
                final IConstraint c4 = (IConstraint)new OR(c3);
                this.S.post((IConstraint)new Implicate(c2, c4));
            }
        }
        for (int i = 0; i < this.N - 1; ++i) {
            for (int j = i + 1; j < this.N; ++j) {
                final IConstraint[] c1 = { (IConstraint)new IsEqual(this.o[i], 1), (IConstraint)new IsEqual(this.o[j], 0) };
                final IConstraint c2 = (IConstraint)new AND(c1);
                final IConstraint[] c3 = { (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.h[i]), this.x[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], this.w[j]), this.x[i]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.w[i]), this.y[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[j], this.h[j]), this.y[i]) };
                final IConstraint c4 = (IConstraint)new OR(c3);
                this.S.post((IConstraint)new Implicate(c2, c4));
            }
        }
        for (int i = 0; i < this.N - 1; ++i) {
            for (int j = i + 1; j < this.N; ++j) {
                final IConstraint[] c1 = { (IConstraint)new IsEqual(this.o[i], 1), (IConstraint)new IsEqual(this.o[j], 1) };
                final IConstraint c2 = (IConstraint)new AND(c1);
                final IConstraint[] c3 = { (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.h[i]), this.x[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], this.h[j]), this.x[i]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.w[i]), this.y[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[j], this.w[j]), this.y[i]) };
                final IConstraint c4 = (IConstraint)new OR(c3);
                this.S.post((IConstraint)new Implicate(c2, c4));
            }
        }
        this.mgr.close();
    }
    
    private void search() {
        final HillClimbingSearch searcher = new HillClimbingSearch();
        searcher.search((IConstraint)this.S, 10000);
    }
    
    private void print() {
        final char[][] p = new char[this.W][this.H];
        for (int i = 0; i < this.W; ++i) {
            for (int j = 0; j < this.H; ++j) {
                p[i][j] = '.';
            }
        }
        for (int i = 0; i < this.N; ++i) {
            if (this.o[i].getValue() == 0) {
                for (int j = this.x[i].getValue(); j < this.x[i].getValue() + this.w[i]; ++j) {
                    for (int k = this.y[i].getValue(); k < this.y[i].getValue() + this.h[i]; ++k) {
                        p[j][k] = (char)(i + 48);
                    }
                }
            }
            else {
                for (int j = this.x[i].getValue(); j < this.x[i].getValue() + this.h[i]; ++j) {
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
    
    public void solve() {
        this.stateModel();
        this.search();
        this.print();
    }
    
    public static void main(final String[] args) {
        final BinPacking2D app = new BinPacking2D();
        app.solve();
    }
}
