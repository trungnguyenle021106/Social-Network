'use strict';

var nameInput = $('#name');
var roomInput = $('#room-id');
var usernameForm = document.querySelector('#usernameForm');
var messageInput = document.getElementById("message");
var messageArea = document.querySelector('#messageArea');


var likeElement = document.getElementById("likeAmount");
var commentElement = document.getElementById("commentAmount");
var shareElement = document.getElementById("shareAmount");

var postDetailElement = document.getElementById("postDetail");
var movePageElement = document.getElementById("movePage");
var shareFormElement = document.getElementById("shareForm");

var stompClient = null;
var currentSubscription;
var username = null;
var roomId = null;
var topic = null;


// SOCKET
function connect(event) {

    username = nameInput.val().trim();
    if (username) {
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected);
    }
//    event.preventDefault();
}

function enterRoom(newRoomId) {
    roomId = newRoomId;
    topic = `/app/chat/${roomId}`;

    currentSubscription = stompClient.subscribe(`/channel/${roomId}`, onMessageReceived);

//    stompClient.send(`${topic}/addUser`,
//            {},
//            JSON.stringify({sender: username})
//            );
}

function onConnected() {
    enterRoom(roomInput.val());
}

function onMessageReceived(payload) {



    var message = JSON.parse(payload.body);
    var userId = $('#myUserID').val();

    if (message.type == "DELETED_POST")
    {
        postDetailElement.style.display = "none";
        movePageElement.style.display = "block";
    }



    if (message.type == "COMMENT")
    {
        var arrayStringHtml = [];
        var temp = getCommentHTML(message.listComments, userId);
        
        arrayStringHtml = getOldHTML(message.sender);
        var amountComments = (temp.toString().split("User_Comment").length - 1)/2;
        if (amountComments == 0)
        {
            amountComments = "";
        }
        messageArea.innerHTML = temp;
        commentElement.textContent = amountComments;
        callBackHtml(arrayStringHtml);
        
    }



    if (message.type == "LIKE" && message.sender == userId) {
        var likeButton = document.getElementById("likeButton");
        var temp = message.content.toString().split("amount");
        var html = temp[0];
        likeButton.innerHTML = html;
    }

    if (message.type == "LIKE") {
        var temp = message.content.toString().split("amount");
        var amount = temp[1];
        if (amount == 0)
        {
            amount = "";
        }
        likeElement.textContent = amount;
    }


    if (message.type == "SHARE")
    {
        var amountShare = message.content;
        if (amountShare == 0)
        {
            amountShare = "";
        }
        shareElement.textContent = amountShare;
    }

//    messageArea.scrollTop = messageArea.scrollHeight;
}
// SOCKET

function callBackHtml(arrayStringHtml)
{
    var userId = $('#myUserID').val();

    if (arrayStringHtml.length != 0)
    {
        for (var i = 0; i < arrayStringHtml.length; i++) {

            var htmlContent = arrayStringHtml[i];
            var User_Comment_Elements = document.getElementsByClassName("User_Comment");

// Lặp qua từng phần tử div và xem nội dung HTML
            for (var j = 0; j < User_Comment_Elements.length; j++) {

                if (User_Comment_Elements[j].innerHTML.toString().includes(`id="${userId}"`) == true)
                {
                    var div1 = document.createElement('div');
                    div1.innerHTML = User_Comment_Elements[j].innerHTML;
                    var div2 = document.createElement('div');
                    div2.innerHTML = arrayStringHtml[i];

                    var deleteButton1 = div1.querySelectorAll(".Delete_Comment_Button")[0];
                    var deleteButton2 = div2.querySelectorAll(".Delete_Comment_Button")[0];

                    var id1 = deleteButton1.id;
                    var id2 = deleteButton2.id;

                    if (id1 == id2)
                    {
                        var idComment = id1.split("_")[1];
                        var idUCCurElement = "User_Comment_" + idComment;
                        var UCCurElement = document.getElementById(idUCCurElement);
                        UCCurElement.innerHTML = arrayStringHtml[i];
                    }
                    div1.remove();
                    div2.remove();
                }


            }


        }

    }
}

