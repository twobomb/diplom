package com.twobomb.service;

import com.twobomb.entity.*;
import com.twobomb.repository.DiscinplineRepository;
import com.twobomb.repository.GroupRepository;
import com.twobomb.repository.PersonRepository;
import net.bytebuddy.implementation.bytecode.Throw;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
@Service("DisciplineService")
@Repository
@Transactional
public class DisciplineService {
    @Autowired
    DiscinplineRepository discinplineRepository;

    @Autowired
    LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    GroupRepository groupRepository;

    public void attachGroup(Group group,Discipline discipline){
        SessionFactory sessionFactory =  localContainerEntityManagerFactoryBean.getObject().unwrap(SessionFactory.class);
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
        }
        catch (HibernateException e){
            e.printStackTrace();
            System.out.println("Уже существует транзакция");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        group = session.get(group.getClass(),group.getId());
        group.addDiscipline(discipline);
        groupRepository.save(group);
    }
    public void attachTeacher(Person person,Discipline discipline){

        SessionFactory sessionFactory =  localContainerEntityManagerFactoryBean.getObject().unwrap(SessionFactory.class);
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
        }
        catch (HibernateException e){
            e.printStackTrace();
            System.out.println("Уже существует транзакция");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if(!person.getUser().getRole().getRole().equals(Role.TEACHER)){
            try {
                throw new Exception("Attached person not have role teacher!");
            }
            catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
        person = session.get(Person.class,person.getId());
//        discipline = session.get(Discipline.class,discipline.getId());
        person.addDisciplineTeacher(discipline);
        personRepository.save(person);
    }
    public List<Discipline> getAll(){
        return discinplineRepository.findAll();
    }
    //Достать дисциплины связанные с пользователем

//    @Autowired
//    SessionFactory sessionFactory;
    public List<Discipline> getBindDisciplineWidthUser(User user){

//        Session session = sessionFactory.openSession();
        List<Discipline> res = new ArrayList<>();
        try {
            switch (user.getRole().getRole()) {
                case Role.ADMIN:
                    res = getAll();
                    break;
                case Role.STUDENT: {
                    Group group = user.getPerson().getGroup();
//                    session.update(group);
                    res = group.getDisciplines();
//                for (Discipline d : res)
//                    if (d.getCourseWork().size() == 0)
//                        res.remove(d);
                }
                break;
                case Role.TEACHER: {
                    Person p = user.getPerson();
//                    session.update(p);
                    res = p.getDisciplinesTeacher();
                }
                break;
                default:
                        throw new Exception("Unknown role");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
//            session.close();
        }
    return res;
    }
    //Получить курсовые привязанные к пользователю
    public List<CourseWork> getCourseWorkWithUser(User user){
        Group group = user.getPerson().getGroup();
        List<Discipline> userDisciplines = group.getDisciplines();
        List<CourseWork> res = new ArrayList<>();
        for(Discipline d:userDisciplines)
            res.addAll(d.getCourseWork());
        return res;
    }
}
