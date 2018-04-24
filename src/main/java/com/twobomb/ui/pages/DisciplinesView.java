package com.twobomb.ui.pages;

import com.twobomb.Utils.AppConst;
import com.twobomb.entity.*;
import com.twobomb.service.DisciplineService;
import com.twobomb.ui.MainView;
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

import java.util.ArrayList;
import java.util.List;

@Tag("disciplines-view")
@HtmlImport("templates/DisciplinesView.html")
@Route(value = AppConst.PAGE_DISCIPLINES,layout = MainView.class)
@PageTitle(AppConst.TITLE_DISCIPLINES)

public class DisciplinesView extends PolymerTemplate<DisciplinesView.Model> implements HasUrlParameter<Long> {

    @Id("listDisciplines")
    ListBox<String> listBox;



    @Autowired
    public DisciplinesView(DisciplineService disciplineService, User currentUser/*,SessionFactory sessionFactory*/) {
//        Session session = sessionFactory.openSession();
        try {
            Person person = currentUser.getPerson();
//            person = session.get(person.getClass(), person.getId());

            List<String> disciplineName = new ArrayList<>();
            List<Integer> courseworkCount = new ArrayList<>();
            List<Boolean> isChecked = new ArrayList<>();
            List<Boolean> isNotChecked = new ArrayList<>();

            List<Discipline> list = disciplineService.getBindDisciplineWidthUser(currentUser);

//        courseworkCount = disciplineService.getCourseWorkWithUser(currentUser).size();
//        isChecked = person.getAffixThemesStudent().size() == courseworkCount;
            for (Discipline l : list) {
//                l = session.get(Discipline.class, l.getId());
                courseworkCount.add(l.getCourseWork().size());
                disciplineName.add(l.getName());

                //Курсовые работы дисциплины
                List<CourseWork> courseWorks = l.getCourseWork();
                //Привязанные темы студента
                List<Theme> themeAffix = person.getAffixThemesStudent();

                //Проверка во всех ли курсовых данного предмета есть привязанная тема данного студента
                Boolean isCheckedTmp = true;
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
                isChecked.add(isCheckedTmp);
                isNotChecked.add(!isCheckedTmp);
            }
            Model m = getModel();
            m.setCourseworkCount(courseworkCount);
            m.setDisciplineNames(disciplineName);
            m.setThemeChecked(isChecked);
            m.setThemeNotChecked(isNotChecked);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
//            session.close();
        }
    }

    @EventHandler
    private void handleClick() {

    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Long aLong) {
    }


    public interface Model extends TemplateModel {
            void setDisciplineNames(List<String> d);
            void setThemeChecked(List<Boolean> d);
            void setThemeNotChecked(List<Boolean> d);
            void setCourseworkCount(List<Integer> d);
    }
}
