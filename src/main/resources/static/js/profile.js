var offset = 5;
var limit = 5;

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
const friendsElement = document.querySelector('.friends-element');
if (friendsElement) {
    friendsElement.addEventListener('mouseenter', () => {
        friendsElement.style.overflowY = 'auto';
    });
    friendsElement.addEventListener('mouseleave', () => {
        friendsElement.style.overflowY = 'hidden';
    });
} else {
    console.warn("Không tìm thấy phần tử với class 'friends-element'");
}


const friendsAcceptElement = document.querySelector('.friends-accept-element');

if (friendsAcceptElement) {
    friendsAcceptElement.addEventListener('mouseenter', () => {
        friendsAcceptElement.style.overflowY = 'auto';
    });

    friendsAcceptElement.addEventListener('mouseleave', () => {
        friendsAcceptElement.style.overflowY = 'hidden';
    });
} else {
    console.warn("Không tìm thấy phần tử với class 'friends-accept-element'");
}



document.addEventListener('DOMContentLoaded', function () {
    const editInforLink = document.getElementById('edit-infor-link');
    const listFriendsLink = document.getElementById('list-friends-link');

    const editInforContent = document.querySelector('.edit-infor');
    const listFriendsContent = document.querySelector('.list-friends');

    // Kiểm tra xem editInforLink có tồn tại không
    if (editInforLink) {
        editInforLink.addEventListener('click', function (event) {
            event.preventDefault();
            editInforContent.style.display = 'block';
            listFriendsContent.style.display = 'none';
        });
    } else {
        console.warn("Không tìm thấy phần tử với id 'edit-infor-link'");
    }

    // Kiểm tra xem listFriendsLink có tồn tại không
    if (listFriendsLink) {
        listFriendsLink.addEventListener('click', function (event) {
            event.preventDefault();
            editInforContent.style.display = 'none';
            listFriendsContent.style.display = 'block';
        });
    } else {
        console.warn("Không tìm thấy phần tử với id 'list-friends-link'");
    }
});


document.addEventListener('DOMContentLoaded', function () {
    document.addEventListener('click', function (event) {
        var clickedElement = event.target;
        // Kiểm tra nút đã được bấm
        if (clickedElement.tagName === 'INPUT' && clickedElement.type === 'submit') {
            var btnRightContainer = clickedElement.closest('.friend-accept');
            var replacementText = btnRightContainer.querySelector('.replacementText');
            // Giấu nút
            btnRightContainer.querySelector('.friend-accept-container-button').style.display = 'none';
            // Đặt text thay thế
            if (clickedElement.value == 'Chấp nhận') {
                replacementText.innerText = `Bạn đã chấp nhận lời mời.`;
            } else
                replacementText.innerText = `Bạn đã từ chối lời mời.`;
        }
    });
});

document.addEventListener('DOMContentLoaded', function () {
    document.addEventListener('click', function (event) {
        var clickedElement = event.target;
        // Kiểm tra nút đã được bấm
        if (clickedElement.tagName === 'INPUT' && clickedElement.type === 'submit') {
            var btnRightContainer = clickedElement.closest('.friend');
            var replacementText = btnRightContainer.querySelector('.replacementText');
            // Giấu nút
            btnRightContainer.querySelector('.friend-container-button').style.display = 'none';
            // Đặt text thay thế
            replacementText.innerText = `Bạn đã hủy kết bạn.`;
        }
    });
});

function accpectInviteFromProfile(clickedButton) {
    var buttonText = clickedButton.value;
    var requestData;
    var friendshipId = $(clickedButton).closest('.friend-accept').find('[name="friendship-id"]').val();
    if (buttonText === 'Chấp nhận') {
        requestData = 'accepted';
        $.ajax({
            type: "POST",
            url: "/accpectInviteFromProfile",
            contentType: "application/json",
            data: JSON.stringify({buttonText: requestData, Id: friendshipId}),
            success: function (response) {
                // Handle the response dynamically
                if (response === "profile") {
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
            url: "/accpectInviteFromProfile",
            contentType: "application/json",
            data: JSON.stringify({buttonText: requestData, Id: friendshipId}),
            success: function (response) {
                // Handle the response dynamically
                if (response === "profile") {
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
function removeFriend(clickedButton) {
    var friendshipId = $(clickedButton).closest('.friend').find('[name="friendship-id"]').val();
    $.ajax({
        type: "POST",
        url: "/removeFriend",
        contentType: "application/json",
        data: JSON.stringify({Id: friendshipId}),
        success: function (response) {
            // Handle the response dynamically
            if (response === "profile") {
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

function FriendshipOperator()
{
   $(document).on('click', '.OperatorFriendship', function () {
        var $this = $(this);
        var FriendshipOperatorIds = $this.attr('id').toString().split("_");
        var userId = FriendshipOperatorIds[0];
        var friendUserId = FriendshipOperatorIds[1];
        
        
            $.ajax({
                url: '/friends/operator',
                type: 'GET',
                contentType: 'application/json',
                data: {
                     userId:  userId,
                     friendUserId : friendUserId
                },
                success: function () {
                    location.reload();
                },
                error: function () {
                    // Xử lý phản hồi thất bại
                    console.error('Có lỗi xảy ra.');
                }
            });
        
    });
}