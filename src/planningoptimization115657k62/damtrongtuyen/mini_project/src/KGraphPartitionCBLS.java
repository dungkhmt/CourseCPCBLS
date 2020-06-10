package planningoptimization115657k62.damtrongtuyen.mini_project.src;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

import localsearch.constraints.basic.IsEqual;
import localsearch.functions.sum.Sum;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

public class KGraphPartitionCBLS {
    int N;
    int K;
    int[][] c;
    // todo: lower, upper bound
    int lowerBound, upperBound;
    int member[];
    int ALPHA = 2; // change alpha
//    int minDeltaF = 5;
    LocalSearchManager mgr;
    VarIntLS[] x;
    IConstraint S;
    IFunction f;
    Random R = new Random();

    public void loadData(String fn){
        try{
            Scanner in = new Scanner(new File(fn));
            N = in.nextInt();
            K = in.nextInt();

            c = new int[N][N];
            member = new int[K];
            for(int i = 0; i < N; i++){
                for(int j = 0; j < N; j++)
                    c[i][j] = in.nextInt();
            }
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void buildModel(){
        mgr = new LocalSearchManager();
        x = new VarIntLS[N];
        for(int i = 0; i < N; i++) x[i] = new VarIntLS(mgr,0,1);
        S = new IsEqual(new Sum(x), N/2); // constraint
        f = new KGraphPartitionCost(c, x, K); // objective function
        mgr.close();
    }
    public void generateInitSolutionBalance(){// todo: test hàm này đã

        // khởi tạo initMember đỉnh liên tiếp vào 1 cụm
        int initMember = (int) N/K;
        // todo: change 2 line below, not code like this
        lowerBound = initMember - ALPHA/2;
        upperBound = initMember + ALPHA/2;

        int remain = N - initMember*K;
        for (int i = 0; i < K; i++) {
            member[i] = initMember;
            for (int j = i*initMember; j < (i+1)*initMember; j++) {
                x[j].setValuePropagate(i);
            }
        }
        for (int i = 0; i < remain; i++) {
                member[i] += 1;
                x[i+initMember*K].setValuePropagate(i);
            }


//        for (int i = initMember*(K-1); i < N; i++) {
//            x[i].setValuePropagate(K-1);
//            member[K-1] += 1;
//        }

    }
    public String obj(){
        return "S = " + S.violations() + ", f = " + f.getValue();
    }


    class SwapMove{
        int i;
        int j;
        public SwapMove(int i, int j){
            this.i = i; this.j = j;
        }
    }

    class Move1 // chuyển đỉnh v sang cluster của đỉnh v
    {
        int u, v;
        public Move1(int a, int b)
        {
            this.u = a;
            this.v = b;
        }
    }

    class Assign1
    {
        // chuyển đỉnh v sang cluster k
        int v, k;
        public Assign1(int a, int b)
        {
            this.v = a;
            this.k = b;
        }
    }
    public void printGraph(){
        for(int i = 0;i < N; i++){
            System.out.print("A[" + i + "] = ");
            for(int j = 0;j < N; j++) if(c[i][j] > 0) System.out.print(j + "(" + c[i][j] + ") ");
            System.out.println();
        }
    }
    public void printCrossEdges(){
//        System.out.println("Partition member: \n");
//        for (int i = 0; i < K; i++) {
//            System.out.println("Partition "+i);
//            for (int j = 0; j < N; j++) {
//                if(x[j].getValue()==i)
//                {
//                    System.out.print(j+"\t");
//                }
//                System.out.println("====================================");
//            }
//        }

        for(int i = 0; i < N; i++)
            for(int j = 0; j < N; j++)
                if(i != j && x[i].getValue() != x[j].getValue() && c[i][j] > 0)
                    System.out.println("c[" + i + "," + j + "] = " + c[i][j]);
    }
    public void search1(int maxIter){// hill climbing
        Hashtable<Integer, Integer> dictValue = new Hashtable<Integer, Integer>();
        System.out.println("init obj = " + obj());
        //tim kiem cuc bo, duy tinh tinh thoa man rang buoc

        ArrayList<Assign1> assCand = new ArrayList<Assign1>();
        int it = 0;
        int cur = f.getValue();

        while(it < maxIter){
            assCand.clear();
            // explore neighborhood (kham pha, truy van lang gieng)
//            int minDeltaF = Integer.MAX_VALUE; // chấp nhận nước đi tồi hơn. set=0 nếu không muốn lây nước đi tồi hơn
            int minDeltaF = 5; //todo: thay đổi giá trị này, chấp nhận nước đi tồi hơn đến mức nào
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < K; j++) {
                    // check xem di chuyển node i sang cluster j, thỏa mãn điều kiện số đỉnh
                    if((x[i].getValue()!= j) && (member[x[i].getValue()]-1>=lowerBound) && (member[j]+1<=upperBound))
                    {
//                        member[x[i].getValue()] -= 1;
//                        member[j] += 1;
                        int d = f.getAssignDelta(x[i], j); // todo: delta tính là cost sau - trước=> ưu tiên âm lớn
                        if(d < minDeltaF)
                        {
                            minDeltaF = d;
                            assCand.clear();
                            assCand.add(new Assign1(i, j));
                        }
                        else if(d==minDeltaF)
                        {
                            assCand.add(new Assign1(i, j));
                        }
                    }
                }
            }

            if(assCand.size() == 0){
                System.out.println("Reach local optimum"); break;
            }
//            int abc = minDeltaF;
            Assign1 m = assCand.get(R.nextInt(assCand.size()));
            member[x[m.v].getValue()] -= 1;
            member[m.k] += 1;

//            System.out.println(f);
            x[m.v].setValuePropagate(m.k); // gán một giá trị mới
//            System.out.println(f);

            System.out.print("solution: ");
            for (int i = 0; i < N; i++) {
//                System.out.print("X["+i+"] = "+x[i].getValue()+"\t");
                System.out.print(x[i].getValue()+"\t");

            }

            System.out.println();
            if(minDeltaF + cur != f.getValue()){
                System.out.println("BUG???, cur = " + cur + ", delta = " + minDeltaF + ", f = " +
                        f.getValue() + ", x[" + m.v + "] <-> x[" + m.k + "]");
                break;
            }
            cur = f.getValue();
            System.out.println("Step " + it + ", obj = " + obj());
            int st = dictValue.getOrDefault(cur, -1);// xử lý trường hợp chạy loanh quanh
            if(st == -1)
            {
                dictValue.put(cur, 0);
            }
            dictValue.put(cur, dictValue.get(cur) + 1);
            if(st > 5) // sau 2*5 vòng lặp ko cải tiến => break luôn
            {
                System.out.println("### break because 5 iteration not decsreace objective function");
                break;
            }
            it++;
        }
    }
    class AssignMove{
        int i; int v;
        public AssignMove(int i, int v){
            this.i = i; this.v = v;
        }
    }
    class twoNeighbor{
        Assign1 move1;
        Assign1 move2;
        public twoNeighbor(Assign1 x, Assign1 y)
        {
            this.move1 = x;
            this.move2 = y;
        }
    }
    public void searchLNS(int max_iter, int n_neighbor)
    {
        Hashtable<Integer, Integer> dictValue = new Hashtable<Integer, Integer>();
        // đang thử với n_neighbor=2
        System.out.println("init obj = " + obj());

        ArrayList<twoNeighbor> pMove = new ArrayList<twoNeighbor>();

        int it = 0;
        int cur = f.getValue();
        while (it < max_iter)
        {
            pMove.clear();
            int minDeltaF = 10;
            // explore neighborhood
            for (int i = 0; i < N; i++) { // đỉnh i chuyển sang cụm j
                for (int j = 0; j < K; j++) {
                    for (int k = i+1; k < N; k++) {// đỉnh k chuyển sang cụm l
                        for (int l = 0; l < K; l++) {

                            if(i==j && k==l)
                                continue;
                            member[x[i].getValue()] -= 1;
                            member[j] += 1;
                            member[x[k].getValue()] -= 1;
                            member[l] += 1;
                            // check if this ia a possible move
                            boolean flag = true;
                            for (int m = 0; m < K; m++) {
                                if(member[m]<lowerBound || member[m]>upperBound)
                                {
                                    flag = false;
                                    break;
                                }
                            }
                            member[x[i].getValue()] += 1;
                            member[j] -= 1;
                            member[x[k].getValue()] += 1;
                            member[l] -= 1;

                            if(flag)
                            {
                                int d = f.getAssignDelta(x[i], j); // one by one
                                int tmp = x[i].getValue();
                                x[i].setValuePropagate(j); // setValue or + propagate?
                                d += f.getAssignDelta(x[k], l);
                                x[i].setValuePropagate(tmp);

                                if(d < minDeltaF)
                                {
                                    minDeltaF = d;
                                    pMove.clear();
                                    Assign1 m1 = new Assign1(i, j);
                                    Assign1 m2 = new Assign1(k, l);
                                    pMove.add(new twoNeighbor(m1, m2));
                                }
                                else if(d== minDeltaF)
                                {
                                    Assign1 m1 = new Assign1(i, j);
                                    Assign1 m2 = new Assign1(k, l);
                                    pMove.add(new twoNeighbor(m1, m2));
                                }
                            }

                        }// end for
                    }
                }
            }// explore neighborhood done!


            if(pMove.size() == 0)
            {
                System.out.println("Reach local optimum");
                break;
            }

            twoNeighbor tn = pMove.get(R.nextInt(pMove.size()));
            Assign1 m1 = tn.move1;
            Assign1 m2 = tn.move2;


//            System.out.println("vertice "+m1.v+" move from cluster "+x[m1.v].getValue()+" to cluster " + m1.k);
//            System.out.println("vertice "+m2.v+" move from cluster "+x[m2.v].getValue()+" to cluster " + m2.k);
            member[x[m1.v].getValue()] -= 1;//move1
            member[m1.k] += 1;
            x[m1.v].setValuePropagate(m1.k);

            member[x[m2.v].getValue()] -= 1;//move2
            member[m2.k] += 1;
            // for debug
//            int o = f.getValue();
//            int tmp = f.getAssignDelta(x[m2.v], m2.k);
            x[m2.v].setValuePropagate(m2.k);

            System.out.print("solution: ");
            for (int i = 0; i < N; i++) {
//                System.out.print("X["+i+"] = "+x[i].getValue()+"\t");
                System.out.print(x[i].getValue()+"\t");

            }
            System.out.println();
            if(minDeltaF + cur != f.getValue()){
                System.out.println("BUG???, cur = " + cur + ", delta = " + minDeltaF + ", f = " +
                            f.getValue() + ", x[" + m1.v + "] ->" + m1.k +
                             ", x[" + m2.v + "] ->" + m2.k);
                break;
            }
            cur = f.getValue();
            System.out.println("Step " + it + ", obj = " + obj());

            int st = dictValue.getOrDefault(cur, -1);// xử lý trường hợp chạy loanh quanh
            if(st == -1)
            {
                dictValue.put(cur, 0);
            }
            dictValue.put(cur, dictValue.get(cur) + 1);
            if(st > 5) // sau 2*5 vòng lặp ko cải tiến => break luôn
            {
                System.out.println("### break because 5 iteration not decsreace objective function");
                break;
            }
            it++;
        }



    }

