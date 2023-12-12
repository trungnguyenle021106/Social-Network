/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 *
 * @author Nguyen
 */
@Entity
@Table(name = "Friendship")
public class FriendShip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer friendship_id;

    @ManyToOne
    @JoinColumn(name = "my_user_id")
    private Users myUser;

    @ManyToOne
    @JoinColumn(name = "friend_user_id")
    private Users friendUser;

    private String status;

    // Getters and setters
    public Integer getFriendship_id() {
        return friendship_id;
    }

    public void setFriendship(Integer friendship_id) {
        this.friendship_id = friendship_id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
