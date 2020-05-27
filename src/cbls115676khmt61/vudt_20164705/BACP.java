package cbls115676khmt61.vudt_20164705;

import cbls115676khmt61.vudt_20164705.search.HillClimbingSearch;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.HillClimbing;

class BACP {
  int nbOfSubs;
  int nbOfSemesters;
  int maxCoursesPerTerm;
  int maxCreditsPerTerm;
  int minCoursesPerTerm;
  int minCreditsPerTerm;
  int[] subWeights;
  int[][] preConds;

  LocalSearchManager mgr;
  ConstraintSystem constraints;
  VarIntLS[] subSemesters;
  IFunction[] nbOfCoursesTerms;
  IFunction[] nbOfCreditsTerms;


  public BACP(int nbOfSubs, int nbOfSemesters, 
                int minCoursesPerTerm, int maxCoursesPerTerm,
                int maxCreditsPerTerm, int minCreditsPerTerm,
                int[] subWeights,
                int[][] preConds) {
    this.nbOfSubs = nbOfSubs;
    this.nbOfSemesters = nbOfSemesters;
    this.maxCoursesPerTerm = maxCoursesPerTerm;
    this.maxCreditsPerTerm = maxCreditsPerTerm;
    this.minCoursesPerTerm = minCoursesPerTerm;
    this.minCreditsPerTerm = minCreditsPerTerm;
    this.subWeights = subWeights;
    this.preConds = preConds;
  }

  public void initModel() {
    mgr = new LocalSearchManager();
    subSemesters = new VarIntLS[nbOfSubs];
    for(int i = 0; i < nbOfSubs; i++) {
      subSemesters[i] = new VarIntLS(mgr, 0, nbOfSemesters - 1);
    }
    constraints = new ConstraintSystem(mgr);
    for(int i = 0; i < preConds.length; i++) {
      constraints.post(
        new LessThan(subSemesters[preConds[i][0]], subSemesters[preConds[i][1]])
        );
    }
    nbOfCoursesTerms = new IFunction[nbOfSemesters];
    nbOfCreditsTerms = new IFunction[nbOfSemesters];
    for(int i = 0; i < nbOfSemesters; i++) {
      nbOfCoursesTerms[i] = new ConditionalSum(subSemesters, i);
      nbOfCreditsTerms[i] = new ConditionalSum(subSemesters, subWeights, i);
      constraints.post(new LessOrEqual(nbOfCoursesTerms[i], maxCoursesPerTerm));
      constraints.post(new LessOrEqual(minCoursesPerTerm, nbOfCoursesTerms[i]));
      constraints.post(new LessOrEqual(nbOfCreditsTerms[i], maxCreditsPerTerm));
      constraints.post(new LessOrEqual(minCreditsPerTerm, nbOfCreditsTerms[i]));
    }
    mgr.close();
  }

  public void search() {
    int maxIter = 1000;
    HillClimbingSearch searcher = new HillClimbingSearch();
    searcher.search(constraints, maxIter, true);
    // HillClimbing searcher = new HillClimbing();
    // searcher.hillClimbing(constraints, maxIter);
  }

  public void printSolution() {
    System.out.println("Violations: " + constraints.violations());
    for(int j = 0; j < nbOfSemesters; j++){
      System.out.print("Semester " + j + ": ");
      for(int i = 0; i < nbOfSubs; i++)if(subSemesters[i].getValue() == j){
        System.out.print(i + ", ");
      }
      System.out.println("number courses = " + nbOfCoursesTerms[j].getValue()
          + ", number credits = " + nbOfCreditsTerms[j].getValue());
    }
  }

  public static void main(String[] args) {
    int nbOfSubs = 12;
    int nbOfSemesters = 4;
    int[] subWeights = {2, 1, 2, 1, 3, 2, 1, 3, 2, 3, 1, 3};
    int minCoursesPerTerm = 3;
    int maxCoursesPerTerm = 3;
    int minCreditsPerTerm = 5;
    int maxCreditsPerTerm = 7;
    int[][] preConds = {
      {1,0},
      {5,8},
      {4,5},
      {4,7},
      {3,10},
      {5,11}
    };
    BACP app = new BACP(nbOfSubs, nbOfSemesters,
      minCoursesPerTerm, maxCoursesPerTerm,
      maxCreditsPerTerm, minCreditsPerTerm,
      subWeights, preConds);
    app.initModel();
    app.search();
    app.printSolution();
  }
}