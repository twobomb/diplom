package com.twobomb.service;

import com.twobomb.Utils.AppConst;
import com.twobomb.entity.*;
import com.twobomb.repository.DiscinplineRepository;
import com.twobomb.repository.GroupRepository;
import com.twobomb.repository.PersonRepository;
import com.twobomb.ui.pages.CourseworkView;
import com.twobomb.ui.pages.DisciplinesView;
import com.twobomb.ui.pages.ThemeView;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.Period;
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
    public static String string_between_date(Date d1, Date d2){
        long date1 = d1.getTime();
        long date2 = d2.getTime();
        long t = 0;
        String res = "";
        if(date1 > date2)
            t = date1 - date2;
        else
            t= date2 - date1;
        long days = t/1000/60/60/24;
        long hours = t/1000/60/60;
        long min = t/1000/60;

        if(days > 0 )
            res += days + " " + trueRussianDecline("дней","день","дня", (int) days);
        else if (hours > 0)
            res += hours + " " + trueRussianDecline("часов","час","часа", (int) hours);
        else
            res += min + " " + trueRussianDecline("минут","минута","минуты", (int) min);
        if(date1 > date2)
            res+=" назад";
        return res;
    }                                 //example d1 дней       d2  день        d3 дня
    public static String trueRussianDecline(String d1, String d2, String d3, int c){
        String res = "";
        switch (c%100){
            case 11: case 12: case 13: case 14:return d1;
            default:
                switch (c%10){
                    case 0:case 5:case 6:case 7:case 8:case 9:return d1;
                    case 1: return d2;
                    case 2:case 3:case 4: return d3;
                }
        }
        return "";
    }

    public void detachThemeToPersionInCoursework(Integer indexCoursework, Integer indexTheme) throws Exception{
        User currentUser = ctx.getBean("CurrentUser",User.class);
        List<Discipline> disciplineList = getBindDisciplineWidthUser(currentUser);
        List<CourseWork> courseworksList = new ArrayList<>();
        disciplineList.forEach( discipline -> courseworksList.addAll(discipline.getCourseWork()));
        CourseWork currentCoursework;
        if(indexCoursework >= 0 && indexCoursework < courseworksList.size())
            currentCoursework = courseworksList.get(indexCoursework);
        else
            throw new Exception("Незвестный индекс курсовой");
        List<Theme> themes = currentCoursework.getThemes();
        Theme currentTheme;
        if(indexTheme >= 0 && indexTheme < themes.size())
            currentTheme = themes.get(indexTheme);
        else
            throw new Exception("Неизвестный индекс темы!");
        Map<CourseWork,Person> affixStud = currentTheme.getCoueseworkWidthAffixedStudents();
        boolean isAffix = false;
        for(CourseWork cw:affixStud.keySet())
            if(cw.equals(currentCoursework) && affixStud.get(cw).equals(currentUser.getPerson())){
                isAffix = true;
                break;
            }
        if(!isAffix)
            throw new Exception("Вы не прикреплены к данной теме");

        ControlDiscipline controlDiscipline = currentCoursework.getDiscipline().getControl_discipline();
        Date now = Calendar.getInstance().getTime();
        if(controlDiscipline == null ||
                controlDiscipline.getDateBegin().after(now) ||
                controlDiscipline.getDateEnd().before(now))
            throw new Exception("Смотрите дату начала и конца подачи тем!");
        boolean isStudentChange = AppConst.DEFAULT_IS_STUDENT_CHANGE;
        if(controlDiscipline != null)
            isStudentChange = controlDiscipline.getIs_student_change();
        if(!isStudentChange)
            throw new Exception("Вы не можете открепится от выбранной темы так как это запрещено установками дисциплины!");
        currentTheme.detachThemeFromCourseworkWithStudent(currentCoursework,currentUser.getPerson());
    }

    public void addAttachThemeToPersonInCoursework(Integer indexCoursework, Integer indexTheme) throws Exception {
        User currentUser = ctx.getBean("CurrentUser",User.class);
        List<Discipline> disciplineList = getBindDisciplineWidthUser(currentUser);
        List<CourseWork> courseworksList = new ArrayList<>();
        disciplineList.forEach( discipline -> courseworksList.addAll(discipline.getCourseWork()));
        CourseWork currentCoursework;
        if(indexCoursework >= 0 && indexCoursework < courseworksList.size())
            currentCoursework = courseworksList.get(indexCoursework);
        else
            throw new Exception("Незвестный индекс курсовой");
        List<Theme> themes = currentCoursework.getThemes();
        Theme currentTheme;
        if(indexTheme >= 0 && indexTheme < themes.size())
            currentTheme = themes.get(indexTheme);
        else
            throw new Exception("Неизвестный индекс темы!");
        List<Theme> freeThemes = getFreeThemeListFromCoursework(currentCoursework,currentUser.getPerson().getGroup());
        if(!freeThemes.contains(currentTheme))
            throw new Exception("К данной теме уже прикреплен студент!");
        ControlDiscipline controlDiscipline = currentCoursework.getDiscipline().getControl_discipline();
        Date now = Calendar.getInstance().getTime();
        if(controlDiscipline == null ||
                controlDiscipline.getDateBegin().after(now) ||
                controlDiscipline.getDateEnd().before(now))
            throw new Exception("Смотрите дату начала и конца подачи тем!");

        List<Theme> affix = currentUser.getPerson().getAffixThemesStudent();
        for(Theme t:affix)
            if(t.getCourseWorks().contains(currentCoursework))
                throw new Exception("Вы уже закреплены к одной из тем в данной курсовой работе!");
        currentTheme.addAttachStudentAndCoursework(currentUser.getPerson(),currentCoursework);


    }
    //Вернуть темы данной курсовой работы к которым никто не привязан из данной группы
    public List<Theme> getFreeThemeListFromCoursework(CourseWork cw,Group group){
        List<Theme> freeThemes = new ArrayList<>();
        List<Person> studentsInGroup = group.getPersons();
        Map<Theme,Person> affix =  cw.getThemesWithAffixedStudent();
        for(Theme theme:cw.getThemes()){
            boolean isFreeTheme = true;
            for(Theme t :affix.keySet()){
                if(t.equals(theme) && studentsInGroup.contains(affix.get(t))){
                    isFreeTheme = false;
                    break;
                }
            }
            if(isFreeTheme)
                freeThemes.add(theme);
        }
        return freeThemes;
    }
    public List<CourseworkView.CourseWorkInfo> getCourseWorkInfoList(Long disId){
        User currentUser = ctx.getBean("CurrentUser",User.class);
        List<Discipline> disciplineList = getBindDisciplineWidthUser(currentUser);
        List<CourseWork> courseworksListMain = new ArrayList<>();
        disciplineList.forEach( discipline -> courseworksListMain.addAll(discipline.getCourseWork()));
        if(disId >= 0 && disId < disciplineList.size())
            disciplineList = new ArrayList<>(Collections.singleton(disciplineList.get(Math.toIntExact(disId))));

        List<CourseWork> courseWorks = new ArrayList<>();

        for(Discipline discipline:disciplineList)
            courseWorks.addAll(discipline.getCourseWork());

        List<CourseworkView.CourseWorkInfo> courseWorkInfos = new ArrayList<>();
        int index = 1;
        for(CourseWork cw:courseWorks){
            CourseworkView.CourseWorkInfo courseWorkInfo = new CourseworkView.CourseWorkInfo();
            courseWorkInfo.setIndex(index++);
            courseWorkInfo.setIndexOfMainList(courseworksListMain.indexOf(cw)+1);
            courseWorkInfo.setCourseworkName(cw.getName());
            courseWorkInfo.setDisciplineName(cw.getDiscipline().getName());
            List<Person> teacherAttach = cw.getDiscipline().getAttachedTeachers();
            String teacherNames = "";
            for(Person teacher:teacherAttach)
                teacherNames+=teacher.getLastname()+" "+teacher.getFirstname().charAt(0)+"., ";
            teacherNames = teacherNames.replaceFirst(", $","");
            courseWorkInfo.setPrepodList(teacherNames);
            ControlDiscipline control = cw.getDiscipline().getControl_discipline();
            String begin = "неизвестно";
            if(control != null && control.getDateBegin() != null)
                begin = new SimpleDateFormat("dd.MM.yyyy").format(control.getDateBegin());
            courseWorkInfo.setDateBegin(begin);
            String end = "неизвестно";
            if(control != null && control.getDateEnd() != null)
                end = new SimpleDateFormat("dd.MM.yyyy").format(control.getDateEnd());
            courseWorkInfo.setDateEnd(end);
            boolean isCheked = false;
            List<Theme> themeList = currentUser.getPerson().getAffixThemesStudent();
            for(Theme theme:themeList)
                if(theme.getCourseWorks().contains(cw)){
                    isCheked = true;
                    break;
                }
            courseWorkInfo.setIsThemeChecked(isCheked);
            courseWorkInfos.add(courseWorkInfo);

        }

        return courseWorkInfos;
    }
    public ThemeView.AdditionalCourseWorkInfo getAdditionalCourseWorkInfo(Long indexCoursework){
        User currentUser = ctx.getBean("CurrentUser",User.class);
        List<Discipline> disciplineList = getBindDisciplineWidthUser(currentUser);
        List<CourseWork> courseworksList = new ArrayList<>();
        disciplineList.forEach( discipline -> courseworksList.addAll(discipline.getCourseWork()));

        CourseWork currentCoursework = null;
        if(indexCoursework >= 0 && indexCoursework < courseworksList.size())
            currentCoursework = courseworksList.get(Math.toIntExact(indexCoursework));
        else
            return null;

        ThemeView.AdditionalCourseWorkInfo info = new ThemeView.AdditionalCourseWorkInfo();
        info.setGroupName(currentUser.getPerson().getGroup().getName());
        info.setCourseworkName(currentCoursework.getName());
        info.setDisciplineName(currentCoursework.getDiscipline().getName());
        String teachersNames = "";
        for(Person teacher:currentCoursework.getDiscipline().getAttachedTeachers())
            teachersNames+=teacher.getLastname()+" "+teacher.getFirstname().charAt(0)+"., ";
        teachersNames = teachersNames.replaceFirst(", $","");
        info.setTeachers(teachersNames);
        info.setIsAutoset(AppConst.DEFAULT_IS_AUTOSET);
        info.setIsStudentChange(AppConst.DEFAULT_IS_STUDENT_CHANGE);
        info.setIsStudentOffer(AppConst.DEFAULT_IS_STUDENT_OFFER);
        info.setBeginDate("неизвестно");
        info.setEndDate("неизвестно");
        ControlDiscipline controlDiscipline = currentCoursework.getDiscipline().getControl_discipline();
        if(controlDiscipline != null){
            info.setIsAutoset(controlDiscipline.getIs_autoset());
            info.setIsStudentOffer(controlDiscipline.getIs_student_offer());
            info.setIsStudentChange(controlDiscipline.getIs_student_change());
            String beginDate = "";
            Date now = Calendar.getInstance().getTime();
            if(controlDiscipline.getDateBegin().before(now))
                beginDate += "началось ";
            else
                beginDate += "начнется через ";
            beginDate += string_between_date(now,controlDiscipline.getDateBegin())+" ";
            beginDate += new SimpleDateFormat("dd.MM.yyyy").format(controlDiscipline.getDateBegin());

            String endDate = "";
            if(controlDiscipline.getDateEnd().before(now))
                endDate  += "закончилось ";
                else
                endDate += " через ";
            endDate  += string_between_date(now,controlDiscipline.getDateEnd())+" ";
            endDate  += new SimpleDateFormat("dd.MM.yyyy").format(controlDiscipline.getDateEnd());


            info.setBeginDate(beginDate);
            info.setEndDate(endDate);
        }
        int courseNeedThemes = 0;
        List<Person> teachers =  currentCoursework.getDiscipline().getAttachedTeachers();
        for(Person teacher:teachers){
            List<TeacherInfo> teacherInfos =  teacher.getTeacherInfos();
            for(TeacherInfo ti:teacherInfos)
                if(ti.getCourseWork().equals(currentCoursework))
                    courseNeedThemes+=ti.getCount_theme();
        }
        courseNeedThemes = courseNeedThemes - currentCoursework.getThemes().size();
        info.setTeachersMustAddCount(courseNeedThemes);
        return info;

    }
    public List<ThemeView.ThemeItemInfo> getThemeItemInfoList(Long indexCoursework){
        User currentUser = ctx.getBean("CurrentUser",User.class);
        List<Discipline> disciplineList = getBindDisciplineWidthUser(currentUser);
        List<CourseWork> courseworksList = new ArrayList<>();
        disciplineList.forEach( discipline -> courseworksList.addAll(discipline.getCourseWork()));

        List<Theme> themeList = null;
        CourseWork currentCoursework = null;
        List<ThemeView.ThemeItemInfo> themeItemInfoList = new ArrayList<>();
        if(indexCoursework >= 0 && indexCoursework < courseworksList.size()) {
            currentCoursework = courseworksList.get(Math.toIntExact(indexCoursework));
            themeList = currentCoursework.getThemes();
        }
        else
            return themeItemInfoList;
        int index = 1;
        for(Theme t:themeList){
            ThemeView.ThemeItemInfo themeItemInfo = new ThemeView.ThemeItemInfo();
            themeItemInfo.setTeacherAdd(t.getAdd_person().getLastname()+" "+ t.getAdd_person().getFirstname().charAt(0)+".");
            themeItemInfo.setDescription(t.getDescription().isEmpty()?"Описание отсутствует":t.getDescription());
            themeItemInfo.setName(t.getText());
            String editedBy = "";
            if(t.getEdit_person() != null && t.getEdit_date()!= null){
                editedBy = "Отредактировал ";
                editedBy+=t.getEdit_person().getUser().getRole().getRole_name()+" ";
                editedBy+=t.getEdit_person().getLastname()+" "+t.getEdit_person().getFirstname().charAt(0)+ " ";
                editedBy+= new SimpleDateFormat("dd.MM.yyyy").format(t.getEdit_date());
            }
            themeItemInfo.setEdited(editedBy);
            themeItemInfo.setIndex(index++);
            //Можно ли прикрепится к теме
            Map<CourseWork,Person> mapTmp =  t.getCoueseworkWidthAffixedStudents();
            Map<CourseWork,Person> map =  new HashMap<CourseWork,Person>();
            //Исключает студентов не нашей группы
            for(CourseWork key:mapTmp.keySet()){
                if(mapTmp.get(key).getGroup().equals(currentUser.getPerson().getGroup()))
                    map.put(key,mapTmp.get(key));
            }
            Person studentAttachToThisTheme = map.get(currentCoursework);
            themeItemInfo.setIsCanAttach(studentAttachToThisTheme == null);
            //Определение статуса
            themeItemInfo.setIsCanAttach(false);
            themeItemInfo.setIsCurrentUserAttach(false);
            if(studentAttachToThisTheme == null) {
                themeItemInfo.setStatus("Тема свободна");
                if(!isPersonAttachToThemeInCoursework(currentUser.getPerson(),currentCoursework))
                    themeItemInfo.setIsCanAttach(true);
            }
            else if(studentAttachToThisTheme.equals(currentUser.getPerson())){
                themeItemInfo.setStatus("Вы закреплены за этой темой");
                themeItemInfo.setIsCurrentUserAttach(true);
            }
            else
                themeItemInfo.setStatus("За темой закреплен "+studentAttachToThisTheme.getLastname()+" "+studentAttachToThisTheme.getFirstname().charAt(0)+".");
            //Disabled статус кнопок прязвки в завимости от времени
            ControlDiscipline controlDiscipline =currentCoursework.getDiscipline().getControl_discipline();
            Date now = Calendar.getInstance().getTime();
            if(controlDiscipline == null ||
                    controlDiscipline.getDateBegin().after(now) ||
                    controlDiscipline.getDateEnd().before(now)){
                themeItemInfo.setIsDisabledAttachBtn(true);
                themeItemInfo.setIsDisabledDetachBtn(true);
            }else{
                themeItemInfo.setIsDisabledAttachBtn(false);
                themeItemInfo.setIsDisabledDetachBtn(false);
            }
            boolean isStudentChange = AppConst.DEFAULT_IS_STUDENT_CHANGE;
            if(controlDiscipline != null)
                isStudentChange = controlDiscipline.getIs_student_change();
            if(!isStudentChange)
                themeItemInfo.setIsDisabledDetachBtn(true);
            themeItemInfoList.add(themeItemInfo);
        }
        return themeItemInfoList;
    }
    public boolean isPersonAttachToThemeInCoursework(Person p, CourseWork c){
        for(Theme t:c.getThemes())
            if(t.getCoueseworkWidthAffixedStudents().get(c) != null)
                if(t.getCoueseworkWidthAffixedStudents().get(c).equals(p))
                    return true;
        return false;
    }
    public boolean isAttachedThemeToCoursework(Theme t,CourseWork c){
        return c.getThemes().contains(t);
    }
    public List<DisciplinesView.DisciplineInfo> getDisciplineInfoList(){
        User currentUser = ctx.getBean("CurrentUser",User.class);
        Person person = currentUser.getPerson();

        List<Discipline> list = getBindDisciplineWidthUser(currentUser);
        List<DisciplinesView.DisciplineInfo> disciplineInfos = new ArrayList<>();
        int index = 1;
        for (Discipline l : list) {
            DisciplinesView.DisciplineInfo disciplineInfo = new DisciplinesView.DisciplineInfo();
            disciplineInfo.setIndex(index++);
            disciplineInfo.setCourseworkCount(l.getCourseWork().size());
            disciplineInfo.setDisciplineName(l.getName());

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
            disciplineInfo.setIsChecked(isCheckedTmp);
            disciplineInfos.add(disciplineInfo);
        }

        return disciplineInfos;
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
        person.addDisciplineTeacher(discipline);
        personRepository.save(person);
    }
    public List<Discipline> getAll(){
        return discinplineRepository.findAll();
    }

    public List<Discipline> getBindDisciplineWidthUser(User user){

        List<Discipline> res = new ArrayList<>();
        try {
            switch (user.getRole().getRole()) {
                case Role.ADMIN:
                    res = getAll();
                    break;
                case Role.STUDENT: {
                    Group group = user.getPerson().getGroup();
                    res = group.getDisciplines();
                }
                break;
                case Role.TEACHER: {
                    Person p = user.getPerson();
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
