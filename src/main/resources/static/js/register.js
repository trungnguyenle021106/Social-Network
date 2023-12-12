$(document).ready(function () {
    // Khi giá trị của trường nhập liệu thay đổi
    $("#username, #password, #confirm_password").on('input', function () {
        // Kiểm tra xem trường nhập liệu có lỗi không
        if ($(this).val().length > 0) {
            // Nếu không có lỗi, thay đổi viền trở lại màu đen
            $(this).css("border", "1px solid black");
            // Xóa thông báo lỗi
            $("#" + $(this).attr('id') + "Error").text("");
        }
    });
    $("#form-register").on("submit", function (e) {
        e.preventDefault();
        $.ajax({
            url: $(this).attr('action'),
            type: 'post',
            data: $(this).serialize(),
            success: function (data) {
                if (data == "Username!!") {
                    // Hiển thị thông báo lỗi
                    $("#usernameError").text("Vui lòng kiểm tra lại tên đăng nhập");
                    // Đặt con trỏ vào trường nhập liệu
                    $("#username").focus();
                    // Thay đổi viền thành màu đỏ
                    $("#username").css("border", "1px solid red");
                }
                if (data == "Usernameexisted!!") {
                    // Hiển thị thông báo lỗi
                    $("#usernameError").text("Tên đăng nhập đã tồn tại");
                    // Đặt con trỏ vào trường nhập liệu
                    $("#username").focus();
                    // Thay đổi viền thành màu đỏ
                    $("#username").css("border", "1px solid red");
                }
                if (data == "Passconfirm!!") {
                    // Hiển thị thông báo lỗi
                    $("#confirm_passwordError").text("Xác nhận mật khẩu không khớp");
                    // Đặt con trỏ vào trường nhập liệu
                    $("#confirm_password").focus();
                    // Thay đổi viền thành màu đỏ
                    $("#confirm_password").css("border", "1px solid red");
                }
                if (data == "Pass!!") {
                    // Hiển thị thông báo lỗi
                    $("#passwordError").text("Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt");
                    // Đặt con trỏ vào trường nhập liệu
                    $("#password").focus();
                    // Thay đổi viền thành màu đỏ
                    $("#password").css("border", "1px solid red");
                }
                if (data == "/login") {
                    window.location.href = data;
                }

            },
            error: function (jqXHR, textStatus, errorThrown) {
                // If the server returns an error message
                // Parse the response
                var response = JSON.parse(jqXHR.responseText);
                // Clear all previous errors
                $(".error-message").empty();
                // Display the errors on the form
                for (var key in response.errors) {
                    $("#" + key + "Error").text(response.errors[key]);
                    // Đặt con trỏ vào trường nhập liệu
                    $("#" + key).focus();
                    // Thay đổi viền thành màu đỏ
                    $("#" + key).css("border", "1px solid red");
                }
            }
        });
    });
});