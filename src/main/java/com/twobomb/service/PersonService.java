package com.twobomb.service;

import com.twobomb.entity.Person;
import com.twobomb.entity.TeacherInfo;
import com.twobomb.repository.PersonRepository;
import com.twobomb.repository.TeacherInfoRepository;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@SuppressWarnings("Duplicates")
@Service("PersonService")
@Repository
@Transactional
public class PersonService {

    @Autowired
    LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    @Autowired
    PersonRepository personRepository;

}
