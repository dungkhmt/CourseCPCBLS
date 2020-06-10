package planningoptimization115657k62.nguyenvanduc.mini_project_8.src;


import java.io.File;
import java.util.*;

import javax.sound.midi.Soundbank;


public class LocalS2 {
    public class KIP {
        //subjects - rooms
        ArrayList<Integer> subjects;
        ArrayList<Integer> rooms;
        HashSet<Integer> subjects_set;
        HashSet<Integer> rooms_set;

        public KIP() {
            subjects = new ArrayList<>();
            rooms = new ArrayList<>();
            subjects_set = new HashSet<>();
            rooms_set = new HashSet<>();
        }

        public void add_sub_room(int sub, int room) {
            subjects.add(sub);
            rooms.add(room);
            subjects_set.add(sub);
            rooms_set.add(room);
        }

        public void remove_idx(int idx) {
            subjects_set.remove(subjects.get(idx));
            rooms_set.remove(subjects.get(idx));
            subjects.remove(idx);
            rooms.remove(idx);

        }
    }

    int N; //so mon
    int M; // so phong
    int[] d; // d[i] so luong sinh vien dang ki mon i
    int[] c; // c[i] suc chua cua phong i
    int[][] conflict; //conflict[i][j] : mon i va j khong the cung kip
    int min_kip;

    KIP[] kips = null;
    ArrayList<Integer>[] L_rooms = null;
    // L_rooms[i] = ds cac phong co the thi cua mon i (c[j] >= d[i])

    long start_time;
    long time;


    public static void main(String[] args) {
        LocalS2 app = new LocalS2();
        app.input("src/planningoptimization115657k62/nguyenvanduc/mini_project_8/"
        		+ "data/1000_50_3000.txt");
//        app.generate_initSolution();
        
        app.solve(10000);
        System.out.println("done");
        
    }

    private void input(String file) {
        try {
            File f = new File(file);
            Scanner scanner = new Scanner(f);

            N = scanner.nextInt();
            M = scanner.nextInt();

            d = new int[N+1];
            for (int i = 1; i <= N; i++) {
                d[i] = scanner.nextInt();
            }

            c = new int[M+1];
            for (int i = 1; i <= M; i++) {
                c[i] = scanner.nextInt();
            }

            int Q = scanner.nextInt();
            conflict = new int[N+1][N+1];
            for (int i = 1; i <= N; i++) {
                for (int j = 1; j <= N; j++) {
                    conflict[i][j] = 0;
                }
            }
            for (int i = 0; i < Q; i++) {
                int t1 = scanner.nextInt();
                int t2 = scanner.nextInt();
                conflict[t1][t2] = 1;
                conflict[t2][t1] = 1;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void solve(int max_it) {
        min_kip = N;
        start_time = System.currentTimeMillis();
        int it = 0;
        while (it++ < max_it) {
            generate_initSolution();
            find_solution();
        }


    }


    //sinh loi giai ban dau thoa man rang buoc
    public void generate_initSolution() {
    	
    	kips = new KIP[N+1];
        L_rooms = new ArrayList[N+1];
        // L_rooms[i] = ds cac phong co the thi cua mon i (c[j] >= d[i])
        for (int i = 1; i <= N; i++) {
            L_rooms[i] = new ArrayList<>();
            for (int j = 1; j <= M; j++) {
                if (c[j] >= d[i]) {
                    L_rooms[i].add(j);
                }
            }
        }
        
//        print L_rooms
//        for (int i = 1; i <= N; i++) {
//        	System.out.print("Mon " + i + ": ");
//        	for (int j : L_rooms[i]) System.out.print(j + ", ");
//        	System.out.println();
//        	
//        }
        
        //random moi kip 1 mon, 1 phong
        Random R = new Random();
        ArrayList<Integer> subs = new ArrayList<>();
        for (int i = 1; i <= N; i++) subs.add(i);

        for (int i = 1; i <= N; i++) {
            kips[i] = new KIP();

            //random subject
            int idx = R.nextInt(subs.size());
            int ran_sub = subs.get(idx);
            subs.remove(idx);

            //random room
            int ran_room = L_rooms[ran_sub].get(R.nextInt(L_rooms[ran_sub].size()));

            kips[i].add_sub_room(ran_sub, ran_room);

        }
//        System.out.println("init ");
//        print_solution();


    }

    public void find_solution() {
        int new_min_kip = N;
        int k = N;
        boolean found = false;
        int cur = -1;
        while (k > cur) {
//            System.out.println("while (k > 1)");
            while(!kips[k].rooms.isEmpty()) {
//                System.out.println("while(!kips[k].rooms.isEmpty())");
                //tim kip, phong cho cac mon trong kip k
                int s = kips[k].subjects.get(0);
                int r = kips[k].rooms.get(0);
                found = false;
                //duyet cac kip tu k1 = 1 ->, neu kip k1 da co mon conflict voi mon s thi k1++

                for (int k1 = 1; k1 < k && !found; k1++) {

                    //kiem tra xem s co conflict voi mon nao trong kip k1 khong?
                    boolean conflict_k1 = false;
                    for (int i1 : kips[k1].subjects) {
                        if (conflict[s][i1] == 1) {
                            conflict_k1 = true;
                            break; //dung viec kiem tra
                        } //chuyen sang kip k1++
                    }

                    //neu khong conflict voi cac mon trong k1
                    if (!conflict_k1) {
                        //da tim duoc kip k1
                        //tim phong
                        // duyet cac phong co the cua mon s,
                        Collections.shuffle(L_rooms[s]);
                        for (int j1 : L_rooms[s]) {
                            //new phong j1 con trong trong kip k1
                            if (!kips[k1].rooms_set.contains(j1)) {
                                //da tim duoc phong cho mon s
                                kips[k1].add_sub_room(s, j1);
//                                System.out.println("add " + s + " - " + j1 + " to kip " + k1);
                                kips[k].remove_idx(0);
                                cur = k1;
                                found = true;
                                
                                break; //dung viec tim phong
                            }
                        }
                    }
                }
                if (!found) {
                    new_min_kip = k;
                    break;
                }
            }
            if (!found) break;
            k--;
        }

        if (new_min_kip < min_kip) {
            min_kip = new_min_kip;
            print_solution();
        }

    }

    public void print_solution() {
    	time = (System.currentTimeMillis() - start_time);
        System.out.println("------------------------------------------");
        System.out.println("time: " + time + " ms");
        System.out.println("solution: #kip = " + min_kip);
        System.out.println("Kip: mon - phong");
        for (int k = 1; k <= min_kip; k++) {
           
            System.out.print("Kip " + k + " : ");
            ArrayList<Integer> subjects_k = kips[k].subjects;
            ArrayList<Integer> rooms_k = kips[k].rooms;
            for (int i = 0; i < subjects_k.size(); i++) {
                System.out.print(subjects_k.get(i) + " - " + rooms_k.get(i) + ", ");
            }
            System.out.println();
        }
    }



}
