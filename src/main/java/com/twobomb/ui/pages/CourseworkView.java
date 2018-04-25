package com.twobomb.ui.pages;

import com.twobomb.Utils.AppConst;
import com.twobomb.entity.Discipline;
import com.twobomb.entity.User;
import com.twobomb.service.DisciplineService;
import com.twobomb.ui.MainView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

@Tag("coursework-view")
@HtmlImport("templates/CourseworkView.html")
@Route(value = AppConst.PAGE_COURSEWORKS,layout = MainView.class)
//@RouteAlias(value = AppConst.PAGE_DISCIPLINES, layout = MainView.class)
@PageTitle(AppConst.TITLE_COURSEWORK)
public class CourseworkView extends PolymerTemplate<CourseworkView.Model> implements HasUrlParameter<Long> {


    DisciplineService disciplineService;
    User currentUser;

    @Autowired
    public CourseworkView(DisciplineService disciplineService,User currentUser) {
        this.disciplineService = disciplineService;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Long aLong) {
        HashMap<String,Object> res = disciplineService.getData_CV(aLong == null?-1:  (aLong-1));
        Model model = getModel();
        model.setCourseworkName((List<String>) res.get("courseworkName"));
        model.setDisciplineName((List<String>) res.get("disciplineName"));
        model.setPrepodList((List<String>) res.get("prepodList"));
        model.setDateBegin((List<String>) res.get("dateBegin"));
        model.setDateEnd((List<String>) res.get("dateEnd"));
        model.setIsThemeChecked((List<Boolean>) res.get("isThemeChecked"));
        model.setIsLoaded(true);
    }

    public interface Model extends TemplateModel {
        public void setCourseworkName(List<String> d);
        public void setDisciplineName(List<String> d);
        public void setPrepodList(List<String> d);
        public void setDateBegin(List<String> d);
        public void setDateEnd(List<String> d);
        public void setIsThemeChecked(List<Boolean> d);
        public void setIsLoaded(boolean d);
    }

}
