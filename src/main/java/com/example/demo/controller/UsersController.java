/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.entity.FriendShip;
import com.example.demo.entity.Likes;
import com.example.demo.entity.Post;
import com.example.demo.entity.Share;
import com.example.demo.entity.Users;
import com.example.demo.model.PostInfo;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.LikesRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.ShareRepository;
import com.example.demo.repository.UsersRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Nguyen
 */
@Controller
public class UsersController {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private ShareRepository shareRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;

    @GetMapping("/allus")
    public String getAll(Model m) {
        Iterable<Users> ls = usersRepository.findAll();

//        for (Users user : ls) {
//            System.out.println(user.getFull_name());
//            System.out.println(user.getUsername());
//        }
        m.addAttribute("list", ls);
        return "users";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalidate the session, removing any session data
        request.getSession().invalidate();

        // Create a new cookie to overwrite the existing one
        Cookie cookie = new Cookie("userid", null);
        // Set the max age to 0 to delete the cookie
        cookie.setMaxAge(0);
        // Add the cookie to the response to update the client-side cookie
        response.addCookie(cookie);

        // Set Cache-Control Headers to prevent caching of the page
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0); // Proxies.

        // Redirect the user to the login page
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginform() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, RedirectAttributes redirectAttributes, HttpServletResponse response, HttpServletRequest request) {
        Optional<Users> user = usersRepository.findByUsnameAndPsw(username, password);
        if (user.isPresent()) {
            // Tạo một cookie mới với tên là "userid" và giá trị là ID của người dùng
            Cookie cookie = new Cookie("userid", user.get().getUser_id().toString());
            // Đặt thời gian sống của cookie (ví dụ: 1 giờ)
            cookie.setMaxAge(3600);
            // Thêm cookie vào HTTP response
            response.addCookie(cookie);

            // Set the session attribute
            request.getSession().setAttribute("userid", user.get().getUser_id().toString());

            System.out.println("Login successful!");
            return "redirect:/homes";
        } else {
            System.out.print("Invalid username or password!");
            redirectAttributes.addFlashAttribute("error", "Invalid username or password!");
            return "redirect:/login";
        }
    }

    @GetMapping("/us/add")
    public String showRegistrationForm(Model model) {
        model.addAttribute("users", new Users());
        return "register";
    }
    //them moi 1 user

    @ResponseBody
    @PostMapping("/us/add")
    public String registerUser(@ModelAttribute("users") Users user, BindingResult bindingResult, @RequestParam("confirm_password") String confirmPassword, Model model) {
        // Validate username
        String username = user.getUsername();
        boolean isEmail = username.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        boolean isPhoneNumber = username.matches("^\\+?[0-9. ()-]{10,25}$"); // Modify this regex to match your phone number format
        if (!isEmail && !isPhoneNumber) {
            bindingResult.rejectValue("username", "error.username", "Username must be a phone number or email!");
            return "Username!!";
        }
        // Check if username already exists
        if (usersRepository.existsByUsername(username)) {
            bindingResult.rejectValue("username", "error.username", "Username already exists!");
            return "Usernameexisted!!";
        }
        // Validate password
        String password = user.getPassword();
        if (!password.equals(confirmPassword)) {
            bindingResult.rejectValue("password", "error.password", "Password confirmation does not match!");
            return "Passconfirm!!";
        }

        // Check password strength
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecialChar = password.matches(".*[^a-zA-Z0-9].*");
        if (password.length() < 8 || !hasLowerCase || !hasUpperCase || !hasDigit || !hasSpecialChar) {
            bindingResult.rejectValue("password", "error.password", "Password must have at least 8 characters, including uppercase, lowercase, digit and special character!");
            return "Pass!!";
        }

        // If all checks pass, create new user
        Users newUser = new Users();
        newUser.setFull_name(user.getFull_name());
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setAvata_url("/images/img_avt/user.jpg");
        usersRepository.save(newUser);

        return "/login";
    }

    @GetMapping("/us/profile/{userId}")
    public String profile(@PathVariable String userId, Model m, HttpServletResponse response, HttpServletRequest request) {
        //lấy đối tượng user
        Optional<Users> us = usersRepository.findById(Integer.valueOf(userId));
        List<PostInfo> allPost = getAllPosst(us.get().getUser_id());

        //lấy <=5 post đầu tiên
        int endIndex = Math.min(5, allPost.size());
        List<PostInfo> Post5 = allPost.subList(0, endIndex);

        Users uus = us.get();
        int idLogin = 0;
        // Danh sách bạn bè
        List<FriendShip> friendList = friendshipRepository.findByMyUserAndStatus(Integer.valueOf(userId), "accepted");
        // Danh sách lời mời
        List<FriendShip> inviteList = friendshipRepository.findByMyFriendAndStatus(Integer.valueOf(userId), "pending");

        m.addAttribute("friendList", friendList);
        m.addAttribute("inviteList", inviteList);
        m.addAttribute("user_profile", uus);
        m.addAttribute("posts", Post5);

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("userid")) {
                    idLogin = Integer.valueOf(cookie.getValue());
                }
            }
        }
        m.addAttribute("idLogin", idLogin);
        Optional<Users> userLogin = usersRepository.findById(Integer.valueOf(idLogin));
        FriendShip fs = friendshipRepository.findFriendshipByMyUserAndFriendUser(userLogin.get(), us.get()).orElse(null);
        m.addAttribute("FSWithThisUser", fs);
        return "profile";
    }

    @PostMapping("/accpectInviteFromProfile")
    @ResponseBody
    public String AcceptInvitation(@RequestBody Map<String, Object> requestBody) {
        String choice = (String) requestBody.get("buttonText");
        Object idObject = requestBody.get("Id");
        int friendUserId = 0;
        if (idObject != null) {
            friendUserId = Integer.parseInt((String) idObject);
            Optional<FriendShip> friendShip1 = friendshipRepository.findById(friendUserId);
            if (friendShip1.isPresent()) {
                FriendShip f = friendShip1.get();
                f.setStatus(choice);
                friendshipRepository.save(f);
            }

            FriendShip friend1 = friendShip1.get();
            Optional<FriendShip> friendShip2;
            try {
                friendShip2 = friendshipRepository.findById(friendUserId + 1);
                if (friendShip2.isPresent()) {
                    FriendShip friend2 = friendShip2.get();
                    if (friend1.getFriendUser().getUser_id() == friend2.getMyUser().getUser_id()
                            && friend1.getMyUser().getUser_id() == friend2.getFriendUser().getUser_id()) {
                        if (friendShip2.isPresent()) {
                            FriendShip f = friendShip2.get();
                            f.setStatus(choice);
                            friendshipRepository.save(f);
                        }
                    } else {
                        Optional<FriendShip> friendShip3 = friendshipRepository.findById(friendUserId - 1);
                        if (friendShip3.isPresent()) {
                            FriendShip f = friendShip3.get();
                            f.setStatus(choice);
                            friendshipRepository.save(f);
                        }
                    }
                } else {
                    Optional<FriendShip> friendShip3 = friendshipRepository.findById(friendUserId - 1);
                    if (friendShip3.isPresent()) {
                        FriendShip f = friendShip3.get();
                        f.setStatus(choice);
                        friendshipRepository.save(f);
                    }
                }
            } catch (Exception e) {
                // Handle exceptions here (e.g., database connection issues)
                e.printStackTrace(); // Print the exception details (you might want to log it instead)
            }
        }

        return "profile";
    }

    @PostMapping("/removeFriend")
    @ResponseBody
    public String RemoveFriend(@RequestBody Map<String, Object> requestBody) {
        Object idObject = requestBody.get("Id");
        int friendUserId = 0;
        if (idObject != null) {
            friendUserId = Integer.parseInt((String) idObject);
            Optional<FriendShip> friendShip1 = friendshipRepository.findById(friendUserId);
            if (friendShip1.isPresent()) {
                FriendShip f = friendShip1.get();
                f.setStatus("unfriend");
                friendshipRepository.save(f);
            }
            FriendShip friend1 = friendShip1.get();
            Optional<FriendShip> friendShip2;
            try {
                friendShip2 = friendshipRepository.findById(friendUserId + 1);
                if (friendShip2.isPresent()) {
                    FriendShip friend2 = friendShip2.get();
                    if (friend1.getFriendUser().getUser_id() == friend2.getMyUser().getUser_id()
                            && friend1.getMyUser().getUser_id() == friend2.getFriendUser().getUser_id()) {
                        if (friendShip2.isPresent()) {
                            FriendShip f = friendShip2.get();
                            f.setStatus("unfriend");
                            friendshipRepository.save(f);
                        }
                    } else {
                        Optional<FriendShip> friendShip3 = friendshipRepository.findById(friendUserId - 1);
                        if (friendShip3.isPresent()) {
                            FriendShip f = friendShip3.get();
                            f.setStatus("unfriend");
                            friendshipRepository.save(f);
                        }
                    }
                } else {
                    Optional<FriendShip> friendShip3 = friendshipRepository.findById(friendUserId - 1);
                    if (friendShip3.isPresent()) {
                        FriendShip f = friendShip3.get();
                        f.setStatus("unfriend");
                        friendshipRepository.save(f);
                    }
                }
            } catch (Exception e) {
                // Handle exceptions here (e.g., database connection issues)
                e.printStackTrace(); // Print the exception details (you might want to log it instead)
            }
        }
        return "profile";
    }

    // Hiển thị form chỉnh sửa
    @GetMapping("/us/edit/{id}")
    public String usEditForm(Model model, @PathVariable int id) {
        // Lấy user theo id
        Optional<Users> us = usersRepository.findById(id);

        if (us.isPresent()) {
            model.addAttribute("user", us.get());
        } else {
            // Xử lý trường hợp không tìm thấy user
            return "redirect:/login";
        }

        return "profile_edit";
    }

    private void deleteImageFromStorage(String imageUrl) {
        if (imageUrl != null) {
            // Lấy tên tệp tin từ URL
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            // Xác định đường dẫn của tệp hình ảnh
            Path imagePath = Path.of("src/main/resources/static/images/img_avt/" + fileName);

            // Kiểm tra xem tệp tồn tại trước khi xóa
            if (Files.exists(imagePath)) {
                try {
                    // Xóa hình ảnh từ thư mục lưu trữ
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Async
    public CompletableFuture<String> saveImage(MultipartFile file, String username) throws IOException {
        if (!file.isEmpty()) {
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            String originalFilename = file.getOriginalFilename();
            String fileName = username + "_" + timestamp + "_" + originalFilename;
            Path imagePath = Path.of("src/main/resources/static/images/img_avt/" + fileName);
            Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            return CompletableFuture.completedFuture(fileName);
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    @PostMapping("/checkPassword")
    public ResponseEntity<String> checkPassword(@RequestParam String oldPassword, @RequestParam String newPassword, HttpServletRequest request) {
        // Lấy thông tin người dùng 
        Cookie[] cookies = request.getCookies();
        String userId = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("userid")) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }
        Optional<Users> us = usersRepository.findById(Integer.valueOf(userId));
        Users user = us.get();
        // Kiểm tra mật khẩu cũ
        if (user.getPassword().equals(oldPassword)) {
            // Nếu mật khẩu cũ đúng kiểm tra mật khẩu mới
            String password = newPassword;
            boolean hasLowerCase = password.matches(".*[a-z].*");
            boolean hasUpperCase = password.matches(".*[A-Z].*");
            boolean hasDigit = password.matches(".*[0-9].*");
            boolean hasSpecialChar = password.matches(".*[^a-zA-Z0-9].*");
            if (password.length() < 8 || !hasLowerCase || !hasUpperCase || !hasDigit || !hasSpecialChar) {
                return ResponseEntity.ok("newpassno");
            } else {
                return ResponseEntity.ok("Pass");
                //return ResponseEntity.ok("Password must have at least 8 characters, including uppercase, lowercase, digit and special character!");
            }
        } else {
            return ResponseEntity.ok("oldpassno");
        }
    }

    @PostMapping("/us/edit/{id}")
    public String editProfile(@ModelAttribute Users user,
            @RequestParam(name = "fileInput", required = false) MultipartFile newAvatar,
            @RequestParam("new_name") String newName, Model model,
            @RequestParam("newpass") String newpass,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Lấy thông tin user từ cơ sở dữ liệu
        Optional<Users> existingUser = usersRepository.findById(user.getUser_id());

        if (existingUser.isPresent()) {
            // Cập nhật thông tin user
            Users updatedUser = existingUser.get();
            String fullName = user.getFull_name();
            if (fullName == null) {
                fullName = "";  // hoặc một giá trị mặc định nào đó
            }
            updatedUser.setFull_name(newName);

            // Kiểm tra xem avt user có phải là user1.png
            if (!newAvatar.isEmpty()) {
                if (!"user1.png".equals(updatedUser.getAvata_url())) {
                    // xóa avatar trong mục trên máy chủ
                    deleteImageFromStorage(existingUser.get().getAvata_url());
                }

                //lưu new avt vào máy 
                String Username = updatedUser.getUsername();
                CompletableFuture<String> newAvt = saveImage(newAvatar, Username);

                String imageUrl;
                try {
                    imageUrl = newAvt.get(); // Đợi cho đến khi ảnh được lưu xong
                } catch (Exception e) {
                    // Xử lý ngoại lệ
                    imageUrl = null;
                }

                if (imageUrl != null) {
                    updatedUser.setAvata_url(imageUrl);
                } else {
                    updatedUser.setAvata_url(null);
                }
            }
            // kiểm tra new pass nếu có
            if (!newpass.isEmpty()) {
                updatedUser.setPassword(newpass);
            }
            // Lưu thông tin user đã cập nhật vào cơ sở dữ liệu
            usersRepository.save(updatedUser);

        } else {
            // Nếu không tìm thấy user, chuyển hướng về trang đăng nhập
            return "redirect:/login";
        }

        return "redirect:/us/profile/" + existingUser.get().getUser_id();
    }

    @GetMapping("/us/delete/{id}")
    public String deleteus(@PathVariable int id) {
        //lay user theo id
        Optional<Users> user = usersRepository.findById(id);
        usersRepository.delete(user.get());

        return "redirect:/allus";
    }

    //AJAX TNGUYEN
    @GetMapping("/all/users")
    @ResponseBody
    public ArrayList<Users> getAllUsers() {
        Iterable<Users> ls = usersRepository.findAll();

        return (ArrayList<Users>) ls;
    }

    //AJAX TNGUYEN
    public List<PostInfo> getAllPosst(int userId) {

        Optional<Users> us = usersRepository.findById(userId);
        //kiểm tra xem có tồn tại user
        if (us.isPresent()) {
            Users currentUser = us.get();
            List<Post> allPost = new ArrayList<>();
            // sử dụng set để lấy bài đăng không trùng 
            Set<Post> postSet = new HashSet<>();

            postSet.addAll(postRepository.findMyUserPost(currentUser.getUser_id()));
            postSet.addAll(postRepository.findSharePosts(currentUser.getUser_id()));

            allPost.addAll(postSet);

            //lấy tất cả comment
            Iterable<Comment> co = commentRepository.findAll();
            for (Post post : allPost) {
                // Lấy 2 comment mới nhất cho mỗi bài đăng
                List<Comment> comments = commentRepository.findTop2ByPostOrderByCommentDateDesc(post);
                //System.out.println();
                post.setComments(comments);

            }

            // Sắp xếp danh sách bài đăng theo thời gian
            allPost.sort((p1, p2) -> p2.getPostDate().compareTo(p1.getPostDate()));

            List<PostInfo> allPostInfo = new ArrayList<>();
            // Create PostInfo objects and add them to allPostInfo

            for (Post post : allPost) {
                PostInfo postInfo = new PostInfo();
                postInfo.setPost(post);

                allPostInfo.add(postInfo);
            }

            for (PostInfo post : allPostInfo) {
                //lấy số lượng like
                int amountLikes = 0;
                for (Likes l : likesRepository.findAllByLikedPost(post.getPost())) {
                    if (l.getStatus().equals("YES")) {
                        amountLikes++;
                    }
                }
                for (Likes l : likesRepository.findAllByLikedPost(post.getPost())) {
                    // kiểm tra xem users có like bài viết chưa
                    if (l.getLikedUser().getUser_id().equals(currentUser.getUser_id()) && l.getStatus().equals("YES")) {
                        post.setBlLike(true);
                        break;
                    } else {
                        post.setBlLike(false);
                    }
                }
                post.setAmountLike(amountLikes);

                // lấy số lượng comment
                List<Comment> comments = commentRepository.findAllByPost(post.getPost());
                int amountComments = 0;
                for (Comment c : comments) {
                    amountComments++;
                }
                post.setAmountComment(amountComments);

                //lấy số lượng share
                int amountShares = 0;
                for (Share s : shareRepository.findAllByPost(post.getPost())) {
                    amountShares++;
                }
                post.setAmountShare(amountShares);
            }

            return allPostInfo;
        }

        return null;
    }
}
