package com.twobomb.ui.pages;

import com.twobomb.Utils.AppConst;
import com.twobomb.entity.CourseWork;
import com.twobomb.entity.Discipline;
import com.twobomb.entity.User;
import com.twobomb.service.DisciplineService;
import com.twobomb.ui.MainView;
import com.twobomb.ui.components.BreadCrumbs;
import com.twobomb.ui.datacontainers.IndexData;
import com.twobomb.ui.datacontainers.UserData;
import com.twobomb.ui.models.MainModel;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.polymertemplate.EventHandler;
import com.vaadin.flow.component.polymertemplate.ModelItem;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.util.List;

@Tag("theme-view")
@HtmlImport("templates/ThemeView.html")
@Route(value = AppConst.PAGE_THEMES,layout = MainView.class)
//@RouteAlias(value = AppConst.PAGE_DISCIPLINES, layout = MainView.class)
@PageTitle(AppConst.TITLE_THEMES)
public class ThemeView extends PolymerTemplate<ThemeView.Model> implements HasUrlParameter<String> {


    DisciplineService disciplineService;
    User currentUser;
    Long indexCoursework;
    String urlParametr = "";
    @Autowired
    public ThemeView(DisciplineService disciplineService,User currentUser) {
        this.disciplineService = disciplineService;
        this.currentUser = currentUser;
    }

    public interface Model extends MainModel {
        void setThemeItemInfo(List<ThemeItemInfo> d);
        void setAdditionalCourseWorkInfo(AdditionalCourseWorkInfo d);
        void setError(String s);

    }

    @EventHandler
    public void saveSettings(@EventData("event.changeSettingsData.beginDate") String beginDate,
                             @EventData("event.changeSettingsData.endDate") String endDate,
                             @EventData("event.changeSettingsData.isAutoset") Boolean isAutoset,
                             @EventData("event.changeSettingsData.isStudentChange") Boolean isStudentChange,
                             @EventData("event.changeSettingsData.isStudentOffer") Boolean isStudentOffer){

        try {
            Discipline discipline = disciplineService.getClassEntityByIndex(BreadCrumbs.parseIndex(urlParametr, BreadCrumbs.ParseIndexes.COURSEWORK), CourseWork.class).getDiscipline();
            disciplineService.setControlDiscipline(
                    discipline,
                    Date.valueOf(beginDate),
                    Date.valueOf(endDate),
                    isAutoset,
                    isStudentChange,
                    isStudentOffer
                    );
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show(e.getMessage(), 5000, Notification.Position.BOTTOM_START);
        }

        setParameter(null,urlParametr);
    }
    @EventHandler
    public void addTheme(@EventData("event.addThemeData.name") String name,@EventData("event.addThemeData.description") String description){
        if(name != null){
            if(!name.trim().equals("")) {
                Long indexCoursework = BreadCrumbs.parseIndex(urlParametr, BreadCrumbs.ParseIndexes.COURSEWORK);
                try {
                    disciplineService.addThemeAndAttachToCousework(name.trim(), description.trim(), indexCoursework);
                } catch (Exception e) {
                    e.printStackTrace();
                    Notification.show(e.getMessage(), 5000, Notification.Position.BOTTOM_START);
                }
            }
        }
//        UI.getCurrent().getPage().getHistory().go(0);
        setParameter(null,urlParametr);
    }

    @EventHandler
    public void attachMe( @EventData("event.indexAttachTheme") String index){
        try {
            disciplineService.addAttachThemeToPersonInCoursework(indexCoursework.intValue(),Integer.valueOf(index));
        } catch (Exception e) {
            getModel().setError(e.getMessage());
            Notification.show(e.getMessage(), 5000, Notification.Position.BOTTOM_START);
        }
        setParameter(null,urlParametr);

    }
    @EventHandler
    public void detachMe( @EventData("event.indexDetachTheme") String index){
        try {
            disciplineService.detachThemeToPersionInCoursework(indexCoursework.intValue(),Integer.valueOf(index));
        } catch (Exception e) {
            getModel().setError(e.getMessage());
            Notification.show(e.getMessage(), 5000, Notification.Position.BOTTOM_START);
        }
        setParameter(null,urlParametr);

    }


    public static class AdditionalCourseWorkInfo{
        String courseworkName;
        String disciplineName;
        String teachers;
        String groupName;
        String beginDate;
        String beginDateForDatePicker;
        String endDate;
        String endDateForDatePicker;
        Integer teachersMustAddCount;
        Boolean isAutoset;
        Boolean isStudentOffer;
        Boolean isStudentChange;

        public AdditionalCourseWorkInfo() {
        }

        public String getBeginDateForDatePicker() {
            return beginDateForDatePicker;
        }

        public void setBeginDateForDatePicker(String beginDateForDatePicker) {
            this.beginDateForDatePicker = beginDateForDatePicker;
        }

        public String getEndDateForDatePicker() {
            return endDateForDatePicker;
        }

