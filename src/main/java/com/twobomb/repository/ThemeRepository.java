package com.twobomb.repository;

import com.twobomb.entity.Person;
import com.twobomb.entity.Theme;
import com.twobomb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ThemeRepository extends JpaRepository<Theme,Long> {

}
