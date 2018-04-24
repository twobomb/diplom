package com.twobomb.service;

import com.twobomb.entity.Role;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.Serializable;

@SuppressWarnings("Duplicates")
@Repository
@Transactional
@Service("UtilService")
public class UtilService {

    @Autowired
    LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;


    public UtilService() {
    }

    public void update(Object o){
        SessionFactory sessionFactory =  localContainerEntityManagerFactoryBean.getObject().unwrap(SessionFactory.class);
        Session session = sessionFactory.openSession();
        session.update(o);
        session.close();
    }
    public <T>T get(Class<T> tClass, Serializable serializable){
        SessionFactory sessionFactory =  localContainerEntityManagerFactoryBean.getObject().unwrap(SessionFactory.class);
        Session session = sessionFactory.openSession();
        T o  = session.get(tClass,serializable);
        session.close();
        return o;
    }

    public <T> T merge(T t){
        SessionFactory sessionFactory =  localContainerEntityManagerFactoryBean.getObject().unwrap(SessionFactory.class);
        Session session = sessionFactory.openSession();
        t = (T) session.merge(t);
        session.close();
        return t;
    }
}
