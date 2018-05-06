package com.twobomb.ui.pages;

import com.twobomb.Utils.AppConst;
import com.twobomb.entity.Role;
import com.twobomb.entity.User;
import com.twobomb.service.DisciplineService;
import com.twobomb.ui.MainView;
import com.twobomb.ui.datacontainers.UserData;
import com.twobomb.ui.models.MainModel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.util.List;

@Tag("group-view")
@HtmlImport("templates/GroupView.html")
@Route(value = AppConst.PAGE_GROUPS,layout = MainView.class)
@Secured({Role.TEACHER, Role.ADMIN})
//@RouteAlias(value = AppConst.PAGE_DISCIPLINES, layout = MainView.class)
@PageTitle(AppConst.TITLE_GROUPS)
public class GroupView extends PolymerTemplate<GroupView.Model> implements HasUrlParameter<Long> {

    private User currentUser;
    private DisciplineService disciplineService;

    @Autowired
    public GroupView(User cur,DisciplineService ser) {
        currentUser = cur;
        disciplineService = ser;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Long aLong) {
        getModel().setGroupInfo(disciplineService.getListGroupInfo());
        getModel().setUserData(UserData.getInstance(currentUser));

    }

    public interface Model extends MainModel {
        void setGroupInfo(List<GroupInfo> data);
        void setError(String s);

    }


    public static class GroupInfo {
        private Integer index;
        private String groupName;
        private String studentCount;
        private String countDiscipline;
        private Integer countCoursework;
        private Boolean isCheck;
        private Integer course;


        private String linkShowDisciplines;
        private String linkShowCourseworks;

        public GroupInfo() {

        }

        public Integer getCourse() {
            return course;
        }

        public void setCourse(Integer course) {
            this.course = course;
        }
        public Boolean getIsCheck() {
            return isCheck;
        }

        public void setIsCheck(Boolean check) {
            isCheck = check;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getStudentCount() {
            return studentCount;
        }

        public void setStudentCount(String studentCount) {
            this.studentCount = studentCount;
        }

        public String getCountDiscipline() {
            return countDiscipline;
        }

        public void setCountDiscipline(String countDiscipline) {
            this.countDiscipline = countDiscipline;
        }

        public Integer getCountCoursework() {
            return countCoursework;
        }

        public void setCountCoursework(Integer countCoursework) {
            this.countCoursework = countCoursework;
        }

        public String getLinkShowDisciplines() {
            return linkShowDisciplines;
        }

        public void setLinkShowDisciplines(String linkShowDisciplines) {
            this.linkShowDisciplines = linkShowDisciplines;
        }

        public String getLinkShowCourseworks() {
            return linkShowCourseworks;
        }

        public void setLinkShowCourseworks(String linkShowCourseworks) {
            this.linkShowCourseworks = linkShowCourseworks;
        }
    }
}


