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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/homes")
    public String Index(Model m, HttpServletResponse response, HttpServletRequest request) {
        if (request.getSession().getAttribute("userid") == null) {
            return "redirect:/login";
        }
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

        // Kiểm tra xem cookie có tồn tại không
        if (userId == null) {
            // Không tìm thấy cookie, chuyển hướng người dùng về trang đăng nhập
            return "redirect:/login";
        }
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0); // Proxies.

        //lấy đối tượng user
        Optional<Users> us = usersRepository.findById(Integer.valueOf(userId));
        List<PostInfo> allPost = getAllPosst(us.get().getUser_id());

        //lấy <=5 post đầu tiên
        int endIndex = Math.min(5, allPost.size());
        List<PostInfo> Post5 = allPost.subList(0, endIndex);
        System.out.println(endIndex);
        Users uus = us.get();

        // Danh sách bạn bè
        List<FriendShip> friendList = friendshipRepository.findByMyUserAndStatus(Integer.valueOf(userId), "accepted");
        // Danh sách lời mời
        List<FriendShip> inviteList = friendshipRepository.findByMyFriendAndStatus(Integer.valueOf(userId), "pending");
        // Danh sách gợi ý
            List <FriendShip> removeList = friendshipRepository.findBy2Status("accepted","pending");
            Iterable <Users> userList = usersRepository.findAll();
            Iterator <Users> iterator = userList.iterator();
            List<Users> newUserList = new ArrayList<>();
            //add danh sach
            int user = Integer.parseInt(userId);
            List<Integer> integerList = new ArrayList<>();
            for (FriendShip f:removeList){
                if(user==f.getMyUser().getUser_id())
                    integerList.add(f.getFriendUser().getUser_id());
                if(user==f.getFriendUser().getUser_id())
                    integerList.add(f.getMyUser().getUser_id());
            }
            while (iterator.hasNext()) {
                Users users = iterator.next();
                boolean flag = true;
                if(user == users.getUser_id())
                    flag = false;
                else{
                    for(int i:integerList){
                        if(users.getUser_id()==i){
                            flag = false;
                            break;
                        }
                    }
                }
                if(flag){
                    newUserList.add(users);
                }
            }
            while (iterator.hasNext()) {
                Users users = iterator.next();
                System.out.println(users.getUser_id());
            }
        m.addAttribute("friendList", friendList);
        m.addAttribute("inviteList", inviteList);
        m.addAttribute("user_login", uus);
        m.addAttribute("posts", Post5);
        m.addAttribute("userList" ,newUserList);
        Cookie cookie = new Cookie("userid", userId);
        // Đặt thời gian sống của cookie (ví dụ: 10 giờ)
        cookie.setMaxAge(36000);
        response.addCookie(cookie);

        return "home";
    }

    @GetMapping("/loadMorePosts")
    public String loadMorePosts(@RequestParam("userId") Integer userId,
            @RequestParam("limit") Integer limit,
            @RequestParam("offset") Integer offset,
            Model model) {

        Optional<Users> us = usersRepository.findById(userId);
        List<PostInfo> allPost = getAllPosst(us.get().getUser_id());
        //List<List<Integer>> amountLSC = getAmountLikeShareComment(allPost);

        //lấy 5 post tt
        int endIndex = Math.min(offset + limit, allPost.size());
        List<PostInfo> post5next = allPost.subList(offset, endIndex);

        Users uus = us.get();
        model.addAttribute("user_login", uus);
        model.addAttribute("posts", post5next);
        //model.addAttribute("amountLSC", amountLSC);

        // Trả về một trang HTML mới chứa các bài viết
        return "fragments/post-list :: postList";
    }

    @PostMapping("/accpectInviteFromHome")
    @ResponseBody
    public String AcceptInvitation(@RequestBody Map<String, Object> requestBody){
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
                    if(friend1.getFriendUser().getUser_id()==friend2.getMyUser().getUser_id()
                        && friend1.getMyUser().getUser_id()==friend2.getFriendUser().getUser_id()){
                        if (friendShip2.isPresent()) {
                            FriendShip f = friendShip2.get();
                            f.setStatus(choice);
                            friendshipRepository.save(f);
                        }
                    }
                    else{
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
        
        return "home";
    }
    
    @PostMapping("/sendFriendRequest")
    @ResponseBody
    public String SendFriendRequest(@RequestBody Map<String, Object> requestBody){
        Object idUser = requestBody.get("IdUser");
        Object idFriend = requestBody.get("IdFriend");
        int userId = Integer.parseInt((String) idUser);
        int friendId = Integer.parseInt((String) idFriend);
        System.out.println(userId +"/"+ friendId);
        List <FriendShip> friendRe = friendshipRepository.findByMyUserAndMyFriend(Integer.valueOf(userId), Integer.valueOf(friendId));
        if (friendRe.size() == 0){
            Optional<Users> user = usersRepository.findById(userId);
            Optional<Users> friend = usersRepository.findById(friendId);
            FriendShip f1 = new FriendShip();
            FriendShip f2 = new FriendShip();
            Users flag1 = user.get();
            Users flag2 = friend.get();
            f1.setMyUser(flag1);
            f1.setFriendUser(flag2);
            f1.setStatus("pending");
            friendshipRepository.save(f1);
            f2.setMyUser(flag2);
            f2.setFriendUser(flag1);
            f2.setStatus("unfriend");
            friendshipRepository.save(f2);
        }
        else{
            FriendShip f = new FriendShip();
            for (FriendShip i : friendRe) {
                f.setFriendship(i.getFriendship_id());
                f.setMyUser(i.getMyUser());
                f.setFriendUser(i.getFriendUser());
                f.setStatus("pending");
            }
            friendshipRepository.save(f);
        }
            
        return "home";
    }
    
    public List<PostInfo> getAllPosst(int userId) {

        Optional<Users> us = usersRepository.findById(userId);
        //kiểm tra xem có tồn tại user
        if (us.isPresent()) {
            Users currentUser = us.get();
            List<Post> allPost = new ArrayList<>();
            // sử dụng set để lấy bài đăng không trùng 
            Set<Post> postSet = new HashSet<>();

            postSet.addAll(postRepository.findMyUserPost(currentUser.getUser_id()));
            postSet.addAll(postRepository.findFriendPosts(currentUser.getUser_id()));
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
