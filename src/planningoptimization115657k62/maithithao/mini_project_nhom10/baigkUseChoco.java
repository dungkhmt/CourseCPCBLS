

import java.io.File;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;


/*Mô hình hóa
 * N: lớp
 * t(i): tiết
 * g(i): giáo viên
 * M: phòng
 * c(i): sỗ chỗ ngồi trong phòng
 * p[i][u][k] 1, .. M, phòng lớp i học thứ u tiết k?? mô hình kiểu này thì có nghĩa là môn i học mọi tiết à?
 * a[i][u][k]=0, 1 khi lớp i học thứ u tiết k
 * a[i][u][k]=1 && a[j][u][k]=1 => p[i][u][k]!=p[j][u][k] //cùng thứ, cùng tiết => khác phòng
 * g[i]=g[j]=> a[i][u][k]=1 != a[j][u][k]  //cùng giáo viên => khác tiết
 * p[i][u][k]==j=> s[i]<=c[j]             // sức chứa
 * ====> sum(u=0->5,k=0->12)(a[i][u][k])=t[i] 
 * 
 * 
 * */

/*
 * x[i][u][k][j] = 1: lop i hoc thu u, tiet k, phong j
 * 1. 1 lop tai 1 tiet chi hoc o 0 hoac 1 phong: sum(x[i][u][k][0->M-1]) <= 1 voi moi i, u, k 
 * 2. i1, i2 cung thu, tiet -> khac phong: x[i1][u][k][j] + x[i2][u][k][j] <= 1 voi moi i1, i2, u, k, j (i1 < i2) 
 * 3. i1, i2 cung giao vien -> khac thu, khac tiet: for(i1) for (i2 > i1) if g[i1] = g[i2] => x[i1][u][k][j1] + x[i2][u][k][j2] <= 1 voi moi u, k, j1 < j2
 * 4. suc chua: for i, for j:  if s[i] >= c[j] : for u, for k:  x[i][u][k][j] = 0 
 * 5. for i: sum(for u, k, j theo x) = t[i]
 * 
 * */
public class baigkUseChoco{
	int U ;
	int K ;
    int N;//so lop hoc
    int M;// so phong hoc
    int[] s;// si so cac lop
    int[] g;// giao vien
    int[] c;// so cho ngoi cua cac phong
    int[] t;// tong so tiet cua cac lop trong tuan
    
 
    
    Model model = null;
    IntVar [][][][] x = null;
    
    
    public void build_model() {
    	input("data/gk.txt");
    	model = new Model();
    	
    	x = new IntVar[N][U][K][M];
    	for (int i = 0; i < N; i++) {
    		for (int u = 0; u < U; u++) {
    			for (int k = 0; k < K; k++) {
    				for (int j = 0; j < M; j++) {
    					//suc chua: for i, for j:  if s[i] >= c[j] : for u, for k:  x[i][u][k][j] = 0 
    					if (s[i] > c[j]) {
    						x[i][u][k][j] =  model.intVar(0, 0);
    					} else {
    						x[i][u][k][j] = model.intVar(0, 1);
    					}
    				}
    			}
    		}
    	}

	
    	//1.1 lop tai 1 tiet chi hoc o 0 hoac 1 phong: sum(x[i][u][k][0->M-1]) <= 1 voi moi i, u, k 
    	int[] oneM = new int[M];
    	for (int j = 0; j < M; j++) oneM[j] = 1;
    	
    	for (int i = 0; i < N; i++) {
    		for (int u = 0; u < U; u++) {
    			for (int k = 0; k < K; k++) {
    				model.scalar(x[i][u][k], oneM, "<=", 1).post();
    			}
    		}
    	}
    	
    	//2. i1, i2 cung thu, tiet -> khac phong: x[i1][u][k][j] + x[i2][u][k][j] <= 1 voi moi i1, i2, u, k, j (i1 < i2) 
    	for (int i1 = 0; i1 < N-1; i1++) {
    		for (int i2 = i1 + 1; i2 < N; i2++) {
    			for (int u = 0; u < U; u++) {
        			for (int k = 0; k < K; k++) {
        				for (int j = 0; j < M; j++) {
        					model.arithm(x[i1][u][k][j], "+", x[i2][u][k][j], "<=", 1).post();
        				}
        				
        			}
        		}
    		}
    	}
    	
    	//3. lop i1, i2 cung giao vien -> khac thu, khac tiet: 
    	//for(i1) for (i2 > i1) if g[i1] = g[i2] => x[i1][u][k][j1] + x[i2][u][k][j2] <= 1 voi moi u, k, j1 < j2
    	for (int i1 = 0; i1 < N-1; i1++) {
    		for (int i2 = i1 + 1; i2 < N; i2++) {
    			if  (g[i1] == g[i2]) {
    				for (int u = 0; u < U; u++) {
            			for (int k = 0; k < K; k++) {
            				for (int j1 = 0; j1 < M; j1++) {
            					for (int j2 = j1 + 1; j2 < M; j2++) {
            						model.arithm(x[i1][u][k][j1], "+", x[i2][u][k][j2], "<=", 1).post();
            					}
            					
            				}
            				
            			}
            		}
    			}
    			
    		}
    	}
    	
    	//5. tong cac tiet cua lop i trong tuan = t[i]----for i: sum(for u, k, j theo x) = t[i]
    	int[] oneZ = new int[U*K*M];
    	for (int i = 0; i < U*K*M; i++) oneZ[i] = 1;
    	
    	for (int i = 0; i < N; i++) {
    		IntVar[] tmp = new IntVar[U*K*M];
    		int idx = 0;
    		for (int u = 0; u < U; u++) {
    			for (int k = 0; k < K; k++) {
    				for (int j = 0; j < M; j++) {
    					tmp[idx] = x[i][u][k][j];
    					idx++;
    				}
    			}
    		}
    		model.scalar(tmp, oneZ, "=", t[i]).post();
    	}
    	
    	Solver solver = model.getSolver();
    	if (!solver.solve()) {
    		System.out.println("No solution!");
    	}
    	
    	//print solution:
    	System.out.println("lop: thu-tiet-phong");
    	for (int i = 0; i < N; i++) {
    		System.out.println();
    		System.out.print("lop " + (i+1) + " : ");
    		for (int u = 0; u < U; u++) {
    			for (int k = 0; k < K; k++) {
    				for (int j = 0; j < M; j++) {
    					if (x[i][u][k][j].getValue() == 1) {
    						System.out.print((u+2) + "-" + (k+1) + "-" + (j+1) + ", ");
    					}
    				}
    			}
    		}
    	}
    	
    	
    	
    }
    private void input(String file_name) {
        try {
            File f = new File(file_name);
            Scanner scanner = new Scanner(f);

            N = scanner.nextInt();
            M = scanner.nextInt();
            U = scanner.nextInt();
            K = scanner.nextInt();
            

            s = new int[N];
            g=new int[N];
            c=new int[M];
            t=new int[N];
            for (int i = 0; i < N; i++) {
                s[i] = scanner.nextInt();
            }
            for (int i = 0; i < N; i++) {
                g[i] = scanner.nextInt();
            }
            for (int i = 0; i < M; i++) {
                c[i] = scanner.nextInt();
            }
            for (int i = 0; i < N; i++) {
                t[i] = scanner.nextInt();
            }
            /*b = new int[U*K];
            for (int i = 0; i < U*K; i++) {
                b[i] = 1;
            }*/

            
            

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
         baigkUseChoco app = new baigkUseChoco();
         
         app.build_model();
	}

}
