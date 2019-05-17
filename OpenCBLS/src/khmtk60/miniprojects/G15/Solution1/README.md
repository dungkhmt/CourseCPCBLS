# MultiKnapsack
Kết quả tốt nhất nằm trong thư mục dataset/submit.
  - Tập 100: Không xếp được.
  - Tập 1000: 768 items.
  - Tập 3000: 2931 items.
  - Compile: ./compile.sh
  - Chạy pha 1: java -cp ".:bin:library/gson-2.8.5.jar" src.SolutionPhase1
  - Chạy pha 2: java -cp ".:bin:library/gson-2.8.5.jar" src.SolutionPhase2
  - Check: java -cp ".:bin:library/gson-2.8.5.jar" src.MinMaxTypeMultiKnapsackSolution | java -cp ".:bin:library/gson-2.8.5.jar" src.SolutionChecker
