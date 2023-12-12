var offset = 5;
var limit = 5;

document.getElementById("profileButton").addEventListener("click", function () {
    // Lấy ID người dùng từ cookie
    var userId = getCookie("userid");

    // Tạo URL cho trang hồ sơ của người dùng
    var profileUrl = "/us/profile/" + userId;

    // Chuyển hướng người dùng đến trang hồ sơ của họ
    window.location.href = profileUrl;
});

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}




// Sự kiện khi nhấn nút "Tải thêm"
$("#loadMoreButton").click(function () {

    loadMorePosts();
});

function loadMorePosts() {
    var userId = document.getElementById("user_login").getAttribute("user_login");
    // Gửi Ajax request để lấy thêm bài viết
    $.ajax({
        type: "GET",
        url: "/loadMorePosts",
        data: {
            offset: offset, // chỉ số bắt đầu
            limit: limit, // số post cần lấy mỗi lần
            userId: userId
        },
        success: function (response) {
            // Xử lý dữ liệu trả về từ server

            console.log(response);
            // Cập nhật nội dung trang HTML với dữ liệu mới
            updatePageContent(response);
            offset += limit;
        },
        error: function (error) {
            // Xử lý lỗi nếu có
            console.error("Error: ", error);
        }
    });
}

function updatePageContent(data) {
    // Cập nhật nội dung trang HTML với dữ liệu mới
    $("#postContainer").append(data);
}

function showSuggestions(keyword) {

    $.ajax({
        url: '/all/users',
        type: 'GET',
        contentType: 'application/json',

        success: function (data) {

            var usersList = [];

            data.forEach(function (user) {
                if (user.full_name.toLowerCase().includes(keyword.toLowerCase()))
                {
                    usersList.push(user);
                }
            });

            var suggestions = usersList;

            var suggestionBox = document.getElementById('suggestion-box');
            suggestionBox.innerHTML = '';

            if (suggestions.length > 0) {
                suggestionBox.style.display = 'block';

                suggestions.forEach(function (suggestion) {

                    // Tạo phần tử div chính
                    var userSearchDiv = document.createElement('div');
                    userSearchDiv.className = 'User_Search';

                    var avatarImg = document.createElement('img');
                    avatarImg.className = 'User_Avt_Search';
                    avatarImg.setAttribute('src', '/images/img_avt/' + suggestion.avata_url);
                    userSearchDiv.appendChild(avatarImg);

                    var borderSearchDiv = document.createElement('div');
                    borderSearchDiv.className = 'Boder_Search';
                    userSearchDiv.appendChild(borderSearchDiv);

                    var userNameDiv = document.createElement('div');
                    userNameDiv.className = 'User_Name1_Search';
                    userNameDiv.textContent = suggestion.full_name;
                    borderSearchDiv.appendChild(userNameDiv);

                    var suggestionItem = document.createElement('div');
                    suggestionItem.className = 'suggestion-item';

                    suggestionItem.appendChild(userSearchDiv);
                    suggestionItem.onclick = function () {
                        window.location.href = "/us/profile/" + suggestion.user_id;

                        suggestionBox.style.display = 'none';
                    };
                    suggestionBox.appendChild(suggestionItem);
                });
            } else {
                suggestionBox.style.display = 'none';
            }
        },
        error: function () {
            // Xử lý phản hồi thất bại
            console.error('Có lỗi xảy ra.');
        }
    });

}

document.addEventListener('click', function (event) {
    var suggestionBox = document.getElementById('suggestion-box');
    if (event.target !== suggestionBox && !suggestionBox.contains(event.target)) {
        suggestionBox.style.display = 'none';
    }
});


var avatarImg = document.getElementById('avatar-img');
var dropdownMenu = document.getElementById('dropdown-menu');

if (avatarImg && dropdownMenu) {
    avatarImg.addEventListener('click', function () {
        if (dropdownMenu.style.display === 'block') {
            dropdownMenu.style.display = 'none';
        } else {
            dropdownMenu.style.display = 'block';
        }
    });
} else {
    console.warn("Không tìm thấy một hoặc cả hai phần tử với id 'avatar-img' hoặc 'dropdown-menu'");
}

function LogOut()
{
    document.cookie = "userid=;";
    window.location.href = "/logout";
}