function getOldHTML(senderId)
{
    var userId = $('#myUserID').val();
    var userCommentElements = messageArea.getElementsByClassName("User_Comment");
    var htmlArray = [];

    var htmlContents = [];
    for (var i = 0; i < userCommentElements.length; i++) {
        var userCommentElement = userCommentElements[i];
        var htmlContent = userCommentElement.innerHTML;
        htmlContents.push(htmlContent.toString().trim());
    }

    for (var i = 0; i < htmlContents.length; i++)
    {

        var string = htmlContents[i];
        var subString = "block";
        var pattern = new RegExp(`\\b${subString}\\b`);
        var containsSubstring = pattern.test(string);


        var idToCheck = userId;
        var containsId = string.includes(`id="${idToCheck}"`);

        var containsIdSender = string.includes(`id="${senderId}"`);

        if (containsSubstring === true && containsId === true && containsIdSender === false)
        {
            htmlArray.push(string);
        }

    }
    return htmlArray;
}

//NÚT EDIT
function cancelEditCommentButton()
{
    $(document).on('click', '.Cancel_Edit_Comment_Button', function () {
        var $this = $(this);
        var cancelId = $this.attr('id');
        var idComment = (cancelId.split("cancel_").toString().replace(",", ""));

        var inputId = "input_" + idComment;
        var acceptId = "accept_" + idComment;

        var inputElement = document.getElementById(inputId);
        var canceclElement = document.getElementById(cancelId);
        var acceptElement = document.getElementById(acceptId);

        inputElement.removeAttribute("contentEditable");
        inputElement.classList.remove("bordered-input");
        acceptElement.style.display = "none";
        canceclElement.style.display = "none";
    });
}

function acceptEditCommentButton()
{
    $(document).on('click', '.Accept_Edit_Comment_Button', function () {
        var $this = $(this);
        var acceptId = $this.attr('id');
        var idComment = (acceptId.split("accept_").toString().replace(",", ""));

        var inputId = "input_" + idComment;
        var cancelId = "cancel_" + idComment;

        var inputElement = document.getElementById(inputId);


        if (inputElement.textContent != "") {

            var acceptElement = document.getElementById(acceptId);
            var cancelElement = document.getElementById(cancelId);

            editcm(idComment, inputElement.textContent);

            inputElement.removeAttribute("contentEditable");
            inputElement.classList.remove("bordered-input");
            acceptElement.style.display = "none";
            cancelElement.style.display = "none";
        } else {
            alert("Bình luận không được để trống");
            inputElement.focus();
        }
    });
}

function editButton()
{
    $(document).on('click', '.Edit_Comment_Button', function () {
        var $this = $(this);
        var editId = $this.attr('id');
        var idComment = (editId.split("edit_").toString().replace(",", ""));
        var inputId = "input_" + idComment;
        var inputElement = document.getElementById(inputId);
        var acceptId = "accept_" + idComment;
        var acceptElement = document.getElementById(acceptId);
        var cancelId = "cancel_" + idComment;
        var cancelElement = document.getElementById(cancelId);


        inputElement.setAttribute("contentEditable", "true");
        inputElement.classList.add("bordered-input");
        inputElement.focus();
        acceptElement.style.display = "block";
        cancelElement.style.display = "block";
    });
}

function editcm(id, editedComment)
{
    var userId = $('#myUserID').val();
    var postId = $('#postID').val();
// Gửi AJAX request
    $.ajax({
        url: '/comment/edit/',
        type: 'GET',
        contentType: 'application/json',
        data: {
            post_id: postId,
            comment_id: id,
            editedComment: editedComment
        },
        success: function (data) {
            // Xử lý phản hồi thành công

            var Message = {
                listComments: data,
                type: "COMMENT",
                sender: userId
            };
            stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(Message));
        },
        error: function () {
            // Xử lý phản hồi thất bại
            console.error('Có lỗi xảy ra.');
        }
    });
}
//NÚT EDIT

