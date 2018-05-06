package com.twobomb.Utils;

import java.util.Locale;

public class AppConst {
    public static final String VIEWPORT = "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes";
    public static final String PAGE_ROOT = "";
    public static final String PAGE_DISCIPLINES = "disciplines";
    public static final String PAGE_COURSEWORKS = "coursework";
    public static final String PAGE_GROUPS = "groups";
    public static final String PAGE_THEMES = "themes";
    public static final String PAGE_LOGOUT = "logout";
    public static final String PAGE_DEFAULT = PAGE_DISCIPLINES;


    public static final String ICON_DISCIPLINES = "vaadin:list";
    public static final String ICON_COURSEWORK = "vaadin:file-text-o";
    public static final String ICON_THEMES = "vaadin:form";
    public static final String ICON_GROUP = "vaadin:users";
    public static final String ICON_LOGOUT = "vaadin:sign-out";
    
    public static final String TITLE_ACCESS_DENIED = "Доступ запрещен";
    public static final String TITLE_NOT_FOUND = "Страница не найдена";
    public static final String TITLE_GROUPS = "Группы";
    public static final String TITLE_DISCIPLINES = "Дисциплины";
    public static final String TITLE_COURSEWORK = "Курсовые";
    public static final String TITLE_THEMES = "Темы";
    public static final String TITLE_LOGOUT = "Выход";



    //Дефолтные параметры дисцплин
    //Студент может предлагать тему
    public static final Boolean DEFAULT_IS_STUDENT_OFFER = false;
    //По завершении автораспределние тем
    public static final Boolean DEFAULT_IS_AUTOSET = false;
    //Студент может поменять тему после того как прикрепился
    public static final Boolean DEFAULT_IS_STUDENT_CHANGE = true;



}
