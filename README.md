# J2EE
USECASE TỔNG QUAN : 
![image](https://github.com/trungnguyenle021106/Social-Network-SpringMVC-Socket/assets/91516024/30d249a5-4838-444c-97d2-3fdfbe67ede0)
USECASE PHÂN RÃ :
![image](https://github.com/trungnguyenle021106/Social-Network-SpringMVC-Socket/assets/91516024/90d5adfd-e535-4441-8e51-3e22a789479b)
![image](https://github.com/trungnguyenle021106/Social-Network-SpringMVC-Socket/assets/91516024/e5f6fa19-698e-4e10-a323-316b406a17f1)
![image](https://github.com/trungnguyenle021106/Social-Network-SpringMVC-Socket/assets/91516024/43c07f1b-39d5-46e1-888c-942a233abaaf)
![image](https://github.com/trungnguyenle021106/Social-Network-SpringMVC-Socket/assets/91516024/e87df0cc-a0a3-4213-aafa-2c08df392775)
![image](https://github.com/trungnguyenle021106/Social-Network-SpringMVC-Socket/assets/91516024/8751de64-c0f6-442a-94ac-4e46fd7ef456)
![image](https://github.com/trungnguyenle021106/Social-Network-SpringMVC-Socket/assets/91516024/684e0598-93b3-443f-9a33-d8e0f7c63bde)
![image](https://github.com/trungnguyenle021106/Social-Network-SpringMVC-Socket/assets/91516024/0122ea8c-bf9c-46fc-88c8-5650800fc78b)

DATABASE :
Thực thể "Users" (Người dùng):
-	user_id (Khóa chính): Mã số người dùng duy nhất.
-	username: Tên người dùng.
-	password: Mật khẩu (đã mã hóa).
-	full_name: Họ và tên đầy đủ của người dùng.
-	avatar_url: URL hình đại diện của người dùng.

Thực thể "Post" (Bài đăng):
-	post_id (Khóa chính): Mã số bài đăng duy nhất.
-	user_id (Khóa ngoại): Liên kết với người dùng tạo bài đăng.
-	content: Nội dung bài đăng  văn bản.
-	url: Nội dung bài đăng là hình ảnh hoặc video.
-	post_date: Ngày và giờ tạo bài đăng.

Thực thể "Friendship" (Mối quan hệ bạn bè):
-	friendship_id (Khóa chính): Mã số mối quan hệ bạn bè duy nhất.
-	my_user_id (Khóa ngoại): Liên kết với người dùng là bản thân trong mối quan hệ.
-	friend_user_id (Khóa ngoại): Liên kết với người dùng là bạn trong mối quan hệ.
-	status : Trạng thái (pending, accepted, declined, unfriend)

Thực thể "Comment" (Bình luận):
-	comment_id (Khóa chính): Mã số bình luận duy nhất.
-	post_id (Khóa ngoại): Liên kết với bài đăng mà bình luận được thêm vào.
-	user_id (Khóa ngoại): Liên kết với người dùng thêm bình luận.
-	content: Nội dung bình luận.
-	comment_date: Ngày và giờ thêm bình luận.

Thực thể "Likes" (Thích):
-	like_id (Khóa chính): Mã số thích duy nhất.
-	post_id (Khóa ngoại): Liên kết với bài đăng mà người dùng thích.
-	user_id (Khóa ngoại): Liên kết với người dùng thực hiện thích.
-	like_date: Ngày và giờ thực hiện thích.
-	status: Trạng thái có like hoặc không (yes, no).

Thực thể "Share" (Chia sẻ):
-	share_id (Khóa chính): Mã số chia sẻ duy nhất.
-	post_id (Khóa ngoại): Liên kết với bài đăng mà người dùng chia sẻ.
-	my_user_id (Khóa ngoại): Liên kết với người dùng thực hiện chia sẻ.
-	friend_user_id (Khóa ngoại): Liên kết với người dùng được chia sẻ.
-	share_date: Ngày và giờ thực hiện chia sẻ.
