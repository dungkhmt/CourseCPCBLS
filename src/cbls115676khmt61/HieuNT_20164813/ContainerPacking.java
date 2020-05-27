package cbls115676khmt61.HieuNT_20164813;

import cbls115676khmt61.HieuNT_20164813.HillClimbingSearch;
import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ContainerPacking {
	int W;
  int L;
  int N;
  int[] w;
  int[] l;
  LocalSearchManager mgr;
  ConstraintSystem S;
  VarIntLS[] x;
  VarIntLS[] y;
  VarIntLS[] o;
  
  public ContainerPacking() {
      this.W = 4;
      this.L = 6;
      this.N = 6;
      this.w = new int[] { 1, 3, 2, 3, 1, 2 };
      this.l = new int[] { 4, 1, 2, 1, 4, 3 };
  }
  
  private void stateModel() {
      this.mgr = new LocalSearchManager();
      this.x = new VarIntLS[this.N];
      this.y = new VarIntLS[this.N];
      this.o = new VarIntLS[this.N];
      for (int i = 0; i < this.N; ++i) {
          this.x[i] = new VarIntLS(this.mgr, 0, this.W - 1);
          this.y[i] = new VarIntLS(this.mgr, 0, this.L - 1);
          this.o[i] = new VarIntLS(this.mgr, 0, 1);
      }
      this.S = new ConstraintSystem(this.mgr);
      for (int i = 0; i < this.N; ++i) {
          this.S.post((IConstraint)new Implicate((IConstraint)new IsEqual(this.o[i], 0), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.w[i]), this.W)));
          this.S.post((IConstraint)new Implicate((IConstraint)new IsEqual(this.o[i], 0), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.l[i]), this.L)));
          this.S.post((IConstraint)new Implicate((IConstraint)new IsEqual(this.o[i], 1), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.l[i]), this.W)));
          this.S.post((IConstraint)new Implicate((IConstraint)new IsEqual(this.o[i], 1), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.w[i]), this.L)));
      }
      
      for (int i = 0; i < this.N - 1; ++i) {
          for (int j = i + 1; j < this.N; ++j) {
              final IConstraint[] c1 = { (IConstraint)new IsEqual(this.o[i], 0), (IConstraint)new IsEqual(this.o[j], 0) };
              final IConstraint c2 = (IConstraint)new AND(c1);
              final IConstraint[] c3 = { (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.w[i]), this.x[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], this.w[j]), this.x[i]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.l[i]), this.y[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[j], this.l[j]), this.y[i]) };
              final IConstraint c4 = (IConstraint)new OR(c3);
              this.S.post((IConstraint)new Implicate(c2, c4));
              
              final IConstraint[] c5 = { (IConstraint)new LessThan(this.x[j], this.x[i]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], -1), (IFunction)new FuncPlus(this.x[j], this.w[j])) };
              final IConstraint c6 = (IConstraint) new AND(c5);
              final IConstraint[] c7 = { (IConstraint)new LessThan(this.x[i], this.x[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], -1), (IFunction)new FuncPlus(this.x[i], this.w[i])) };
              final IConstraint c8 = (IConstraint) new AND(c7);
              final IConstraint c9 = (IConstraint) new OR(c6, c8); 
              final IConstraint c10 = (IConstraint)new LessOrEqual(this.y[i], this.y[j]);
              final IConstraint c11 = (IConstraint) new AND(c2, c9);
              this.S.post((IConstraint)new Implicate(c11, c10));
          }
      }
      
      for (int i = 0; i < this.N - 1; ++i) {
          for (int j = i + 1; j < this.N; ++j) {
              final IConstraint[] c1 = { (IConstraint)new IsEqual(this.o[i], 0), (IConstraint)new IsEqual(this.o[j], 1) };
              final IConstraint c2 = (IConstraint)new AND(c1);
              final IConstraint[] c3 = { (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.w[i]), this.x[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], this.l[j]), this.x[i]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.l[i]), this.y[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[j], this.w[j]), this.y[i]) };
              final IConstraint c4 = (IConstraint)new OR(c3);
              this.S.post((IConstraint)new Implicate(c2, c4));
              
              final IConstraint[] c5 = { (IConstraint)new LessThan(this.x[j], this.x[i]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], -1), (IFunction)new FuncPlus(this.x[j], this.l[j])) };
              final IConstraint c6 = (IConstraint) new AND(c5);
              final IConstraint[] c7 = { (IConstraint)new LessThan(this.x[i], this.x[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], -1), (IFunction)new FuncPlus(this.x[i], this.w[i])) };
              final IConstraint c8 = (IConstraint) new AND(c7);
              final IConstraint c9 = (IConstraint) new OR(c6, c8); 
              final IConstraint c10 = (IConstraint)new LessOrEqual(this.y[i], this.y[j]);
              final IConstraint c11 = (IConstraint) new AND(c2, c9);
              this.S.post((IConstraint)new Implicate(c11, c10));
          }
      }
      
      for (int i = 0; i < this.N - 1; ++i) {
          for (int j = i + 1; j < this.N; ++j) {
              final IConstraint[] c1 = { (IConstraint)new IsEqual(this.o[i], 1), (IConstraint)new IsEqual(this.o[j], 0) };
              final IConstraint c2 = (IConstraint)new AND(c1);
              final IConstraint[] c3 = { (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.l[i]), this.x[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], this.w[j]), this.x[i]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.w[i]), this.y[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[j], this.l[j]), this.y[i]) };
              final IConstraint c4 = (IConstraint)new OR(c3);
              this.S.post((IConstraint)new Implicate(c2, c4));
              
              final IConstraint[] c5 = { (IConstraint)new LessThan(this.x[j], this.x[i]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], -1), (IFunction)new FuncPlus(this.x[j], this.w[j])) };
              final IConstraint c6 = (IConstraint) new AND(c5);
              final IConstraint[] c7 = { (IConstraint)new LessThan(this.x[i], this.x[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], -1), (IFunction)new FuncPlus(this.x[i], this.l[i])) };
              final IConstraint c8 = (IConstraint) new AND(c7);
              final IConstraint c9 = (IConstraint) new OR(c6, c8); 
              final IConstraint c10 = (IConstraint)new LessOrEqual(this.y[i], this.y[j]);
              final IConstraint c11 = (IConstraint) new AND(c2, c9);
              this.S.post((IConstraint)new Implicate(c11, c10));
          }
      }
      
      for (int i = 0; i < this.N - 1; ++i) {
          for (int j = i + 1; j < this.N; ++j) {
              final IConstraint[] c1 = { (IConstraint)new IsEqual(this.o[i], 1), (IConstraint)new IsEqual(this.o[j], 1) };
              final IConstraint c2 = (IConstraint)new AND(c1);
              final IConstraint[] c3 = { (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], this.l[i]), this.x[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], this.l[j]), this.x[i]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[i], this.w[i]), this.y[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.y[j], this.w[j]), this.y[i]) };
              final IConstraint c4 = (IConstraint)new OR(c3);
              this.S.post((IConstraint)new Implicate(c2, c4));
              
              final IConstraint[] c5 = { (IConstraint)new LessThan(this.x[j], this.x[i]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[i], -1), (IFunction)new FuncPlus(this.x[j], this.l[j])) };
              final IConstraint c6 = (IConstraint) new AND(c5);
              final IConstraint[] c7 = { (IConstraint)new LessThan(this.x[i], this.x[j]), (IConstraint)new LessOrEqual((IFunction)new FuncPlus(this.x[j], -1), (IFunction)new FuncPlus(this.x[i], this.l[i])) };
              final IConstraint c8 = (IConstraint) new AND(c7);
              final IConstraint c9 = (IConstraint) new OR(c6, c8); 
              final IConstraint c10 = (IConstraint)new LessOrEqual(this.y[i], this.y[j]);
              final IConstraint c11 = (IConstraint) new AND(c2, c9);
              this.S.post((IConstraint)new Implicate(c11, c10));
          }
      }
      
      
      this.mgr.close();
  }
  
  private void search() {
      final HillClimbingSearch searcher = new HillClimbingSearch();
      searcher.search((IConstraint)this.S, 10000);
  }
  
  private void print() {
      final char[][] p = new char[this.W][this.L];
      for (int i = 0; i < this.W; ++i) {
          for (int j = 0; j < this.L; ++j) {
              p[i][j] = '.';
          }
      }
      for (int i = 0; i < this.N; ++i) {
          if (this.o[i].getValue() == 0) {
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
  
  public void solve() {
      this.stateModel();
      this.search();
      this.print();
  }
  
  public static void main(final String[] args) {
      final ContainerPacking app = new ContainerPacking();
      app.solve();
  }
}