function confirmDelete()
{
    $(document).on('click', '.xoa', function () {
        var $this = $(this);
        var deleteId = $this.attr('id');
        var result = confirm("Bạn có muốn tiếp tục?");
        if (result === true) {
            $.ajax({
                url: '/post/delete',
                type: 'GET',
                contentType: 'application/json',
                data: {
                    post_id: deleteId
                },
                success: function (data) {
                    window.location.href = "/homes";
                },
                error: function () {
                    // Xử lý phản hồi thất bại
                    console.error('Có lỗi xảy ra.');
                }
            });
        }
    });

}
document.addEventListener('DOMContentLoaded', function () {
    document.addEventListener('click', function (event) {
        var clickedElement = event.target;

        // Kiểm tra nút đã được bấm
        if (clickedElement.tagName === 'INPUT' && clickedElement.type === 'submit') {
            var btnRightContainer = clickedElement.closest('.left_user');
            var replacementText = btnRightContainer.querySelector('.replacementText');

            // Giấu nút
            btnRightContainer.querySelector('.btn_left').style.display = 'none';

            // Đặt text thay thế
            replacementText.innerText = `Lời mời đã được gửi.`;
        }
    });
});
document.addEventListener('click', function (event) {
    var clickedElement = event.target;
    // Kiểm tra nút đã được bấm
    if (clickedElement.tagName === 'INPUT' && clickedElement.type === 'submit') {
        var btnRightContainer = clickedElement.closest('.right_user');
        if (btnRightContainer) {
            var replacementText = btnRightContainer.querySelector('.replacementText');
            // Tiếp tục xử lý
            if (replacementText) {
                // Giấu nút
                btnRightContainer.querySelector('.btn_right').style.display = 'none';
                // Đặt text thay thế
                if (clickedElement.value == 'Chấp nhận') {
                    replacementText.innerText = `Bạn đã chấp nhận lời mời.`;
                } else {
                    replacementText.innerText = `Bạn đã từ chối lời mời.`;
                }
            } else {
                console.warn("Không tìm thấy phần tử với class 'replacementText' trong btnRightContainer.");
            }
        } else {
            console.warn("Không tìm thấy phần tử với class 'right_user' khi sử dụng closest.");
        }
    }
});


function accpectInviteFromHome(clickedButton) {
    var buttonText = clickedButton.value;
    var requestData;
    var friendshipId = $(clickedButton).closest('.right_user').find('[name="friendship-id"]').val();
    ;
    if (buttonText === 'Chấp nhận') {
        requestData = 'accepted';
        $.ajax({
            type: "POST",
            url: "/accpectInviteFromHome",
            contentType: "application/json",
            data: JSON.stringify({buttonText: requestData, Id: friendshipId}),
            success: function (response) {
                // Handle the response dynamically
                if (response === "home") {
                    // Update a specific element on the page with the response
                    $('#resultContainer').text("Successfully processed. Redirecting to home...");

                    // You can also use window.location.href to redirect the entire page
                    // window.location.href = "/home";
                } else {
                    // Handle other responses or actions as needed
                }
            },
            error: function (error) {
                console.error("Error during POST request", error);
            }
        });
    } else {
        requestData = 'declined';
        $.ajax({
            type: "POST",
            url: "/accpectInviteFromHome",
            contentType: "application/json",
            data: JSON.stringify({buttonText: requestData, Id: friendshipId}),
            success: function (response) {
                // Handle the response dynamically
                if (response === "home") {
                    // Update a specific element on the page with the response
                    $('#resultContainer').text("Successfully processed. Redirecting to home...");

                    // You can also use window.location.href to redirect the entire page
                    // window.location.href = "/home";
                } else {
                    // Handle other responses or actions as needed
                }
            },
            error: function (error) {
                console.error("Error during POST request", error);
            }
        });
    }
}
function sendFriendRequest(clickedButton) {
    var userId = $(clickedButton).closest('.left_user').find('[name="user-id"]').val();
    var friendshipId = $(clickedButton).closest('.left_user').find('[name="friendship-id"]').val();

    $.ajax({
        type: "POST",
        url: "/sendFriendRequest",
        contentType: "application/json",
        data: JSON.stringify({IdUser: userId, IdFriend: friendshipId}),
        success: function (response) {
            // Handle the response dynamically
            if (response === "home") {
                // Update a specific element on the page with the response
                $('#resultContainer').text("Successfully processed. Redirecting to home...");

                // You can also use window.location.href to redirect the entire page
                // window.location.href = "/home";
            } else {
                // Handle other responses or actions as needed
            }
        },
        error: function (error) {
            console.error("Error during POST request", error);
        }
    });
}