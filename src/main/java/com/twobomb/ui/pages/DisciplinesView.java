package com.twobomb.ui.pages;

import com.twobomb.Utils.AppConst;
import com.twobomb.entity.*;
import com.twobomb.service.DisciplineService;
import com.twobomb.ui.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.polymertemplate.EventHandler;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.html.HTMLElement;

import java.util.ArrayList;
import java.util.HashMap;
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
            HashMap<String,Object> data = disciplineService.getData_DV();
            Model m = getModel();
            m.setCourseworkCount((List<Integer>) data.get("courseworkCount"));
            m.setDisciplineNames((List<String>) data.get("disciplineName"));
            m.setThemeChecked((List<Boolean>) data.get("isChecked"));
            m.setThemeNotChecked((List<Boolean>) data.get("isNotChecked"));
            m.setCourseworkPage(AppConst.PAGE_COURSEWORKS);
    }

    public interface Model extends TemplateModel {
            void setDisciplineNames(List<String> d);
            void setThemeChecked(List<Boolean> d);
            void setThemeNotChecked(List<Boolean> d);
            void setCourseworkCount(List<Integer> d);
            void setCourseworkPage(String d);
    }
}
