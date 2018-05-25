package com.twobomb.service;

import com.twobomb.Utils.AppConst;
import com.twobomb.entity.*;
import com.twobomb.repository.*;
import com.twobomb.ui.pages.*;
import com.vaadin.flow.component.notification.Notification;
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
import java.util.*;


@SuppressWarnings("Duplicates")
@Service("DisciplineService")
@Repository
@Transactional
public class DisciplineService {


    //region Автовайреды
    @Autowired
    DiscinplineRepository discinplineRepository;

    @Autowired
    ControlDisciplineRepository controlDisciplineRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    ApplicationContext ctx;
    //endregion

    //region Возвращает дату между датами , словами
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
    }
    //endregion

    //region Правильные окончания чисел, {example512 d1=дней       d2 =день        d3=дня  c=4 return дня}
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
    //endregion

    //region Отвязать текущего студента от темы в курсовой работе
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
    //endregion

    //region Привязать тему и курсовую  к текущей персоне
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
    //endregion

    //region Вернуть темы данной курсовой работы к которым никто не привязан из данной группы
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
    //endregion

    //region Получить лист информации о курсовых для текущего юзера
    public List<CourseworkView.CourseWorkInfo> getCourseWorkInfoList(Long indexDiscipline,Long indexGroup) throws Exception {

        User currentUser = ctx.getBean("CurrentUser",User.class);
        List<Discipline> disciplineList = getMainListEntityByClass(Discipline.class);
        if((currentUser.getRole().isTeacher() || currentUser.getRole().isAdmin()) && indexGroup != null)
                disciplineList = getListDisciplineFromGroupAttachedToTeacher(getClassEntityByIndex(indexGroup,Group.class),currentUser.getPerson());

        List<CourseWork> courseworksListMain = getMainListEntityByClass(CourseWork.class);

        if(indexDiscipline != null)
            disciplineList = new ArrayList<>(Collections.singleton(getClassEntityByIndex(indexDiscipline,Discipline.class)));

        List<CourseWork> courseWorks = new ArrayList<>();

        for(Discipline discipline:disciplineList)
            courseWorks.addAll(discipline.getCourseWork());

        List<CourseworkView.CourseWorkInfo> courseWorkInfos = new ArrayList<>();
        int index = 1;
        for(CourseWork cw:courseWorks){
            CourseworkView.CourseWorkInfo courseWorkInfo = new CourseworkView.CourseWorkInfo();
            courseWorkInfo.setIndex(index++);
            courseWorkInfo.setIndexOfMainList(courseworksListMain.indexOf(cw));
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
            boolean isChecked = false;
            if(currentUser.getRole().isStudent()) {
                List<Theme> themeList = currentUser.getPerson().getAffixThemesStudent();
                for (Theme theme : themeList)
                    if (theme.getCourseWorks().contains(cw)) {
                        isChecked = true;
                        break;
                    }
            }else if(currentUser.getRole().isTeacher()){
                    isChecked = true;
                //Проверка количество добавленных тем соответствует необходимому кол-ву
                    List<TeacherInfo> ti =cw.getTeacherInfos();
                    ti.removeIf(teacherInfo -> !teacherInfo.getPerson().equals(currentUser.getPerson()));
                    if(ti.size() == 0) {
                        Notification.show("Отсутствует  информация о необходимом количестве тем в курсовой работе '"+cw.getName()+"'",8000, Notification.Position.BOTTOM_START);
                        isChecked = false;
                    }else {
                        List<Theme> themes = cw.getThemes();
                        int countAddTheme = 0;
                        for (Theme theme : themes)
                            if (theme.getAdd_person().equals(currentUser.getPerson()))
                                countAddTheme++;
                        if (ti.get(0).getCount_theme() > countAddTheme) {
                            isChecked = false;
                        }
                    }

            }
            courseWorkInfo.setIsThemeChecked(isChecked);
            courseWorkInfos.add(courseWorkInfo);

        }

        return courseWorkInfos;
    }
    //endregion

    //region Получить лист связанных с пользователем групп
    public List<Group> getBindWithUserGroups(User currentUser){
        if(currentUser.getRole().isStudent())
            return new ArrayList<>(Collections.singleton(currentUser.getPerson().getGroup()));

        List<Discipline> disciplineList = getBindDisciplineWidthUser(currentUser);
        List<Group> groups = new ArrayList<>();

        disciplineList.forEach( (discipline) -> {
            discipline.getGroups().forEach((group)->{
                if(!groups.contains(group))
                    groups.add(group);
            });
        });
        return groups;
    }
    //endregion

    //region Получить лист информации о группах для текущего юзера
    public List<GroupView.GroupInfo> getListGroupInfo(Long indexDiscipline,Long indexCoursework) throws Exception {
        User currentUser = ctx.getBean("CurrentUser",User.class);
        Role role = currentUser.getRole();
        Discipline curDisc = null;
        List<Group> mainListGroups = getMainListEntityByClass(Group.class);
        List<Group> groups = new ArrayList<>();
        if(indexDiscipline==null && indexCoursework == null)
            groups = mainListGroups;
        else if(indexDiscipline != null){
            curDisc = getClassEntityByIndex(indexDiscipline,Discipline.class);
            groups.addAll(curDisc.getGroups());
        }
        else if(indexCoursework != null){
            curDisc = getClassEntityByIndex(indexCoursework,CourseWork.class).getDiscipline();
            groups.addAll(curDisc.getGroups());
        }


        int index = 1;
        List<GroupView.GroupInfo> groupInfoList = new ArrayList<>();
        for (Group group:groups){
            GroupView.GroupInfo groupInfo = new GroupView.GroupInfo();
            groupInfo.setIndex(index++);
            groupInfo.setIndexOfMainList(mainListGroups.indexOf(group));
            groupInfo.setCourse(group.getCourse());
            groupInfo.setGroupName(group.getName());
            int countStud = group.getPersons().size();
            groupInfo.setStudentCount(countStud+ " " + trueRussianDecline("студентов","студент","студента",countStud));
            List<CourseWork> courseWorks = getListCourseworkFromGroupAttachedToTeacher(group,currentUser.getPerson());

            final Discipline curDiscFinal = curDisc;
            if(curDisc != null)
                courseWorks.removeIf(courseWork -> !courseWork.getDiscipline().equals(curDiscFinal));

            groupInfo.setCountCoursework(courseWorks.size());


            int countDisc = getListDisciplineFromGroupAttachedToTeacher(group,currentUser.getPerson()).size();
            groupInfo.setCountDiscipline(countDisc + " " + trueRussianDecline("ваших","ваша","ваши",countDisc) + " "+ trueRussianDecline("дисциплин","дисциплина","дисциплины",countDisc));
            groupInfo.setIsCheck(true);
            if(role.isTeacher())
                for (CourseWork cw:courseWorks)
                    if(!isAllNeededThemesAddedToCourseWorkByTeacher(cw,currentUser.getPerson())){
                        groupInfo.setIsCheck(false);
                        break;
                    }
            groupInfo.setLinkShowCourseworks(AppConst.PAGE_COURSEWORKS+"/group-"+index);
            groupInfo.setLinkShowDisciplines(AppConst.PAGE_DISCIPLINES+"/group-"+index);
            groupInfoList.add(groupInfo);
        }
        return groupInfoList;
    }
    //endregion

    //region Все ли темы добавил преподаватель к курсовой, количество должно быть равно или больше необходимому
    public boolean isAllNeededThemesAddedToCourseWorkByTeacher(CourseWork cw,Person teacher){
        boolean isChecked = true;
        List<TeacherInfo> ti =cw.getTeacherInfos();
        ti.removeIf(teacherInfo -> !teacherInfo.getPerson().equals(teacher));
        if(ti.size() == 0) {
            Notification.show("Отсутствует  информация о необходимом количестве тем в курсовой работе '"+cw.getName()+"'",8000, Notification.Position.BOTTOM_START);
            isChecked = false;
        }else {
            List<Theme> themes = cw.getThemes();
            int countAddTheme = 0;
            for (Theme theme : themes)
                if (theme.getAdd_person().equals(teacher))
                    countAddTheme++;
            if (ti.get(0).getCount_theme() > countAddTheme) {
                isChecked = false;
            }
        }
        return isChecked;
    }
    //endregion

    //region Получить лист дисциплин группы n привязанных к преподавателю n
    public List<CourseWork> getListCourseworkFromGroupAttachedToTeacher(Group group,Person person){
        List<CourseWork> courseWorks = new ArrayList<>();
        group.getDisciplines().forEach((discipline)->{
            if(discipline.getAttachedTeachers().contains(person) || person.getUser().getRole().isAdmin())
                courseWorks.addAll(discipline.getCourseWork());
        });
        return courseWorks;
    }
    //endregion

    //region Получить лист курсовых группы n привязанных к преподавателю n
    public List<Discipline> getListDisciplineFromGroupAttachedToTeacher(Group group,Person person){
        List<Discipline> disciplineList = new ArrayList<>();
        group.getDisciplines().forEach((discipline)->{
            if(discipline.getAttachedTeachers().contains(person) || person.getUser().getRole().isAdmin())
                disciplineList.add(discipline);
        });
        return disciplineList;
    }
    //endregion

    //region Получить сущность класса (Discipline,Group,Coursework) по индексу из списка всех сущнотей этого класса для текущего юзера
    public <T> T getClassEntityByIndex(Long index, Class<T> t) throws Exception{
        User currentUser = ctx.getBean("CurrentUser",User.class);

        if(index == null)
            throw new Exception("Отсутствует индекс для класса "+t.getClass().getName());

        if(t.equals(Discipline.class)){
            List<Discipline> disciplineList = getMainListEntityByClass(Discipline.class);
            if(index < 0 || index >= disciplineList.size())
                throw new Exception("Выход индекса дисциплин за пределы допустимого");
            return (T) disciplineList.get(Math.toIntExact(index));
        }else if (t.equals(CourseWork.class)){
            List<CourseWork> courseworksList = getMainListEntityByClass(CourseWork.class);
            if(index < 0 || index >= courseworksList.size())
                throw new Exception("Выход индекса курсовых за пределы допустимого");
            return (T) courseworksList.get(Math.toIntExact(index));
        }else if(t.equals(Group.class)){
            List<Group> groupsList = getMainListEntityByClass(Group.class);
            if(index < 0 || index >= groupsList.size())
                throw new Exception("Выход индекса групп за пределы допустимого");
            return (T) groupsList.get(Math.toIntExact(index));
        }
        else
            throw new Exception("Неизвестный класс("+t.getClass().getName()+") для поиска индекса!");
    }
    //endregion

    //region Возвращает главный лист сущностей класса T, по нему идет поиск по индексам
    public <T> List<T> getMainListEntityByClass(Class<T> t) throws Exception{
        User currentUser = ctx.getBean("CurrentUser",User.class);

        if(t.equals(Discipline.class)){
            List<Discipline> disciplineList =  getBindDisciplineWidthUser(currentUser);
            disciplineList.sort(new Comparator<Discipline>() {
                @Override
                public int compare(Discipline o1, Discipline o2) {
                    return o1.getId() > o2.getId() ? -1 : (o1.getId() < o2.getId()) ? 1 : 0;
                }
            });
            return (List<T>) disciplineList;
        }else if (t.equals(CourseWork.class)){
            List<Discipline> disciplineList = getBindDisciplineWidthUser(currentUser);
            List<CourseWork> courseworksList = new ArrayList<>();
            disciplineList.forEach(discipline -> courseworksList.addAll(discipline.getCourseWork()));
            courseworksList.sort(new Comparator<CourseWork>() {
                @Override
                public int compare(CourseWork o1, CourseWork o2) {
                    return o1.getId() > o2.getId() ? -1 : (o1.getId() < o2.getId()) ? 1 : 0;
                }
            });
            return (List<T>) courseworksList;
        }else if(t.equals(Group.class)){
            List<Group> groups = getBindWithUserGroups(currentUser);
            groups.sort(new Comparator<Group>() {
                @Override
                public int compare(Group o1, Group o2) {
                    return o1.getId() > o2.getId() ? -1 : (o1.getId() < o2.getId()) ? 1 : 0;
                }
            });
            return (List<T>) groups;
        }
        else
            throw new Exception("Неизвестный класс("+t.getClass().getName()+") для поиска главного листа!");
    }
    //endregion

    //region У группы есть эта курсовая работа?
    public boolean isGroupAttachedToCoursework(Group group,CourseWork cw){
        return group.getDisciplines().contains(cw.getDiscipline());
    }
    //endregion

    //region Получить дополнительную инфу о курсовой n
    public ThemeView.AdditionalCourseWorkInfo getAdditionalCourseWorkInfo(Long indexCoursework,Long indexGroup) throws Exception {
        User currentUser = ctx.getBean("CurrentUser",User.class);
        List<CourseWork> courseworksList = new ArrayList<>();
        Group curGroup = null;
        if(currentUser.getRole().isStudent()) {
            curGroup = currentUser.getPerson().getGroup();
            List<Discipline> disciplineList = getBindDisciplineWidthUser(currentUser);
            disciplineList.forEach(discipline -> courseworksList.addAll(discipline.getCourseWork()));
        }
        else if(currentUser.getRole().isTeacher() || currentUser.getRole().isAdmin()){
            curGroup = getClassEntityByIndex(indexGroup,Group.class);
            List<Discipline> disciplineList = curGroup.getDisciplines();

            disciplineList.forEach(discipline -> courseworksList.addAll(discipline.getCourseWork()));
        }

        CourseWork currentCoursework = getClassEntityByIndex(indexCoursework,CourseWork.class);

        if(!isGroupAttachedToCoursework(curGroup,currentCoursework))
            throw new Exception("Данная курсовая работа не привязана к этой группе!");
        ThemeView.AdditionalCourseWorkInfo info = new ThemeView.AdditionalCourseWorkInfo();

        info.setGroupName(curGroup.getName());
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
        info.setBeginDateForDatePicker("");
        info.setEndDate("неизвестно");
        info.setEndDateForDatePicker("");
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


            info.setBeginDateForDatePicker(new SimpleDateFormat("yyyy-MM-dd").format(controlDiscipline.getDateBegin()));
            info.setEndDateForDatePicker(new SimpleDateFormat("yyyy-MM-dd").format(controlDiscipline.getDateEnd()));
            info.setBeginDate(beginDate);
            info.setEndDate(endDate);
        }
        if(currentUser.getRole().isStudent()) {
            int courseNeedThemes = 0;
            List<Person> teachers = currentCoursework.getDiscipline().getAttachedTeachers();
            for (Person teacher : teachers) {
                List<TeacherInfo> teacherInfos = teacher.getTeacherInfos();
                for (TeacherInfo ti : teacherInfos)
                    if (ti.getCourseWork().equals(currentCoursework))
                        courseNeedThemes += ti.getCount_theme();
            }
            courseNeedThemes = courseNeedThemes - currentCoursework.getThemes().size();
            info.setTeachersMustAddCount(courseNeedThemes);
        }
        else if (currentUser.getRole().isTeacher()){
            List<Theme> addedThemes = currentUser.getPerson().getAdd_themes();
            final CourseWork curCwFinal = currentCoursework;
            addedThemes.removeIf(theme -> !theme.getCourseWorks().contains(curCwFinal));

            List<TeacherInfo> ti = currentUser.getPerson().getTeacherInfos();
            ti.removeIf(teacherInfo -> !teacherInfo.getCourseWork().equals(curCwFinal));
            if(ti.size() == 0)
                Notification.show("Отсутствует информация о необходимом количестве тем для вас!",6000, Notification.Position.BOTTOM_START);
            else
                info.setTeachersMustAddCount(ti.get(0).getCount_theme() - addedThemes.size());
        }
        return info;

    }
    //endregion

    //region Получить лист информации о темах в курсовой n
    public List<ThemeView.ThemeItemInfo> getThemeItemInfoList(Long indexCoursework, Long indexGroup) throws Exception {

        User currentUser = ctx.getBean("CurrentUser",User.class);

        Group curGroup = null;
        if(currentUser.getRole().isStudent())
            curGroup = currentUser.getPerson().getGroup();
        else if(currentUser.getRole().isTeacher() || currentUser.getRole().isAdmin())
            curGroup = getClassEntityByIndex(indexGroup,Group.class);
        List<Theme> themeList = null;
        CourseWork currentCoursework = getClassEntityByIndex(indexCoursework,CourseWork.class);


        if(!isGroupAttachedToCoursework(curGroup,currentCoursework))
            throw new Exception("Данная курсовая работа не привязана к этой группе!");

        List<ThemeView.ThemeItemInfo> themeItemInfoList = new ArrayList<>();
        themeList = currentCoursework.getThemes();
        int index = 1;
        for(Theme t:themeList){
            ThemeView.ThemeItemInfo themeItemInfo = new ThemeView.ThemeItemInfo();
            if(t.getAdd_person().equals(currentUser.getPerson()))
                themeItemInfo.setTeacherAdd("Вы");
            else
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
                if(mapTmp.get(key).getGroup().equals(curGroup))
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
    //endregion

    //region Персона p привязана к курсовой c?
    public boolean isPersonAttachToThemeInCoursework(Person p, CourseWork c){
        for(Theme t:c.getThemes())
            if(t.getCoueseworkWidthAffixedStudents().get(c) != null)
                if(t.getCoueseworkWidthAffixedStudents().get(c).equals(p))
                    return true;
        return false;
    }
    //endregion

    //region Тема t привязана к курсовой c?
    public boolean isAttachedThemeToCoursework(Theme t,CourseWork c){
        return c.getThemes().contains(t);
    }
    //endregion

    //region Получить лист информации о дисциплинах для текущего юзера
    public List<DisciplinesView.DisciplineInfo> getDisciplineInfoList(Long indexGroup) throws Exception {
        User currentUser = ctx.getBean("CurrentUser",User.class);
        Person person = currentUser.getPerson();

        List<Discipline> list = new ArrayList<>();
        List<Discipline> mainListDiscipline = getMainListEntityByClass(Discipline.class);
        list = mainListDiscipline;

        if((currentUser.getRole().isTeacher() || currentUser.getRole().isAdmin()) && indexGroup!= null) {
            list = getListDisciplineFromGroupAttachedToTeacher(getClassEntityByIndex(indexGroup,Group.class),currentUser.getPerson());
        }

        List<DisciplinesView.DisciplineInfo> disciplineInfos = new ArrayList<>();
        int index = 1;
        for (Discipline l : list) {
            DisciplinesView.DisciplineInfo disciplineInfo = new DisciplinesView.DisciplineInfo();
            disciplineInfo.setIndexOfMainList(mainListDiscipline.indexOf(l));
            disciplineInfo.setIndex(index++);
            disciplineInfo.setCourseworkCount(l.getCourseWork().size());
            disciplineInfo.setDisciplineName(l.getName());

            //Курсовые работы дисциплины
            List<CourseWork> courseWorks = l.getCourseWork();
            //Привязанные темы студента
            List<Theme> themeAffix = person.getAffixThemesStudent();

            Boolean isCheckedTmp = true;
            if(currentUser.getRole().isStudent()) {
                //Проверка во всех ли курсовых данного предмета есть привязанная тема данного студента
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
            }
            else if(currentUser.getRole().isTeacher()){
                //Проверка количество добавленных тем соответствует необходимому кол-ву
                List<CourseWork> cwList = l.getCourseWork();
                for(CourseWork cw:cwList){
                    List<TeacherInfo> ti =cw.getTeacherInfos();
                    ti.removeIf(teacherInfo -> !teacherInfo.getPerson().equals(currentUser.getPerson()));
                    if(ti.size() == 0) {
                        Notification.show("Отсутствует  информация о необходимом количестве тем в курсовой работе '"+cw.getName()+"'",8000, Notification.Position.BOTTOM_START);
                        isCheckedTmp = false;
                        break;
                    }
                    List<Theme> themes =  cw.getThemes();
                    int countAddTheme = 0;
                    for(Theme theme:themes)
                        if(theme.getAdd_person().equals(currentUser.getPerson()))
                            countAddTheme++;
                    if(ti.get(0).getCount_theme() > countAddTheme ){
                        isCheckedTmp = false;
                        break;
                    }
                }
            }
            disciplineInfo.setIsChecked(isCheckedTmp);
            disciplineInfos.add(disciplineInfo);
        }

        return disciplineInfos;
    }
    //endregion

    //region Привязать группу к дисциплине
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
    //endregion

    //region Привязать преподавателя к дисциплине
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
    //endregion

    //region Получить все привязанные дисциплины к текущему юзеру
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
    //endregion

    //region Получить лист курсовых привязанных к пользователю
    public List<CourseWork> getCourseWorkWithUser(User user){
        Group group = user.getPerson().getGroup();
        List<Discipline> userDisciplines = group.getDisciplines();
        List<CourseWork> res = new ArrayList<>();
        for(Discipline d:userDisciplines)
            res.addAll(d.getCourseWork());
        return res;
    }
    //endregion

    //region Получить все дисциплины
    public List<Discipline> getAll(){
        return discinplineRepository.findAll();
    }
    //endregion

    //region Добавление темы и привязка к курсовой
    public void addThemeAndAttachToCousework(String name,String description,Long indexCoursework) throws Exception {
        User currentUser = ctx.getBean("CurrentUser",User.class);
        if(!currentUser.getRole().isTeacher())
            throw new Exception("Темы могут добавлять только преподаватели!");
        CourseWork cw = getClassEntityByIndex(indexCoursework,CourseWork.class);

        if(!currentUser.getPerson().getDisciplinesTeacher().contains(cw.getDiscipline()))
            throw new Exception("Курсовая не привязана к текущему пользователю!");

        Theme theme = new Theme(description,name,currentUser.getPerson());
        theme.addCourseWork(cw);
        themeRepository.saveAndFlush(theme);
    }
    //endregion

    //region Установка или изменение настроек дисциплины
    public void setControlDiscipline(Discipline discipline,Date beginDate,Date endDate,boolean isAutoset,boolean isStudentChange,boolean isStudentOffer) throws Exception {

        User currentUser = ctx.getBean("CurrentUser",User.class);
        if(!currentUser.getRole().isTeacher() && !currentUser.getRole().isAdmin())
            throw new Exception("У вас недостаточно прав для изменения настроек дисциплины!");
        //Discipline discipline = getClassEntityByIndex(disciplineIndex,Discipline.class);
        if(discipline.getControl_discipline() == null){
            ControlDiscipline cd = new ControlDiscipline(beginDate,endDate,isAutoset,isStudentChange,isStudentOffer,discipline);
            controlDisciplineRepository.saveAndFlush(cd);
        }
        else{
            ControlDiscipline cd = discipline.getControl_discipline();
            cd.setDateBegin(beginDate);
            cd.setDateEnd(endDate);
            cd.setIs_autoset(isAutoset);
            cd.setIs_student_change(isStudentChange);
            cd.setIs_student_offer(isStudentOffer);
            controlDisciplineRepository.saveAndFlush(cd);
        }
    }
    //endregion

    //region Возвращает список всех  курсов
    public List<String> getAllCourses(){
        List<Group> groups = groupRepository.findAll();
        List<String> courses = new ArrayList<>();
        groups.forEach(group -> {
            if(!courses.contains(String.valueOf(group.getCourse())))
                courses.add(String.valueOf(group.getCourse()));
        });
        courses.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.valueOf(o1) > Integer.valueOf(o2)?1:Integer.valueOf(o1) < Integer.valueOf(o2)?-1:0;
            }
        });
        return courses;
    }
    //endregion

    //region Получить ВКР группы
    public Discipline getVKR(Group g){
        List<Discipline> list = g.getDisciplines();
        list.removeIf(discipline -> !discipline.isVKR());
        if(list.size() > 0)
            return list.get(0);
        return null;
    }
    //endregion

    //region Получить лист информации о группах для отчета
    public List<ReportView.ReportInfoGroups> getListReportInfoGroups() throws Exception {
        List<ReportView.ReportInfoGroups> list = new ArrayList<>();
        List<Group> groups = groupRepository.findAll();
        groups.removeIf(group -> getVKR(group) == null);

        int index = 1;
        List<Group> mainListGroup = getMainListEntityByClass(Group.class);
        for(Group g:groups){
            ReportView.ReportInfoGroups info = new ReportView.ReportInfoGroups();
            info.setGroup(g.getName());
            info.setIndex(String.valueOf(index++));
            info.setIndexOfMainList(mainListGroup.indexOf(g));
            List<Person> personList = new ArrayList<>(getVKR(g).getCourseWork().get(0).getThemesWithAffixedStudent().values());
            personList.removeIf(person -> !person.getGroup().equals(g));
            info.setChecked(personList.size() == g.getPersons().size());
            list.add(info);
        }
        return list;
    }
    //endregion

    //region Получить хеш мапу для WordGenerator для шаблона с одной группой
    public HashMap<String,Object> getSingleHashMapByGroupIndex(Long indexGroupOfMainList) throws Exception {
        HashMap<String,Object> map = new HashMap<String,Object>();
        Group group = getClassEntityByIndex(indexGroupOfMainList,Group.class);
        Discipline vkr = getVKR(group);
        if(vkr == null)
            throw new Exception("У этой группы нет ВКР!");

        map.put("code_group",group.getCipher());
        map.put("group_name",group.getName());

        Map<Theme, Person> affx =  vkr.getCourseWork().get(0).getThemesWithAffixedStudent();

        List<String> name = new ArrayList<String>();
        List<String> theme   = new ArrayList<String>();
        List<String> head_teacher_name = new ArrayList<String>();
        List<String> adviser_teacher_name = new ArrayList<String>();
        int index = 1;
        for(Person student:group.getPersons()){
            name.add((index++)+".\t"+student.getLastname()+" "+student.getFirstname());
            if(affx.values().contains(student)) {
                for(Theme t: affx.keySet())
                    if(affx.get(t).equals(student)) {
                        theme.add(t.getText());
                        head_teacher_name.add(t.getAdd_person().getLastname()+" "+t.getAdd_person().getFirstname());
                        List<TeacherInfo> infos = t.getAdd_person().getTeacherInfos();
                        boolean isHaveScienceAgreement = false;
                        for(TeacherInfo inf: infos)
                            if(inf.getCourseWork().equals(vkr.getCourseWork().get(0)) && inf.getSciece_agreement() != null){
                                adviser_teacher_name.add(inf.getSciece_agreement().getLastname() + " "+ inf.getSciece_agreement().getFirstname());
                                isHaveScienceAgreement  =true;
                            }
                        if(!isHaveScienceAgreement)
                            adviser_teacher_name.add("");
                        break;
                    }
            }
            else {
                theme.add("ТЕМА НЕ ВЫБРАНА");
                head_teacher_name.add("ТЕМА НЕ ВЫБРАНА");
                adviser_teacher_name.add("");
            }

        }
        map.put("student_name",name);
        map.put("student_theme",theme);
        map.put("head_teacher_name",head_teacher_name);
        map.put("adviser_teacher_name",adviser_teacher_name);

        return map;
    }
    //endregion

    // region Получить хеш мапу для WordGenerator для шаблона с несколькими группами
    public HashMap<String,Object> getMultiHashMapByGroupIndex(List<Long> indexesGroupOfMainList) throws Exception {
        HashMap<String,Object> map = new HashMap<String,Object>();
        List<Group> groups = new ArrayList<>();
        for(Long index:indexesGroupOfMainList)
            groups.add(getClassEntityByIndex(index,Group.class));
        for(Group g:groups) {
            Discipline vkr = getVKR(g);
            if(vkr == null)
                throw new Exception("У этой группы нет ВКР!");
        }
        List<String> groupname  = new ArrayList<>();
        List<String> groupcode  = new ArrayList<>();
        List<List<String>> students  = new ArrayList<>();
        List<List<String>> heads  = new ArrayList<>();
        List<List<String>> advs  = new ArrayList<>();
        List<List<String>> themes  = new ArrayList<>();

        for(Group group:groups){
            groupname.add(group.getName());
            groupcode.add(group.getCipher());
            List<String> g_students = new ArrayList<>();
            List<String> g_heads = new ArrayList<>();
            List<String> g_advs = new ArrayList<>();
            List<String> g_themes = new ArrayList<>();

            Map<Theme, Person> affx =  getVKR(group).getCourseWork().get(0).getThemesWithAffixedStudent();

            int index = 1;
            for(Person student:group.getPersons()){
                g_students.add((index++)+".\t"+student.getLastname()+" "+student.getFirstname());
                if(affx.values().contains(student)) {
                    for(Theme t: affx.keySet())
                        if(affx.get(t).equals(student)) {
                            g_themes.add(t.getText());
                            g_heads.add(t.getAdd_person().getLastname()+" "+t.getAdd_person().getFirstname());
                            List<TeacherInfo> infos = t.getAdd_person().getTeacherInfos();
                            boolean isHaveScienceAgreement = false;
                            for(TeacherInfo inf: infos)
                                if(inf.getCourseWork().equals(getVKR(group).getCourseWork().get(0)) && inf.getSciece_agreement() != null){
                                    g_advs.add(inf.getSciece_agreement().getLastname() + " "+ inf.getSciece_agreement().getFirstname());
                                    isHaveScienceAgreement  =true;
                                }
                            if(!isHaveScienceAgreement)
                                g_advs.add("");
                            break;
                        }
                }
                else {
                    g_themes.add("ТЕМА НЕ ВЫБРАНА");
                    g_heads.add("ТЕМА НЕ ВЫБРАНА");
                    g_advs.add("");
                }
            }

            students.add(g_students);
            heads.add(g_heads);
            advs.add(g_advs);
            themes.add(g_themes);
        }

        map.put("code_group",groupcode);
        map.put("group_name",groupname);
        map.put("student_name",students);
        map.put("student_theme",themes);
        map.put("head_teacher_name",heads);
        map.put("adviser_teacher_name",advs);
        return map;
    }
    //endregion
}
