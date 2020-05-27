package cbls115676khmt61.TranHuyHung_20164777.old;

import localsearch.model.IConstraint;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.constraints.basic.LessThan;
import localsearch.model.IFunction;
import localsearch.model.ConstraintSystem;
import localsearch.model.VarIntLS;
import localsearch.model.LocalSearchManager;

public class BACP
{
    int N;
    int P;
    int[] credits;
    int[][] pre;
    int a;
    int b;
    int c;
    int d;
    LocalSearchManager mgr;
    VarIntLS[] X;
    ConstraintSystem S;
    IFunction[] numberCoursesPeriod;
    IFunction[] numberCreditsPeriod;
    
    public BACP() {
        this.N = 12;
        this.P = 4;
        this.credits = new int[] { 2, 1, 2, 1, 3, 2, 1, 3, 2, 3, 1, 3 };
        this.pre = new int[][] { { 1, 0 }, { 5, 8 }, { 4, 5 }, { 4, 7 }, { 3, 10 }, { 5, 11 } };
        this.a = 3;
        this.b = 3;
        this.c = 5;
        this.d = 7;
    }
    
    public void stateModel() {
        this.mgr = new LocalSearchManager();
        this.X = new VarIntLS[this.N];
        for (int i = 0; i < this.N; ++i) {
            this.X[i] = new VarIntLS(this.mgr, 0, this.P - 1);
        }
        this.S = new ConstraintSystem(this.mgr);
        for (int k = 0; k < this.pre.length; ++k) {
            final IConstraint c = (IConstraint)new LessThan(this.X[this.pre[k][0]], this.X[this.pre[k][1]]);
            this.S.post(c);
        }
        this.numberCoursesPeriod = new IFunction[this.P];
        this.numberCreditsPeriod = new IFunction[this.P];
        for (int j = 0; j < this.P; ++j) {
            this.numberCoursesPeriod[j] = (IFunction)new ConditionalSum(this.X, j);
            this.numberCreditsPeriod[j] = (IFunction)new ConditionalSum(this.X, this.credits, j);
            this.S.post((IConstraint)new LessOrEqual(this.numberCoursesPeriod[j], this.b));
            this.S.post((IConstraint)new LessOrEqual(this.a, this.numberCoursesPeriod[j]));
            this.S.post((IConstraint)new LessOrEqual(this.numberCreditsPeriod[j], this.d));
            this.S.post((IConstraint)new LessOrEqual(this.c, this.numberCreditsPeriod[j]));
        }
        this.mgr.close();
    }
    
    public void search() {
        final MyTabuSearch ts = new MyTabuSearch((IConstraint)this.S);
        ts.search(100000, 20, 100);
    }
    
    public void printResult() {
        for (int j = 0; j < this.P; ++j) {
            System.out.print("HK " + j + ": ");
            for (int i = 0; i < this.N; ++i) {
                if (this.X[i].getValue() == j) {
                    System.out.print(String.valueOf(i) + ", ");
                }
            }
            System.out.print("number courses = " + this.numberCoursesPeriod[j].getValue());
            System.out.println(", number credits = " + this.numberCreditsPeriod[j].getValue());
        }
    }
    
    public static void main(final String[] args) {
        final BACP app = new BACP();
        app.stateModel();
        app.search();
        app.printResult();
    }
}