//NÚT DELETE
function deleteButton()
{
    var userId = $('#myUserID').val();
    $(document).on('click', '.Delete_Comment_Button', function () {
        var $this = $(this);
        var deleteId = $this.attr('id');
        var postId = $('#postID').val();
        var idComment = (deleteId.split("delete_").toString().replace(",", ""));
        // Gửi AJAX request
        $.ajax({
            url: '/comment/delete',
            type: 'GET',
            contentType: 'application/json',
            data: {
                post_id: postId,
                comment_id: idComment
            },
            success: function (data) {
                // Xử lý phản hồi thành công

                var Message = {
                    listComments: data,
                    type: "COMMENT",
                    sender: userId
                };
                stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(Message));
            },
            error: function () {
                // Xử lý phản hồi thất bại
                console.error('Có lỗi xảy ra.');
            }
        });
    });
}
//NÚT DELETE

//NÚT 3 CHẤM
function ellipsisButton()
{

    $(document).on('click', '.c3', function () {

        var $this = $(this);
        var ellipsisId = $this.attr('id');
        var idComment = (ellipsisId.split("ellipsis_").toString().replace(",", ""));

        var editId = "edit_" + idComment;
        var acceptId = "accept_" + idComment;
        var cancelId = "cancel_" + idComment;
        var deleteId = "delete_" + idComment;

        var editElement = document.getElementById(editId);
        var acceptElement = document.getElementById(acceptId);
        var cancelElement = document.getElementById(cancelId);
        var deleteElement = document.getElementById(deleteId);

        if (editElement.style.display === "block")
        {
            editElement.style.display = "none";
            acceptElement.style.display = "none";
            cancelElement.style.display = "none";
            deleteElement.style.display = "none";
        } else {
            editElement.style.display = "block";
            deleteElement.style.display = "block";
        }

    });


}
//NÚT 3 CHẤM

// NÚT LIKE
function likeButton()
{
    $('#likeButton').click(function () {
        var postId = $('#postID').val();
        var userId = $('#myUserID').val();

        var status = document.getElementById("inputLike").value.toString();
        var likeId = document.getElementById("likeId").value;

        var url = "";
        if (status == "YES")
        {
            url = "/likes/enable_disable_Like";
            status = "NO";
            like_unlike(url, status, likeId, postId, userId);

        } else if (status == "NO") {
            url = "/likes/enable_disable_Like";
            status = "YES";
            like_unlike(url, status, likeId, postId, userId);

        } else if (status == "NOLIKE") {
            url = "/likes/addLike";
            newlike(url, postId, userId);

        }
    });
}

function like_unlike(url, status, likeId, post_id, user_id)
{

    $.ajax({
        url: url, // Đường dẫn đến endpoint xử lý yêu cầu của bạn
        type: 'GET', // Phương thức yêu cầu (POST, GET, PUT, DELETE, v.v.)
        data: {
            likeID: likeId,
            status: status,
            post_id: post_id,
            user_id: user_id
        },
        success: function (data) {
            var Message = {
                content: getLikeHTML(data),
                type: "LIKE",
                sender: user_id
            };
            stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(Message));
        },
        error: function () {
            console.log("lỗi");
        }
    });
}

function newlike(url, postId, userId)
{
    $.ajax({
        url: url, // Đường dẫn đến endpoint xử lý yêu cầu của bạn
        type: 'GET', // Phương thức yêu cầu (POST, GET, PUT, DELETE, v.v.)
        data: {
            post_id: postId,
            user_id: userId
        },
        success: function (data) {
            var Message = {
                content: getLikeHTML(data),
                type: "LIKE",
                sender: userId
            };
            stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(Message));
        },
        error: function () {
            console.log("lỗi");
        }
    });
}
// NÚT LIKE


//NÚT ADD
function addcm()
{

    $('#addButton').click(function () {

        var comment = messageInput.textContent.toString();
        var postId = $('#postID').val();
        var userId = $('#myUserID').val();
        if (comment === "")
        {
            alert("Chưa nhập nội dung bình luận");
            document.getElementById("placeholder").style.display = "none";
            messageInput.focus();
        } else {
            $.ajax({
                url: '/comment/add',
                type: 'GET',
                contentType: 'application/json',
                data: {
                    post_id: postId,
                    content: comment,
                    user_id: userId
                },
                success: function (data) {

                    // Xử lý phản hồi thành công
                    var Message = {
                        listComments: data,
                        type: "COMMENT",
                        sender: userId
                    };
                    messageInput.textContent = "";
                    document.getElementById("placeholder").style.display = "block";
                    stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(Message));

                },
                error: function () {
                    // Xử lý phản hồi thất bại
                    console.error('Có lỗi xảy ra.');
                }
            });
        }


// Gửi AJAX request


    });
}
//NÚT ADD

