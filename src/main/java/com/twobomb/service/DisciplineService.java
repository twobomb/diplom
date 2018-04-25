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
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    ApplicationContext ctx;

    public HashMap<String,Object> getData_CV(Long dis_id){

        User currentUser = ctx.getBean("CurrentUser",User.class);
        List<Discipline> disciplineList = getBindDisciplineWidthUser(currentUser);
        if(dis_id >= 0 && dis_id < disciplineList.size())
            disciplineList = new ArrayList<>(Collections.singleton(disciplineList.get(Math.toIntExact(dis_id))));

        List<CourseWork> courseWorks = new ArrayList<>();

        for(Discipline discipline:disciplineList)
            courseWorks.addAll(discipline.getCourseWork());


        List<String> courseworkName = new ArrayList<>();
        List<String> disciplineName = new ArrayList<>();
        List<String> prepodList = new ArrayList<>();
        List<String> dateBegin = new ArrayList<>();
        List<String> dateEnd = new ArrayList<>();
        List<Boolean> isThemeChecked = new ArrayList<>();
        for(CourseWork cw:courseWorks){
            courseworkName.add(cw.getName());
            disciplineName.add(cw.getDiscipline().getName());
            List<Person> teacherAttach = cw.getDiscipline().getAttachedTeachers();
            String teacherNames = "";
            for(Person teacher:teacherAttach)
                teacherNames+=teacher.getLastname()+" "+teacher.getFirstname().charAt(0)+"., ";
            teacherNames = teacherNames.replaceFirst(", $","");
            prepodList.add(teacherNames);
            ControlDiscipline control = cw.getDiscipline().getControl_discipline();
            String begin = "неизвестно";
            if(control.getDateBegin() != null)
                begin = new SimpleDateFormat("dd.MM.yyyy").format(control.getDateBegin());
            dateBegin.add(begin);
            String end = "неизвестно";
            if(control.getDateEnd() != null)
                end = new SimpleDateFormat("dd.MM.yyyy").format(control.getDateEnd());
            dateEnd.add(end);
            boolean isCheked = false;
            List<Theme> themeList = currentUser.getPerson().getAffixThemesStudent();
            for(Theme theme:themeList)
                if(theme.getCourseWorks().contains(cw)){
                    isCheked = true;
                    break;
                }
            isThemeChecked.add(isCheked);
        }

        HashMap<String,Object> result = new HashMap<>();
        result.put("courseworkName",courseworkName);
        result.put("disciplineName",disciplineName);
        result.put("prepodList",prepodList);
        result.put("dateBegin",dateBegin);
        result.put("dateEnd",dateEnd);
        result.put("isThemeChecked",isThemeChecked);

        return result;
    }
    public HashMap<String,Object> getData_DV(){
        User currentUser = ctx.getBean("CurrentUser",User.class);
        Person person = currentUser.getPerson();
//            person = session.get(person.getClass(), person.getId());
        HashMap<String,Object> result = new HashMap<>();
        List<String> disciplineName = new ArrayList<>();
        List<Integer> courseworkCount = new ArrayList<>();
        List<Boolean> isChecked = new ArrayList<>();
        List<Boolean> isNotChecked = new ArrayList<>();

        List<Discipline> list = getBindDisciplineWidthUser(currentUser);

        for (Discipline l : list) {
//                l = session.get(Discipline.class, l.getId());
            courseworkCount.add(l.getCourseWork().size());
            disciplineName.add(l.getName());

            //Курсовые работы дисциплины
            List<CourseWork> courseWorks = l.getCourseWork();
            //Привязанные темы студента
            List<Theme> themeAffix = person.getAffixThemesStudent();

            //Проверка во всех ли курсовых данного предмета есть привязанная тема данного студента
            Boolean isCheckedTmp = true;
            for (CourseWork cw : courseWorks) {
                List<Theme> cwThemes = cw.getThemes();
                boolean flag = false;
                for (Theme studTheme : themeAffix) {
                    if (cwThemes.contains(studTheme)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    isCheckedTmp = false;
                    break;
                }
            }
            isChecked.add(isCheckedTmp);
            isNotChecked.add(!isCheckedTmp);

        }
        result.put("courseworkCount",courseworkCount);
        result.put("disciplineName",disciplineName);
        result.put("isChecked",isChecked);
        result.put("isNotChecked",isNotChecked);

        return result;
    }
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
