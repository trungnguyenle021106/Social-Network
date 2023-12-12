/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.entity.Likes;
import com.example.demo.entity.Post;
import com.example.demo.entity.Users;
import com.example.demo.repository.LikesRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UsersRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Admin
 */
@Controller
public class LikesController {

    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/likes")
    public String getAll(Model m) {
        Iterable<Likes> ls = likesRepository.findAll();

//        for (Likes like : ls) {
//            System.out.println(like.getLikedUser().getFull_name());
//            System.out.println(like.getStatus());
//            System.out.println(like.getLikedPost().getPost_id());
//            like.getLike_id();
//            
//        }
        m.addAttribute("list", ls);
        return "likes";
    }

    //AJAX
    @GetMapping("/likes/addLike")
    @ResponseBody
    public List<Likes> addLike(@RequestParam("post_id") int postID, @RequestParam("user_id") int user_id) {
        Optional<Post> post = postRepository.findById(postID);
        Optional<Users> user = usersRepository.findById(user_id);
        Likes newlike = new Likes();

        newlike.setLikedPost(post.get());
        newlike.setLikedUser(user.get());
        newlike.setStatus("YES");
        newlike.setLike_date(String.valueOf(java.time.LocalDate.now()));
        likesRepository.save(newlike);

        List<Likes> likes = likesRepository.findAllByLikedPost(post.get());

        return likes;
    }

    @GetMapping("/likes/enable_disable_Like")
    @ResponseBody
    public List<Likes> enable_disable_Like(@RequestParam("post_id") int postID, @RequestParam("user_id") int myUserID,
            @RequestParam("likeID") int likeID, @RequestParam("status") String status) {
        Optional<Post> post = postRepository.findById(postID);
        Optional<Likes> like = likesRepository.findById(likeID);
        like.get().setStatus(status);
        like.get().setLike_date(String.valueOf(java.time.LocalDate.now()));

        likesRepository.save(like.get());

        List<Likes> likes = likesRepository.findAllByLikedPost(post.get());

        return likes;
    }
    //AJAX

    @GetMapping("likes/add/{post_id}")
    public String addlike(@PathVariable("post_id") int postID, Model M) {
        Optional<Post> p = postRepository.findById(postID);

        Likes newlike = new Likes();

        newlike.setLikedPost(p.get());
        newlike.setLikedUser(p.get().getMyUser());
        newlike.setStatus("YES");
        newlike.setLike_date(String.valueOf(java.time.LocalDate.now()));
        likesRepository.save(newlike);

        return "redirect:/post";
    }

    @GetMapping("/likes/enablelike/{id}")
    public String enableLike(@PathVariable int id) {
        //lay like theo id

        Optional<Likes> like = likesRepository.findById(id);
        if (like != null) {

            Likes liked = like.get();
            liked.setStatus("YES");
            //lưu lại vào database
            likesRepository.save(liked);
        }

        return "redirect:/likes";
    }

    @GetMapping("/likes/disablelike/{id}")
    public String disableLike(@PathVariable int id) {
        //lay like theo id

        Optional<Likes> like = likesRepository.findById(id);
        if (like != null) {
            //tao moi us và gán cho
            Likes liked = like.get();
            liked.setStatus("NO");
            //lưu lại vào database
            likesRepository.save(liked);
        }

        return "redirect:/likes";
    }

}