// XỬ LÝ HTML
function getCommentHTML(data, userId)
{

    var html = "";

    data.forEach(function (commentUser) {
        var htmlString = "";
//                Không hiểu sao mất chữ r trong avatar :v
        if (userId == commentUser.myUser.user_id) {
            htmlString = '<div class="User_Comment" id="User_Comment_' + commentUser.comment_id + '">\n' +
                    '\t<img class="My_user_avt" src="/images/img_avt/' + commentUser.myUser.avata_url + '" />\n' +
                    '\t<div class="Boder_comment">\n' +
                    '\t\t<!-- USERID COMMENT -->\n' +
                    '\t\t<div class="User_Name1" id="' + commentUser.myUser.user_id + '">' + commentUser.myUser.full_name + '</div>\n' +
                    '\t\t<div class="post_date">' + commentUser.commentDate + '</div>\n' +
                    '\t\t<!-- COMMENT ID -->\n' +
                    '\t\t<div class="Content_commnent" id="input_' + commentUser.comment_id + '">' + commentUser.content + '</div>\n' +
                    '\t\t<button style="display:none" class="Delete_Comment_Button" id="delete_' + commentUser.comment_id + '">XÓA</button>\n' +
                    '\t\t<button style="display:none" class="Edit_Comment_Button" id="edit_' + commentUser.comment_id + '">SỬA</button>\n' +
                    '\t\t<button style="display:none" class="Accept_Edit_Comment_Button" id="accept_' + commentUser.comment_id + '">CHẤP NHẬN</button>\n' +
                    '\t\t<button style="display:none" class="Cancel_Edit_Comment_Button" id="cancel_' + commentUser.comment_id + '">HỦY</button>\n' +
                    '\t</div>\n' +
                    '\t\t<button  class="c3" id="ellipsis_' + commentUser.comment_id + '">...</button>\n' +
                    '</div>';
        } else
        {
            htmlString = '<div class="User_Comment" id="User_Comment_' + commentUser.comment_id + '">\n' +
                    '\t<img class="My_user_avt" src="/images/img_avt/' + commentUser.myUser.avata_url + '" />\n' +
                    '\t<div class="Boder_comment">\n' +
                    '\t\t<!-- USERID COMMENT -->\n' +
                    '\t\t<div class="User_Name1" id="' + commentUser.myUser.user_id + '">' + commentUser.myUser.full_name + '</div>\n' +
                    '\t\t<div class="post_date">' + commentUser.commentDate + '</div>\n' +
                    '\t\t<!-- COMMENT ID -->\n' +
                    '\t\t<div class="Content_commnent" id="' + commentUser.comment_id + '">' + commentUser.content + '</div>\n' +
                    '\t</div>\n' +
                    '</div>';
        }
        html = html + htmlString;
    });
    return html;
}

function getLikeHTML(data)
{
    var userId = $('#myUserID').val();
    var amountLikes = 0;
    var this_like = null;
    var likeImg;
    var htmlCode;

    data.forEach(function (like) {
        if (like.likedUser.user_id == userId)
        {
            this_like = like;
            if (like.status == "YES")
            {
                amountLikes++;
            }
        } else if (like.status == "YES") {
            amountLikes++;
        }
    });

    if (this_like.status == "YES" && this_like.likedUser.user_id == userId)
    {
        likeImg = "/images/liked.png";
        htmlCode = "<input style=\"display:none\" id=\"likeId\" value=\"" + this_like.like_id + "\">" +
                "<input style=\"display:none\" id=\"inputLike\" value=\"" + this_like.status + "\">" +
                "<img class=\"custom-icon1\" id=\"likeImg\" src=\"" + likeImg + "\">" +
                "<span style=\"color:#0d6efd; fontSize:20px; marginLeft:10px\" id=\"likeText\">Like</span>";
        htmlCode = htmlCode + "amount" + amountLikes;
    } else {
        likeImg = "/images/like.png";
        htmlCode = "<input style=\"display:none\" id=\"likeId\" value=\"" + this_like.like_id + "\">" +
                "<input style=\"display:none\" id=\"inputLike\" value=\"" + this_like.status + "\">" +
                "<img class=\"custom-icon1\" id=\"likeImg\" src=\"" + likeImg + "\">" +
                "<span style=\"fontSize:20px; marginLeft:10px\" id=\"likeText\">Like</span>";
        htmlCode = htmlCode + "amount" + amountLikes;
    }
    return htmlCode;
}
// XỬ LÝ HTML

