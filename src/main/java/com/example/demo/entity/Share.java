///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Date;
import lombok.Data;

@Data
@Entity
@Table(name = "Share")
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer share_id;

    @ManyToOne
    @JoinColumn(name = "my_user_id")
    private Users myUser;

    @ManyToOne
    @JoinColumn(name = "friend_user_id")
    private Users friendUser;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    

    private Date share_date;

    public void setPost(Post post)
    {
        this.post = post;
    }
    
    public Post getPost()
    {
        return post;
    }
    
    public Integer getShare_id() {
        return share_id;
    }

    public void setShare_id(Integer share_id) {
        this.share_id = share_id;
    }

    public Users getMyUser() {
        return myUser;
    }
    
    public void setMyUser(Users myUser) {
        this.myUser = myUser;
    }

    public Users getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(Users friendUser) {
        this.friendUser = friendUser;
    }

    public Date getShare_date() {
        return share_date;
    }

    public void setShare_date(Date share_date) {
        this.share_date = share_date;
    }
    
    
    public Integer getFriend_user_id()
    {
        return friendUser.getUser_id();
    }
    
    public Integer getMy_user_id()
    {
        return myUser.getUser_id();
    }
    
    
    public Integer getPost_id()
    {
        return post.getPost_id();
    }
    
    public void setPost_id(Integer post_id) {
        this.post.setPost_id(post_id);
    }
}
