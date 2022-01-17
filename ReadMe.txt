
- Hướng dẫn sử dụng:
	+ Cách mở Server: java -jar ChatAppServer.jar
	+ Cách mở Client: java -jar ChatAppClients.jar
	+ Chức năng đăng nhập: Nhập username và password và bấm button sign in để đăng nhập (có xác thực tài khoản, kiểm trang tài khoản đã đăng nhập hay chưa ...)
	+ Chức năng đăng ký: bấm button Sign Up, sau đó nhập username, password, Re-password tiếp theo bấm button bấm sign up để đăng ký ( kiểm tra tài khoản đã tồn tại hay chưa)
	+ Chức năng chat với nhiều user khác đang online: Với bất kỳ user nào đăng nhập đều hiển thị trên danh sách Online User. Chọn bất kì một user trên danh sách Online User,
	Chat Panel với user đó sẽ được hiển thị(Show tất cả các lịch sử tin nhắn trước đó nếu có).Sau đó nhập tin nhắn muốn gửi vào ô chat và bấm button send để gửi tin nhắn đến
	Cho user được chọn. User nhận tin nhắn sẽ hiện thị trong Chat panel của chính mình và người gửi tin nhắn. Click chọn User bất kì để xem tin nhắn của mình và user được chọn.
	+ Chức năng gửi file trong khi chat: sau khi chọn một user bất kỳ trên danh sách Online User. Ta bấm button File (JFileChooser sẽ mở ra), sau đó ta chọn file phù hợp muốn gửi,
	bấm open trên JFileChooser để gửi file đến cho user. Sau khi thực hiện các bước trên xong thì tên file sẽ hiển thị trong Chat Panel của ta và User được gửi. Click vào tin nhắn
	chứa tên file để mở file đó ra. Lưu ý file nhận được từ user khác gửi đến sẽ nằm ở thư mục cùng cấp với file thực thi.
	+ Sau khi bấm button Exit hoặc tắt cửa sổ Client thì danh sách Online User sẽ tự động cập nhật ở các ở các Client khác.
- Tài Liệu tham khảo:
	https://stackoverflow.com/questions/13115784/sending-a-message-to-all-clients-client-server-communication/13116162