// HÀM KHI ONLOAD
function loadFunctions()
{
    messageInput.textContent = "";
    loadLike();
    connect();
}

function loadLike()
{
    var inputLike = document.getElementById("inputLike");
    var imgLike = document.getElementById("likeImg");
    var likeText = document.getElementById("likeText");

    if (inputLike.value == "YES")
    {
        imgLike.src = "/images/liked.png";

        likeText.style.color = "#0d6efd";
        likeText.style.fontSize = "20px";
        likeText.style.marginLeft = "10px";
    } else {
        imgLike.src = "/images/like.png";
        likeText.style.fontSize = "20px";
        likeText.style.marginLeft = "10px";
    }
}
// HÀM KHI ONLOAD

// CÁC BUTTON
function ButtonEvent()
{
    addcm();
    ellipsisButton();
    deleteButton();
    editButton();
    acceptEditCommentButton();
    cancelEditCommentButton();
    likeButton();
}
ButtonEvent();
// CÁC BUTTON



// NÚT XÓA BÀI VIẾT
function deletePostButton()
{
    var userId = $('#myUserID').val();
    var postId = $('#postID').val();
    var result = confirm("Bạn có muốn tiếp tục?");
    if (result === true) {
        $.ajax({
            url: '/post/delete',
            type: 'GET',
            contentType: 'application/json',
            data: {
                post_id: postId
            },
            success: function (data) {
                // Xử lý phản hồi thành công

                var Message = {
                    type: "DELETED_POST"
                };
                stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(Message));
                var jsonResponse = JSON.parse(data);
                var redirectURL = jsonResponse.redirect;
                window.location.replace(redirectURL);
            },
            error: function (data) {
                // Xử lý phản hồi thất bại
                console.error('Có lỗi xảy ra.');
            }
        });
    }
}
// NÚT XÓA BÀI VIẾT

// NÚT CHIA SẺ BÀI VIẾT
function shareButton()
{
    postDetailElement.style.display = "none";
    shareFormElement.style.display = "block";
}

function acceptShareButton()
{
    var postId = $('#postID').val();
    var userId = $('#myUserID').val();
    var checkboxes = document.querySelectorAll('input[type="checkbox"]');
    var checkedCheckboxes = [];

    checkboxes.forEach(function (checkbox) {
        if (checkbox.checked) {
            var temp = checkbox.id.toString().split("cb_");
            checkedCheckboxes.push(temp[1]);
        }
    });

    if (checkedCheckboxes.length == 0)
    {
        checkedCheckboxes = null;
    }

    $.ajax({
        url: '/share/operator',
        type: 'GET',

        data: {
            listFriendUserID: JSON.stringify(checkedCheckboxes),
            user_id: userId,
            post_id: postId
        },
        success: function (data) {
            // Xử lý phản hồi thành công
            postDetailElement.style.display = "block";
            shareFormElement.style.display = "none";
            var Message = {
                content: data,
                type: "SHARE"
            };
            stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(Message));
        },
        error: function (data) {
            console.error('Có lỗi xảy ra.');
        }
    });
}

function cancelShareButton()
{
    postDetailElement.style.display = "block";
    shareFormElement.style.display = "none";
}
// NÚT CHIA SẺ BÀI VIẾT

// THOÁT PHÒNG KHI LOAD TRANG
window.addEventListener('beforeunload', function () {
    currentSubscription.unsubscribe();
});
// THOÁT PHÒNG KHI LOAD TRANG


// PLACEHOLDER CHO THẺ DIV
function placeHolder()
{
    document.getElementById("placeholder").style.display = "none";
    messageInput.focus();
}
// PLACEHOLDER CHO THẺ DIV

// CHUYỂN TRANG
function moveToPage() {
    var redirectURL = "/homes";
    window.location.replace(redirectURL);
}
//CHUYỂN TRANG