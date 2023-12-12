// Bắt sự kiện submit của form
$("#form-edit-profile").submit(function (event) {
    // Ngăn chặn form tự submit
    event.preventDefault();

    // Lấy giá trị mật khẩu cũ và mới từ input
    var oldPassword = $("#oldpass").val();
    var newPassword = $("#newpass").val();

    if (newPassword === "" && oldPassword === "") {
        
        // Nếu cả hai đều rỗng, thực hiện form
        document.getElementById('form-edit-profile').submit();
    } else
    if (newPassword === "" || oldPassword === "") {
        //nếu 1 trong hai rỗng
        if(newPassword === ""){
            $("#newpass").focus();
            $("#checkoldpass").text("Hãy nhập mật khẩu mới.");
        } else 
        if(oldPassword === ""){
            $("#oldpass").focus();
            $("#checkoldpass").text("Hãy nhập mật khẩu cũ.");
        }
        
    } else {
        // Gọi Ajax để kiểm tra mật khẩu cũ và khẩu mới
        $.ajax({
            type: "POST",
            url: "/checkPassword",
            data: {oldPassword: oldPassword, newPassword: newPassword},
            success: function (response) {
                // Xử lý kết quả thành công
                if(response === "Pass"){
                    document.getElementById('form-edit-profile').submit();
                    $("#newpass").focus();
                } else if(response === "newpassno"){
                    $("#checknewpass").text("Mật khẩu không đủ mạnh, ít nhất có 8 ký tự, có ký tự viết hoa, số và ký tự đăc biệt.");
                    $("#newpass").focus();
                }else if(response === "oldpassno"){
                    $("#checkoldpass").text("Nhập sai mật khẩu cũ, hãy nhập lại");
                    $("#oldpass").focus();
                }
                
                
            },
            error: function (error) {
                // Xử lý lỗi
                //$("#passwordCheckResult").text("Error changing password");
            }
        });
    }
});