/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.entity.FriendShip;
import com.example.demo.entity.Share;
import com.example.demo.entity.Users;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.ShareRepository;
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
 * @author Nguyen
 */
@Controller
public class FriendShipController {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ShareRepository shareRepository;
    
    @GetMapping("/friendship")
    public String getAll(Model m) {
        Iterable<FriendShip> ls = friendshipRepository.findAll();

        for (FriendShip fs : ls) {
            System.out.println(fs.getMyUser().getFull_name());
            System.out.println(fs.getFriendUser().getFull_name());
        }
        m.addAttribute("list", ls);

        return "friendship";
    }

    @GetMapping("/friends/{userId}")
    public String getListFriends(@PathVariable Integer userId, Model model) {
        Users user = usersRepository.findById(userId).orElse(null);
        Iterable<Users> userList = usersRepository.findAll();

        if (user != null) {
            List<FriendShip> friendList = friendshipRepository.findByMyUserAndStatus(user.getUser_id(), "accepted");
            for (FriendShip fs : friendList) {
                System.out.println(fs.getMyUser().getFull_name());
                System.out.println(fs.getFriendUser().getFull_name());
            }

            model.addAttribute("friends", friendList);
            model.addAttribute("userlist", userList);
            model.addAttribute("userId", userId);
            return "friendship";
        } else {
            return "userNotFound";
        }
    }

//kết chấp nhận kết bạn
    @GetMapping("/friends/add/{userId}")
    public String addFriend(@PathVariable Integer userId,
            @RequestParam Integer friendId) {
        //tạo mới 2 bảng friendship
        FriendShip friendShip = new FriendShip();
        FriendShip friendShip2 = new FriendShip();
        //tạo user
        Users users = new Users();
        Users friend = new Users();
        //gán us truyền vào cho user vưa tạo
        users.setUser_id(userId);
        friend.setUser_id(friendId);

        //lập bảng người dùng mời đã dc chấp nhận
        friendShip.setMyUser(users);
        friendShip.setFriendUser(friend);
        friendShip.setStatus("pending");

        // lập bảng người chấp nhận lời mời
        friendShip2.setMyUser(friend);
        friendShip2.setFriendUser(users);
        friendShip2.setStatus("unfriend");

        friendshipRepository.save(friendShip);
        friendshipRepository.save(friendShip2);

        return "redirect:/friends/" + userId;
    }

    @GetMapping("/friends/unfriend/{userId}")
    public String editFriend(@PathVariable Integer userId, @RequestParam Integer friendId) {

        Optional<FriendShip> f = friendshipRepository.findById(friendId);
        FriendShip friend = f.get();
        friend.setStatus("unfriend");

        friendshipRepository.save(friend);

        return "redirect:/friends/" + userId;
    }

    
    private void newFriendShip(int userId,int friendId)
    {
        FriendShip friendShip = new FriendShip();
        FriendShip friendShip2 = new FriendShip();
        //tạo user
        Users users = new Users();
        Users friend = new Users();
        //gán us truyền vào cho user vưa tạo
        users.setUser_id(userId);
        friend.setUser_id(friendId);

        //lập bảng người dùng mời đã dc chấp nhận
        friendShip.setMyUser(users);
        friendShip.setFriendUser(friend);
        friendShip.setStatus("pending");

        // lập bảng người chấp nhận lời mời
        friendShip2.setMyUser(friend);
        friendShip2.setFriendUser(users);
        friendShip2.setStatus("unfriend");

        friendshipRepository.save(friendShip);
        friendshipRepository.save(friendShip2);
    }
    //AJAX
    @GetMapping("/friends/operator")
    @ResponseBody
    public void OperatorFriendShip(@RequestParam("userId") int userId, @RequestParam("friendUserId") int friendId) {
        Optional<Users> userLogin = usersRepository.findById(Integer.valueOf(userId));
        Optional<Users> user = usersRepository.findById(Integer.valueOf(friendId));
        FriendShip fs = friendshipRepository.findFriendshipByMyUserAndFriendUser(userLogin.get(), user.get()).orElse(null);

        if (fs == null) {
            newFriendShip( userId, friendId);
        } else {
            if(fs.getStatus().equals("accepted"))
            {
                FriendShip sf = friendshipRepository.findFriendshipByMyUserAndFriendUser(user.get(), userLogin.get()).get();
                fs.setStatus("unfriend");
                sf.setStatus("unfriend");
                
                List<Share> shares =  shareRepository.findAllByMyUserAndFriendUser(userLogin.get(),user.get());
                List<Share> serahs =  shareRepository.findAllByMyUserAndFriendUser(user.get(),userLogin.get());
                
                if(shares != null)
                {
                    for(Share s : shares)
                    {
                        shareRepository.delete(s);
                    }
                }
                
                if(serahs != null)
                {
                    for(Share s : serahs)
                    {
                        shareRepository.delete(s);
                    }
                }
                
                friendshipRepository.save(fs);
                friendshipRepository.save(sf);
            }
            else if(fs.getStatus().equals("declined"))
            {
                FriendShip sf = friendshipRepository.findFriendshipByMyUserAndFriendUser(user.get(), userLogin.get()).get();
                fs.setStatus("pending");
                sf.setStatus("declined");
                
                friendshipRepository.save(fs);
                friendshipRepository.save(sf);
            }
            else if(fs.getStatus().equals("unfriend"))
            {
                FriendShip sf = friendshipRepository.findFriendshipByMyUserAndFriendUser(user.get(), userLogin.get()).get();
                fs.setStatus("pending");
                sf.setStatus("unfriend");
                
                friendshipRepository.save(fs);
                friendshipRepository.save(sf);
            }
            else if(fs.getStatus().equals("pending"))
            {
                FriendShip sf = friendshipRepository.findFriendshipByMyUserAndFriendUser(user.get(), userLogin.get()).get();
                 friendshipRepository.delete(sf);
                  friendshipRepository.delete(fs);
            }
                
        }
      

    }
}
