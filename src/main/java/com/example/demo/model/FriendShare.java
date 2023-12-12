/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.model;

import com.example.demo.entity.Users;

/**
 *
 * @author Nguyen Le
 */
public class FriendShare {

    private Users FriendUser;

    private boolean isShared;

    public boolean isIsShared() {
        return isShared;
    }

    public void setIsShared(boolean isShared) {
        this.isShared = isShared;
    }
    
    public Boolean getIsShared()
    {
        return this.isShared;
    }
    
    public Users getFriendUser() {
        return this.FriendUser;
    }

    public void setFriendUser(Users FriendUser) {
        this.FriendUser = FriendUser;
    }

}