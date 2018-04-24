package com.twobomb.repository;

import com.twobomb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Long> {

    @Query("select b from User b where b.login = :login")
    User findByLogin(@Param("login") String login);



}
