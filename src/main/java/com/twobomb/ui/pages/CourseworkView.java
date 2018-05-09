package com.twobomb.ui.pages;

import com.twobomb.Utils.AppConst;
import com.twobomb.entity.User;
import com.twobomb.service.DisciplineService;
import com.twobomb.ui.MainView;
import com.twobomb.ui.components.BreadCrumbs;
import com.twobomb.ui.datacontainers.IndexData;
import com.twobomb.ui.datacontainers.UserData;
import com.twobomb.ui.models.MainModel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Tag("coursework-view")
@HtmlImport("templates/CourseworkView.html")
@Route(value = AppConst.PAGE_COURSEWORKS,layout = MainView.class)
//@RouteAlias(value = AppConst.PAGE_DISCIPLINES, layout = MainView.class)
@PageTitle(AppConst.TITLE_COURSEWORK)
public class CourseworkView extends PolymerTemplate<CourseworkView.Model> implements HasUrlParameter<String> {


    DisciplineService disciplineService;
    User currentUser;

    @Autowired
    public CourseworkView(DisciplineService disciplineService,User currentUser) {
        this.disciplineService = disciplineService;
        getModel().setUserData(UserData.getInstance(currentUser));
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String str) {
        Long indexDiscipline = BreadCrumbs.parseIndex(str, BreadCrumbs.ParseIndexes.DISCIPLINE);
        Long indexGroup = BreadCrumbs.parseIndex(str, BreadCrumbs.ParseIndexes.GROUP);

        List<CourseWorkInfo> res = null;
        try {
            res = disciplineService.getCourseWorkInfoList(indexDiscipline,indexGroup);
        } catch (Exception e) {
            Notification.show(e.getMessage(),5000, Notification.Position.BOTTOM_START);
            e.printStackTrace();
        }
        Model model = getModel();
        getModel().setIndexData(IndexData.Instance(str));
        model.setCourseworkInfo(res);
    }

    public interface Model extends MainModel {
         void setCourseworkInfo(List<CourseWorkInfo> d);
    }

    public static class CourseWorkInfo {
        private Integer index;
        private String courseworkName;
        private String disciplineName;
        private String prepodList;
        private String dateBegin;
        private String dateEnd;

        //Для студента выбрана ли тема, для препода все ли темы добавлен
        private Boolean isThemeChecked;
        private Integer indexOfMainList;
        public CourseWorkInfo() {
        }

        public Integer getIndexOfMainList() {
            return indexOfMainList;
        }

        public void setIndexOfMainList(Integer indexOfMainList) {
            this.indexOfMainList = indexOfMainList;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public void setCourseworkName(String courseworkName) {
            this.courseworkName = courseworkName;
        }

        public void setDisciplineName(String disciplineName) {
            this.disciplineName = disciplineName;
        }

        public void setPrepodList(String prepodList) {
            this.prepodList = prepodList;
        }

        public void setDateBegin(String dateBegin) {
            this.dateBegin = dateBegin;
        }

        public void setDateEnd(String dateEnd) {
            this.dateEnd = dateEnd;
        }

        public void setIsThemeChecked(Boolean themeChecked) {
            isThemeChecked = themeChecked;
        }

        public String getCourseworkName() {
            return courseworkName;
        }

        public String getDisciplineName() {
            return disciplineName;
        }

        public String getPrepodList() {
            return prepodList;
        }

        public String getDateBegin() {
            return dateBegin;
        }

        public String getDateEnd() {
            return dateEnd;
        }

        public Boolean getIsThemeChecked() {
            return isThemeChecked;
        }
    }
}
