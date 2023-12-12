/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.model;

import com.example.demo.entity.Post;

/**
 *
 * @author Nguyen
 */
public class PostInfo {
    
    private Post post;
    
    private Integer amountLike;
    private Integer amountComment;
    private Integer amountShare;
    private boolean blLike = false ;

    public boolean isBlLike() {
        return blLike;
    }

    public void setBlLike(boolean blLike) {
        this.blLike = blLike;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Integer getAmountLike() {
        return amountLike;
    }

    public void setAmountLike(Integer amountLike) {
        this.amountLike = amountLike;
    }

    public Integer getAmountComment() {
        return amountComment;
    }

    public void setAmountComment(Integer amountComment) {
        this.amountComment = amountComment;
    }

    public Integer getAmountShare() {
        return amountShare;
    }

    public void setAmountShare(Integer amountShare) {
        this.amountShare = amountShare;
    }
    
}
