package com.thuy.bachkhoa;

import java.util.ArrayList;
import java.util.Random;

public class MyQueen {

    private int N;
    private int x[];


    Random random = new Random(1);

    public MyQueen(int n) {
        this.N = n;
        this.x = new int[N];
    }

    // khởi tạo vị trị của các con hậu
    private void generateQueen() {
        for (int i = 0; i < N; i++) {
            x[i] = random.nextInt(N) + 1;
        }
    }

    //con hậu thứ i ăn được bao nhiêu con hậu khác
    private int loss(int i) {
        int l = 0;
        for (int j = 0; j < N; j++)
            if (i != j && (x[i] == x[j] || x[i] + i == x[j] + j || x[i] - i == x[j] - j))
                l++;
        return l;
    }

    // trả về mảng, phần tử thứ i là số con hậu mà con hậu thứ i ăn được
    private int[] loss() {
        int l[] = new int[N];

        for (int i = 0; i < N; i++)
            l[i] = 0;

        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                if (x[i] == x[j] || x[i] + i == x[j] + j || x[i] - i == x[j] - j) {
                    l[i]++;
                    l[j]++;
                }

            }
        }
        return l;
    }

    private int evaluate() {
        int l[] = loss();
        int sum = 0;
        for (int i = 0; i < N; i++) {
            sum += l[i];
        }
        return sum / 2;
    }

    public void localSearchGreedy() {
        generateQueen();
        // tham lam theo con
        int iter = 1_000_000; //số lần lặp tối đa tìm kiếm trong không gian
        int i = 0;

        // tại mỗi vòng lặp ta tham lam theo con hậu ăn được nhiều con hậu khác nhất
        while (i++ < iter) {
            int l[] = loss();

            // tính số cặp hâu đang ăn nhau
            int sum = 0;
            for (int j = 0; j < N; j++)
                sum += l[j];
            System.out.println("Step " + i + " loss :" + sum / 2);
            if (sum == 0) break;


            // tìm con hậu ăn nhiều con hậu khác nhất
            int n = 0, max = Integer.MIN_VALUE;
            for (int j = 0; j < N; j++)
                if (max < l[j]) {
                    max = l[j];
                    n = j;
                }


            // tham lam theo con hậu ăn nhiều con hậu khác nhất, tìm cho nó vị trí mà nó ăn được ít con hậu nhất
            int r = x[n], loss_min = l[n], temp;
            for (int j = 1; j <= N; j++) {
                x[n] = j;
                temp = loss(n);
                if (loss_min > temp) {
                    loss_min = temp;
                    r = j;
                }
            }
            x[n] = r;
        }

    }

    public void localSearchHill() {
        generateQueen();
        // tham lam theo con
        int iter = 1_000_000; //số lần lặp tối đa tìm kiếm trong không gian
        int loop = 0, e = evaluate(), tempE;

        while (loop++ < iter && e > 0) {
            for (int i = 0; i < N; i++) {

                e = evaluate();
                // tập lưu các lời giả tốt hơn hoặc bằng lời giải hiện tại
                ArrayList<Integer> candidates = new ArrayList<>();

                for (int j = 1; j <= N; j++) {
                    x[i] = j;
                    tempE = evaluate();
                    if (e >= tempE) {
                        candidates.add(j);
                    }
                }
                x[i] = candidates.get(random.nextInt(candidates.size()));
            }

            System.out.println("Step " + loop + " loss :" + evaluate());
        }
    }

    public static void main(String[] args) {
        MyQueen queen = new MyQueen(200);
        queen.localSearchHill();
    }

}
