package com.twobomb.repository;

import com.twobomb.entity.Role;
import com.twobomb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface RoleRepository  extends JpaRepository<Role,Long> {

        @Query("select r from Role r where r.role = :role")
        public Role findByRole(@Param("role")String role);



}
