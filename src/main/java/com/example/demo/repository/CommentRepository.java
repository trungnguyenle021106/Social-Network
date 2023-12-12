/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.repository;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author asus
 */
@Repository
public interface CommentRepository extends CrudRepository<Comment, Integer> {

    List<Comment> findAllByPost(Post post);

    List<Comment> findTop2ByPostOrderByCommentDateDesc(Post post);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Comment c WHERE c.post.id = :postId")
    void deleteByPostId(@Param("postId") Integer postId);
}
