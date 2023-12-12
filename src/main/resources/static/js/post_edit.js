document.getElementById("closeInp").addEventListener("click", function(event) {
    event.preventDefault(); // Ngăn chặn hành vi mặc định của thẻ <a>
    window.history.back(); // Chuyển hướng người dùng về trang trước đó
});

document.getElementById('fileInput').addEventListener('change', function () {
    var input = this;
    var file = input.files[0];

    if (file) {
        var reader = new FileReader();
        reader.onload = function (e) {
            document.getElementById('previewImage').setAttribute('src', e.target.result);
        };
        reader.readAsDataURL(file);
    } else {
        document.getElementById('previewImage').setAttribute('src', '');
    }
});



