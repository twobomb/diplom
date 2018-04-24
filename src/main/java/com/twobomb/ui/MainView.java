package com.twobomb.ui;

import java.util.ArrayList;
import java.util.List;

import com.twobomb.app.security.SecurityUtils;
import com.twobomb.ui.components.AppNavigation;
import com.twobomb.ui.components.PageInfo;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import static com.twobomb.Utils.AppConst.*;

@Tag("main-view")
@HtmlImport("templates/MainView.html")
@PageTitle("Главная")
@BodySize(height = "100vh", width = "100vw")
@Viewport(VIEWPORT)
@Theme(Lumo.class)
public class MainView extends PolymerTemplate<TemplateModel>
        implements RouterLayout, BeforeEnterObserver, AfterNavigationObserver {

    @Id("appNavigation")
    private AppNavigation appNavigation;

    public MainView() {

        List<PageInfo> pages = new ArrayList<>();

        pages.add(new PageInfo(PAGE_DISCIPLINES, ICON_DISCIPLINES, TITLE_DISCIPLINES));
        pages.add(new PageInfo(PAGE_COURSEWORKS, ICON_COURSEWORK, TITLE_COURSEWORK));
        pages.add(new PageInfo(PAGE_THEMES, ICON_THEMES, TITLE_THEMES));
//        if (SecurityUtils.isAccessGranted(UsersView.class)) {
//            pages.add(new PageInfo(PAGE_USERS, ICON_USERS, TITLE_USERS));
//        }
        pages.add(new PageInfo(PAGE_LOGOUT, ICON_LOGOUT, TITLE_LOGOUT));

        appNavigation.init(pages, PAGE_DEFAULT, PAGE_LOGOUT);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        appNavigation.afterNavigation(afterNavigationEvent);

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }
}
