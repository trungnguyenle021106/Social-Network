/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.example.demo.model;

import com.example.demo.entity.Comment;
import java.util.List;

/**
 *
 * @author Nguyen Le
 */
public class Message {

    private String sender;

    private String content;

    private String type;
    
    private List<Comment> Comments;
    
    public void setListComments(List<Comment> Comments)
    {
        this.Comments = Comments;
    }
    
    public List<Comment> getListComments()
    {
        return this.Comments;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

}