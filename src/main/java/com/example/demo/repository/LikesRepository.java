/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.repository;

import com.example.demo.entity.Likes;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.example.demo.entity.Post;
import com.example.demo.entity.Users;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends CrudRepository<Likes, Integer> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Likes l WHERE l.likedPost.post_id = :postId")
    void deleteByPostId(Integer postId);

    List<Likes> findAllByLikedPost(Post post);

    Likes findByLikedUser(Users users);
}
