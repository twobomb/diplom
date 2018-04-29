package com.twobomb.ui.pages;

import com.twobomb.Utils.AppConst;
import com.twobomb.service.DisciplineService;
import com.twobomb.ui.MainView;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.EventHandler;
import com.vaadin.flow.component.polymertemplate.ModelItem;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Tag("theme-view")
@HtmlImport("templates/ThemeView.html")
@Route(value = AppConst.PAGE_THEMES,layout = MainView.class)
//@RouteAlias(value = AppConst.PAGE_DISCIPLINES, layout = MainView.class)
@PageTitle(AppConst.TITLE_THEMES)
public class ThemeView extends PolymerTemplate<ThemeView.Model> implements HasUrlParameter<Long> {


    DisciplineService disciplineService;

    Long indexCoursework;
    @Autowired
    public ThemeView(DisciplineService disciplineService) {
        this.disciplineService = disciplineService;
    }
    public interface Model extends TemplateModel {
        void setThemeItemInfo(List<ThemeItemInfo> d);
        void setAdditionalCourseWorkInfo(AdditionalCourseWorkInfo d);
        void setError(String s);

    }

    @EventHandler
    public void attachMe( @EventData("event.indexAttachTheme") String index){
        try {
            disciplineService.addAttachThemeToPersonInCoursework(indexCoursework.intValue(),Integer.valueOf(index));
        } catch (Exception e) {
            getModel().setError(e.getMessage());
        }
        setParameter(null,indexCoursework+1);

    }


    public static class AdditionalCourseWorkInfo{
        String courseworkName;
        String disciplineName;
        String teachers;
        String groupName;
        String beginDate;
        String endDate;
        Integer teachersMustAddCount;
        Boolean isAutoset;
        Boolean isStudentOffer;
        Boolean isStudentChange;

        public AdditionalCourseWorkInfo() {
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
    public void setParameter(BeforeEvent beforeEvent,@OptionalParameter Long aLong) {
        aLong = aLong == null?-1:(aLong-1);
        indexCoursework = aLong;
        List<ThemeItemInfo> themeItemInfos = disciplineService.getThemeItemInfoList(aLong);

        getModel().setAdditionalCourseWorkInfo(disciplineService.getAdditionalCourseWorkInfo(aLong));
        getModel().setThemeItemInfo(themeItemInfos);
    }
}
