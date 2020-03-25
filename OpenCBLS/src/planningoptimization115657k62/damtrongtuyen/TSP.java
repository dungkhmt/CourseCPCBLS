import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import java.sql.SQLOutput;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
public class TSP {
    static{
        System.loadLibrary("jniortools");
    }
    int N = 5;
    int [][] c = {{0,4,2,5,6},
            {2,0,5,2,7},
            {1,2,0,6,3},
            {7,5,8,0,3},
            {1,2,4,3,0}};
    double inf = Double.POSITIVE_INFINITY;
    MPSolver solver;
    MPVariable[][] x;
    public void solve()
    {
//        if(N > 10)
//        {
//            System.out.println("N = 10 is too high, please use " +
//                    "solveDynamicAddSubTourConstraint instead");
//            return;
//        }
        solver = new MPSolver("TSP solver", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
        x = new MPVariable[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(i != j)
                {
                    x[i][j] = solver.makeIntVar(0, 1, "x"+i+","+j+"]");
                }
            }
        }
        MPObjective objective = solver.objective();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                objective.setCoefficient(x[i][j], c[i][j]);
            }
        }
        for (int i = 0; i < N; i++) {
            MPConstraint c1 = solver.makeConstraint(1, 1);
            for (int j = 0; j < N; j++) {
                if(i != j)
                {
                    c1.setCoefficient(x[i][j], 1);
                }
            }
            MPConstraint c2 = solver.makeConstraint(1, 1);
            for (int j = 0; j < N; j++) {
                c2.setCoefficient(x[j][i], 1);
            }
        }
//        SubSetGenerator generator = new SubSetGenerator(N);
//        HashSet<Integer> S = generator.first();
//        while(S!= null)
//        {
//            if(S.size() > 1 && S.size() < N) // don't care about = 1 and =n
//            {
//                MPConstraint sec = solver.makeConstraint(0, S.size()-1);
//                for(int i: S)
//                {
//                    for(int j: S)
//                    {
//                        if(i != j)
//                        {
//                            sec.setCoefficient(x[i][j], 1);
//                        }
//                    }
//                }
//            }
//            S = generator.next();
//        }
        int count = 1;
        boolean loop = true;
        while(loop)
        {
            System.out.println("-----------------------------------------------");
            System.out.println("loop: "+count);
            count++;
            final  MPSolver.ResultStatus resultStatus = solver.solve();
            if(resultStatus != MPSolver.ResultStatus.OPTIMAL)
            {
                System.out.println("do not find  optimal solution");
                return ;
            }
//            System.out.println("time for this computation: " + solver.wallTime()+" miliseconds");
            System.out.println("optimal value: "+ solver.objective().value());

            int result[][] = new int[N][N];
            for (int i = 0; i < this.N; i++) {
                for (int j = 0; j < this.N; j++) {
                    if(i!=j)
                    {
                        result[i][j] = (int) x[i][j].solutionValue();
//                        System.out.print(result[i][j] + "\t");
                    }
                    else
                    {
                        result[i][j] = 0;
//                        System.out.print("null");
                    }
                }
//                System.out.println();
            }

            HashSet<LinkedList<Integer>> subTour = this.checKSubtour(result);
            System.out.println("number of subTour: "+subTour.size());
//            System.out.println("print subtour of this solution");
            if(subTour.size()==1)
            {
                System.out.println("done!");
                System.out.println("tour: "+subTour.toArray()[0]);
                loop = false;
                return;
            }
            for(LinkedList<Integer> list:subTour)
            {
                System.out.println(list);
                MPConstraint sec = solver.makeConstraint(0, list.size()-1);
                for(int k: list)
                {
                    for(int f: list)
                    {
                                    sec.setCoefficient(x[k][f], 1);
                    }
//                    System.out.println(f);
                }
            }
        } //end while


    }
    public int[][] genTestData(int N)
    {
        int[][] result = new int[N][N];
        Random rand = new Random();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(i == j)
                {
                    result[i][j] = 0;
                }
                else {
                    result[i][j] = rand.nextInt(1000);
                }
            }
        }
        return result;
    }
    public void setC(int N)
    {
        this.N = N;
        this.c = genTestData(N);
//        System.out.println("c size: " + this.c.length);
//        for (int i = 0; i < this.c.length; i++) {
//            for (int j = 0; j < this.c.length; j++) {
//                System.out.print(this.c[i][j]+ "  ");
//            }
//            System.out.println();
//        }
    }

    public HashSet <LinkedList<Integer>> checKSubtour(int[][] result)
    {
        HashSet<LinkedList<Integer>> subTour = new HashSet<LinkedList<Integer>>();
        int length = result.length;
        int start = 0;
//        for (int i = 0; i < length; i++) {
//            if(result[0][i] == 1)
//            {
//                start = i;
//            }
//        }
        LinkedList<Integer> tour = new LinkedList<Integer>();
//        tour.add(Integer.valueOf(start));
//        boolean loop = true;
        boolean checked[] = new boolean[length];
        for (int i = 0; i < length; i++) {
            checked[i] = false;
        }
//        checked[0] = true;
//        checked[start] = true;
        for (int i = 0; i < length; i++) {
            if(!checked[i])
            {
                checked[i] = true;
                tour.add(i);
                start = i;
                boolean loop = true;
                while (loop)
                {
                    for (int j = 0; j < length; j++) {
                        if(result[start][j]==1)
                        {
                            if(checked[j]) // come back to start point
                            {
//                                System.out.println("a tour: ");
//                                for(int t:tour)
//                                {
//                                    System.out.print(t+"\t");
//                                }
//                                System.out.println();
                                LinkedList<Integer> copy;
                                copy = (LinkedList) tour.clone();
                                subTour.add(copy);
                                tour.clear();
                                loop = false;
                            }
                            else
                            {
                                checked[j] = true;
                                start = j;
                                tour.add(j);
                            }
                            break;

                        }
                    }
                }
                // end while
            }
        }
        // end for
        return subTour;

    }
    public static void main(String[] args) {
        TSP app = new TSP();
        app.setC(30);
        app.solve();
    }
}



