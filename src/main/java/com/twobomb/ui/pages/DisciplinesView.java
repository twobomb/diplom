package com.twobomb.ui.pages;

import com.twobomb.Utils.AppConst;
import com.twobomb.service.DisciplineService;
import com.twobomb.ui.MainView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Tag("disciplines-view")
@HtmlImport("templates/DisciplinesView.html")
@Route(value = AppConst.PAGE_DISCIPLINES,layout = MainView.class)
@RouteAlias(value = AppConst.PAGE_ROOT, layout = MainView.class)
@PageTitle(AppConst.TITLE_DISCIPLINES)

public class DisciplinesView extends PolymerTemplate<DisciplinesView.Model>{

    @Id("listDisciplines")
    ListBox<String> listBox;



    @Autowired
    public DisciplinesView(DisciplineService disciplineService) {
            List<DisciplineInfo> data = disciplineService.getDisciplineInfoList();
            Model m = getModel();
            m.setDisciplineInfo(data);
            m.setCourseworkPage(AppConst.PAGE_COURSEWORKS);
    }

    public static class DisciplineInfo{

        private Integer index;
        private Integer courseworkCount;
        private String disciplineName;
        private Boolean isChecked;

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
    public interface Model extends TemplateModel {
            void setDisciplineInfo(List<DisciplineInfo> d);
            void setCourseworkPage(String d);
    }
}
