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
import com.example.demo.model.FriendShare;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.FriendshipRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.example.demo.repository.LikesRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.ShareRepository;
import com.example.demo.repository.UsersRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PostController {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private ShareRepository shareRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;

    @GetMapping("/post")
    public String getAll(Model m) {
        Iterable<Post> ls = postRepository.findAll();
        m.addAttribute("list", ls);
        return "post";
    }

    @GetMapping("/post/add")
    public String addPost(Model m, HttpServletRequest request) {

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
        user.getAvata_url();
        System.out.println(user.getFull_name());

        m.addAttribute("user", us);

        return "post_create";
    }

    @Async
    public CompletableFuture<String> saveImage(MultipartFile file, String username) throws IOException {
        if (!file.isEmpty()) {
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            String originalFilename = file.getOriginalFilename();
            String fileName = username + "_" + timestamp + "_" + originalFilename;
            Path imagePath = Path.of("src/main/resources/static/images/img_post/" + fileName);
            Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            return CompletableFuture.completedFuture(fileName);
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    @PostMapping("/post/add")
    public String addPost(
            @RequestParam("file") MultipartFile file,
            @RequestParam("content") String content,
            @RequestParam("user_id") Integer userId) throws IOException {

        Post po = new Post();
        po.setContent(content);

        Optional<Users> users = usersRepository.findById(userId);
        Users user = users.orElseThrow(() -> new RuntimeException("User not found"));
        user.setUser_id(userId);
        po.setMyUser(user);
        String username = user.getUsername();

        CompletableFuture<String> imageUrlFuture = saveImage(file, username);

        // Làm một số công việc khác hoặc đợi cho đến khi ảnh được lưu xong
        String imageUrl;
        try {
            imageUrl = imageUrlFuture.get(); // Đợi cho đến khi ảnh được lưu xong
        } catch (Exception e) {
            // Xử lý ngoại lệ nếu có
            imageUrl = null;
        }

        if (imageUrl != null) {
            po.setUrl(imageUrl);
        } else {
            po.setUrl(null);
        }

        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        po.setPostDate(currentTimestamp);

        postRepository.save(po);

        return "redirect:/homes";
    }

    private void deleteImageFromStorage(String imageUrl) {
        if (imageUrl != null) {
            // Lấy tên tệp tin từ URL
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            // Xác định đường dẫn của tệp hình ảnh
            Path imagePath = Path.of("src/main/resources/static/images/img_post/" + fileName);

            // Kiểm tra xem tệp tồn tại trước khi xóa
            if (Files.exists(imagePath)) {
                try {
                    System.out.println("---------------------------------------------------------------------------------------------------------------");
                    // Xóa hình ảnh từ thư mục lưu trữ
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Hiển thị form chỉnh sửa
    @GetMapping("/post/edit/{id}")
    public String usEditForm(Model model, @PathVariable int id) {
        //lay user theo id
        Optional<Post> po = postRepository.findById(id);
        po.get().getUrl();

        Optional<Users> us = usersRepository.findById(po.get().getMyUser().getUser_id());

        model.addAttribute("user", us);
        model.addAttribute("post", po.get());

        return "post_edit";
    }

    //thuc hien update user
    @PostMapping("/post/edit/{id}")
    public String editPost(@ModelAttribute Post postedit,
            @RequestParam("fileInput") MultipartFile file) throws IOException {
        Optional<Post> optionalPost = postRepository.findById(postedit.getPost_id());

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setContent(postedit.getContent());

            Optional<Users> optionalUser = usersRepository.findById(postedit.getMyUser().getUser_id());
            Users user = optionalUser.orElse(null);

            if (user != null) {
                String username = user.getUsername();

                // Kiểm tra xem có file mới được chọn hay không
                if (!file.isEmpty()) {
                    // Xóa file cũ
                    String imgOld = post.getUrl();
                    deleteImageFromStorage(imgOld);

                    // Lưu file mới và nhận CompletableFuture
                    CompletableFuture<String> imageUrlFuture = saveImage(file, username);

                    // Thực hiện công việc khác hoặc đợi cho đến khi ảnh được lưu xong
                    try {
                        String imageUrl = imageUrlFuture.get(); // Đợi cho đến khi ảnh được lưu xong
                        post.setUrl(imageUrl);
                    } catch (Exception e) {
                        e.printStackTrace(); // Xử lý ngoại lệ
                    }
                }

                // Lưu lại vào database
                postRepository.save(post);
            }
        }

        return "redirect:/homes";
    }

    //TEST XỬ LÝ
    @GetMapping("/post/detail/{id}")
    public String getPostDetail(Model model,
            @PathVariable int id,
            @CookieValue(name = "userid", required = false) String usid) {
        int post_id = id;
        int user_id = Integer.parseInt(usid);

        Optional<Post> post = postRepository.findById(post_id);

        Optional<Users> myUser = usersRepository.findById(user_id);
        List<FriendShip> friendships = friendshipRepository.findByMyUserAndStatus(user_id, "accepted");
        List<Share> shares = shareRepository.findAllByPost(post.get());
        ArrayList<FriendShare> list_fshare = getListFSHARE(friendships, post.get());

        Likes like = null;
        for (Likes l : likesRepository.findAllByLikedPost(post.get())) {
            if (l.getLikedUser().getUser_id() == user_id) {
                like = l;
            }
        }

        int amountLikes = 0;
        for (Likes l : likesRepository.findAllByLikedPost(post.get())) {
            if (l.getStatus().equals("YES")) {
                amountLikes++;
            }
        }

        List<Comment> comments = commentRepository.findAllByPost(post.get());
        int amountComments = 0;
        for (Comment c : comments) {
            amountComments++;
        }

        int amountShares = 0;
        for (Share s : shareRepository.findAllByPost(post.get())) {
            amountShares++;
        }

        for (Share s : shares) {
            if (s.getMy_user_id() == user_id) {
                for (FriendShare fs : list_fshare) {
                    if (fs.getFriendUser().getUser_id() == s.getFriend_user_id()) {
                        fs.setIsShared(true);
                    }
                }
            }

        }

        model.addAttribute("comments", comments);
        model.addAttribute("myUser", myUser.get());
        model.addAttribute("post", post.get());
        model.addAttribute("like", like);
        model.addAttribute("list_fshare", list_fshare);
        model.addAttribute("amountLikes", amountLikes > 0 ? amountLikes : "");
        model.addAttribute("amountComments", amountComments > 0 ? amountComments : "");
        model.addAttribute("amountShares", amountShares > 0 ? amountShares : "");
        return "post_detail";
    }

    private ArrayList<FriendShare> getListFSHARE(List<FriendShip> friendShips, Post post) {
        ArrayList<FriendShare> friendShares = new ArrayList<>();

        for (FriendShip f : friendShips) {
            if (f.getFriendUser().getUser_id() != post.getMyUser().getUser_id()) {
                FriendShare fshare = new FriendShare();
                fshare.setFriendUser(f.getFriendUser());
                fshare.setIsShared(false);
                friendShares.add(fshare);
            }

        }
        if (friendShares != null) {
            return friendShares;
        }
        return null;

    }

    //TEST XỬ LÝ 
    //AJAX
    @GetMapping("/post/delete")
    @ResponseBody
    public String delPost(@RequestParam("post_id") int postID, HttpServletResponse response) {
        //lay user theo id
        Optional<Post> po = postRepository.findById(postID);
        if (po.isPresent()) {

            likesRepository.deleteByPostId(postID);

            shareRepository.deleteSharesByPostId(postID);

            commentRepository.deleteByPostId(postID);

            postRepository.delete(po.get());

            String imageUrl = po.get().getUrl();
            deleteImageFromStorage(imageUrl);
        }
        postRepository.delete(po.get());

        return "{\"redirect\": \"/homes\"}";
    }
    //AJAX

}
