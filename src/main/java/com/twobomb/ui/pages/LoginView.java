package com.twobomb.ui.pages;

import com.twobomb.Utils.AppConst;
import com.twobomb.app.security.SecurityConfiguration;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import static com.twobomb.Utils.AppConst.VIEWPORT;

@Tag("login-view")
@HtmlImport("templates/LoginView.html")
@Route(value = "login")
@PageTitle(AppConst.TITLE_LOGIN)

@BodySize(height = "100vh", width = "100vw")
@Viewport(VIEWPORT)
@Theme(Lumo.class)
public class LoginView extends PolymerTemplate<LoginView.Model>
        implements RouterLayout, BeforeEnterObserver, AfterNavigationObserver {

    public LoginView() {

    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        boolean error = afterNavigationEvent.getLocation().getQueryParameters().getParameters().containsKey("error");
        getModel().setError(error);

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    }

    public interface Model extends TemplateModel {
        void setError(boolean error);
    }
}

