package com.twobomb.ui;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.twobomb.Utils.AppConst;
import com.twobomb.app.security.SecurityUtils;
import com.twobomb.ui.components.AppNavigation;
import com.twobomb.ui.components.BreadCrumbs;
import com.twobomb.ui.components.PageInfo;
import com.twobomb.ui.pages.error.AccessDeniedException;
import com.twobomb.ui.pages.GroupView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.ui.UI;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.commons.io.IOUtils;

import javax.servlet.annotation.WebServlet;

import static com.twobomb.Utils.AppConst.*;

@Tag("main-view")
@HtmlImport("templates/MainView.html")
@PageTitle("Главная")
@BodySize(height = "100vh", width = "100vw")
@Viewport(VIEWPORT)
@Theme(Lumo.class)
public class MainView extends PolymerTemplate<TemplateModel>
        implements RouterLayout, BeforeEnterObserver, AfterNavigationObserver{

    @Id("appNavigation")
    private AppNavigation appNavigation;

    @Id("breadCrumbs")
    private BreadCrumbs breadCrumbs;

    public MainView() {

        List<PageInfo> pages = new ArrayList<>();

        if (SecurityUtils.isAccessGranted(GroupView.class))
            pages.add(new PageInfo(PAGE_GROUPS, ICON_GROUP, TITLE_GROUPS));
        pages.add(new PageInfo(PAGE_DISCIPLINES, ICON_DISCIPLINES, TITLE_DISCIPLINES));
        pages.add(new PageInfo(PAGE_COURSEWORKS, ICON_COURSEWORK, TITLE_COURSEWORK));
        pages.add(new PageInfo(PAGE_THEMES, ICON_THEMES, TITLE_THEMES));

        pages.add(new PageInfo(PAGE_LOGOUT, ICON_LOGOUT, TITLE_LOGOUT));

        appNavigation.init(pages, PAGE_DEFAULT, PAGE_LOGOUT);

    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        appNavigation.afterNavigation(event);
        breadCrumbs.init(event.getLocation());

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SecurityUtils.isAccessGranted(event.getNavigationTarget()))
            event.rerouteToError(AccessDeniedException.class,"Доступ запрещен. У вас недостаточно прав!");
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
