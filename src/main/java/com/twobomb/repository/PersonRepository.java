package com.twobomb.repository;

import com.twobomb.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;


public interface PersonRepository extends JpaRepository<Person,Long> {
}
