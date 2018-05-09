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
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Tag("disciplines-view")
@HtmlImport("templates/DisciplinesView.html")
@Route(value = AppConst.PAGE_DISCIPLINES,layout = MainView.class)
@RouteAlias(value = AppConst.PAGE_ROOT, layout = MainView.class)
@PageTitle(AppConst.TITLE_DISCIPLINES)

public class DisciplinesView extends PolymerTemplate<DisciplinesView.Model> implements HasUrlParameter<String>{


    DisciplineService disciplineService;
    User currentUser;

    @Autowired
    public DisciplinesView(DisciplineService disciplineService,User currentUser) {
        this.disciplineService = disciplineService;
        this.currentUser = currentUser;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String str) {

        Long indexGroup = BreadCrumbs.parseIndex(str, BreadCrumbs.ParseIndexes.GROUP);

        List<DisciplineInfo> data = null;
        try {
            data = disciplineService.getDisciplineInfoList(indexGroup);
        } catch (Exception e) {
            Notification.show(e.getMessage(),5000, Notification.Position.BOTTOM_START);
            e.printStackTrace();
        }

        Model m = getModel();
        m.setIndexData(IndexData.Instance(str));
        m.setDisciplineInfo(data);
        m.setUserData(UserData.getInstance(currentUser));
    }


    public static class DisciplineInfo{

        private Integer index;
        private Integer courseworkCount;
        private String disciplineName;
        private Integer indexOfMainList;

        //Для стуеднта выбраны ли темы, для учителя добавлены ли все темы
        private Boolean isChecked;

        public Integer getIndexOfMainList() {
            return indexOfMainList;
        }

        public void setIndexOfMainList(Integer indexOfMainList) {
            this.indexOfMainList = indexOfMainList;
        }

        public DisciplineInfo() {
        }

        public Boolean getIsChecked() {
            return isChecked;
        }

        public void setIsChecked(Boolean checked) {
            isChecked = checked;
        }



        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }
        public Integer getCourseworkCount() {
            return courseworkCount;
        }

        public String getDisciplineName() {
            return disciplineName;
        }


        public void setCourseworkCount(Integer courseworkCount) {
            this.courseworkCount = courseworkCount;
        }

        public void setDisciplineName(String disciplineName) {
            this.disciplineName = disciplineName;
        }

    }
    public interface Model extends MainModel {
            void setDisciplineInfo(List<DisciplineInfo> d);

    }
}
