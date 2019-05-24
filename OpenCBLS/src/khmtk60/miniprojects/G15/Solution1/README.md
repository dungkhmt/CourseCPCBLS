# MultiKnapsack
Kết quả tốt nhất nằm trong thư mục dataset/best_solution.
  - Tập 100: Không xếp được.
  - Tập 1000: 768 items.
  - Tập 3000: 2931 items.
  - Compile bằng dòng lệnh: 
    - ./compile.sh
    - Chạy pha 1: java -cp ".:bin:library/gson-2.8.5.jar" src.SolutionPhase1
    - Chạy pha 2: java -cp ".:bin:library/gson-2.8.5.jar" src.SolutionPhase2
    - Check: java -cp ".:bin:library/gson-2.8.5.jar" src.SolutionCheckerSol1
  - Chạy bằng eclipse: Chạy các file trong Project OpenCBLS như ảnh trong thư mục images
    - Chạy pha 1: Chạy file SolutionPhase1.java
    - Chạy pha 2: Chạy file SolutionPhase2.java
    - Check: SolutionCheckerSol1.java
