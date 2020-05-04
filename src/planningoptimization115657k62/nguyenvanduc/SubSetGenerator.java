package planningoptimization115657k62.nguyenvanduc;

import java.util.HashSet;

public class SubSetGenerator {
    int N;
    int[] x; //binary sequence
    public SubSetGenerator(int N) {
        this.N = N;
    }

    public HashSet<Integer> first() {
        x = new int[N];
        for (int i = 0; i < N; i++) {
            x[i] = 0;
        }

        HashSet<Integer> S = new HashSet<Integer>();
//        for (int i = 0; i < N; i++) {
//            if (x[i] == 1) S.add(i);
//        }

        return  S;
    }

    public HashSet<Integer> next() {
        int j = N-1;
        while (j >= 0 && x[j] == 1) {
            x[j] = 0;
            j--;
        }

        if (j >= 0) {
            x[j] = 1;
            HashSet<Integer> S = new HashSet<Integer>();
            for (int i = 0; i < N; i++) {
                if (x[i] == 1) S.add(i);
            }
            return S;
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        SubSetGenerator gen = new SubSetGenerator(3);
        HashSet<Integer> s = gen.first();
        while (s != null) {
            System.out.println();
            for (int i : s) {
                System.out.print(i + ", ");
            }
            s = gen.next();
        }

    }
}
