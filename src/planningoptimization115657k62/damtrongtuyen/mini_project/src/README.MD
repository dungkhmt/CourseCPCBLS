# CODE + DATA MINI PROJECT 20192 - TUYEN.DT173463
- $./graph_data$: chứa data dùng để test
- $./visualize result/visualize_graph.py$: visualize kết quả đồ thị sau khi phân cụm với trọng số của cạnh, màu của đỉnh đại diện cho cụm, dữ liệu xuất ra từ quá trình phân cụm lưu trong file graph_14_39_result.txt, cấu hình lại trong file python nếu cần
- $./GraphDataUtils$: dùng cho việc sinh và load đồ thị, các giá trị đầu vào như số đỉnh, số cụm
- $./KGraphPartition$: sử dụng Mip + Google Or-tools để giải bài toán
- $./KGraphPartitionChocoSolver$: sử dụng CP + Choco để giải bài toán
- Local Search
  - $./KGraphPartitionCost$: định nghĩa lại các giá hàm cost, assigndelta... dùng cho thuật toán search, sử dụng lại một số hàm của Opencbls
  - $./KGraphPartitionCBLS$: giải bài toán sử dụng các giải thuật tìm kiếm cục bộ: Hill Climbing và Large Neighborhood Search
  
  
*Cấu hình lại đường dẫn tới file dữ liệu đầu vào: biến $GRAPH_FOLDER$ trong mỗi file để chạy thuật toán; lưu kết quả: uncomment $app.saveResult(fileResult)$* 
