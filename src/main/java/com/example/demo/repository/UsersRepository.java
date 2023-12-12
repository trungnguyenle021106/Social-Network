/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.repository;

import com.example.demo.entity.FriendShip;
import com.example.demo.entity.Users;
import java.util.List;
import java.lang.Boolean;
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
public interface UsersRepository extends CrudRepository<Users, Integer> {
    @Query(value = "SELECT * FROM [Users] WHERE username =:username and password =:password", nativeQuery = true)
    Optional<Users> findByUsnameAndPsw(@Param("username") String uesrname, @Param("password") String password);

    @Query(value = "SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Users u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);
   

}

