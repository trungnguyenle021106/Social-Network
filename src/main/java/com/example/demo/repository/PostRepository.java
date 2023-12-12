/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.repository;

import com.example.demo.entity.Post;
import com.example.demo.entity.Users;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author asus
 */
@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {

    List<Post> findTop3ByOrderByPostDateDesc();

    public Page<Post> findAll(Pageable pageable);

    public List<Post> findTop5ByMyUserOrderByPostDateDesc(Users currentUser);

    @Query(value = "SELECT * FROM post p WHERE p.user_id =:userId", nativeQuery = true)
    public List<Post> findMyUserPost(@Param("userId") Integer userId);

    // Phương thức lấy bài đăng của bạn bè
    @Query(value = "SELECT * FROM post p JOIN friendship f ON p.user_id = f.friend_user_id WHERE f.my_user_id =:userId AND f.status = 'accepted' ", nativeQuery = true)
    List<Post> findFriendPosts(@Param("userId") Integer userId);

    // Trong PostRepository.java
    @Query(value = "SELECT s.post FROM Share s WHERE s.friendUser.user_id = :userId")
    List<Post> findSharePosts(@Param("userId") Integer userId );

}
