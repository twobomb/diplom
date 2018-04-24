package com.twobomb.repository;

import com.twobomb.entity.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;


public interface DiscinplineRepository extends JpaRepository<Discipline,Long> {
}
