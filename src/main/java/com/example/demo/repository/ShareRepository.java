/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.repository;

import com.example.demo.entity.Post;
import com.example.demo.entity.Share;
import com.example.demo.entity.Users;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Nguyen Le
 */
public interface ShareRepository extends CrudRepository<Share, Integer> {

    List<Share> findAllByPost(Post post);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Share s WHERE s.post.id = :postId")
    void deleteSharesByPostId(@Param("postId") Integer postId);
    
    List<Share> findAllByMyUserAndFriendUser(Users myUser, Users friendUser);
}