    public void saveResult(String path)
    {
        File file = new File(path);
        try{
            PrintWriter out = new PrintWriter(path);
            out.print(N+" ");
            out.println(K);
            //
            for(int i = 0; i < N; i++){
                for(int j =0; j < N; j++)
                    out.print(c[i][j] + " ");
                out.println();
            }
            for (int i = 0; i < N; i++) {
                out.print(x[i].getValue()+" ");
            }
            out.println();

            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
//    public void search2(int maxIter){ // large neighborhood search
//        ArrayList<AssignMove> cand = new ArrayList<AssignMove>();
//        int it = 0;
//        // F(S.violations(), f)
//        while(it < maxIter){
//            cand.clear();
//            int minDeltaS = Integer.MAX_VALUE;
//            int minDeltaF = Integer.MAX_VALUE;
//            // explore neighborhood (kham pha lang gieng)
//            // lang gieng: chon 1 bien va gan gia tri moi
//            for(int i = 0; i < N; i++){
//                int v = 1 - x[i].getValue();
//                int deltaS = S.getAssignDelta(x[i], v);
//                int deltaF = f.getAssignDelta(x[i], v);
//                if(deltaS < 0 || deltaS == 0 && deltaF < 0){// hill climbing
//                    if(deltaS < minDeltaS || deltaS == minDeltaS && deltaF < minDeltaF){
//                        cand.clear();
//                        minDeltaS = deltaS; minDeltaF = deltaF;
//                        cand.add(new AssignMove(i,v));
//                    }else if(deltaS == minDeltaS && deltaF == minDeltaF){
//                        cand.add(new AssignMove(i,v));
//                    }
//                }
//            }
//
//            if(cand.size() == 0){
//                System.out.println("Reach  local optimum"); break;
//            }
//
//            AssignMove m = cand.get(R.nextInt(cand.size()));
//
//            x[m.i].setValuePropagate(m.v);
//
//            System.out.println("Step " + it + ", obj = " + obj());
//            it++;
//        }
//    }
//    public void runExpr(){
//        int nbRuns = 20;
//        int[] rs = new int[nbRuns];
//
//        for(int r = 0; r < nbRuns; r++){
//            buildModel();
//            search2(10000);
//            rs[r] = f.getValue();
//        }
//        for(int i = 0; i < nbRuns; i++){
//            System.out.println("Run " + i + ", rs = " + rs[i]);
//        }
//        int minf = Integer.MAX_VALUE;
//        int maxf = 0;
//        double avr = 0;
//        double std_dev = 0;
//        for(int i = 0; i < nbRuns; i++){
//            if(minf > rs[i]) minf = rs[i];
//            if(maxf < rs[i]) maxf = rs[i];
//            avr += rs[i];
//        }
//        avr = avr*1.0/nbRuns;
//        System.out.println("minf = " + minf + " maxf = " + maxf + ", avr = " + avr);
//    }
    public static void main(String[] args) {
        KGraphPartitionCBLS app = new KGraphPartitionCBLS();
        String GRAPH_FOLDER = "C:\\Users\\damtr\\IdeaProjects\\Test\\src\\graph_data\\";
        String fileName = "graph_150_15749.txt";

        String filePath = GRAPH_FOLDER + fileName;
        String fileResult = filePath.replace(".txt", "_result.txt");
        System.out.println(fileResult);
        //app.genData(fn, 6, 8);
        app.loadData(filePath);

        long start = new Date().getTime();
        app.buildModel();
        app.generateInitSolutionBalance();

        System.out.println("Starting search1");
        app.search1(20000);
        long end = new Date().getTime();
        System.out.println("search1 done after "+(end-start) + " ms" + "\n\n");

        System.out.println("Starting large neighborhood search:");
        app.searchLNS(100, 2);
        System.out.println("Large neighborhood search done after "+(new Date().getTime() - end) + " ms" + "\n\n");
        System.out.println("Total time: "+(new Date().getTime() - start) + " ms" + "\n\n");

        System.out.println(" nof member of each cluster:"+ "( " + app.K+" cluster)");
        for (int i = 0; i < app.K; i++) {
            System.out.print(app.member[i]+"\t");
        }
        System.out.println("\n solution: ");
        for (int i = 0; i < app.K; i++) {
            System.out.println("Member of cluster "+ i + ": ");
            System.out.print("\t");
            for (int j = 0; j < app.N; j++) {
                if(app.x[j].getValue() == i)
                {
                    System.out.print(j + ", ");
                }
            }
            System.out.println();
        }

        System.out.println("\n Optimal value:"+app.f.getValue());

        // save result:
//        app.saveResult(fileResult);
    }

}
