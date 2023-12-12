/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/SpringFramework/Repository.java to edit this template
 */
package com.example.demo.repository;

import com.example.demo.entity.FriendShip;
import com.example.demo.entity.Users;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Nguyen
 */
@Repository
public interface FriendshipRepository extends CrudRepository<FriendShip, Integer> {

    @Query(value = "SELECT * FROM Friendship WHERE my_user_id =:userId and status =:s", nativeQuery = true)
    List<FriendShip> findByMyUserAndStatus(@Param("userId") Integer userId, @Param("s") String status);

    @Query(value = "SELECT * FROM Friendship WHERE friend_user_id =:friendId and status =:s", nativeQuery = true)
    List<FriendShip> findByMyFriendAndStatus(@Param("friendId") Integer friendId, @Param("s") String status);
    
    @Query( value =  "SELECT * FROM Friendship WHERE my_user_id =:userId and friend_user_id =:friendId", nativeQuery = true)
    List <FriendShip> findByMyUserAndMyFriend(@Param("userId") Integer userId, @Param("friendId") Integer friendId);
    
    @Query( value =  "SELECT * FROM Friendship WHERE status IN (:s1, :s2)", nativeQuery = true)
    List <FriendShip> findBy2Status(@Param("s1") String status1, @Param("s2") String status2);
    
    Optional<FriendShip> findFriendshipByMyUserAndFriendUser(@Param("myUser") Users myUser, @Param("friendUser") Users friendUser);
}