        public void setEndDateForDatePicker(String endDateForDatePicker) {
            this.endDateForDatePicker = endDateForDatePicker;
        }

        public String getCourseworkName() {
            return courseworkName;
        }

        public void setCourseworkName(String courseworkName) {
            this.courseworkName = courseworkName;
        }
        public Boolean getIsAutoset() {
            return isAutoset;
        }

        public String getDisciplineName() {
            return disciplineName;
        }

        public void setDisciplineName(String disciplineName) {
            this.disciplineName = disciplineName;
        }

        public String getTeachers() {
            return teachers;
        }

        public void setTeachers(String teachers) {
            this.teachers = teachers;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getBeginDate() {
            return beginDate;
        }

        public void setBeginDate(String beginDate) {
            this.beginDate = beginDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public Integer getTeachersMustAddCount() {
            return teachersMustAddCount;
        }

        public void setTeachersMustAddCount(Integer teachersMustAddCount) {
            this.teachersMustAddCount = teachersMustAddCount;
        }

        public void setIsAutoset(Boolean autoset) {
            isAutoset = autoset;
        }

        public Boolean getIsStudentOffer() {
            return isStudentOffer;
        }

        public void setIsStudentOffer(Boolean studentOffer) {
            isStudentOffer = studentOffer;
        }

        public Boolean getIsStudentChange() {
            return isStudentChange;
        }

        public void setIsStudentChange(Boolean studentChange) {
            isStudentChange = studentChange;
        }
    }
    public static class ThemeItemInfo{
        Integer index;
        String name;
        String description;
        String status;
        String edited;
        Boolean isDisabledAttachBtn;
        Boolean isDisabledDetachBtn;
        Boolean isCanAttach;//Можно прикрепиться
        Boolean isCurrentUserAttach;//Прикреплен текущий юзер
        String teacherAdd;

        public String getTeacherAdd() {
            return teacherAdd;
        }

        public void setTeacherAdd(String teacherAdd) {
            this.teacherAdd = teacherAdd;
        }


        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getEdited() {
            return edited;
        }

        public void setEdited(String edited) {
            this.edited = edited;
        }

        public Boolean getIsDisabledAttachBtn() {
            return isDisabledAttachBtn;
        }

        public void setIsDisabledAttachBtn(Boolean disabledAttachBtn) {
            isDisabledAttachBtn = disabledAttachBtn;
        }

        public Boolean getIsDisabledDetachBtn() {
            return isDisabledDetachBtn;
        }

        public void setIsDisabledDetachBtn(Boolean disabledDetachBtn) {
            isDisabledDetachBtn = disabledDetachBtn;
        }

        public Boolean getIsCanAttach() {
            return isCanAttach;
        }

        public void setIsCanAttach(Boolean canAttach) {
            isCanAttach = canAttach;
        }

        public Boolean getIsCurrentUserAttach() {
            return isCurrentUserAttach;
        }

        public void setIsCurrentUserAttach(Boolean currentUserAttach) {
            isCurrentUserAttach = currentUserAttach;
        }

    }
    @Override
    public void setParameter(BeforeEvent beforeEvent,@OptionalParameter String str) {
        Long indexCoursework = BreadCrumbs.parseIndex(str, BreadCrumbs.ParseIndexes.COURSEWORK);

        Long indexGroup= BreadCrumbs.parseIndex(str, BreadCrumbs.ParseIndexes.GROUP);
        Long indexDiscipline = BreadCrumbs.parseIndex(str, BreadCrumbs.ParseIndexes.DISCIPLINE);

        if(str != null)
            urlParametr = str;

        getModel().setUserData(UserData.getInstance(currentUser));
        getModel().setIndexData(IndexData.Instance(str));

        if(indexCoursework == null) {
            Notification.show("Выберите курсовую чтобы просмотреть темы",4000, Notification.Position.BOTTOM_START);
            com.vaadin.flow.component.UI.getCurrent().getPage().executeJavaScript("location.assign('"+AppConst.PAGE_COURSEWORKS+"')");
        }
        else if((currentUser.getRole().isTeacher() || currentUser.getRole().isAdmin()) && indexGroup == null) {
            Notification.show("Выберите группу чтобы просмотреть темы",4000, Notification.Position.BOTTOM_START);

        }else {
            this.indexCoursework = indexCoursework;

            try {
                getModel().setAdditionalCourseWorkInfo(disciplineService.getAdditionalCourseWorkInfo(indexCoursework,indexGroup));
                getModel().setThemeItemInfo(disciplineService.getThemeItemInfoList(indexCoursework,indexGroup));
            } catch (Exception e) {
                e.printStackTrace();
                Notification.show(e.getMessage(),5000, Notification.Position.BOTTOM_START);
                com.vaadin.flow.component.UI.getCurrent().getPage().executeJavaScript("location.assign('"+AppConst.PAGE_COURSEWORKS+"')");
            }
        }
    }
}
