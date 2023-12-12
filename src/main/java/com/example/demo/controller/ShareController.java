/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.entity.Post;
import com.example.demo.entity.Share;
import com.example.demo.entity.Users;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.ShareRepository;
import com.example.demo.repository.UsersRepository;
import java.sql.Date;
import java.util.ArrayList;
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
 * @author Nguyen Le
 */
@Controller
public class ShareController {

    @Autowired
    private ShareRepository shareRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/Share")
    public String getAll(Model m) {
        Iterable<Share> ls = shareRepository.findAll();
        m.addAttribute("list", ls);
        return "share";
    }

    @PostMapping("/Share/add")
    public String add(@RequestParam("post_id") int postID, @RequestParam("my_user_id") int myUserID,
            @RequestParam("friend_user_id") int friendUserID, @RequestParam("share_date") Date shareDate) {

        Share newshare = new Share();
        newshare.setPost(getPost(postID));
        newshare.setMyUser(getUser(myUserID));
        newshare.setFriendUser(getFriendUser(friendUserID));
        newshare.setShare_date(shareDate);
        shareRepository.save(newshare);

        return "redirect:/Share";
    }

    @PostMapping("/Share/delete/{id}")
    public String delete(@PathVariable int id) {
        //lay user theo id
        Optional<Share> share = shareRepository.findById(id);
        shareRepository.delete(share.get());

        return "redirect:/Share";
    }

    @GetMapping("/Share/edit/{id}")
    public String editFrom(Model model, @PathVariable int id) {
        Optional<Share> share = shareRepository.findById(id);
        model.addAttribute("share", share);
        return "share_edit";
    }

    @PostMapping("/Share/edit/{id}")
    public String edit(@ModelAttribute Share share_edit) {
        Optional<Share> shares = shareRepository.findById(share_edit.getShare_id());

        if (shares != null) {
            Share share = shares.get();

            share.setPost(share_edit.getPost());

            Users us1 = new Users();
            us1.setUser_id(share_edit.getMyUser().getUser_id());
            share.setMyUser(us1);
            
            Users us2 = new Users();
            us2.setUser_id(share_edit.getFriendUser().getUser_id());
            share.setFriendUser(us2);

            share.setShare_date(share_edit.getShare_date());
            shareRepository.save(share);
        }

        return "redirect:/Share";
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

    private Users getUser(int id) {
        Iterable<Users> ls = usersRepository.findAll();
        for (Users u : ls) {
            if (u.getUser_id() == id) {
                return u;
            }
        }
        return null;
    }

    private Users getFriendUser(int id) {
        Iterable<Users> ls = usersRepository.findAll();
        for (Users u : ls) {
            if (u.getUser_id() == id) {
                return u;
            }
        }
        return null;
    }
    
     //AJAX
    @GetMapping("/share/operator")
    @ResponseBody
    public int ShareOperator(
            @RequestParam("post_id") int postID,
            @RequestParam("listFriendUserID") String listFriendUserID,
            @RequestParam("user_id") int user_id) {

        System.out.println(listFriendUserID.equals("null"));
        Optional<Post> post = postRepository.findById(postID);
        List<Share> shares = shareRepository.findAllByPost(post.get());
        ArrayList<Share> ShareForMyFriend = new ArrayList<Share>();

        // SHARE CỦA USER NÀY
        for (Share s : shares) {
            if (s.getMy_user_id() == user_id) {
                ShareForMyFriend.add(s);
            }
        }
        
        if (ShareForMyFriend == null && listFriendUserID.equals("null") == false) {
            
            // Loại bỏ các ký tự [ và ] từ chuỗi
            String cleanedString = listFriendUserID.replaceAll("[\\[\\]\"]", "");

// Chia chuỗi thành các phần tử dựa trên dấu phẩy (,)
            String[] elements = cleanedString.split(",");
            List<Integer> listFriendIDShare = new ArrayList<>();
            for (String element : elements) {
                String cleanElement = element.replace("\"", ""); // Loại bỏ các dấu ngoặc kép trong phần tử
                int number = Integer.parseInt(cleanElement.trim());
                listFriendIDShare.add(number);
            }
            for (int i : listFriendIDShare) {
                Share newshare = new Share();
                newshare.setPost(getPost(postID));
                newshare.setMyUser(getUser(user_id));
                newshare.setFriendUser(getFriendUser(i));
                java.util.Date utilDate = new java.util.Date();
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                newshare.setShare_date(sqlDate);
                shareRepository.save(newshare);
            }
        } 
        else if (listFriendUserID.equals("null") == false && ShareForMyFriend != null) {
            
            // Loại bỏ các ký tự [ và ] từ chuỗi
            String cleanedString = listFriendUserID.replaceAll("[\\[\\]\"]", "");

// Chia chuỗi thành các phần tử dựa trên dấu phẩy (,)
            String[] elements = cleanedString.split(",");

// Tạo danh sách số nguyên và chuyển đổi từng phần tử thành số nguyên
            List<Integer> listFriendIDShare = new ArrayList<>();
            
            
            for (String element : elements) {
                String cleanElement = element.replace("\"", ""); // Loại bỏ các dấu ngoặc kép trong phần tử
                int number = Integer.parseInt(cleanElement.trim());
                listFriendIDShare.add(number);
                
            }
            
            ArrayList<Integer> addShareList = new ArrayList<Integer>(listFriendIDShare);
            
            for (Share s : ShareForMyFriend) {
                if (listFriendIDShare.contains(s.getFriend_user_id()) == true) {
                    addShareList.remove(s.getFriend_user_id());
                }
            }
            
            for (int i : addShareList) {
                Share newshare = new Share();
                newshare.setPost(getPost(postID));
                newshare.setMyUser(getUser(user_id));
                newshare.setFriendUser(getFriendUser(i));
                java.util.Date utilDate = new java.util.Date();
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                newshare.setShare_date(sqlDate);
                shareRepository.save(newshare);
            }
            
            for (Share s : ShareForMyFriend) {
                if (listFriendIDShare.contains(s.getFriend_user_id()) == false) {
                    shareRepository.delete(s);
                }
            }
        } else if (listFriendUserID.equals("null") == true && ShareForMyFriend != null) {
            
            for (Share s : ShareForMyFriend) {
                shareRepository.delete(s);
            }
        }
        shares = shareRepository.findAllByPost(post.get());
        return shares.size();
    }

    //AJAX
}
