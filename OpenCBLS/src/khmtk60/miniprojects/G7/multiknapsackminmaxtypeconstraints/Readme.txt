Trong code có 2 mô hình được sử dụng :
	+ Dùng mảng 2 chiều: Em convert dữ liệu sang int rồi dùng thư viện CBLS. Chỉ chạy được file 100 items. file 1000 items thì bị tràn 
	+ Dùng mảng 1 chiều: Đã chạy được file 1000 items nhưng do máy yếu nên mới chỉ chạy được 20 step mặc dù violation vẫn đang giảm. 
		Kết quả chạy file 100 dùng mảng 1 chiều là:
		495 489 479 496 487 491 491 495 478 498 479 499 510 499 493 479 497 499 488 510 486 485 493 492 510 487 501 492 510 510 511 511 490 490 511 510 1154 80 510 1045 1162 1039 414 1056 1331 1162 804 459 390 839 797 1014 433 1285 1020 439 1350 1109 439 537 510 509 510 514 515 510 515 509 515 508 503 515 508 515 507 514 509 510 509 510 508 506 500 506 503 507 500 508 741 1389 833 186 484 483 56 253 1674 447 1388 832 
Để chạy code của phương pháp dùng mảng 2 chiều cần sửa đường link dẫn đến data. 
	bỏ chú thích 3 lệnh mks.data() mks.stateModel() và mks.search() trong hàm main của file MinMaxTypeMultiKnapsackSolution
Để chạy code của phương pháp dùng mảng 1 chiều thì bỏ chú thích mks.use_1d_arr()
Vì kết quả chưa phải giá trị tối ưu nên em chưa đưa vào. Em xin thầy để em có thể submit kết quả tốt nhất sau
		