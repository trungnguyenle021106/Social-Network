/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.entity.Post;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Users;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.UsersRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author asus
 */
@Controller
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PostRepository postRepository;
    

    @GetMapping("/comment")
    public String getAll(Model m) {
        Iterable<Comment> ls = commentRepository.findAll();
        m.addAttribute("list", ls);
        return "comment";
    }

    
    
    
    
    //XỬ LÝ AJAX
    @GetMapping("/comment/add")
    @ResponseBody
    public List<Comment> addCommemt(@RequestParam("post_id") int postID, @RequestParam("user_id") int myUserID,
            @RequestParam("content") String content) {
        Comment ncmt = new Comment();
        ncmt.setPost(getPost(postID));
        ncmt.setMyUser(getUser(myUserID));
        ncmt.setContent(content);
        java.util.Date utilDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        ncmt.setCommentDate(sqlDate);
        commentRepository.save(ncmt);

        Optional<Post> post = postRepository.findById(postID);
        List<Comment> comments = commentRepository.findAllByPost(post.get());
          
        
        return comments;
    }
    
    
    @GetMapping("/comment/delete")
    @ResponseBody
    public List<Comment> deleteComment(@RequestParam("comment_id") int comment_id, @RequestParam("post_id") int postID) {
        //lay cmt theo id
        Optional<Comment> cmt = commentRepository.findById(comment_id);
        commentRepository.delete(cmt.get());

        
        Optional<Post> post = postRepository.findById(postID);
        List<Comment> comments = commentRepository.findAllByPost(post.get());
        
        return comments;
    }
    
    @GetMapping("/comment/edit/")
    @ResponseBody
    public List<Comment> editComment(@RequestParam("comment_id") int comment_id, @RequestParam("post_id") int postID,
    @RequestParam("editedComment") String editedComment) {
        //lay cmt theo id
        Optional<Comment> cmt = commentRepository.findById(comment_id);
        cmt.get().setContent(editedComment);
        java.util.Date utilDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        cmt.get().setCommentDate(sqlDate);
        commentRepository.save(cmt.get());
        
        Optional<Post> post = postRepository.findById(postID);
        List<Comment> comments = commentRepository.findAllByPost(post.get());
        
        return comments;
    }
    //XỬ LÝ AJAX

    
    
    
    
    
    
    @GetMapping("/comment/edit/{id}")
    public String cmtEditForm(Model model, @PathVariable int id) {
        //lay cmt theo id
        Optional<Comment> cmt = commentRepository.findById(id);
        model.addAttribute("comment", cmt.orElse(new Comment()));

        return "comment_edit";
    }

    @PostMapping("/comment/edit/{id}")
    public String editcmt(@ModelAttribute Comment cmtedit) {

        Optional<Comment> cmt = commentRepository.findById(cmtedit.getComment_id());

        if (cmt != null) {
            Comment ecmt = cmt.get();
            Post po = new Post();
            po.setPost_id(cmtedit.getPost().getPost_id());
            ecmt.setPost(po);
            Users us = new Users();
            us.setUser_id(cmtedit.getMyUser().getUser_id());
            ecmt.setMyUser(us);
            ecmt.setContent(cmtedit.getContent());
            ecmt.setCommentDate(cmtedit.getCommentDate());
            commentRepository.save(ecmt);
        }
        return "redirect:/comment";
    }

    @GetMapping("/comment/delete/{id}")
    public String deleteCmt(@PathVariable int id) {
        //lay cmt theo id
        Optional<Comment> cmt = commentRepository.findById(id);
        commentRepository.delete(cmt.get());

        return "redirect:/comment";
    }

    private Users getUser(int id) {
        Iterable<Users> ls = usersRepository.findAll();
        for (Users u : ls) {
            if (u.getUser_id() == id) {
                return u;
            }
        }
        return null;
    }

    private Post getPost(int id) {
        Iterable<Post> ls = postRepository.findAll();
        for (Post p : ls) {
            if (p.getPost_id() == id) {
                return p;
            }
        }
        return null;
    }

       

    
}